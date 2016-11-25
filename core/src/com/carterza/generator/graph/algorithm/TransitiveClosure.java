package com.carterza.generator.graph.algorithm;

import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;

import java.util.HashMap;
import java.util.Map;

public class TransitiveClosure {
    //private DirectedDFS[] tc;  // tc[v] = reachable from v
    private Map<Node, DirectedDFS> tc;

    /**
     * Computes the transitive closure of the digraph {@code G}.
     * @param G the digraph
     */
    public TransitiveClosure(Graph G) {
        tc = new HashMap<Node, DirectedDFS>();
        for(Node n : G.getNodes().values()) {
            tc.put(n, new DirectedDFS(G,n));
        }
    }

    /**
     * Is there a directed path from vertex {@code v} to vertex {@code w} in the digraph?
     * @param  v the source vertex
     * @param  w the target vertex
     * @return {@code true} if there is a directed path from {@code v} to {@code w},
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean reachable(Node n, Node w) {
        // validateVertex(v);
        // validateVertex(w);
        return tc.get(n).marked(w);
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = tc.size();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code TransitiveClosure} data type.
     *
     * @param args the command-line arguments
     */
    /*public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);

        TransitiveClosure tc = new TransitiveClosure(G);

        // print header
        StdOut.print("     ");
        for (int v = 0; v < G.V(); v++)
            StdOut.printf("%3d", v);
        StdOut.println();
        StdOut.println("--------------------------------------------");

        // print transitive closure
        for (int v = 0; v < G.V(); v++) {
            StdOut.printf("%3d: ", v);
            for (int w = 0; w < G.V(); w++) {
                if (tc.reachable(v, w)) StdOut.printf("  T");
                else                    StdOut.printf("   ");
            }
            StdOut.println();
        }
    }*/

}
