package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleTool extends ToolBase {
	
	enum CircleToolMode {
		FREE,
		SET,
		KNOB,
	}
	
	CircleToolMode mode;
	double xRadius;
	double yRadius;
	CircleToolShape shape;
	
	final Knob ulKnob;
	final Knob brKnob;
	
	Knob knob;
	
	public CircleTool() {
		super();
		
		mode = CircleToolMode.FREE;
		yRadius = Vertex.INIT_VERTEX_RADIUS;
		xRadius = Vertex.INIT_VERTEX_RADIUS;
		
		ulKnob = new Knob(APP.worldScreen.world) {
			public void drag(Point p) {
				
				Point newPoint = world.quadrantMap.getPoint(p);
				
				Dim offset = shape.c1.aabb.dim.multiply(0.5);
				
				Point newPoint2 = new Point(newPoint.x + offset.width, newPoint.y + offset.height);
				
				CircleTool.this.setPoint(newPoint2);
			}
		};
		
		brKnob = new Knob(APP.worldScreen.world) {
			public void drag(Point p) {
				
				Point newPoint = world.quadrantMap.getPoint(p);
				
				Point diff = new Point(newPoint.x - this.p.x, newPoint.y - this.p.y);
				
				xRadius += diff.x/2;
				yRadius += diff.y/2;
				
				Point newPoint1 = CircleTool.this.p.plus(diff.multiply(0.5));
				
				CircleTool.this.setPoint(newPoint1);
			}
		};
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new CircleToolShape(p, xRadius, yRadius);
			ulKnob.setPoint(shape.c1.aabb.ul);
			brKnob.setPoint(shape.c1.aabb.br);
		} else {
			shape = null;
		}	
	}
	
	public void setXRadius(double r) {
		this.xRadius = r;
		
		shape = new CircleToolShape(p, xRadius, yRadius);
	}
	
	public void setYRadius(double r) {
		this.yRadius = r;
		
		shape = new CircleToolShape(p, xRadius, yRadius);
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Point getBR() {
		return shape.c1.aabb.br;
	}
	
	public void escKey() {
		switch (mode) {
		case FREE:
			APP.worldScreen.tool = new RegularTool();
			APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
			APP.worldScreen.contentPane.repaint();
			break;
		case SET:
			mode = CircleToolMode.FREE;
			APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
			APP.worldScreen.contentPane.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void aKey() {
		switch (mode) {
		case FREE:
			mode = CircleToolMode.SET;
			APP.worldScreen.contentPane.repaint();
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke(APP.worldScreen.world);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke();
			APP.worldScreen.world.graph.computeVertexRadii(affected);
			
			APP.worldScreen.tool = new RegularTool();
			
			APP.worldScreen.tool.setPoint(APP.worldScreen.world.lastMovedWorldPoint);
			
			APP.worldScreen.world.render_worldPanel();
			APP.worldScreen.world.render_preview();
			APP.worldScreen.contentPane.repaint();
			break;
		case KNOB:
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			
			APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
			
			APP.worldScreen.contentPane.repaint();
			break;
		case SET:
		case KNOB:
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(APP.worldScreen.world.lastDraggedWorldPoint);
			break;
		case SET:
			if (!APP.worldScreen.world.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(ulKnob.hitTest(APP.worldScreen.world.lastPressedWorldPoint) ||
					brKnob.hitTest(APP.worldScreen.world.lastPressedWorldPoint))) {
				break;
			}
			if (ulKnob.hitTest(APP.worldScreen.world.lastPressedWorldPoint)) {
				mode = CircleToolMode.KNOB;
				knob = ulKnob;
				origKnobCenter = knob.p;
			} else if (brKnob.hitTest(APP.worldScreen.world.lastPressedWorldPoint)) {
				mode = CircleToolMode.KNOB;
				knob = brKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			
			Point diff = new Point(APP.worldScreen.world.lastDraggedWorldPoint.x - APP.worldScreen.world.lastPressedWorldPoint.x, APP.worldScreen.world.lastDraggedWorldPoint.y - APP.worldScreen.world.lastPressedWorldPoint.y);
			
			knob.drag(origKnobCenter.plus(diff));
			
			APP.worldScreen.contentPane.repaint();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
		case SET:
			break;
		case KNOB:	
			mode = CircleToolMode.SET;
			APP.worldScreen.contentPane.repaint();
			break;
		}
	}
	
	Point origKnobCenter;
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		shape.draw(ctxt);
		
		switch (mode) {
		case FREE:
			break;
		case SET:
		case KNOB:
			ulKnob.draw(ctxt);
			brKnob.draw(ctxt);
			break;
		}
		
		ctxt.setPaintMode();
	}

}
