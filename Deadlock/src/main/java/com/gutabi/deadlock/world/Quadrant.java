package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.sprites.AnimatedGrass;

//@SuppressWarnings("static-access")
public class Quadrant {
	
//	public final WorldCamera cam;
	public final QuadrantMap map;
	public final int r;
	public final int c;
	public final boolean active;
	
	public Quadrant up;
	public Quadrant left;
	public Quadrant right;
	public Quadrant down;
	public Quadrant rightDown;
	
	public boolean grid;
	
	public final AABB aabb;
	
	public Quadrant(QuadrantMap map, int r, int c, boolean active) {
//		this.cam = cam;
		this.map = map;
		this.r = r;
		this.c = c;
		this.active = active;
		
		aabb = new AABB(c * QuadrantMap.QUADRANT_WIDTH, r * QuadrantMap.QUADRANT_HEIGHT, QuadrantMap.QUADRANT_WIDTH, QuadrantMap.QUADRANT_HEIGHT);
	}
	
	public void init() {
		if (active) {
			for (int i = 0; i < 16; i+=4) {
				for (int j = 0; j < 16; j+=4) {
					map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + i, r * QuadrantMap.QUADRANT_HEIGHT + j)));
				}
			}
			if (right == null || !right.active) {
				for (int i = 0; i < 16; i+=4) {
					map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + 16, r * QuadrantMap.QUADRANT_HEIGHT + i)));
				}
			}
			if (down == null || !down.active) {
				for (int i = 0; i < 16; i+=4) {
					map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + i, r * QuadrantMap.QUADRANT_HEIGHT + 16)));
				}
			}
			if (rightDown == null || !rightDown.active) {
				map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + 16, r * QuadrantMap.QUADRANT_HEIGHT + 16)));
			}
		}
	}
	
	public Point center() {
		return new Point(c * QuadrantMap.QUADRANT_WIDTH + QuadrantMap.QUADRANT_WIDTH/2, r * QuadrantMap.QUADRANT_HEIGHT + QuadrantMap.QUADRANT_HEIGHT/2);
	}
	
	public void toggleGrid() {
		grid = !grid;
	}
	
	private double gridSpacing = 2.0;
	
	public void computeGridSpacing() {
		
		double curGridSpacingPixels =  gridSpacing * pixelsPerMeter;
		while (curGridSpacingPixels > 64+16) {
			gridSpacing *= 0.5;
			curGridSpacingPixels =  gridSpacing * pixelsPerMeter;
		}
		while (curGridSpacingPixels < 64-16) {
			gridSpacing *= 2.0;
			curGridSpacingPixels =  gridSpacing * pixelsPerMeter;
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
	
	public String toFileString() {
		if (active) {
			return "1";
		} else {
			return "0";
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			if (active) {
				
				if (!ctxt.DEBUG_DRAW) {
					AffineTransform origTransform = ctxt.getTransform();
					
					ctxt.translate(c * QuadrantMap.QUADRANT_WIDTH, r * QuadrantMap.QUADRANT_HEIGHT);
					ctxt.paintImage(map.quadrantGrass,
							0, 0, QuadrantMap.QUADRANT_WIDTH, QuadrantMap.QUADRANT_HEIGHT,
							0, 0, map.quadrantGrass.getWidth(), map.quadrantGrass.getHeight());
					
					ctxt.setTransform(origTransform);
				} else {
					ctxt.setColor(VIEW.DARKGREEN);
					aabb.paint(ctxt);
					
					ctxt.setColor(Color.BLACK);
					ctxt.setPixelStroke(1);
					aabb.draw(ctxt);
					
					ctxt.setColor(Color.BLACK);
					ctxt.paintString(c * QuadrantMap.QUADRANT_WIDTH, r * QuadrantMap.QUADRANT_HEIGHT + 1, 1.0, c + " " + r);
				}
				
				if (grid) {
					
					ctxt.setColor(Color.GRAY);
					ctxt.setPixelStroke(1);
					
					for (double k = 0.0; DMath.lessThanEquals(k, QuadrantMap.QUADRANT_HEIGHT); k+=gridSpacing) {
						Point p0 = new Point(c * QuadrantMap.QUADRANT_WIDTH + 0, r * QuadrantMap.QUADRANT_HEIGHT + k);
						Point p1 = new Point(c * QuadrantMap.QUADRANT_WIDTH + QuadrantMap.QUADRANT_WIDTH, r * QuadrantMap.QUADRANT_HEIGHT + k);
						Line line = new Line(p0, p1);
						line.draw(ctxt);
					}
					for (double k = 0.0; DMath.lessThanEquals(k, QuadrantMap.QUADRANT_WIDTH); k+=gridSpacing) {
						Point p0 = new Point(c * QuadrantMap.QUADRANT_WIDTH + k, r * QuadrantMap.QUADRANT_HEIGHT + 0);
						Point p1 = new Point(c * QuadrantMap.QUADRANT_WIDTH + k, r * QuadrantMap.QUADRANT_HEIGHT + QuadrantMap.QUADRANT_HEIGHT);
						Line line = new Line(p0, p1);
						line.draw(ctxt);
					}
					
				}
				
			} else {
				ctxt.setColor(Color.DARK_GRAY);
				aabb.paint(ctxt);
			}
			break;
		case PREVIEW:
			if (active) {
				ctxt.setColor(VIEW.DARKGREEN);
				aabb.paint(ctxt);
			} else {
				ctxt.setColor(Color.DARK_GRAY);
				aabb.paint(ctxt);
			}
			break;
		}
		
	}
	
}
