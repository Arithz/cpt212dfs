package GUI;

import DFS.Graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class GUI {

    // graph properties
    private final Graph graph;

    //window properties
    private final int width = 800;
    private final int height = 800;

    // ui properties
    private JComboBox<String> sourceComboBox;
    private JComboBox<String> destinationComboBox;
    private JTextArea outputArea;

    public GUI(Graph graph) {
        this.graph = graph;
    }

    // loop through the graph and paint the nodes and edges
    public void createWindow() {
        JFrame frame = new JFrame("Malaysia State Path Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLayout(new BorderLayout()); // Set the layout manager for the content pane

        // Create a JPanel to draw the graph on
        GraphPanel graphPanel = new GraphPanel();

        // Create a JPanel to hold the input fields and output area
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        // labels
        JLabel sourceLabel = new JLabel("Source");
        JLabel destinationLabel = new JLabel("Destination");

        // search path button
        JButton searchButton = new JButton("Search");
        addListenerToSearchButton(searchButton, graphPanel);

        // select options for source and destination
        this.sourceComboBox = new JComboBox<>();
        this.destinationComboBox = new JComboBox<>();

        // Add all the locations to the combo boxes
        for (String location : graph.getAdjList().keySet()) {
            sourceComboBox.addItem(location);
            destinationComboBox.addItem(location);
        }

        
        // create button to add new edge
        JButton addEdgeButton = new JButton("Add Edge");
        addListenerToAddEdgeButton(addEdgeButton, graphPanel);

        // Add the components to the inputPanel
        inputPanel.add(sourceLabel);
        inputPanel.add(sourceComboBox);
        inputPanel.add(destinationLabel);
        inputPanel.add(destinationComboBox);
        inputPanel.add(searchButton);
        inputPanel.add(addEdgeButton);

        // output area to show the path taken and the distance
        JTextArea outputArea = new JTextArea(5, 20);
        this.outputArea = outputArea;
        outputArea.setEditable(false);

        // Create a JPanel to hold the inputPanel and outputArea
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(outputArea, BorderLayout.CENTER);

        // finalise the window
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(graphPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }



    private void addListenerToSearchButton(JButton searchButton, GraphPanel graphPanel) {
        searchButton.addActionListener(e -> {
            String source = (String) sourceComboBox.getSelectedItem();
            String destination = (String) destinationComboBox.getSelectedItem();

            if (source.equals(destination)) {
                JOptionPane.showMessageDialog(null, "Source and destination cannot be the same");
                return;
            }

            // Insert the paths result
            List<String> path = this.graph.DFSSearch(source, destination);

            if (path == null) {
                outputArea.setText("No path found");
                return;
            }

            // Build the output string
            StringBuilder output = new StringBuilder();
            output.append("Path: ");

            // Iterate over the path map and append each location with its distance
            int totaldistance = 0;
            for(String location : path) {
                output.append(location);
                output.append(" -> ");

                // Add the distance to the total distance
                if (path.indexOf(location) != path.size() - 1) {
                    Map<String, Integer> neighbours = graph.getAdjList().get(location);
                    totaldistance += neighbours.get(path.get(path.indexOf(location) + 1));
                }
            }

            // Remove the trailing "->"
            output.setLength(output.length() - 4);

            output.append("\n");
            output.append("Distance: ");
            output.append(totaldistance);

            outputArea.setText(output.toString());

            // Repaint the graph when the search button is clicked
            graphPanel.setLocations(source, destination, path);

        });
    }

    private class GraphPanel extends JPanel {
        String source;
        String destination;
        List<String> path;

        void setLocations(String source, String destination, List<String> path) {
            this.source = source;
            this.destination = destination;
            this.path = path;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Map<String, Point> locationPoints = calculateLocations();

            for (Map.Entry<String, Point> entry : locationPoints.entrySet()) {
                String location = entry.getKey();
                Point point = entry.getValue();

                // Draw circle point for each location
                int pointSize = 10;
                int x = point.x - (pointSize / 2);
                int y = point.y - (pointSize / 2);

                if(location.equals(source))
                    g.setColor(Color.RED);
                else if(location.equals(destination))
                    g.setColor(Color.GREEN);
                else if(path != null && path.contains(location))
                    g.setColor(Color.BLUE);
                else
                    g.setColor(Color.BLACK);
                g.fillOval(x, y, pointSize, pointSize);

                // Draw location name on top of the point
                g.setColor(Color.BLACK);
                //draw on center of the point
                int stringX = point.x - (g.getFontMetrics().stringWidth(location) / 2);
                int stringY = point.y + (g.getFontMetrics().getHeight() / 2) -20;
                g.drawString(location, stringX, stringY);

                Map<String, Integer> neighbours = graph.getAdjList().get(location);
                for (Map.Entry<String, Integer> neighbourEntry : neighbours.entrySet()) {
                    String neighbour = neighbourEntry.getKey();
                    Point endPoint = locationPoints.get(neighbour);

                    // Draw line between the two points in gray color

                    if(path != null) {
                        for(int i = 0; i < path.size() - 1; i++) {
                            if((path.get(i).equals(location) && path.get(i + 1).equals(neighbour)) ||
                            (path.get(i).equals(neighbour) && path.get(i + 1).equals(location))) {
                                // add number to the line to show sequence of the path
                                g.drawString(String.valueOf(i + 1), ((point.x + endPoint.x) / 2)+5, (point.y + endPoint.y) / 2);
                                g.setColor(Color.BLUE);
                                break;
                            }
                            else
                                g.setColor(Color.GRAY);
                        }
                    }
                    g.drawLine(point.x, point.y, endPoint.x, endPoint.y);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }
    }

    private Map<String, Point> calculateLocations() {
        Map<String, Point> locationPoints = new HashMap<>();
        int panelWidth = this.width;
        int panelHeight = this.height;
        int centerX = panelWidth / 2 -20;
        int centerY = panelHeight / 2 -80;
        double angleIncrement = 2 * Math.PI / graph.getAdjList().size();
        double currentAngle = 0;

        ArrayList<String> locations = new ArrayList<>(graph.getAdjList().keySet());

        for (int i = 0; i < locations.size(); i++) {
            int x = (int) (centerX + Math.cos(currentAngle) * panelWidth / 3);
            int y = (int) (centerY + Math.sin(currentAngle) * panelHeight / 3);
            locationPoints.put(locations.get(i), new Point(x, y));
            currentAngle += angleIncrement;
        }
        return locationPoints;
    }

    // Add listener to the "Add Edge" button
    private void addListenerToAddEdgeButton(JButton addEdgeButton, GraphPanel graphPanel) {
        addEdgeButton.addActionListener(e -> {
            // Create a new JFrame for the popup window
            JFrame popupFrame = new JFrame("Add Edge");
            popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            popupFrame.setSize(300, 200);
            popupFrame.setLayout(new FlowLayout());

            // Create combo boxes for source and destination selection
            JComboBox<String> sourceComboBox = new JComboBox<>();
            JComboBox<String> destinationComboBox = new JComboBox<>();

            // Add all the locations to the combo boxes
            for (String location : graph.getAdjList().keySet()) {
                sourceComboBox.addItem(location);
                destinationComboBox.addItem(location);
            }

            // Create text field for distance input
            JTextField distanceTextField = new JTextField(10);

            // Create button to add the new edge
            JButton confirmButton = new JButton("Add");

            //make the components in different row
            JPanel row1 = new JPanel();
            row1.add(new JLabel("Source: "));
            row1.add(sourceComboBox);

            JPanel row2 = new JPanel();
            row2.add(new JLabel("Destination: "));
            row2.add(destinationComboBox);

            JPanel row3 = new JPanel();
            row3.add(new JLabel("Distance: "));
            row3.add(distanceTextField);

            JPanel row4 = new JPanel();
            row4.add(confirmButton);

            // Add all the components to the popup frame
            popupFrame.add(row1);
            popupFrame.add(row2);
            popupFrame.add(row3);
            popupFrame.add(row4);

            // Add listener to the confirm button
            confirmButton.addActionListener(confirmEvent -> {
                String source = (String) sourceComboBox.getSelectedItem();
                String destination = (String) destinationComboBox.getSelectedItem();
                String distanceStr = distanceTextField.getText();

                if (source.equals(destination)) {
                    JOptionPane.showMessageDialog(popupFrame, "Source and destination cannot be the same");
                    return;
                }

                if (distanceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(popupFrame, "Please enter a distance value");
                    return;
                }

                try {
                    int distance = Integer.parseInt(distanceStr);
                    this.graph.addEdge(source, destination, distance);
                    // Add the reverse edge as well
                    this.graph.addEdge(destination, source, distance);
                    JOptionPane.showMessageDialog(popupFrame, "New edge added successfully");
                    graphPanel.repaint(); // Repaint the graph panel
                    popupFrame.dispose(); // Close the popup frame
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(popupFrame, "Invalid distance value");
                }
            });

            // Display the popup frame
            popupFrame.setVisible(true);
        });
    }

}

