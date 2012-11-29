package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class FixtureCursorShape extends Shape {
	
	public final Point p;
	public final Axis axis;
	public final Circle worldSource;
	public final Circle worldSink;
	
	final Line line;
	
	public final AABB aabb;
	
	public FixtureCursorShape(Point p, Point source, Point sink, Axis axis) {
		
		this.p = p;
		this.axis = axis;
		
		switch (axis) {
		case LEFTRIGHT:
			worldSource = new Circle(null, new Point(source.x - MODEL.QUADRANT_WIDTH/2, p.y), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(null, new Point(sink.x + MODEL.QUADRANT_WIDTH/2, p.y), Vertex.INIT_VERTEX_RADIUS);
			line = new Line(worldSource.center.x, p.y, worldSink.center.x, p.y);
			break;
		case TOPBOTTOM:
			worldSource = new Circle(null, new Point(p.x, source.y - MODEL.QUADRANT_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
			worldSink = new Circle(null, new Point(p.x, sink.y + MODEL.QUADRANT_HEIGHT/2), Vertex.INIT_VERTEX_RADIUS);
			line = new Line(p.x, worldSource.center.y, p.x, worldSink.center.y);
			break;
		default:
			assert false;
			worldSource = null;
			worldSink = null;
			line = null;
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
	public void draw(RenderingContext ctxt) {
		assert false;
	}
	
	public void paint(RenderingContext ctxt) {
		
		java.awt.Stroke origStroke = ctxt.getStroke();
		
		ctxt.setColor(Color.GRAY);
		ctxt.setStroke(new BasicStroke((float)(3.2 / VIEW.PIXELS_PER_METER_DEBUG), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		worldSource.draw(ctxt);
		worldSink.draw(ctxt);
		line.draw(ctxt);
		
		ctxt.setStroke(origStroke);
		
		if (MODEL.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
		
	} 
	
}
