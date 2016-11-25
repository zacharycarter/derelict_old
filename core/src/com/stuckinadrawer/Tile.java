package com.stuckinadrawer;

public class Tile {

    public com.stuckinadrawer.TileType tileType;
    private int roomID = -1;

    public Tile(com.stuckinadrawer.TileType tileType) {
        this.tileType = tileType;
    }


    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

}
