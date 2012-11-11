package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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
	
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		if (a == Axis.LEFTRIGHT) {
			g2.drawImage(MODEL.world.sheet,
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((p.x + r) * MODEL.PIXELS_PER_METER),
					(int)((p.y + r) * MODEL.PIXELS_PER_METER),
					128, 224, 128+32, 224+32,
					null);
		} else {
			g2.drawImage(MODEL.world.sheet,
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((p.x + r) * MODEL.PIXELS_PER_METER),
					(int)((p.y + r) * MODEL.PIXELS_PER_METER),
					96, 224, 96+32, 224+32,
					null);
		}
		
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
			
		}
		
	}

}
