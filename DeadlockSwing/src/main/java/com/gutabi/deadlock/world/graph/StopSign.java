package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;

@SuppressWarnings("static-access")
public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	public final World world;
	public final Road e;
	public final Vertex v;
	
	int dir;
	Point p;
	double r = 0.5 * STOPSIGN_SIZE;
	
	private boolean enabled;
	
	private Circle shape;
	
	public StopSign(World world, Road e, int dir) {
		this.world = world;
		this.e = e;
		this.dir = dir;
		
		if (dir == 0) {
			v = e.start;
		} else {
			v = e.end;
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
			p = e.getStartBorderPoint();
		} else {
			p = e.getEndBorderPoint();
		}
		
		shape = new Circle(this, p, r);
	}	
	
	public void paint(RenderingContext ctxt) {
		
		if (APP.STOPSIGN_DRAW) {
			
			if (enabled) {
				
				ctxt.paintImage(
						p.x - StopSign.STOPSIGN_SIZE/2, p.y - StopSign.STOPSIGN_SIZE/2, 1/APP.PIXELS_PER_METER,
						VIEW.sheet,
						0, 0, (int)Math.round(APP.PIXELS_PER_METER * STOPSIGN_SIZE), (int)Math.round(APP.PIXELS_PER_METER * STOPSIGN_SIZE),
						32, 224, 32+32, 224+32);
			}
			
			if (APP.DEBUG_DRAW) {
				
				shape.getAABB().draw(ctxt);
				
			}
			
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		if (enabled) {
			ctxt.setColor(Color.RED);
			shape.paint(ctxt);
		} else {
			ctxt.setPixelStroke(1);
			ctxt.setColor(Color.WHITE);
			shape.draw(ctxt);
		}
		
	}
	
}
