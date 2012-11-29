package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.tree.AABB;
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
		
		aabb = new AABB(c * MODEL.QUADRANT_WIDTH, r * MODEL.QUADRANT_HEIGHT, MODEL.QUADRANT_WIDTH, MODEL.QUADRANT_HEIGHT);
	}
	
	public Point center() {
		return new Point(c * MODEL.QUADRANT_WIDTH + MODEL.QUADRANT_WIDTH/2, r * MODEL.QUADRANT_HEIGHT + MODEL.QUADRANT_HEIGHT/2);
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (active) {
			
			switch (ctxt.type) {
			case CANVAS:
				if (!MODEL.DEBUG_DRAW) {
					
					ctxt.paintImage(c * MODEL.QUADRANT_WIDTH, r * MODEL.QUADRANT_HEIGHT, VIEW.quadrantGrass,
							0,
							0,
							VIEW.metersToPixels(MODEL.QUADRANT_WIDTH),
							VIEW.metersToPixels(MODEL.QUADRANT_HEIGHT),
							0,
							0,
							VIEW.quadrantGrass.getWidth(),
							VIEW.quadrantGrass.getHeight());
					
				} else {
					
					
					ctxt.g2.setColor(VIEW.LIGHTGREEN);
					
					aabb.paint(ctxt);
					
					ctxt.g2.setColor(VIEW.DARKGREEN);
					ctxt.g2.setStroke(new BasicStroke((float)(3.2 / VIEW.PIXELS_PER_METER_DEBUG), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					aabb.draw(ctxt);
					
					ctxt.g2.setColor(Color.BLACK);
					ctxt.paintString(c * MODEL.QUADRANT_WIDTH, r * MODEL.QUADRANT_HEIGHT + 1, c + " " + r);
					
				}
				
				if (MODEL.grid) {
					
					ctxt.g2.setColor(Color.GRAY);
					ctxt.g2.setStroke(new BasicStroke((float)(3.2 / VIEW.PIXELS_PER_METER_DEBUG), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					
					for (int k = 0; k <= MODEL.QUADRANT_HEIGHT; k+=2) {
						Line line = new Line(c * MODEL.QUADRANT_WIDTH + 0, r * MODEL.QUADRANT_HEIGHT + k, c * MODEL.QUADRANT_WIDTH + MODEL.QUADRANT_WIDTH, r * MODEL.QUADRANT_HEIGHT + k);
						line.draw(ctxt);
					}
					for (int k = 0; k <= MODEL.QUADRANT_WIDTH; k+=2) {
						Line line = new Line(c * MODEL.QUADRANT_WIDTH + k, r * MODEL.QUADRANT_HEIGHT + 0, c * MODEL.QUADRANT_WIDTH + k, r * MODEL.QUADRANT_HEIGHT + MODEL.QUADRANT_HEIGHT);
						line.draw(ctxt);
					}
					
				}
				break;
			case PREVIEW:
				ctxt.g2.setColor(VIEW.DARKGREEN);
				aabb.paint(ctxt);
				break;
			}
			
		}
	}
	
}
