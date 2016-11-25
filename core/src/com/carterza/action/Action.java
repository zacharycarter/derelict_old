package com.carterza.action;

import com.carterza.Derelict;
import com.carterza.actor.Actor;
import com.carterza.world.WorldUpdate;

/**
 * Created by zachcarter on 11/21/16.
 */
abstract public class Action {
    private Actor actor;
    private Derelict game;
    int cost;

    public Action(Actor actor, int cost) {
        this.actor = actor;
        this.cost = cost;
    }

    public Actor getActor() {
        return actor;
    }

    public Derelict getGame() {
        return game;
    }

    public abstract ActionResult perform();

    public int getCost() {
        return this.cost;
    }
}
