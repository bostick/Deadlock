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
		
		aabb = new AABB(c * World.QUADRANT_WIDTH, r * World.QUADRANT_HEIGHT, World.QUADRANT_WIDTH, World.QUADRANT_HEIGHT);
	}
	
	public Point center() {
		return new Point(c * World.QUADRANT_WIDTH + World.QUADRANT_WIDTH/2, r * World.QUADRANT_HEIGHT + World.QUADRANT_HEIGHT/2);
	}
	
	public void toggleGrid() {
		grid = !grid;
	}
	
	private double gridSpacing = 2.0;
	
	public void computeGridSpacing() {
		
		double curGridSpacingPixels =  gridSpacing * world.pixelsPerMeter;
		while (curGridSpacingPixels > 64+16) {
			gridSpacing *= 0.5;
			curGridSpacingPixels =  gridSpacing * world.pixelsPerMeter;
		}
		while (curGridSpacingPixels < 64-16) {
			gridSpacing *= 2.0;
			curGridSpacingPixels =  gridSpacing * world.pixelsPerMeter;
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
		
		switch (ctxt.type) {
		case CANVAS:
			if (active) {
				
				if (!APP.DEBUG_DRAW) {
					
					ctxt.paintImage(
							c * World.QUADRANT_WIDTH, r * World.QUADRANT_HEIGHT, 1 / world.pixelsPerMeter,
							world.quadrantGrass,
							0, 0,
							(int)Math.ceil(world.pixelsPerMeter * World.QUADRANT_WIDTH),
							(int)Math.ceil(world.pixelsPerMeter * World.QUADRANT_HEIGHT),
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
					ctxt.paintString(c * World.QUADRANT_WIDTH, r * World.QUADRANT_HEIGHT + 1, 1.0 / world.pixelsPerMeter, c + " " + r);
					
				}
				
				if (grid) {
					
					ctxt.setColor(Color.GRAY);
					ctxt.setPixelStroke(1);
					
					for (double k = 0.0; DMath.lessThanEquals(k, World.QUADRANT_HEIGHT); k+=gridSpacing) {
						Point p0 = new Point(c * World.QUADRANT_WIDTH + 0, r * World.QUADRANT_HEIGHT + k);
						Point p1 = new Point(c * World.QUADRANT_WIDTH + World.QUADRANT_WIDTH, r * World.QUADRANT_HEIGHT + k);
						Line line = new Line(p0, p1);
						line.draw(ctxt);
					}
					for (double k = 0.0; DMath.lessThanEquals(k, World.QUADRANT_WIDTH); k+=gridSpacing) {
						Point p0 = new Point(c * World.QUADRANT_WIDTH + k, r * World.QUADRANT_HEIGHT + 0);
						Point p1 = new Point(c * World.QUADRANT_WIDTH + k, r * World.QUADRANT_HEIGHT + World.QUADRANT_HEIGHT);
						Line line = new Line(p0, p1);
						line.draw(ctxt);
					}
					
				}
				
			} else {
				ctxt.setColor(Color.WHITE);
				aabb.paint(ctxt);
			}
			break;
		case PREVIEW:
			if (active) {
				ctxt.setColor(VIEW.DARKGREEN);
				aabb.paint(ctxt);
			} else {
				ctxt.setColor(Color.WHITE);
				aabb.paint(ctxt);
			}
			break;
		}
		
	}
	
}
