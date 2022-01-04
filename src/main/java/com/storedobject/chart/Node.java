package com.storedobject.chart;


import com.storedobject.helper.ID;
import com.vaadin.flow.component.html.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node implements ComponentPart {
    private final long id = ID.newID();
    private int serial;

    private final String name;
    private Object info;
    private List<Edge> edges;
    private double xCoord;
    private double yCoord;
    private final HashMap<String, Runnable> eventListeners = new HashMap<>();
    private Type category;

    public Node(String name) {
        this(name, null);
    }

    public Node(String name, Object info) {
        this(name, info, null);
    }

    public Node(String name, Object info, Type category) {
        this(name, 0., 0., info, category);
    }
    public Node(String name, double x, double y, Object info, Type category) {
        this.name = name;
        this.xCoord = x;
        this.yCoord = y;
        this.edges = new ArrayList<>();
        this.category = category;
        this.info = (TooltipView) info;

    }

    public double getXCoord() { return this.xCoord; }
    public Node setXCoord(double x) {
        this.xCoord = x;
        return this;
    }

    public double getYCoord() {return this.yCoord; }
    public Node setYCoord(double y) {
        this.yCoord = y;
        return this;
    }

    public Type getCategory() {
        return category;
    }

    public Node setCategory(Type category) {
        this.category = category;
        return this;
    }

    public final TooltipView getInfo() {
        return (TooltipView) this.info;
    }
    public Node setInfo(Object info) {
        this.info = (TooltipView) info;
        return this;
    }
    public Edge connectWith(Node target) {
        Edge edge = new Edge(this, target);
        this.edges.add(edge);
        return edge;
    }

    public Node addEventListener(String eventName, Runnable action) {
        this.eventListeners.put(eventName, action);
        return this;
    }

    public HashMap<String, Runnable> getEventListeners() {
        return this.eventListeners;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final long getId() {
        return id;
    }

    @Override
    public void setSerial(int serial) {
        this.serial = serial;
    }

    @Override
    public int getSerial() {
        return serial;
    }


    public List<Edge> getEdges() {
        return edges;
    }

    public Node setEdges(List<Edge> edges) {
        this.edges = edges;
        return this;
    }

    @Override
    public void validate() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append("{\"name\":").append(ComponentPart.escape(this.getName()));
        ComponentPart.encode(sb, "x", this.getXCoord());
        ComponentPart.encode(sb, "y", this.getYCoord());
        if(this.getInfo() != null) {
            sb.append(",\"tooltip\":{");
            ComponentPart.encode(sb, "formatter", this.getInfo().formatter().getElement().getOuterHTML());
            sb.append("}");
        }
        if (this.category != null) {
            ComponentPart.encode(sb, "category", this.getCategory().getName());
        }
        sb.append('}');
    }

    public static class Type implements ComponentPart {
        private final long id = ID.newID();
        private int serial;

        private final String name;
        private final Symbol symbol;


        public Type(String name) {
            this.name = name;
            this.symbol = new Symbol();
            this.symbol.setType(PointSymbolType.ROUND_RECTANGLE);
        }

        public Type setSymbolType(PointSymbolType type) {
            this.symbol.setType(type);
            return this;
        }

        public Type setSymbolImage(String url) {
            this.symbol.setUrl(url);
            return this;
        }

        public Type setSymbolSvgPath(String svgPath) {
            this.symbol.setSvgPath(svgPath);
            return this;
        }

        public Type setSymbolSize(int width) {
            this.symbol.setSize(width);
            return this;
        }

        public Type setSymbolSize(int width, int height) {
            this.symbol.setSize(width, height);
            return this;
        }

        @Override
        public final String getName() {
            return name;
        }

        @Override
        public final long getId() {
            return id;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }

        @Override
        public int getSerial() {
            return serial;
        }


        @Override
        public void validate() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append("{\"name\":").append(ComponentPart.escape(this.getName()));
            sb.append(',');
            this.symbol.encodeJSON(sb);
            sb.append('}');
        }
    }

    public static class Symbol implements ComponentProperty {
        private PointSymbolType type = PointSymbolType.CIRCLE;
        private boolean show = true;
        String size;
        private boolean hoverAnimation = true;
        private String url;
        private String svgPath;
        private Boolean isURL = true;


        public Symbol() {
            this.url = "";
            this.svgPath = "";
        }

        public void setUrl(String url) {
            this.setType(PointSymbolType.NONE);
            this.isURL = true;
            this.url = String.format("image://%s", url);
        }

        public String getCustomSymbol() {
            return this.isURL ? this.url : this.svgPath;
        }
        public void setSvgPath(String svgPath) {
            this.setType(PointSymbolType.NONE);
            this.isURL = false;
            this.svgPath = String.format("path://%s", svgPath);
        }

        public void show() {
            show = true;
        }


        public void hide() {
            show = false;
        }


        public void setType(PointSymbolType pointSymbolType) {
            this.type = pointSymbolType;
        }


        public void setSize(int size) {
            if(size <= 0) {
                this.size = null;
            } else {
                this.size = "" + size;
            }
        }

        public void setSize(int width, int height) {
            if(width > 0 && height > 0) {
                this.size = "[" + width + "," + height + "]";
            } else if(width > 0) {
                setSize(width);
            } else if(height > 0) {
                setSize(height);
            } else {
                this.size = null;
            }
        }

        public void setHoverAnimation(boolean hoverAnimation) {
            this.hoverAnimation = hoverAnimation;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            String t = this.type != PointSymbolType.NONE ? this.type.toString() : this.getCustomSymbol();
            ComponentPart.encode(sb, "showSymbol", show);
            ComponentPart.encode(sb,"symbol", t);
            if(size != null) {
                ComponentPart.addComma(sb);
                sb.append("\"symbolSize\":").append(size);
            }
            ComponentPart.encode(sb, "hoverAnimation", hoverAnimation);
        }
    }
}
