package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brenton on 2/14/2015.
 */
public class Board {

    float blX;
    float blY;
    float width;
    float height;

    public Board() {
        blX = -3;
        blY = -3;
        width = 6;
        height = 6;
    }

    public void draw(Batch batch) {

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                batch.draw(GDXBypassGame.game.tileSprite, blX + i, blY + j, 1, 1);
            }
        }
    }
}
