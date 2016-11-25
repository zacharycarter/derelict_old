package com.carterza.generator.graph;

import com.carterza.generator.spacecraft.Condition;
import com.carterza.generator.spacecraft.Symbol;

import java.util.*;

public class Graph {

    private HashMap<ComparablePoint2i, Node> nodes;
    private HashMap<Integer, Edge> edges;
    private List<Node> nodesFound;
    private TreeMap<Integer, List<Node>> nodesSortedByLevel;
    private TreeMap<Integer, List<Node>> nodesSortedByDistance;
    private Map<Node, Boolean> marked;
    private Map<Node, Integer> id;
    private int[] size;
    private int count;

    private static final double FLOATING_POINT_EPSILON = 1E-12;

    public Graph(){
        this.nodes = new HashMap<ComparablePoint2i, Node>();
        this.edges = new HashMap<Integer, Edge>();
        nodesSortedByLevel = new TreeMap<Integer, List<Node>>();
        nodesSortedByDistance = new TreeMap<Integer, List<Node>>();
        nodesFound = new ArrayList<Node>();
    }

    public Graph(ArrayList<Node> nodes){
        this.nodes = new HashMap<ComparablePoint2i, Node>();
        this.edges = new HashMap<Integer, Edge>();

        for(Node n: nodes){
            this.nodes.put(n.getId(), n);
        }

    }

    /**
     * This method adds am edge between Vertices one and two
     * of weight 1, if no Edge between these Vertices already
     * exists in the Graph.
     *
     * @param one The first vertex to add
     * @param two The second vertex to add
     * @return true iff no Edge relating one and two exists in the Graph
     */
    public boolean addEdge(Node one, Node two){
        return addEdge(one, two, 1);
    }


    /**
     * Accepts two vertices and a weight, and adds the edge
     * ({one, two}, weight) iff no Edge relating one and two
     * exists in the Graph.
     *
     * @param one The first Vertex of the Edge
     * @param two The second Vertex of the Edge
     * @param weight The weight of the Edge
     * @return true iff no Edge already exists in the Graph
     */
    public boolean addEdge(Node one, Node two, int weight){
        if(one.equals(two)){
            return false;
        }

        //ensures the Edge is not in the Graph
        Edge e = new Edge(one, two, weight);
        if(edges.containsKey(e.hashCode())){
            return false;
        }

        //and that the Edge isn't already incident to one of the vertices
        else if(one.containsNeighbor(e) || two.containsNeighbor(e)){
            return false;
        }

        edges.put(e.hashCode(), e);
        one.addNeighbor(e);
        two.addNeighbor(e);
        return true;
    }

    public boolean addEdge(Node one, Node two, Symbol symbol){
        if(one.equals(two)){
            return false;
        }

        //ensures the Edge is not in the Graph
        Edge e = new Edge(one, two, 1);
        if(edges.containsKey(e.hashCode())){
            return false;
        }

        //and that the Edge isn't already incident to one of the vertices
        else if(one.containsNeighbor(e) || two.containsNeighbor(e)){
            return false;
        }

        e.setSymbol(symbol);

        edges.put(e.hashCode(), e);
        one.addNeighbor(e);
        two.addNeighbor(e);
        return true;
    }

    /**
     *
     * @param e The Edge to look up
     * @return true iff this Graph contains the Edge e
     */
    public boolean containsEdge(Edge e){
        if(e.getOne() == null || e.getTwo() == null){
            return false;
        }

        return this.edges.containsKey(e.hashCode());
    }


    /**
     * This method removes the specified Edge from the Graph,
     * including as each vertex's incidence neighborhood.
     *
     * @param e The Edge to remove from the Graph
     * @return Edge The Edge removed from the Graph
     */
    public Edge removeEdge(Edge e){
        e.getOne().removeNeighbor(e);
        e.getTwo().removeNeighbor(e);
        return this.edges.remove(e.hashCode());
    }

    /**
     *
     * @param vertex The Vertex to look up
     * @return true iff this Graph contains vertex
     */
    public boolean containsVertex(Node vertex){
        return this.nodes.get(vertex.getId()) != null;
    }


    public Node getNode(ComparablePoint2i id){
        return nodes.get(id);
    }

