package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brenton on 2/14/2015.
 */
public class World extends Actor {

    Sprite sprite;

    int grassCols;
    int grassRows;

    public World(Sprite sprite, int grassCols, int grassRows) {

        this.sprite = sprite;

        this.grassCols = grassCols;
        this.grassRows = grassRows;

//        setX(-1);
//        setY(-1);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        for (int i = 0; i < grassCols; i++) {
            for (int j = 0; j < grassRows; j++) {
                batch.draw(sprite, -grassCols/2.0f + i, -grassRows/2.0f + j, 1, 1);
//                batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
//                batch.draw
            }
        }
    }
}
