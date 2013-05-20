package com.brentonbostick.capsloc.world.tools;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.Capsule;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.geom.CompoundShape;
import com.brentonbostick.capsloc.geom.ShapeUtils;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.QuadrantMap;
import com.brentonbostick.capsloc.world.graph.Axis;
import com.brentonbostick.capsloc.world.graph.Vertex;

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
	
	public boolean intersect(Object s) {
		
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
