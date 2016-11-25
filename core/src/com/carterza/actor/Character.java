package com.carterza.actor;

import com.badlogic.gdx.physics.box2d.Body;
import com.carterza.component.AnimationComponent;
import com.carterza.math.Vec2I;
import com.carterza.world.World;

/**
 * Created by zachcarter on 11/21/16.
 */
public abstract class Character extends Actor {

    protected Vec2I position;
    private AnimationComponent animationComponent;
    private Body physicsBody;

    public Character(long id, World world, int speed, AnimationComponent animationComponent, Vec2I position, Body physicsBody) {
        super(id, world, speed);
        this.animationComponent = animationComponent;
        this.position = position;
        this.physicsBody = physicsBody;
    }

    public Vec2I getPosition() {
        return this.position;
    }

    public AnimationComponent getAnimationComponent() {
        return animationComponent;
    }

    public void setPosition(Vec2I position) {
        this.physicsBody.setTransform(position.x+.5f, position.y+.5f, physicsBody.getAngle());
        this.position = new Vec2I((int)physicsBody.getPosition().x, (int)physicsBody.getPosition().y);
    }

    public Body getPhysicsBody() {
        return physicsBody;
    }
}
