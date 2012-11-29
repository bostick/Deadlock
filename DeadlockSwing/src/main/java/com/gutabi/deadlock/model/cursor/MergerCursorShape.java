package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class MergerCursorShape extends Shape {
	
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
	
	public MergerCursorShape(Point p) {
		
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
	
	@Override
	public boolean hitTest(Point p) {
		assert false;
		return false;
	}
	
	@Override
	public Shape plus(Point p) {
		assert false;
		return null;
	}
	
	@Override
	public void draw(RenderingContext ctxt) {
		assert false;
	}
	
	public void paint(RenderingContext ctxt) {
		
		java.awt.Stroke origStroke = ctxt.getStroke();
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.setStroke(new BasicStroke((float)(3.2 / VIEW.PIXELS_PER_METER_DEBUG), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		ctxt.setColor(Color.GRAY);
		
		worldQ.draw(ctxt);
		
		worldTop.draw(ctxt);
		worldLeft.draw(ctxt);
		worldRight.draw(ctxt);
		worldBottom.draw(ctxt);
		
		ctxt.setStroke(origStroke);
		ctxt.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			aabb.paint(ctxt);
			
		}
		
	}

}
