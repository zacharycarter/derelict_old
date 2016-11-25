package com.carterza.generator.spacecraft;

import com.carterza.generator.util.PixelGridUtil;
import com.stewsters.util.math.Point2i;

import java.util.concurrent.ThreadLocalRandom;

public class BlueprintGenerator {
    private int rows = 0;
    private int cols = 0;

    public Pixel[][] create(AssetSize size) {

        if (size.equals(AssetSize.RANDOM)) {
            int rowCol = ThreadLocalRandom.current().nextInt(100, 300 + 1);
            rows = rowCol;
            cols = rowCol;
        } else if (size.equals(AssetSize.SMALL)) {
            rows = 100;
            cols = 100;
        } else if (size.equals(AssetSize.MEDIUM)) {
            rows = 200;
            cols = 200;
        } else if (size.equals(AssetSize.LARGE)) {
            rows = 300;
            cols = 300;
        }

        Pixel[][] grid = createBaseGrid(size);
        addExtras(grid, size);

        grid = PixelGridUtil.floor(grid);
        grid = PixelGridUtil.mirrorCopyGridHorizontally(grid);
        grid = PixelGridUtil.addBorders(grid);
        grid = PixelGridUtil.floor(grid);
        PixelGridUtil.fillEmptySurroundedPixelsInGrid(grid);
        PixelGridUtil.addNoiseToFlatPixels(grid);
        PixelGridUtil.setPixelDepth(grid);

        if (validateGrid(grid, size)) {
            return grid;
        } else {
            return create(size);
        }
    }

    private boolean validateGrid(Pixel[][] grid, AssetSize size) {

        boolean result = true;

        int noOfFilledPixels = 0;
        int noOfSecondaryPixels = 0;

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y].value == Pixel.FILLED) {
                    noOfFilledPixels++;
                } else if (grid[x][y].value == Pixel.SECONDARY) {
                    noOfSecondaryPixels++;
                }
            }
        }

        if (noOfSecondaryPixels == 0) {
            result = false;
        }

        if (noOfSecondaryPixels > (noOfFilledPixels / 4)) {
            result = false;
        }

        return result;
    }

    private Pixel[][] createBaseGrid(AssetSize size) {

        Pixel[][] grid = new Pixel[rows][cols];
        PixelGridUtil.initEmptyGrid(grid, rows, cols);

        Point2i point = new Point2i(rows / 2, cols - 1);

        int steps = ThreadLocalRandom.current().nextInt(calculateMinNoOfSteps(size), calculateMaxNoOfSteps(size) + 1);
        int subSteps = ThreadLocalRandom.current().nextInt(calculateMinNoOfSubSteps(size), calculateMaxNoOfSubSteps(size) + 1);

        for (int i = 0; i < steps; i++) {

            if (point == null) {
                // we are passed the first step lets find the lowest most pixel
                // that is closest to the middle, and go again from there...

                // top down
                for (int x = 0; x < rows; x++) {
                    // left to right
                    for (int y = 0; y < cols; y++) {
                        if (grid[x][y].value == Pixel.FILLED) {
                            point = new Point2i(x, y);
                        }
                    }
                }
            }

            for (int y = 0; y < subSteps; y++) {
                point = processPoint(point, grid);
            }

            point = null;
        }

        return grid;
    }

    private void addExtras(Pixel[][] grid, AssetSize size) {

        int steps = ThreadLocalRandom.current().nextInt(calculateMinNoOfSteps(size) - 10, (calculateMaxNoOfSteps(size) - 10) + 1);
        int subSteps = ThreadLocalRandom.current().nextInt(calculateMinNoOfSubSteps(size) - 10, (calculateMaxNoOfSubSteps(size) - 10) + 1);

        for (int i = 0; i < steps; i++) {
            Point2i point = PixelGridUtil.getRandomFilledPoint(grid);

            for (int y = 0; y < subSteps; y++) {
                point = processPoint(point, grid);
            }
        }
    }

    private Point2i processPoint(Point2i point, Pixel[][] grid) {

        if (grid[point.x][point.y].value == Pixel.EMPTY) {
            grid[point.x][point.y].value = Pixel.FILLED;
        }

        return PixelGridUtil.getRandomAdjacentPoint(point, grid);
    }

    private int calculateMinNoOfSteps(AssetSize size) {

        int result = 0;

        if (size.equals(AssetSize.RANDOM)) {
            result = 5;
        } else if (size.equals(AssetSize.SMALL)) {
            result = 5;
        } else if (size.equals(AssetSize.MEDIUM)) {
            result = 10;
        } else if (size.equals(AssetSize.LARGE)) {
            result = 20;
        }

        return result;
    }

    private int calculateMaxNoOfSteps(AssetSize size) {

        int result = 0;

        if (size.equals(AssetSize.RANDOM)) {
            result = 50;
        } else if (size.equals(AssetSize.SMALL)) {
            result = 15;
        } else if (size.equals(AssetSize.MEDIUM)) {
            result = 30;
        } else if (size.equals(AssetSize.LARGE)) {
            result = 50;
        }

        return result;
    }

    private int calculateMinNoOfSubSteps(AssetSize size) {

        int result = 1;

        if (size.equals(AssetSize.RANDOM)) {
            result = 5;
        } else if (size.equals(AssetSize.SMALL)) {
            result = 5;
        } else if (size.equals(AssetSize.MEDIUM)) {
            result = 10;
        } else if (size.equals(AssetSize.LARGE)) {
            result = 15;
        }

        return result;
    }

    private int calculateMaxNoOfSubSteps(AssetSize size) {

        int result = 0;

        if (size.equals(AssetSize.RANDOM)) {
            result = 50;
        } else if (size.equals(AssetSize.SMALL)) {
            result = 30;
        } else if (size.equals(AssetSize.MEDIUM)) {
            result = 40;
        } else if (size.equals(AssetSize.LARGE)) {
            result = 50;
        }

        return result;
    }
}
