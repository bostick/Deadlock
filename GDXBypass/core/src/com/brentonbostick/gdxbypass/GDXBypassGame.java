package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

//    int GAME_WIDTH = 640;
//    int GAME_HEIGHT = 480;

    OrthographicCamera worldCamera;
    ScreenViewport worldViewport;
    Stage worldStage;

    OrthographicCamera uiCamera;
    ScreenViewport uiViewport;
    Stage uiStage;

	SpriteBatch batch;
    ShapeRenderer shapeRenderer;
	TextureAtlas textureAtlas;
	Sprite grassSprite;
    Sprite tileSprite;

    CarFactory carFactory;
    Car car;

    int grassRows;
    int grassCols;
    float unitLength;

    InputProcessor mainInputProcessor;

    Table table;

    boolean console;
    boolean debug;

	@Override
	public void create () {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
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

        worldCamera = new OrthographicCamera();

        worldViewport = new ScreenViewport(worldCamera);
        worldViewport.setUnitsPerPixel(1.0f / unitLength);
//        viewport = new FillViewport(GAME_WIDTH / unitLength, GAME_HEIGHT / unitLength, camera);

        worldStage = new Stage(worldViewport);
//        Gdx.input.setInputProcessor(stage);

        table = new Table();

        World world = new World(grassSprite, grassCols, grassRows);

        Board board = new Board(tileSprite);

        car = carFactory.newCar();
        int firstULRow = 1;
        int firstULCol = 1;
        float carX = board.getX() + firstULCol;
        float carY = board.getY() + 6 - firstULRow - car.getHeight();
        car.setX(carX);
        car.setY(carY);
//        car.setTouchable(Touchable.enabled);

//

        Group bg = new Group();
        Group fg = new Group();

        worldStage.addActor(bg);
        worldStage.addActor(fg);

        bg.addActor(world);
        bg.addActor(board);

        fg.addActor(car);

        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        TextButton debugButton = new TextButton("Debug", skin);
//        TextButton buttonExit = new TextButton("Exit", skin);
        debugButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Same way we moved here from the Splash Screen
                //We set it to new Splash because we got no other screens
                //otherwise you put the screen there where you want to go
//                ((Game)Gdx.app.getApplicationListener()).setScreen(new Splash());
//                Gdx.app.log("MyTag", "my informative message");
                if (!debug) {
                    debug = true;
                } else {
                    debug = false;
                }
                car.setDebug(debug);
            }
        });


        table.add(debugButton).row();
        table.add(new TextButton("Test 1", skin){
            {
                addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        car.addAction(Actions.rotateBy(360, 2));
                    }
                });
            }
        }).row();
        table.add(new TextButton("Test 2", skin)).row();
        table.add(new TextButton("Test 3", skin)).row();

        table.setFillParent(true);

        uiCamera = new OrthographicCamera();

        uiViewport = new ScreenViewport(uiCamera);
//        uiViewport.setUnitsPerPixel(1.0f / unitLength);
//        viewport = new FillViewport(GAME_WIDTH / unitLength, GAME_HEIGHT / unitLength, camera);

        uiStage = new Stage(uiViewport);

//        table.addAction(Actions.sequence(Actions.rotateBy(-90, 1)));
        table.setTransform(true);
        table.addAction(Actions.alpha(0));
//        table.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(2)));
//        table.addAction(Actions.sequence(Actions.moveBy(0, 150), Actions.moveBy(0, -150, 0.75f)));
        uiStage.addActor(table);

        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent ev, int keyCode) {
                switch (keyCode) {
                    case Input.Keys.ESCAPE:
                        if (console) {
                            console = false;
//                            table.addAction(Actions.sequence(Actions.moveBy(0, 150, 0.75f)));
                            table.addAction(Actions.fadeOut(0.2f));
                            Gdx.input.setInputProcessor(mainInputProcessor);
                        }
                        break;
                }
                return true;
            }
        });

        mainInputProcessor = new InputAdapter() {
            public boolean keyUp(int keyCode) {
                switch (keyCode) {
                    case Input.Keys.ESCAPE:
                        if (!console) {
                            console = true;
//                            table.addAction(Actions.sequence(Actions.moveBy(0, 150), Actions.moveBy(0, -150, 0.75f)));
                            table.addAction(Actions.fadeIn(0.2f));
                            Gdx.input.setInputProcessor(uiStage);
                        }
                        break;
                }
                return true;
            }
        };

//        Gdx.input.setInputProcessor(uiStage);
        Gdx.input.setInputProcessor(mainInputProcessor);
	}

	@Override
	public void render () {

        worldStage.act(Gdx.graphics.getDeltaTime());
        uiStage.act();

//        viewport.setScreenWidth(GAME_WIDTH);
//        viewport.setScreenHeight(GAME_HEIGHT);
//        viewport.setScreenPosition(0, 0);
//        viewport.set
//        camera.position.set(0.7f, 0, 0);
//        camera.update();

//        Vector3 pos = camera.position;

        worldCamera.position.set(0, 0, 0);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldStage.draw();


        uiStage.draw();

//        if (debug) {
//            shapeRenderer.setProjectionMatrix(worldViewport.getCamera().combined);
//            shapeRenderer.setColor(Color.RED);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            car.drawDebug(shapeRenderer);
//            shapeRenderer.end();
//        }
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
