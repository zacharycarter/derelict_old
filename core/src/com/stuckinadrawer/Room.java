package com.stuckinadrawer;

import com.badlogic.gdx.math.Rectangle;
import com.stuckinadrawer.graphs.Vertex;
import net.bytten.gameutil.Vec2I;

import java.util.ArrayList;

public class Room {
    public int x, y, width, height;
    public int id;
    public ArrayList<Vertex> elements = new ArrayList<Vertex>();
    public int incomingRoomID = -1;

    public Room() {
        this.width = 3;
        this.height = 3;
    }

    public void expand() {
        this.width++;
        this.height++;

    }

    public boolean contains(Vec2I point) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        if(rectangle.contains(point.x, point.y)) return true;
        return false;
    }

    @Override
    public String toString() {
        return "x : " + x + " y : " + y + " width : " + width + " height : " + height;
    }
}