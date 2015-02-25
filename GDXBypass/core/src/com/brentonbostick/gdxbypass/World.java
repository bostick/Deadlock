package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brenton on 2/14/2015.
 */
public class World extends Actor {

    Board board;

    float unitLength;
    int grassCols;
    int grassRows;

    Car car;

    public World() {

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

        setX(-width/2);
        setY(-height/2);
        setWidth(width);
        setHeight(height);

        board = new Board();

        car = new Car();

        BoardPosition pos = new BoardPosition(board, 1, 1);
        car.setPosition(pos);
    }

    public boolean integrate(float dt) {
        return false;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        for (int i = 0; i < grassCols; i++) {
            for (int j = 0; j < grassRows; j++) {
                batch.draw(GDXBypassGame.game.grassSprite, -grassCols/2.0f + i, -grassRows/2.0f + j, 1, 1);
            }
        }

        board.draw(batch);

        car.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer renderer) {
        super.drawDebug(renderer);

    }
}
