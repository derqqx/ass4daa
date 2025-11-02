package graph.topo;
import graph.core.*;
import java.util.*;

public class TopologicalSort extends BaseMetrics {

    public TopoResult kahnTopologicalSort(Graph graph) {
        reset();

        Map<String, Integer> inDegree = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        List<String> topoOrder = new ArrayList<>();

        // Initialize in-degree
        for (String node : graph.getNodes()) {
            inDegree.put(node, 0);
            incrementOperations(1);
        }

        // Calculate in-degree
        for (Graph.Edge edge : graph.getEdges()) {
            inDegree.put(edge.to, inDegree.get(edge.to) + 1);
            incrementOperations(2);
        }

        // Enqueue nodes with 0 in-degree
        for (String node : graph.getNodes()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
                incrementOperations(1);
            }
            incrementOperations(1);
        }

        // Process queue
        while (!queue.isEmpty()) {
            String current = queue.poll();
            topoOrder.add(current);
            incrementOperations(2);

            for (Graph.Edge edge : graph.getEdgesFrom(current)) {
                String neighbor = edge.to;
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                incrementOperations(2);

                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                    incrementOperations(1);
                }
            }
        }

        return new TopoResult(topoOrder, getExecutionTimeMs(), getOperationsCount());
    }

    public static class TopoResult {
        public final List<String> order;
        public final double executionTimeMs;
        public final long operationsCount;

        public TopoResult(List<String> order, double executionTimeMs, long operationsCount) {
            this.order = order;
            this.executionTimeMs = executionTimeMs;
            this.operationsCount = operationsCount;
        }
    }
}