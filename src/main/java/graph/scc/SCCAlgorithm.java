package graph.scc;

import graph.core.*;
import java.util.*;

public class SCCAlgorithm extends BaseMetrics {
    private int index = 0;
    private final Stack<String> stack = new Stack<>();
    private final Map<String, Integer> indices = new HashMap<>();
    private final Map<String, Integer> lowLinks = new HashMap<>();
    private final Set<String> onStack = new HashSet<>();
    private final List<List<String>> components = new ArrayList<>();
    private Graph graph;

    public SCCResult findSCC(Graph graph) {
        reset();
        this.graph = graph;
        components.clear();
        indices.clear();
        lowLinks.clear();
        onStack.clear();
        stack.clear();
        index = 0;

        for (String node : graph.getNodes()) {
            incrementOperations(1);
            if (!indices.containsKey(node)) {
                strongConnect(node);
            }
        }

        return new SCCResult(components, getExecutionTimeMs(), getOperationsCount());
    }

    private void strongConnect(String node) {
        indices.put(node, index);
        lowLinks.put(node, index);
        index++;
        stack.push(node);
        onStack.add(node);
        incrementOperations(4);

        for (Graph.Edge edge : graph.getEdgesFrom(node)) {
            incrementOperations(1);
            String neighbor = edge.to;

            if (!indices.containsKey(neighbor)) {
                strongConnect(neighbor);
                lowLinks.put(node, Math.min(lowLinks.get(node), lowLinks.get(neighbor)));
                incrementOperations(2);
            } else if (onStack.contains(neighbor)) {
                lowLinks.put(node, Math.min(lowLinks.get(node), indices.get(neighbor)));
                incrementOperations(2);
            }
        }

        if (lowLinks.get(node).equals(indices.get(node))) {
            List<String> component = new ArrayList<>();
            String popNode;
            do {
                popNode = stack.pop();
                onStack.remove(popNode);
                component.add(popNode);
                incrementOperations(3);
            } while (!popNode.equals(node));
            components.add(component);
        }
    }

    public static class SCCResult {
        public final List<List<String>> components;
        public final double executionTimeMs;
        public final long operationsCount;

        public SCCResult(List<List<String>> components, double executionTimeMs, long operationsCount) {
            this.components = components;
            this.executionTimeMs = executionTimeMs;
            this.operationsCount = operationsCount;
        }
    }
}