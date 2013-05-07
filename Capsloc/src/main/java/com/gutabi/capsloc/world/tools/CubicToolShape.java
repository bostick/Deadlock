package com.gutabi.capsloc.world.tools;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.capsloc.geom.Capsule;
import com.gutabi.capsloc.geom.CapsuleSequence;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.CubicCurve;
import com.gutabi.capsloc.geom.GeometryPath;
import com.gutabi.capsloc.geom.Line;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.graph.Vertex;

public class CubicToolShape {

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
	GeometryPath path = APP.platform.createGeometryPath();
	
	public CubicToolShape(World world, Point start, Point c0, Point c1, Point end) {
		
		this.world = world;
		this.start = start;
		startCircle = new Circle(start, Vertex.INIT_VERTEX_RADIUS);
		this.end = end;
		this.c0 = c0;
		this.c1 = c1;
		
		c = new CubicCurve(start, c0, c1, end);
		path.add(c);
		
		tan0 = new Line(c0, start);
		tan1 = new Line(c1, end);
		
		skeleton = c.skeleton();
		
		List<Circle> cs = new ArrayList<Circle>();
		for (Point p : skeleton) {
			cs.add(new Circle(p, Vertex.INIT_VERTEX_RADIUS));
		}
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < cs.size()-1; i++) {
			Circle a = cs.get(i);
			Circle b = cs.get(i+1);
			caps.add(new Capsule(a, b));
		}
		
		skeletonSeq = new CapsuleSequence(caps);
		
//		aabb = skeletonSeq.aabb;
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		skeletonSeq.draw(ctxt);
		
		ctxt.setColor(Color.ORANGE);
		path.draw(ctxt);
		tan0.draw(ctxt);
		tan1.draw(ctxt);
		
	}
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}
	
}
