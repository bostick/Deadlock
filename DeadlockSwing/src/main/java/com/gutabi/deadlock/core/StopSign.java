package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.gutabi.deadlock.utils.Java2DUtils;

@SuppressWarnings("static-access")
public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	Edge e;
	int dir;
	Point p;
	double r = 0.5 * STOPSIGN_SIZE;
	
	public StopSign(Edge e, int dir) {
		this.e = e;
		this.dir = dir;
		hiliteColor = Color.RED;
	}
	
	
	
	public boolean hitTest(Point p, double radius) {
		return DMath.lessThanEquals(Point.distance(p, this.p), r + radius);
	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep() {
		return true;
	}
	
	public void computePoint() {
		if (dir == 0) {
			p = e.getStartBorderPoint();
		} else {
			p = e.getEndBorderPoint();
		}
		
		computePath();
	}
	
	public void computePath() {
		Shape s = new Ellipse2D.Double(p.x - r, p.y - r, 2 * r, 2 * r);
		
		List<Point> poly = Java2DUtils.shapeToList(s, 0.01);
		
		path = Java2DUtils.listToPath(poly);
		
		computeRenderingRect();
	}	
	
	private void computeRenderingRect() {
		Rectangle2D bound = path.getBounds2D();
		aabbLoc = new Point(bound.getX(), bound.getY());
		aabbDim = new Dim(bound.getWidth(), bound.getHeight());
	}
	
	public void paint(Graphics2D g2) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform trans = (AffineTransform)origTransform.clone();
		
		trans.translate(p.x, p.y);
		
		trans.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setTransform(trans);
		
		BufferedImage im = MODEL.world.stopSign;
		
		g2.drawImage(im,
				(int)(-STOPSIGN_SIZE * MODEL.PIXELS_PER_METER / 2),
				(int)(-STOPSIGN_SIZE * MODEL.PIXELS_PER_METER / 2),
				(int)(STOPSIGN_SIZE * MODEL.PIXELS_PER_METER),
				(int)(STOPSIGN_SIZE * MODEL.PIXELS_PER_METER), null);
		
		g2.setTransform(origTransform);
	}
	
	public void paintHilite(Graphics2D g2) {
		g2.setColor(hiliteColor);
		g2.fill(path);
	}
	
}
