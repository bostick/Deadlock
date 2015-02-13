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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;

import java.util.Map;

public class GDXBypassGame extends ApplicationAdapter {

	SpriteBatch batch;
	TextureAtlas textureAtlas;
	Sprite grassSprite;
    Sprite tileSprite;

    CarFactory carFactory;

    private Stage stage;
    int grassRows;
    int grassCols;

    float unitLength;

	@Override
	public void create () {
        batch = new SpriteBatch();

        textureAtlas = new TextureAtlas(Gdx.files.internal("data/bypass.pack"));

        grassSprite = new Sprite(textureAtlas.findRegion("grass-back"));
        tileSprite = new Sprite(textureAtlas.findRegion("tile-1"));

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        grassRows = 8;
        grassCols = 8;
        if (width > height) {
            // landscape
            unitLength = (float)height / 8.0f;
            grassCols = (int)Math.ceil(width / unitLength);
        } else {
            unitLength = (float)width / 8.0f;
            grassRows = (int) Math.ceil(height / unitLength);
        }

        carFactory = new CarFactory(textureAtlas, unitLength);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Car car = carFactory.newCar();
        car.setTouchable(Touchable.enabled);

        car.addAction(Actions.sequence(Actions.moveTo(300, 0, 1), Actions.rotateBy(360, 0.5f)));

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

        for (int i = 0; i < grassCols; i++) {
            for (int j = 0; j < grassRows; j++) {
                batch.draw(grassSprite, width/2 - (grassCols*unitLength)/2 + i*unitLength, height/2 - (grassRows*unitLength)/2 + j*unitLength, unitLength, unitLength);
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                batch.draw(tileSprite, width/2 - (6*unitLength)/2 + i*unitLength, height/2 - (6*unitLength)/2 + j*unitLength, unitLength, unitLength);
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
