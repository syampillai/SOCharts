package com.storedobject.chart;

import com.storedobject.helper.ID;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GraphChart extends SelfPositioningChart {

    private final List<Node> nodes = new ArrayList<>();
    private final NodeDataProvider nd;
    private boolean roam = false;
    private boolean draggable = false;
    private boolean autoCurveness = true;
    private PointSymbol edgeStartSymbol = new PointSymbol();
    private PointSymbol edgeEndSymbol = new PointSymbol(PointSymbolType.ARROW);
    private Category defaultCategory = new Category("Default");
    private final List<Category> categories = new ArrayList<>();
    private Layout layout = Layout.FORCE;
    private Offset center = null;
    private Force force;

    public GraphChart() {
        super(ChartType.Graph);
        this.nd = new NodeDataProvider();
        super.setData(nd);
    }

    public Category getDefaultCategory() {
        return defaultCategory;
    }

    public GraphChart defaultCategory(Category defaultCategory) {
        setDefaultCategory(defaultCategory);
        return this;
    }

    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory == null ? new Category("Default") : defaultCategory;
    }

    public void setCenter(Offset center) {
        this.center = center;
    }

    public GraphChart center(Offset center) {
        setCenter(center);
        return this;
    }

    public Offset getCenter(boolean createIfNotExist) {
        if(center == null && createIfNotExist) {
            center = new Offset();
        }
        return center;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public GraphChart layout(Layout layout) {
        this.layout = layout;
        return this;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public void setRoam(boolean roam) {
        this.roam = roam;
    }

    public GraphChart roam(boolean roam) {
        this.roam = roam;
        return this;
    }

    public boolean isRoam() {
        return this.roam;
    }

    public Force getForce(boolean createIfNotExist) {
        if(force == null && createIfNotExist) {
            force = new Force();
        }
        return force;
    }

    public GraphChart force(Force force) {
        this.force = force;
        return this;
    }

    public void setForce(Force force) {
        this.force = force;
    }

    public void setAutoCurveness(boolean autoCurveness) {
        this.autoCurveness = autoCurveness;
    }

    public GraphChart autoCurveness(boolean autoCurveness) {
        this.autoCurveness = autoCurveness;
        return this;
    }

    public boolean isAutoCurveness() {
        return this.autoCurveness;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public GraphChart draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public final PointSymbol getEdgeStartSymbol() {
        return this.edgeStartSymbol;
    }

    public GraphChart edgeStartSymbol(PointSymbol symbol) {
        this.edgeStartSymbol = symbol == null ? new PointSymbol(PointSymbolType.NONE) : symbol;
        return this;
    }

    public final PointSymbol getEdgeEndSymbol() {
        return this.edgeEndSymbol;
    }

    public GraphChart edgeEndSymbol(PointSymbol symbol) {
        this.edgeEndSymbol = symbol == null ? new PointSymbol(PointSymbolType.NONE) : symbol;
        return this;
    }

    public void setEdgeEndSymbol(PointSymbol symbol) {
        this.edgeEndSymbol = symbol == null ? new PointSymbol(PointSymbolType.NONE) : symbol;
    }

    public void addNode(Node... nodes) {
        if(nodes != null) {
            for(Node node: nodes) {
                if(node != null) {
                    this.nodes.add(node);
                }
            }
        }
    }

    public GraphChart node(Node... nodes) {
        addNode(nodes);
        return this;
    }

    @Override
    public void validate() throws ChartException {
        if (this.nodes.isEmpty()) {
            throw new ChartException("Data not set for " + className());
        }
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return nd;
    }

    private int indexOf(Category category) {
        if(category == null) {
            category = defaultCategory;
        }
        int i = categories.indexOf(category);
        if(i < 0) {
            categories.add(category);
            i = categories.size() - 1;
        }
        return i;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        nodes.forEach(n -> indexOf(n.category)); // Ensure categories are added.
        super.encodeJSON(sb);
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, "layout", this.getLayout());
        ComponentPart.encode(sb, "roam", this.isRoam());
        ComponentPart.encode(sb, "draggable", this.isDraggable());
        ComponentPart.encode(sb, "autoCurveness", this.isAutoCurveness());
        Offset center = this.getCenter(false);
        if(center != null) {
            center.encodeJSON("center", sb);
        }
        sb.append(String.format(",\"edgeSymbol\": [%s, %s]",
                edgeStartSymbol.getType().toString(), edgeEndSymbol.getType().toString()));
        sb.append(String.format(",\"edgeSymbolSize\":" + edgeStartSymbol.size));
        if(force != null) {
            sb.append(",\"force\":");
            force.encodeJSON(sb);
        }
        AtomicBoolean first = new AtomicBoolean(true);
        sb.append("\"categories\":[");
        first.set(true);
        categories.forEach(c -> {
            if(first.get()) {
                first.set(false);
            } else {
                sb.append(",");
            }
            c.encodeJSON(sb);
        });
        sb.append("],\"links\":[");
        first.set(true);
        nodes.forEach(n -> n.streamEdges().forEach(e -> {
            if(first.get()) {
                first.set(false);
            } else {
                sb.append(",");
            }
            e.encodeJSON(sb);
        }));
        sb.append("]");
    }

    private class NodeDataProvider extends BasicInternalDataProvider<Node> {

        @Override
        public Stream<Node> stream() {
            return nodes.stream();
        }

        @Override
        public void encode(StringBuilder sb, Node value) {
            value.encodeJSON(GraphChart.this, sb);
        }
    }

    public static class Node implements ComponentPart {

        private final long id = ID.newID();
        private final String name;
        private final List<Edge> edges = new ArrayList<>();
        private double x, y;
        private Category category;

        public Node(String name) {
            this(name, null);
        }

        public Node(String name, Category category) {
            this(name, 0, 0, category);
        }

        public Node(String name, double x, double y) {
            this(name, x, y, null);
        }

        public Node(String name, double x, double y, Category category) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.category = category;
        }

        public double getX() {
            return this.x;
        }

        public Node x(double x) {
            this.x = x;
            return this;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return this.y;
        }

        public Node y(double y) {
            this.y = y;
            return this;
        }

        public void setY(double y) {
            this.y = y;
        }

        public Category getCategory() {
            return category;
        }

        public Node category(Category category) {
            this.category = category;
            return this;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public Edge connectTo(Node target) {
            if(target == null) {
                return null;
            }
            Edge edge = edges.stream().filter(e -> e.getDestinationNode() == target).findFirst().orElse(null);
            if(edge != null) {
                return edge;
            }
            edge = new Edge(this, target);
            this.edges.add(edge);
            return edge;
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
        }

        @Override
        public int getSerial() {
            return 0;
        }

        @Override
        public void validate() throws ChartException {
        }

        public Stream<Edge> streamEdges() {
            return edges.stream();
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            ComponentPart.encode(sb, "name", name);
            ComponentPart.encode(sb, "x", this.getX());
            ComponentPart.encode(sb, "y", this.getY());
        }

        public void encodeJSON(GraphChart chart, StringBuilder sb) {
            sb.append("{");
            encodeJSON(sb);
            ComponentPart.encode(sb, "category", chart.indexOf(this.category));
            sb.append("}");
        }
    }

    public static class Category extends PointSymbol {

        private final String name;

        public Category(String name) {
            this.name = name;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append("{");
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "name", name);
            sb.append("}");
        }
    }

    public static class Edge extends LineStyle {

        private final long id = ID.newID();
        private final Node sourceNode, destinationNode;

        private Edge(Node sourceNode, Node destinationNode) {
            this.sourceNode = sourceNode;
            this.destinationNode = destinationNode;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append('{');
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "id", "" + id);
            ComponentPart.encode(sb, "source", "" + sourceNode.id);
            ComponentPart.encode(sb, "target", "" + destinationNode.id);
            sb.append('}');
        }

        public Node getSourceNode() {
            return this.sourceNode;
        }

        public Node getDestinationNode() {
            return this.destinationNode;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Edge  e && e.id == this.id;
        }
    }

    public enum Layout {

        FORCE,
        CIRCULAR,
        NONE;

        @Override
        public String toString() {
            return "\"" + super.toString().toLowerCase() + "\"";
        }
    }

    public static class Force implements ComponentProperty {

        private Layout initialLayout = null;
        private int repulsion = 50;
        private double gravity = 0.1;
        private double friction = 0.6;
        private double edgeLength = 30;
        private boolean layoutAnimation = true;

        public Layout getInitialLayout() {
            return initialLayout;
        }

        public Force initialLayout(Layout initialLayout) {
            this.initialLayout = initialLayout;
            return this;
        }

        public void setInitialLayout(Layout initialLayout) {
            this.initialLayout = initialLayout;
        }

        public int getRepulsion() {
            return repulsion;
        }

        public Force repulsion(int repulsion) {
            this.repulsion = repulsion;
            return this;
        }

        public void setRepulsion(int repulsion) {
            this.repulsion = repulsion;
        }

        public double getGravity() {
            return gravity;
        }

        public Force gravity(double gravity) {
            this.gravity = gravity;
            return this;
        }

        public void setGravity(double gravity) {
            this.gravity = gravity;
        }

        public double getFriction() {
            return friction;
        }

        public Force friction(double friction) {
            this.friction = friction;
            return this;
        }

        public void setFriction(double friction) {
            this.friction = friction;
        }

        public double getEdgeLength() {
            return edgeLength;
        }

        public Force edgeLength(double edgeLength) {
            this.edgeLength = edgeLength;
            return this;
        }

        public void setEdgeLength(double edgeLength) {
            this.edgeLength = edgeLength;
        }

        public boolean isLayoutAnimation() {
            return layoutAnimation;
        }

        public Force layoutAnimation(boolean layoutAnimation) {
            this.layoutAnimation = layoutAnimation;
            return this;
        }

        public void setLayoutAnimation(boolean layoutAnimation) {
            this.layoutAnimation = layoutAnimation;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append("{");
            Layout layout = this.getInitialLayout();
            if(layout != null) {
                ComponentPart.encode(sb, "initialLayout", layout);
            }
            ComponentPart.encode(sb, "repulsion", this.getRepulsion());
            ComponentPart.encode(sb, "gravity", this.getGravity());
            ComponentPart.encode(sb, "friction", this.getFriction());
            ComponentPart.encode(sb, "edgeLength", this.getEdgeLength());
            ComponentPart.encode(sb, "layoutAnimation", this.isLayoutAnimation());
            sb.append('}');
        }
    }
}
