package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class Cursor {
	
	public final double r;
	private Point p;
	
	public Cursor(double r) {
		this.r = r;
	}
	
	public void setPoint(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public void paint(Graphics2D g2) {
		
		paintCursor(g2);
		
	}
	
	private void paintCursor(Graphics2D g2) {
		
		if (p != null) {
			g2.drawOval(
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((2 * r) * MODEL.PIXELS_PER_METER),
					(int)((2 * r) * MODEL.PIXELS_PER_METER));
		}
		
	}
	
}
