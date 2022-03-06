package com.storedobject.chart;

/**
 * Class to support properties related "emphasis".
 *
 * @author Syam
 */
public class Emphasis extends AbstractProperty {

    @Override
    protected void encodeStatus(StringBuilder sb) {
        ComponentPart.encode(sb, "disabled", disabled);
    }

    @Override
    protected void encodeProperty(StringBuilder sb) {
    }
}
