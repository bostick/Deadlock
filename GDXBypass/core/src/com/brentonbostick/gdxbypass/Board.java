package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brenton on 2/14/2015.
 */
public class Board extends Actor {

    Sprite sprite;

    public Board(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                batch.draw(sprite, -6/2 + i, -6/2 + j, 1, 1);
            }
        }
    }
}
