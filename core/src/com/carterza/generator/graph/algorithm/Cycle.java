package com.carterza.generator.graph.algorithm;

import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class Cycle {
    private Map<Node, Boolean> marked;
    private Map<Node, Node> edgeTo;
    private Map<Edge, Stack<Node>> cycle;

    /**
     * Determines whether the undirected graph {@code G} has a cycle and,
     * if so, finds such a cycle.
     *
     * @param G the undirected graph
     */
    public Cycle(Graph G) {
        if (hasSelfLoop(G)) return;
        if (hasParallelEdges(G)) return;
        marked = new HashMap<Node, Boolean>();
        for(Node n : G.getNodes().values()) {
            marked.put(n, false);
        }
        edgeTo = new HashMap<Node, Node>();
        for(Node n : G.getNodes().values()) {
            if(!marked.get(n))
                dfs(G, null, n);
        }
    }


    // does this graph have a self loop?
    // side effect: initialize cycle to be self loop
    private boolean hasSelfLoop(Graph G) {
        for(Node n : G.getNodes().values()) {
            for(Edge e : n.getNeighbors()) {
                Node neighbor = e.getNeighbor(n);
                if(n == neighbor) {
                    cycle = new HashMap<Edge, Stack<Node>>();
                    cycle.put(e, new Stack<Node>());
                    cycle.get(e).push(n);
                    cycle.get(e).push(n);
                    return true;
                }
            }
        }
        return false;
    }

    // does this graph have two parallel edges?
    // side effect: initialize cycle to be two parallel edges
    private boolean hasParallelEdges(Graph G) {
        marked = new HashMap<Node, Boolean>();
        for(Node n : G.getNodes().values()) {
            marked.put(n, false);
        }

        for(Node n : G.getNodes().values()) {
            for(Edge e : n.getNeighbors()) {
                Node neighbor = e.getNeighbor(n);
                if(marked.get(neighbor)) {
                    cycle = new HashMap<Edge, Stack<Node>>();
                    cycle.put(e, new Stack<Node>());
                    cycle.get(e).push(n);
                    cycle.get(e).push(neighbor);
                    cycle.get(e).push(n);
                    return true;
                }
                marked.put(neighbor, true);
            }

            for(Edge e : n.getNeighbors()) {
                Node neighbor = e.getNeighbor(n);
                marked.put(neighbor, false);
            }
        }
        return false;
    }

    /**
     * Returns true if the graph {@code G} has a cycle.
     *
     * @return {@code true} if the graph has a cycle; {@code false} otherwise
     */
    public boolean hasCycle() {
        return cycle != null;
    }

    /**
     * Returns a cycle in the graph {@code G}.
     * @return a cycle if the graph {@code G} has a cycle,
     *         and {@code null} otherwise
     */
    public Map<Edge, Stack<Node>> cycle() {
        return cycle;
    }

    public void setCycle(Map<Edge, Stack<Node>> cycle) {
        this.cycle = cycle;
    }

    private void dfs(Graph G, Node u, Node v) {
        marked.put(v, true);
        for(Edge e : v.getNeighbors()) {
            Node neighbor = e.getNeighbor(v);

            // short circuit if cycle already found
            if (cycle != null) return;

            if (!marked.get(neighbor)) {
                edgeTo.put(neighbor, v);
                dfs(G, v, neighbor);
            }

            // check for cycle (but disregard reverse of edge leading to v)
            else if (neighbor != u) {
                cycle = new HashMap<Edge, Stack<Node>>();
                cycle.put(e, new Stack<Node>());
                for(Node x = v; x != neighbor; x = edgeTo.get(x)) {
                    cycle.get(e).push(x);
                }
                cycle.get(e).push(neighbor);
                cycle.get(e).push(v);
            }

        }
    }

    /**
     * Unit tests the {@code Cycle} data type.
     *
     * @param args the command-line arguments
     *//*
    public static void main(String[] args) {
        In in = new In(args[0]);
        Graph G = new Graph(in);
        Cycle finder = new Cycle(G);
        if (finder.hasCycle()) {
            for (int v : finder.cycle()) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
        else {
            StdOut.println("Graph is acyclic");
        }
    }*/


}

