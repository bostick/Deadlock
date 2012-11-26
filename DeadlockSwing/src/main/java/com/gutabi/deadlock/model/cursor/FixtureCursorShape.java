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

@SuppressWarnings("static-access")
public class FixtureCursorShape extends Shape {
	
	public final Point p;
	public final Axis axis;
	public final Circle worldSource;
	public final Circle worldSink;
	
	public final AABB aabb;
	
	public FixtureCursorShape(Point p, Point source, Point sink, Axis axis) {
		
		this.p = p;
		this.axis = axis;
		
		switch (axis) {
		case LEFTRIGHT:
			worldSource = new Circle(null, new Point(source.x - MODEL.QUADRANT_WIDTH/2, p.y), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(null, new Point(sink.x + MODEL.QUADRANT_WIDTH/2, p.y), Vertex.INIT_VERTEX_RADIUS);
			break;
		case TOPBOTTOM:
			worldSource = new Circle(null, new Point(p.x, source.y - MODEL.QUADRANT_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(null, new Point(p.x, sink.y + MODEL.QUADRANT_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
			break;
		default:
			assert false;
			worldSource = null;
			worldSink = null;
			break;
		}
		
		AABB aabbTmp = null;
		aabbTmp = AABB.union(aabbTmp, worldSource.aabb);
		aabbTmp = AABB.union(aabbTmp, worldSink.aabb);
		aabb = aabbTmp;
		
	}

	@Override
	public boolean hitTest(Point p) {
		assert false;
		return false;
	}
	
	@Override
	public Shape plus(Point p) {
		assert false;
		return null;
	}

	@Override
	public AABB getAABB() {
		return aabb;
	}

	@Override
	public void draw(Graphics2D g2) {
		assert false;
	}
	
	@Override
	public void paint(Graphics2D g2) {
		
		java.awt.Stroke origStroke = g2.getStroke();
//		AffineTransform origTransform = g2.getTransform();
		
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
//		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			aabb.draw(g2);
			
		}
		
	} 
	
}
