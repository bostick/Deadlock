package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	private Turning t;
	
//	private final Color color;
	
	public Intersection(Point p) {
		super(p);
		
		t = Turning.NONE;
		
		//color = new Color(0x44, 0x44, 0x44, 0xff);
//		color = Color.GRAY;
		
	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	public boolean supportsStopSigns() {
		return true;
	}
	
	public void setTurning(Turning t) {
		this.t = t;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		carQueue.clear();
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public void paint(Graphics2D backgroundGraphImageG2, Graphics2D previewBackgroundImageG2) {
		
		backgroundGraphImageG2.setColor(Color.GRAY);
		
		shape.paint(backgroundGraphImageG2);
		
		if (t == Turning.CLOCKWISE) {
			backgroundGraphImageG2.drawImage(VIEW.sheet,
					(int)((p.x) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.x) * MODEL.PIXELS_PER_METER) + 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) + 16,
					160, 224, 160+32, 224+32,
					null);
		} else if (t == Turning.COUNTERCLOCKWISE) {
			backgroundGraphImageG2.drawImage(VIEW.sheet,
					(int)((p.x) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.x) * MODEL.PIXELS_PER_METER) + 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) + 16,
					192, 224, 192+32, 224+32,
					null);
		}
		
		previewBackgroundImageG2.setColor(Color.GRAY);
		
		AffineTransform origTransform = previewBackgroundImageG2.getTransform();
		
		previewBackgroundImageG2.scale(100 / (MODEL.world.worldWidth * MODEL.PIXELS_PER_METER), 100 / (MODEL.world.worldHeight * MODEL.PIXELS_PER_METER));
		
		shape.paint(previewBackgroundImageG2);
		
		previewBackgroundImageG2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			shape.getAABB().draw(backgroundGraphImageG2);
			
		}
		
	}
	
	public void paintHilite(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		shape.draw(g2);
		
	}
}
