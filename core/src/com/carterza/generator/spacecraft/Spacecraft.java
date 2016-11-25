package com.carterza.generator.spacecraft;

import com.badlogic.gdx.graphics.Pixmap;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.spacecraft.Door;
import com.carterza.generator.spacecraft.GridMap;
import com.carterza.generator.spacecraft.Room;
import com.stewsters.util.math.Point2i;
import net.bytten.metazelda.Dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zachcarter on 10/6/16.
 */
public class Spacecraft {
    public GridMap gridMap;

    public Pixmap minimap;
    public Pixmap shipTexture;

    public ArrayList<Room> rooms = new ArrayList<Room>();

    public Graph roomGraph = new Graph();
    public Graph originalRoomGraph = new Graph();

    public HashMap<net.bytten.metazelda.Room, Room> roomMap = new HashMap<net.bytten.metazelda.Room, Room>();

    public int getDoorCount() {
        int doorCount = 0;
        for(Room r : rooms) {
            doorCount += r.getDoors().size();
        }
        return  doorCount;
    }

   /* public void linkOneWay(Room room1, Room room2) {
        linkOneWay(room1, room2, null);
    }

    public void link(Room room1, Room room2) {
        link(room1, room2, null);
    }

    public void linkOneWay(Room room1, Room room2, Symbol cond) {
        assert rooms.contains(room1) && rooms.contains(room2);
        room1.setEdge(room2.id, cond);
    }

    public void link(Room room1, Room room2, Symbol cond) {
        linkOneWay(room1, room2, cond);
        linkOneWay(room2, room1, cond);
    }

    public boolean roomsAreLinked(Room room1, Room room2) {
        return room1.getEdge(room2.id) != null ||
                room2.getEdge(room1.id) != null;
    }*/

    public Room findRoom(Point2i center) {
        for(Room room : rooms) {
            if(room.center().equals(center)) {
                return room;
            }
        }
        return null;
    }

    public Room findRoomContainingPoint(Point2i point) {
        for(Room room : rooms) {
            if(room.contains(point.x, point.y)) {
                return room;
            }
        }
        return null;
    }

    public Room findStart() {
        for (Room room: rooms) {
            if (room.isStart()) return room;
        }
        return null;
    }

    public Room findRoomContainingSymbol(int symbol) {
        Room r = null;
        for(Room room: rooms) {
            if(room.getItem() != null) {
                if(room.getItem().getValue() == symbol) {
                    r = room;
                }
            }
        }
        return r;
    }

    public Room findRoomContainingSymbol(String symbolName) {
        Room r = null;
        for(Room room: rooms) {
            if(room.getItem() != null) {
                if(room.getItem().toString().equals(symbolName)) {
                    r = room;
                }
            }
        }
        return r;
    }

    public Room get(int id) {
        Room roomFound = new Room(0, 0, 0, 0);
        for(Room room: rooms) {
            if(room.id == id)
                roomFound = room;
        }
        return roomFound;
    }

    public Room findGoal() {
        for (Room room: rooms) {
            if (room.isGoal()) return room;
        }
        return null;
    }

    public Room findBoss() {
        for (Room room: rooms) {
            if (room.isBoss()) return room;
        }
        return null;
    }

    public Room findSwitch() {
        for (Room room: rooms) {
            if (room.getItem() != null)
                if(room.getItem().isSwitch()) return room;
        }
        return null;
    }

    public void removeDoorFromRooms(Door door) {
        for (Room r : rooms) {
            if(r.getDoors().contains(door))
                r.getDoors().remove(door);
        }
    }/*

    public List<String> findRoomIdsWithPrecondition(String precondition) {
        List<String> roomIdsWithPrecondition = new ArrayList<String>();
        for(Room r : rooms) {
            if(r.getPrecond() != null) {
                if(r.getPrecond().toString().contains(precondition))
                    roomIdsWithPrecondition.add(r.id);
            }
        }
        return roomIdsWithPrecondition;
    }*/

    public Door findDoor(Point2i point2i) {
        for(Room room : rooms) {
            for(Door door : room.getDoors()) {
                if(door.getX() == point2i.x && door.getY() == point2i.y) {
                    return door;
                }
            }
        }
        return null;
    }
}
