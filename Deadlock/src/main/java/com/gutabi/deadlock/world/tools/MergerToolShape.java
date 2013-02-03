package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.CompoundShape;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Vertex;

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
	public final AABB worldQ;
	
	private AABB aabb;
	
	public MergerToolShape(Point p) {
		
		worldQ = APP.platform.createShapeEngine().createAABB(null, );
		
		worldTop = APP.platform.createShapeEngine().createCircle(null, new Point(ul.plus(p).x + Merger.MERGER_WIDTH/2, ul.plus(p).y), Vertex.INIT_VERTEX_RADIUS);
		worldLeft = APP.platform.createShapeEngine().createCircle(null, new Point(ul.plus(p).x, ul.plus(p).y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		worldRight = APP.platform.createShapeEngine().createCircle(null, new Point(ul.plus(p).x + Merger.MERGER_WIDTH, ul.plus(p).y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		worldBottom = APP.platform.createShapeEngine().createCircle(null, new Point(ul.plus(p).x + Merger.MERGER_WIDTH/2, ul.plus(p).y+Merger.MERGER_HEIGHT), Vertex.INIT_VERTEX_RADIUS);
		
		aabb = worldQ;
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
