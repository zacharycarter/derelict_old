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
import com.carterza.generator.graph.Node;
import com.carterza.generator.graph.algorithm.Cycle;
import com.carterza.generator.spacecraft.*;
import com.carterza.generator.spacecraft.TileType;
import com.stewsters.util.math.Point2i;
import com.stuckinadrawer.*;
import com.stuckinadrawer.Room;
import com.stuckinadrawer.graphs.Grammar;
import com.stuckinadrawer.graphs.Vertex;
import net.bytten.gameutil.Vec2I;
import net.bytten.gameutil.algorithms.AStar;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WorldMap {
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
    private ArrayList<com.stuckinadrawer.Room> rooms;
    private List<Edge> removedEdges;

    private static final int LEVEL_WIDTH = 256;
    private static final int LEVEL_HEIGHT = 256;

    public WorldMap(OrthographicCamera camera) {
        this.camera = camera;
        this.map = new TiledMap();
        this.batch = new SpriteBatch();
        this.random = new Random();
        this.removedEdges = new ArrayList<Edge>();
    }


    public void generate(TextureAtlas atlas) {
        Blueprint blueprint = generateBlueprint();
        while(true) {
            try {
                this.spacecraft = SpacecraftGenerator.generate(blueprint);
                KeyLevelRoomMapping levels = processRoomGraph(spacecraft);
                computeIntensity(spacecraft, levels);
                placeKeys(levels);
                graphify(spacecraft);
                SpacecraftGenerator.carveHallways(spacecraft);
                SpacecraftGenerator.generateHallwayWalls(blueprint, spacecraft);
                SpacecraftGenerator.generateMiniMap(blueprint, spacecraft);
                SpacecraftGenerator.generateShipTexture(blueprint,spacecraft);
                PixmapIO.writePNG(new FileHandle(new File("minimap.png")), spacecraft.minimap);
                PixmapIO.writePNG(new FileHandle(new File("ship.png")), spacecraft.shipTexture);
                break;
            } catch (RetryException e) {
                e.printStackTrace();
            }
        }

        TiledMapTileLayer baseLayer = new TiledMapTileLayer(LEVEL_WIDTH, LEVEL_HEIGHT, 24, 24);
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
                        int tileIndex = calcTileIndex(
                                this.spacecraft.gridMap.getCellTypeAt(x, y+1) == TileType.WALL
                                , this.spacecraft.gridMap.getCellTypeAt(x-1, y) == TileType.WALL
                                , this.spacecraft.gridMap.getCellTypeAt(x, y-1) == TileType.WALL
                                , this.spacecraft.gridMap.getCellTypeAt(x+1, y) == TileType.WALL
                        );

                        int doorTileIndex = calcTileIndex(
                                this.spacecraft.gridMap.getCellTypeAt(x, y+1) == TileType.DOOR
                                , this.spacecraft.gridMap.getCellTypeAt(x-1, y) == TileType.DOOR
                                , this.spacecraft.gridMap.getCellTypeAt(x, y-1) == TileType.DOOR
                                , this.spacecraft.gridMap.getCellTypeAt(x+1, y) == TileType.DOOR
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
                        else if (doorTileIndex == 4 && tileIndex == 1)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("vertical_wall_segment")));
                        else if (doorTileIndex == 1 && tileIndex == 4)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("vertical_wall_segment")));
                        else if (doorTileIndex == 2 && tileIndex == 8)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("horizontal_wall_segment")));
                        else if (doorTileIndex == 8 && tileIndex == 2)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("horizontal_wall_segment")));
                        else if (doorTileIndex == 4 && tileIndex == 2)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_ne_corner")));
                        else if (doorTileIndex == 2 && tileIndex == 4)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_ne_corner")));
                        else if (doorTileIndex == 4 && tileIndex == 8)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_nw_corner")));
                        else if (doorTileIndex == 8 && tileIndex == 4)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_nw_corner")));
                        else if (doorTileIndex == 8 && tileIndex == 1)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_sw_corner")));
                        else if (doorTileIndex == 1 && tileIndex == 8)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_sw_corner")));
                        else if (doorTileIndex == 2 && tileIndex == 1)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_se_corner")));
                        else if (doorTileIndex == 1 && tileIndex == 2)
                            cell.setTile(new StaticTiledMapTile(atlas.findRegion("wall_se_corner")));
                        break;
                    default:
                        cell.setTile(new StaticTiledMapTile(atlas.findRegion("trans")));
                        break;
                }
                baseLayer.setCell(x, y, cell);
            }
        }
        map.getLayers().add(baseLayer);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 24f);
    }

    private void placeKeys(KeyLevelRoomMapping levels) {
        // Now place the keys. For every key-level but the last one, place a
        // key for the next level in it, preferring rooms with fewest links
        // (dead end rooms).
        for (int key = 0; key < levels.keyCount()-1; ++key) {
            List<Node> nodes = levels.getRooms(key);

            Collections.shuffle(nodes, random);
            // Collections.sort is stable: it doesn't reorder "equal" elements,
            // which means the shuffling we just did is still useful.
            Collections.sort(nodes, INTENSITY_COMPARATOR);
            // Alternatively, use the EDGE_COUNT_COMPARATOR to put keys at
            // 'dead end' rooms.

            Symbol keySym = new Symbol(key);

            boolean placedKey = false;
            for (Node node: nodes) {
                if (node.getItem() == null) {
                    node.setItem(keySym);
                    placedKey = true;
                    break;
                }
            }
            if (!placedKey)
                System.out.println("Couldn't place key!");
            // there were no rooms into which the key would fit
            // throw new RetryException();
        }
    }

    private void graphify(Spacecraft spacecraft) {
        for(Edge edge : removedEdges) {
            Node one = edge.getOne(), two = edge.getTwo();
            boolean forwardImplies = one.getPrecondition().implies(two.getPrecondition()),
                    backwardImplies = two.getPrecondition().implies(one.getPrecondition());
            if (forwardImplies && backwardImplies) {
                // both rooms are at the same keyLevel.
               /* if (random.nextDouble() >=
                        0.2)
                    continue;*/

                spacecraft.roomGraph.addEdge(edge.getOne(), edge.getTwo());
            } else {
                Symbol difference = edge.getOne().getPrecondition().singleSymbolDifference(
                        edge.getTwo().getPrecondition());
                if (difference == null || (!difference.isSwitchState() /*&&
                        random.nextDouble() >=
                                0.2*/))
                    continue;
                spacecraft.roomGraph.addEdge(edge.getOne(), edge.getTwo(), difference);
            }
        }
    }

    private int calcTileIndex(boolean above, boolean left, boolean below, boolean right) {
        int sum = 0;
        if (above) sum += 1;
        if (left)  sum += 2;
        if (below) sum += 4;
        if (right) sum += 8;
        return sum;
    };

    private KeyLevelRoomMapping processRoomGraph(Spacecraft sc) {

        Cycle finder = new Cycle(sc.roomGraph);


        while(finder.hasCycle()) {
            Map<Edge, Stack<Node>> cycle = (Map<Edge, Stack<Node>>) finder.cycle();
            this.removedEdges.add(sc.roomGraph.removeEdge(cycle.entrySet().iterator().next().getKey()));
            finder = new Cycle(sc.roomGraph);
        }

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

        TreeMap<Integer, List<Node>> nodesSortedByLevel = sc.roomGraph.getNodesSortedByLevel();
        Iterator<Map.Entry<Integer, List<Node>>> nodesSortedByLevelIterator  = nodesSortedByLevel.entrySet().iterator();
        while(nodesSortedByLevelIterator.hasNext()) {
            Map.Entry<Integer, List<Node>> e = nodesSortedByLevelIterator.next();
            for(Node n : e.getValue()) {
                if(n.getParent() == null && e.getKey() != 0) {
                    for(Edge edge : n.getNeighbors()) {
                        if(nodesSortedByLevel.get(e.getKey()-1).contains(edge.getNeighbor(n)))
                            n.setParent(edge.getNeighbor(n));
                    }
                }
                for(Edge edge : n.getNeighbors()) {
                    if(edge.getNeighbor(n).getLevel() == n.getLevel()+1)
                        n.getChildren().add(edge.getNeighbor(n));
                }
            }
        }


        // Place locks
        int keyLevel = 0;
        KeyLevelRoomMapping levels = new KeyLevelRoomMapping();
        nodesSortedByLevelIterator  = nodesSortedByLevel.entrySet().iterator();
        nodesSortedByLevelIterator.next();
        Node startRoomNode = sc.roomGraph.getNode(new ComparablePoint2i(sc.findStart().center()));
        levels.addRoom(keyLevel, startRoomNode);
        Condition cond = new Condition();
        startRoomNode.setPrecondition(cond);
        Symbol latestKey = null;
        int targetRoomsPerLock = sc.roomGraph.getNodes().size()/4;
        while(nodesSortedByLevelIterator.hasNext()) {
            Map.Entry<Integer, List<Node>> e = nodesSortedByLevelIterator.next();
            for(Node n : e.getValue()) {
                boolean doLock = false;

                if (shouldAddNewLock(keyLevel, levels.getRooms(keyLevel).size(), targetRoomsPerLock, sc)) {
                    latestKey = new Symbol(keyLevel++);
                    cond = cond.and(latestKey);
                    doLock = true;
                }

                n.setPrecondition(cond);

                sc.roomGraph.setEdgeSymbol(n.getParent(), n, doLock ? latestKey : null);
                levels.addRoom(keyLevel, n);
            }
        }

        // Find goal node
        TreeMap<int[], Node> nodesSortedByLevelAndDistance = new TreeMap<int[], Node>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                int i = -Integer.compare(o1[0], o2[0]);
                if(i != 0) return i;

                return -Integer.compare(o1[1], o2[1]);
            }
        });

        for(Node n : sc.roomGraph.getNodes().values()) {
            nodesSortedByLevelAndDistance.put(new int[]{n.getLevel(), n.getDistance()}, n);
        }

        Node goalRoomNode = null;
        Iterator<Map.Entry<int[], Node>> nodesSortedByLevelAndDistanceIterator = nodesSortedByLevelAndDistance.entrySet().iterator();
        while(nodesSortedByLevelAndDistanceIterator.hasNext()) {
            Map.Entry<int[], Node> entry = nodesSortedByLevelAndDistanceIterator.next();
            Node node = entry.getValue();
            if (node.getChildren().size() > 0 || node.getItem() != null) continue;
            Node parent = node.getParent();
            if (parent == null || parent.getChildren().size() != 1 ||
                    node.getItem() != null ||
                    !parent.getPrecondition().implies(node.getPrecondition()))
                continue;
            goalRoomNode = node;
            break;
        }
        goalRoomNode.setItem(new Symbol(Symbol.GOAL));
        sc.findRoom(new Point2i(goalRoomNode.getId().x , goalRoomNode.getId().y)).setItem(new Symbol(Symbol.GOAL));
        Node bossRoomNode = goalRoomNode.getParent();
        bossRoomNode.setItem(new Symbol(Symbol.BOSS));
        sc.findRoom(new Point2i(bossRoomNode.getId().x , bossRoomNode.getId().y)).setItem(new Symbol(Symbol.BOSS));

        int oldKeyLevel = bossRoomNode.getPrecondition().getKeyLevel(),
                newKeyLevel = Math.min(levels.keyCount(), 4);

        List<Node> oklRooms = levels.getRooms(oldKeyLevel);
        if (goalRoomNode != null) oklRooms.remove(goalRoomNode);
        oklRooms.remove(goalRoomNode.getParent());

        if (goalRoomNode != null) levels.addRoom(newKeyLevel, goalRoomNode);
        levels.addRoom(newKeyLevel, goalRoomNode.getParent());

        Symbol bossKey = new Symbol(newKeyLevel-1);
        Condition precond = bossRoomNode.getPrecondition().and(bossKey);
        bossRoomNode.setPrecondition(precond);
        if (goalRoomNode != null) goalRoomNode.setPrecondition(precond);

        if (newKeyLevel == 0) {
            // dungeon.link(bossRoom.getParent(), bossRoom);
        } else {
            for(Edge e : goalRoomNode.getParent().getNeighbors()) {
                Node neighbor = e.getNeighbor(goalRoomNode);
                if(neighbor != null) {
                    if (e.getNeighbor(goalRoomNode).equals(goalRoomNode.getParent())) {
                        e.setSymbol(bossKey);
                    }
                }
            }
        }
        return levels;
    }

    /**
     * Comparator objects for sorting {@link Room}s in a couple of different
     * ways. These are used to determine in which rooms of a given keyLevel it
     * is best to place the next key.
     *
     * @see #placeKeys
     */
    protected static final Comparator<Node>
            EDGE_COUNT_COMPARATOR = new Comparator<Node>() {
        @Override
        public int compare(Node arg0, Node arg1) {
            return arg0.getNeighborCount() - arg1.getNeighborCount();
        }
    },
            INTENSITY_COMPARATOR = new Comparator<Node>() {
                @Override
                public int compare(Node arg0, Node arg1) {
                    return arg0.getIntensity() > arg1.getIntensity() ? -1
                            : arg0.getIntensity() < arg1.getIntensity() ? 1
                            : 0;
                }
            };

    protected static final double
            INTENSITY_GROWTH_JITTER = 0.1,
            INTENSITY_EASE_OFF = 0.2;

    /**
     * Recursively applies the given intensity to the given {@link Room}, and
     * higher intensities to each of its descendants that are within the same
     * keyLevel.
     * <p>
     * Intensities set by this method may (will) be outside of the normal range
     * from 0.0 to 1.0. See {@link #normalizeIntensity} to correct this.
     *
     * @param room      the room to set the intensity of
     * @param intensity the value to set intensity to (some randomn variance is
     *                  added)
     * @see Room
     */
    protected double applyIntensity(Node node, double intensity) {
        intensity *= 1.0 - INTENSITY_GROWTH_JITTER/2.0 +
                INTENSITY_GROWTH_JITTER * random.nextDouble();

        node.setIntensity(intensity);

        double maxIntensity = intensity;
        for (Node child: node.getChildren()) {
            if (node.getPrecondition().implies(child.getPrecondition())) {
                maxIntensity = Math.max(maxIntensity, applyIntensity(child,
                        intensity + 1.0));
            }
        }

        return maxIntensity;
    }

    /**
     * Scales intensities within the dungeon down so that they all fit within
     * the range 0 <= intensity < 1.0.
     *
     * @see Room
     */
    protected void normalizeIntensity(Spacecraft sc) {
        double maxIntensity = 0.0;
        for (Node node: sc.roomGraph.getNodes().values()) {
            maxIntensity = Math.max(maxIntensity, node.getIntensity());
        }
        for (Node node : sc.roomGraph.getNodes().values()) {
            node.setIntensity(node.getIntensity() * 0.99 / maxIntensity);
        }
    }

    /**
     * Computes the 'intensity' of each {@link Room}. Rooms generally get more
     * intense the deeper they are into the dungeon.
     *
     * @param levels    the keyLevel -> room-set mapping to update
     * @throws RetryException if it fails
     * @see KeyLevelRoomMapping
     * @see Room
     */
    protected void computeIntensity(Spacecraft sc, KeyLevelRoomMapping levels) {

        double nextLevelBaseIntensity = 0.0;
        for (int level = 0; level < levels.keyCount(); ++level) {

            double intensity = nextLevelBaseIntensity *
                    (1.0 - INTENSITY_EASE_OFF);

            for (Node node: levels.getRooms(level)) {
                if (node.getParent() == null ||
                        !node.getParent().getPrecondition().
                                implies(node.getPrecondition())) {
                    nextLevelBaseIntensity = Math.max(
                            nextLevelBaseIntensity,
                            applyIntensity(node, intensity));
                }
            }
        }

        normalizeIntensity(sc);

        sc.roomGraph.getNode(new ComparablePoint2i(sc.findBoss().center())).setIntensity(1.0);
        com.carterza.generator.spacecraft.Room goalRoom = sc.findGoal();
        if (goalRoom != null)
            sc.roomGraph.getNode(new ComparablePoint2i(sc.findGoal().center())).setIntensity(0.0);
    }

        /*Node goalRoomNode = nodesSortedByLevelAndDistance.lastEntry().getValue();
        spacecraft.findRoom(goalRoomNode.getId()).setItem(new Symbol(Symbol.GOAL));
        spacecraft.findRoom(goalRoomNode.getParent().getId()).setItem(new Symbol(Symbol.BOSS));

        this.solutionPath = astar(
                new ComparablePoint2i(spacecraft.findStart().center())
                , new ComparablePoint2i(spacecraft.findGoal().center())
        );*/

    private boolean shouldAddNewLock(int keyLevel, int size, int targetRoomsPerLock, Spacecraft sc) {
        int usableKeys = 4;
        if (true)
            usableKeys -= 1;
        return size >= targetRoomsPerLock && keyLevel < usableKeys;
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
                        spec.setColor(com.badlogic.gdx.graphics.Color.WHITE);
                        spec.drawPixel(c * scaleFactor, r * scaleFactor);
                    } else if (grid[r][c].value == Pixel.FILLED) {
                        spec.setColor(com.badlogic.gdx.graphics.Color.BLACK);
                        spec.drawPixel(c * scaleFactor, r * scaleFactor);
                    } else if (grid[r][c].value == Pixel.SECONDARY) {
                        spec.setColor(com.badlogic.gdx.graphics.Color.BLACK);
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

    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }

    private class AStarClient implements AStar.IClient<ComparablePoint2i> {

        private int keyLevel;

        public AStarClient() {

        }

        @Override
        public Collection<ComparablePoint2i> getNeighbors(ComparablePoint2i roomId) {
            List<ComparablePoint2i> ids = new ArrayList<ComparablePoint2i>();
            Node currentNode = spacecraft.roomGraph.getNode(roomId);
            for (com.carterza.generator.graph.Edge edge: currentNode.getNeighbors()) {
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

    private ArrayList<com.stuckinadrawer.Room> createRooms() {
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

    public com.carterza.generator.spacecraft.Room findRoomContainingPoint(net.bytten.gameutil.Vec2I position) {
        com.carterza.generator.spacecraft.Room room = null;
        for(com.carterza.generator.spacecraft.Room r : this.spacecraft.rooms) {
            if(r.contains(position.x, position.y)) room = r;
        }
        return room;
    }

    public Tile[][] getLevel() {
        return this.level;
    }

    public class KeyLevelRoomMapping {
        protected List<List<Node>> map = new ArrayList<List<Node>>(
                4);

        List<Node> getRooms(int keyLevel) {
            while (keyLevel >= map.size()) map.add(null);
            if (map.get(keyLevel) == null)
                map.set(keyLevel, new ArrayList<Node>());
            return map.get(keyLevel);
        }

        void addRoom(int keyLevel, Node node) {
            getRooms(keyLevel).add(node);
        }

        int keyCount() {
            return map.size();
        }
    }


}
