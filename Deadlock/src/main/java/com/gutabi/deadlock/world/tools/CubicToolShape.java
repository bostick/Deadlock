package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.geom.Capsule;
import com.gutabi.deadlock.geom.CapsuleSequence;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Vertex;

public class CubicToolShape implements Shape {

	public final World world;
	public final Point start;
	public final Circle startCircle; 
	public final Point end;
	public final Point c0;
	public final Point c1;
	
	public final CubicCurve c;
	public final Line tan0;
	public final Line tan1;
	
	public final List<Point> skeleton;
	public final CapsuleSequence skeletonSeq;
	
//	private final AABB aabb;
	
	public CubicToolShape(World world, Point start, Point c0, Point c1, Point end) {
		
		this.world = world;
		this.start = start;
		startCircle = APP.platform.createShapeEngine().createCircle(null, start, Vertex.INIT_VERTEX_RADIUS);
		this.end = end;
		this.c0 = c0;
		this.c1 = c1;
		
		c = APP.platform.createShapeEngine().createCubicCurve(start, c0, c1, end);
		tan0 = APP.platform.createShapeEngine().createLine(c0, start);
		tan1 = APP.platform.createShapeEngine().createLine(c1, end);
		
		skeleton = c.skeleton();
		
		List<Circle> cs = new ArrayList<Circle>();
		for (Point p : skeleton) {
			cs.add(APP.platform.createShapeEngine().createCircle(null, p, Vertex.INIT_VERTEX_RADIUS));
		}
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < cs.size()-1; i++) {
			Circle a = cs.get(i);
			Circle b = cs.get(i+1);
			caps.add(APP.platform.createShapeEngine().createCapsule(null, a, b));
		}
		
		skeletonSeq = new CapsuleSequence(null, caps);
		
//		aabb = skeletonSeq.aabb;
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		skeletonSeq.draw(ctxt);
		
		ctxt.setColor(Color.ORANGE);
		c.draw(ctxt);
		tan0.draw(ctxt);
		tan1.draw(ctxt);
		
	}
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}
	
}
