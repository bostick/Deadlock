package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

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
	
	public boolean supportsStopSigns() {
		return false;
	}
	
	public void paint(Graphics2D backgroundGraphImageG2, Graphics2D previewBackgroundImageG2) {
		
		if (a == Axis.LEFTRIGHT) {
			backgroundGraphImageG2.drawImage(VIEW.sheet,
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((p.x + r) * MODEL.PIXELS_PER_METER),
					(int)((p.y + r) * MODEL.PIXELS_PER_METER),
					128, 224, 128+32, 224+32,
					null);
		} else {
			backgroundGraphImageG2.drawImage(VIEW.sheet,
					(int)((p.x - r) * MODEL.PIXELS_PER_METER),
					(int)((p.y - r) * MODEL.PIXELS_PER_METER),
					(int)((p.x + r) * MODEL.PIXELS_PER_METER),
					(int)((p.y + r) * MODEL.PIXELS_PER_METER),
					96, 224, 96+32, 224+32,
					null);
		}
		
//		previewBackgroundImageG2.fillOval((int)((p.x - r) * 100 / MODEL.world.worldWidth), (int)((p.y - r) * 100 / MODEL.world.worldHeight), (int)((2 * r) * 100 / MODEL.world.worldHeight), (int)((2 * r) * 100 / MODEL.world.worldHeight));
		
		previewBackgroundImageG2.setColor(VIEW.LIGHTGREEN);
		
		AffineTransform origTransform = previewBackgroundImageG2.getTransform();
		
		previewBackgroundImageG2.scale(100 / (MODEL.world.worldWidth * MODEL.PIXELS_PER_METER), 100 / (MODEL.world.worldHeight * MODEL.PIXELS_PER_METER));
		
		shape.paint(previewBackgroundImageG2);
		
		previewBackgroundImageG2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			shape.getAABB().draw(backgroundGraphImageG2);
			
		}
		
	}

}
