package com.storedobject.chart;

/**
 * Class to support animation related properties.
 *
 * @author Syam
 */
public class Animation extends AbstractProperty {

    @Override
    protected void encodeStatus(StringBuilder sb) {
        ComponentPart.encode(sb, "animation", !disabled);
    }

    @Override
    protected void encodeProperty(StringBuilder sb) {
    }
}
