package com.carterza;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.carterza.action.ActionResult;
import com.carterza.input.InputHandler;
import com.carterza.screen.GameScreen;
import com.carterza.screen.GameScreenOld;
import com.carterza.world.World;
import com.carterza.world.WorldOld;

public class Derelict extends Game {

	GameScreen gameScreen;
	GameScreenOld gameScreenOld;

	@Override
	public void create() {
		gameScreen = new GameScreen(this);
		gameScreenOld = new GameScreenOld(this);
		setScreen(gameScreen);
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
	}

	public World getWorld() {
		return gameScreen.getWorld();
	}

	public void updateGUI(ActionResult actionResult) {
		gameScreen.update(actionResult);
	}

	/*public WorldOld getWorld() {
		return gameScreenOld.getWorld();
	}*/

}
