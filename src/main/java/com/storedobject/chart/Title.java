package com.storedobject.chart;

public class Title extends AbstractDisplayablePart implements Component {

    private String text, subtext;
    private Position position;

    public Title(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encode(sb, "text", text);
        String t = getSubtext();
        if(t != null) {
            sb.append(',');
            ComponentPart.encode(sb, "subtext", t);
        }
        Position p = getPosition();
        if(p != null) {
            sb.append(',');
            p.encodeJSON(sb);
        }
    }

    @Override
    public void validate() {
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}