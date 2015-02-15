package com.brentonbostick.gdxbypass;

/**
 * Created by brenton on 2/15/2015.
 */
public enum Side {

    TOP { public Side other() { return BOTTOM; } public float getAngle() { return _270_DEGREES; } },
    LEFT { public Side other() { return RIGHT; } public float getAngle() { return _180_DEGREES; } },
    RIGHT { public Side other() { return LEFT; } public float getAngle() { return _0_DEGREES; } },
    BOTTOM { public Side other() { return TOP; } public float getAngle() { return _90_DEGREES; } };

    static final float _0_DEGREES = 0.0f * (float)Math.PI;
    static final float _90_DEGREES = 0.5f * (float)Math.PI;
    static final float _180_DEGREES = 1.0f * (float)Math.PI;
    static final float _270_DEGREES = 1.5f * (float)Math.PI;

    public abstract Side other();
    public abstract float getAngle();

    public boolean isRightOrBottom() {
        return this == Side.RIGHT || this == Side.BOTTOM;
    }

    public static Side angleToSide(float angle) {
        if (angle == _0_DEGREES) {
            return RIGHT;
        } else if (angle == _90_DEGREES) {
            return BOTTOM;
        } else if (angle == _180_DEGREES) {
            return LEFT;
        } else if (angle == _270_DEGREES) {
            return TOP;
        }
        return null;
    }

    public static Side fromFileString(String s) {
        if (s.equals("TOP")) {
            return Side.TOP;
        } else if (s.equals("LEFT")) {
            return Side.LEFT;
        } else if (s.equals("RIGHT")) {
            return Side.RIGHT;
        } else if (s.equals("BOTTOM")) {
            return Side.BOTTOM;
        }
        return null;
    }

}
