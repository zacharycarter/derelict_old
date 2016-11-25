
package com.carterza.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.carterza.Derelict;
import com.carterza.debug.Debug;
import com.carterza.input.InputHandler;
import com.carterza.input.InputHandlerOld;
import com.carterza.world.World;
import com.carterza.world.WorldOld;
import com.stuckinadrawer.Room;
import net.bytten.gameutil.Vec2I;


/**
 * Created by zachcarter on 11/21/16.
 */

public class GameScreenOld implements Screen {

    enum CameraState { NORMAL, PANNING }

    private final Derelict game;
    private WorldOld world;
    private OrthographicCamera camera;
    private AssetManager assetManager;
    private Debug debug;
    Room lastRoom, currentRoom;
    private Object cameraState;

    public GameScreenOld(Derelict derelict) {
        this.game = derelict;
        initialize();
    }

    private void initialize() {
        this.assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("test/TestMap.tmx", TiledMap.class);
        assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
        assetManager.load("packed/derelict.atlas", TextureAtlas.class);
        assetManager.finishLoading();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 384/24, 288/24);
        camera.update();
        this.world = new WorldOld(
                this.game
                , camera
                , (TiledMap) assetManager.get("test/TestMap.tmx")
                , (TextureAtlas) assetManager.get("packed/derelict.atlas")
        );
        Gdx.input.setInputProcessor(new InputHandlerOld(this.game, camera));
        this.debug = new Debug(camera);
        currentRoom = world.getWorldMap().findRoomContainingPoint(new Vec2I(world.getHero().getPosition().x, world.getHero().getPosition().y));
        lastRoom = currentRoom;
        camera.position.set(new Vector3(currentRoom.x+(currentRoom.width+1)/2+.5f, currentRoom.y+(currentRoom.height)/2+.5f,0));
    }

    @Override
    public void render(float delta) {
        currentRoom = world.getWorldMap().findRoomContainingPoint(new Vec2I(world.getHero().getPosition().x, world.getHero().getPosition().y));
        if(currentRoom != null && currentRoom != lastRoom) {
            cameraState = CameraState.PANNING;
            world.getHero().getAnimationComponent().setShowAnimation(false);
            lastRoom = currentRoom;
        }
        if(cameraState == CameraState.PANNING) {
            if(!camera.position.equals(new Vector3(currentRoom.x+(currentRoom.width+1)/2+.5f, currentRoom.y+(currentRoom.height)/2+.5f,0))) {
                if(camera.position.x < currentRoom.x+(currentRoom.width+1)/2+.5f) {
                    camera.position.x++;
                } else if(camera.position.x > currentRoom.x+(currentRoom.width+1)/2+.5f) {
                    camera.position.x--;
                } if(camera.position.y <  currentRoom.y+(currentRoom.height)/2+.5f) {
                    camera.position.y++;
                } else if(camera.position.y >  currentRoom.y+(currentRoom.height)/2+.5f) {
                    camera.position.y--;
                }
            } else {
                cameraState = CameraState.NORMAL;
                world.getHero().getAnimationComponent().setShowAnimation(true);
            }
        }
        camera.update();
        world.update();
        world.render();
        //debug.render(camera, world);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
    }

    public WorldOld getWorld() {
        return this.world;
    }
}

