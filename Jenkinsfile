#!groovy
@Library('iot-invent-shared') _

pipeline {

    agent any
    // Prepare required tools
 	tools {
  		maven 'M3'
 	}
    options {
        timestamps()
        disableConcurrentBuilds()
		// Keep only the last 7 builds
    	buildDiscarder(logRotator(numToKeepStr: '3', artifactNumToKeepStr: '3'))
        skipStagesAfterUnstable()
	}
	parameters {
        booleanParam(name: "CLEANUP",
                description: "Cleanup Current Workspace.",
                defaultValue: false)
//        booleanParam(name: "RELEASE",
//                description: "Build a release from current commit.",
//                defaultValue: false)
        string(name: "MVN_PARAMS", defaultValue: "", description: "Aditional Maven Parameters for: mvn clean install deploy")
    }
    stages {
 		stage("Cleanup") {
            when {
                expression { params.CLEANUP }
            }
            steps {
				cleanWs()
            	checkout scm
            }
        }
        stage('Build & Deploy') {
            when {
       			not { branch 'master' }
      		}
            steps {
				nodejs(nodeJSInstallationName: 'nodeJs') {
					withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
						sh "mvn ${params.MVN_PARAMS} -e -B clean deploy -Pproduction"
    				}
				}
  			}
        }
 		stage("Release") {
            when { branch 'master' }
            environment {
        		RELEASE_VERSION = getNextRelease()
     		}
            steps {
				echo "Releasing ${RELEASE_VERSION}"
				nodejs(nodeJSInstallationName: 'nodeJs') {
					withMaven(maven: 'M3', mavenSettingsConfig: 'iot_maven') {
					
						sh "mvn -B clean -Pproduction -Drevision=${RELEASE_VERSION} -Dchangelist="
						sh "mvn -B enforcer:enforce -Pproduction -Drevision=${RELEASE_VERSION} -Dchangelist="
						sh "mvn -B deploy -Pproduction -Drevision=${RELEASE_VERSION} -Dchangelist="
    				}
    			}
    			withCredentials([gitUsernamePassword(credentialsId: 'iot-invent-bot',
                 	gitToolName: 'Default')]) {
                	sh """
       				git config --global user.email support@iot-invent.com
       				git config --global user.name iot-invent-bot
       				"""
       				sh "git tag v${RELEASE_VERSION}"
					sh "git push origin v${RELEASE_VERSION}"
                } 
            }
        }
    }
 	
 	post {
		always {
      		sendNotifications currentBuild.result
    	}
    }
}
