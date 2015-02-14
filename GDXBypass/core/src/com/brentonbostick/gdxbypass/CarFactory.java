package com.brentonbostick.gdxbypass;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by bbostic on 2/13/2015.
 */
public class CarFactory {

    TextureAtlas textureAtlas;

    Sprite sprite;

    public CarFactory(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        sprite = new Sprite(textureAtlas.findRegion("car-3-1"));
    }

    public Car newCar() {
        return new Car(sprite);
    }

}
