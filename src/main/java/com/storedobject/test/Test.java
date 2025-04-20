package com.storedobject.test;

import com.storedobject.chart.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.Random;

@Route("")
public class Test extends VerticalLayout {

    private final SOChart soChart = new SOChart();

    public Test() {
        setSizeFull();
        soChart.setSizeFull();
        drawMenu();
    }

    private void drawMenu() {
        removeAll();
        add(new Button("Gantt Chart", e -> build(() -> ganttChart(soChart))));
        add(new Button("Line Chart", e -> build(() -> lineChart(soChart))));
    }

    private void build(Runnable builder) {
        removeAll();
        soChart.removeAll();
        add(new Button("Back to Menu", e -> drawMenu()));
        add(soChart);
        builder.run();
        try {
            soChart.update();
        } catch (Exception ignored) {
        }
    }

    private static void ganttChart(SOChart soChart) {
        // Sample project with few entries
        Project project = new Project();
        project.setStart(LocalDateTime.now().minusDays(10));
        Project.TaskGroup tg1 = project.createTaskGroup("Group 1");
        Project.Task tg1T1 = tg1.createTask("Task 1/1", 6);
        Project.Task tg1T2 = tg1.createTask("Task 1/2", 5);
        tg1T2.setCompleted(100); // This task is 100% complete
        project.dependsOn(tg1T2, tg1T1);
        Project.Task tg1T3 = tg1.createTask("Task 1/3", 11);
        Project.Task milestone = tg1.createTask("Example Milestone", 0); // Milestone
        project.dependsOn(milestone, tg1T3);
        project.dependsOn(tg1T2, milestone);
        Project.TaskGroup tg2 = project.createTaskGroup("Group 2");
        Project.Task tg2T1 = tg2.createTask("Task 2/1", 3);
        Project.Task tg2T2 = tg2.createTask("Task 2/2", 7);
        Project.Task tg2T3 = tg2.createTask("Task 2/3", 13);
        tg2.createTask("Task 2/4", 9);
        tg2T3.setColor(new Color("green")); // Specific color for this task
        tg2T1.setCompleted(35); // This task is 35% complete
        project.dependsOn(tg2T2, tg1);
        project.dependsOn(tg1T2, tg2T1);

        // Plot the project on a Gantt Chart
        GanttChart gc = new GanttChart(project);

        // Add the Gantt Chart to our chart component
        soChart.add(gc);

        // Click event
        soChart.addClickEventListener(gc, e -> System.err.println("Event: " + e));
    }

    private static void lineChart(SOChart soChart) {
        // Generating some random values for a LineChart
        Random random = new Random();
        Data xValues = new Data(), yValues = new Data();
        for (int x = 0; x < 40; x++) {
            xValues.add(x);
            yValues.add(random.nextDouble());
        }
        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        LineChart lineChart = new LineChart(xValues, yValues);
        lineChart.setName("40 Random Values");

        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Click event
        soChart.addClickEventListener(lineChart, e -> System.err.println("Event: " + e));
    }
}
