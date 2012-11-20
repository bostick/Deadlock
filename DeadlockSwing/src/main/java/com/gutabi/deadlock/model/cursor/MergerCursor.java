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
import com.gutabi.deadlock.model.Cursor;

@SuppressWarnings("static-access")
public class MergerCursor extends Cursor {
	
	public static java.awt.Stroke dashedOutlineStroke = new BasicStroke(7.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{15.0f}, 0.0f);
	
	private Circle top;
	private Circle left;
	private Circle bottom;
	private Circle right;
	private Quad q;
	
	private AABB localAABB;
	
	public MergerCursor() {
		
		Point ul = new Point(-Merger.MERGER_WIDTH/2, -Merger.MERGER_HEIGHT/2);
		Point p0 = ul;
		Point p1 = new Point(ul.x + Merger.MERGER_WIDTH, ul.y);
		Point p2 = new Point(ul.x + Merger.MERGER_WIDTH, ul.y + Merger.MERGER_HEIGHT);
		Point p3 = new Point(ul.x, ul.y + Merger.MERGER_HEIGHT);
		q = new Quad(this, p0, p1, p2, p3);
		
		top = new Circle(this, new Point(ul.x + Merger.MERGER_WIDTH/2, ul.y), Vertex.INIT_VERTEX_RADIUS);
		left = new Circle(this, new Point(ul.x, ul.y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		right = new Circle(this, new Point(ul.x + Merger.MERGER_WIDTH, ul.y + Merger.MERGER_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
		bottom = new Circle(this, new Point(ul.x + Merger.MERGER_WIDTH/2, ul.y+Merger.MERGER_HEIGHT), Vertex.INIT_VERTEX_RADIUS);
		
		localAABB = q.aabb;
		localAABB = AABB.union(localAABB, top.aabb);
		localAABB = AABB.union(localAABB, left.aabb);
		localAABB = AABB.union(localAABB, right.aabb);
		localAABB = AABB.union(localAABB, bottom.aabb);
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		aabb = localAABB.plus(p);
	}
	
	public boolean intersect(Shape s) {
		
		Quad worldQ = (Quad)q.plus(p);
		Circle worldTop = (Circle)top.plus(p);
		Circle worldLeft = (Circle)left.plus(p);
		Circle worldRight = (Circle)right.plus(p);
		Circle worldBottom = (Circle)bottom.plus(p);
		
		if (s.intersect(worldQ)) {
			return true;
		}
		if (s.intersect(worldTop)) {
			return true;
		}
		if (s.intersect(worldLeft)) {
			return true;
		}
		if (s.intersect(worldRight)) {
			return true;
		}
		if (s.intersect(worldBottom)) {
			return true;
		}
		return false;
		
	}
	
	public void paint(Graphics2D g2) {
		
		if (p == null) {
			return;
		}
		
		java.awt.Stroke origStroke = g2.getStroke();
		AffineTransform origTransform = g2.getTransform();
		
		g2.setStroke(dashedOutlineStroke);
		g2.setColor(Color.GRAY);
		
		g2.translate(p.x * MODEL.PIXELS_PER_METER, p.y * MODEL.PIXELS_PER_METER);
		
		q.draw(g2);
		
		top.draw(g2);
		left.draw(g2);
		right.draw(g2);
		bottom.draw(g2);
		
		g2.setStroke(origStroke);
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			aabb.paint(g2);
			
		}
		
	}

}
