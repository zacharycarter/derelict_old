package com.carterza.generator.graph.algorithm;

import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DFS {
    private Map<Node, Boolean> marked;
    private int count;           // number of vertices connected to s
    private List<Edge> edgesHit;

    /**
     * Computes the vertices in graph {@code G} that are
     * connected to the source vertex {@code s}.
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DFS(Graph G, Node s) {
        this.edgesHit = new ArrayList<Edge>();
        marked = new HashMap<Node, Boolean>();
        for(Node n : G.getNodes().values()) {
            marked.put(n, false);
        }
        // validateVertex(s);
        dfs(G, s);
    }

    // depth first search from v
    private void dfs(Graph G, Node v) {
        count++;
        marked.put(v, true);
        for(Edge e : v.getNeighbors()) {
            Node neighbor = e.getNeighbor(v);
            if(!marked.get(neighbor)) {
                dfs(G, neighbor);
            } else {
                edgesHit.add(e);
            }
        }
    }

    /**
     * Is there a path between the source vertex {@code s} and vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean marked(Node v) {
        // validateVertex(v);
        return marked.get(v);
    }

    /**
     * Returns the number of vertices connected to the source vertex {@code s}.
     * @return the number of vertices connected to the source vertex {@code s}
     */
    public int count() {
        return count;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    /*private void validateVertex(int v) {
        int V = marked.s;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }*/

    /**
     * Unit tests the {@code DepthFirstSearch} data type.
     *
     * @param args the command-line arguments
     */
    /*public static void main(String[] args) {
        In in = new In(args[0]);
        Graph G = new Graph(in);
        int s = Integer.parseInt(args[1]);
        DepthFirstSearch search = new DepthFirstSearch(G, s);
        for (int v = 0; v < G.V(); v++) {
            if (search.marked(v))
                StdOut.print(v + " ");
        }

        StdOut.println();
        if (search.count() != G.V()) StdOut.println("NOT connected");
        else                         StdOut.println("connected");
    }*/

    public List<Edge> getEdgesHit() {
        return this.edgesHit;
    }

}

