package com.carterza.generator.spacecraft;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.carterza.generator.exception.RetryException;
import com.carterza.generator.graph.*;
import com.carterza.generator.graph.Edge;
import com.carterza.generator.pathfind.HallwayMover2d;
import com.carterza.generator.pathfind.HallwayPathFinder;
import com.carterza.generator.pathfind.HallwayPathFinderDodgeRooms;
import com.stewsters.util.mapgen.twoDimension.MapGen2d;
import com.stewsters.util.mapgen.twoDimension.brush.DrawCell2d;
import com.stewsters.util.mapgen.twoDimension.predicate.AndPredicate2d;
import com.stewsters.util.mapgen.twoDimension.predicate.CellEquals2d;
import com.stewsters.util.mapgen.twoDimension.predicate.CellNearCell2d;
import com.stewsters.util.mapgen.twoDimension.predicate.OrPredicate2d;
import com.stewsters.util.math.Point2i;
import com.stewsters.util.noise.OpenSimplexNoise;
import com.stewsters.util.pathing.twoDimention.shared.FullPath2d;
import com.stuckinadrawer.Tile;

import java.util.ArrayList;
import java.util.Random;

public class SpacecraftGenerator {

    public final static Color transparent = new Color(0, 0, 0, 0);
    private final static Random r = new Random();
    private final static OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise();


    /**
     * Generates a ship
     *
     * @param blueprint A Blueprint that contains parameters for ship creation
     * @return A Buffered Image of the ship
     */
    public static Spacecraft generate(Blueprint blueprint) throws RetryException {

        Spacecraft spacecraft = new Spacecraft();
        spacecraft.gridMap = new GridMap(blueprint.width, blueprint.height);
        spacecraft.roomGraph = new Graph();

        generateUnderlyingStructure(blueprint, spacecraft);
        generateRooms(blueprint, spacecraft);
        generateHallways(blueprint, spacecraft);
        // generateHallwayWalls(blueprint, spacecraft);
        // paintShip(blueprint, spacecraft);

        return spacecraft;

    }

