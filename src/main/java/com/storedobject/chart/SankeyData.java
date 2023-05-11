package com.storedobject.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Concrete implementation of {@link SankeyDataProvider} as an {@link ArrayList} of {@link Node}s.
 *
 * @author Syam
 */
public class SankeyData extends AbstractData<SankeyDataProvider.Node> implements SankeyDataProvider {

    private final List<Edge> edges = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param nodes Nodes to be added.
     */
    public SankeyData(SankeyDataProvider.Node... nodes) {
        super(DataType.OBJECT, nodes);
    }

    /**
     * Add an {@link Edge}.
     *
     * @param edge {@link Edge} to be added. If the {@link Edge} contains {@link Node}s that are not part of this, it
     *                         will be automatically added.
     */
    public void addEdge(SankeyDataProvider.Edge edge) {
        SankeyDataProvider.Node from = edge.getFrom(), to = edge.getTo();
        if(stream().noneMatch(n -> n.getName().equals(from.getName()))) {
            add(from);
        }
        if(stream().noneMatch(n -> n.getName().equals(to.getName()))) {
            add(to);
        }
        edges.add(edge);
    }

    @Override
    public Stream<Node> getNodes() {
        return stream();
    }

    @Override
    public Stream<Edge> getEdges() {
        return edges.stream();
    }
}
