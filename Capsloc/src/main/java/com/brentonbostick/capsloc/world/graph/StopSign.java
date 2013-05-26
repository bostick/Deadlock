package com.brentonbostick.capsloc.world.graph;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.Entity;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.sprites.SpriteSheet.SpriteSheetSprite;

public class StopSign extends Entity {
	
	public static final double STOPSIGN_SIZE = 0.5;
	
	public final Road r;
	public final Vertex v;
	
	int dir;
	Point p;
	double radius = 0.5 * STOPSIGN_SIZE;
	
	private boolean enabled;
	
	public Circle shape;
	
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
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
	public void preStart() {
		
	}
	
	public void postStop() {
		
	}
	
	public boolean preStep(double t) {
		return false;
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
		
		shape = new Circle(p, radius);
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
				
				ctxt.pushTransform();
				
				ctxt.translate(p.x - StopSign.STOPSIGN_SIZE/2, p.y - StopSign.STOPSIGN_SIZE/2);
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.STOPSIGN, ctxt.cam.pixelsPerMeter, ctxt.cam.pixelsPerMeter, 0, 0, STOPSIGN_SIZE, STOPSIGN_SIZE);
				
				ctxt.popTransform();
				
				if (APP.DEBUG_DRAW) {
					shape.aabb.draw(ctxt);
				}
				
			}
			
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		if (enabled) {
			ctxt.setColor(Color.RED);
			shape.paint(ctxt);
		} else {
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			ctxt.setColor(Color.WHITE);
			shape.draw(ctxt);
		}
		
	}
	
}
