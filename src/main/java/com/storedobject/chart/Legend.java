package com.storedobject.chart;

public class Legend extends AbstractComponentPart implements Component {

    private Position position;

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
    }

    @Override
    public void validate() {
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
