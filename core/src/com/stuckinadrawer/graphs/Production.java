package com.stuckinadrawer.graphs;

import java.io.Serializable;

public class Production implements Serializable{
    private Graph left;
    private Graph right;
    private String name;

    public Production(Graph left, Graph right) {
        this.left = left;
        this.right = right;
    }

    public Graph getLeft() {
        return left;
    }

    public void setLeft(Graph left) {
        this.left = left;
    }

    public Graph getRight() {
        return right;
    }

    public void setRight(Graph right) {
        this.right = right;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String theString = name + ": \n";
        theString += "Left side: \n";
        theString += left.toString();
        theString += "Right side: \n";
        theString += right.toString();
        return theString;
    }
}
