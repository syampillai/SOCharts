package com.storedobject.chart;

public interface Component extends ComponentPart {

    default void addParts(SOChart soChart) {
    }
}