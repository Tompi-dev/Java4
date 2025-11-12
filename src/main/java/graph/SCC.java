package graph;

import model.Edge;
import model.TaskGraph;
import util.Metrics;

import java.util.*;

public class SCC {

    public static class Result {
        public final List<List<String>> components;
        public final Map<String, Integer> compIndex;

        public Result(List<List<String>> components, Map<String, Integer> compIndex) {
            this.components = components;
            this.compIndex = compIndex;
        }
    }

    private int time;
    private final Map<String, Integer> disc = new HashMap<>();
    private final Map<String, Integer> low = new HashMap<>();
    private final Deque<String> stack = new ArrayDeque<>();
    private final Set<String> inStack = new HashSet<>();
    private final List<List<String>> comps = new ArrayList<>();

    public Result run(TaskGraph g, Metrics m) {
        time = 0;
        disc.clear(); low.clear();
        stack.clear(); inStack.clear();
        comps.clear();

        for (String u : g.getNodes()) {
            if (!disc.containsKey(u)) {
                dfs(u, g, m);
            }
        }
        Map<String, Integer> compIndex = new HashMap<>();
        for (int id = 0; id < comps.size(); id++) {
            for (String v : comps.get(id)) compIndex.put(v, id);
        }
        return new Result(comps, compIndex);
    }

    private void dfs(String u, TaskGraph g, Metrics m) {
        disc.put(u, time);
        low.put(u, time);
        time++;
        stack.push(u);
        inStack.add(u);
        if (m != null) m.dfsVisits++;

        for (Edge e : g.outgoing(u)) {
            if (m != null) m.edgesExplored++;
            String v = e.to();
            if (!disc.containsKey(v)) {
                dfs(v, g, m);
                low.put(u, Math.min(low.get(u), low.get(v)));
            } else if (inStack.contains(v)) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }


        if (Objects.equals(low.get(u), disc.get(u))) {
            List<String> comp = new ArrayList<>();
            while (true) {
                String w = stack.pop();
                inStack.remove(w);
                comp.add(w);
                if (w.equals(u)) break;
            }
            comps.add(comp);
        }
    }


    public Map<Integer, List<WeightedEdge>> buildWeightedCondensation(TaskGraph g, Result r) {
        int k = r.components.size();
        @SuppressWarnings("unchecked")
        Map<Integer, Double>[] best = new Map[k];
        for (int i = 0; i < k; i++) best[i] = new HashMap<>();

        for (Edge e : g.getEdges()) {
            Integer a = r.compIndex.get(e.from());
            Integer b = r.compIndex.get(e.to());
            if (a == null || b == null || a.equals(b)) continue;
            double w = e.weight();
            best[a].merge(b, w, Math::min);
        }

        Map<Integer, List<WeightedEdge>> dag = new LinkedHashMap<>();
        for (int i = 0; i < k; i++) {
            List<WeightedEdge> out = new ArrayList<>();
            for (Map.Entry<Integer, Double> en : best[i].entrySet()) {
                out.add(new WeightedEdge(en.getKey(), en.getValue()));
            }
            dag.put(i, out);
        }
        return dag;
    }
}
