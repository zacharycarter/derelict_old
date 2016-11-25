
package com.carterza.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.carterza.Derelict;
import com.carterza.action.*;
import com.carterza.actor.*;
import com.carterza.actor.Character;
import com.carterza.component.AnimationComponent;
import com.carterza.map.WorldMap;
import com.carterza.map.WorldMapOld;
import com.carterza.math.Vec2I;

import java.util.*;

public class WorldOld {
    private com.badlogic.gdx.physics.box2d.World physics;
    private final List<ActorOld> actors;
    private final Queue<ActorTurnOld> actorTurnQueue;
    private final WorldMapOld worldMap;
    private long currentActorId = 0;
    private HeroOld hero;
    private TextureAtlas atlas;
    long counter = 0;
    SpriteBatch batch;
    private float age = 0;
    private OrthographicCamera camera;
    Derelict game;

    public WorldOld(Derelict game, OrthographicCamera camera, TiledMap map, TextureAtlas atlas) {
        this.actors = new ArrayList<ActorOld>();
        this.camera = camera;
        this.actorTurnQueue = new PriorityQueue<ActorTurnOld>();
        this.worldMap = new WorldMapOld(camera);
        this.atlas = atlas;
        initialize();
    }

    private void initialize() {
        worldMap.generate(atlas);
        physics = new com.badlogic.gdx.physics.box2d.World(new Vector2(0,0),true);
        batch = new SpriteBatch();


        HeroOld hero = new HeroOld(
                currentActorId
                , this
                , 2
                , new AnimationComponent(new Animation(2.0f, this.atlas.findRegions("player_idle_unarmed"), Animation.PlayMode.LOOP))
                , new Vec2I(worldMap.findEntrance()[0],worldMap.findEntrance()[1])
        );
        this.hero = hero;
        actors.add(hero);
        actorTurnQueue.add(new ActorTurnOld(hero, hero.actionDelay(), counter));
        counter++;
        currentActorId++;

        ActorOld monsterOne = new MonsterOld(
                currentActorId
                , this
                , 2
                , new AnimationComponent(new Animation(2.0f, this.atlas.findRegions("ai"), Animation.PlayMode.LOOP))
                , new Vec2I(23,23)
        );
        actors.add(monsterOne);
        actorTurnQueue.add(new ActorTurnOld(monsterOne, monsterOne.actionDelay(), counter));
        counter++;
        currentActorId++;

        ActorOld monsterTwo = new MonsterOld(
                currentActorId
                , this
                , 1
                , new AnimationComponent(new Animation(2.0f, this.atlas.findRegions("ai"), Animation.PlayMode.LOOP))
                , new Vec2I(1,23)
        );
        actors.add(monsterTwo);
        actorTurnQueue.add(new ActorTurnOld(monsterTwo, monsterTwo.actionDelay(), counter));
        counter++;
        currentActorId++;
    }

    public void render() {

        worldMap.render();
        renderActors();
    }

    private void renderActors() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for(ActorOld a : actors) {
            if(a instanceof CharacterOld) {
                CharacterOld c = (CharacterOld)a;
                AnimationComponent actorAnimationComponent = c.getAnimationComponent();
                if (actorAnimationComponent != null) {
                    actorAnimationComponent.setAnimationAge(actorAnimationComponent.getAnimationAge() + Gdx.graphics.getDeltaTime());
                    if(actorAnimationComponent.isShowAnimation()) {
                        final TextureRegion frame = actorAnimationComponent.getAnimation().getKeyFrame(actorAnimationComponent.getAnimationAge());
                        batch.draw(frame, c.getPosition().x, c.getPosition().y, frame.getRegionWidth() / 24, frame.getRegionHeight() / 24);
                    }
                }
            }
        }
        batch.end();
    }

    public void dispose() {
        physics.dispose();
    }

    public WorldUpdate update() {
        this.age += Gdx.graphics.getDeltaTime();

        WorldUpdate worldUpdate = new WorldUpdate();

        ActorTurnOld actorTurn = actorTurnQueue.remove();

        ActorOld actor = actorTurn.getActor();

        if(actor instanceof HeroOld) {
            if (((HeroOld) actor).needsInput()) {
                actorTurnQueue.add(actorTurn);
                return worldUpdate;
            }
        }
        worldUpdate.worldProgressed = true;

        updateTurnQueue(-actorTurn.getTime());

        int actionCost = actor.act();

        actorTurnQueue.add(new ActorTurnOld(actor, actor.actionDelay(actionCost), counter));
        counter++;

        ActionOld actionToPerform = null;
        ActionResult actionResult = null;
        Iterator<ActionOld> actionIterator = ActionManagerOld.getActionQueue().iterator();
        while(actionIterator.hasNext()) {
            actionToPerform = actionIterator.next();
            actionResult = actionToPerform.perform();
            actionIterator.remove();
        }

        return worldUpdate;
    }

    private void updateTurnQueue(double time) {
        Iterator<ActorTurnOld> turnQueueIterator = actorTurnQueue.iterator();
        while(turnQueueIterator.hasNext()) {
            ActorTurnOld actorTurn = turnQueueIterator.next();
            actorTurn.setTime(actorTurn.getTime()+time);
        }
    }

    public HeroOld getHero() {
        return hero;
    }

    public WorldMapOld getWorldMap() {
        return this.worldMap;
    }


}

