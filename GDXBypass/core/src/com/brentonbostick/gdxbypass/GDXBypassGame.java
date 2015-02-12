package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GDXBypassGame extends ApplicationAdapter {

	SpriteBatch batch;
	TextureAtlas textureAtlas;
	Sprite grassSprite;
    Sprite tileSprite;

	@Override
	public void create () {
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(Gdx.files.internal("data/bypass.pack"));
        grassSprite = new Sprite(textureAtlas.findRegion("grass-back"));
        tileSprite = new Sprite(textureAtlas.findRegion("tile-1"));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grassSprite.setPosition(i*32, j*32);
                grassSprite.draw(batch);
            }
        }

        for (int i = 1; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                tileSprite.setPosition(i*32, j*32);
                tileSprite.draw(batch);
            }
        }

		batch.end();
	}

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();

    }
}
