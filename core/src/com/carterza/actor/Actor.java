package com.carterza.actor;

import com.carterza.action.ActionManager;
import com.carterza.action.DummyAction;
import com.carterza.component.AnimationComponent;
import com.carterza.math.Vec2I;
import com.carterza.world.World;

public abstract class Actor {
    private final long id;
    final static double BASE_DELAY = 10;
    int speed;
    private final World world;

    public Actor(long id, World world, int speed)
    {
        this.id = id;
        this.world = world;
        this.speed = speed;
    }

    public long getId() {
        return id;
    }

    public boolean needsInput() {
        return false;
    }

    public double actionDelay() {
        return BASE_DELAY / this.speed;
    }

    public double actionDelay(double delay) {
        return delay / this.speed;
    }

    public abstract int act();

    public World getWorld() {
        return this.world;
    }
}
