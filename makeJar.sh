cd
cd ~/IdeaProjects/SOCharts
cd src/main
rm -fr resources
mkdir -p resources/META-INF/resources/frontend
cp frontend/* resources/META-INF/resources/frontend/ 2>/dev/null
rm -f resources/META-INF/resources/frontend/index.html
cd ~/IdeaProjects/SOCharts
mvn clean install -Pdirectory
mkdir -p zipTarget
rm -f zipTarget/*.zip
cp target/*.zip zipTarget
cd
