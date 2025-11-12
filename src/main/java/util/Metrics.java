package util;

public class Metrics {
    public long dfsVisits = 0;
    public long edgesExplored = 0;
    public long pushes = 0;       // Kahn queue pushes
    public long pops = 0;         // Kahn queue pops
    public long relaxations = 0;  // DAG SSSP relaxations

    public void reset() {
        dfsVisits = edgesExplored = pushes = pops = relaxations = 0;
    }

    @Override public String toString() {
        return "Metrics{" +
                "dfsVisits=" + dfsVisits +
                ", edgesExplored=" + edgesExplored +
                ", pushes=" + pushes +
                ", pops=" + pops +
                ", relaxations=" + relaxations +
                '}';
    }
}
