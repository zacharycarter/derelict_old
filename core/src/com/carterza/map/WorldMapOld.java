package com.carterza.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.carterza.generator.color.ColorPalette;
import com.carterza.generator.exception.RetryException;
import com.carterza.generator.graph.ComparablePoint2i;
import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Graph;
import com.carterza.generator.graph.Node;
import com.carterza.generator.spacecraft.*;
import com.carterza.generator.spacecraft.TileType;
import com.stuckinadrawer.*;
import com.stuckinadrawer.Room;
import com.stuckinadrawer.graphs.Grammar;
import com.stuckinadrawer.graphs.GrammarManager;
import com.stuckinadrawer.graphs.Vertex;
import net.bytten.gameutil.Vec2I;
import net.bytten.gameutil.algorithms.AStar;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WorldMapOld {
    TiledMap map;
    TiledMapRenderer mapRenderer;
    private Texture tiles;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Spacecraft spacecraft;
    private List<ComparablePoint2i> solutionPath;
    private com.stuckinadrawer.graphs.Graph levelGraph;
    private Tile[][] level;
    Random random;
    private ArrayList<Room> rooms;

    private static final int LEVEL_WIDTH = 256;
    private static final int LEVEL_HEIGHT = 256;

    public WorldMapOld(OrthographicCamera camera) {
        this.camera = camera;
        this.map = new TiledMap();
        this.batch = new SpriteBatch();
        this.random = new Random();
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

    private Tile[][] initEmptyLevel(int levelWidth, int levelHeight) {
        Tile[][] level = new Tile[levelWidth][levelHeight];

        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                //if(gridMap.getTile(x,y) != TileType.INTERNALS)
                    level[x][y] = new Tile(com.stuckinadrawer.TileType.EMPTY);
                //else
                    //level[x][y] = new Tile(com.stuckinadrawer.TileType.SHIP_INTERNALS);
            }
        }
        return level;
    }


    public void generate(TextureAtlas atlas) {
        Blueprint blueprint = generateBlueprint();
        //this.spacecraft = SpacecraftGenerator.generate(blueprint);
        //processRoomGraph(spacecraft);
        //placeLocksAndKeys(spacecraft);

        GrammarManager grammarManager = new GrammarManager();
        grammarManager.setGrammar(loadGrammar());
        grammarManager.applyAllProductions();
        levelGraph = grammarManager.getCurrentGraph();

        rooms = createRooms();
        Grid grid = new Grid();

        int[][] roomGrid = grid.createRoomGrid(rooms);
        int fieldSize = 10;
        int subLevelWidth = roomGrid.length * fieldSize;
        int subLevelHeight = roomGrid[0].length * fieldSize;
        int levelWidth = spacecraft.gridMap.getWidthInTiles();
        int levelHeight = spacecraft.gridMap.getHeightInTiles();

        level = initEmptyLevel(levelWidth, levelHeight);

        /*Room startRoom = null;
        for(Room r : rooms) {
            for(Vertex v : r.elements) {
                if (v.getType().equals("start")) {
                    startRoom = r;
                }
            }
        }*/
        /*int centerRoomId = roomGrid[roomGrid.length/2][roomGrid[1].length/2];
        Room centerRoom = null;
        for(Room r : rooms) {
            if(r.id == centerRoomId) centerRoom = r;
        }
        System.out.println(rooms);*/
        for (Room r : rooms) {
            putRoomInMap(r, 10, levelWidth, levelHeight, subLevelWidth, subLevelHeight);
        }

        // makeCorridors(level, levelWidth, levelHeight, rooms);

        for(int x = 0; x < level.length; x++) {
            for(int y = 0; y < level[0].length; y++) {
                if(
                        spacecraft.gridMap.getTile(x,y) == TileType.INTERNALS
                        && level[x][y].tileType == com.stuckinadrawer.TileType.EMPTY
                        ) level[x][y] = new Tile(com.stuckinadrawer.TileType.SHIP_INTERNALS);
                // else if(spacecraft.gridMap.getTile(x,y) == TileType.INTERNALS) level[x][y] = new Tile(com.stuckinadrawer.TileType.SHIP_INTERNALS);
            }
        }

        this.level = level;

        // new Renderer(level);


        //PixmapIO.writePNG(new FileHandle(new File("ship.png")), spacecraft.output);

        TiledMapTileLayer baseLayer = new TiledMapTileLayer(levelWidth, levelHeight, 24, 24);
        for(int x = 0; x < levelWidth; x++) {
            for(int y = 0; y < levelHeight; y++) {
                com.stuckinadrawer.TileType tileType = level[x][y].tileType;
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                switch(tileType) {
                    case ROOM:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("floor_tile01")));
                        break;
                    case CORRIDOR:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("door_tile01")));
                        break;
                    case SHIP_INTERNALS:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("ship_internals")));
                        break;
                    case WALL:
                        int tileIndex = calcTileIndex(
                                level[x][y+1].tileType == com.stuckinadrawer.TileType.WALL
                                , level[x-1][y].tileType == com.stuckinadrawer.TileType.WALL
                                , level[x][y-1].tileType == com.stuckinadrawer.TileType.WALL
                                , level[x+1][y].tileType == com.stuckinadrawer.TileType.WALL
                        );

                        if(tileIndex == 5)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("vertical_wall_segment")));
                        else if(tileIndex == 12)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_nw_corner")));
                        else if(tileIndex == 6)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_ne_corner")));
                        else if (tileIndex == 15)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_intersection")));
                        else if (tileIndex == 9)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_sw_corner")));
                        else if (tileIndex == 7)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_left_t")));
                        else if (tileIndex == 13)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_right_t")));
                        else if (tileIndex == 11)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_bottom_t")));
                        else if (tileIndex == 3)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_se_corner")));
                        else if (tileIndex == 14)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_top_t")));
                        else if (tileIndex == 10)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("horizontal_wall_segment")));
                        break;
                    // case EMPTY:
                        // cell.setTile(new StaticTiledMapTile(atlas.findRegion("ship_internals")));
                        // break;
                }
                baseLayer.setCell(x, y, cell);
            }
        }



        /*TiledMapTileLayer baseLayer = new TiledMapTileLayer(LEVEL_WIDTH, LEVEL_HEIGHT, 24, 24);
        for(int x = 0; x < LEVEL_WIDTH; x++) {
            for(int y = 0; y < LEVEL_HEIGHT; y++) {
                TileType tileType = this.spacecraft.gridMap.getTile(x, y);
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                switch(tileType) {
                    case FLOOR:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("floor_tile01")));
                        break;
                    case DOOR:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("door_tile01")));
                        break;
                    case INTERNALS:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("ship_internals")));
                        break;
                    case WALL:
                        // Horizontal wall segment
                        if(this.spacecraft.gridMap.getTile(x-1,y) == TileType.WALL && this.spacecraft.gridMap.getTile(x+1,y) == TileType.WALL) {
                            cell.setTile(new StaticTiledMapTile(new StaticTiledMapTile(atlas.findRegion("horizontal_wall_segment"))));
                        }
                        // Vertical wall segment
                        else if(this.spacecraft.gridMap.getTile(x,y-1) == TileType.WALL && this.spacecraft.gridMap.getTile(x,y+1) == TileType.WALL) {
                            cell.setTile(new StaticTiledMapTile(new StaticTiledMapTile(atlas.findRegion("vertical_wall_segment"))));
                        }
                        // SW Corner
                        else if (this.spacecraft.gridMap.getTile(x+1,y) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x-1,y) != TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y+1) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y-1) != TileType.WALL) {
                            cell.setTile(new StaticTiledMapTile(new StaticTiledMapTile(atlas.findRegion("wall_sw_corner"))));
                        }
                        // SE Corner
                        else if (this.spacecraft.gridMap.getTile(x-1,y) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x+1,y) != TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y+1) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y-1) != TileType.WALL) {
                            cell.setTile(new StaticTiledMapTile(new StaticTiledMapTile(atlas.findRegion("wall_se_corner"))));
                        }
                        // NW Corner
                        else if (this.spacecraft.gridMap.getTile(x+1,y) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x-1,y) != TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y-1) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y+1) != TileType.WALL) {
                            cell.setTile(new StaticTiledMapTile(new StaticTiledMapTile(atlas.findRegion("wall_nw_corner"))));
                        }
                        // NE Corner
                        else if (this.spacecraft.gridMap.getTile(x-1,y) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x+1,y) != TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y-1) == TileType.WALL
                                && this.spacecraft.gridMap.getTile(x,y+1) != TileType.WALL) {
                            cell.setTile(new StaticTiledMapTile(new StaticTiledMapTile(atlas.findRegion("wall_ne_corner"))));
                        }
                        break;
                    default:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("trans")));
                        break;
                }
                baseLayer.setCell(x, y, cell);
            }
        }*/
        map.getLayers().add(baseLayer);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 24f);
    }

    private int calcTileIndex(boolean above, boolean left, boolean below, boolean right) {
        int sum = 0;
        if (above) sum += 1;
        if (left)  sum += 2;
        if (below) sum += 4;
        if (right) sum += 8;
        return sum;
    };

    private void processRoomGraph(Spacecraft sc) {
        com.carterza.generator.spacecraft.Room roomOne = null
                , roomTwo = null;
        int maxDistance = -1;
        for(com.carterza.generator.spacecraft.Room r1 : sc.rooms) {
            for(com.carterza.generator.spacecraft.Room r2 : sc.rooms) {
                int distance = r2.center().getManhattanDistance(r1.center());
                if(distance > maxDistance) {
                    maxDistance = distance;
                    roomOne = r1;
                    roomTwo = r2;
                }
            }
        }

        com.carterza.generator.spacecraft.Room[] rooms = new com.carterza.generator.spacecraft.Room[]{roomOne, roomTwo};
        com.carterza.generator.spacecraft.Room startRoom = rooms[Math.random() > 0.5 ? 1 : 0];
        startRoom.setItem(new Symbol(Symbol.START));

        sc.roomGraph.bfs(sc.roomGraph.getNode(new ComparablePoint2i(startRoom.center())));

        TreeMap<int[], Node> nodesSortedByLevelAndDistance = new TreeMap<int[], Node>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                int i = Integer.compare(o1[0], o2[0]);
                if(i != 0) return i;

                return Integer.compare(o1[1], o2[1]);
            }
        });

        for(Node n : sc.roomGraph.getNodes().values()) {
            nodesSortedByLevelAndDistance.put(new int[]{n.getLevel(), n.getDistance()}, n);
        }

        spacecraft.findRoom(nodesSortedByLevelAndDistance.lastEntry().getValue().getId()).setItem(new Symbol(Symbol.GOAL));

        this.solutionPath = astar(
                        new ComparablePoint2i(spacecraft.findStart().center())
                        , new ComparablePoint2i(spacecraft.findGoal().center())
                );
    }

    Comparator<Node> distanceLevelComparator = new Comparator<Node>() {
        @Override public int compare(Node n1, Node n2) {
            int i = Integer.compare(n1.getLevel(), n2.getLevel());
            if(i != 0) return i;

            return Integer.compare(n1.getDistance(), n2.getDistance());
        }
    };

    private Blueprint generateBlueprint() {
        Pixel[][] grid = new BlueprintGenerator().create(AssetSize.LARGE);
        Blueprint blueprint = new Blueprint();
        blueprint.width = LEVEL_WIDTH;
        blueprint.height = LEVEL_HEIGHT;

        ColorPalette colorPalette = new ColorPalette();
        colorPalette.generate();

        blueprint.colorPalette = colorPalette.sub();

        Pixmap spec = new Pixmap(grid[0].length, grid[1].length, Pixmap.Format.RGBA8888);

        try {
            int scaleFactor = 1;
            for (int r = 0; r < grid[0].length; r++) {
                for (int c = 0; c < grid[1].length; c++) {

                    if (grid[r][c].value == Pixel.BORDER) {
                        spec.setColor(Color.WHITE);
                        spec.drawPixel(c * scaleFactor, r * scaleFactor);
                    } else if (grid[r][c].value == Pixel.FILLED) {
                        spec.setColor(Color.BLACK);
                        spec.drawPixel(c * scaleFactor, r * scaleFactor);
                    } else if (grid[r][c].value == Pixel.SECONDARY) {
                        spec.setColor(Color.BLACK);
                        spec.drawPixel(c * scaleFactor, r * scaleFactor);
                    }
                }
            }
        } catch (Exception e) {
            try {
                System.out.println("Failed to generate blueprint. Retrying.");
                throw new RetryException();
            } catch (RetryException e1) {
                generateBlueprint();
            }
        }

        PixmapIO.writePNG(new FileHandle(new File("output.png")), spec);

        // blueprint.spec = new Pixmap(Gdx.files.internal("map-templates/test.png"));

        blueprint.spec = spec;
        return blueprint;
    }

    public void render() {
        Gdx.gl.glClearColor(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.camera.update();
        this.mapRenderer.setView(camera);
        this.mapRenderer.render();
        // batch.begin();
        // font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        // batch.end();
    }

    public void zoomOut() {
        this.camera.zoom -= 1;
        this.camera.update();
    }

    public void zoomIn() {
        this.camera.zoom += 1;
        this.camera.update();
    }

    public Spacecraft getSpacecraft() {
        return this.spacecraft;
    }

    public List<ComparablePoint2i> getSolutionPath() {
        return solutionPath;
    }

    private class AStarClient implements AStar.IClient<ComparablePoint2i> {

        private int keyLevel;

        public AStarClient() {

        }

        @Override
        public Collection<ComparablePoint2i> getNeighbors(ComparablePoint2i roomId) {
            List<ComparablePoint2i> ids = new ArrayList<ComparablePoint2i>();
            Node currentNode = spacecraft.roomGraph.getNode(roomId);
            for (Edge edge: currentNode.getNeighbors()) {
                ids.add(edge.getNeighbor(currentNode).getId());
            }
            return ids;
        }

        @Override
        public Vec2I getVec2I(ComparablePoint2i roomId) {
            return new Vec2I(roomId.x, roomId.y);
        }
    }

    private List<ComparablePoint2i> astar(ComparablePoint2i start, ComparablePoint2i goal) {
        AStar<ComparablePoint2i> astar = new AStar<ComparablePoint2i>(new AStarClient(), start, goal);
        return astar.solve();
    }

    public Vertex findStartVertex() {
        // get starting vertex
        for (Vertex v : levelGraph.getVertices()) {
            if (v.getType().equals("start")) {
                return v;
            }
        }
        return null;
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

    private void putRoomInMap(Room r, int fieldSize, int levelWidth, int levelHeight, int subLevelWidth, int subLevelHeight) {
        System.out.println("mapping: " + r.id + "on pos " + r.x + " " + r.y);

        int w = ThreadLocalRandom.current().nextInt(10, 20 + 1);
        int h = ThreadLocalRandom.current().nextInt(10, 20 + 1);
        r.x = r.x * 16  ;
        r.y = r.y * 12;
        r.width = 16;
        r.height = 12;

        r.x += (this.spacecraft.gridMap.getWidthInTiles() / 2) - subLevelWidth/2;
        r.y += (this.spacecraft.gridMap.getHeightInTiles() / 2) - subLevelHeight/2;



        for (int x = r.x; x <= r.x + r.width; x++) {
            for (int y = r.y; y <= r.y + r.height; y++) {

                if (x < 0 || x >= levelWidth || y < 0 || y >= levelHeight) continue;

                if (x == r.x || x == r.x + r.width || y == r.y || y == r.y + r.height) {
                    level[x][y] = new Tile(com.stuckinadrawer.TileType.WALL);
                } else {
                    level[x][y] = new Tile(com.stuckinadrawer.TileType.ROOM);
                }

                level[x][y].setRoomID(r.id);
            }
        }
        int i = 0;
        for (Vertex v : r.elements) {
            com.stuckinadrawer.TileType tileType = com.stuckinadrawer.TileType.EMPTY;
            if (v.getType().equals("start")) {
                tileType = com.stuckinadrawer.TileType.ENTRANCE;
            }
            if (v.getType().equals("exit")) {
                tileType = com.stuckinadrawer.TileType.EXIT;
            }
            if (v.getType().equals("opponent")) {
                tileType = com.stuckinadrawer.TileType.OPPONENT;
            }
            if (v.getType().equals("boss")) {
                tileType = com.stuckinadrawer.TileType.BOSS;
            }
            if (v.getType().equals("key")) {
                tileType = com.stuckinadrawer.TileType.KEY;
            }
            if (v.getType().equals("lock")) {
                tileType = com.stuckinadrawer.TileType.LOCK;
            }
            if (v.getType().equals("trap")) {
                tileType = com.stuckinadrawer.TileType.TRAP;
            }
            if (v.getType().equals("chest")) {
                tileType = com.stuckinadrawer.TileType.CHEST;
            }
            if (v.getType().equals("buff")) {
                tileType = com.stuckinadrawer.TileType.BUFF;
            }
            if (r.x + 2 + i < 0 || r.x + 2 + i >= levelWidth || r.y + 2 < 0 || r.y + 2 >= levelHeight) continue;
            level[r.x + 2 + i][r.y + 2].tileType = tileType;


            i++;
        }

    }

    private void makeCorridors(Tile[][] level, int levelWidth, int levelHeight, List<Room> rooms) {
        Pathfinder pathfinder = new Pathfinder(level);
        for (Room r : rooms) {
            if (r.id == 0) continue;
            Room connectedRoom = rooms.get(r.incomingRoomID);
            LinkedList<Point> path = pathfinder.findPath(r, connectedRoom);
            for (Point pos : path) {
                Tile t = level[pos.getX()][pos.getY()];
                if (t.tileType == com.stuckinadrawer.TileType.WALL || t.tileType == com.stuckinadrawer.TileType.EMPTY || t.tileType == com.stuckinadrawer.TileType.SHIP_INTERNALS) {
                    level[pos.getX()][pos.getY()].tileType = com.stuckinadrawer.TileType.CORRIDOR;
                }
            }
            surroundCorridorWithWalls(levelWidth, levelHeight, level);
        }


    }

    private void surroundCorridorWithWalls(int levelWidth, int levelHeight, Tile[][] level) {
        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                if (level[x][y].tileType == com.stuckinadrawer.TileType.CORRIDOR) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        for (int yy = y - 1; yy <= y + 1; yy++) {
                            if (xx < 0 || xx > levelWidth || yy < 0 || yy > levelHeight) continue;
                            if (level[xx][yy].tileType == com.stuckinadrawer.TileType.EMPTY) {
                                level[xx][yy].tileType = com.stuckinadrawer.TileType.WALL;
                            }
                        }
                    }
                }
            }
        }

    }

    public int[] findEntrance() {
        for(int x = 0; x < level.length; x++) {
            for(int y = 0; y < level[0].length; y++)
                if(level[x][y].tileType == com.stuckinadrawer.TileType.ENTRANCE) return new int[]{x,y};
        }
        return new int[]{0,0};
    }

    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    public Room findRoomContainingPoint(Vec2I position) {
        Room room = null;
        for(Room r : this.rooms) {
            if(r.contains(position)) room = r;
        }
        return room;
    }

    public Tile[][] getLevel() {
        return this.level;
    }

}
