package com.gutabi.capsloc.world.tools;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.CompoundShape;
import com.gutabi.capsloc.geom.MutableAABB;
import com.gutabi.capsloc.geom.ShapeUtils;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.graph.Merger;
import com.gutabi.capsloc.world.graph.Vertex;

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
		aabbTmp.union(worldTop.aabb);
		aabbTmp.union(worldLeft.aabb);
		aabbTmp.union(worldRight.aabb);
		aabbTmp.union(worldBottom.aabb);
		aabb = new AABB(aabbTmp.x, aabbTmp.y, aabbTmp.width, aabbTmp.height);
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public boolean hitTest(Point p) {
		assert false;
		return false;
	}
	
	public Object plus(Point p) {
		assert false;
		return null;
	}
	
	public boolean intersect(Object s) {
		
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
