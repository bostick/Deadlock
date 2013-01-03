package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.ui.RenderingContext;

public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	public final Road r;
	public final Vertex v;
	
	int dir;
	Point p;
	double radius = 0.5 * STOPSIGN_SIZE;
	
	private boolean enabled;
	
	private Circle shape;
	
	public StopSign(Road r, int dir) {
		this.r = r;
		this.dir = dir;
		
		if (dir == 0) {
			v = r.start;
		} else {
			v = r.end;
		} 
		
		enabled = false;
		
		computePoint();
		
	}
	
	public Point getPoint() {
		return p;
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Circle getShape() {
		return shape;
	}
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public void computePoint() {
		
		if (dir == 0) {
			p = r.getStartBorderPoint();
		} else {
			p = r.getEndBorderPoint();
		}
		
		shape = new Circle(this, p, radius);
	}
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("start sign\n");
		
		s.append("road " + r.id);
		s.append("direction " + dir);
		s.append("enabled " + enabled);
		
		s.append("end sign\n");
		
		return s.toString();
	}
	
	public static StopSign fromFileString(String s) {
		return null;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (APP.STOPSIGN_DRAW) {
			
			if (enabled) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(p.x - StopSign.STOPSIGN_SIZE/2, p.y - StopSign.STOPSIGN_SIZE/2);
				ctxt.paintImage(APP.sheet,
						0, 0, STOPSIGN_SIZE, STOPSIGN_SIZE,
						32, APP.spriteSectionRow+0, 32+32, APP.spriteSectionRow+0+32);
				
				ctxt.setTransform(origTransform);
				
				if (APP.DEBUG_DRAW) {
					shape.getAABB().draw(ctxt);
				}
				
			}
			
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		if (enabled) {
			ctxt.setColor(Color.RED);
			shape.paint(ctxt);
		} else {
			ctxt.setPixelStroke(1.0);
			ctxt.setColor(Color.WHITE);
			shape.draw(ctxt);
		}
		
	}
	
}
