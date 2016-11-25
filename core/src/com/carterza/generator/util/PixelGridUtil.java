package com.carterza.generator.util;

import com.carterza.generator.spacecraft.Pixel;
import com.stewsters.util.math.Point2i;

import java.util.concurrent.ThreadLocalRandom;

public class PixelGridUtil {

    /**
     * Returns true if the passed in point is within the boundaries of the
     * passed in grid i.e. in a grid initialise with 10 rows and 10 columns
     * points with x or y above 10 are not within the grid.
     *
     * @param point {@link Point}
     * @param grid  {@link Pixel}[][]
     * @return {@link boolean}
     */
    public static boolean isPointWithinGrid(Point2i point, Pixel[][] grid) {

        try {
            @SuppressWarnings("unused")
            Pixel pixel = grid[point.x][point.y];
            return true;
        } catch (IndexOutOfBoundsException ioobe) {
            return false;
        }

    }

    /**
     * Outputs the passed in grid to the console
     *
     * @param grid {@link Pixel}[][]
     */
    public static void outputGridAsAscii(Pixel[][] grid) {

        for (int x = 0; x < grid.length; x++) {

            StringBuilder strBld = new StringBuilder();

            for (int y = 0; y < grid[0].length; y++) {

                if (grid[x][y].value == Pixel.EMPTY) {
                    strBld.append(" ");
                } else if (grid[x][y].value == Pixel.FILLED) {
                    strBld.append(".");
                } else if (grid[x][y].value == Pixel.BORDER) {
                    strBld.append("x");
                }
            }

            System.out.println(strBld.toString());
        }
    }

