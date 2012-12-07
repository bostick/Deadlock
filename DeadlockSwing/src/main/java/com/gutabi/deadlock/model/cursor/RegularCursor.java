package com.gutabi.deadlock.model.cursor;


import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Vertex;
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
			shape = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.GRAY);
		ctxt.setWorldPixelStroke(1);
		
		shape.draw(ctxt);
		
	}
	
}
