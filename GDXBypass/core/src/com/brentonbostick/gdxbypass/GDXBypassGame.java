package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;

import java.util.Map;

public class GDXBypassGame extends ApplicationAdapter {

	SpriteBatch batch;
	TextureAtlas textureAtlas;
	Sprite grassSprite;
    Sprite tileSprite;
    Sprite carSprite;

    float newSpriteSize;

    public class Car extends Actor {

        boolean started;

        Car() {
            setBounds(getX(),getY(),3*newSpriteSize, newSpriteSize);
//            addListener(new InputListener(){
//                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                    ((Car)event.getTarget()).started = true;
//                    return true;
//                }
//            });
        }

//        @Override
//        public void act(float delta){
//            if(started){
//                x+=5;
//            }
//        }

        @Override
        public void draw(Batch batch, float alpha) {
            batch.draw(carSprite, getX(), getY(), 3*newSpriteSize, newSpriteSize);
        }
    }

    private Stage stage;

	@Override
	public void create () {
        batch = new SpriteBatch();

        textureAtlas = new TextureAtlas(Gdx.files.internal("data/bypass.pack"));

        grassSprite = new Sprite(textureAtlas.findRegion("grass-back"));
        tileSprite = new Sprite(textureAtlas.findRegion("tile-1"));
        carSprite = new Sprite(textureAtlas.findRegion("car-3-1"));

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (width > height) {
            // landscape
            newSpriteSize = (float)height / 8.0f;

        } else {
            newSpriteSize = (float)width / 8.0f;
        }

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Car car = new Car();
        car.setTouchable(Touchable.enabled);

        MoveToAction moveAction = new MoveToAction();
        moveAction.setPosition(300f, 0f);
        moveAction.setDuration(10f);
        car.addAction(moveAction);

        stage.addActor(car);

	}

	@Override
	public void render () {

        stage.act(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        int grassRows = 8;
        int grassCols = 8;
        if (width > height) {
            // landscape
            grassCols = (int)Math.ceil(width / newSpriteSize);
        } else {
            grassRows = (int) Math.ceil(height / newSpriteSize);
        }

        for (int i = 0; i < grassCols; i++) {
            for (int j = 0; j < grassRows; j++) {
                batch.draw(grassSprite, width/2 - (grassCols*newSpriteSize)/2 + i*newSpriteSize, height/2 - (grassRows*newSpriteSize)/2 + j*newSpriteSize, newSpriteSize, newSpriteSize);
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                batch.draw(tileSprite, width/2 - (6*newSpriteSize)/2 + i*newSpriteSize, height/2 - (6*newSpriteSize)/2 + j*newSpriteSize, newSpriteSize, newSpriteSize);
            }
        }

		batch.end();

        stage.draw();
	}

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        stage.dispose();
    }
}
