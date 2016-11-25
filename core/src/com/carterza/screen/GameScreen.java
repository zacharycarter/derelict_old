package com.carterza.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.carterza.Derelict;
import com.carterza.action.ActionResult;
import com.carterza.camera.OrthoCamController;
import com.carterza.debug.Debug;
import com.carterza.input.InputHandler;
import com.carterza.map.MiniMap;
import com.carterza.world.World;

/**
 * Created by zachcarter on 11/21/16.
 */
public class GameScreen implements Screen {

    private final Derelict game;
    private World world;
    private MiniMap miniMap;
    private OrthographicCamera camera;
    private OrthographicCamera guiCamera;
    private AssetManager assetManager;
    private Debug debug;

    public GameScreen(Derelict derelict) {
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
        guiCamera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/24, Gdx.graphics.getHeight()/24);
        guiCamera.setToOrtho(false, Gdx.graphics.getWidth()/24, Gdx.graphics.getHeight()/24);
        camera.update();
        guiCamera.update();
        this.world = new World(
                this.game
                , camera
                , (TiledMap) assetManager.get("test/TestMap.tmx")
                , (TextureAtlas) assetManager.get("packed/derelict.atlas")
        );

        assetManager.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
        assetManager.load("ship.png", Texture.class);
        assetManager.load("minimap.png", Texture.class);
        assetManager.finishLoading();

        world.setShipTexture((Texture) assetManager.get("ship.png"));

        this.miniMap = new MiniMap(world, guiCamera, (Texture) assetManager.get("minimap.png"));
        Gdx.input.setInputProcessor(new InputHandler(this.game, camera));
        this.debug = new Debug(camera);
    }

    @Override
    public void render(float delta) {
        camera.position.set(world.getHero().getPosition().x, world.getHero().getPosition().y, 0);
        camera.update();
        guiCamera.position.set(0 ,0, 0);
        guiCamera.update();

        // miniMap.getMiniMapSpriteBatch().setProjectionMatrix(camera.combined);
        world.update();
        world.render();
        miniMap.render();
        debug.render(camera, world);
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

    public World getWorld() {
        return this.world;
    }

    public void update(ActionResult actionResult) {
        miniMap.update(actionResult);
    }
}
