package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.model.World;

@SuppressWarnings("static-access")
public class FixtureCursor extends Cursor {
	
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
		
		double distToTopOrBottom = Math.min(p.y - 0.0, World.WORLD_HEIGHT - p.y);
		double distToLeftOrRight = Math.min(p.x - 0.0, World.WORLD_WIDTH - p.x);
		
		if (distToTopOrBottom < distToLeftOrRight) {
			axis = Axis.TOPBOTTOM;
		} else {
			axis = Axis.LEFTRIGHT;
		}
		
		switch (axis) {
		case LEFTRIGHT:
			worldSource = new Circle(this, new Point(0.0, p.y), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(this, new Point(World.WORLD_WIDTH, p.y), Vertex.INIT_VERTEX_RADIUS);
			break;
		case TOPBOTTOM:
			worldSource = new Circle(this, new Point(p.x, 0.0), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(this, new Point(p.x, World.WORLD_HEIGHT), Vertex.INIT_VERTEX_RADIUS);
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
		return s.intersect(worldSource) || s.intersect(worldSink);
	}
	
	public void paint(Graphics2D g2) {
		
		if (p == null) {
			return;
		}
		
		java.awt.Stroke origStroke = g2.getStroke();
//		AffineTransform origTransform = g2.getTransform();
		
		g2.setColor(Color.GRAY);
		g2.setStroke(RegularCursor.solidOutlineStroke);
		
		switch (axis) {
		case LEFTRIGHT:
			worldSource.draw(g2);
			worldSink.draw(g2);
			g2.drawLine((int)(0.0 * MODEL.PIXELS_PER_METER), (int)(p.y * MODEL.PIXELS_PER_METER), (int)(World.WORLD_WIDTH * MODEL.PIXELS_PER_METER), (int)(p.y * MODEL.PIXELS_PER_METER));
			break;
		case TOPBOTTOM:
			worldSource.draw(g2);
			worldSink.draw(g2);
			g2.drawLine((int)(p.x * MODEL.PIXELS_PER_METER), (int)(0.0 * MODEL.PIXELS_PER_METER), (int)(p.x * MODEL.PIXELS_PER_METER), (int)(World.WORLD_HEIGHT * MODEL.PIXELS_PER_METER));
			break;
		default:
			assert false;
			break;
		}
		
		g2.setStroke(origStroke);
//		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			aabb.paint(g2);
			
		}
		
	}
	
}