    /**
     * Simply populates the passed in grid with {@link Pixel} objects
     *
     * @param grid {@link Pixel}[][]
     * @param rows {@link int}
     * @param cols {@link int}
     */
    public static void initEmptyGrid(Pixel[][] grid, int rows, int cols) {

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                grid[x][y] = new Pixel();
            }
        }
    }

    /**
     * Takes the passed in grid and returns a grid that has been floored i.e.
     * reduced so that rows and column with no filled pixels on them are removed
     *
     * @param grid {@link Pixel}[][]
     * @return {@link Pixel}[][]
     */
    public static Pixel[][] floor(Pixel[][] grid) {

        Pixel[][] flooredGrid = null;

        int lastFilledRow = 0;
        int lastFilledCol = 0;
        int firstFilledRow = grid.length;
        int firstFilledColumn = grid[0].length;

        for (int r = 0; r < grid.length; r++) {
            boolean empty = true;

            for (int c = 0; c < grid[0].length; c++) {
                boolean colEmpty = true;

                if (grid[r][c].value != Pixel.EMPTY) {
                    if (firstFilledRow > r) {
                        firstFilledRow = r;
                    }

                    if (firstFilledColumn > c) {
                        firstFilledColumn = c;
                    }

                    empty = false;
                    colEmpty = false;
                }

                if (!colEmpty && c > lastFilledCol) {
                    lastFilledCol = c;
                }
            }

            if (!empty) {
                lastFilledRow = r;
            }
        }

        flooredGrid = new Pixel[lastFilledRow - (firstFilledRow - 1)][lastFilledCol - (firstFilledColumn - 1)];

        int newRow = 0;

        for (int r = firstFilledRow; r < lastFilledRow + 1; r++) {
            int newCol = 0;

            for (int c = firstFilledColumn; c < lastFilledCol + 1; c++) {
                flooredGrid[newRow][newCol] = grid[r][c];
                newCol++;
            }

            newRow++;
        }

        return flooredGrid;
    }

    /**
     * Takes the passed in grid and returns a grid that contains the original
     * plus a mirrored copy
     *
     * @param halfGrid {@link Pixel}[][]
     * @return {@link Pixel}[][]
     */
    public static Pixel[][] mirrorCopyGridHorizontally(Pixel[][] halfGrid) {

        int rows = halfGrid.length;
        int cols = (halfGrid[0].length * 2);

        Pixel[][] fullGrid = new Pixel[rows][cols];

        // Copy left to right
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < (cols / 2); c++) {
                fullGrid[r][c] = halfGrid[r][c];
                fullGrid[r][(cols - 1) - c] = halfGrid[r][c];
            }
        }

        return fullGrid;
    }

    /**
     * Takes the passed in grid and returns a grid that contains the original
     * plus a mirrored copy
     *
     * @param halfGrid {@link Pixel}[][]
     * @return {@link Pixel}[][]
     */
    public static Pixel[][] mirrorCopyGridVertically(Pixel[][] halfGrid) {

        int rows = (halfGrid.length * 2);
        int cols = halfGrid[0].length;

        Pixel[][] fullGrid = new Pixel[rows][cols];

        // Copy left to right
        for (int r = 0; r < rows / 2; r++) {
            for (int c = 0; c < cols; c++) {
                fullGrid[r][c] = halfGrid[r][c];
                fullGrid[(rows - 1) - r][c] = halfGrid[r][c];
            }
        }

        return fullGrid;
    }

    /**
     * Takes the passed in grid extends it by 2 then puts a border pixel next to
     * every edge pixel i.e. pixels that do not have another filled pixel next
     * to them
     *
     * @param grid {@link Pixel}[][]
     * @return {@link Pixel}[][]
     */
    public static Pixel[][] addBorders(Pixel[][] grid) {

        Pixel[][] result = PixelGridUtil.extendGrid(grid, 2);

        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[0].length; c++) {
                if (result[r][c].value == Pixel.FILLED) {

                    // Top
                    if (result[r == 0 ? 0 : r - 1][c].value != Pixel.FILLED &&
                            result[r == 0 ? 0 : r - 1][c].value != Pixel.SECONDARY) {
                        result[r == 0 ? 0 : r - 1][c].value = Pixel.BORDER;
                    }

                    // Left
                    if (result[r][c == 0 ? 0 : c - 1].value != Pixel.FILLED &&
                            result[r][c == 0 ? 0 : c - 1].value != Pixel.SECONDARY) {
                        result[r][c == 0 ? 0 : c - 1].value = Pixel.BORDER;
                    }

                    // Right
                    if (result[r][c == (result[0].length / 2) - 1 ? (result[0].length / 2) - 1 : c + 1].value != Pixel.FILLED &&
                            result[r][c == (result[0].length / 2) - 1 ? (result[0].length / 2) - 1 : c + 1].value != Pixel.SECONDARY) {
                        result[r][c == (result[0].length / 2) - 1 ? (result[0].length / 2) - 1 : c + 1].value = Pixel.BORDER;
                    }

                    // Bottom
                    if (result[r == result.length - 1 ? result.length - 1 : r + 1][c].value != Pixel.FILLED &&
                            result[r == result.length - 1 ? result.length - 1 : r + 1][c].value != Pixel.SECONDARY) {
                        result[r == result.length - 1 ? result.length - 1 : r + 1][c].value = Pixel.BORDER;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Takes the passed in grid and extends it by the passed in amount
     *
     * @param grid         {@link Pixel}[][]
     * @param extendAmount {@link int}
     * @return {@link Pixel}[][]
     */
    public static Pixel[][] extendGrid(Pixel[][] grid, int extendAmount) {
        Pixel[][] extendedGrid = new Pixel[grid.length + extendAmount][grid[0].length + extendAmount];
        initEmptyGrid(extendedGrid, grid.length + extendAmount, grid[0].length + extendAmount);

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                extendedGrid[r + (extendAmount / 2)][c + (extendAmount / 2)] = grid[r][c];
            }
        }

        return extendedGrid;
    }

    /**
     * Loops through each pixel in the grid and works out if they are surrounded
     * by filled pixels i.e. a straight path through other pixels until it hits
     * a pixel of value Pixel.FILLED
     *
     * @param grid {@link Pixel}[][]
     */
    public static void fillEmptySurroundedPixelsInGrid(Pixel[][] grid) {

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {

                if (grid[r][c].value == Pixel.EMPTY) {

                    boolean filledPixelAbove = false;
                    boolean filledPixelBelow = false;
                    boolean filledPixelOnTheLeft = false;
                    boolean filledPixelOnTheRight = false;

                    for (int r1 = r - 1; r1 > 0; r1--) {

                        if (grid[r1][c].value == Pixel.FILLED) {
                            filledPixelAbove = true;
                            break;
                        }

                    }

                    for (int r1 = r + 1; r1 < grid.length; r1++) {

                        if (grid[r1][c].value == Pixel.FILLED) {
                            filledPixelBelow = true;
                            break;
                        }

                    }

                    for (int c1 = c - 1; c1 > 0; c1--) {

                        if (grid[r][c1].value == Pixel.FILLED) {
                            filledPixelOnTheLeft = true;
                            break;
                        }

                    }

                    for (int c1 = c + 1; c1 < grid[0].length; c1++) {

                        if (grid[r][c1].value == Pixel.FILLED) {
                            filledPixelOnTheRight = true;
                            break;
                        }

                    }

                    if (filledPixelAbove && filledPixelBelow && filledPixelOnTheLeft && filledPixelOnTheRight) {
                        grid[r][c].value = Pixel.SECONDARY;
                    }
                }
            }
        }
    }

    /**
     * Loops through each pixel in the grid and works out if they are surrounded
     * by filled pixels i.e. a straight path through other pixels until it hits
     * a pixel of value Pixel.FILLED without going over any pixels with value
     * Pixel.EMPTY
     *
     * @param grid {@link Pixel}[][]
     */
    public static void addNoiseToFlatPixels(Pixel[][] grid) {

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {

                if (grid[r][c].value == Pixel.SECONDARY) {

                    boolean filledPixelAbove = false;
                    boolean filledPixelBelow = false;
                    boolean filledPixelOnTheLeft = false;
                    boolean filledPixelOnTheRight = false;

                    for (int r1 = r - 1; r1 > 0; r1--) {
                        if (grid[r1][c].value == Pixel.EMPTY) {
                            filledPixelAbove = false;
                            break;
                        } else if (grid[r1][c].value == Pixel.FILLED) {
                            filledPixelAbove = true;
                            break;
                        }
                    }

                    for (int r1 = r + 1; r1 < grid.length; r1++) {
                        if (grid[r1][c].value == Pixel.EMPTY) {
                            filledPixelBelow = false;
                            break;
                        } else if (grid[r1][c].value == Pixel.FILLED) {
                            filledPixelBelow = true;
                            break;
                        }
                    }

                    for (int c1 = c - 1; c1 > 0; c1--) {
                        if (grid[r][c1].value == Pixel.EMPTY) {
                            filledPixelOnTheLeft = false;
                            break;
                        } else if (grid[r][c1].value == Pixel.FILLED) {
                            filledPixelOnTheLeft = true;
                            break;
                        }
                    }

                    for (int c1 = c + 1; c1 < grid[0].length; c1++) {
                        if (grid[r][c1].value == Pixel.EMPTY) {
                            filledPixelOnTheLeft = false;
                            break;
                        } else if (grid[r][c1].value == Pixel.FILLED) {
                            filledPixelOnTheRight = true;
                            break;
                        }
                    }

                    if (filledPixelAbove && filledPixelBelow && filledPixelOnTheLeft && filledPixelOnTheRight) {

                        grid[r][c].value = Pixel.SECONDARY;

                        int random = ThreadLocalRandom.current().nextInt(1, (100) + 1);

                        if (random < 10) {
                            grid[r][c].value = Pixel.BORDER;
                        } else if (random > 90) {
                            grid[r][c].value = Pixel.FILLED;
                        }
                    }
                }
            }
        }
    }

    public static Point2i getRandomFilledPoint(Pixel[][] grid) {

        Point2i point = null;

        while (point == null) {

            int x = ThreadLocalRandom.current().nextInt(1, (grid.length - 1) + 1);
            int y = ThreadLocalRandom.current().nextInt(1, (grid[0].length - 1) + 1);

            Pixel possiblePixel = grid[x][y];

            if (possiblePixel.value == Pixel.FILLED) {
                point = new Point2i(x, y);
            }
        }

        return point;

    }

    public static void mergeGridsRandomly(Pixel[][] sourceGrid, Pixel[][] targetGrid) {

        int attempts = 0;

        Point2i targetPoint = null;

        while (targetPoint == null && attempts < 10) {

            Point2i potentialPoint = getRandomFilledPoint(targetGrid);
            potentialPoint.x = potentialPoint.x - sourceGrid.length - 1;
            potentialPoint.y = potentialPoint.y - sourceGrid[0].length - 1;

            if (isPointWithinGrid(potentialPoint, targetGrid)) {
                targetPoint = potentialPoint;
            }

            attempts++;
        }

        if (targetPoint != null) {

            int startY = targetPoint.y;

            for (int r = 0; r < sourceGrid.length; r++) {
                for (int c = 0; c < sourceGrid[0].length; c++) {
                    targetGrid[targetPoint.x][targetPoint.y].value = sourceGrid[r][c].value;
                    targetPoint.y = targetPoint.y + 1;
                }

                targetPoint.x = targetPoint.x + 1;
                targetPoint.y = startY;
            }
        }
    }

    public static Pixel[][] floorHorizontally(Pixel[][] grid) {

        Pixel[][] flooredGrid = null;

        int lastFilledCol = 0;
        int firstFilledColumn = grid[0].length;

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                boolean colEmpty = true;

                if (grid[r][c].value != Pixel.EMPTY) {

                    if (firstFilledColumn > c) {
                        firstFilledColumn = c;
                    }

                    colEmpty = false;
                }

                if (!colEmpty && c > lastFilledCol) {
                    lastFilledCol = c;
                }
            }
        }

        flooredGrid = new Pixel[grid.length][lastFilledCol - (firstFilledColumn - 1)];

        int newRow = 0;

        for (int r = 0; r < grid.length; r++) {
            int newCol = 0;

            for (int c = firstFilledColumn; c < lastFilledCol + 1; c++) {
                flooredGrid[newRow][newCol] = grid[r][c];
                newCol++;
            }

            newRow++;
        }

        return flooredGrid;
    }

    public static Pixel[][] floorVertically(Pixel[][] grid) {

        Pixel[][] flooredGrid = null;

        int lastFilledRow = 0;
        int firstFilledRow = grid.length;

        for (int r = 0; r < grid.length; r++) {
            boolean empty = true;

            for (int c = 0; c < grid[0].length; c++) {

                if (grid[r][c].value != Pixel.EMPTY) {
                    if (firstFilledRow > r) {
                        firstFilledRow = r;
                    }

                    empty = false;
                }
            }

            if (!empty) {
                lastFilledRow = r;
            }
        }

        flooredGrid = new Pixel[lastFilledRow - (firstFilledRow - 1)][grid[0].length];

        int newRow = 0;

        for (int r = firstFilledRow; r < lastFilledRow + 1; r++) {
            int newCol = 0;

            for (int c = 0; c < grid[0].length; c++) {
                flooredGrid[newRow][newCol] = grid[r][c];
                newCol++;
            }

            newRow++;
        }

        return flooredGrid;
    }

    public static Pixel[][] combineGrids(java.util.List<Pixel[][]> grids) {

        int maxRows = 0;
        int maxCols = grids.get(0)[0].length;

        for (Pixel[][] grid : grids) {
            maxRows += grid.length;
        }

        Pixel[][] combinedGrid = new Pixel[maxRows][maxCols];
        initEmptyGrid(combinedGrid, maxRows, maxCols);

        int startingRow = 0;

        for (Pixel[][] grid : grids) {
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < maxCols; c++) {

                    combinedGrid[startingRow + r][c].value = grid[r][c].value;
                }
            }

            startingRow += grid.length;
        }

        return combinedGrid;
    }

    public static void setPixelDepth(Pixel[][] grid) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {

                if (grid[r][c].value != Pixel.EMPTY &&
                        grid[r][c].value != Pixel.BORDER) {

                    int pixelValue = grid[r][c].value;

                    int noOfSamePixelsAbove = 0;
                    int noOfSamePixelsBelow = 0;
                    int noOfSamePixelsOnTheLeft = 0;
                    int noOfSamePixelsOnTheRight = 0;

                    for (int r1 = r - 1; r1 > 0; r1--) {
                        if (pixelValue == grid[r1][c].value) {
                            noOfSamePixelsAbove++;
                        }
                    }

                    for (int r1 = r + 1; r1 < grid.length; r1++) {
                        if (pixelValue == grid[r1][c].value) {
                            noOfSamePixelsBelow++;
                        }
                    }

                    for (int c1 = c - 1; c1 > 0; c1--) {
                        if (pixelValue == grid[r][c1].value) {
                            noOfSamePixelsOnTheLeft++;
                        }
                    }

                    for (int c1 = c + 1; c1 < grid[0].length; c1++) {
                        if (pixelValue == grid[r][c1].value) {
                            noOfSamePixelsOnTheRight++;
                        }
                    }

                    int depth1 = Math.min(noOfSamePixelsAbove, noOfSamePixelsBelow);
                    int depth2 = Math.min(noOfSamePixelsOnTheLeft, noOfSamePixelsOnTheRight);

                    grid[r][c].depth = Math.min(depth1, depth2);
                }
            }
        }
    }

    public static Point2i getRandomAdjacentPoint(Point2i point, Pixel[][] grid) {

        // top
        Point2i pointTop = new Point2i(point.x - 1, point.y);

        // bottom
        Point2i pointBottom = new Point2i(point.x + 1, point.y);

        // left
        Point2i pointLeft = new Point2i(point.x, point.y - 1);

        // right
        Point2i pointRight = new Point2i(point.x, point.y + 1);

        boolean pointTopValid = PixelGridUtil.isPointWithinGrid(pointTop, grid);
        boolean pointBottomValid = PixelGridUtil.isPointWithinGrid(pointBottom, grid);
        boolean pointLeftValid = PixelGridUtil.isPointWithinGrid(pointLeft, grid);
        boolean pointRightValid = PixelGridUtil.isPointWithinGrid(pointRight, grid);

        int noOfValidNeighbours = 0;

        if (pointTopValid) {
            noOfValidNeighbours++;
        }

        if (pointBottomValid) {
            noOfValidNeighbours++;
        }

        if (pointLeftValid) {
            noOfValidNeighbours++;
        }

        if (pointRightValid) {
            noOfValidNeighbours++;
        }

        Point2i[] neighbours = new Point2i[noOfValidNeighbours + 1];

        int index = 0;
        if (pointTopValid) {
            neighbours[index] = pointTop;
            index++;
        }

        if (pointBottomValid) {
            neighbours[index] = pointBottom;
            index++;
        }

        if (pointLeftValid) {
            neighbours[index] = pointLeft;
            index++;
        }

        if (pointRightValid) {
            neighbours[index] = pointRight;
            index++;
        }

        // go to a random neighbour
        Point2i newPoint = null;

        while (newPoint == null) {
            int ri = ThreadLocalRandom.current().nextInt(0, (neighbours.length - 1) + 1);

            if (neighbours[ri] != null) {
                newPoint = neighbours[ri];
            }
        }

        return newPoint;
    }

    public static void removePixelsByType(Pixel[][] grid, int type) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c].value == type) {
                    grid[r][c].value = Pixel.EMPTY;
                }
            }
        }
    }

    public static int countPixelsByType(Pixel[][] grid, int type) {

        int result = 0;

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c].value == type) {
                    result++;
                }
            }
        }

        return result;
    }
}
