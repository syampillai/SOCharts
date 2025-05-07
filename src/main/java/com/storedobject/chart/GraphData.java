package com.storedobject.chart;

import com.storedobject.helper.ID;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The GraphData class represents a data structure used for managing and visualizing graph-based data.
 * It supports operations such as adding nodes, connecting edges, and configuring various graph properties.
 *
 * @param <N> The type of nodes that the graph will handle.
 *
 * @author Syam
 */
public class GraphData<N extends GraphData.Node> {

    private final List<N> nodes = new ArrayList<>();
    private AbstractDataProvider<N> nd;
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
    private TextLabel textLabel = new TextLabel();

    /**
     * Constructs a new instance of GraphData and initializes default properties.
     * Specifically, the default category's size is set to 10 during construction.
     */
    public GraphData() {
        defaultCategory.setSize(10);
    }

    /**
     * Retrieves the data provider for nodes, initializing it if necessary.
     * The type of data provider (internal or external) depends on the provided parameter.
     *
     * @param internal a boolean flag indicating whether to retrieve an internal node data provider
     *                 (if true, returns an internal node data provider; otherwise, an external node data provider).
     * @return the initialized or existing node data provider, either internal or external,
     *         based on the value of the parameter.
     */
    AbstractDataProvider<N> getNodeData(boolean internal) {
        if(nd != null) {
            if((internal && nd instanceof NodeData) || (!internal && nd instanceof InternalNodeData)) {
                nd = null;
            }
        }
        if(nd == null) {
            nd = internal ? new InternalNodeData() : new NodeData();
        }
        return nd;
    }

    /**
     * Checks whether the graph data is empty or not.
     *
     * @return {@code true} if the data structure contains no nodes, {@code false} otherwise.
     */
    boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Retrieves the default category associated with the graph data.
     *
     * @return The default {@code Category} instance.
     */
    public Category getDefaultCategory() {
        return defaultCategory;
    }

    /**
     * Sets the default category for the graph and returns the current instance of {@code GraphData}.
     *
     * @param defaultCategory The default category to be set. If {@code null}, a default category with the name "Default" will be created and set.
     * @return The current {@code GraphData<N>} instance with the updated default category.
     */
    public GraphData<N> defaultCategory(Category defaultCategory) {
        setDefaultCategory(defaultCategory);
        return this;
    }

