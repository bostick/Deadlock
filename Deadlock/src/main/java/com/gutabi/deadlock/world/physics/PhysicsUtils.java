package com.gutabi.deadlock.world.physics;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.math.Vec2;
import com.gutabi.deadlock.world.QueryCallback;

public class PhysicsUtils {
	
	public static Vec2 vec2(Point p) {
		return new Vec2((float)p.x, (float)p.y);
	}
	
	public static Point point(Vec2 p) {
		return new Point(p.x, p.y);
	}

}
