package com.carterza.generator.spacecraft;

import com.carterza.generator.graph.*;
import com.carterza.generator.graph.Edge;

import java.util.Objects;

/**
 * Created by zachcarter on 11/5/16.
 */
public class Door implements Comparable<Door> {
    private int x;
    private int y;
    private Room fromRoom;
    private Room toRoom;
    private Edge edge;

    public Door(Edge edge, int x, int y, Room fromRoom, Room toRoom) {
        this.edge = edge;
        this.x = x;
        this.y = y;
        this.fromRoom = fromRoom;
        this.toRoom = toRoom;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof Door)
        {
            isEqual = (
                    this.x == ((Door) object).x
                    && this.y == ((Door) object).y
                    && this.fromRoom == ((Door) object).fromRoom
                    && this.toRoom == ((Door) object).toRoom
            );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, fromRoom, toRoom);
    }

    @Override
    public int compareTo(Door o) {
        return (this.x == o.x && this.y == o.y && this.fromRoom == fromRoom && this.toRoom == toRoom) ? 1: 0;
    }

    @Override
    public String toString() {
        return "Door - From Room : " + fromRoom.id + " To Room : " + toRoom.id + " X: " + x + " Y: " + y;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public Room getFromRoom() {
        return this.fromRoom;
    }
    public Room getToRoom() {
        return this.toRoom;
    }

    public void setToRoom(Room toRoom) {
        this.toRoom = toRoom;
    }

    public Edge getEdge() {
        return edge;
    }
}
