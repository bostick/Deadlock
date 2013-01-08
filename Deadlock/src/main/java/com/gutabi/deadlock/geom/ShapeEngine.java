package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public abstract class ShapeEngine {
	
	public abstract AABB createAABB(double x, double y, double width, double height);
	
	public abstract Circle createCircle(Object parent, Point center, double radius);
	
}
