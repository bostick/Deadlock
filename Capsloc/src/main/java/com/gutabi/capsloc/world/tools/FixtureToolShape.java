package com.gutabi.capsloc.world.tools;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.Capsule;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.CompoundShape;
import com.gutabi.capsloc.geom.Shape;
import com.gutabi.capsloc.geom.ShapeUtils;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.QuadrantMap;
import com.gutabi.capsloc.world.graph.Axis;
import com.gutabi.capsloc.world.graph.Vertex;

public class FixtureToolShape implements CompoundShape {
	
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
		
		worldSourceCircle = new Circle(worldSource, Vertex.INIT_VERTEX_RADIUS);
		worldSinkCircle = new Circle(worldSink, Vertex.INIT_VERTEX_RADIUS);
		cap = new Capsule(worldSourceCircle, worldSinkCircle);
		
		aabb = cap.aabb;
	}

	public AABB getAABB() {
		return aabb;
	}
	
	public boolean intersect(Shape s) {
		
		if (ShapeUtils.intersect(worldSourceCircle, s)) {
			return true;
		}
		if (ShapeUtils.intersect(worldSinkCircle, s)) {
			return true;
		}
		return false;
		
	}
	
	public void draw(RenderingContext ctxt) {
		cap.draw(ctxt);
	}
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}
	
}
