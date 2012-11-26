package com.gutabi.deadlock.model.cursor;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.model.Cursor;

//@SuppressWarnings("static-access")
public class MergerCursor extends Cursor {
	
	MergerCursorShape shape;
	
	public void setPoint(Point p) {
		this.p = p;
		
		shape = new MergerCursorShape(p);
		
//		aabb = localAABB.plus(p);
	}
	
	public Shape getShape() {
		return shape;
	}
	
//	public boolean intersect(Shape s) {
//		
//		if (ShapeUtils.intersect(s, worldQ)) {
//			return true;
//		}
//		if (ShapeUtils.intersect(s, worldTop)) {
//			return true;
//		}
//		if (ShapeUtils.intersect(s, worldLeft)) {
//			return true;
//		}
//		if (ShapeUtils.intersect(s, worldRight)) {
//			return true;
//		}
//		if (ShapeUtils.intersect(s, worldBottom)) {
//			return true;
//		}
//		return false;
//		
//	}
	
//	public boolean intersect(AABB aabb) {
//		return aabb.intersect(this.aabb);
//	}
//	
//	public boolean completelyWithin(AABB par) {
//		return aabb.completelyWithin(par);
//	}
	
	public void paint(Graphics2D g2) {
	
		if (p == null) {
			return;
		}
		
		shape.paint(g2);
		
	}

}
