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

    static GDXBypassGame game;

    World world;

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
    Sprite carSprite;

    InputProcessor mainInputProcessor;

    Table table;

    boolean console;
    boolean debug;

	@Override
	public void create () {

        game = this;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        textureAtlas = new TextureAtlas(Gdx.files.internal("data/bypass.pack"));

        grassSprite = new Sprite(textureAtlas.findRegion("grass-back"));
        tileSprite = new Sprite(textureAtlas.findRegion("tile-1"));
        carSprite = new Sprite(textureAtlas.findRegion("car-3-1"));

        world = new World();

        worldCamera = new OrthographicCamera();

        worldViewport = new ScreenViewport(worldCamera);
        worldViewport.setUnitsPerPixel(1.0f / world.unitLength);

        worldStage = new Stage(worldViewport);

        table = new Table();

        worldStage.addActor(world);

        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        TextButton debugButton = new TextButton("Debug", skin);
        debugButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!debug) {
                    debug = true;
                } else {
                    debug = false;
                }
                worldStage.setDebugAll(debug);
            }
        });


        table.add(debugButton).row();
        table.add(new TextButton("Test 1", skin){
            {
                addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
//                        world.car.addAction(Actions.rotateBy(360, 2));
                    }
                });
            }
        }).row();
        table.add(new TextButton("Test 2", skin)).row();
        table.add(new TextButton("Test 3", skin)).row();

        table.setFillParent(true);

        uiCamera = new OrthographicCamera();

        uiViewport = new ScreenViewport(uiCamera);

        uiStage = new Stage(uiViewport);

        table.setTransform(true);
        table.addAction(Actions.alpha(0));
        uiStage.addActor(table);

        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent ev, int keyCode) {
                switch (keyCode) {
                    case Input.Keys.ESCAPE:
                        if (console) {
                            console = false;
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
                            table.addAction(Actions.fadeIn(0.2f));
                            Gdx.input.setInputProcessor(uiStage);
                        }
                        break;
                }
                return true;
            }

            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                float width = Gdx.graphics.getWidth();
                float height = Gdx.graphics.getHeight();
                Gdx.app.log("MyTag", (x-width/2)/world.unitLength + " " + (-y + height/2)/world.unitLength);
                return true;
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {

                return true;
            }

            @Override
            public boolean touchDragged (int x, int y, int pointer) {
                return true;
            }
        };

        Gdx.graphics.setContinuousRendering(false);

        Gdx.input.setInputProcessor(mainInputProcessor);
	}

	@Override
	public void render () {

        worldStage.act(Gdx.graphics.getDeltaTime());
        uiStage.act();

        worldCamera.position.set(0, 0, 0);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldStage.draw();

        uiStage.draw();
	}

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        uiStage.dispose();
        worldStage.dispose();
    }
}
