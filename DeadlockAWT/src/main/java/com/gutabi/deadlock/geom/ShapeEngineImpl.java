package com.gutabi.deadlock.geom;

public class ShapeEngineImpl extends ShapeEngine {
	
	public AABB createAABB(double x, double y, double width, double height) {
		return new AABBImpl(x, y, width, height);
	}
	
}
