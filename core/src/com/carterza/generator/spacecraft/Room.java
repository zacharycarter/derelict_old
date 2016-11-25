package com.carterza.generator.spacecraft;

import com.stewsters.util.math.Point2i;
import com.stewsters.util.math.geom.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Room extends Rect {

    public int id;
    protected Condition precond;
    protected Symbol item;
    protected List<Edge> edges;
    protected double intensity;
    private Room parent;
    private List<Room> children;
    private List<Door> doors;
    public int width;
    public int height;

    public Room(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
        this.width = x2-x1;
        this.height = y2-y1;
        this.children = new ArrayList<Room>();
        this.intensity = 0.0;
        this.edges = new ArrayList<Edge>();
        this.doors = new ArrayList<Door>();
    }

    public Room(int id, int x1, int y1, int x2, int y2, Symbol item, Condition precond) {
        super(x1, y1, x2, y2);
        this.id = id;
        this.children = new ArrayList<Room>();
        this.precond = precond;
        this.intensity = 0.0;
        this.item = item;
        this.edges = new ArrayList<Edge>();
        this.doors = new ArrayList<Door>();
    }

    public int area() {
        return this.width * this.height;
    }


    public Room getParent() {
        return parent;
    }

    public void setParent(Room parent) {
        this.parent = parent;
    }

    public List<Room> getChildren() {
        return children;
    }

    public void setChildren(List<Room> children) {
        this.children = children;
    }

    /**
     * @return  the item contained in the Room, or null if there is none
     */
    public Symbol getItem() {
        return item;
    }

    /**
     * @param item  the item to place in the Room
     */
    public void setItem(Symbol item) {
        this.item = item;
    }

    /**
     * @return the precondition for this Room
     * @see Condition
     */
    public Condition getPrecond() {
        return precond;
    }

    /**
     * @param precond   the precondition to set this Room's to
     * @see Condition
     */
    public void setPrecond(Condition precond) {
        this.precond = precond;
    }

    /**
     * Gets the array of {@link Edge} slots this Room has. There is one slot
     * for each compass {@link Direction}. Non-null slots in this array
     * represent links between this Room and adjacent Rooms.
     *
     * @return the array of Edges
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Gets the Edge object for a link in a given direction.
     *
     * @param d the compass {@link Direction} of the Edge for the link from this
     *          Room to an adjacent Room
     * @return  the {@link Edge} for the link in the given direction, or null if
     *          there is no link from this Room in the given direction
     */
    public Edge getEdge(int targetRoomId) {
        for (Edge e: edges) {
            if (e.getTargetRoomId() == targetRoomId)
                return e;
        }
        return null;
    }

    public Edge setEdge(int targetRoomId, Symbol symbol) {
        Edge e = getEdge(targetRoomId);
        if (e != null) {
            e.symbol = symbol;
        } else {
            e = new Edge(targetRoomId, symbol);
            edges.add(e);
        }
        return e;
    }

    /**
     * @return the intensity of the Room
     * @see Room
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * @param intensity the value to set the Room's intensity to
     * @see Room
     */
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * Gets the number of Rooms this Room is linked to.
     *
     * @return  the number of links
     */
    public int linkCount() {
        return edges.size();
    }

    /**
     * @return whether this room is the entry to the dungeon.
     */
    public boolean isStart() {
        return item != null && item.isStart();
    }

    /**
     * @return whether this room is the goal room of the dungeon.
     */
    public boolean isGoal() {
        return item != null && item.isGoal();
    }

    /**
     * @return whether this room contains the dungeon's boss.
     */
    public boolean isBoss() {
        return item != null && item.isBoss();
    }

    public List<Door> getDoors() {
        return doors;
    }

    public Door findDoorWithCoordinates(Point2i coordinates) {
        Door door = null;
        for(Door d : doors) {
            if(d.getX() == coordinates.x && d.getY() == coordinates.y) {
                door = d;
            }

        }
        return door;
    }

    public Door findDoorToRoom(Room room, Room toRoom) {
        Door door = null;
        for(Door d : room.getDoors()) {
            if(d.getToRoom().equals(toRoom))
                door = d;
        }
        return door;
    }

    public Point2i findRandomTileInRoom() {
        int minX = Math.min(this.x1, this.x2);
        int minY = Math.min(this.y1, this.y2);
        int maxX = Math.max(this.x1, this.x2);
        int maxY = Math.max(this.y1, this.y2);
        System.out.println("X1 : " + this.x1 + " X2 : " + this.x2 + " Y1: " + this.y1 + " Y2: " + this.y2);
        return new Point2i(ThreadLocalRandom.current().nextInt(minX+1, maxX-1), ThreadLocalRandom.current().nextInt(minY+1, maxY-1));
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof Room)
        {
            isEqual = (
                    //this.id == ((Room) object).id
                            this.x1 == ((Room) object).x1
                            && this.x2 == ((Room) object).x2
                            && this.y1 == ((Room) object).y1
                            && this.y2 == ((Room) object).y2
            );
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, x2, y1, y2);
    }
}

