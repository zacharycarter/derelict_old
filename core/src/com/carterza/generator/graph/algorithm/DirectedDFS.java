package com.carterza.generator.graph.algorithm;

import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;

import java.util.HashMap;
import java.util.Map;

public class DirectedDFS {
    // private boolean[] marked;  // marked[v] = true if v is reachable
    private Map<Node, Boolean> marked;
    // from source (or sources)
    private int count;         // number of vertices reachable from s

    /**
     * Computes the vertices in digraph {@code G} that are
     * reachable from the source vertex {@code s}.
     * @param G the digraph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DirectedDFS(Graph G, Node s) {
        marked = new HashMap<Node, Boolean>();
        // validateVertex(s);
        dfs(G, s);
    }

    /**
     * Computes the vertices in digraph {@code G} that are
     * connected to any of the source vertices {@code sources}.
     * @param G the graph
     * @param sources the source vertices
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     *         for each vertex {@code s} in {@code sources}
     */
    public DirectedDFS(Graph G, Iterable<Node> sources) {
        marked = new HashMap<Node, Boolean>();
        // validateVertices(sources);
        for(Node n : sources) {
            if(!marked.get(n)) dfs(G, n);
        }
    }

    private void dfs(Graph G, Node v) {
        count++;
        marked.put(v, true);
        for(Edge e : v.getNeighbors()) {
            Node neighbor = e.getNeighbor(v);
            if(!marked.get(neighbor)) dfs(G, neighbor);
        }
    }

    /**
     * Is there a directed path from the source vertex (or any
     * of the source vertices) and vertex {@code v}?
     * @param  v the vertex
     * @return {@code true} if there is a directed path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean marked(Node v) {
        //validateVertex(v);
        return marked.get(v);
    }

    /**
     * Returns the number of vertices reachable from the source vertex
     * (or source vertices).
     * @return the number of vertices reachable from the source vertex
     *   (or source vertices)
     */
    public int count() {
        return count;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
   /* private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }*/

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int V = marked.size();
        for (int v : vertices) {
            if (v < 0 || v >= V) {
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
            }
        }
    }


    /**
     * Unit tests the {@code DirectedDFS} data type.
     *
     * @param args the command-line arguments
     */
    /*public static void main(String[] args) {

        // read in digraph from command-line argument
        In in = new In(args[0]);
        Digraph G = new Digraph(in);

        // read in sources from command-line arguments
        Bag<Integer> sources = new Bag<Integer>();
        for (int i = 1; i < args.length; i++) {
            int s = Integer.parseInt(args[i]);
            sources.add(s);
        }

        // multiple-source reachability
        DirectedDFS dfs = new DirectedDFS(G, sources);

        // print out vertices reachable from sources
        for (int v = 0; v < G.V(); v++) {
            if (dfs.marked(v)) StdOut.print(v + " ");
        }
        StdOut.println();
    }*/

}

