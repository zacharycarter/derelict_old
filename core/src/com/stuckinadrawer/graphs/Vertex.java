package com.stuckinadrawer.graphs;

import java.io.Serializable;

public class Vertex implements Serializable{
    private String type;
    private final int id;
    protected int x, y;
    public boolean notPositioned = true;
    public float forceX, forceY = 0;

    private int morphism = -1;

    public boolean marked = false;

    public Vertex(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }


    public int getMorphism() {
        return morphism;
    }

    public void setMorphism(int morphism) {
        this.morphism = morphism;
    }

    public String getDescription(){
        return getId()+":"+getType()+":"+getMorphism();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        notPositioned = false;
    }

    public void applyForce(double stepSize) {
        this.x += forceX * stepSize;
        this.y += forceY * stepSize;
    }

    public void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (id != vertex.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id;
        return id;
    }

    @Override
    public String toString(){
        return getDescription();
    }
}
