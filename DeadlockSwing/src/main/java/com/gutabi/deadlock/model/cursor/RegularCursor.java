package com.gutabi.deadlock.model.cursor;


import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class RegularCursor extends Cursor {
	
//	public static java.awt.Stroke solidOutlineStroke = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	Circle shape;
	
	public RegularCursor() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new Circle(null, p, 22.6274 / VIEW.PIXELS_PER_METER_DEBUG);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		java.awt.Stroke origStroke = ctxt.g2.getStroke();
		
		ctxt.g2.setColor(Color.GRAY);
		ctxt.g2.setStroke(new BasicStroke((float)(3.2 / VIEW.PIXELS_PER_METER_DEBUG), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		shape.draw(ctxt);
		
		ctxt.g2.setStroke(origStroke);
		
	}
	
}
