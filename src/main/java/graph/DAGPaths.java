package graph;

import java.util.*;


public class DAGPaths {

    public static class ShortestResult {
        public final double[] dist;   // dist[v]
        public final int[] parent;    // parent[v] for path reconstruction
        public ShortestResult(double[] d, int[] p) { this.dist = d; this.parent = p; }
    }

    public static class LongestResult {
        public final double distance; // best distance among reachable nodes
        public final List<Integer> path;
        public LongestResult(double d, List<Integer> p) { this.distance = d; this.path = p; }
    }

    public static ShortestResult shortestPaths(Map<Integer, List<WeightedEdge>> dag,
                                               List<Integer> topoOrder,
                                               int source,
                                               util.Metrics m) {
        int n = dag.size();
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0.0;

        for (int u : topoOrder) {
            if (Double.isInfinite(dist[u])) continue;
            for (WeightedEdge e : dag.getOrDefault(u, List.of())) {
                int v = e.to();
                double nd = dist[u] + e.weight();
                if (nd < dist[v]) {
                    dist[v] = nd;
                    parent[v] = u;
                }
                if (m != null) m.relaxations++;
            }
        }
        return new ShortestResult(dist, parent);
    }


    public static LongestResult longestPath(Map<Integer, List<WeightedEdge>> dag,
                                            List<Integer> topoOrder,
                                            int source) {
        int n = dag.size();
        double[] best = new double[n];
        int[] parent = new int[n];
        Arrays.fill(best, Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);
        best[source] = 0.0;

        for (int u : topoOrder) {
            if (Double.isInfinite(-best[u])) continue; // skip unreachable
            for (WeightedEdge e : dag.getOrDefault(u, List.of())) {
                int v = e.to();
                double cand = best[u] + e.weight();
                if (cand > best[v]) {
                    best[v] = cand;
                    parent[v] = u;
                }
            }
        }


        double maxDist = Double.NEGATIVE_INFINITY;
        int arg = -1;
        for (int v = 0; v < n; v++) {
            if (best[v] > maxDist) {
                maxDist = best[v];
                arg = v;
            }
        }
        List<Integer> path = reconstructPath(parent, source, arg);
        return new LongestResult(maxDist, path);
    }


    public static List<Integer> reconstructPath(int[] parent, int source, int target) {
        if (target < 0) return List.of();
        Deque<Integer> st = new ArrayDeque<>();
        int cur = target;
        while (cur != -1) {
            st.push(cur);
            if (cur == source) break;
            cur = parent[cur];
        }
        if (st.peek() == null || st.peek() != source) return List.of(); // unreachable
        List<Integer> res = new ArrayList<>();
        while (!st.isEmpty()) res.add(st.pop());
        return res;
    }
}
