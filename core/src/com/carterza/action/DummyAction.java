package com.carterza.action;

import com.carterza.actor.Actor;

/**
 * Created by zachcarter on 11/21/16.
 */
public class DummyAction extends Action {

    public DummyAction(Actor actor) {
        super(actor, 100);
    }

    @Override
    public ActionResult perform() {
        System.out.println("Actor with id : " + getActor().getId() + " performing dummy action!");
        return new ActionResult(this, true);
    }
}
