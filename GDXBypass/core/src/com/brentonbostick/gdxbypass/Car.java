package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by bbostic on 2/13/2015.
 */
public class Car {

    BoardPosition pos;
    Side side;
    int length;

    public Car() {
//        width = 3;
//        height = 1;
//        originX = width/2;
//        originY = height/2;
        length = 3;
        side = Side.BOTTOM;
    }

    public void setPosition(BoardPosition pos) {
        this.pos = pos;
    }

    public void draw(Batch batch) {
        switch (side) {
            case RIGHT: {
                Board board = pos.board;
                float px = board.blX + pos.col;
                float py = board.blY + 6 - pos.row - 1;
                batch.draw(GDXBypassGame.game.carSprite, px, py, 0.5f, 0.5f, 3, 1, 1, 1, 0);
                break;
            }
            case BOTTOM: {
                Board board = pos.board;
                float px = board.blX + pos.col;
                float py = board.blY + 6 - pos.row - 1;
                batch.draw(GDXBypassGame.game.carSprite, px, py, 0.5f, 0.5f, 3, 1, 1, 1, 270);
                break;
            }
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
//        super.drawDebug(shapeRenderer);
//        shapeRenderer.translate(getX() + getOriginX(), getY() + getOriginY(), 0);
//        shapeRenderer.rotate(0, 0, 1, getRotation());
//        shapeRenderer.translate(-(getX() + getOriginX()), -(getY() + getOriginY()), 0);
//        shapeRenderer.circle(getX()+2.5f, getY()+0.5f, 0.2f);
    }

}
