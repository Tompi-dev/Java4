

import graph.DAGPaths;
import graph.SCC;
import graph.ToposortInput;
import graph.WeightedEdge;
import model.TaskGraph;
import util.DataLoader;
import util.Metrics;
import util.Stopwatch;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java -cp target/smart-city-graphs-0.1.0.jar "
                    + "kz.aiu.smartcity.Main <path_to_json> <sourceNodeLabel>");
            System.exit(1);
        }
        String jsonPath = args[0];
        String sourceNode = args[1];

        TaskGraph g = DataLoader.load(jsonPath);
        System.out.println("Loaded graph: n=" + g.nodeCount() + ", m=" + g.edgeCount());
        System.out.println("Nodes: " + g.getNodes());
        System.out.println("Edge weight model: EDGE_WEIGHTS");

        Metrics metrics = new Metrics();


        Stopwatch sccTimer = Stopwatch.startNew();
        SCC scc = new SCC();
        SCC.Result sccRes = scc.run(g, metrics);
        Map<Integer, List<WeightedEdge>> cond = scc.buildWeightedCondensation(g, sccRes);
        sccTimer.stop();

        System.out.println("\nSCC components (" + sccRes.components.size() + "):");
        for (int i = 0; i < sccRes.components.size(); i++) {
            List<String> comp = sccRes.components.get(i);
            System.out.println("C" + i + " (size " + comp.size() + ") = " + comp);
        }
        System.out.println("\nCondensation DAG (weighted):");
        for (Map.Entry<Integer, List<WeightedEdge>> e : cond.entrySet()) {
            System.out.print("C" + e.getKey() + " -> ");
            System.out.println(e.getValue());
        }
        System.out.println("SCC stage metrics: " + metrics);
        System.out.println("SCC stage time: " + sccTimer.elapsedNanos() + " ns");

        Stopwatch topoTimer = Stopwatch.startNew();
        ToposortInput topoIn = ToposortInput.fromWeighted(cond);
        List<Integer> topoOrder = graph.TopoSort.kahn(topoIn, metrics);
        topoTimer.stop();

        System.out.println("\nTopological order of components:");
        System.out.println(topoOrder);
        System.out.println("Topo metrics (pushes/pops): pushes=" + metrics.pushes + ", pops=" + metrics.pops);
        System.out.println("Topo stage time: " + topoTimer.elapsedNanos() + " ns");


        System.out.println("\nDerived order of original tasks after SCC compression:");
        List<String> derived = new ArrayList<>();
        for (Integer cid : topoOrder) {
            derived.addAll(sccRes.components.get(cid));
        }
        System.out.println(derived);


        if (!sccRes.compIndex.containsKey(sourceNode)) {
            System.err.println("Source node '" + sourceNode + "' not found in graph.");
            System.exit(2);
        }
        int sourceComp = sccRes.compIndex.get(sourceNode);


        Stopwatch spTimer = Stopwatch.startNew();
        DAGPaths.ShortestResult sp = DAGPaths.shortestPaths(cond, topoOrder, sourceComp, metrics);
        spTimer.stop();
        System.out.println("\nShortest-path distances from component C" + sourceComp + ":");
        for (int i = 0; i < sp.dist.length; i++) {
            System.out.println("C" + i + " = " + (Double.isInfinite(sp.dist[i]) ? "INF" : sp.dist[i]));
        }

        for (int t = 0; t < sp.dist.length; t++) {
            if (!Double.isInfinite(sp.dist[t]) && t != sourceComp) {
                List<Integer> p = DAGPaths.reconstructPath(sp.parent, sourceComp, t);
                System.out.println("Shortest path to C" + t + ": " + p);
            }
        }
        System.out.println("SP relaxations: " + metrics.relaxations);
        System.out.println("Shortest paths stage time: " + spTimer.elapsedNanos() + " ns");


        Stopwatch lpTimer = Stopwatch.startNew();
        DAGPaths.LongestResult lp = DAGPaths.longestPath(cond, topoOrder, sourceComp);
        lpTimer.stop();
        System.out.println("\nCritical (longest) path from C" + sourceComp + ": length=" + lp.distance);
        System.out.println("Path: " + lp.path);
        System.out.println("Longest paths stage time: " + lpTimer.elapsedNanos() + " ns");
    }
}
