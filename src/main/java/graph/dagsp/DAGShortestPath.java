package graph.dagsp;

import graph.core.*;
import graph.topo.TopologicalSort;
import java.util.*;

public class DAGShortestPath extends BaseMetrics {
    public ShortestPathResult findShortestPath(Graph dag, String source) {
        reset();

        TopologicalSort topoSort = new TopologicalSort();
        var topoResult = topoSort.kahnTopologicalSort(dag);
        incrementOperations((int) topoResult.operationsCount);

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        for (String node : dag.getNodes()) {
            dist.put(node, Integer.MAX_VALUE);
            incrementOperations(1);
        }
        dist.put(source, 0);

        for (String node : topoResult.order) {
            incrementOperations(1);

            if (dist.get(node) != Integer.MAX_VALUE) {
                for (Graph.Edge edge : dag.getEdgesFrom(node)) {
                    incrementOperations(1);
                    int newDist = dist.get(node) + edge.weight;
                    if (newDist < dist.get(edge.to)) {
                        dist.put(edge.to, newDist);
                        prev.put(edge.to, node);
                        incrementOperations(2);
                    }
                }
            }
        }

        return new ShortestPathResult(dist, prev, getExecutionTimeMs(),
                getOperationsCount() + topoResult.operationsCount);
    }

    public CriticalPathResult findCriticalPath(Graph dag) {
        reset();

        // Use negative weights for longest path
        Graph negatedGraph = new Graph();
        for (String node : dag.getNodes()) {
            negatedGraph.addNode(node, dag.getNodeDuration(node));
        }
        for (Graph.Edge edge : dag.getEdges()) {
            negatedGraph.addEdge(edge.from, edge.to, -edge.weight);
        }

        // Find shortest path with negative weights (longest path in original)
        String source = findSourceNode(dag);
        var result = findShortestPath(negatedGraph, source);

        // Convert back to positive
        Map<String, Integer> longestDist = new HashMap<>();
        for (var entry : result.distances.entrySet()) {
            longestDist.put(entry.getKey(), -entry.getValue());
        }

        // Find critical path
        String sink = findSinkNode(longestDist);
        List<String> criticalPath = reconstructPath(result.predecessors, source, sink);

        return new CriticalPathResult(criticalPath, longestDist.get(sink),
                getExecutionTimeMs(), getOperationsCount());
    }

    private String findSourceNode(Graph graph) {
        Set<String> hasIncoming = new HashSet<>();
        for (Graph.Edge edge : graph.getEdges()) {
            hasIncoming.add(edge.to);
        }
        for (String node : graph.getNodes()) {
            if (!hasIncoming.contains(node)) {
                return node;
            }
        }
        return graph.getNodes().iterator().next();
    }

    private String findSinkNode(Map<String, Integer> dist) {
        return dist.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(dist.keySet().iterator().next());
    }

    private List<String> reconstructPath(Map<String, String> prev, String source, String sink) {
        List<String> path = new ArrayList<>();
        String current = sink;
        while (current != null) {
            path.add(0, current);
            current = prev.get(current);
        }
        return path.get(0).equals(source) ? path : new ArrayList<>();
    }

    public static class ShortestPathResult {
        public final Map<String, Integer> distances;
        public final Map<String, String> predecessors;
        public final double executionTimeMs;
        public final long operationsCount;

        public ShortestPathResult(Map<String, Integer> distances, Map<String, String> predecessors,
                                  double executionTimeMs, long operationsCount) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.executionTimeMs = executionTimeMs;
            this.operationsCount = operationsCount;
        }
    }

    public static class CriticalPathResult {
        public final List<String> path;
        public final int length;
        public final double executionTimeMs;
        public final long operationsCount;

        public CriticalPathResult(List<String> path, int length,
                                  double executionTimeMs, long operationsCount) {
            this.path = path;
            this.length = length;
            this.executionTimeMs = executionTimeMs;
            this.operationsCount = operationsCount;
        }
    }
}