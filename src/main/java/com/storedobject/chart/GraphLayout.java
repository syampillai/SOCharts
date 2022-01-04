package com.storedobject.chart;

public enum GraphLayout {
    FORCE,
    CIRCULAR,
    NONE;

    @Override
    public String toString() {
        return "\"" + super.toString().toLowerCase() + "\"";
    }
}
