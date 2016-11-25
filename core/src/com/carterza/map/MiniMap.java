package com.carterza.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.carterza.action.ActionResult;
import com.carterza.action.MoveActionResult;
import com.carterza.actor.Character;
import com.carterza.world.World;

/**
 * Created by zachcarter on 11/24/16.
 */
public class MiniMap {
    private SpriteBatch miniMapSpriteBatch;
    private ShapeRenderer miniMapShapeRenderer;
    World world;
    Texture texture;
    Camera guiCamera;
    boolean drawThisUpdate = false;
    private float blinkRate = 5f;
    private int blinkFrames = 20;
    private int blinkFrameCounter = 0;
    private float blinkTimer = 0;
    private boolean isBlinking = true;
    private Pixmap originalTexturePixmap;
    private Pixmap texturePixmap;
    int[][] pixels;

    public MiniMap(World world, OrthographicCamera guiCamera, Texture texture) {
        this.world = world;
        this.guiCamera = guiCamera;
        this.texture = texture;
        texture.getTextureData().prepare();
        originalTexturePixmap = texture.getTextureData().consumePixmap();
        texture.getTextureData().prepare();
        texturePixmap = texture.getTextureData().consumePixmap();
        /*this.pixels = new int[this.texture.getWidth()][this.texture.getHeight()];
        texturePixmap.setColor(0,0,0,.5f);
        texturePixmap.fill();*/


        miniMapSpriteBatch = new SpriteBatch();
        miniMapShapeRenderer = new ShapeRenderer();
    }

    public void render() {
        miniMapSpriteBatch.setProjectionMatrix(guiCamera.combined);
        miniMapSpriteBatch.begin();
        miniMapSpriteBatch.draw(
                new Texture(texturePixmap, Pixmap.Format.RGBA8888, false)
                , -12
                , -9
                , 10
                , 10
                , 0
                , 0
                , texture.getWidth()
                , texture.getHeight()
                , false
                ,true
        );
        miniMapSpriteBatch.end();

        if(isBlinking) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            blinkFrameCounter++;
            if (blinkTimer < blinkRate) {
                if (blinkFrameCounter % blinkFrames == 0) {
                    float distanceFromXOrigin = Math.abs(0 - world.getHero().getPosition().x);
                    float distanceFromYOrigin = Math.abs(0 - world.getHero().getPosition().y);
                    miniMapShapeRenderer.setProjectionMatrix(guiCamera.combined);
                    miniMapShapeRenderer.setColor(Color.YELLOW);
                    miniMapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    miniMapShapeRenderer.rect(
                            -12 + (distanceFromXOrigin / 25.6f)/**.3f*/
                            , -9 + (distanceFromYOrigin / 25.6f)/**.3f*/
                            , .1f
                            , .1f
                    );
                    miniMapShapeRenderer.end();
                }
            } else {
                blinkTimer = 0;
                // isBlinking = false;
            }
        }
        miniMapShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        miniMapShapeRenderer.rect(-86,-64, 64, 64);
        miniMapShapeRenderer.end();

    }

    public SpriteBatch getMiniMapSpriteBatch() {
        return this.miniMapSpriteBatch;
    }

    public void update(ActionResult actionResult) {
        if(actionResult instanceof MoveActionResult) {
            /*MoveActionResult moveActionResult = (MoveActionResult)actionResult;
            texturePixmap.drawPixel
                    (moveActionResult.getPositionMovedTo().x
                            , moveActionResult.getPositionMovedTo().y
                            , new Color(originalTexturePixmap.getPixel(moveActionResult.getPositionMovedTo().x, moveActionResult.getPositionMovedTo().y)).toIntBits()
                    );*/
        }
    }
}
