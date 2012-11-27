package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.cursor.RegularCursor;
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
			
			AffineTransform origTransform = ctxt.g2.getTransform();
			
			ctxt.g2.translate(p.x, p.y);
			ctxt.g2.scale(MODEL.METERS_PER_PIXEL_DEBUG, MODEL.METERS_PER_PIXEL_DEBUG);
			
			ctxt.g2.drawImage(VIEW.sheet,
					(int)(-MODEL.world.stopSignSizePixels() * 0.5),
					(int)(-MODEL.world.stopSignSizePixels() * 0.5),
					(int)(MODEL.world.stopSignSizePixels() * 0.5),
					(int)(MODEL.world.stopSignSizePixels() * 0.5),
					32, 224, 32+32, 224+32,
					null);
			
			ctxt.g2.setTransform(origTransform);
			
		}
		
		if (MODEL.DEBUG_DRAW) {
			
			shape.getAABB().draw(ctxt);
			
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
		if (enabled) {
			
			ctxt.g2.setColor(Color.RED);
			
			shape.paint(ctxt);
			
		} else {
			
			java.awt.Stroke origStroke = ctxt.g2.getStroke();
			ctxt.g2.setStroke(RegularCursor.solidOutlineStroke);
			
			ctxt.g2.setColor(Color.WHITE);
			
			shape.draw(ctxt);
			
			ctxt.g2.setStroke(origStroke);
			
		}
		
	}
	
}
