package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.model.Quadrant;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class FixtureCursor extends Cursor {
	
	Quadrant currentQuadrant;
	Quadrant top;
	Quadrant left;
	Quadrant right;
	Quadrant bottom;
	
	double distToTopOrBottom;
	double distToLeftOrRight;
	
	private Axis axis;
	
	private FixtureCursorShape shape;
	
	public FixtureCursor() {
		
//		axis = Axis.NONE;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			currentQuadrant = MODEL.world.findQuadrant(p);
			
			if (currentQuadrant == null) {
				shape = null;
				axis = null;
				return;
			}
			
			if (!currentQuadrant.active) {
				shape = null;
				axis = null;
				return;
			}
			
			top = MODEL.world.upFixPoint(currentQuadrant);
			Point topCenter = top.center();
			
			bottom = MODEL.world.downFixPoint(currentQuadrant);
			Point bottomCenter = bottom.center();
			
			left = MODEL.world.leftFixPoint(currentQuadrant);
			Point leftCenter = left.center();
			
			right = MODEL.world.rightFixPoint(currentQuadrant);
			Point rightCenter = right.center();
			
			double distToTop = Math.abs(p.y - (topCenter.y - MODEL.QUADRANT_HEIGHT/2));
			double distToBottom = Math.abs(p.y - (bottomCenter.y + MODEL.QUADRANT_HEIGHT/2));
			double distToLeft = Math.abs(p.x - (leftCenter.x - MODEL.QUADRANT_WIDTH/2));
			double distToRight = Math.abs(p.x - (rightCenter.x + MODEL.QUADRANT_WIDTH/2));
			
			distToTopOrBottom = Math.min(distToTop, distToBottom);
			
			distToLeftOrRight = Math.min(distToLeft, distToRight);
			
			if (distToTopOrBottom < distToLeftOrRight) {
				axis = Axis.TOPBOTTOM;
			} else {
				axis = Axis.LEFTRIGHT;
			}
			
			switch (axis) {
			case LEFTRIGHT:
				shape = new FixtureCursorShape(p, leftCenter, rightCenter, axis);
				break;
			case TOPBOTTOM:
				shape = new FixtureCursorShape(p, topCenter, bottomCenter, axis);
				break;
			default:
				assert false;
				break;
			}
		} else {
			currentQuadrant = null;
			top = null;
			left = null;
			right = null;
			bottom = null;
			
			distToTopOrBottom = -1;
			distToLeftOrRight = -1;
			
			axis = null;
			
			shape = null;
		}
		
	}
	
	public Axis getAxis() {
		return axis;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Point getSourcePoint() {
		return shape.worldSource.center;
	}
	
	public Point getSinkPoint() {
		return shape.worldSink.center;
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		if (axis == null) {
			
		} else {
			
			shape.draw(ctxt);
			
		}
		
	}
	
}
