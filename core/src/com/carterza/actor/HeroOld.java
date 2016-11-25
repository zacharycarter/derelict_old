package com.carterza.actor;

import com.carterza.action.Action;
import com.carterza.action.ActionOld;
import com.carterza.component.AnimationComponent;
import com.carterza.math.Vec2I;
import com.carterza.world.World;
import com.carterza.world.WorldOld;

/**
 * Created by zachcarter on 11/21/16.
 */
public class HeroOld extends CharacterOld {

    ActionOld nextAction;

    public HeroOld(long id, WorldOld world, int speed, AnimationComponent animationComponent, Vec2I position) {
        super(id, world, speed, animationComponent, position);
    }

    @Override
    public boolean needsInput() {
        return this.nextAction == null;
    }

    public void setNextAction(ActionOld action) {
        this.nextAction = action;
    }

    @Override
    public int act() {
        int actionCost = this.nextAction.getCost();
        this.nextAction = null;
        return actionCost;
    }
}
