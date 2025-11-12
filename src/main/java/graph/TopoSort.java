package graph;

import util.Metrics;

import java.util.*;


public class TopoSort {

    public static List<Integer> kahn(ToposortInput in, Metrics metrics) {
        int n = in.n;
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : in.adj.get(u)) indeg[v]++;
        }

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) {
            q.add(i);
            if (metrics != null) metrics.pushes++;
        }

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.remove();
            if (metrics != null) metrics.pops++;
            order.add(u);
            for (int v : in.adj.get(u)) {
                if (--indeg[v] == 0) {
                    q.add(v);
                    if (metrics != null) metrics.pushes++;
                }
            }
        }
        if (order.size() != n)
            throw new IllegalStateException("Graph is not a DAG (cycle detected in condensation?)");
        return order;
    }
}
