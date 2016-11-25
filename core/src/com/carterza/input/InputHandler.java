package com.carterza.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.carterza.Derelict;
import com.carterza.action.ActionManager;
import com.carterza.action.DummyAction;
import com.carterza.action.MoveAction;
import com.carterza.actor.Hero;
import com.carterza.math.Direction;
import com.carterza.world.World;

/**
 * Created by zachcarter on 11/21/16.
 */
public class InputHandler implements InputProcessor {

    Derelict game;
    World gameWorld;
    Camera camera;

    public InputHandler(Derelict derelict, OrthographicCamera camera) {
        this.game = derelict;
        this.gameWorld = game.getWorld();
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.Q) {
            this.gameWorld.getWorldMap().zoomOut();
        } else if(keycode == Input.Keys.E) {
            this.gameWorld.getWorldMap().zoomIn();
        } else if(keycode == Input.Keys.UP) {
            Hero hero = gameWorld.getHero();
            MoveAction nextAction = new MoveAction(hero, Direction.N);
            hero.setNextAction(nextAction);
            ActionManager.getActionQueue().add(nextAction);
        } else if(keycode == Input.Keys.DOWN) {
            Hero hero = gameWorld.getHero();
            MoveAction nextAction = new MoveAction(hero, Direction.S);
            hero.setNextAction(nextAction);
            ActionManager.getActionQueue().add(nextAction);
        } else if(keycode == Input.Keys.LEFT) {
            Hero hero = gameWorld.getHero();
            MoveAction nextAction = new MoveAction(hero, Direction.W);
            hero.setNextAction(nextAction);
            ActionManager.getActionQueue().add(nextAction);
        } else if(keycode == Input.Keys.RIGHT) {
            Hero hero = gameWorld.getHero();
            MoveAction nextAction = new MoveAction(hero, Direction.E);
            hero.setNextAction(nextAction);
            ActionManager.getActionQueue().add(nextAction);
        } else if(keycode == Input.Keys.W) {

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
