package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Vertex;

@SuppressWarnings("static-access")
public abstract class Fixture extends Vertex {
	
	public final Axis a;
	
	public Fixture(Point p, Axis a) {
		super(p);
		this.a = a;
	}
	
	public boolean supportsStopSigns() {
		return false;
	}
	
	public void paint(Graphics2D g2) {
		
		if (a == Axis.LEFTRIGHT) {
			g2.drawImage(VIEW.sheet,
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((p.x + r) * MODEL.PIXELS_PER_METER),
					(int)((p.y + r) * MODEL.PIXELS_PER_METER),
					128, 224, 128+32, 224+32,
					null);
		} else {
			g2.drawImage(VIEW.sheet,
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((p.x + r) * MODEL.PIXELS_PER_METER),
					(int)((p.y + r) * MODEL.PIXELS_PER_METER),
					96, 224, 96+32, 224+32,
					null);
		}
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			shape.getAABB().draw(g2);
			
		}
		
	}

}
