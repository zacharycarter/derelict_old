package com.carterza.actor;

import com.carterza.component.AnimationComponent;
import com.carterza.math.Vec2I;
import com.carterza.world.World;
import com.carterza.world.WorldOld;

/**
 * Created by zachcarter on 11/21/16.
 */
public abstract class CharacterOld extends ActorOld {

    protected Vec2I position;
    private AnimationComponent animationComponent;

    public CharacterOld(long id, WorldOld world, int speed, AnimationComponent animationComponent, Vec2I position) {
        super(id, world, speed);
        this.animationComponent = animationComponent;
        this.position = position;
    }

    public Vec2I getPosition() {
        return this.position;
    }

    public AnimationComponent getAnimationComponent() {
        return animationComponent;
    }

    public void setPosition(Vec2I position) {
        this.position = position;
    }
}
