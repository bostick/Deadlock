package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.Cursor;

@SuppressWarnings("static-access")
public class FixtureCursor extends Cursor {
	
	int currentQuadrant;
	int top;
	int left;
	int right;
	int bottom;
	
	double distToTopOrBottom;
	double distToLeftOrRight;
	
	Circle worldSource;
	Circle worldSink;
	
	private Axis axis;
	
	public FixtureCursor() {
		
//		sourceCircle = new Circle(this, new Point(0, 0), Vertex.INIT_VERTEX_RADIUS);
//		sinkCircle = new Circle(this, new Point(0, 0), Vertex.INIT_VERTEX_RADIUS);
		
		axis = Axis.NONE;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		currentQuadrant = MODEL.world.findQuadrant(p);
		
		if (currentQuadrant == -1) {
			worldSource = null;
			worldSink = null;
			axis = null;
			return;
		}
		
		if (!MODEL.world.isActiveQuadrant(currentQuadrant)) {
			worldSource = null;
			worldSink = null;
			axis = null;
			return;
		}
		
		top = currentQuadrant;
		while (true) {
			int next = MODEL.world.quadrantUp(top);
			if (MODEL.world.isValidQuadrant(next) && MODEL.world.isActiveQuadrant(next)) {
				top = next;
			} else {
				break;
			}
		}
		Point topCenter = MODEL.world.quadrantCenter(top);
		
		bottom = currentQuadrant;
		while (true) {
			int next = MODEL.world.quadrantDown(bottom);
			if (MODEL.world.isValidQuadrant(next) && MODEL.world.isActiveQuadrant(next)) {
				bottom = next;
			} else {
				break;
			}
		}
		Point bottomCenter = MODEL.world.quadrantCenter(bottom);
		
		left = currentQuadrant;
		while (true) {
			int next = MODEL.world.quadrantLeft(left);
			if (MODEL.world.isValidQuadrant(next) && MODEL.world.isActiveQuadrant(next)) {
				left = next;
			} else {
				break;
			}
		}
		Point leftCenter = MODEL.world.quadrantCenter(left);
		
		right = currentQuadrant;
		while (true) {
			int next = MODEL.world.quadrantRight(right);
			if (MODEL.world.isValidQuadrant(next) && MODEL.world.isActiveQuadrant(next)) {
				right = next;
			} else {
				break;
			}
		}
		Point rightCenter = MODEL.world.quadrantCenter(right);
		
		double distToTop = Math.abs(p.y - (topCenter.y - MODEL.world.QUADRANT_HEIGHT/2));
		double distToBottom = Math.abs(p.y - (bottomCenter.y + MODEL.world.QUADRANT_HEIGHT/2));
		double distToLeft = Math.abs(p.x - (leftCenter.x - MODEL.world.QUADRANT_WIDTH/2));
		double distToRight = Math.abs(p.x - (rightCenter.x + MODEL.world.QUADRANT_WIDTH/2));
		
		distToTopOrBottom = Math.min(distToTop, distToBottom);
		
		distToLeftOrRight = Math.min(distToLeft, distToRight);
		
		if (distToTopOrBottom < distToLeftOrRight) {
			axis = Axis.TOPBOTTOM;
		} else {
			axis = Axis.LEFTRIGHT;
		}
		
		switch (axis) {
		case LEFTRIGHT:
			worldSource = new Circle(null, new Point(leftCenter.x - MODEL.world.QUADRANT_WIDTH/2, p.y), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(null, new Point(rightCenter.x + MODEL.world.QUADRANT_WIDTH/2, p.y), Vertex.INIT_VERTEX_RADIUS);
			break;
		case TOPBOTTOM:
			worldSource = new Circle(null, new Point(p.x, topCenter.y - MODEL.world.QUADRANT_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(null, new Point(p.x, bottomCenter.y + MODEL.world.QUADRANT_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
			break;
		default:
			assert false;
			break;
		}
		
		aabb = null;
		aabb = AABB.union(aabb, worldSource.aabb);
		aabb = AABB.union(aabb, worldSink.aabb);
		
	}
	
	public Axis getAxis() {
		return axis;
	}
	
	public Point getSourcePoint() {
		return worldSource.center;
	}
	
	public Point getSinkPoint() {
		return worldSink.center;
	}
	
	public boolean intersect(Shape s) {
		return ShapeUtils.intersect(s, worldSource) || ShapeUtils.intersect(s, worldSink);
	}
	
	public boolean intersect(AABB aabb) {
		assert false;
		return false;
	}
	
	public boolean completelyWithin(AABB par) {
		assert false;
		return false;
	}
	
	public void paint(Graphics2D g2) {
		
		if (p == null) {
			return;
		}
		
		if (axis == null) {
			
		} else {
			
			java.awt.Stroke origStroke = g2.getStroke();
//			AffineTransform origTransform = g2.getTransform();
			
			g2.setColor(Color.GRAY);
			g2.setStroke(RegularCursor.solidOutlineStroke);
			
			switch (axis) {
			case LEFTRIGHT:
				worldSource.draw(g2);
				worldSink.draw(g2);
				g2.drawLine(
						(int)(worldSource.center.x * MODEL.PIXELS_PER_METER),
						(int)(p.y * MODEL.PIXELS_PER_METER),
						(int)(worldSink.center.x * MODEL.PIXELS_PER_METER),
						(int)(p.y * MODEL.PIXELS_PER_METER));
				break;
			case TOPBOTTOM:
				worldSource.draw(g2);
				worldSink.draw(g2);
				g2.drawLine(
						(int)(p.x * MODEL.PIXELS_PER_METER),
						(int)(worldSource.center.y * MODEL.PIXELS_PER_METER),
						(int)(p.x * MODEL.PIXELS_PER_METER),
						(int)(worldSink.center.y * MODEL.PIXELS_PER_METER));
				break;
			default:
				assert false;
				break;
			}
			
			g2.setStroke(origStroke);
//			g2.setTransform(origTransform);
			
			if (MODEL.DEBUG_DRAW) {
				
//				paintAABB(g2);
				aabb.draw(g2);
				
			}
			
		}
		
		if (MODEL.DEBUG_DRAW) {
			
//			int r = currentQuadrant / MODEL.world.quadrantCols;
//			int c = currentQuadrant % MODEL.world.quadrantCols;
//			
//			g2.setColor(Color.BLACK);
//			g2.drawString(Double.toString(distToTopOrBottom), (int)(p.x * MODEL.PIXELS_PER_METER) - 0 - 10, (int)(p.y * MODEL.PIXELS_PER_METER) - 10 - 10);
//			g2.drawString(Double.toString(distToLeftOrRight), (int)(p.x * MODEL.PIXELS_PER_METER) - 10 - 10, (int)(p.y * MODEL.PIXELS_PER_METER) - 0 - 10);
//			g2.drawString(Integer.toString(right), (int)(p.x * MODEL.PIXELS_PER_METER) + 10 - 10, (int)(p.y * MODEL.PIXELS_PER_METER) - 0 - 10);
//			g2.drawString(Integer.toString(bottom), (int)(p.x * MODEL.PIXELS_PER_METER) - 0 - 10, (int)(p.y * MODEL.PIXELS_PER_METER) + 10 - 10);
		}
	}
	
}
