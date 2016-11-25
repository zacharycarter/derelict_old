package com.carterza.generator.graph;

import com.stewsters.util.math.Point2i;

/**
 * Created by zachcarter on 11/3/16.
 */
public class ComparablePoint2i extends Point2i implements Comparable<ComparablePoint2i> {

    public ComparablePoint2i(int x, int y) {
        super(x, y);
    }

    public ComparablePoint2i(Point2i point2i) {
        super(point2i.x, point2i.y);
    }

    @Override
    public int compareTo(ComparablePoint2i o) {
        int i = Integer.compare(x, o.x);
        if (i != 0) return i;

        return Integer.compare(y, o.y);
    }
}