    /**
     * This method adds a Vertex to the graph. If a Vertex with the same label
     * as the parameter exists in the Graph, the existing Vertex is overwritten
     * only if overwriteExisting is true. If the existing Vertex is overwritten,
     * the Edges incident to it are all removed from the Graph.
     *
     * @param node
     * @param overwriteExisting
     * @return true iff vertex was added to the Graph
     */
    public boolean addNode(Node node, boolean overwriteExisting){
        Node current = this.nodes.get(node.getId());
        if(current != null){
            if(!overwriteExisting){
                return false;
            }

            while(current.getNeighborCount() > 0){
                this.removeEdge(current.getNeighbor(0));
            }
        }


        nodes.put(node.getId(), node);
        return true;
    }


    public Node removeVertex(ComparablePoint2i id){
        Node v = nodes.remove(id);

        while(v.getNeighborCount() > 0){
            this.removeEdge(v.getNeighbor((0)));
        }

        return v;
    }

    /**
     *
     * @return Set<String> The unique labels of the Graph's Vertex objects
     */
    public Set<ComparablePoint2i> vertexKeys(){
        return this.nodes.keySet();
    }

    /**
     *
     * @return Set<Edge> The Edges of this graph
     */
    public Set<Edge> getEdges(){
        return new HashSet<Edge>(this.edges.values());
    }

    public TreeMap<Integer, List<Node>> getNodesSortedByLevel() {
        return this.nodesSortedByLevel;
    }

    public TreeMap<Integer, List<Node>> getNodesSortedByDistance() {
        return this.nodesSortedByDistance;
    }

    public void setNodesSortedByLevel(TreeMap<Integer, List<Node>> nodesSortedByLevel) {
        this.nodesSortedByLevel = nodesSortedByLevel;
    }

    public void bfs(Node rootNode){
        initializeNodes();
        Queue q = new LinkedList();
        q.add(rootNode);
        rootNode.visited=true;
        int parentDistance = 0;
        int parentLevel = 0;
        rootNode.setLevel(parentLevel);
        rootNode.setDistance(parentDistance);
        List<Node> nodeList = new ArrayList<Node>();
        nodeList.add(rootNode);
        nodesSortedByDistance.put(0, nodeList);
        nodesSortedByLevel.put(0, nodeList);
        nodesFound.add(rootNode);
        while(!q.isEmpty()){
            Node n = (Node)q.poll();
            parentLevel = n.getLevel();
            parentDistance = n.getDistance();
            for(Edge edge : n.getNeighbors()){
                Node adj = edge.getNeighbor(n);
                if(!adj.visited){
                    int distance = parentDistance+edge.getWeight();
                    int level = parentLevel + 1;
                    adj.setLevel(level);
                    adj.setDistance(distance);
                    adj.visited=true;
                    q.add(adj);
                    nodesFound.add(adj);
                    if(nodesSortedByDistance.get(distance) == null) {
                        nodeList = new ArrayList<Node>();
                        nodeList.add(adj);
                        nodesSortedByDistance.put(distance, nodeList);
                    } else nodesSortedByDistance.get(distance).add(adj);

                    if(nodesSortedByLevel.get(level) == null) {
                        nodeList = new ArrayList<Node>();
                        nodeList.add(adj);
                        nodesSortedByLevel.put(level, nodeList);
                    }  else nodesSortedByLevel.get(level).add(adj);
                }
            }
        }
    }

    private void dfs(Node v) {
        marked = new HashMap<Node, Boolean>();
        for(Node n : getNodes().values()) {
            marked.put(n, false);
        }
        id = new HashMap<Node, Integer>();
        size = new int[getNodes().values().size()];

        marked.put(v, true);
        id.put(v, count);
        size[count]++;
        for(Edge e : v.getNeighbors()) {
            Node neighbor = e.getNeighbor(v);
            if(!marked.get(neighbor)) {
                dfs(neighbor);
            }
        }
    }

    private void initializeNodes() {
        for(Node n : nodes.values()) {
            n.visited = false;
            n.setDistance(-1);
            n.setLevel(-1);
        }

        nodesSortedByDistance = new TreeMap<Integer, List<Node>>();
        nodesSortedByLevel = new TreeMap<Integer, List<Node>>();
        nodesFound = new ArrayList<Node>();
    }

    public HashMap<ComparablePoint2i, Node> getNodes() {
        return this.nodes;
    }

    public List<Node> getNodesFound() {
        return nodesFound;
    }

    public void setEdgeSymbol(Node parent, Node child, Symbol symbol) {
        assert nodes.values().contains(parent) && nodes.values().contains(child);

        for(Edge edge : parent.getNeighbors()) {
            Node neighbor = edge.getNeighbor(parent);
            if(neighbor.equals(child)) edge.setSymbol(symbol);
        }
    }
}