    /**
     * Sets the default category for the graph data. If the provided category is null, a new category
     * named "Default" will be assigned.
     *
     * @param defaultCategory The category to set as default. If null, a default category with the name "Default" is created.
     */
    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory == null ? new Category("Default") : defaultCategory;
    }

    /**
     * Retrieves the {@link TextLabel} associated with this instance of the class.
     *
     * @return The {@link TextLabel} object representing the textual label configuration.
     */
    public TextLabel getTextLabel() {
        return textLabel;
    }

    /**
     * Sets the text label associated with this GraphData object.
     *
     * @param textLabel the {@code TextLabel} object to set
     */
    public void setTextLabel(TextLabel textLabel) {
        this.textLabel = textLabel;
    }

    /**
     * Sets the center offset for the graph data.
     *
     * @param center The center position specified as an Offset object containing x and y coordinates.
     */
    public void setCenter(Offset center) {
        this.center = center;
    }

    /**
     * Sets the center offset for the graph and returns the updated graph data instance.
     *
     * @param center The {@link Offset} object representing the x and y center offsets to be applied.
     * @return The current {@link GraphData} instance with the updated center offset.
     */
    public GraphData<N> center(Offset center) {
        setCenter(center);
        return this;
    }

    /**
     * Retrieves the center offset configuration for this object.
     * If the center has not been initialized and the parameter {@code createIfNotExist} is {@code true},
     * a new {@link Offset} instance will be created and assigned.
     *
     * @param createIfNotExist If {@code true} and the center is not already initialized, a new {@link Offset} will be created.
     * @return The current {@link Offset} instance representing the center, or a new one if created.
     */
    public Offset getCenter(boolean createIfNotExist) {
        if(center == null && createIfNotExist) {
            center = new Offset();
        }
        return center;
    }

    /**
     * Sets the layout for the graph.
     *
     * @param layout The layout to set. Possible values are {@code FORCE}, {@code CIRCULAR}, or {@code NONE}.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * Sets the layout type for the graph data and returns the updated instance.
     *
     * @param layout The layout type to be set for the graph. Possible values include {@link Layout#FORCE},
     *               {@link Layout#CIRCULAR}, and {@link Layout#NONE}.
     * @return The updated instance of {@code GraphData<N>} with the specified layout.
     */
    public GraphData<N> layout(Layout layout) {
        this.layout = layout;
        return this;
    }

    /**
     * Retrieves the layout configuration for the graph.
     *
     * @return The current layout configuration of the graph, which determines
     *         the arrangement of elements such as nodes and edges in the graph.
     */
    public Layout getLayout() {
        return this.layout;
    }

    /**
     * Sets whether the graph is in roam mode or not.
     *
     * @param roam A boolean value indicating if roam mode should be enabled (true)
     *             or disabled (false).
     */
    public void setRoam(boolean roam) {
        this.roam = roam;
    }

    /**
     * Sets whether roaming is enabled or disabled for the graph data.
     *
     * @param roam A boolean value indicating if roaming is enabled (true) or disabled (false).
     * @return The current instance of {@code GraphData} for method chaining.
     */
    public GraphData<N> roam(boolean roam) {
        this.roam = roam;
        return this;
    }

    /**
     * Checks whether roaming is enabled.
     *
     * @return {@code true} if roaming is enabled, {@code false} otherwise.
     */
    public boolean isRoam() {
        return this.roam;
    }

    /**
     * Retrieves the instance of the {@code Force} object.
     * If the instance does not exist and {@code createIfNotExist} is {@code true},
     * a new {@code Force} object is created and returned.
     *
     * @param createIfNotExist A {@code boolean} value indicating whether to create
     *                         a new {@code Force} instance if it does not already exist.
     * @return The {@code Force} instance. Returns {@code null} if the instance
     *         does not exist and {@code createIfNotExist} is {@code false}.
     */
    public Force getForce(boolean createIfNotExist) {
        if(force == null && createIfNotExist) {
            force = new Force();
        }
        return force;
    }

    /**
     * Sets the force layout configuration for the graph data.
     *
     * @param force The {@link Force} object containing the configuration parameters of the force layout.
     * @return The current instance of {@code GraphData<N>} for method chaining.
     */
    public GraphData<N> force(Force force) {
        this.force = force;
        return this;
    }

    /**
     * Sets the {@code Force} configuration for the graph layout.
     *
     * @param force the {@code Force} instance containing the configuration settings for the graph's physical simulation.
     */
    public void setForce(Force force) {
        this.force = force;
    }

    /**
     * Sets whether the curveness of edges in the graph should be automatically determined.
     *
     * @param autoCurveness A boolean value indicating whether auto-curveness is enabled or not.
     *                      If true, the curveness of edges will be automatically adjusted;
     *                      if false, no automatic adjustment will be applied.
     */
    public void setAutoCurveness(boolean autoCurveness) {
        this.autoCurveness = autoCurveness;
    }

    /**
     * Sets whether automatic curveness is enabled or not and returns the current instance for method chaining.
     *
     * @param autoCurveness true to enable automatic curveness, false to disable it.
     * @return The current instance of {@code GraphData<N>}.
     */
    public GraphData<N> autoCurveness(boolean autoCurveness) {
        this.autoCurveness = autoCurveness;
        return this;
    }

    /**
     * Determines whether the auto-curveness feature is enabled.
     *
     * @return true if auto-curveness is enabled, false otherwise.
     */
    public boolean isAutoCurveness() {
        return this.autoCurveness;
    }

    /**
     * Sets whether the graph is draggable or not.
     *
     * @param draggable true to enable dragging, false to disable it.
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    /**
     * Sets the draggable property of the GraphData object.
     *
     * @param draggable determines whether the graph data elements are draggable.
     *                  If true, the elements can be dragged; otherwise, they cannot.
     * @return the updated GraphData object with the modified draggable property.
     */
    public GraphData<N> draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    /**
     * Checks whether the object is draggable or not.
     *
     * @return true if the object is draggable, false otherwise
     */
    public boolean isDraggable() {
        return this.draggable;
    }

    /**
     * Returns the symbol representing the start of an edge in the graph.
     *
     * @return the symbol representing the start of an edge as a PointSymbol object
     */
    public final PointSymbol getEdgeStartSymbol() {
        return this.edgeStartSymbol;
    }

    /**
     * Sets the start symbol for edges in the graph. If the provided symbol is null,
     * a default symbol of type NONE will be used.
     *
     * @param symbol the PointSymbol to set as the start symbol for edges.
     *               If null, a default symbol with type NONE is used.
     * @return the current {@code GraphData<N>} instance for method chaining.
     */
    public GraphData<N> edgeStartSymbol(PointSymbol symbol) {
        this.edgeStartSymbol = symbol == null ? new PointSymbol(PointSymbolType.NONE) : symbol;
        return this;
    }

    /**
     * Retrieves the symbol representing the end of an edge in the graph.
     *
     * @return the {@link PointSymbol} object representing the edge's end symbol.
     */
    public final PointSymbol getEdgeEndSymbol() {
        return this.edgeEndSymbol;
    }

    /**
     * Sets the symbol for the end of edges in the graph and returns the updated GraphData instance.
     * If the provided symbol is null, a default point symbol with type NONE is used.
     *
     * @param symbol the PointSymbol to set for the edge end; if null, a default symbol with type NONE is used
     * @return the updated GraphData instance
     */
    public GraphData<N> edgeEndSymbol(PointSymbol symbol) {
        this.edgeEndSymbol = symbol == null ? new PointSymbol(PointSymbolType.NONE) : symbol;
        return this;
    }

    /**
     * Sets the symbol used to represent the end of an edge in the graph.
     * If the provided symbol is null, it defaults to a symbol of type {@code PointSymbolType.NONE}.
     *
     * @param symbol the {@code PointSymbol} to set as the edge end symbol, or null to reset to the default symbol
     */
    public void setEdgeEndSymbol(PointSymbol symbol) {
        this.edgeEndSymbol = symbol == null ? new PointSymbol(PointSymbolType.NONE) : symbol;
    }

    /**
     * Adds one or more nodes to the list of nodes if they are not null.
     *
     * @param nodes the nodes to be added. Each node is checked for null before being added to the list.
     */
    @SafeVarargs
    public final void addNode(N... nodes) {
        if(nodes != null) {
            for(N node: nodes) {
                if(node != null) {
                    this.nodes.add(node);
                }
            }
        }
    }

    /**
     * Adds one or more nodes to the graph and returns the updated graph data.
     *
     * @param nodes the nodes to be added to the graph
     * @return the updated graph data instance after adding the nodes
     */
    @SafeVarargs
    public final GraphData<N> node(N... nodes) {
        addNode(nodes);
        return this;
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

    /**
     * Encodes the current object data into a JSON format and appends it to the given
     * {@code StringBuilder}. The method processes various object-specific properties
     * such as labels, layouts, connectivity, and other configurations to construct
     * the JSON structure.
     *
     * @param sb the {@code StringBuilder} to which the generated JSON representation
     *           will be appended
     */
    void encodeJSON(StringBuilder sb) {
        nodes.forEach(n -> indexOf(n.category)); // Ensure categories are added.
        if(textLabel != null) {
            ComponentPart.addComma(sb);
            sb.append("\"label\":{");
            textLabel.encodeJSON(sb);
            sb.append("}");
        }
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
        String s = edgeStartSymbol.size;
        if(s != null && !s.isEmpty()) {
            sb.append(",\"edgeSymbolSize\":").append(edgeStartSymbol.size);
        }
        if(force != null) {
            sb.append(",\"force\":");
            force.encodeJSON(sb);
        }
        AtomicBoolean first = new AtomicBoolean(true);
        sb.append(",\"categories\":[");
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

    /**
     * Establishes a connection between a source node and a target node.
     *
     * @param source the source node to connect from
     * @param target the target node to connect to
     * @return the created Edge representing the connection between the source and target nodes
     */
    public final Edge connect(N source, N target) {
        return source.connectTo(target);
    }

    /**
     * Connects the last node in the current structure to the specified target node.
     *
     * @param target the target node to connect to the last node
     * @return the edge created between the last node and the target node, or null if there is no last node
     */
    public final Edge connectFromLastNode(N target) {
        N last = getLastNode();
        return last == null ? null : last.connectTo(target);
    }

    /**
     * Connects the given source node to the last node in the graph.
     *
     * @param source the source node to be connected to the last node
     * @return the edge created by connecting the source node to the last node,
     *         or null if there is no last node available
     */
    public final Edge connectToLastNode(N source) {
        N last = getLastNode();
        return last == null ? null : source.connectTo(last);
    }

    /**
     * Retrieves the last node in the list.
     *
     * @return the last node if the list is not empty, or null if the list is empty
     */
    public final N getLastNode() {
        if(nodes.isEmpty()) {
            return null;
        }
        return nodes.getLast();
    }

    /**
     * Retrieves the first node from the list of nodes.
     *
     * @return the first node if the list is not empty; otherwise, returns null
     */
    public final N getFirstNode() {
        if(nodes.isEmpty()) {
            return null;
        }
        return nodes.getFirst();
    }

    /**
     * Retrieves the node at the specified index from the collection of nodes.
     *
     * @param index the index of the node to retrieve, must be within the bounds of the nodes collection
     * @return the node at the specified index if the index is valid; otherwise, returns null
     */
    public final N getNode(int index) {
        if(index < 0 || index >= nodes.size()) {
            return null;
        }
        return nodes.get(index);
    }

    /**
     * Retrieves the node with the specified name from the collection of nodes.
     *
     * @param name the name of the node to retrieve; must not be null
     * @return the node with the specified name if found, otherwise null
     */
    public final N getNode(String name) {
        return nodes.stream().filter(n -> n.getName() != null && n.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Returns the total number of nodes present.
     *
     * @return the count of nodes as an integer
     */
    public final int nodeCount() {
        return nodes.size();
    }

    /**
     * Provides a sequential Stream of nodes contained in the current collection.
     *
     * @return a Stream containing all nodes in the collection
     */
    public final Stream<N> nodes() {
        return nodes.stream();
    }

    private class InternalNodeData extends BasicInternalDataProvider<N> {

        @Override
        public Stream<N> stream() {
            return nodes();
        }

        @Override
        public void encode(StringBuilder sb, Node value) {
            value.encodeJSON(GraphData.this, sb);
        }
    }

    private class NodeData extends BasicDataProvider<N> {

        @Override
        public Stream<N> stream() {
            return nodes();
        }

        @Override
        public void encode(StringBuilder sb, Node value) {
            value.encodeJSON(GraphData.this, sb);
        }
    }

    /**
     * The abstract class Node serves as the base class for components within a graph structure.
     * It represents a single node entity that can be connected to other nodes via edges and contains
     * functionality to manage its own metadata and relationships.
     * This class ensures each node has a unique identifier, a name, a category, and a list of edges.
     * Nodes can be connected to other nodes and can serialize themselves into JSON format.
     *
     * @author Syam
     */
    public static abstract class Node implements ComponentPart {

        private final long id = ID.newID();
        protected String name;
        private final List<Edge> edges = new ArrayList<>();
        Category category;

        /**
         * Retrieves the category associated with this node.
         *
         * @return The category of this node.
         */
        public Category getCategory() {
            return category;
        }

        /**
         * Sets the category associated with this node.
         *
         * @param category The category to be associated with this node.
         */
        public void setCategory(Category category) {
            this.category = category;
        }

        /**
         * Connects the current node to the specified target node by creating an edge between them.
         * If an edge already exists between the current node and the target node, it is returned.
         * Otherwise, a new edge is created, added to the current node's list of edges, and returned.
         *
         * @param target The target node to which a connection is to be established. May not be null.
         * @return The {@code Edge} object representing the connection to the target node,
         *         or {@code null} if the target node is null.
         */
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

        /**
         * Retrieves the name of the node.
         *
         * @return the name of the node as a String.
         */
        @Override
        public final String getName() {
            return name;
        }

        /**
         * Sets the name of this node.
         *
         * @param name The name to be set for this node.
         */
        @Override
        public void setName(String name) {
            this.name = name;
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

        /**
         * Streams the edges associated with this node as a {@code Stream}.
         *
         * @return A {@code Stream} of {@code Edge} objects representing the edges of this node.
         */
        public final Stream<Edge> streamEdges() {
            return edges.stream();
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            ComponentPart.encode(sb, "id", "" + id);
            if(name != null && !name.isEmpty()) {
                ComponentPart.encode(sb, "name", name);
            }
        }

        /**
         * Encodes the node's data in JSON format and appends it to the given {@code StringBuilder}.
         *
         * @param chart The {@code GraphData} containing the chart to which this node belongs.
         * @param sb The {@code StringBuilder} to which the JSON representation of this node will be appended.
         */
        void encodeJSON(GraphData<?> chart, StringBuilder sb) {
            sb.append("{");
            encodeJSON(sb);
            ComponentPart.encode(sb, "category", chart.indexOf(this.category));
            sb.append("}");
        }
    }

    /**
     * A concrete implementation of the {@link Node} class representing a node in a 2D space.
     * This class extends the base functionality of {@link Node} to include x and y coordinates
     * for spatial positioning and provides methods to manipulate and retrieve these coordinates.
     *
     * @author Syam
     */
    public static class XYNode extends Node {

        private double x, y;

        /**
         * Constructs an {@code XYNode} with the specified name. The node is initialized
         * with {@code null} as its category.
         *
         * @param name the name of the node
         */
        public XYNode(String name) {
            this(name, null);
        }

        /**
         * Constructs an {@code XYNode} with the specified name and category.
         * The x and y coordinates of the node are initialized to 0.
         *
         * @param name     The name of the node.
         * @param category The category of the node. It can be null.
         */
        public XYNode(String name, Category category) {
            this(name, 0, 0, category);
        }

        /**
         * Creates an {@code XYNode} instance with the specified name, x and y coordinates.
         *
         * @param name The name of the node.
         * @param x The x-coordinate of the node.
         * @param y The y-coordinate of the node.
         */
        public XYNode(String name, double x, double y) {
            this(name, x, y, null);
        }

        /**
         * Constructs an instance of XYNode with the given name, x and y coordinates,
         * and an optional category.
         *
         * @param name The name of the node.
         * @param x The x-coordinate of the node's position in 2D space.
         * @param y The y-coordinate of the node's position in 2D space.
         * @param category The category associated with this node. Can be null if no category is specified.
         */
        public XYNode(String name, double x, double y, Category category) {
            this.name = name;
            this.x = x;
            this.y = y;
            setCategory(category);
        }

        /**
         * Retrieves the x-coordinate of this node.
         *
         * @return The x-coordinate as a double value.
         */
        public double getX() {
            return this.x;
        }

        /**
         * Sets the x-coordinate of this node and returns the current instance.
         *
         * @param x The x-coordinate to set.
         * @return The current instance of the node with the updated x-coordinate.
         */
        public Node x(double x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the x-coordinate for the node.
         *
         * @param x The x-coordinate to set.
         */
        public void setX(double x) {
            this.x = x;
        }

        /**
         * Retrieves the Y-coordinate value of this node.
         *
         * @return the Y-coordinate as a double.
         */
        public double getY() {
            return this.y;
        }

        /**
         * Sets the y-coordinate value for the node and returns the node instance.
         *
         * @param y The y-coordinate value to set.
         * @return The updated instance of the node with the new y-coordinate value.
         */
        public Node y(double y) {
            this.y = y;
            return this;
        }

        /**
         * Sets the y-coordinate value.
         *
         * @param y The y-coordinate to set.
         */
        public void setY(double y) {
            this.y = y;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "x", x);
            ComponentPart.encode(sb, "y", y);
        }
    }

    /**
     * The {@code ValueNode} class represents a type of {@code Node} that holds a numerical value.
     * It extends the base functionality of the {@code Node} class by adding a specific attribute
     * to store and manage a numerical value. This value can be encoded into a JSON format when
     * serializing the node.
     * This class is useful for scenarios where nodes in a graph have associated numerical properties
     * that need to be dynamically updated or serialized as part of its data.
     *
     * @author Syam
     */
    public static class ValueNode extends Node {

        private Number value;

        /**
         * Constructs a {@code ValueNode} instance with the specified numerical value.
         *
         * @param value Numerical value to be associated with this node.
         */
        public ValueNode(Number value) {
            this.value = value;
        }

        /**
         * Returns the numerical value held by this node.
         *
         * @return The numerical value as a {@code Number}.
         */
        public Number getValue() {
            return value;
        }

        /**
         * Sets the numerical value for this node.
         *
         * @param value The numeric value to set. It can be of any type that extends the {@link Number} class.
         */
        public void setValue(Number value) {
            this.value = value;
        }

        /**
         * Sets the numerical value for the {@code ValueNode}.
         * The provided value is assigned to the node and this method returns the current instance
         * to allow method chaining.
         *
         * @param value The numerical value to be assigned to the node.
         * @return The current instance of {@code ValueNode} with the updated value.
         */
        public ValueNode value(Number value) {
            this.value = value;
            return this;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "value", value);
        }
    }

    /**
     * Represents a specific category for a chart or graphical element, extending the functionality
     * of {@code PointSymbol} to include a category name. This class can encode the category information
     * into JSON format, including properties inherited from {@code PointSymbol}.
     *
     * @author Syam
     */
    public static class Category extends PointSymbol {

        private final String name;

        /**
         * Constructs a new {@code Category} with the specified name.
         *
         * @param name The name of the category.
         */
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

    /**
     * Represents a directional edge between two nodes in a graph structure. Inherits
     * styling properties from the {@code LineStyle} class, providing additional encoded
     * JSON representation that incorporates unique edge properties such as the edge's ID,
     * source node, and destination node.
     *
     * @author Syam
     */
    public static class Edge extends LineStyle {

        private final long id = ID.newID();
        private final Node sourceNode, destinationNode;

        private Edge(Node sourceNode, Node destinationNode) {
            this.sourceNode = sourceNode;
            this.destinationNode = destinationNode;
        }

        /**
         * Establishes a connection from the current destination node of this {@code Edge}
         * to the specified target node by creating or retrieving an edge between them.
         *
         * @param target The target {@code Node} to which a connection is to be made.
         *               May not be null.
         * @return The {@code Edge} object representing the connection to the target node,
         *         or {@code null} if the target node is null.
         */
        public Edge connectTo(Node target) {
            return destinationNode.connectTo(target);
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

        /**
         * Retrieves the source node of this edge.
         *
         * @return The source {@code Node} associated with this edge.
         */
        public Node getSourceNode() {
            return this.sourceNode;
        }

        /**
         * Retrieves the destination node of this edge.
         *
         * @return The {@code Node} instance that represents the destination of this edge.
         */
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

    /**
     * The Layout enum defines various layout options that can be used for organizational
     * or structural representation. Each layout type specifies a different arrangement model.
     * Enum Constants:
     * - FORCE: Represents a force-directed layout, commonly used for graphs or networks.
     * - CIRCULAR: Represents a circular layout where elements are arranged in a circular fashion.
     * - NONE: Represents the absence of any specific layout.
     * The `toString` method is overridden to provide a string representation of the layout
     * type in the lowercase and enclosed in quotation marks.
     *
     * @author Syam
     */
    public enum Layout {

        FORCE,
        CIRCULAR,
        NONE;

        @Override
        public String toString() {
            return "\"" + super.toString().toLowerCase() + "\"";
        }
    }

    /**
     * Represents a force-directed layout configuration as a property of a chart component.
     * This class defines various attributes that influence the behavior and appearance
     * of the force-directed layout, such as repulsion, gravity, friction, edge length, and
     * the initial layout configuration.
     *
     * @author Syam
     */
    public static class Force implements ComponentProperty {

        private Layout initialLayout = null;
        private int repulsion = 50;
        private double gravity = 0.1;
        private double friction = 0.6;
        private double edgeLength = 30;
        private boolean layoutAnimation = true;

        /**
         * Retrieves the initial layout configuration.
         *
         * @return The initial {@code Layout} configuration, which defines the structural arrangement.
         */
        public Layout getInitialLayout() {
            return initialLayout;
        }

        /**
         * Sets the initial layout for the {@code Force} instance.
         *
         * @param initialLayout the layout to be used as the initial layout. This specifies
         *                      the arrangement model to apply at the start.
         * @return the current {@code Force} instance for method chaining.
         */
        public Force initialLayout(Layout initialLayout) {
            this.initialLayout = initialLayout;
            return this;
        }

        /**
         * Sets the initial layout of the force-directed graph.
         *
         * @param initialLayout The layout to be set as the initial layout.
         *                      It specifies the arrangement model such as FORCE, CIRCULAR, or NONE.
         */
        public void setInitialLayout(Layout initialLayout) {
            this.initialLayout = initialLayout;
        }

        /**
         * Retrieves the repulsion value.
         *
         * @return The value of the repulsion parameter.
         */
        public int getRepulsion() {
            return repulsion;
        }

        /**
         * Sets the repulsion value for the force configuration.
         *
         * @param repulsion The repulsion value to set.
         * @return The current instance of the {@code Force} object.
         */
        public Force repulsion(int repulsion) {
            this.repulsion = repulsion;
            return this;
        }

        /**
         * Sets the repulsion value.
         *
         * @param repulsion the repulsion value to be set, typically used to control the spacing
         *                  or force applied between nodes in a layout.
         */
        public void setRepulsion(int repulsion) {
            this.repulsion = repulsion;
        }

        /**
         * Retrieves the gravity value.
         *
         * @return The current gravity value as a double.
         */
        public double getGravity() {
            return gravity;
        }

        /**
         * Sets the gravity parameter and returns the current {@link Force} object.
         *
         * @param gravity The gravity value to be set.
         * @return The current {@link Force} object with the updated gravity value.
         */
        public Force gravity(double gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Sets the value for the gravitational force to be applied.
         *
         * @param gravity The gravitational force value to be set.
         */
        public void setGravity(double gravity) {
            this.gravity = gravity;
        }

        /**
         * Retrieves the friction value.
         *
         * @return The current friction value.
         */
        public double getFriction() {
            return friction;
        }

        /**
         * Sets the friction value for this force instance.
         *
         * @param friction The friction value to set. It represents the resistance force that opposes motion.
         * @return This {@code Force} instance for method chaining.
         */
        public Force friction(double friction) {
            this.friction = friction;
            return this;
        }

        /**
         * Sets the friction value for the force calculations.
         *
         * @param friction The friction coefficient to be set. This value typically affects the
         *                 rate of deceleration or resistance within the force model.
         */
        public void setFriction(double friction) {
            this.friction = friction;
        }

        /**
         * Retrieves the edge length value.
         *
         * @return The current edge length as a double.
         */
        public double getEdgeLength() {
            return edgeLength;
        }

        /**
         * Sets the edge length for the force and returns the current instance of the Force object.
         *
         * @param edgeLength the length of the edges.
         * @return the current instance of the Force object.
         */
        public Force edgeLength(double edgeLength) {
            this.edgeLength = edgeLength;
            return this;
        }

        /**
         * Sets the length of edges in the layout.
         *
         * @param edgeLength the desired length of edges
         */
        public void setEdgeLength(double edgeLength) {
            this.edgeLength = edgeLength;
        }

        /**
         * Determines whether layout animation is enabled or not.
         *
         * @return true if layout animation is enabled, false otherwise.
         */
        public boolean isLayoutAnimation() {
            return layoutAnimation;
        }

        /**
         * Enables or disables layout animation for the force layout.
         *
         * @param layoutAnimation A boolean value indicating whether layout animation should be enabled (true) or disabled (false).
         * @return The current instance of the {@code Force} object for method chaining.
         */
        public Force layoutAnimation(boolean layoutAnimation) {
            this.layoutAnimation = layoutAnimation;
            return this;
        }

        /**
         * Enables or disables the layout animation.
         *
         * @param layoutAnimation Specifies whether the layout animation should be enabled (true) or disabled (false).
         */
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
