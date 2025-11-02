import graph.core.Graph;
import graph.scc.SCCAlgorithm;
import graph.scc.CondensationGraph;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        new File("results").mkdirs();
        String inputFile = "data/dataset_2_small_pure_dag.json";
        String outputFile = "results/output.json";

        try {
            Graph graph = readGraphFromFile(inputFile);
            System.out.println("Loaded graph with " + graph.getNodes().size() + " nodes");

            // находим скк
            System.out.println("Finding Strongly Connected Components...");
            SCCAlgorithm sccAlgo = new SCCAlgorithm();
            var sccResult = sccAlgo.findSCC(graph);

            System.out.println("Found " + sccResult.components.size() + " SCCs");
            for (int i = 0; i < sccResult.components.size(); i++) {
                System.out.println("Component " + i + ": " + sccResult.components.get(i));
            }

            // строим график конденсации
            System.out.println("Building condensation graph...");
            CondensationGraph condGraph = new CondensationGraph(sccResult.components, graph);

            // топологическая сортировка на графе конденсации
            System.out.println("Performing topological sort...");
            TopologicalSort topoAlgo = new TopologicalSort();
            var topoResult = topoAlgo.kahnTopologicalSort(condGraph);
            System.out.println("Topological order: " + topoResult.order);

            // самые короткий и длинные пути
            System.out.println("Finding critical path...");
            DAGShortestPath spAlgo = new DAGShortestPath();
            var criticalPathResult = spAlgo.findCriticalPath(condGraph);

            System.out.println("Critical path length: " + criticalPathResult.length);
            System.out.println("Critical path: " + criticalPathResult.path);

            // сохраняем результаты
            saveResults(sccResult, topoResult, criticalPathResult, outputFile);
            System.out.println("Results saved to " + outputFile);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static Graph readGraphFromFile(String filename) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filename)) {
            JsonGraphData data = gson.fromJson(reader, JsonGraphData.class);
            Graph graph = new Graph();

            for (JsonNode node : data.nodes) {
                graph.addNode(node.id, node.duration);
            }

            for (JsonEdge edge : data.edges) {
                graph.addEdge(edge.from, edge.to, edge.weight);
            }
            return graph;
        }
    }

    private static void saveResults(SCCAlgorithm.SCCResult sccResult,
                                    TopologicalSort.TopoResult topoResult,
                                    DAGShortestPath.CriticalPathResult criticalPathResult,
                                    String filename) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> results = new HashMap<>();
        results.put("scc_components", sccResult.components);
        results.put("scc_execution_time_ms", sccResult.executionTimeMs);
        results.put("scc_operations", sccResult.operationsCount);

        results.put("topological_order", topoResult.order);
        results.put("topo_execution_time_ms", topoResult.executionTimeMs);
        results.put("topo_operations", topoResult.operationsCount);

        results.put("critical_path", criticalPathResult.path);
        results.put("critical_path_length", criticalPathResult.length);
        results.put("critical_path_execution_time_ms", criticalPathResult.executionTimeMs);
        results.put("critical_path_operations", criticalPathResult.operationsCount);

        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(results, writer);
        }
    }
    static class JsonGraphData {
        List<JsonNode> nodes;
        List<JsonEdge> edges;
    }

    static class JsonNode {
        String id;
        int duration;
    }

    static class JsonEdge {
        String from;
        String to;
        int weight;
    }
}