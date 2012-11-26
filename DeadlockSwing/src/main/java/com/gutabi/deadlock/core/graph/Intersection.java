package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	private Turning t;
	
	private final Color color;
	
	public Intersection(Point p) {
		super(p);
		
		t = Turning.NONE;
		
		//color = new Color(0x44, 0x44, 0x44, 0xff);
		color = Color.GRAY;
		
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
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(color);
		
		shape.paint(g2);
		
		if (t == Turning.CLOCKWISE) {
			g2.drawImage(VIEW.sheet,
					(int)((p.x) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.x) * MODEL.PIXELS_PER_METER) + 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) + 16,
					160, 224, 160+32, 224+32,
					null);
		} else if (t == Turning.COUNTERCLOCKWISE) {
			g2.drawImage(VIEW.sheet,
					(int)((p.x) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) - 16,
					(int)((p.x) * MODEL.PIXELS_PER_METER) + 16,
					(int)((p.y) * MODEL.PIXELS_PER_METER) + 16,
					192, 224, 192+32, 224+32,
					null);
		}
				
		if (MODEL.DEBUG_DRAW) {
			
			shape.getAABB().draw(g2);
			
		}
		
	}
	
	public void paintHilite(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		shape.draw(g2);
		
	}
}
