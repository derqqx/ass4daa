import graph.core.Graph;
import graph.scc.SCCAlgorithm;
import graph.scc.CondensationGraph;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmsTest {
    @Test
    public void testSCCOnSimpleGraph() {
        Graph graph = new Graph();
        graph.addNode("A", 1);
        graph.addNode("B", 2);
        graph.addNode("C", 3);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("C", "A", 1);

        SCCAlgorithm scc = new SCCAlgorithm();
        var result = scc.findSCC(graph);

        assertEquals(1, result.components.size());
        assertTrue(result.components.get(0).containsAll(List.of("A", "B", "C")));
    }
    @Test
    public void testTopologicalSortOnDAG() {
        Graph graph = new Graph();
        graph.addNode("A", 1);
        graph.addNode("B", 2);
        graph.addNode("C", 3);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);

        TopologicalSort topo = new TopologicalSort();
        var result = topo.kahnTopologicalSort(graph);

        assertEquals(3, result.order.size());
        assertEquals("A", result.order.get(0));
    }
    @Test
    public void testCriticalPath() {
        Graph graph = new Graph();
        graph.addNode("A", 5);
        graph.addNode("B", 3);
        graph.addNode("C", 2);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);

        DAGShortestPath sp = new DAGShortestPath();
        var result = sp.findCriticalPath(graph);

        assertTrue(result.length >= 0);
        assertFalse(result.path.isEmpty());
    }
}