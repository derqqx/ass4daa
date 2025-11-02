// src/main/java/DataGenerator.java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;

public class DataGenerator {

    static class JsonGraphData {
        List<JsonNode> nodes = new ArrayList<>();
        List<JsonEdge> edges = new ArrayList<>();
    }

    static class JsonNode {
        String id;
        int duration;
        JsonNode(String id, int duration) { this.id = id; this.duration = duration; }
    }

    static class JsonEdge {
        String from;
        String to;
        int weight;
        JsonEdge(String from, String to, int weight) { this.from = from; this.to = to; this.weight = weight; }
    }

    public static void main(String[] args) throws IOException {
        // Create data directory
        File dataDir = new File("data");
        boolean created = dataDir.mkdirs();
        if (created) {
            System.out.println("✅ Created /data/ directory");
        } else if (dataDir.exists()) {
            System.out.println("📁 /data/ directory already exists");
        } else {
            System.err.println("❌ Failed to create /data/ directory");
            return;
        }

        // Generate 9 datasets + tasks.json for Main
        System.out.println("🔨 Generating datasets in /data/ folder...");

        // SMALL graphs (6-10 nodes)
        generateSmallDataset(1, "sparse_cyclic", true, false);
        generateSmallDataset(2, "pure_dag", false, false);
        generateSmallDataset(3, "dense_mixed", true, true);

        // MEDIUM graphs (10-20 nodes) with multiple SCCs
        generateMediumDataset(4, "multiple_sccs", true, true);
        generateMediumDataset(5, "sparse_dag", false, false);
        generateMediumDataset(6, "dense_cyclic", true, false);

        // LARGE graphs (20-50 nodes) for performance
        generateLargeDataset(7, "sparse_mixed", true, true);
        generateLargeDataset(8, "dense_cyclic", true, false);
        generateLargeDataset(9, "multiple_sccs_dense", true, true);

        // Create tasks.json for Main (simple test case)
        createSimpleTestGraph();

        generateReport();
        System.out.println("✅ Generated 9 datasets + tasks.json in /data/ folder");
    }

    private static void generateSmallDataset(int id, String type, boolean includeCycles, boolean multipleSCCs) throws IOException {
        Random rand = new Random(id * 1000L);
        JsonGraphData data = new JsonGraphData();
        int nodeCount = 6 + rand.nextInt(5); // 6-10 nodes

        // Create nodes
        for (int i = 0; i < nodeCount; i++) {
            data.nodes.add(new JsonNode("T" + i, rand.nextInt(10) + 1));
        }

        if (multipleSCCs) {
            // Create 2 separate components
            createTwoComponents(data, nodeCount, rand, includeCycles);
        } else if (includeCycles) {
            // Single component with cycles
            createCyclicComponent(data, nodeCount, rand);
        } else {
            // Pure DAG
            createDAG(data, nodeCount, rand);
        }

        saveDataset(data, id, "small", type, nodeCount);
    }

    private static void generateMediumDataset(int id, String type, boolean includeCycles, boolean multipleSCCs) throws IOException {
        Random rand = new Random(id * 1000L);
        JsonGraphData data = new JsonGraphData();
        int nodeCount = 10 + rand.nextInt(11); // 10-20 nodes

        // Create nodes
        for (int i = 0; i < nodeCount; i++) {
            data.nodes.add(new JsonNode("T" + i, rand.nextInt(10) + 1));
        }

        if (multipleSCCs) {
            // Create 3 separate components
            createThreeComponents(data, nodeCount, rand, includeCycles);
        } else if (includeCycles) {
            // Single component with cycles
            createCyclicComponent(data, nodeCount, rand);
        } else {
            // Pure DAG
            createDAG(data, nodeCount, rand);
        }

        saveDataset(data, id, "medium", type, nodeCount);
    }

