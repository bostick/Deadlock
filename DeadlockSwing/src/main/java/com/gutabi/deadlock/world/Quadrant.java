package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.cursor.Cursor;

@SuppressWarnings("static-access")
public class Quadrant {
	
	public final int r;
	public final int c;
	public final boolean active;
	
	public Quadrant up;
	public Quadrant left;
	public Quadrant right;
	public Quadrant down;
	
	public boolean grid;
	
	public final AABB aabb;
	
	public Quadrant(int r, int c, boolean active) {
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
		
		double curGridSpacingPixels =  gridSpacing * APP.world.PIXELS_PER_METER_DEBUG;
		while (curGridSpacingPixels > 80) {
			gridSpacing -= 0.1;
			curGridSpacingPixels =  gridSpacing * APP.world.PIXELS_PER_METER_DEBUG;
		}
		while (curGridSpacingPixels < 48) {
			gridSpacing += 0.1;
			curGridSpacingPixels =  gridSpacing * APP.world.PIXELS_PER_METER_DEBUG;
		}
	}
	
	public void setCursorPoint(Cursor c, Point lastPoint) {
		if (grid) {
			
			Point closestGridPoint = new Point(gridSpacing * Math.round(lastPoint.x / gridSpacing), gridSpacing * Math.round(lastPoint.y / gridSpacing));
			if (aabb.hitTest(closestGridPoint)) {
				c.setPoint(closestGridPoint);
			} else {
				c.setPoint(lastPoint);
			}
			
		} else {
			c.setPoint(lastPoint);
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (active) {
			
			switch (ctxt.type) {
			case CANVAS:
				if (!APP.DEBUG_DRAW) {
					
					ctxt.paintWorldImage(c * APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT, APP.world.quadrantGrass,
							0,
							0,
							(int)Math.round(APP.world.PIXELS_PER_METER_DEBUG * APP.QUADRANT_WIDTH),
							(int)Math.round(APP.world.PIXELS_PER_METER_DEBUG * APP.QUADRANT_HEIGHT),
							0,
							0,
							APP.world.quadrantGrass.getWidth(),
							APP.world.quadrantGrass.getHeight());
					
				} else {
					
					ctxt.setColor(VIEW.DARKGREEN);
					aabb.paint(ctxt);
					
					ctxt.setColor(Color.BLACK);
					ctxt.setWorldPixelStroke(1);
					aabb.draw(ctxt);
					
					ctxt.setColor(Color.BLACK);
					ctxt.paintWorldString(c * APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT + 1, 1.0, c + " " + r);
					
				}
				
				if (grid) {
					
					ctxt.setColor(Color.GRAY);
					ctxt.setWorldPixelStroke(1);
					
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
