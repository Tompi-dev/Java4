package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Edge;
import model.TaskGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    public static TaskGraph load(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));
        List<String> nodes = new ArrayList<>();
        for (JsonNode n : root.withArray("nodes")) nodes.add(n.asText());
        List<Edge> edges = new ArrayList<>();
        for (JsonNode e : root.withArray("edges")) {
            String from = e.get("from").asText();
            String to = e.get("to").asText();
            double w = e.has("weight") ? e.get("weight").asDouble() : 1.0;
            edges.add(new Edge(from, to, w));
        }
        return new TaskGraph(nodes, edges);
    }
}
