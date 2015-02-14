package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;

public class GDXBypassGame extends ApplicationAdapter {

    OrthographicCamera camera;
    ScreenViewport viewport;

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

        carFactory = new CarFactory(textureAtlas);

        camera = new OrthographicCamera();

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1.0f / unitLength);

        stage = new Stage(viewport);
//        Gdx.input.setInputProcessor(stage);

        World world = new World(grassSprite, grassCols, grassRows);

        Board board = new Board(tileSprite);

        Car car = carFactory.newCar();
        car.setTouchable(Touchable.enabled);

//        car.addAction(Actions.sequence(Actions.moveTo(300, 0, 1), Actions.rotateBy(360, 0.5f)));

        Group bg = new Group();
        Group fg = new Group();

        stage.addActor(bg);
        stage.addActor(fg);

        bg.addActor(world);
        bg.addActor(board);

        fg.addActor(car);

	}

	@Override
	public void render () {

        camera.position.set(0, 0, 0);

        stage.act(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
	}

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        stage.dispose();
    }
}