    public static Spacecraft carveHallways(Spacecraft spacecraft) throws RetryException {
        GridMap gridMap = spacecraft.gridMap;
        // Cutting paths
        HallwayPathFinderDodgeRooms pathFinder2d = new HallwayPathFinderDodgeRooms(gridMap, gridMap.getWidthInTiles() * gridMap.getHeightInTiles(), false, spacecraft);
        HallwayMover2d hallwayMover2d = new HallwayMover2d(gridMap);

        for(Edge edge : spacecraft.roomGraph.getEdges()) {
            Point2i roomCenterFrom = edge.getOne().getId();
            Point2i roomCenterTo = edge.getTwo().getId();

            FullPath2d fullPath2d = pathFinder2d.findPath(hallwayMover2d, roomCenterFrom.x, roomCenterFrom.y, roomCenterTo.x, roomCenterTo.y);

            if (fullPath2d != null) {
                for (int step = 0; step < fullPath2d.getLength(); step++) {
                    TileType tileType = gridMap.getTile(fullPath2d.getX(step), fullPath2d.getY(step));
                    if (tileType == TileType.WALL || tileType == TileType.REINFORCED_WALL) {
                        gridMap.write(fullPath2d.getX(step), fullPath2d.getY(step), TileType.DOOR);

                        Room roomRecievingDoor = spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(step), fullPath2d.getY(step)));

                        int temp = step + 1;
                        while(spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(temp), fullPath2d.getY(temp))) == null) {
                            temp++;
                        }

                        Room roomDoorTo = spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(temp), fullPath2d.getY(temp)));

                        roomRecievingDoor.getDoors().add(
                                new Door(
                                        edge
                                        , fullPath2d.getX(step)
                                        , fullPath2d.getY(step)
                                        , roomRecievingDoor
                                        , roomDoorTo
                                )
                        );

                    } else if (tileType != TileType.DOOR) {
                        // spacecraft.gridMap.writeToBothSides(fullPath2d.getX(step), fullPath2d.getY(step), TileType.FLOOR);
                        gridMap.write(fullPath2d.getX(step), fullPath2d.getY(step), TileType.FLOOR);
                    }
                }
            } else {
                throw new RetryException();
            }
        }
        return spacecraft;
    }

    /**
     * Generates the ship's shape
     *
     * @param blueprint
     * @param spacecraft
     */
    public static void generateUnderlyingStructure(Blueprint blueprint, Spacecraft spacecraft) {

        int blackX = -1;
        int blackY = -1;

        // Generate underlying structure;
        int xStructureOffset = r.nextInt(200) - 100;
        int yStructureOffset = r.nextInt(200) - 100;
        for (int x = 0; x < spacecraft.gridMap.getWidthInTiles() / 2; x++) {
            for (int y = 0; y < spacecraft.gridMap.getHeightInTiles(); y++) {

                /*int specRGB = blueprint.spec.getRGB(
                        (int) (((float) x / (float) blueprint.width) * blueprint.spec.getWidth()),
                        (int) (((float) y / (float) blueprint.height) * blueprint.spec.getHeight()));*/

                int specRGB = blueprint.spec.getPixel(
                        (int) (((float) x / (float) blueprint.width) * blueprint.spec.getWidth()),
                        (int) (((float) y / (float) blueprint.height) * blueprint.spec.getHeight()));

                boolean showHere = specRGB == Color.rgba8888(Color.BLACK);
                if (showHere) {
                    blackX = x;
                    blackY = y;
                }


                showHere |= specRGB == Color.rgba8888(Color.WHITE) &&
                        (openSimplexNoise.eval((x + xStructureOffset) * (10f / blueprint.width), (y + yStructureOffset) * (10f / blueprint.height)) > 0);

                if (showHere)
                    spacecraft.gridMap.writeToBothSides(x, y, TileType.INTERNALS);
            }
        }

        //Trim off the underlying structure that is not contiguous
        spacecraft.gridMap.trimNonContiguous(blackX, blackY);
    }

    private static Spacecraft generateRooms(Blueprint blueprint, Spacecraft spacecraft) {

        spacecraft.rooms = new ArrayList<Room>();

        //Generate rooms in underlying structure
        for (int i = 0; i < 100; i++) {

            //We start with large rooms (11-20) then work our way down to smaller closets
            int roomX = r.nextInt(10) + ((100 - i) / 10) + 2;
            int roomY = r.nextInt(10) + ((100 - i) / 10) + 2; // actual room size will be 2 smaller, as we need walls

            int x = r.nextInt(spacecraft.gridMap.getWidthInTiles() / 2 - roomX);
            int y = r.nextInt(spacecraft.gridMap.getHeightInTiles() - roomY);


            boolean primed = false; // we have seen a valid spot for a room.
            boolean placed = false; // we have placed the room

            // we need to move in until we are over valid terrain
            // move x in
            while (!placed) {
                x++;
                boolean valid = spacecraft.gridMap.testRoom(x - 1, y - 1, x + roomX + 1, y + roomY + 1, TileType.INTERNALS);
                if (valid) {
                    primed = true;
                } else if (primed) {
                    //then we are no longer valid.  rewind.
                    placed = true;
                    x--;
                }

                if (x >= spacecraft.gridMap.getWidthInTiles())
                    break;
            }

            primed = false;
            placed = false;
            // move Y in
            while (!placed) {
                y++;

                boolean valid = spacecraft.gridMap.testRoom(x - 1, y - 1, x + roomX + 1, y + roomY + 1, TileType.INTERNALS);
                if (valid) {
                    primed = true;
                } else if (primed) {
                    //then we are no longer valid.  rewind.
                    placed = true;
                    y--;
                }

                if (y >= spacecraft.gridMap.getHeightInTiles())
                    break;
            }

            if (spacecraft.gridMap.testRoom(x, y, x + roomX, y + roomY, TileType.INTERNALS)) {
                Room roomOne = new Room(x, y, x + roomX, y + roomY)
                        , roomTwo = new Room(
                        spacecraft.gridMap.getWidthInTiles() - (x + roomX) - 1,
                        y,
                        spacecraft.gridMap.getWidthInTiles() - x - 1,
                        y + roomY);

                spacecraft.rooms.add(roomOne);
                spacecraft.rooms.add(roomTwo    );

                spacecraft.gridMap.writeRoom(x, y, x + roomX, y + roomY, TileType.FLOOR, TileType.WALL, false);
                spacecraft.roomGraph.addNode(new Node(new ComparablePoint2i(roomOne.center())), true);
                spacecraft.roomGraph.addNode(new Node(new ComparablePoint2i(roomTwo.center())), true);
            }
        }
        return spacecraft;
    }

    private static Spacecraft generateHallways(Blueprint blueprint, Spacecraft spacecraft) throws RetryException {

        GridMap tempGridMap = new GridMap(spacecraft.gridMap.getWidthInTiles(), spacecraft.gridMap.getHeightInTiles());
        for(int x = 0; x < spacecraft.gridMap.getWidthInTiles(); x++) {
            for(int y = 0; y < spacecraft.gridMap.getWidthInTiles(); y++) {
                tempGridMap.setCellTypeAt(x,y, spacecraft.gridMap.getCellTypeAt(x,y));
            }
        }

        // Cutting paths
        HallwayPathFinder pathFinder2d = new HallwayPathFinder(tempGridMap, tempGridMap.getWidthInTiles() * tempGridMap.getHeightInTiles(), false, spacecraft);
        HallwayMover2d hallwayMover2d = new HallwayMover2d(tempGridMap);

        // for each pair of room centers
        for (Room roomFrom : spacecraft.rooms) {
            for (Room roomTo : spacecraft.rooms) {

                Point2i roomCenterFrom = roomFrom.center();
                Point2i roomCenterTo = roomTo.center();

                FullPath2d fullPath2d = pathFinder2d.findPath(hallwayMover2d, roomCenterFrom.x, roomCenterFrom.y, roomCenterTo.x, roomCenterTo.y);

                /*if (fullPath2d != null) {
                    for (int step = 0; step < fullPath2d.getLength(); step++) {
                        TileType tileType = spacecraft.gridMap.getTile(fullPath2d.getX(step), fullPath2d.getY(step));


                        if (tileType == TileType.WALL || tileType == TileType.REINFORCED_WALL) {
                            spacecraft.gridMap.writeToBothSides(fullPath2d.getX(step), fullPath2d.getY(step), TileType.DOOR);
                        } else if (tileType != TileType.DOOR) {
                            spacecraft.gridMap.writeToBothSides(fullPath2d.getX(step), fullPath2d.getY(step), TileType.FLOOR);
                        }

                    }
                }*/
                if (fullPath2d != null) {
                    for (int step = 0; step < fullPath2d.getLength(); step++) {
                        TileType tileType = tempGridMap.getTile(fullPath2d.getX(step), fullPath2d.getY(step));
                        if (tileType == TileType.WALL || tileType == TileType.REINFORCED_WALL) {
                            // spacecraft.gridMap.writeToBothSides(fullPath2d.getX(step), fullPath2d.getY(step), TileType.DOOR);
                            tempGridMap.write(fullPath2d.getX(step), fullPath2d.getY(step), TileType.DOOR);

                            // int widthMinusOne = spacecraft.gridMap.getWidthInTiles() - 1;
                            Room roomRecievingDoor = spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(step), fullPath2d.getY(step)));
                            // Room oppositeRoomRecievingDoor = spacecraft.findRoomContainingPoint(new Point2i(widthMinusOne - fullPath2d.getX(step), fullPath2d.getY(step)));

                            int temp = step + 1;
                            while(spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(temp), fullPath2d.getY(temp))) == null) {
                                temp++;
                            }


                            Room roomDoorTo = spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(temp), fullPath2d.getY(temp)));
                            // Room oppositeRoomDoorTo = spacecraft.findRoomContainingPoint(new Point2i(widthMinusOne - fullPath2d.getX(temp), fullPath2d.getY(temp)));

                            /*roomRecievingDoor.getDoors().add(
                                    new Door(
                                            fullPath2d.getX(step)
                                            , fullPath2d.getY(step)
                                            , roomRecievingDoor
                                            , roomDoorTo
                                    )
                            );*/
                            /*oppositeRoomRecievingDoor.getDoors().add(
                                    new Door(
                                            widthMinusOne - fullPath2d.getX(step)
                                            , fullPath2d.getY(step)
                                            , oppositeRoomRecievingDoor
                                            , oppositeRoomDoorTo
                                    )
                            );*/

                            Graph graph = spacecraft.roomGraph;
                            Boolean edgeAdded = graph.addEdge(
                                    graph.getNode(new ComparablePoint2i(roomRecievingDoor.center().x, roomRecievingDoor.center().y))
                                    , graph.getNode(new ComparablePoint2i(roomDoorTo.center().x, roomDoorTo.center().y))
                                    , ((temp-1)-(step+1)) + roomRecievingDoor.area() + roomDoorTo.area()
                            );

                            /*graph.addEdge(
                                    graph.getNode(new ComparablePoint2i(oppositeRoomRecievingDoor.center().x, oppositeRoomRecievingDoor.center().y))
                                    , graph.getNode(new ComparablePoint2i(oppositeRoomDoorTo.center().x, oppositeRoomDoorTo.center().y))
                                    , ((temp-1)-(step+1)) + oppositeRoomRecievingDoor.area() + oppositeRoomDoorTo.area()
                            );*/

                        } else if (tileType != TileType.DOOR) {
                            // spacecraft.gridMap.writeToBothSides(fullPath2d.getX(step), fullPath2d.getY(step), TileType.FLOOR);
                            tempGridMap.write(fullPath2d.getX(step), fullPath2d.getY(step), TileType.FLOOR);
                        } else if (tileType == TileType.DOOR) {
                            // int widthMinusOne = spacecraft.gridMap.getWidthInTiles() - 1;
                            Room roomRecievingDoor = spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(step), fullPath2d.getY(step)));
                            // Room oppositeRoomRecievingDoor = spacecraft.findRoomContainingPoint(new Point2i(widthMinusOne - fullPath2d.getX(step), fullPath2d.getY(step)));

                            int temp = step + 1;
                            while(spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(temp), fullPath2d.getY(temp))) == null) {
                                temp++;
                            }


                            Room roomDoorTo = spacecraft.findRoomContainingPoint(new Point2i(fullPath2d.getX(temp), fullPath2d.getY(temp)));
                            // Room oppositeRoomDoorTo = spacecraft.findRoomContainingPoint(new Point2i(widthMinusOne - fullPath2d.getX(temp), fullPath2d.getY(temp)));


                            Graph graph = spacecraft.roomGraph;
                            com.carterza.generator.graph.Edge e = new Edge(graph.getNode(new ComparablePoint2i(roomRecievingDoor.center().x, roomRecievingDoor.center().y))
                                    , graph.getNode(new ComparablePoint2i(roomDoorTo.center().x, roomDoorTo.center().y))
                                    , ((temp-1)-(step+1)) + roomRecievingDoor.area() + roomDoorTo.area());
                            if(!spacecraft.roomGraph.containsEdge(e))
                                spacecraft.roomGraph.addEdge(e.getOne(), e.getTwo());
                        }
                    }
                } else {

                }
            }
        }

        return spacecraft;
    }

    public static Spacecraft generateHallwayWalls(Blueprint blueprint, Spacecraft spacecraft) {

        //flood fill the vacuum, everything else should be solid ship
//        MapGen2d.floodFill(spacecraft.gridMap, new Point2i(0, 0), new CellEquals2d(TileType.AETHER), new DrawCell2d(TileType.VACUUM));

//        MapGen2d.floodFill(spacecraft.gridMap, new Point2i(spacecraft.gridMap.getWidthInTiles() - 1, spacecraft.gridMap.getHeightInTiles() - 1), new CellEquals2d(TileType.AETHER), new DrawCell2d(TileType.VACUUM));

//        // Any interior pockets should be internals
//        MapGen2d.fill(spacecraft.gridMap, new CellEquals2d(TileType.AETHER), new DrawCell2d(TileType.INTERNALS));

        MapGen2d.fill(spacecraft.gridMap, new CellEquals2d(TileType.AETHER), new DrawCell2d(TileType.VACUUM));

        //surround any external halls with wall
        MapGen2d.fill(spacecraft.gridMap,
                new AndPredicate2d(
                        new CellEquals2d(TileType.VACUUM),
                        new CellNearCell2d(TileType.FLOOR)
                ),
                new DrawCell2d(TileType.WALL)
        );


        //surround any halls with wall
        MapGen2d.fill(spacecraft.gridMap,
                new AndPredicate2d(
                        new CellEquals2d(TileType.INTERNALS),
                        new OrPredicate2d(
                                new CellNearCell2d(TileType.FLOOR),
                                new CellNearCell2d(TileType.DOOR))

                ),
                new DrawCell2d(TileType.WALL)
        );

        // surround the ship with armor
        MapGen2d.fill(spacecraft.gridMap,
                new AndPredicate2d(
                        new CellEquals2d(TileType.VACUUM),

                        new OrPredicate2d(
                                new CellNearCell2d(TileType.WALL),
                                new CellNearCell2d(TileType.REINFORCED_WALL.WALL)
                        )

                ),
                new DrawCell2d(TileType.ARMOR)
        );

        return spacecraft;
    }

    public static Spacecraft generateMiniMap(Blueprint blueprint, Spacecraft spacecraft) {
        int xColorOffset = r.nextInt(200) - 100;
        int yColorOffset = r.nextInt(200) - 100;

        spacecraft.minimap = new Pixmap(blueprint.width, blueprint.height, Pixmap.Format.RGBA8888);
        for (int x = 0; x < spacecraft.minimap.getWidth() / 2; x++) {
            for (int y = 0; y < spacecraft.minimap.getHeight(); y++) {

                Color color;

                if (spacecraft.gridMap.getTile(x, y) == TileType.INTERNALS) {

                    int index = (int) (blueprint.colorPalette.colors.size() * (openSimplexNoise.eval((x + xColorOffset) * (10f / blueprint.width), (y + yColorOffset) * (10f / blueprint.height)) + 1) / 2.0);
                    color = blueprint.colorPalette.colors.get(index);

                } else if (!spacecraft.gridMap.getTile(x, y).equals(TileType.AETHER) && !spacecraft.gridMap.getTile(x, y).equals(TileType.VACUUM)) {
                    color = spacecraft.gridMap.getTile(x, y).color;
                } else {

                    color = transparent;

                }

                spacecraft.minimap.drawPixel(x, y, Color.rgba8888(color));
                spacecraft.minimap.drawPixel(spacecraft.minimap.getWidth() - 1 - x, y, Color.rgba8888(color));

            }
        }


        /*for(Room room : spacecraft.rooms) {
            Color color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.0f);
            for(int x = room.x1; x < room.x2; x++) {
                for(int y = room.y1; y < room.y2; y++) {
                    spacecraft.output.drawPixel(x, y, Color.rgba8888(color));
                }
            }
        }*/

        return spacecraft;
    }

    public static Spacecraft generateShipTexture(Blueprint blueprint, Spacecraft spacecraft) {
        int xColorOffset = r.nextInt(200) - 100;
        int yColorOffset = r.nextInt(200) - 100;

        spacecraft.shipTexture = new Pixmap(blueprint.width, blueprint.height, Pixmap.Format.RGBA8888);
        for (int x = 0; x < spacecraft.shipTexture.getWidth() / 2; x++) {
            for (int y = 0; y < spacecraft.shipTexture.getHeight(); y++) {

                Color color;

                if (spacecraft.gridMap.getTile(x, y) == TileType.INTERNALS) {

                    int index = (int) (blueprint.colorPalette.colors.size() * (openSimplexNoise.eval((x + xColorOffset) * (10f / blueprint.width), (y + yColorOffset) * (10f / blueprint.height)) + 1) / 2.0);
                    color = blueprint.colorPalette.colors.get(index);

                } else {

                    color = transparent;

                }

                spacecraft.shipTexture.drawPixel(x, y, Color.rgba8888(color));
                spacecraft.shipTexture.drawPixel(spacecraft.shipTexture.getWidth() - 1 - x, y, Color.rgba8888(color));

            }
        }


        /*for(Room room : spacecraft.rooms) {
            Color color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.0f);
            for(int x = room.x1; x < room.x2; x++) {
                for(int y = room.y1; y < room.y2; y++) {
                    spacecraft.output.drawPixel(x, y, Color.rgba8888(color));
                }
            }
        }*/

        return spacecraft;
    }

}
