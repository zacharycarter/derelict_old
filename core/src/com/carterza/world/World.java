package com.carterza.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.carterza.Derelict;
import com.carterza.action.Action;
import com.carterza.action.ActionManager;
import com.carterza.action.ActionResult;
import com.carterza.actor.*;
import com.carterza.actor.Character;
import com.carterza.component.AnimationComponent;
import com.carterza.generator.graph.Node;
import com.carterza.generator.spacecraft.Room;
import com.carterza.map.Overworld;
import com.carterza.map.WorldMap;
import com.carterza.math.Vec2I;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    private com.badlogic.gdx.physics.box2d.World physics;
    private final List<Actor> actors;
    private final Queue<ActorTurn> actorTurnQueue;
    private final WorldMap worldMap;
    private long currentActorId = 0;
    private Hero hero;
    private TextureAtlas atlas;
    long counter = 0;
    SpriteBatch batch;
    private float age = 0;
    private OrthographicCamera camera;
    Derelict game;
    Texture shipTexture;
    private Overworld overworld;
    Random random;

    int[] numsToGenerate           = new int[]    { 0,   1,   2   };
    double[] discreteProbabilities = new double[] { .15, .75, .1 };

    public World(Derelict game, OrthographicCamera camera, TiledMap map, TextureAtlas atlas) {
        this.actors = new ArrayList<Actor>();
        this.random = new Random();
        this.camera = camera;
        this.actorTurnQueue = new PriorityQueue<ActorTurn>();
        this.worldMap = new WorldMap(camera);
        this.atlas = atlas;
        this.game = game;
        this.overworld = new Overworld();
        initialize();
    }

    private void initialize() {
        physics = new com.badlogic.gdx.physics.box2d.World(new Vector2(0,0),true);
        batch = new SpriteBatch();
        overworld.generate();
        worldMap.generate(atlas);
        createHero();
        createAI();
    }

    private void createAI() {
        Map<Room, Stack<Vec2I>> spawnPoints = new HashMap<Room, Stack<Vec2I>>();

        EnumeratedIntegerDistribution distribution =
                new EnumeratedIntegerDistribution(numsToGenerate, discreteProbabilities);

        int numMonstersToSpawn = 0;
        for(Node node : worldMap.getSpacecraft().roomGraph.getNodes().values()) {
            double intensity = node.getIntensity();
            Room room = worldMap.getSpacecraft().findRoom(node.getId());
            if(intensity > 0) {
                numMonstersToSpawn = (int) (random.nextInt(3) * intensity) + distribution.sample();
                if (intensity >= 0 && intensity < (1 / 3)) {
                    numMonstersToSpawn -= 1;
                } else if (intensity >= (1 / 3) && intensity < (2 / 3)) {
                    numMonstersToSpawn += 0;
                } else if (intensity >= (2 / 3) && intensity <= 1) {
                    numMonstersToSpawn += 2;
                }
                Math.max(numMonstersToSpawn, 0);
                if (intensity > (1 / 3))
                    numMonstersToSpawn = Math.max(numMonstersToSpawn, 1);
            } else {
                numMonstersToSpawn = 0;
            }

            spawnPoints.put(room, new Stack<Vec2I>());
            for(int i = 0; i < numMonstersToSpawn; i++) {
                Vec2I potentialSpawnPoint = new Vec2I(
                        ThreadLocalRandom.current().nextInt(room.x1+1, room.x2-1 + 1)
                        , ThreadLocalRandom.current().nextInt(room.y1+1, room.y2-1 + 1));
                while(spawnPoints.get(room).contains(potentialSpawnPoint)) {
                    potentialSpawnPoint = new Vec2I(
                            ThreadLocalRandom.current().nextInt(room.x1+1, room.x2-1 + 1)
                            , ThreadLocalRandom.current().nextInt(room.y1+1, room.y2-1 + 1));
                }
                spawnPoints.get(room).push(potentialSpawnPoint);
            }



            List<Monster> monsters = new ArrayList<Monster>();
            for(int i = 0; i < numMonstersToSpawn; i++) {
                Monster monster = new Monster(
                        currentActorId
                        , this
                        , 1
                        , new AnimationComponent(new Animation(2.0f, this.atlas.findRegions("ai"), Animation.PlayMode.LOOP))
                        , spawnPoints.get(room).pop()
                        , null
                );
                monsters.add(monster);
                actorTurnQueue.add(new ActorTurn(monster, monster.actionDelay(), counter));
            }
            monsters.addAll(monsters);
            actors.addAll(monsters);
            counter++;
            currentActorId++;
        }
    }

    private void createHero() {
        Room r = worldMap.getSpacecraft().findStart();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        // Set our body to the same position as our sprite
        // bodyDef.position.set((positionComponent.getPosition().x+12)/24, (positionComponent.getPosition().y+12)/24);
        bodyDef.position.set((r.center().x + .5f), (r.center().y + .5f));
        bodyDef.fixedRotation = true;
        // Create a body in the world using our definition
        Body body = physics.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f, .5f);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);

        shape.dispose();

        Hero hero = new Hero(
                currentActorId
                , this
                , 2
                , new AnimationComponent(new Animation(2.0f, this.atlas.findRegions("player_idle_unarmed"), Animation.PlayMode.LOOP))
                , new Vec2I(r.center().x,r.center().y)
                , body
        );

        this.hero = hero;
        actors.add(hero);
        actorTurnQueue.add(new ActorTurn(hero, hero.actionDelay(), counter));
        counter++;
        currentActorId++;
    }

    public void render() {
        worldMap.render();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderShip();
        renderActors();
        batch.end();
    }

    private void renderShip() {
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, .3f);
        batch.draw(
                shipTexture
                , 0
                , 0
                , shipTexture.getWidth()
                , shipTexture.getHeight()
                , 0
                , 0
                , shipTexture.getWidth()
                , shipTexture.getHeight()
                , false
                ,true
        );
        batch.setColor(c.r, c.g, c.b, 1);
    }

    private void renderActors() {
        for(Actor a : actors) {
            if(a instanceof com.carterza.actor.Character) {
                Character c = (Character)a;
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
    }

    public void dispose() {
        physics.dispose();
        batch.dispose();
    }

    public WorldUpdate update() {
        float delta = Gdx.graphics.getDeltaTime();
        this.age += delta;

        physics.step(delta, 6, 2);

        WorldUpdate worldUpdate = new WorldUpdate();

        ActorTurn actorTurn = actorTurnQueue.remove();

        Actor actor = actorTurn.getActor();

        if(actor instanceof Hero) {
            if (((Hero) actor).needsInput()) {
                actorTurnQueue.add(actorTurn);
                return worldUpdate;
            }
        }
        worldUpdate.worldProgressed = true;

        updateTurnQueue(-actorTurn.getTime());

        int actionCost = actor.act();

        actorTurnQueue.add(new ActorTurn(actor, actor.actionDelay(actionCost), counter));
        counter++;

        Action actionToPerform = null;
        ActionResult actionResult = null;
        Iterator<Action> actionIterator = ActionManager.getActionQueue().iterator();
        while(actionIterator.hasNext()) {
            actionToPerform = actionIterator.next();
            actionResult = actionToPerform.perform();
            game.updateGUI(actionResult);
            actionIterator.remove();
        }

        return worldUpdate;
    }

    private void updateTurnQueue(double time) {
        Iterator<ActorTurn> turnQueueIterator = actorTurnQueue.iterator();
        while(turnQueueIterator.hasNext()) {
            ActorTurn actorTurn = turnQueueIterator.next();
            actorTurn.setTime(actorTurn.getTime()+time);
        }
    }

    public Hero getHero() {
        return hero;
    }

    public WorldMap getWorldMap() {
        return this.worldMap;
    }


    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public void setShipTexture(Texture shipTexture) {
        this.shipTexture = shipTexture;
    }

    public Overworld getOverworld() {
        return this.overworld;
    }

    public com.badlogic.gdx.physics.box2d.World getPhysics() {
        return physics;
    }
}
