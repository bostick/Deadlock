package com.gutabi.deadlock.world.tool;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class FixtureToolShape extends Shape {
	
	public final Point p;
	public final Axis axis;
	public Point worldSource;
	public Point worldSink;
	
	public final Circle worldSourceCircle;
	public final Circle worldSinkCircle;
	public final Capsule cap;
	
	public final AABB aabb;
	
	public FixtureToolShape(Point p, Point source, Point sink, Axis axis) {
		
		this.p = p;
		this.axis = axis;
		
		switch (axis) {
		case LEFTRIGHT:
			worldSource = new Point(source.x - QuadrantMap.QUADRANT_WIDTH/2, p.y);
			worldSink = new Point(sink.x + QuadrantMap.QUADRANT_WIDTH/2, p.y);
			break;
		case TOPBOTTOM:
			worldSource = new Point(p.x, source.y - QuadrantMap.QUADRANT_HEIGHT/2);
			worldSink = new Point(p.x, sink.y + QuadrantMap.QUADRANT_HEIGHT/2);
			break;
		}
		
		worldSourceCircle = new Circle(null, worldSource, Vertex.INIT_VERTEX_RADIUS);
		worldSinkCircle = new Circle(null, worldSink, Vertex.INIT_VERTEX_RADIUS);
		cap = new Capsule(null, worldSourceCircle, worldSinkCircle, -1);
		
		aabb = cap.aabb;
	}

	public AABB getAABB() {
		return aabb;
	}
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
		
		cap.draw(ctxt);
		
		if (APP.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
	} 
	
}
