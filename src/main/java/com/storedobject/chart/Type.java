package com.storedobject.chart;

public enum Type {

    Line,
    Bar,
    Pie(new String[] { "itemName", "value" }, false),
    Scatter,
    EffectScatter,
    Funnel(new String[] { "itemName", "value" }, false),
    ;

    private final String[] axes;
    private final boolean coordinateSystem;

    Type() {
        this(new String[] { "x", "y"});
    }

    Type(String[] axes) {
        this(axes, true );
    }

    Type(String[] axes, boolean coordinateSystem) {
        this.axes = axes;
        this.coordinateSystem = coordinateSystem;
    }

    public String[] getAxes() {
        return axes;
    }

    public boolean requireCoordinateSystem() {
        return coordinateSystem;
    }
}