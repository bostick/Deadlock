package com.gutabi.capsloc.ui;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.MutableAABB;
import com.gutabi.capsloc.geom.MutablePolygon;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class Shimmer {
	
	public double x;
	public double y;
	public double width;
	public double height;
	public double brX;
	public double brY;
	
	public double startMillis;
	
	MutablePolygon poly = APP.platform.createMutablePolygon();
	
	public Shimmer(double startMillis) {
		this.startMillis = startMillis;
	}
	
	public void setShape(AABB aabb) {
		x = aabb.x;
		y = aabb.y;
		width = aabb.width;
		height = aabb.height;
		brX = x+width;
		brY = y+height;
	}
	
	public void setShape(MutableAABB aabb) {
		x = aabb.x;
		y = aabb.y;
		width = aabb.width;
		height = aabb.height;
		brX = x+width;
		brY = y+height;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (startMillis == -1) {
			startMillis = System.currentTimeMillis();
		}
		
		/*
		 * millis / pixel
		 */
		double traverseSpeed = 5;
		double timeToRest = 4000;
		double timeToTraverse = width * traverseSpeed;
		
		double t = System.currentTimeMillis();
		while (t > startMillis + (timeToTraverse + timeToRest)) {
			startMillis = startMillis + (timeToTraverse + timeToRest);
		}
		
		double param = (t - startMillis) / (timeToTraverse + timeToRest);
		assert param >= 0.0;
		assert param <= 1.0;
		
		if ((timeToTraverse + timeToRest) * param <= timeToTraverse) {
			// traverse
			double top = x + (((1 + timeToRest / timeToTraverse)) * param) * ((brX + height) - x);
			double bottom = y + (((1 + timeToRest / timeToTraverse)) * param) * ((brY + width) - y);
			
			double p0x;
			double p0y;
			double p1x;
			double p1y;
			if (top <= brX-5) {
				p0x = top;
				p0y = y;
				p1x = top+5;
				p1y = y;
			} else {
				p0x = brX;
				p0y = y + (top-brX);
				p1x = brX;
				p1y = Math.min(y + (top-brX)+5, brY);
			}
			
			double p2x;
			double p2y;
			double p3x;
			double p3y;
			if (bottom <= brY-5) {
				p2x = x;
				p2y = bottom;
				p3x = x;
				p3y = bottom+5;
			} else {
				p2x = x + (bottom - brY);
				p2y = brY;
				p3x = Math.min(x + (bottom - brY)+5, brX);
				p3y = brY;
			}
			
			poly.setPoints(p0x, p0y, p1x, p1y, p2x, p2y, p3x, p3y);
			ctxt.setColor(Color.WHITE);
			poly.paint(ctxt);
			
		} else {
			// rest
			
		}
	}
}