package com.carterza.action;

import com.carterza.Derelict;
import com.carterza.actor.Actor;
import com.carterza.actor.ActorOld;

/**
 * Created by zachcarter on 11/21/16.
 */
abstract public class ActionOld {
    private ActorOld actor;
    private Derelict game;
    int cost;

    public ActionOld(ActorOld actor, int cost) {
        this.actor = actor;
        this.cost = cost;
    }

    public ActorOld getActor() {
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
