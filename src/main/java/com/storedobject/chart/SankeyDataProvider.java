package com.storedobject.chart;

import java.util.stream.Stream;

/**
 * Data provider interface for the {@link SankeyChart}.
 *
 * @author Syam
 */
public interface SankeyDataProvider extends ComponentPart {

    /**
     * Get the nodes. The stream should not contain duplicate node names.
     *
     * @return Nodes as a stream.
     */
    Stream<SankeyChart.Node> getNodes();

    /**
     * Get the edges. The stream should not contain edges with circular references.
     *
     * @return Edges as a stream.
     */
    Stream<SankeyChart.Edge> getEdges();
}
