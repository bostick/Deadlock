package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by bbostic on 2/13/2015.
 */
public class Car extends Actor {

    Sprite sprite;

//    boolean started;

    public Car(Sprite sprite) {

        this.sprite = sprite;

        setWidth(3);
        setHeight(1);
        setOrigin(getWidth()/2, getHeight()/2);

//        setBounds(getX(),getY(),3*unitLength, unitLength);
//            addListener(new InputListener(){
//                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                    ((Car)event.getTarget()).started = true;
//                    return true;
//                }
//            });
    }

//        @Override
//        public void act(float delta){
//            if(started){
//                x+=5;
//            }
//        }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void drawDebug(ShapeRenderer shapeRenderer) {
        super.drawDebug(shapeRenderer);
        shapeRenderer.translate(getX() + getOriginX(), getY() + getOriginY(), 0);
        shapeRenderer.rotate(0, 0, 1, getRotation());
        shapeRenderer.translate(-(getX() + getOriginX()), -(getY() + getOriginY()), 0);
        shapeRenderer.circle(getX()+2.5f, getY()+0.5f, 0.2f);
    }

}
