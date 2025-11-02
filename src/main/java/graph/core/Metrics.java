package graph.core;

public interface Metrics {
    void reset();
    long getOperationsCount();
    double getExecutionTimeMs();
    void incrementOperations(int count);
}