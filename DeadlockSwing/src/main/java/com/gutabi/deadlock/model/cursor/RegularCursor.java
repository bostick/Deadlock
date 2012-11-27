package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class RegularCursor extends Cursor {
	
	public static java.awt.Stroke solidOutlineStroke = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	Circle worldCircle;
	
	AABB aabb;
	
	public RegularCursor() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		worldCircle = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		
		aabb = worldCircle.aabb;
	}
	
	public Shape getShape() {
		return worldCircle;
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		java.awt.Stroke origStroke = ctxt.g2.getStroke();
		
		ctxt.g2.setColor(Color.GRAY);
		ctxt.g2.setStroke(solidOutlineStroke);
		
		worldCircle.draw(ctxt);
		
		ctxt.g2.setStroke(origStroke);
		
		if (MODEL.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
		
	}
	
}
