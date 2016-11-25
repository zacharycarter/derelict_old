package com.carterza.action;

import com.carterza.actor.Actor;
import com.carterza.actor.ActorOld;
import com.carterza.actor.Character;
import com.carterza.actor.CharacterOld;
import com.carterza.math.Direction;

/**
 * Created by zachcarter on 11/22/16.
 */
public class MoveActionOld extends ActionOld {

    Direction moveDirection;
    CharacterOld character;

    public MoveActionOld(ActorOld actor, Direction moveDirection) {
        super(actor, 100);
        if(actor instanceof CharacterOld)
            this.character = (CharacterOld)actor;
        this.moveDirection = moveDirection;
    }

    @Override
    public ActionResult perform() {
        if(this.character != null) {
            this.character.setPosition(this.character.getPosition().add(moveDirection));
            return new ActionResult(true);
        }
        return null;
    }
}
