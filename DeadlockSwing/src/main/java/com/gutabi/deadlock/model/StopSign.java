package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	public final Road e;
	public final Vertex v;
	
	int dir;
	Point p;
	double r = 0.5 * STOPSIGN_SIZE;
	
	private boolean enabled;
	
	private Circle shape;
	
	public StopSign(Road e, int dir) {
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
	
	public boolean isDeleteable() {
		return true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Shape getShape() {
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
		
		if (enabled) {
			
			ctxt.paintImage(p.x - StopSign.STOPSIGN_SIZE/2, p.y - StopSign.STOPSIGN_SIZE/2, VIEW.sheet,
					0,
					0,
					VIEW.metersToPixels(STOPSIGN_SIZE),
					VIEW.metersToPixels(STOPSIGN_SIZE),
					32, 224, 32+32, 224+32);
			
		}
		
		if (MODEL.DEBUG_DRAW) {
			
			shape.getAABB().draw(ctxt);
			
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		if (enabled) {
			ctxt.setColor(Color.RED);
			shape.paint(ctxt);
		} else {
			ctxt.setPixelStroke();
			ctxt.setColor(Color.WHITE);
			shape.draw(ctxt);
		}
		
	}
	
}
