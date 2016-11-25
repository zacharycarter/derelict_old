package com.carterza.math;

public enum Direction {

    N(0, 1),
    NN(0, 2),
    E(1, 0),
    EE(2, 0),
    S(0, -1),
    SS(0, -2),
    W(-1, 0),
    WW(-2, 0),
    O(0, 0),

    ;

    public static final Direction[] CARDINALS = new Direction[]{N, E, S, W},
            CARDINALS_WITH_O = new Direction[]{N, E, S, W, O},
            LEFT_RIGHT = new Direction[]{W, E},
            LEFT_RIGHT_WITH_O = new Direction[]{W, E, O},
            UP_DOWN = new Direction[]{N, S},
            UP_DOWN_WITH_O = new Direction[]{N, S, O};

    public final int x, y;
    private final Vec2I coords;
    private final Vec2D vec2D;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
        this.coords = new Vec2I(x,y);
        this.vec2D = new Vec2D(x,y);
    }

    public Vec2D toVec2D() {
        return vec2D;
    }

    public Vec2I toCoords() {
        return coords;
    }

    public Direction nextClockwise() {
        switch (this) {
            case N:
                return E;
            case E:
                return S;
            case S:
                return W;
            case W:
                return N;
            default:
                return O;
        }
    }

    public Direction nextAnticlockwise() {
        return nextClockwise().opposite();
    }

    public Direction opposite() {
        switch (this) {
            case N: return S;
            case E: return W;
            case S: return N;
            case W: return E;
            default:
                return O;
        }
    }

}
