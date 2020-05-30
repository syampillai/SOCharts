package com.storedobject.chart;

/**
 * Representation of polar coordinate system with an angle axis and a radius axis.
 *
 * @author Syam
 */
public class PolarCoordinate extends CoordinateSystem {

    private AngleAxis<?> angleAxis;
    private RadiusAxis<?> radiusAxis;

    /**
     * Constructor.
     */
    public PolarCoordinate() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param angleAxis Angle axis.
     * @param radiusAxis Radius axis.
     */
    public PolarCoordinate(AngleAxis<?> angleAxis, RadiusAxis<?> radiusAxis) {
        this.angleAxis = angleAxis;
        this.radiusAxis = radiusAxis;
    }

    /**
     * Get the angle axis.
     *
     * @return Angle axis.
     */
    public AngleAxis<?> getAngleAxis() {
        return angleAxis;
    }

    /**
     * Set the angle axis.
     *
     * @param angleAxis Angle axis to set.
     */
    public void setAngleAxis(AngleAxis<?> angleAxis) {
        this.angleAxis = angleAxis;
    }

    /**
     * Get the radius axis.
     *
     * @return Radius axis.
     */
    public RadiusAxis<?> getRadiusAxis() {
        return radiusAxis;
    }

    /**
     * Set the radius axis.
     *
     * @param radiusAxis Radius axis to set.
     */
    public void setRadiusAxis(RadiusAxis<?> radiusAxis) {
        this.radiusAxis = radiusAxis;
    }

    @Override
    public void validate() throws Exception {
        if(radiusAxis == null) {
            throw new Exception("Radius Axis is not set");
        }
        if(angleAxis == null) {
            throw new Exception("Angle Axis is not set");
        }
        if(radiusAxis.coordinateSystem != null && radiusAxis.coordinateSystem != this) {
            throw new Exception("Radius Axis is used by some other coordinate system");
        }
        radiusAxis.coordinateSystem = this;
        if(angleAxis.coordinateSystem != null && angleAxis.coordinateSystem != this) {
            throw new Exception("Polar Axis is used by some other coordinate system");
        }
        angleAxis.coordinateSystem = this;
        radiusAxis.validate();
        angleAxis.validate();
    }

    @Override
    public void addParts(SOChart soChart) {
        radiusAxis.coordinateSystem = this;
        angleAxis.coordinateSystem = this;
        soChart.addParts(radiusAxis, angleAxis);
        super.addParts(soChart);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
    }
}
