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
import com.gutabi.deadlock.core.geom.QuadCurve;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

@SuppressWarnings("static-access")
public class QuadCursorShape extends Shape {
	
	public final Circle start;
//	public final Circle pCircle;
//	public final Line line;
//	public final Capsule cap;
	
	public final QuadCurve q;
	
	public final List<Point> skeleton;
//	public final Polyline skeletonShape;
	public final CapsuleSequence skeletonSeq;
	
	private final AABB aabb;
	
	public QuadCursorShape(Point start, Point c, Point end) {
		
		this.start = new Circle(null, start, Vertex.INIT_VERTEX_RADIUS);
//		this.pCircle = new Circle(null, end, Vertex.INIT_VERTEX_RADIUS);
		
//		this.cap = new Capsule(null, this.first, pCircle, -1);
		
		q = new QuadCurve(start, c, end);
		
		skeleton = ShapeUtils.skeleton(q);
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
		
		skeletonSeq.draw(ctxt);
		
		ctxt.setColor(Color.ORANGE);
		q.draw(ctxt);
		
		if (APP.DEBUG_DRAW) {
			
			ctxt.setColor(Color.BLACK);
			aabb.draw(ctxt);
			
		}
	}
}
