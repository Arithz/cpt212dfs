import DFS.Graph;
import GUI.GUI;

public class DFSMain {
    private final Graph graph;

    public DFSMain() {
        this.graph = new Graph();
    }

    public static void main(String[] args) {
        String filepath = "locations.txt";
        Graph graph = new Graph();
        GUI gui = new GUI(graph);

        graph.initializeGraphFromFile(filepath);
        gui.createWindow();
    }
}