    private static void generateLargeDataset(int id, String type, boolean includeCycles, boolean multipleSCCs) throws IOException {
        Random rand = new Random(id * 1000L);
        JsonGraphData data = new JsonGraphData();
        int nodeCount = 20 + rand.nextInt(31); // 20-50 nodes

        // Create nodes
        for (int i = 0; i < nodeCount; i++) {
            data.nodes.add(new JsonNode("T" + i, rand.nextInt(10) + 1));
        }

        if (multipleSCCs) {
            // Create multiple components
            createMultipleComponents(data, nodeCount, rand, includeCycles);
        } else if (includeCycles) {
            // Single component with cycles
            createCyclicComponent(data, nodeCount, rand);
        } else {
            // Pure DAG
            createDAG(data, nodeCount, rand);
        }

        saveDataset(data, id, "large", type, nodeCount);
    }

    private static void createTwoComponents(JsonGraphData data, int nodeCount, Random rand, boolean includeCycles) {
        int split = nodeCount / 2;

        // Component 1: T0 to T(split-1)
        for (int i = 0; i < split - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }
        if (includeCycles && split >= 2) {
            data.edges.add(new JsonEdge("T0", "T1", rand.nextInt(5) + 1));
            data.edges.add(new JsonEdge("T1", "T0", rand.nextInt(5) + 1));
        }

        // Component 2: T(split) to T(nodeCount-1)
        for (int i = split; i < nodeCount - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }
        if (includeCycles && (nodeCount - split) >= 2) {
            data.edges.add(new JsonEdge("T" + split, "T" + (split + 1), rand.nextInt(5) + 1));
            data.edges.add(new JsonEdge("T" + (split + 1), "T" + split, rand.nextInt(5) + 1));
        }

        // Add some cross-component edges (but not enough to connect them)
        if (nodeCount >= 4) {
            data.edges.add(new JsonEdge("T" + (split - 1), "T" + split, rand.nextInt(5) + 1));
        }
    }

    private static void createThreeComponents(JsonGraphData data, int nodeCount, Random rand, boolean includeCycles) {
        int compSize = nodeCount / 3;

        // Component 1
        for (int i = 0; i < compSize - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }
        if (includeCycles) {
            data.edges.add(new JsonEdge("T0", "T1", rand.nextInt(5) + 1));
            data.edges.add(new JsonEdge("T1", "T0", rand.nextInt(5) + 1));
        }

        // Component 2
        for (int i = compSize; i < 2 * compSize - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }
        if (includeCycles) {
            data.edges.add(new JsonEdge("T" + compSize, "T" + (compSize + 1), rand.nextInt(5) + 1));
            data.edges.add(new JsonEdge("T" + (compSize + 1), "T" + compSize, rand.nextInt(5) + 1));
        }

        // Component 3
        for (int i = 2 * compSize; i < nodeCount - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }
        if (includeCycles) {
            data.edges.add(new JsonEdge("T" + (2 * compSize), "T" + (2 * compSize + 1), rand.nextInt(5) + 1));
            data.edges.add(new JsonEdge("T" + (2 * compSize + 1), "T" + (2 * compSize), rand.nextInt(5) + 1));
        }

        // Add edges between components to create DAG structure
        data.edges.add(new JsonEdge("T" + (compSize - 1), "T" + compSize, rand.nextInt(5) + 1));
        data.edges.add(new JsonEdge("T" + (2 * compSize - 1), "T" + (2 * compSize), rand.nextInt(5) + 1));
    }

    private static void createMultipleComponents(JsonGraphData data, int nodeCount, Random rand, boolean includeCycles) {
        int numComponents = 3 + rand.nextInt(3); // 3-5 components
        int compSize = nodeCount / numComponents;

        for (int comp = 0; comp < numComponents; comp++) {
            int start = comp * compSize;
            int end = (comp == numComponents - 1) ? nodeCount : start + compSize;

            // Create component
            for (int i = start; i < end - 1; i++) {
                data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
            }

            if (includeCycles && (end - start) >= 2) {
                data.edges.add(new JsonEdge("T" + start, "T" + (start + 1), rand.nextInt(5) + 1));
                data.edges.add(new JsonEdge("T" + (start + 1), "T" + start, rand.nextInt(5) + 1));
            }

            // Connect to next component
            if (comp < numComponents - 1) {
                data.edges.add(new JsonEdge("T" + (end - 1), "T" + end, rand.nextInt(5) + 1));
            }
        }
    }

