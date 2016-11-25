package com.carterza.generator.graph.algorithm;

import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CC {
    // private boolean[] marked;   // marked[v] = has vertex v been marked?
    private Map<Node, Boolean> marked;
    private Map<Node, Integer> id;
    // private Map<Node, Integer> size;
    // private int[] id;           // id[v] = id of connected component containing v
    private int[] size;         // size[id] = number of vertices in given component
    private int count;          // number of connected components

    /**
     * Computes the connected components of the undirected graph {@code G}.
     *
     * @param G the undirected graph
     */
    public CC(Graph G) {
        marked = new HashMap<Node, Boolean>();
        for(Node n : G.getNodes().values()) {
            marked.put(n, false);
        }
        id = new HashMap<Node, Integer>();
        size = new int[G.getNodes().values().size()];
        // size = new HashMap<Node, Integer>();
        Iterator<Node> nodeIterator = G.getNodes().values().iterator();
        while(nodeIterator.hasNext()) {
            Node n = nodeIterator.next();
            if(!marked.get(n)) {
                dfs(G, n);
                count++;
            }
        }
    }

    /**
     * Computes the connected components of the edge-weighted graph {@code G}.
     *
     * @param G the edge-weighted graph
     */
/*    public CC(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
                count++;
            }
        }
    }*/

    // depth-first search for a Graph
    private void dfs(Graph G, Node v) {
        marked.put(v, true);
        id.put(v, count);
        size[count]++;
        for(Edge e : v.getNeighbors()) {
            Node neighbor = e.getNeighbor(v);
            if(!marked.get(neighbor)) {
                dfs(G, neighbor);
            }
        }
    }


    /**
     * Returns the component id of the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the component id of the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int id(Node v) {
        // validateVertex(v);
        return id.get(v);
    }

    /**
     * Returns the number of vertices in the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the number of vertices in the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int size(Node v) {
        //validateVertex(v);
        return size[id.get(v)];
    }

    /**
     * Returns the number of connected components in the graph {@code G}.
     *
     * @return the number of connected components in the graph {@code G}
     */
    public int count() {
        return count;
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean connected(Node v, Node w) {
        // validateVertex(v);
        // validateVertex(w);
        return id.get(v).equals(id.get(w));
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     * @deprecated Replaced by {@link #connected(int, int)}.
     */
    @Deprecated
    public boolean areConnected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return id.get(v).equals(id.get(w));
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.size();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code CC} data type.
     *
     * @param args the command-line arguments
     */
    /*public static void main(String[] args) {
        In in = new In(args[0]);
        Graph G = new Graph(in);
        CC cc = new CC(G);

        // number of connected components
        int m = cc.count();
        StdOut.println(m + " components");

        // compute list of vertices in each connected component
        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
        for (int i = 0; i < m; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < G.V(); v++) {
            components[cc.id(v)].enqueue(v);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int v : components[i]) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
    }*/
}