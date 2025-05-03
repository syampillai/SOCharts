package com.storedobject.chart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents an abstract chart where data points are plotted based on X and Y coordinates. It is designed to
 * be used with a rectangular coordinate system and supports managing data points and associated visual representations.
 * Subclasses implement specific chart types such as heatmaps or bubble charts.
 *
 * @author Syam
 */
public abstract class XYDataChart extends Chart {

    private final List<Value> data = new ArrayList<>();
    private final XYData xyData = new XYData();
    private VisualMap visualMap = new VisualMap(this);

    /**
     * Constructor.
     *
     * @param chartType Chart type.
     * @param xData Labels on the X-axis.
     * @param yData Labels on the Y-axis.
     */
    public XYDataChart(ChartType chartType, AbstractDataProvider<?> xData, AbstractDataProvider<?> yData) {
        super(chartType);
        setData(xData, yData, xyData);
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(coordinateSystem == null || !RectangularCoordinate.class.isAssignableFrom(coordinateSystem.getClass())) {
            throw new ChartException(ComponentPart.className(getClass())
                    + " must be plotted on a rectangular coordinate system");
        }
        RectangularCoordinate rc = (RectangularCoordinate) coordinateSystem;
        rc.validate();
        rc.axes.get(0).setData(getData()[0]);
        rc.axes.get(1).setData(getData()[1]);
    }

    /**
     * Add data point.
     *
     * @param xIndex X-index at which data needs to be added.
     * @param yIndex Y-index at which data needs to be added.
     * @param value Value to be added.
     */
    public void addData(int xIndex, int yIndex, Number value) {
        data.add(new Value(xIndex, yIndex, value));
    }

    /**
     * Add data point.
     *
     * @param xValue X-value at which data needs to be added.
     * @param yIndex Y-index at which data needs to be added.
     * @param value Value to be added.
     */
    public void addData(String xValue, int yIndex, Number value) {
        data.add(new Value(xValue, yIndex, value));
    }

    /**
     * Add data point.
     *
     * @param xIndex X-index at which data needs to be added.
     * @param yValue Y-value at which data needs to be added.
     * @param value Value to be added.
     */
    public void addData(int xIndex, String yValue, Number value) {
        data.add(new Value(xIndex, yValue, value));
    }

    /**
     * Add heatmap data point.
     *
     * @param xValue X-value at which data needs to be added.
     * @param yValue Y-value at which data needs to be added.
     * @param value Value to be added.
     */
    public void addData(String xValue, String yValue, Number value) {
        data.add(new Value(xValue, yValue, value));
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return xyData;
    }

    @Override
    protected int dataValueIndex() {
        return 2;
    }

    @Override
    public void addParts(SOChart soChart) {
        super.addParts(soChart);
        if(visualMap != null) {
            soChart.addParts(visualMap);
        }
    }

    /**
     * Get the {@link VisualMap} associated with this chart.
     *
     * @return The {@link VisualMap} instance.
     */
    public final VisualMap getVisualMap() {
        return visualMap;
    }

    /**
     * Set a {@link VisualMap} for this chart. Set <code>null</code> if you don't want any {@link VisualMap}.
     *
     * @param visualMap {@link VisualMap} to set.
     */
    public void setVisualMap(VisualMap visualMap) {
        this.visualMap = visualMap;
    }

    private class XYData extends BasicInternalDataProvider<Value> {

        public Stream<Value> stream() {
            return data.stream();
        }

        @Override
        public Value getMin() {
            return stream().min(new XYData.XYValueComparator()).orElse(null);
        }

        @Override
        public Value getMax() {
            super.getMax();
            return stream().max(new XYValueComparator()).orElse(null);
        }

        @Override
        public void encode(StringBuilder sb, Value value) {
            sb.append('[');
            if(value.xIndex instanceof Number n) {
                sb.append(n.intValue());
            } else {
                sb.append('"').append(value.xIndex).append('"');
            }
            sb.append(',');
            if(value.yIndex instanceof Number n) {
                sb.append(n.intValue());
            } else {
                sb.append('"').append(value.yIndex).append('"');
            }
            sb.append(',');
            sb.append(value.value).append(']');
        }

        private static class XYValueComparator implements Comparator<Value> {

            private static final NumberComparator numberComparator = new NumberComparator();

            @Override
            public int compare(Value o1, Value o2) {
                return numberComparator.compare(o1.value, o2.value);
            }
        }
    }

    private record Value(Object xIndex, Object yIndex, Number value) {
    }
}
