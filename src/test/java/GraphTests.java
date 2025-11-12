package graph;

import model.Edge;
import model.TaskGraph;
import org.junit.jupiter.api.Test;
import util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {

    @Test
    void sccAndCondensationAreCorrect() {
        // A,B,C cycle; then C->D->E; weights arbitrary
        List<String> nodes = Arrays.asList("A","B","C","D","E");
        List<Edge> edges = List.of(
                new Edge("A","B",2),
                new Edge("B","C",1),
                new Edge("C","A",3),
                new Edge("C","D",1),
                new Edge("D","E",5)
        );
        TaskGraph g = new TaskGraph(nodes, edges);

        SCC scc = new SCC();
        Metrics m = new Metrics();
        SCC.Result r = scc.run(g, m);
        assertEquals(3, r.components.size());
        int compABC = r.compIndex.get("A");
        assertEquals(compABC, r.compIndex.get("B"));
        assertEquals(compABC, r.compIndex.get("C"));

        Map<Integer, List<WeightedEdge>> dag = scc.buildWeightedCondensation(g, r);
        assertTrue(dag.get(compABC).size() >= 1, "ABC must lead to D's component");
    }

    @Test
    void topoKahnWorks() {

        Map<Integer, List<WeightedEdge>> dag = new LinkedHashMap<>();
        dag.put(0, List.of(new WeightedEdge(1,1), new WeightedEdge(2,1)));
        dag.put(1, List.of(new WeightedEdge(3,1)));
        dag.put(2, List.of(new WeightedEdge(3,1)));
        dag.put(3, List.of());

        ToposortInput in = ToposortInput.fromWeighted(dag);
        Metrics m = new Metrics();
        List<Integer> order = TopoSort.kahn(in, m);
        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    void dagShortestAndLongest() {

        Map<Integer, List<WeightedEdge>> dag = new LinkedHashMap<>();
        dag.put(0, List.of(new WeightedEdge(1,2), new WeightedEdge(2,4)));
        dag.put(1, List.of(new WeightedEdge(3,1)));
        dag.put(2, List.of(new WeightedEdge(3,1)));
        dag.put(3, List.of());

        List<Integer> topo = List.of(0,1,2,3);
        Metrics m = new Metrics();
        DAGPaths.ShortestResult sp = DAGPaths.shortestPaths(dag, topo, 0, m);
        assertEquals(0.0, sp.dist[0]);
        assertEquals(3.0, sp.dist[3]); // via 1
        List<Integer> p = DAGPaths.reconstructPath(sp.parent, 0, 3);
        assertEquals(List.of(0,1,3), p);

        DAGPaths.LongestResult lp = DAGPaths.longestPath(dag, topo, 0);
        assertEquals(5.0, lp.distance);
        assertEquals(List.of(0,2,3), lp.path);
    }
}
