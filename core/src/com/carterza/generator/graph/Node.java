package com.carterza.generator.graph;

import com.carterza.generator.spacecraft.Condition;
import com.carterza.generator.spacecraft.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private ArrayList<Edge> neighborhood;
    private ComparablePoint2i id;
    private int level;
    private int distance;
    private Symbol item;
    private Condition precondition;
    private Node parent;
    private List<Node> children;
    private double intensity;

    public boolean visited;

    /**
     *
     * @param id The unique id associated with this Vertex
     */
    public Node(ComparablePoint2i id){
        this.id = id;
        this.neighborhood = new ArrayList<Edge>();
        this.children = new ArrayList<Node>();
    }


    /**
     * This method adds an Edge to the incidence neighborhood of this graph iff
     * the edge is not already present.
     *
     * @param edge The edge to add
     */
    public void addNeighbor(Edge edge){
        if(this.neighborhood.contains(edge)){
            return;
        }

        this.neighborhood.add(edge);
    }


    /**
     *
     * @param other The edge for which to search
     * @return true iff other is contained in this.neighborhood
     */
    public boolean containsNeighbor(Edge other){
        return this.neighborhood.contains(other);
    }

    /**
     *
     * @param index The index of the Edge to retrieve
     * @return Edge The Edge at the specified index in this.neighborhood
     */
    public Edge getNeighbor(int index){
        return this.neighborhood.get(index);
    }


    /**
     *
     * @param index The index of the edge to remove from this.neighborhood
     * @return Edge The removed Edge
     */
    Edge removeNeighbor(int index){
        return this.neighborhood.remove(index);
    }

    /**
     *
     * @param e The Edge to remove from this.neighborhood
     */
    public void removeNeighbor(Edge e){
        this.neighborhood.remove(e);
    }


    /**
     *
     * @return int The number of neighbors of this Vertex
     */
    public int getNeighborCount(){
        return this.neighborhood.size();
    }


    /**
     *
     * @return String The label of this Vertex
     */
    public ComparablePoint2i getId(){
        return this.id;
    }


    /**
     *
     * @return String A String representation of this Vertex
     */
    public String toString(){
        return "Vertex " + id;
    }

    /**
     *
     * @return The hash code of this Vertex's label
     */
    public int hashCode(){
        return this.id.hashCode();
    }

    /**
     *
     * @param other The object to compare
     * @return true iff other instanceof Vertex and the two Vertex objects have the same label
     */
    public boolean equals(Object other){
        if(!(other instanceof Node)){
            return false;
        }

        Node v = (Node)other;
        return this.id.equals(v.id);
    }

    /**
     *
     * @return ArrayList<Edge> A copy of this.neighborhood. Modifying the returned
     * ArrayList will not affect the neighborhood of this Vertex
     */
    public ArrayList<Edge> getNeighbors(){
        return new ArrayList<Edge>(this.neighborhood);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Symbol getItem() {
        return item;
    }

    public void setItem(Symbol item) {
        this.item = item;
    }

    public boolean hasItem() {
        return this.item != null;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void setPrecondition(Condition cond) {
        this.precondition = cond;
    }

    public Condition getPrecondition() {
        return this.precondition;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getIntensity() {
        return intensity;
    }
}
