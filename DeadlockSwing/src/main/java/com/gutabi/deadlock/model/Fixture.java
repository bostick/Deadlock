package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.view.RenderingContext;

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
	
	public void paint(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			if (!MODEL.DEBUG_DRAW) {
				
				AffineTransform origTransform = ctxt.g2.getTransform();
				
				ctxt.g2.translate(p.x, p.y);
				ctxt.g2.scale(MODEL.METERS_PER_PIXEL_DEBUG, MODEL.METERS_PER_PIXEL_DEBUG);
				
				if (a == Axis.LEFTRIGHT) {
					ctxt.g2.drawImage(VIEW.sheet,
							-MODEL.world.fixtureRadiusPixels(r),
							-MODEL.world.fixtureRadiusPixels(r),
							MODEL.world.fixtureRadiusPixels(r),
							MODEL.world.fixtureRadiusPixels(r),
							128, 224, 128+32, 224+32,
							null);
				} else {
					ctxt.g2.drawImage(VIEW.sheet,
							-MODEL.world.fixtureRadiusPixels(r),
							-MODEL.world.fixtureRadiusPixels(r),
							MODEL.world.fixtureRadiusPixels(r),
							MODEL.world.fixtureRadiusPixels(r),
							96, 224, 96+32, 224+32,
							null);
				}
				
				ctxt.g2.setTransform(origTransform);
				
			} else {
				
				shape.draw(ctxt);
				
				shape.getAABB().draw(ctxt);
				
			}
			break;
		case PREVIEW:
			ctxt.g2.setColor(VIEW.LIGHTGREEN);
			
			shape.paint(ctxt);
			break;
		}
		
	}

}
