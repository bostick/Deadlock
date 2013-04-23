package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.MutablePolygon;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Shimmer {
	
	public AABB target;
	
	public double startMillis;
	
	MutablePolygon poly = APP.platform.createMutablePolygon();
	
	public Shimmer(AABB target, double startMillis) {
		this.target = target;
		this.startMillis = startMillis;
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
		double timeToTraverse = target.width * traverseSpeed;
		
		double t = System.currentTimeMillis();
		while (t > startMillis + (timeToTraverse + timeToRest)) {
			startMillis = startMillis + (timeToTraverse + timeToRest);
		}
		
		double param = (t - startMillis) / (timeToTraverse + timeToRest);
		assert param >= 0.0;
		assert param <= 1.0;
		
		if ((timeToTraverse + timeToRest) * param <= timeToTraverse) {
			// traverse
			double top = target.x + (((1 + timeToRest / timeToTraverse)) * param) * ((target.brX + target.height) - target.x);
			double bottom = target.y + (((1 + timeToRest / timeToTraverse)) * param) * ((target.brY + target.width) - target.y);
			
			double p0x;
			double p0y;
			double p1x;
			double p1y;
			if (top <= target.brX-5) {
				p0x = top;
				p0y = target.y;
				p1x = top+5;
				p1y = target.y;
			} else {
				p0x = target.brX;
				p0y = target.y + (top-target.brX);
				p1x = target.brX;
				p1y = Math.min(target.y + (top-target.brX)+5, target.brY);
			}
			
			double p2x;
			double p2y;
			double p3x;
			double p3y;
			if (bottom <= target.brY-5) {
				p2x = target.x;
				p2y = bottom;
				p3x = target.x;
				p3y = bottom+5;
			} else {
				p2x = target.x + (bottom - target.brY);
				p2y = target.brY;
				p3x = Math.min(target.x + (bottom - target.brY)+5, target.brX);
				p3y = target.brY;
			}
			
			poly.setPoints(p0x, p0y, p1x, p1y, p2x, p2y, p3x, p3y);
			ctxt.setColor(Color.WHITE);
			poly.paint(ctxt);
			
		} else {
			// rest
			
		}
	}
}
