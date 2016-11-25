package com.carterza.action;

import com.badlogic.gdx.math.Vector2;
import com.carterza.actor.Actor;
import com.carterza.actor.Character;
import com.carterza.actor.Hero;
import com.carterza.generator.graph.ComparablePoint2i;
import com.carterza.generator.graph.Node;
import com.carterza.generator.spacecraft.Door;
import com.carterza.generator.spacecraft.Room;
import com.carterza.generator.spacecraft.Spacecraft;
import com.carterza.generator.spacecraft.TileType;
import com.carterza.math.Direction;
import com.carterza.math.Vec2I;
import com.stewsters.util.math.Point2i;

/**
 * Created by zachcarter on 11/22/16.
 */
public class MoveAction extends Action {

    Direction moveDirection;
    Character character;

    public MoveAction(Actor actor, Direction moveDirection) {
        super(actor, 100);
        if(actor instanceof Character)
            this.character = (Character)actor;
        this.moveDirection = moveDirection;
    }

    @Override
    public ActionResult perform() {
        if(this.character != null) {
            Vector2 currentPosition = this.character.getPhysicsBody().getPosition();
            Vector2 positionToMoveTo = new Vector2(currentPosition.x + moveDirection.x, currentPosition.y + moveDirection.y);
            if(this.character instanceof Hero) {
                Spacecraft spacecraft = this.character.getWorld().getWorldMap().getSpacecraft();
                Room roomMovingInto = spacecraft.findRoomContainingPoint(new Point2i((int)positionToMoveTo.x, (int)positionToMoveTo.y));
                if(roomMovingInto != null) {
                    Node n = spacecraft.roomGraph.getNode(new ComparablePoint2i(roomMovingInto.center()));
                    if(n.getPrecondition().toString() != "") {
                        return new ActionResult(false);
                    }
                }
                TileType tileType = spacecraft.gridMap.getTile((int)positionToMoveTo.x, (int)positionToMoveTo.y);
                if(tileType == TileType.DOOR) {
                    Door door = spacecraft.findDoor(new Point2i((int)positionToMoveTo.x, (int)positionToMoveTo.y));
                    if(door.getEdge().getSymbol() != null) {
                        System.out.println("here");
                    }
                }
            }
            Vec2I newPosition = new Vec2I((int)positionToMoveTo.x, (int)positionToMoveTo.y);
            this.character.setPosition(newPosition);
            return new MoveActionResult(this, true, newPosition);
        }
        return null;
    }
}
