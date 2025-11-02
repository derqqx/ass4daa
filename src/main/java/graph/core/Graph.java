package graph.core;
import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();
    private final Map<String, Integer> nodeDurations = new HashMap<>();

    public static class Edge {
        public final String from;
        public final String to;
        public final int weight;

        public Edge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    public void addNode(String node, int duration) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        nodeDurations.put(node, duration);
    }
    public void addEdge(String from, String to, int weight) {
        adjacencyList.get(from).add(new Edge(from, to, weight));
    }
    public Set<String> getNodes() {
        return adjacencyList.keySet();
    }
    public List<Edge> getEdges() {
        List<Edge> allEdges = new ArrayList<>();
        for (List<Edge> edges : adjacencyList.values()) {
            allEdges.addAll(edges);
        }
        return allEdges;
    }
    public List<Edge> getEdgesFrom(String node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }
    public int getNodeDuration(String node) {
        return nodeDurations.getOrDefault(node, 0);
    }

}