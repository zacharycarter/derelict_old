package com.carterza.generator.graph.algorithm;

import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;

import java.util.*;

public class TarjanSCC {

    Graph graph;
    Map<Node, Boolean> visited = new HashMap<Node, Boolean>();
    Stack<Node> stack;
    int time;
    Map<Node, Integer> lowlink = new HashMap<Node, Integer>();
    List<List<Node>> components;

    public List<List<Node>> scc(Graph graph) {
        int n = graph.getNodes().size();
        this.graph = graph;

        for(Node node : graph.getNodes().values()) {
            visited.put(node, false);
        }

        stack = new Stack<Node>();
        time = 0;
        components = new ArrayList<List<Node>>();

        for(Node node : graph.getNodes().values()) {
            if(!visited.get(node)) {
                dfs(node);
            }
        }

        return components;
    }

    void dfs(Node u) {
        lowlink.put(u, time++);
        visited.put(u, true);
        stack.add(u);
        boolean isComponentRoot = true;

        for(Edge e : u.getNeighbors()) {
            Node neighbor = e.getNeighbor(u);
            if(!visited.get(neighbor)) {
                dfs(neighbor);
            }
            if(lowlink.get(u) > lowlink.get(neighbor)) {
                lowlink.put(u, lowlink.get(neighbor));
                isComponentRoot = false;
            }
        }

        if (isComponentRoot) {
            List<Node> component = new ArrayList<Node>();
            while (true) {
                Node x = stack.pop();
                component.add(x);
                lowlink.put(x, Integer.MAX_VALUE);
                if (x == u)
                    break;
            }
            components.add(component);
        }
    }

    // Usage example
    /*public static void main(String[] args) {
        List<Node>[] g = new List[3];
        for (int i = 0; i < g.length; i++)
            g[i] = new ArrayList<>();

        g[2].add(0);
        g[2].add(1);
        g[0].add(1);
        g[1].add(0);

        List<List<Integer>> components = new SCCTarjan().scc(g);
        System.out.println(components);
    }*/
}