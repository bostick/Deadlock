package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Vertex;

@SuppressWarnings("static-access")
public class MergerCursorShape extends Shape {
	
	public static java.awt.Stroke dashedOutlineStroke = new BasicStroke(7.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{15.0f}, 0.0f);
	
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
	public void draw(Graphics2D g2) {
		assert false;
	}
	
	public void paint(Graphics2D g2) {
		
//		if (p == null) {
//			return;
//		}
		
		java.awt.Stroke origStroke = g2.getStroke();
		AffineTransform origTransform = g2.getTransform();
		
		g2.setStroke(dashedOutlineStroke);
		g2.setColor(Color.GRAY);
		
//		g2.translate(p.x * MODEL.PIXELS_PER_METER, p.y * MODEL.PIXELS_PER_METER);
		
		worldQ.draw(g2);
		
		worldTop.draw(g2);
		worldLeft.draw(g2);
		worldRight.draw(g2);
		worldBottom.draw(g2);
		
		g2.setStroke(origStroke);
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			aabb.paint(g2);
			
		}
		
	}

}
