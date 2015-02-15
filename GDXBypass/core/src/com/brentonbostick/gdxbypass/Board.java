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
        setX(-3);
        setY(-3);
        this.sprite = sprite;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
//                batch.draw(sprite, getX() + i, getY() + j, 1, 1);
                batch.draw(sprite, getX()+i, getY()+j, getOriginX(), getOriginY(), 1, 1, getScaleX(), getScaleY(), getRotation());
            }
        }
    }
}
