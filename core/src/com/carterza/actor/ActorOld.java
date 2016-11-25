package com.carterza.actor;

import com.carterza.world.World;
import com.carterza.world.WorldOld;

public abstract class ActorOld {
    private final long id;
    final static double BASE_DELAY = 10;
    int speed;
    final WorldOld world;

    public ActorOld(long id, WorldOld world, int speed)
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
}
