package com.carterza.actor;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.physics.box2d.Body;
import com.carterza.component.AnimationComponent;
import com.carterza.math.Vec2I;
import com.carterza.pathfind.FlatTiledGraph;
import com.carterza.pathfind.FlatTiledNode;
import com.carterza.pathfind.TiledManhattanDistance;
import com.carterza.world.World;

public class Monster extends Character {
    FlatTiledGraph graph = new FlatTiledGraph();
    IndexedAStarPathFinder<FlatTiledNode> astar;
    GraphPath<FlatTiledNode> dgp = new DefaultGraphPath<FlatTiledNode>(25*25);
    TiledManhattanDistance<FlatTiledNode> heu = new TiledManhattanDistance<FlatTiledNode>();

    public Monster(long id, World world, int speed, AnimationComponent animationComponent, Vec2I position, Body physicsBody) {
        super(id, world, speed, animationComponent, position, physicsBody);
        this.graph.init();
        this.astar = new IndexedAStarPathFinder<FlatTiledNode>(graph);
    }

    @Override
    public int act() {
        /*FlatTiledNode startNode = graph.getNode(this.position.x, this.position.y);
        FlatTiledNode destinationNode = graph.getNode(world.getHero().getPosition().x, world.getHero().getPosition().y);
        graph.startNode = startNode;
        astar.searchNodePath(startNode, destinationNode, heu, dgp);
        Vec2I nextTile = new Vec2I(dgp.get(1).x, dgp.get(1).y);
        Vec2I nextTileDirection = nextTile.subtract(getPosition());
        MoveAction moveAction = null;
        if(nextTileDirection.x == Direction.N.x && nextTileDirection.y == Direction.N.y)
            moveAction = new MoveAction(this, Direction.N);
        else if(nextTileDirection.x == Direction.S.x && nextTileDirection.y == Direction.S.y)
            moveAction = new MoveAction(this, Direction.S);
        else if(nextTileDirection.x == Direction.E.x && nextTileDirection.y == Direction.E.y)
            moveAction = new MoveAction(this, Direction.E);
        else if(nextTileDirection.x == Direction.W.x && nextTileDirection.y == Direction.W.y)
            moveAction = new MoveAction(this, Direction.W);
        ActionManager.getActionQueue().add(moveAction);
        return moveAction.getCost();*/
        return 100;
    }

}
