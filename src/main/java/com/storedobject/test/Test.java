package com.storedobject.test;

import com.storedobject.chart.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.Random;

@Route("")
public class Test extends VerticalLayout {

    private final SOChart soChart = new SOChart() {
        @Override
        protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
            //System.err.printf("%d>> %s%n", data.getSerial(), json);
            return super.customizeDataJSON(json, data);
        }

        @Override
        protected String customizeJSON(String json) throws Exception {
            //System.out.printf("JSON: %s%n", json);;
            return super.customizeJSON(json);
        }
    };

    public Test() {
        setSizeFull();
        soChart.setSizeFull();
        soChart.debug(true, true, true);
        drawMenu();
    }

    private void drawMenu() {
        removeAll();
        add(new Button("Gantt Chart", e -> build(() -> ganttChart(soChart))));
        add(new Button("Line Chart", e -> build(() -> lineChart(soChart))));
        add(new Button("Line & Bar Chart", e -> build(() -> lineAndBar(soChart))));
        add(new Button("Radar Chart", e -> build(() -> radarChart(soChart))));
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
        gc.setClickable(true);

        // Add the Gantt Chart to our chart component
        soChart.add(gc);

        // Click event
        soChart.addClickListener(gc, e -> notify("Event: " + e));
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
        xAxis.setClickable(true);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Click event
        soChart.addClickListener(lineChart, e -> notify("Event: " + e));
        soChart.addClickListener(e -> notify("Empty space: " + e));
        soChart.addClickListener(xAxis, e -> notify("X-Axis: " + e));
    }

    private static void notify(String message) {
        Notification.show(message);
    }

    private static void lineAndBar(SOChart soChart) {
        // Generating some random values for the bar charts
        Random random = new Random();
        CategoryData xValues = new CategoryData();
        Data yValues1 = new Data();
        Data yValues2 = new Data();
        Data yValues3 = new Data();

        for (int x = 0; x <= 6; x++) {
            xValues.add("" + (2010 + x));
            yValues1.add(random.nextInt(100));
            yValues2.add(random.nextInt(100));
            yValues3.add(random.nextInt(100));
        }

        // Define axes
        XAxis xAxis = new XAxis(xValues);
        xAxis.setMinAsMinData();
        YAxis yAxis1 = new YAxis(yValues1);

        // Bar charts are initialized with the generated XY values
        BarChart barChart1 = new BarChart(xValues, yValues1);
        barChart1.setName("BarCh1");


        BarChart barChart2 = new BarChart(xValues, yValues2);
        barChart1.setName("BarCh2");

        BarChart barChart3 = new BarChart(xValues, yValues3);
        barChart1.setName("BarCh3");


        //barchart1 position
        // Use a coordinate system
        RectangularCoordinate rc1 = new RectangularCoordinate(xAxis, yAxis1);
        barChart1.plotOn(rc1); // Specifying axis because there are more axes
        Position p = new Position();
        // p.justifyLeft();
        p.setRight(Size.percentage(55));
        p.setBottom(Size.percentage(55));
        // p.alignTop();
        rc1.setPosition(p);

        //barchart2 position
        // Use a coordinate system
        RectangularCoordinate rc2 = new RectangularCoordinate(xAxis, yAxis1);
        barChart2.plotOn(rc2); // Specifying axis because there are more axes
        p = new Position();
        // p.justifyLeft();
        p.setLeft(Size.percentage(55));
        p.setBottom(Size.percentage(55));
        // p.alignTop();
        rc2.setPosition(p);

        // barchart3 position
        // Use a coordinate system
        RectangularCoordinate rc3 = new RectangularCoordinate(xAxis, yAxis1);
        barChart3.plotOn(rc3); // Specifying axis because there are more axes
        p = new Position();
        // p.justifyLeft();
        p.setRight(Size.percentage(55));
        p.setTop(Size.percentage(55));
        // p.alignTop();
        rc3.setPosition(p);


        //chart4 - linechart

        Data xValuesLC = new Data(), yValuesLC = new Data();

        for (int x = 0; x < 40; x++) {
            xValuesLC.add(x);
            yValuesLC.add(random.nextDouble());
        }

        XAxis xAxisLC = new XAxis(DataType.NUMBER);
        YAxis yAxisLC = new YAxis(DataType.NUMBER);

        xAxisLC.setMax(40);
        LineChart lineChart = new LineChart(xValuesLC, yValuesLC);

        RectangularCoordinate rc4 = new RectangularCoordinate(xAxisLC, yAxisLC);
        lineChart.plotOn(rc4);

        // position bottom right
        p = new Position();
        p.setLeft(Size.percentage(55));
        p.setTop(Size.percentage(55));

        rc4.setPosition(p);

        // Line Chart - rc4 renders only first 6 data points which is roughly the size of other Bar charts Xaxis items
        soChart.add(rc1, rc2, rc3, rc4);

        // changing order of adding charts to SOChart container is a possible workaround but other charts Xaxis
        // get distorted especially in the case of dynamic data pust

        // soChart.add(rc4, rc2, rc3, rc1);

    }

    private static void radarChart(SOChart soChart) {

        // Legs for the radar chart (We want a 4-legged radar here)
        CategoryData legs = new CategoryData("Quarter 1", "Quarter 2", "Quarter 3", "Quarter 4");
        Data budget = new Data(3500, 3200, 2500, 5000); // Sales budgets for each quarter
        budget.setName("Budget");
        Data sales = new Data(3200, 4500, 1300, 4000); // Sales for each quarter
        sales.setName("Sales");

        // Radar chart
        RadarChart chart = new RadarChart();
        chart.addData(budget, sales);

        // Radar chart needs to be plotted on a Radar Coordinate system
        RadarCoordinate radarCoordinate = new RadarCoordinate(legs);
        radarCoordinate.setCircular(true);
        chart.plotOn(radarCoordinate);

        // Add to the chart display area
        soChart.add(chart);
    }
}
