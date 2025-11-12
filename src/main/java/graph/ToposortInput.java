package graph;

import java.util.*;


public class ToposortInput {
    public final int n;
    public final Map<Integer, List<Integer>> adj;

    public ToposortInput(int n, Map<Integer, List<Integer>> adj) {
        this.n = n;
        this.adj = adj;
    }

    public static ToposortInput fromWeighted(Map<Integer, List<WeightedEdge>> w) {
        Map<Integer, List<Integer>> simple = new LinkedHashMap<>();
        int n = w.size();
        for (int i = 0; i < n; i++) {
            List<Integer> out = new ArrayList<>();
            for (WeightedEdge we : w.getOrDefault(i, List.of())) out.add(we.to());
            simple.put(i, out);
        }
        return new ToposortInput(n, simple);
    }
}
