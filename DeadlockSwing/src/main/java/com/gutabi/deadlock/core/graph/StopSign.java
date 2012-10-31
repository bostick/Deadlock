package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.utils.Java2DUtils;

@SuppressWarnings("static-access")
public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	public final Edge e;
	public final Vertex v;
	
	int dir;
	Point p;
	double r = 0.5 * STOPSIGN_SIZE;
	
	private Path2D path;
	
	public StopSign(Edge e, int dir) {
		this.e = e;
		this.dir = dir;
		
		if (dir == 0) {
			v = e.start;
		} else {
			v = e.end;
		}
		
		hiliteColor = Color.RED;
		
		computePath();
	}
	
	public Point getPoint() {
		return p;
	}
	
	public boolean hitTest(Point p, double radius) {
		return DMath.lessThanEquals(Point.distance(p, this.p), r + radius);
	}
	
	public void computePoint() {
		
		if (dir == 0) {
			p = e.getStartBorderPoint();
		} else {
			p = e.getEndBorderPoint();
		}
		
		computeAABB();
	}
	
	private void computePath() {
		Shape s = new Ellipse2D.Double(-r, -r, 2 * r, 2 * r);
		
		List<Point> poly = Java2DUtils.shapeToList(s, 0.01);
		
		path = Java2DUtils.listToPath(poly);
		
	}	
	
	private void computeAABB() {
		Rectangle2D bound = path.getBounds2D();
		aabb = new Rect(p.x + bound.getX(), p.y + bound.getY(), bound.getWidth(), bound.getHeight());
	}
	
	public void paint(Graphics2D g2) {
		
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.translate(p.x, p.y);
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.drawImage(MODEL.world.sheet,
				(int)(-STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
				(int)(-STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
				(int)(STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
				(int)(STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
				32, 224, 32+32, 224+32,
				null);
		
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
			
		}
	}
	
	public void paintHilite(Graphics2D g2) {
		
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.translate(p.x, p.y);
		
		g2.setColor(hiliteColor);
		g2.fill(path);
	}
	
}
