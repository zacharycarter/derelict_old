package com.carterza.action;

import com.carterza.actor.Actor;

/**
 * Created by zachcarter on 11/22/16.
 */
public class DummySleepAction extends Action {
    public DummySleepAction(Actor actor) {
        super(actor, 100);
    }

    @Override
    public ActionResult perform() {
        System.out.println("Actor with id : " + getActor().getId() + " performing dummy sleep action!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ActionResult(this, true);
    }
}
