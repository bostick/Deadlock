package com.gutabi.deadlock.world.physics;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.math.Point;

public class PhysicsUtils {
	
	public static Vec2 vec2(Point p) {
		return new Vec2((float)p.x, (float)p.y);
	}
	
	public static Point point(Vec2 p) {
		return new Point(p.x, p.y);
	}

}
