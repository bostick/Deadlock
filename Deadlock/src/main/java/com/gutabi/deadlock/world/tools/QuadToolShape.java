package com.gutabi.deadlock.world.tools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.QuadCurve;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class QuadToolShape implements Shape {
	
	public final Point start;
	public final Circle startCircle; 
	public final Point end;
	public final Point c;
	
	public final QuadCurve q;
	public final Line tan0;
	public final Line tan1;
	
	public final List<Point> skeleton;
	public final CapsuleSequence skeletonSeq;
	
//	private final AABB aabb;
	
	public QuadToolShape(WorldScreen screen, Point start, Point c, Point end) {
		
		this.start = start;
		startCircle = new Circle(null, start, Vertex.INIT_VERTEX_RADIUS);
		this.end = end;
		this.c = c;
		
		q = new QuadCurve(start, c, end);
		tan0 = new Line(c, start);
		tan1 = new Line(c, end);
		
		skeleton = ShapeUtils.skeleton(q);
		
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
		
//		aabb = skeletonSeq.aabb;
		
	}
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (!start.equals(end)) {
			skeletonSeq.draw(ctxt);
			
			ctxt.setColor(Color.ORANGE);
			q.draw(ctxt);
			tan0.draw(ctxt);
			tan1.draw(ctxt);
		} else {
			/*
			 * the overlapping causes XOR mode to draw nothing, so handle it here
			 */
			startCircle.draw(ctxt);
			
			ctxt.setColor(Color.ORANGE);
			tan0.draw(ctxt);
		}
		
	}
}