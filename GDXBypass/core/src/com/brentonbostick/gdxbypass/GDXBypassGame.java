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
import com.badlogic.gdx.math.Vector3;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;

public class GDXBypassGame extends ApplicationAdapter {

    int GAME_WIDTH = 640;
    int GAME_HEIGHT = 480;

    OrthographicCamera worldCamera;
    ScreenViewport worldViewport;
    Stage worldStage;

    OrthographicCamera uiCamera;
    ScreenViewport uiViewport;
    Stage uiStage;

	SpriteBatch batch;
	TextureAtlas textureAtlas;
	Sprite grassSprite;
    Sprite tileSprite;

    CarFactory carFactory;

    int grassRows;
    int grassCols;
    float unitLength;

    Table table;

	@Override
	public void create () {
        batch = new SpriteBatch();

        textureAtlas = new TextureAtlas(Gdx.files.internal("data/bypass.pack"));

        grassSprite = new Sprite(textureAtlas.findRegion("grass-back"));
        tileSprite = new Sprite(textureAtlas.findRegion("tile-1"));

        grassRows = 8;
        grassCols = 8;
        if (GAME_WIDTH > GAME_HEIGHT) {
            // landscape
            unitLength = (float)GAME_HEIGHT / 8.0f;
            grassCols = (int)Math.ceil(GAME_WIDTH / unitLength);
        } else {
            unitLength = (float)GAME_WIDTH / 8.0f;
            grassRows = (int) Math.ceil(GAME_HEIGHT / unitLength);
        }

        carFactory = new CarFactory(textureAtlas);

        worldCamera = new OrthographicCamera();

        worldViewport = new ScreenViewport(worldCamera);
        worldViewport.setUnitsPerPixel(1.0f / unitLength);
//        viewport = new FillViewport(GAME_WIDTH / unitLength, GAME_HEIGHT / unitLength, camera);

        worldStage = new Stage(worldViewport);
//        Gdx.input.setInputProcessor(stage);

        table = new Table();

        World world = new World(grassSprite, grassCols, grassRows);

        Board board = new Board(tileSprite);

        Car car = carFactory.newCar();
        int firstULRow = 1;
        int firstULCol = 1;
        float carX = board.getX() + firstULCol;
        float carY = board.getY() + 6 - firstULRow - car.getHeight();
        car.setX(carX);
        car.setY(carY);
//        car.setTouchable(Touchable.enabled);

        car.addAction(Actions.sequence(Actions.rotateBy(-90, 1)));

        Group bg = new Group();
        Group fg = new Group();

        worldStage.addActor(bg);
        worldStage.addActor(fg);

        bg.addActor(world);
        bg.addActor(board);

        fg.addActor(car);

        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        TextButton buttonPlay = new TextButton("Play", skin);
        TextButton buttonExit = new TextButton("Exit", skin);
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Same way we moved here from the Splash Screen
                //We set it to new Splash because we got no other screens
                //otherwise you put the screen there where you want to go
//                ((Game)Gdx.app.getApplicationListener()).setScreen(new Splash());
                Gdx.app.log("MyTag", "my informative message");
            }
        });

        table.add(buttonPlay).row();
        table.add(buttonExit).row();

        table.setFillParent(true);

        uiCamera = new OrthographicCamera();

        uiViewport = new ScreenViewport(uiCamera);
//        uiViewport.setUnitsPerPixel(1.0f / unitLength);
//        viewport = new FillViewport(GAME_WIDTH / unitLength, GAME_HEIGHT / unitLength, camera);

        uiStage = new Stage(uiViewport);

        uiStage.addActor(table);

        Gdx.input.setInputProcessor(uiStage);
	}

	@Override
	public void render () {

//        viewport.setScreenWidth(GAME_WIDTH);
//        viewport.setScreenHeight(GAME_HEIGHT);
//        viewport.setScreenPosition(0, 0);
//        viewport.set
//        camera.position.set(0.7f, 0, 0);
//        camera.update();

//        Vector3 pos = camera.position;

        worldCamera.position.set(0, 0, 0);

        worldStage.act(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldStage.draw();

        uiStage.draw();
	}

//    @Override
//    public void resize(int width, int height){
//        viewport.update(width,height);
//        camera.position.set(0,0,0);
//    }

//    @Override
//    public void resize(int width, int height) {
//        use true here to center the camera
//        that's what you probably want in case of a UI
//        stage.getViewport().update(width, height, false);
//    }

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        worldStage.dispose();
    }
}
