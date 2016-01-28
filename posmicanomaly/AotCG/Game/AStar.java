package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Level;
import posmicanomaly.AotCG.Component.Tile;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jesse Pospisil on 10/27/2015.
 */
public class AStar {
    private int g = 1;
    private Level level;


    private class Node {
        private Tile tile;
        private int y;
        private int x;
        private int f_cost;
        private int h_cost;
        private int g_cost;
        private Node parent;


        public Node(Tile tile) {
            this.tile = tile;
            y = this.tile.getY();
            x = this.tile.getX();
        }

        public boolean isBlocked() {
            return tile.isBlocked();
        }

        @Override
        public boolean equals(Object o) {
            Node targetNode = (Node) o;
            return (this.x == targetNode.x && this.y == targetNode.y);
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }

        public Tile getTile() {
            return tile;
        }

        public void updateCost(Tile target) {
            int mod = 0;
            // Try to avoid stairs and caves unless that's what we want to go to
            switch(this.tile.getType()) {
                case STAIRS_DOWN:
                case STAIRS_UP:
                    mod = 10;
                    break;
                case CAVE_OPENING:
                    mod = 10;
                    break;
                case WATER:
                    mod = 9;
                    break;
                case FOREST:
                    mod = 3;
                    break;
                case BRUSH:
                    mod = 2;
                    break;
                case MOUNTAIN:
                    mod = 8;
                    break;
                case WALL:
                    mod = 9999;
                    break;
            }
            if(this.tile.hasActor()) {
                mod = 10;
            }
            g_cost = g + mod;
            h_cost = (Math.abs(x - target.getX()) + Math.abs(y - target.getY()));
            f_cost = g_cost + h_cost;
        }

        public int getF_cost() {
            return f_cost;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }
    }



    public AStar(Level level) {
        this.level = level;
    }

    public Node getCheapestNode(ArrayList<Node> nodes) {
        Node result = null;
        for (Node n : nodes) {
            if (result == null) {
                result = n;
            } else if (n.getF_cost() < result.getF_cost()) {
                result = n;
            }
        }
        return result;
    }

    private boolean hasNode(ArrayList<Node> nodes, Node node) {
        for(Node n : nodes) {
            if(node.equals(n)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Node> getNeighboringNodes(Node source) {
        ArrayList<Node> result = new ArrayList<>();
        ArrayList<Tile> tiles = level.getNearbyTiles(source.getY(), source.getX());
        for(Tile t : tiles) {
            result.add(new Node(t));
        }

        return result;
    }

    public ArrayList<Tile> getShortestPath(Tile source, Tile target) {
        // Set of tiles to be evaluated
        ArrayList<Node> open = new ArrayList<>();
        // Set of tiles already evaluated
        ArrayList<Node> closed = new ArrayList<>();

        // Add the start tile to the openTiles
        Node sourceNode = new Node(source);
        open.add(sourceNode);


        // Variables
        Node current = null;
        // Loop
        while (!open.isEmpty()) {
            // Get tile in openTiles with the lowest f_cost
            current = getCheapestNode(open);

            // Remove current from openTiles
            open.remove(current);

            // Add current to closedTiles
            closed.add(current);

            // If current tile is the target tile, shortest path has been found
            if (current.getY() == target.getY() && current.getX() == target.getX()) {
                // Reconstruct the path
                ArrayList<Tile> shortestPath = new ArrayList<>();

                Node parent = current;
                while(parent != null) {
                    shortestPath.add(parent.getTile());
                    parent = parent.getParent();
                }
                Collections.reverse(shortestPath);
                // Remove the source tile
                shortestPath.remove(0);
                return shortestPath;
            }

            ArrayList<Node> neighboringNodes = getNeighboringNodes(current);
            for (Node n : neighboringNodes) {
                //if (!n.isBlocked()) {
                    if (hasNode(closed, n)) {
                        continue;
                    }
                    if (!hasNode(open, n)) {
                        open.add(n);
                        n.setParent(current);
                        n.updateCost(target);
                    } else {
                        n.updateCost(target);
                        if (current.getF_cost() < n.getF_cost()) {
                            n.setParent(current);
                        }
                    }
                //}
            }
        }
        System.out.println("AStar.getShortestPath :: open.isEmpty()");
        // No path
        return null;
    }
}
