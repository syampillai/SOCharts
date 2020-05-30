package com.storedobject.chart;

/**
 * Representation of rectangular (cartesian) coordinate system with X and Y axes.
 *
 * @author Syam
 */
public class RectangularCoordinate extends CoordinateSystem {

    private XAxis<?> xAxis;
    private YAxis<?> yAxis;
    private Position position;

    /**
     * Constructor.
     */
    public RectangularCoordinate() {
    }

    /**
     * Constructor.
     *
     * @param xAxis X axis.
     * @param yAxis Y axis.
     */
    public RectangularCoordinate(XAxis<?> xAxis, YAxis<?> yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    /**
     * Get the X axis.
     *
     * @return X axis.
     */
    public XAxis<?> getXAxis() {
        return xAxis;
    }

    /**
     * Set the X axis.
     *
     * @param xAxis X axis.
     */
    public void setXAxis(XAxis<?> xAxis) {
        this.xAxis = xAxis;
    }

    /**
     * Get the Y axis.
     *
     * @return Y axis.
     */
    public YAxis<?> getYAxis() {
        return yAxis;
    }

    /**
     * Set the Y axis.
     *
     * @param yAxis Y axis.
     */
    public void setYAxis(YAxis<?> yAxis) {
        this.yAxis = yAxis;
    }

    @Override
    public void validate() throws Exception {
        if(xAxis == null) {
            throw new Exception("X Axis not set");
        }
        if(yAxis == null) {
            throw new Exception("Y Axis not set");
        }
        if(xAxis.coordinateSystem != null && xAxis.coordinateSystem != this) {
            throw new Exception("X Axis is used by some other coordinate system");
        }
        xAxis.coordinateSystem = this;
        if(yAxis.coordinateSystem != null && yAxis.coordinateSystem != this) {
            throw new Exception("Y Axis is used by some other coordinate system");
        }
        yAxis.coordinateSystem = this;
        xAxis.validate();
        yAxis.validate();
    }

    @Override
    public void addParts(SOChart soChart) {
        xAxis.coordinateSystem = this;
        yAxis.coordinateSystem = this;
        soChart.addParts(xAxis, yAxis);
        super.addParts(soChart);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(position != null) {
            position.encodeJSON(sb);
        }
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
