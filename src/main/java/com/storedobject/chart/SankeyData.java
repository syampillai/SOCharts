package com.storedobject.chart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A concrete implementation of {@link SankeyDataProvider} as an {@link ArrayList} of {@link Node}s. It detects
 * duplicate {@link Node}s and circular references in {@link Edge}s.
 *
 * @author Syam
 */
public class SankeyData extends AbstractData<SankeyChart.Node> implements SankeyDataProvider {

    private final List<SankeyChart.Edge> edges = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param nodes Nodes to be added.
     */
    public SankeyData(SankeyChart.Node... nodes) {
        super(DataType.OBJECT, nodes);
    }

    /**
     * Add an {@link Edge}.
     *
     * @param edge {@link Edge} to be added. If the {@link Edge} contains {@link Node}s that are not part of this, it
     *                         will be automatically added.
     */
    public void addEdge(SankeyChart.Edge edge) {
        SankeyChart.Node from = edge.getFrom(), to = edge.getTo();
        if(from == null || to == null) {
            edges.add(edge);
            return;
        }
        if(stream().noneMatch(n -> n.getName().equals(from.getName()))) {
            add(from);
        }
        if(stream().noneMatch(n -> n.getName().equals(to.getName()))) {
            add(to);
        }
        edges.add(edge);
    }

    @Override
    public Stream<SankeyChart.Node> getNodes() {
        return stream();
    }

    @Override
    public Stream<SankeyChart.Edge> getEdges() {
        return edges.stream();
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        int i, j;
        String name;
        for(i = 0; i < size(); i++) {
            name = get(i).getName();
            for(j = i + 1; j < size(); j++) {
                if(get(j).getName().equals(name)) {
                    throw new ChartException("Duplicate node name - " + name);
                }
            }
        }
        for(SankeyChart.Edge e: edges) {
            e.validate();
        }
        if(edges.isEmpty()) {
            return;
        }
        if(hasCircularReference()) {
            throw new ChartException("Circular edge detected");
        }
    }

    private boolean hasCircularReference() {
        Set<SankeyChart.Node> visitedNodes = new HashSet<>();
        for (SankeyChart.Edge edge : edges) {
            SankeyChart.Node fromNode = edge.getFrom();
            if (!visitedNodes.contains(fromNode)) {
                if (hasCycle(fromNode, visitedNodes, new HashSet<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycle(SankeyChart.Node node, Set<SankeyChart.Node> visited, Set<SankeyChart.Node> path) {
        visited.add(node);
        path.add(node);
        for (SankeyChart.Edge edge : edges) {
            if (edge.getFrom() == node) {
                SankeyChart.Node toNode = edge.getTo();
                if (!visited.contains(toNode)) {
                    if (hasCycle(toNode, visited, path)) {
                        return true;
                    }
                } else if (path.contains(toNode)) {
                    return true;
                }
            }
        }
        path.remove(node);
        return false;
    }
}
