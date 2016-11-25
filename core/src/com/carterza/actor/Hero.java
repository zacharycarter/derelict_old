package com.carterza.actor;

import com.badlogic.gdx.physics.box2d.Body;
import com.carterza.action.Action;
import com.carterza.component.AnimationComponent;
import com.carterza.math.Vec2I;
import com.carterza.world.World;

/**
 * Created by zachcarter on 11/21/16.
 */
public class Hero extends Character {

    Action nextAction;

    public Hero(long id, World world, int speed, AnimationComponent animationComponent, Vec2I position, Body physicsBody) {
        super(id, world, speed, animationComponent, position, physicsBody);
    }

    @Override
    public boolean needsInput() {
        return this.nextAction == null;
    }

    public void setNextAction(Action action) {
        this.nextAction = action;
    }

    @Override
    public int act() {
        int actionCost = this.nextAction.getCost();
        this.nextAction = null;
        return actionCost;
    }
}
