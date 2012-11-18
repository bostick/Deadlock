package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Vertex;

@SuppressWarnings("static-access")
public class RegularCursor extends Cursor {
	
	public static java.awt.Stroke solidOutlineStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	Circle circle;
	
	public RegularCursor() {
		
		circle = new Circle(this, new Point(0, 0), Vertex.INIT_VERTEX_RADIUS);
		
		localAABB = circle.aabb;
		
	}
	
	public boolean intersect(Shape s) {
		
		Circle worldCircle = (Circle)circle.plus(p);
		
		return s.intersect(worldCircle);
		
	}
	
	public void paint(Graphics2D g2) {
		
		if (p == null) {
			return;
		}
		
		java.awt.Stroke origStroke = g2.getStroke();
		AffineTransform origTransform = g2.getTransform();
		
		g2.setColor(Color.GRAY);
		g2.setStroke(solidOutlineStroke);
		
		g2.translate(p.x * MODEL.PIXELS_PER_METER, p.y * MODEL.PIXELS_PER_METER);
		
		circle.draw(g2);
		
		g2.setStroke(origStroke);
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			paintAABB(g2);
			
		}
		
	}
	
}
