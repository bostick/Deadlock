package com.brentonbostick.capsloc.world.tools;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.brentonbostick.capsloc.geom.Capsule;
import com.brentonbostick.capsloc.geom.CapsuleSequence;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.geom.GeometryPath;
import com.brentonbostick.capsloc.geom.Line;
import com.brentonbostick.capsloc.geom.QuadCurve;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.graph.Vertex;

public class QuadToolShape {
	
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
	GeometryPath path = APP.platform.createGeometryPath();
	
	public QuadToolShape(Point start, Point c, Point end) {
		
		this.start = start;
		startCircle = new Circle(start, Vertex.INIT_VERTEX_RADIUS);
		this.end = end;
		this.c = c;
		
		q = new QuadCurve(start, c, end);
		path.add(q);
		
		tan0 = new Line(c, start);
		tan1 = new Line(c, end);
		
		skeleton = q.skeleton();
		
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
		
		if (!start.equals(end)) {
			skeletonSeq.draw(ctxt);
			
			ctxt.setColor(Color.ORANGE);
			path.draw(ctxt);
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
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}
	
}
