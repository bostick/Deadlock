package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class Quadrant {
	
	public final World world;
	public final int r;
	public final int c;
	public final boolean active;
	
	public Quadrant up;
	public Quadrant left;
	public Quadrant right;
	public Quadrant down;
	
	public boolean grid;
	
	public final AABB aabb;
	
	public Quadrant(World world, int r, int c, boolean active) {
		this.world = world;
		this.r = r;
		this.c = c;
		this.active = active;
		
		aabb = new AABB(c * APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT, APP.QUADRANT_WIDTH, APP.QUADRANT_HEIGHT);
	}
	
	public Point center() {
		return new Point(c * APP.QUADRANT_WIDTH + APP.QUADRANT_WIDTH/2, r * APP.QUADRANT_HEIGHT + APP.QUADRANT_HEIGHT/2);
	}
	
	public void toggleGrid() {
		grid = !grid;
	}
	
	private double gridSpacing = 2.0;
	
	public void computeGridSpacing() {
		
		double curGridSpacingPixels =  gridSpacing * APP.PIXELS_PER_METER;
		while (curGridSpacingPixels > 64+16) {
			gridSpacing *= 0.5;
			curGridSpacingPixels =  gridSpacing * APP.PIXELS_PER_METER;
		}
		while (curGridSpacingPixels < 64-16) {
			gridSpacing *= 2.0;
			curGridSpacingPixels =  gridSpacing * APP.PIXELS_PER_METER;
		}
	}
	
	public Point getPoint(Point p) {
		if (active) {
			
			if (grid) {
				Point closestGridPoint = new Point(gridSpacing * Math.round(p.x / gridSpacing), gridSpacing * Math.round(p.y / gridSpacing));
				if (aabb.hitTest(closestGridPoint)) {
					return closestGridPoint;
				} else {
					return p;
				}	
			} else {
				return p;
			}
			
		} else {
			return p;
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (active) {
			
			switch (ctxt.type) {
			case CANVAS:
				if (!APP.DEBUG_DRAW) {
					
					ctxt.paintImage(
							c * APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT, 1 / APP.PIXELS_PER_METER,
							world.quadrantGrass,
							0, 0,
							(int)Math.round(APP.PIXELS_PER_METER * APP.QUADRANT_WIDTH),
							(int)Math.round(APP.PIXELS_PER_METER * APP.QUADRANT_HEIGHT),
							0, 0,
							world.quadrantGrass.getWidth(),
							world.quadrantGrass.getHeight());
					
				} else {
					
					ctxt.setColor(VIEW.DARKGREEN);
					aabb.paint(ctxt);
					
					ctxt.setColor(Color.BLACK);
					ctxt.setPixelStroke(1);
					aabb.draw(ctxt);
					
					ctxt.setColor(Color.BLACK);
					ctxt.paintString(c * APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT + 1, 1.0 / APP.PIXELS_PER_METER, c + " " + r);
					
				}
				
				if (grid) {
					
					ctxt.setColor(Color.GRAY);
					ctxt.setPixelStroke(1);
					
					for (double k = 0.0; DMath.lessThanEquals(k, APP.QUADRANT_HEIGHT); k+=gridSpacing) {
						Point p0 = new Point(c * APP.QUADRANT_WIDTH + 0, r * APP.QUADRANT_HEIGHT + k);
						Point p1 = new Point(c * APP.QUADRANT_WIDTH + APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT + k);
						Line line = new Line(p0, p1);
						line.draw(ctxt);
					}
					for (double k = 0.0; DMath.lessThanEquals(k, APP.QUADRANT_WIDTH); k+=gridSpacing) {
						Point p0 = new Point(c * APP.QUADRANT_WIDTH + k, r * APP.QUADRANT_HEIGHT + 0);
						Point p1 = new Point(c * APP.QUADRANT_WIDTH + k, r * APP.QUADRANT_HEIGHT + APP.QUADRANT_HEIGHT);
						Line line = new Line(p0, p1);
						line.draw(ctxt);
					}
					
				}
				break;
			case PREVIEW:
				ctxt.setColor(VIEW.DARKGREEN);
				aabb.paint(ctxt);
				break;
			}
			
		}
	}
	
}