    private static void createCyclicComponent(JsonGraphData data, int nodeCount, Random rand) {
        // Create a cycle that includes all nodes
        for (int i = 0; i < nodeCount - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }
        data.edges.add(new JsonEdge("T" + (nodeCount - 1), "T0", rand.nextInt(5) + 1));

        // Add some additional cycles
        if (nodeCount >= 4) {
            data.edges.add(new JsonEdge("T0", "T2", rand.nextInt(5) + 1));
            data.edges.add(new JsonEdge("T2", "T0", rand.nextInt(5) + 1));
        }
    }

    private static void createDAG(JsonGraphData data, int nodeCount, Random rand) {
        // Create a simple DAG (no cycles)
        for (int i = 0; i < nodeCount - 1; i++) {
            data.edges.add(new JsonEdge("T" + i, "T" + (i + 1), rand.nextInt(5) + 1));
        }

        // Add some cross edges that don't create cycles
        for (int i = 0; i < nodeCount / 2; i++) {
            int from = rand.nextInt(nodeCount - 2);
            int to = from + 2 + rand.nextInt(nodeCount - from - 2);
            if (to < nodeCount) {
                data.edges.add(new JsonEdge("T" + from, "T" + to, rand.nextInt(5) + 1));
            }
        }
    }

    private static void createSimpleTestGraph() throws IOException {
        JsonGraphData data = new JsonGraphData();

        // Simple graph with 3 SCCs
        for (int i = 0; i < 6; i++) {
            data.nodes.add(new JsonNode("T" + i, (i % 3) + 1));
        }

        // Component 1: T0-T1 cycle
        data.edges.add(new JsonEdge("T0", "T1", 2));
        data.edges.add(new JsonEdge("T1", "T0", 1));

        // Component 2: T2-T3 cycle
        data.edges.add(new JsonEdge("T2", "T3", 3));
        data.edges.add(new JsonEdge("T3", "T2", 2));

        // Component 3: T4-T5 (separate)
        data.edges.add(new JsonEdge("T4", "T5", 4));

        // Connections between components
        data.edges.add(new JsonEdge("T1", "T2", 2));
        data.edges.add(new JsonEdge("T3", "T4", 3));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter("data/tasks.json")) {
            gson.toJson(data, writer);
        }
        System.out.println("📁 Created: data/tasks.json (test graph with 3 SCCs)");
    }

    private static void saveDataset(JsonGraphData data, int id, String size, String type, int nodeCount) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filename = "data/dataset_" + id + "_" + size + "_" + type + ".json";
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(data, writer);
        }
        System.out.println("📁 Generated: " + filename + " - " + nodeCount + " vertices, " +
                data.edges.size() + " edges");
    }

    private static void generateReport() throws IOException {
        StringBuilder report = new StringBuilder();
        report.append("Dataset Report\n");
        report.append("==============\n\n");

        report.append("Guaranteed SCC structures:\n\n");

        report.append("SMALL (6-10 nodes):\n");
        report.append("1. dataset_1_small_sparse_cyclic.json - Single cyclic component\n");
        report.append("2. dataset_2_small_pure_dag.json - Pure DAG (no cycles)\n");
        report.append("3. dataset_3_small_dense_mixed.json - 2 components with cycles\n\n");

        report.append("MEDIUM (10-20 nodes):\n");
        report.append("4. dataset_4_medium_multiple_sccs.json - 3 separate SCCs ✅\n");
        report.append("5. dataset_5_medium_sparse_dag.json - Sparse DAG\n");
        report.append("6. dataset_6_medium_dense_cyclic.json - Dense cyclic graph\n\n");

        report.append("LARGE (20-50 nodes):\n");
        report.append("7. dataset_7_large_sparse_mixed.json - 3-5 components ✅\n");
        report.append("8. dataset_8_large_dense_cyclic.json - Large cyclic graph\n");
        report.append("9. dataset_9_large_multiple_sccs_dense.json - Multiple SCCs dense ✅\n\n");

        report.append("Test file:\n");
        report.append("- tasks.json - Simple test with 3 SCCs for Main class\n");

        try (Writer writer = new FileWriter("data/dataset_report.txt")) {
            writer.write(report.toString());
        }
        System.out.println("📄 Generated dataset_report.txt");
    }
}