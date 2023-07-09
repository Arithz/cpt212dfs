package DFS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Graph {
    
    // Adjacency list representation of the graph and use map because we want to store the weight of the edge
    private Map<String, Map<String,Integer>> adjList; 

    public Graph() {
        this.adjList = new HashMap<>();
    }

    public void addVertex(String vertex) {
        this.adjList.put(vertex, new HashMap<>());
    }

    public void addEdge(String src, String dest, int weight) {
        this.adjList.get(src).put(dest, weight);
    }

    public int getWeight(String src, String dest) {
        // If the edge doesn't exist, the get() method will return null
        Integer weight = this.adjList.get(src).get(dest);
        return weight == null ? -1 : weight;

    }

    public List<String> getNeighbours(String vertex) {
        return new ArrayList<>(this.adjList.get(vertex).keySet());
    }

    public Map<String, Map<String,Integer>> getAdjList() {
        return this.adjList;
    }


    //first start here
    public void initializeGraphFromFile(String filepath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filepath));
            
            for(String line:lines) {
                String[] parts = line.split(":");
                String source = parts[0];
                String[] destinations = parts[1].split(",");

                this.addVertex(source);

                for (String destination : destinations) {
                    String[] destinationParts = destination.split("\\(");
                    String dest = destinationParts[0];
                    int distance = Integer.parseInt(destinationParts[1].split("\\)")[0]);
                    this.addEdge(source, dest, distance);
                }
            }
        }catch(IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    //will be call here when we want to search for the path from source to destination
    public List<String> DFSSearch(String src, String dest) {
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        List<String> path = new ArrayList<>();

        DFSRecursive(src, dest, visited, parentMap, path);

        if (!parentMap.containsKey(dest)) {
            return null;
        }

        // Reconstruct the path from parentMap
        List<String> locationPath = new ArrayList<>();
        String current = dest;
        while (!current.equals(src)) {
            locationPath.add(current);
            current = parentMap.get(current);
        }
        locationPath.add(src);

        Collections.reverse(locationPath);

        return locationPath;
    }

    private void DFSRecursive(String current, String dest, Set<String> visited, Map<String, String> parentMap, List<String> path) {
        visited.add(current);

        if (current.equals(dest)) {
            return;
        }

        Map<String, Integer> neighbors = adjList.get(current);
        if (neighbors != null) {
            for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                String neighbor = neighborEntry.getKey();
                if (!visited.contains(neighbor)) {
                    parentMap.put(neighbor, current);
                    path.add(neighbor);
                    DFSRecursive(neighbor, dest, visited, parentMap, path);
                    if (path.contains(dest)) {
                        // Path to destination found, no need to explore further
                        return;
                    }
                    path.remove(neighbor); // Remove the neighbor from the path if it didn't lead to the destination
                }
            }
        }

        visited.remove(current); // Remove the current vertex from the visited set after exploring its neighbors
    }
}