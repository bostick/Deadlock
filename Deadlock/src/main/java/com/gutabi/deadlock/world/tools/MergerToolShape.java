package com.gutabi.deadlock.world.tools;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.CompoundShape;
import com.gutabi.deadlock.geom.MutableAABB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Vertex;

public class MergerToolShape implements CompoundShape {
	
	private Point ul = new Point(-Merger.MERGER_WIDTH/2, -Merger.MERGER_HEIGHT/2);
	
	public final Circle worldTop;
	public final Circle worldLeft;
	public final Circle worldBottom;
	public final Circle worldRight;
	public final AABB worldQ;
	
	private AABB aabb;
	
	public MergerToolShape(Point p) {
		
		worldQ = new AABB(ul.x + p.x, ul.y + p.y, Merger.MERGER_WIDTH, Merger.MERGER_HEIGHT);
		
		worldTop = new Circle(new Point(ul.plus(p).x + Merger.MERGER_WIDTH/2, ul.plus(p).y), Vertex.INIT_VERTEX_RADIUS);
		worldLeft = new Circle(new Point(ul.plus(p).x, ul.plus(p).y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		worldRight = new Circle(new Point(ul.plus(p).x + Merger.MERGER_WIDTH, ul.plus(p).y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		worldBottom = new Circle(new Point(ul.plus(p).x + Merger.MERGER_WIDTH/2, ul.plus(p).y+Merger.MERGER_HEIGHT), Vertex.INIT_VERTEX_RADIUS);
		
		MutableAABB aabbTmp = new MutableAABB();
		aabbTmp.union(worldQ);
		aabbTmp.union(worldTop.getAABB());
		aabbTmp.union(worldLeft.getAABB());
		aabbTmp.union(worldRight.getAABB());
		aabbTmp.union(worldBottom.getAABB());
		aabb = new AABB(aabbTmp.x, aabbTmp.y, aabbTmp.width, aabbTmp.height);
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
