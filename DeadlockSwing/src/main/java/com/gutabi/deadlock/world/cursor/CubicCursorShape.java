package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.CubicCurve;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

@SuppressWarnings("static-access")
public class CubicCursorShape extends Shape {
	
	public final Point start;
	public final Circle startCircle; 
	public final Point end;
	public final Point c0;
	public final Point c1;
//	public final Circle pCircle;
//	public final Line line;
//	public final Capsule cap;
	
	public final CubicCurve c;
	public final Line tan0;
	public final Line tan1;
	
	public final List<Point> skeleton;
//	public final Polyline skeletonShape;
	public final CapsuleSequence skeletonSeq;
	
	private final AABB aabb;
	
	public CubicCursorShape(Point start, Point c0, Point c1, Point end) {
		
		this.start = start;
		startCircle = new Circle(null, start, Vertex.INIT_VERTEX_RADIUS);
		this.end = end;
		this.c0 = c0;
		this.c1 = c1;
		
//		this.pCircle = new Circle(null, end, Vertex.INIT_VERTEX_RADIUS);
		
//		this.cap = new Capsule(null, this.first, pCircle, -1);
		
		c = new CubicCurve(start, c0, c1, end);
		tan0 = new Line(c0, start);
		tan1 = new Line(c1, end);
		
		skeleton = ShapeUtils.skeleton(c);
//		skeletonShape = new Polyline(skeleton);
		
		List<Circle> cs = new ArrayList<Circle>();
		for (Point p : skeleton) {
			cs.add(new Circle(null, p, Vertex.INIT_VERTEX_RADIUS));
		}
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < cs.size()-1; i++) {
			Circle a = cs.get(i);
			Circle b = cs.get(i+1);
			caps.add(new Capsule(null, a, b, -1));
		}
		
		skeletonSeq = new CapsuleSequence(null, caps);
		
		aabb = skeletonSeq.aabb;
		
	}
	
//	public boolean hitTest(Point p) {
//		assert false;
//		return false;
//	}
//	
//	public Shape plus(Point p) {
//		assert false;
//		return null;
//	}
//	
//	public AABB getAABB() {
//		assert false;
//		return null;
//	}
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
//		ctxt.setColor(Color.GRAY);
//		ctxt.setWorldPixelStroke(1);
		
//		if (!start.equals(end)) {
//			skeletonSeq.draw(ctxt);
//			
//			ctxt.setColor(Color.ORANGE);
//			c.draw(ctxt);
//			tan0.draw(ctxt);
//			tan1.draw(ctxt);
//		} else {
//			/*
//			 * the overlapping causes XOR mode to draw nothing, so handle it here
//			 */
//			startCircle.draw(ctxt);
//			
//			ctxt.setColor(Color.ORANGE);
//			tan0.draw(ctxt);
//		}
		
		skeletonSeq.draw(ctxt);
		
		ctxt.setColor(Color.ORANGE);
		c.draw(ctxt);
		tan0.draw(ctxt);
		tan1.draw(ctxt);
		
		if (APP.DEBUG_DRAW) {
			
			ctxt.setColor(Color.BLACK);
			aabb.draw(ctxt);
			
		}
	}
}
