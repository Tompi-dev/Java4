package graph;


public record WeightedEdge(int to, double weight) {
    @Override public String toString() { return "C" + to + "(" + weight + ")"; }
}
