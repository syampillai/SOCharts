package com.storedobject.chart;

public abstract class AbstractComponentPart implements ComponentPart {

    private boolean show = true;
    private final long id = SOChart.id.incrementAndGet();

    public final long getId() {
        return id;
    }

    public void show() {
        show = true;
    }

    public void hide() {
        show = false;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append("\"id\":").append(id).append(',');
        sb.append("\"show\":").append(show).append(',');
        Position p = getPosition();
        if(p != null) {
            int len = sb.length();
            p.encodeJSON(sb);
            if(sb.length() != len) {
                sb.append(',');
            }
        }
    }

    public Position getPosition() {
        return null;
    }
}
