package com.gutabi.deadlock.world.physics;

import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class PhysicsDebugDraw extends DebugDraw {
	
	RenderingContext ctxt;
	
	public PhysicsDebugDraw() {
		super(new OBBViewportTransform());
		
		m_drawFlags = DebugDraw.e_dynamicTreeBit;
	}
	
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
		assert false;
	}

	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		assert false;
	}

	public void drawCircle(Vec2 center, float radius, Color3f color) {
		assert false;
	}

	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
		assert false;
	}
	
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		ctxt.setColor(Color.WHITE);
		Line line = new Line(Point.point(p1), Point.point(p2));
		line.draw(ctxt);
	}
	
	public void drawTransform(org.jbox2d.common.Transform xf) {
		assert false;
	}

	public void drawString(float x, float y, String s, Color3f color) {
		
	}
	
}
