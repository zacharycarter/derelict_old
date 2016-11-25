
package com.carterza.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.carterza.Derelict;
import com.carterza.action.ActionManagerOld;
import com.carterza.action.MoveActionOld;
import com.carterza.actor.HeroOld;
import com.carterza.math.Direction;
import com.carterza.world.WorldOld;
import com.stuckinadrawer.TileType;


/**
 * Created by zachcarter on 11/21/16.
 */

public class InputHandlerOld implements InputProcessor {

    Derelict game;
    WorldOld gameWorld;
    Camera camera;

    public InputHandlerOld(Derelict derelict, OrthographicCamera camera) {
        this.game = derelict;
        // this.gameWorld = game.getWorld();
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.Q) {
            this.gameWorld.getWorldMap().zoomOut();
        } else if(keycode == Input.Keys.E) {
            this.gameWorld.getWorldMap().zoomIn();
        } else if(keycode == Input.Keys.UP) {
            HeroOld hero = gameWorld.getHero();
            if(gameWorld.getWorldMap().getLevel()[hero.getPosition().x][hero.getPosition().y+1].tileType == TileType.WALL ) {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.NN);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            } else {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.N);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            }
        } else if(keycode == Input.Keys.DOWN) {
            HeroOld hero = gameWorld.getHero();
            if(gameWorld.getWorldMap().getLevel()[hero.getPosition().x][hero.getPosition().y-1].tileType == TileType.WALL ) {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.SS);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            } else {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.S);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            }
        } else if(keycode == Input.Keys.LEFT) {
            HeroOld hero = gameWorld.getHero();
            if(gameWorld.getWorldMap().getLevel()[hero.getPosition().x-1][hero.getPosition().y].tileType == TileType.WALL ) {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.WW);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            } else {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.W);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            }
        } else if(keycode == Input.Keys.RIGHT) {
            HeroOld hero = gameWorld.getHero();
            if(gameWorld.getWorldMap().getLevel()[hero.getPosition().x+1][hero.getPosition().y].tileType == TileType.WALL ) {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.EE);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            } else {
                MoveActionOld nextAction = new MoveActionOld(hero, Direction.E);
                hero.setNextAction(nextAction);
                ActionManagerOld.getActionQueue().add(nextAction);
            }
        } else if(keycode == Input.Keys.W) {
            camera.translate(0,1,0);
            camera.update();
        } else if(keycode == Input.Keys.S) {
            camera.translate(0,-1,0);
            camera.update();
        } else if(keycode == Input.Keys.A) {
            camera.translate(-1,0,0);
            camera.update();
        } else if(keycode == Input.Keys.D) {
            camera.translate(1,0,0);
            camera.update();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

