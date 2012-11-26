package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.cursor.RegularCursor;

@SuppressWarnings("static-access")
public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	public final Road e;
	public final Vertex v;
	
	int dir;
	Point p;
	double r = 0.5 * STOPSIGN_SIZE;
	
//	protected Color color;
//	protected Color hiliteColor;
	
	private boolean enabled;
	
	private Shape shape;
	
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
	
	public void paint(Graphics2D g2) {
		
		if (enabled) {
			
			AffineTransform origTransform = g2.getTransform();
			
			g2.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
			g2.translate(p.x, p.y);
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			g2.drawImage(VIEW.sheet,
					(int)(-STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
					(int)(-STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
					(int)(STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
					(int)(STOPSIGN_SIZE * MODEL.PIXELS_PER_METER * 0.5),
					32, 224, 32+32, 224+32,
					null);
			
			g2.setTransform(origTransform);
			
		}
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			shape.getAABB().draw(g2);
			
		}
		
	}
	
	public void paintHilite(Graphics2D g2) {
		
		if (enabled) {
			
			g2.setColor(Color.RED);
			
			shape.paint(g2);
			
		} else {
			
			java.awt.Stroke origStroke = g2.getStroke();
			g2.setStroke(RegularCursor.solidOutlineStroke);
			
			g2.setColor(Color.WHITE);
			
			shape.draw(g2);
			
			g2.setStroke(origStroke);
			
		}
		
	}
	
}
