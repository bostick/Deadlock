package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by bbostic on 2/13/2015.
 */
public class Car extends Actor {

    Sprite sprite;

    float unitLength;

    boolean started;

    public Car(Sprite sprite, float unitLength) {

        this.sprite = sprite;
        this.unitLength = unitLength;

        setWidth(3*unitLength);
        setHeight(unitLength);
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
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), 3*unitLength, unitLength, getScaleX(), getScaleY(), getRotation());
    }
}
