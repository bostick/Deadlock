package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class Quadrant {
	
	public final int r;
	public final int c;
	public final boolean active;
	
	public Quadrant up;
	public Quadrant left;
	public Quadrant right;
	public Quadrant down;
	
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
				
				if (APP.world.grid) {
					
					ctxt.setColor(Color.GRAY);
					ctxt.setWorldPixelStroke(1);
					
					for (int k = 0; k <= APP.QUADRANT_HEIGHT; k+=2) {
						Line line = new Line(c * APP.QUADRANT_WIDTH + 0, r * APP.QUADRANT_HEIGHT + k, c * APP.QUADRANT_WIDTH + APP.QUADRANT_WIDTH, r * APP.QUADRANT_HEIGHT + k);
						line.draw(ctxt);
					}
					for (int k = 0; k <= APP.QUADRANT_WIDTH; k+=2) {
						Line line = new Line(c * APP.QUADRANT_WIDTH + k, r * APP.QUADRANT_HEIGHT + 0, c * APP.QUADRANT_WIDTH + k, r * APP.QUADRANT_HEIGHT + APP.QUADRANT_HEIGHT);
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
