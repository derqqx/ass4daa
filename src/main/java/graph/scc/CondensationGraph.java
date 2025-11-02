package graph.scc;

import graph.core.Graph;
import java.util.*;

public class CondensationGraph extends Graph {
    private final Map<String, Integer> nodeToComponent = new HashMap<>();
    private final List<List<String>> components;
    private final Map<Integer, String> componentToNode = new HashMap<>();

    public CondensationGraph(List<List<String>> components, Graph originalGraph) {
        this.components = components;

        // Map each node to its component and create component nodes
        for (int i = 0; i < components.size(); i++) {
            List<String> component = components.get(i);

            // Use the first node of component as representative name
            String componentNodeName = component.get(0);
            componentToNode.put(i, componentNodeName);

            for (String node : component) {
                nodeToComponent.put(node, i);
            }

            // Calculate max duration in component
            int maxDuration = component.stream()
                    .mapToInt(originalGraph::getNodeDuration)
                    .max().orElse(0);
            addNode(componentNodeName, maxDuration);
        }

        // Create condensation graph edges (between component representatives)
        Set<String> addedEdges = new HashSet<>();
        for (Graph.Edge edge : originalGraph.getEdges()) {
            int fromComp = nodeToComponent.get(edge.from);
            int toComp = nodeToComponent.get(edge.to);

            if (fromComp != toComp) {
                String fromNode = componentToNode.get(fromComp);
                String toNode = componentToNode.get(toComp);
                String edgeKey = fromNode + "->" + toNode;

                if (!addedEdges.contains(edgeKey)) {
                    addEdge(fromNode, toNode, edge.weight);
                    addedEdges.add(edgeKey);
                }
            }
        }
    }

    public List<List<String>> getComponents() {
        return components;
    }

    public int getComponentIndex(String node) {
        return nodeToComponent.get(node);
    }

    public String getComponentNodeName(int componentIndex) {
        return componentToNode.get(componentIndex);
    }
}