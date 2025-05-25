package com.storedobject.test;

import com.storedobject.chart.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Route("")
public class Test extends VerticalLayout {

    private Timer timer;
    private UI ui;

    private final SOChart soChart = new SOChart() {
        @Override
        protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
            System.err.printf("%d>> %s%n", data.getSerial(), json);
            return super.customizeDataJSON(json, data);
        }

        @Override
        protected String customizeJSON(String json) throws Exception {
            System.out.printf("---------------%nJSON: %s%n", json);
            return super.customizeJSON(json);
        }
    };

    public Test() {
        setSizeFull();
        soChart.setSizeFull();
        soChart.debug(false, true, true);
        drawMenu();
    }

    private void drawMenu() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        ui = UI.getCurrent();
        ui.setPollInterval(1000); // Need for dynamic data push
        removeAll();
        add(new Button("Gantt Chart", e -> build(() -> ganttChart(soChart))));
        add(new Button("Line Chart", e -> build(() -> lineChart(soChart))));
        add(new Button("Line & Bar Chart", e -> build(() -> lineAndBar(soChart))));
        add(new Button("Radar Chart", e -> build(() -> radarChart(soChart))));
        add(new Button("Nightingale Rose Chart", e -> build(() -> nightingaleChart(soChart))));
        add(new Button("Chart with Mark Area", e -> build(() -> withMarkAreaChart(soChart))));
        add(new Button("Chart Push", e -> build(() -> new ChartPush(soChart))));
        add(new Button("Custom Tooltip", e -> build(() -> withCustomTooltip(soChart))));
        add(new Button("Simple Graph Chart", e -> build(() -> simpleGraphChart(soChart))));
        add(new Button("XY Graph Chart", e -> build(() -> xyGraphChart(soChart))));
        add(new Button("Heatmap Chart", e -> build(() -> heatmapChart(soChart))));
        add(new Button("Bubble Chart", e -> build(() -> bubbleChart(soChart))));
        add(new Button("Boxplot Chart", e -> build(() -> boxplotChart(soChart))));
    }

    private void build(Runnable builder) {
        removeAll();
        soChart.removeAll();
        add(new Button("Back to Menu", e -> drawMenu()));
        add(soChart);
        try {
            builder.run();
            soChart.update();
        } catch (Throwable t) {
            t.printStackTrace();
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
        xAxis.setAllowEvents(true);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Click event
        //soChart.addListener(lineChart, ChartEventType.Click, e -> notify("Event: " + e));
        //soChart.addListener(ChartEventType.Click, e -> notify("Empty space: " + e));
        //soChart.addListener(xAxis, ChartEventType.Click, e -> notify("X-Axis: " + e));
        soChart.addListener(EventType.BlankAreaClick, e -> {
            v = !v;
            System.err.println("Visible: " + v);
            soChart.setVisible(v, lineChart);
        });
    }

    private static boolean v = true;

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
        // get distorted especially in the case of dynamic data push

        // soChart.add(rc4, rc2, rc3, rc1);

        soChart.addListener(EventType.Legend, e -> notify("Legend: " + e));
        soChart.addListener(EventType.Click, e -> notify("Clicked: " + e), lineChart);
        soChart.addListener(EventType.Click, e -> notify("Clicked: " + e), barChart2, 4);
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

    private static void nightingaleChart(SOChart soChart) {
        // Let us define some inline data
        CategoryData labels = new CategoryData("Banana", "Apple", "Orange", "Grapes");
        Data data = new Data(25, 40, 20, 30);

        // We are going to create a couple of charts. So, each chart should be positioned appropriately
        // Create a self-positioning chart
        NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
        Position p = new Position();
        p.setTop(Size.percentage(50));
        nc.setPosition(p); // Position it leaving 50% space at the top

        // Second chart to add
        BarChart bc = new BarChart(labels, data);
        RectangularCoordinate coordinate =
                new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
        p = new Position();
        p.setBottom(Size.percentage(55));
        coordinate.setPosition(p); // Position it leaving 55% space at the bottom
        bc.plotOn(coordinate); // Bar chart needs to be plotted on a coordinate system

        // Just to demonstrate it, we are creating a "Download" and a "Zoom" toolbox button
        Toolbox toolbox = new Toolbox();
        toolbox.addButton(new Toolbox.Download(), new Toolbox.Zoom());

        // Let's add some titles
        Title title = new Title("My First Chart");
        title.setSubtext("2nd Line of the Title");

        // Add the chart components to the chart display area
        soChart.add(nc, bc, toolbox, title);
    }

    private static void withMarkAreaChart(SOChart soChart) {

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

        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);

        // Mark area (From x = 20 to x = 30)
        MarkArea.Block area20_30 = new MarkArea.Block();
        area20_30.setName("Area (20 to 30)");
        area20_30.setSides(xAxis, 20, 30);

        // Mark area (Both X and Y axis are specified).
        MarkArea.Block rectBlock = new MarkArea.Block();
        rectBlock.setName("Rectangular Block");
        rectBlock.setSides(xAxis, yAxis, 15, 0.2, "max", 0.8);
        rectBlock.getItemStyle(true).setColor(new Color("red"));

        // Add Mark Areas to the chart.
        lineChart.getMarkArea(true).add(area20_30, rectBlock);

        // Line chart needs a coordinate system to plot on
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        soChart.addListener(EventType.Click, e -> notify("Clicked: " + e), lineChart);
    }

    private class ChartPush {

        private final Random randomGen = new Random();
        private final TimeData seconds = new TimeData();
        private final Data random = new Data();
        private final DataChannel dataChannel;

        public ChartPush(SOChart soChart) {

            // Axes
            XAxis xAxis = new XAxis(seconds);
            xAxis.setMinAsMinData();
            xAxis.setMaxAsMaxData();
            xAxis.getLabel(true).setFormatter("{ss}");
            YAxis yAxis = new YAxis(random);
            yAxis.setMin(0);
            yAxis.setMax(100);

            // Line chart
            LineChart lc = new LineChart(seconds, random);
            lc.plotOn(new RectangularCoordinate(xAxis, yAxis));
            lc.setSmoothness(true);

            // Add the chart to the chart display
            soChart.add(lc);

            // Create a data channel to push the data
            dataChannel = new DataChannel(soChart, seconds, random);
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(
                    new TimerTask() { // Generate a tick in every second
                        @Override
                        public void run() {
                            data();
                        }
                    },
                    5000L,
                    1000L);
        }

        private void data() {
            // Generate a new data point
            seconds.add(System.currentTimeMillis());
            random.add(randomGen.nextInt(100));
            int lastIndex = seconds.size() - 1;
            ui.access(() -> { // Required to lock the UI
                try {
                    if (lastIndex < 60) {
                        // Append data if data-size is less than 60
                        dataChannel.append(seconds.get(lastIndex), random.get(lastIndex));
                    } else {
                        // Push data if the data-size is more than 60 (tail-end will be trimmed)
                        dataChannel.push(seconds.get(lastIndex), random.get(lastIndex));
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    private static void withCustomTooltip(SOChart soChart) {

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

        // Customize tooltips of the line chart
        AbstractDataProvider<?> yFormattedValues =
                yValues.create(
                        DataType.CATEGORY,
                        (v, i) ->
                                v.toString()
                                        .substring(0, 4)); // Specially formatted Y values (Bad code to trim decimals!)
        lineChart
                .getTooltip(true) // Get the tooltip
                .append("My Special Tooltip") // Added some text
                .newline() // New line
                .append("X = ")
                .append(xValues) // X values
                .newline() // New line
                .append("Y = ")
                .append(yFormattedValues); // Customized Y values

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Line Chart with Customized Tooltips"));

    }

    private static void simpleGraphChart(SOChart soChart) {

        GraphData<GraphData.XYNode> g = new GraphData<>();
        g.draggable(true);
        g.getForce(true);
        g.getDefaultCategory().setSize(40);
        GraphData.XYNode n1 = new GraphData.XYNode("Node 1", 50, 100);
        GraphData.XYNode n2 = new GraphData.XYNode("Node 2", 50, 120);
        GraphData.XYNode n3 = new GraphData.XYNode("Node 3", 150, 150);
        GraphData.XYNode n4 = new GraphData.XYNode("Node 4", 200, 80);
        n1.connectTo(n2).connectTo(n3).connectTo(n4);
        n1.connectTo(n3);
        n1.connectTo(n4);
        n2.connectTo(n1);
        n4.connectTo(n3);
        g.addNode(n1, n2, n3, n4);

        GraphChart gc = new GraphChart(g);
        soChart.add(gc);
        soChart.addListener(EventType.Click, e -> notify("Clicked: " + e), gc, "edge");
    }

    private static void xyGraphChart(SOChart soChart) {
        CategoryData days = new CategoryData("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        GraphData<GraphData.ValueNode> g = new GraphData<>();
        g.draggable(true).getForce(true);
        g.getDefaultCategory().setSize(40);
        Random random = new Random();
        days.forEach(d -> {
            GraphData.ValueNode n = new GraphData.ValueNode(random.nextDouble(2000));
            n.setName(d);
            g.connectFromLastNode(n);
            g.addNode(n);
        });
        XYGraphChart chart = new XYGraphChart(days, g);
        chart.plotOn(new RectangularCoordinate(new XAxis(days), new YAxis(DataType.NUMBER)));

        soChart.add(chart);
    }

    private static void heatmapChart(SOChart soChart) {

        // Heatmap chart requires 2 category axes and then, values to be added for each data-point
        CategoryData days = new CategoryData("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        CategoryData slots = new CategoryData("Morning", "Noon", "Afternoon", "Evening", "Night");

        // Create the chart.
        HeatmapChart chart = new HeatmapChart(days, slots);
        chart.getLabel(true).show(); // Want to display the value as labels

        // Add some data-points
        chart.addData(0, 0, 27); // Sunday morning
        chart.addData(0, 3, 28); // Sunday evening
        chart.addData(1, 3, 31); // Monday evening
        chart.addData(1, 4, 25); // Monday night
        chart.addData("Wed", "Noon", 37); // Values can be added by directly addressing X/Y values too.

        // Heatmap charts should be plotted on a rectangular coordinate system
        chart.plotOn(new RectangularCoordinate(new XAxis(days), new YAxis(slots)));

        // Add to the chart display area
        soChart.add(chart);
    }

    private static void bubbleChart(SOChart soChart) {

        // Bubble chart requires 2 axes and then, values to be added for each data-point
        CategoryData days = new CategoryData("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        CategoryData slots = new CategoryData("Morning", "Noon", "Afternoon", "Evening", "Night");

        // Create the chart.
        BubbleChart chart = new BubbleChart(days, slots);
        chart.setBubbleSize(2); // Size of the bubble will be 2 times the temperature
        chart.setValueSuffix("°C");

        // Add some data-points
        chart.addData(0, 0, 27); // Sunday morning
        chart.addData(0, 3, 28); // Sunday evening
        chart.addData(1, 3, 31); // Monday evening
        chart.addData(1, 4, 25); // Monday night
        chart.addData("Wed", "Noon", 37); // Values can be added by directly addressing X/Y values too.

        // Bubble charts should be plotted on a rectangular coordinate system
        chart.plotOn(new RectangularCoordinate(new XAxis(days), new YAxis(slots)));

        // Add to the chart display area
        soChart.add(chart);
    }

    private void boxplotChart(SOChart soChart) {
        CategoryData days = new CategoryData("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        BoxplotData boxplotData = new BoxplotData();
        Random r = new Random();
        days.forEach(d -> boxplotData.add(boxplotData(r)));
        BoxplotChart chart = new BoxplotChart(days, boxplotData);

        chart.plotOn(new RectangularCoordinate(new XAxis(days), new YAxis(DataType.NUMBER)));

        // Add to the chart display area
        soChart.add(chart);

        soChart.addListener(EventType.Click, e -> notify("Click: " + e), chart);
    }

    private BoxplotData.Boxplot boxplotData(Random r) {
        double min = r.nextDouble(1000);
        double max;
        do {
            max = r.nextDouble(1000);
        } while (!(max > min));
        double median;
        do {
            median = r.nextDouble(1000);
        } while (!(median > min) || !(median < max));
        double q1;
        do {
            q1 = r.nextDouble(1000);
        } while (!(q1 > min) || !(q1 < median));
        double q3;
        do {
            q3 = r.nextDouble(1000);
        } while (!(q3 > median) || !(q3 < max));
        return new BoxplotData.Boxplot(min, q1, median, q3, max);
    }
}
