package graph.core;

public class BaseMetrics implements Metrics {
    protected long operationsCount = 0;
    protected long startTime;
    protected long endTime;

    public BaseMetrics() {
        reset();
    }

    @Override
    public void reset() {
        operationsCount = 0;
        startTime = System.nanoTime();
    }

    @Override
    public void incrementOperations(int count) {
        operationsCount += count;
    }

    @Override
    public long getOperationsCount() {
        return operationsCount;
    }

    @Override
    public double getExecutionTimeMs() {
        endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;
    }
}