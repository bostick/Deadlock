package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

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
				
				if (a == Axis.LEFTRIGHT) {
					
					ctxt.paintImage(p.x - r, p.y - r, VIEW.sheet,
							0,
							0,
							2 * VIEW.metersToPixels(r),
							2 * VIEW.metersToPixels(r),
							128, 224, 128+32, 224+32);
					
				} else {
					
					ctxt.paintImage(p.x - r, p.y - r, VIEW.sheet,
							0,
							0,
							2 * VIEW.metersToPixels(r),
							2 * VIEW.metersToPixels(r),
							96, 224, 96+32, 224+32);
					
				}
				
			} else {
				
				ctxt.setColor(VIEW.LIGHTGREEN);
				shape.paint(ctxt);
				
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke();
				shape.getAABB().draw(ctxt);
				
			}
			break;
		case PREVIEW:
			ctxt.setColor(VIEW.LIGHTGREEN);
			
			shape.paint(ctxt);
			break;
		}
		
	}

}
