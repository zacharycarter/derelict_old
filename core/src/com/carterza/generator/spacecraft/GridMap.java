package com.carterza.generator.spacecraft;

import com.stewsters.util.mapgen.CellType;
import com.stewsters.util.mapgen.twoDimension.GeneratedMap2d;
import com.stewsters.util.pathing.twoDimention.shared.Mover2d;
import com.stewsters.util.pathing.twoDimention.shared.PathNode2d;
import com.stewsters.util.pathing.twoDimention.shared.TileBasedMap2d;

public class GridMap implements TileBasedMap2d, GeneratedMap2d {

    public TileType[][] map;
    private boolean[][] partOfShip;
    private int width;
    private int height;
    Spacecraft spacecraft;


    public GridMap(int xSize, int ySize) {

        width = xSize;
        height = ySize;

        map = new TileType[width][height];
        partOfShip = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = TileType.AETHER;
                partOfShip[x][y] = false;
            }
        }


    }

    public GridMap(int xSize, int ySize, Spacecraft spacecraft) {

        this.spacecraft = spacecraft;

        width = xSize;
        height = ySize;

        map = new TileType[width][height];
        partOfShip = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = TileType.AETHER;
                partOfShip[x][y] = false;
            }
        }


    }


    public void writeToBothSides(int x, int y, TileType tileType) {

        map[x][y] = tileType;
        map[width - 1 - x][y] = tileType;

    }

    public void write(int x, int y, TileType tileType) {

        map[x][y] = tileType;

    }

    public boolean testBothSides(int x, int y, TileType tileType) {

        return map[x][y] == tileType &&
                map[width - 1 - x][y] == tileType;
    }

    public boolean test(int x, int y, TileType tileType) {

        return map[x][y] == tileType;
    }

    public void writeRoom(int xLow, int yLow, int xHigh, int yHigh, TileType floorType, TileType wallType, boolean asymmetric) {

        for (int x = xLow; x <= xHigh; x++) {
            for (int y = yLow; y <= yHigh; y++) {
                if (x == xLow || y == yLow || x == xHigh || y == yHigh) {
                    if(!asymmetric) writeToBothSides(x, y, wallType);
                    else write(x, y, wallType);
                } else {
                    if(!asymmetric) writeToBothSides(x, y, floorType);
                    else write(x, y, floorType);
                }
            }
        }
    }

    public boolean testRoom(int xLow, int yLow, int xHigh, int yHigh, TileType tileType) {

        if (xLow < 0 || yLow < 0 || xHigh >= width / 2 || yHigh >= height)
            return false;

        for (int x = xLow; x <= xHigh; x++) {
            for (int y = yLow; y <= yHigh; y++) {
//                if (x < 0 || x >= map.length / 2)
//                    return false;

                if (!testBothSides(x, y, tileType)) {
                    return false;
                }

            }
        }
        return true;
    }

    public boolean testAsymmetricRoom(int xLow, int yLow, int xHigh, int yHigh, TileType tileType) {

        if (xLow < 0 || yLow < 0 || xHigh >= width || yHigh >= height)
            return false;

        for (int x = xLow; x <= xHigh; x++) {
            for (int y = yLow; y <= yHigh; y++) {
//                if (x < 0 || x >= map.length / 2)
//                    return false;

                if (!test(x, y, tileType)) {
                    return false;
                }

            }
        }
        return true;
    }


    @Override
    public int getWidthInTiles() {
        return width;
    }

    @Override
    public int getHeightInTiles() {
        return height;
    }

    @Override
    public CellType getCellTypeAt(int x, int y) {
        if (outsideMap(x, y)) {
            return null;
        }

        return map[x][y];
    }

    @Override
    public void setCellTypeAt(int x, int y, CellType cellType) {

        map[x][y] = (TileType) cellType;
    }


    public boolean outsideMap(int x, int y) {
        return (x < 0 || x >= width || y < 0 || y >= height);
    }

    public TileType getTile(int x, int y) {
        return map[x][y];
    }


    public void trimNonContiguous(int xStart, int yStart) {

        partOfShip[xStart][yStart] = true;

        // expand
        int added = 1;

        while (added > 0) {
            added = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (map[x][y] == TileType.INTERNALS && partOfShip[x][y] == false) {

                        if (isPartOfShip(x + 1, y) ||
                                isPartOfShip(x - 1, y) ||
                                isPartOfShip(x, y + 1) ||
                                isPartOfShip(x, y - 1)) {
                            partOfShip[x][y] = true;
                            added++;
                        }
                    }

                }
            }

        }


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!partOfShip[x][y])
                    map[x][y] = TileType.AETHER;
            }
        }

    }


    // Tests a location to see if its already part
    private boolean isPartOfShip(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        return partOfShip[x][y];

    }

    public TileType[] getRow(int row) {
        return map[row];
    }

    @Override
    public void pathFinderVisited(int x, int y) {

    }

    @Override
    public boolean isBlocked(Mover2d mover, PathNode2d toNode) {
        return false;
    }

    @Override
    public boolean isBlocked(Mover2d mover, int x1, int y1) {
        return false;
    }

    @Override
    public float getCost(Mover2d mover, int sx, int sy, int tx, int ty) {
        return 0;
    }
}
