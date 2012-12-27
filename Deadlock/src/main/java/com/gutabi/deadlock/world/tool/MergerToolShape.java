package com.gutabi.deadlock.world.tool;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.CompoundShape;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class MergerToolShape implements CompoundShape {
	
	private Point ul;
	private Point p0;
	private Point p1;
	private Point p2;
	private Point p3;
	{
		ul = new Point(-Merger.MERGER_WIDTH/2, -Merger.MERGER_HEIGHT/2);
		p0 = ul;
		p1 = new Point(ul.x + Merger.MERGER_WIDTH, ul.y);
		p2 = new Point(ul.x + Merger.MERGER_WIDTH, ul.y + Merger.MERGER_HEIGHT);
		p3 = new Point(ul.x, ul.y + Merger.MERGER_HEIGHT);
	}
	
	public final Circle worldTop;
	public final Circle worldLeft;
	public final Circle worldBottom;
	public final Circle worldRight;
	public final Quad worldQ;
	
	private AABB aabb;
	
	public MergerToolShape(Point p) {
		
		worldQ = new Quad(null, p0.plus(p), p1.plus(p), p2.plus(p), p3.plus(p));
		
		worldTop = new Circle(null, new Point(ul.plus(p).x + Merger.MERGER_WIDTH/2, ul.plus(p).y), Vertex.INIT_VERTEX_RADIUS);
		worldLeft = new Circle(null, new Point(ul.plus(p).x, ul.plus(p).y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		worldRight = new Circle(null, new Point(ul.plus(p).x + Merger.MERGER_WIDTH, ul.plus(p).y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		worldBottom = new Circle(null, new Point(ul.plus(p).x + Merger.MERGER_WIDTH/2, ul.plus(p).y+Merger.MERGER_HEIGHT), Vertex.INIT_VERTEX_RADIUS);
		
		aabb = worldQ.aabb;
		aabb = AABB.union(aabb, worldTop.aabb);
		aabb = AABB.union(aabb, worldLeft.aabb);
		aabb = AABB.union(aabb, worldRight.aabb);
		aabb = AABB.union(aabb, worldBottom.aabb);
		
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public boolean hitTest(Point p) {
		assert false;
		return false;
	}
	
	public Shape plus(Point p) {
		assert false;
		return null;
	}
	
	public boolean intersect(Shape s) {
		
		if (ShapeUtils.intersect(worldQ, s)) {
			return true;
		}
		if (ShapeUtils.intersect(worldTop, s)) {
			return true;
		}
		if (ShapeUtils.intersect(worldLeft, s)) {
			return true;
		}
		if (ShapeUtils.intersect(worldRight, s)) {
			return true;
		}
		if (ShapeUtils.intersect(worldBottom, s)) {
			return true;
		}
		return false;
		
	}
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
		
		worldQ.draw(ctxt);
		
		worldTop.draw(ctxt);
		worldLeft.draw(ctxt);
		worldRight.draw(ctxt);
		worldBottom.draw(ctxt);
		
	}
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}

}
