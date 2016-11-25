package com.stuckinadrawer;

import com.stuckinadrawer.FileReader;
import com.stuckinadrawer.Point;
import com.stuckinadrawer.graphs.Grammar;
import com.stuckinadrawer.graphs.GrammarManager;
import com.stuckinadrawer.graphs.Graph;
import com.stuckinadrawer.graphs.Vertex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class LevelGeneratorTest {

    private Tile[][] level;
    private ArrayList<Room> rooms;
    private Graph levelGraph;
    private int fieldSize = 10;

    private int levelWidth;
    private int levelHeight;

    private LevelGeneratorTest() {
        levelGraph = createLevelGraph();
        rooms = createRooms();
        Grid grid = new Grid();

        int[][] roomGrid = grid.createRoomGrid(rooms);

        levelWidth = roomGrid.length * fieldSize;
        levelHeight = roomGrid[0].length * fieldSize;

        level = initEmptyLevel();


        for (Room r : rooms) {
            putRoomInMap(r);
        }

        makeCorridors();

        new Renderer(level);
    }

    public static void main(String[] arg) {
        new LevelGeneratorTest();
    }

    private void makeCorridors() {
        Pathfinder pathfinder = new Pathfinder(level);
        for (Room r : rooms) {
            if (r.id == 0) continue;
            Room connectedRoom = rooms.get(r.incomingRoomID);
            LinkedList<Point> path = pathfinder.findPath(r, connectedRoom);
            for (Point pos : path) {
                Tile t = level[pos.getX()][pos.getY()];
                if (t.tileType == TileType.WALL || t.tileType == TileType.EMPTY) {
                    level[pos.getX()][pos.getY()].tileType = TileType.CORRIDOR;
                }
            }
            surroundCorridorWithWalls();
        }


    }

    private void surroundCorridorWithWalls() {
        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                if (level[x][y].tileType == TileType.CORRIDOR) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        for (int yy = y - 1; yy <= y + 1; yy++) {
                            if (xx < 0 || xx > levelWidth || yy < 0 || yy > levelHeight) continue;
                            if (level[xx][yy].tileType == TileType.EMPTY) {
                                level[xx][yy].tileType = TileType.WALL;
                            }
                        }
                    }
                }
            }
        }

    }

    private void putRoomInMap(Room r) {
        System.out.println("mapping: " + r.id + "on pos " + r.x + " " + r.y);

        r.x = r.x * fieldSize + 2;
        r.y = r.y * fieldSize + 2;
        r.width = fieldSize - 4;
        r.height = fieldSize - 4;


        for (int x = r.x; x <= r.x + r.width; x++) {
            for (int y = r.y; y <= r.y + r.height; y++) {

                if (x < 0 || x >= levelWidth || y < 0 || y >= levelHeight) continue;

                if (x == r.x || x == r.x + r.width || y == r.y || y == r.y + r.height) {
                    level[x][y] = new Tile(TileType.WALL);
                } else {
                    level[x][y] = new Tile(TileType.ROOM);
                }

                level[x][y].setRoomID(r.id);
            }
        }
        int i = 0;
        for (Vertex v : r.elements) {
            TileType tileType = TileType.EMPTY;
            if (v.getType().equals("start")) {
                tileType = TileType.ENTRANCE;
            }
            if (v.getType().equals("exit")) {
                tileType = TileType.EXIT;
            }
            if (v.getType().equals("opponent")) {
                tileType = TileType.OPPONENT;
            }
            if (v.getType().equals("boss")) {
                tileType = TileType.BOSS;
            }
            if (v.getType().equals("key")) {
                tileType = TileType.KEY;
            }
            if (v.getType().equals("lock")) {
                tileType = TileType.LOCK;
            }
            if (v.getType().equals("trap")) {
                tileType = TileType.TRAP;
            }
            if (v.getType().equals("chest")) {
                tileType = TileType.CHEST;
            }
            if (v.getType().equals("buff")) {
                tileType = TileType.BUFF;
            }
            if (r.x + 2 + i < 0 || r.x + 2 + i >= levelWidth || r.y + 2 < 0 || r.y + 2 >= levelHeight) continue;
            level[r.x + 2 + i][r.y + 2].tileType = tileType;


            i++;
        }

    }

    private ArrayList<Room> createRooms() {
        //uses morphism field to assign groups;
        //same group = in same room

        ArrayList<Room> rooms = new ArrayList<Room>();

        int groupID = 0;

        Stack<Vertex> nextVertices = new Stack<Vertex>();

        Vertex currentVertex = findStartVertex();
        currentVertex.setMorphism(groupID);
        Room room = new Room();
        room.id = groupID;
        room.elements.add(currentVertex);
        room.expand();
        rooms.add(room);

        groupID++;

        nextVertices.addAll(levelGraph.getOutgoingNeighbors(currentVertex));
        System.out.println("### GROUPING ###");
        int sameID = 0;
        while (!nextVertices.empty()) {
            currentVertex = nextVertices.pop();
            currentVertex.setMorphism(groupID);


            if (groupID >= rooms.size()) {
                room = new Room();
                room.id = groupID;
                rooms.add(room);
            } else {
                room = rooms.get(groupID);
            }

            room.expand();
            room.elements.add(currentVertex);

            System.out.println("\n" + currentVertex.getDescription() + " was assigned");
            HashSet<Vertex> outgoingNeighbours = levelGraph.getOutgoingNeighbors(currentVertex);
            System.out.println("has " + outgoingNeighbours.size() + " outgoing neighbours");
            if (outgoingNeighbours.size() > 1) {
                System.out.println("added all, new id");

                sameID = 0;
                for (Vertex v : outgoingNeighbours) {
                    if (v.getType().equals("key")) {
                        room.expand();
                        room.elements.add(v);
                    } else {
                        nextVertices.push(v);
                    }

                }
                groupID++;
            } else if (outgoingNeighbours.size() == 1) {
                System.out.println("added one");
                for (Vertex v : outgoingNeighbours) {
                    nextVertices.push(v);
                    if (v.getType().equals("lock") || v.getType().equals("exit") || sameID >= 2) {
                        groupID++;
                        sameID = 0;
                    } else {
                        sameID++;
                    }
                }
            } else {
                //no outgoing neighbours
                System.out.println("added none, new id");
                groupID++;
                sameID = 0;
            }


        }
        System.out.println("end");

        for (Room r : rooms) {
            String elements = "";
            for (Vertex v : r.elements) {
                elements += v.getType() + " ";
                for (Vertex incomingNeighbour : levelGraph.getIncomingNeighbors(v)) {
                    if (incomingNeighbour.getMorphism() != r.id) {
                        r.incomingRoomID = incomingNeighbour.getMorphism();
                        break;
                    }
                }
            }
            System.out.println("ROOM " + r.id + " contains: " + elements + "; incoming: room " + r.incomingRoomID);
        }

        return rooms;

    }

    private Vertex findStartVertex() {
        // get starting vertex
        for (Vertex v : levelGraph.getVertices()) {
            if (v.getType().equals("start")) {
                return v;
            }
        }
        return null;
    }

    private Tile[][] initEmptyLevel() {
        Tile[][] level = new Tile[levelWidth][levelHeight];

        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                level[x][y] = new Tile(TileType.EMPTY);
            }
        }
        return level;
    }

    private Graph createLevelGraph() {
        GrammarManager grammarManager = new GrammarManager();
        grammarManager.setGrammar(loadGrammar());
        grammarManager.applyAllProductions();
        return grammarManager.getCurrentGraph();
    }

    private Grammar loadGrammar() {
        FileReader fr = new FileReader();
        try {
            return fr.loadGrammar("grammar1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Grammar();
    }


}
