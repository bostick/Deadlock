package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by bbostic on 2/13/2015.
 */
public class CarFactory {

    TextureAtlas textureAtlas;

    float unitLength;

    Sprite sprite;

    public CarFactory(TextureAtlas textureAtlas, float unitLength) {
        this.textureAtlas = textureAtlas;
        this.unitLength = unitLength;
        sprite = new Sprite(textureAtlas.findRegion("car-3-1"));
    }

    public Car newCar() {
        return new Car(sprite, unitLength);
    }

}
