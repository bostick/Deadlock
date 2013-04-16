package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Polygon;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Shimmer {
	
	public AABB target;
	
	public double startMillis;
	
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
		
//		System.out.println(param);
		
		if ((timeToTraverse + timeToRest) * param <= timeToTraverse) {
			// traverse
			double top = target.x + (((1 + timeToRest / timeToTraverse)) * param) * ((target.brX + target.height) - target.x);
			double bottom = target.y + (((1 + timeToRest / timeToTraverse)) * param) * ((target.brY + target.width) - target.y);
			
			Point p0;
			Point p1;
			if (top <= target.brX-5) {
				p0 = new Point(top, target.y);
				p1 = new Point(top+5, target.y);
			} else {
				p0 = new Point(target.brX, target.y + (top-target.brX));
				p1 = new Point(target.brX, Math.min(target.y + (top-target.brX)+5, target.brY));
			}
			
			Point p2;
			Point p3;
			if (bottom <= target.brY-5) {
				p2 = new Point(target.x, bottom);
				p3 = new Point(target.x, bottom+5);
			} else {
				p2 = new Point(target.x + (bottom - target.brY), target.brY);
				p3 = new Point(Math.min(target.x + (bottom - target.brY)+5, target.brX), target.brY);
			}
			
			Polygon poly = APP.platform.createPolygon4(p0, p1, p3, p2);
			ctxt.setColor(Color.WHITE);
			poly.paint(ctxt);
			
		} else {
			// rest
			
		}
	}
}
