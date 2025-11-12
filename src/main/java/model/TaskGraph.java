package model;

import java.util.*;
import java.util.stream.Collectors;

public class TaskGraph {
    private final List<String> nodes;
    private final List<Edge> edges;
    private final Map<String, List<Edge>> adj;

    public TaskGraph(List<String> nodes, List<Edge> edges) {
        this.nodes = List.copyOf(nodes);
        this.edges = List.copyOf(edges);
        this.adj = new HashMap<>();
        for (String u : nodes) adj.put(u, new ArrayList<>());
        for (Edge e : edges) {
            adj.computeIfAbsent(e.from(), k -> new ArrayList<>()).add(e);
            adj.computeIfAbsent(e.to(), k -> new ArrayList<>());
        }
    }

    public List<String> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
    public Map<String, List<Edge>> adj() { return adj; }
    public int nodeCount() { return nodes.size(); }
    public int edgeCount() { return edges.size(); }

    public List<Edge> outgoing(String u) {
        return adj.getOrDefault(u, List.of());
    }

    public TaskGraph subgraph(Set<String> subset) {
        List<String> newNodes = new ArrayList<>(subset);
        List<Edge> newEdges = edges.stream()
                .filter(e -> subset.contains(e.from()) && subset.contains(e.to()))
                .collect(Collectors.toList());
        return new TaskGraph(newNodes, newEdges);
    }
}
