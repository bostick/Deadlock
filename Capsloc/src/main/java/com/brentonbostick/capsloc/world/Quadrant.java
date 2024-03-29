package com.brentonbostick.capsloc.world;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.Line;
import com.brentonbostick.capsloc.geom.ShapeUtils;
import com.brentonbostick.capsloc.math.DMath;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.sprites.AnimatedGrass;
import com.brentonbostick.capsloc.world.sprites.SpriteSheet.SpriteSheetSprite;

public class Quadrant {
	
	public static final int GRASSTILES_PER_QUADRANT = 8;
	
	public final QuadrantMap map;
	public final int r;
	public final int c;
	public final boolean active;
	
	public Quadrant up;
	public Quadrant left;
	public Quadrant right;
	public Quadrant down;
	public Quadrant rightDown;
	
	public GrassMap grassMap = new GrassMap();
	
	public boolean grid;
	
	public final AABB aabb;
	
	public Quadrant(QuadrantMap map, int r, int c, boolean active) {
		
		if (map == null) {
			throw new IllegalArgumentException();
		}
		
		this.map = map;
		this.r = r;
		this.c = c;
		this.active = active;
		
		aabb = new AABB(c * QuadrantMap.QUADRANT_WIDTH, r * QuadrantMap.QUADRANT_HEIGHT, QuadrantMap.QUADRANT_WIDTH, QuadrantMap.QUADRANT_HEIGHT);
	}
	
	public void init() {
		if (active) {
//			for (int i = 0; i < 16; i+=4) {
//				for (int j = 0; j < 16; j+=4) {
//					map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + i, r * QuadrantMap.QUADRANT_HEIGHT + j)));
//				}
//			}
//			if (right == null || !right.active) {
//				for (int i = 0; i < 16; i+=4) {
//					map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + 16, r * QuadrantMap.QUADRANT_HEIGHT + i)));
//				}
//			}
//			if (down == null || !down.active) {
//				for (int i = 0; i < 16; i+=4) {
//					map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + i, r * QuadrantMap.QUADRANT_HEIGHT + 16)));
//				}
//			}
//			if (rightDown == null || !rightDown.active) {
//				map.grassMap.addGrass(new AnimatedGrass(new Point(c * QuadrantMap.QUADRANT_WIDTH + 16, r * QuadrantMap.QUADRANT_HEIGHT + 16)));
//			}
			
			for (int i = 0; i < 60; i++) {
				double x = APP.RANDOM.nextDouble();
				double y = APP.RANDOM.nextDouble();
				grassMap.addGrass(new AnimatedGrass(new Point((c + x) * QuadrantMap.QUADRANT_WIDTH, (r + y) * QuadrantMap.QUADRANT_HEIGHT)));
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
	
	public void computeGridSpacing(WorldCamera cam) {
		
		double curGridSpacingPixels =  gridSpacing * cam.pixelsPerMeter;
		while (curGridSpacingPixels > 64+16) {
			gridSpacing *= 0.5;
			curGridSpacingPixels =  gridSpacing * cam.pixelsPerMeter;
		}
		while (curGridSpacingPixels < 64-16) {
			gridSpacing *= 2.0;
			curGridSpacingPixels =  gridSpacing * cam.pixelsPerMeter;
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
	
	public void preStart() {
		grassMap.preStart();
	}
	
	public boolean step(double t) {
		return grassMap.step(t);
	}
	
	public String toFileString() {
		if (active) {
			return "1";
		} else {
			return "0";
		}
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		if (!ShapeUtils.intersectAA(aabb, ctxt.cam.worldViewport)) {
			return;
		}
		
		if (!active) {
			ctxt.setColor(Color.DARK_GRAY);
			aabb.paint(ctxt);
			return;
		}
		
		if (!APP.DEBUG_DRAW) {
			
			ctxt.pushTransform();
			
			ctxt.translate(aabb.x, aabb.y);
			
			ctxt.scale(aabb.width/GRASSTILES_PER_QUADRANT, aabb.height/GRASSTILES_PER_QUADRANT);
			
			for (int i = 0; i < GRASSTILES_PER_QUADRANT; i++) {
				for (int j = 0; j < GRASSTILES_PER_QUADRANT; j++) {
					if (!ShapeUtils.intersectAA(ctxt.cam.worldViewport, aabb.x + (aabb.width/GRASSTILES_PER_QUADRANT) * j, aabb.y + (aabb.height/GRASSTILES_PER_QUADRANT) * i, aabb.x + (aabb.width/GRASSTILES_PER_QUADRANT) * (j + 1), aabb.y + (aabb.height/GRASSTILES_PER_QUADRANT) * (i + 1))) {
						continue;
					}
					APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASSTILE, 1.0, 1.0, j, i, j + 1, i + 1);
				}
			}
			
			ctxt.popTransform();
			
		} else {
			ctxt.setColor(Color.DARKGREEN);
			aabb.paint(ctxt);
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			aabb.draw(ctxt);
			
			ctxt.setColor(Color.BLACK);
			ctxt.paintString(c * QuadrantMap.QUADRANT_WIDTH, r * QuadrantMap.QUADRANT_HEIGHT + 1, 1.0/ctxt.cam.pixelsPerMeter, c + " " + r);
		}
		
		if (grid) {
			
			ctxt.setColor(Color.GRAY);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			
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
		
	}
	
//	public void paint_preview(RenderingContext ctxt) {
//		
//		if (active) {
//			ctxt.setColor(Color.DARKGREEN);
//			aabb.paint(ctxt);
//		} else {
//			ctxt.setColor(Color.DARK_GRAY);
//			aabb.paint(ctxt);
//		}
//		
//	}
	
	public void paintScene(RenderingContext ctxt) {
		grassMap.paintScene(ctxt);
	}
	
}
