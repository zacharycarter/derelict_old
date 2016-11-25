package com.carterza.action;


import com.carterza.math.Vec2I;

/**
 * Created by zachcarter on 11/21/16.
 */
public class MoveActionResult extends  ActionResult {
    private Vec2I positionMovedTo;

    public MoveActionResult(Action action, boolean succeeded, Vec2I positionMovedTo) {
        super(action, succeeded);
        this.positionMovedTo = positionMovedTo;
    }


    public Vec2I getPositionMovedTo() {
        return positionMovedTo;
    }
}
