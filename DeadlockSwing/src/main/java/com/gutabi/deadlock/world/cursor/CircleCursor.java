package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.List;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleCursor extends CursorBase {
	
	enum CircleCursorMode {
		FREE,
		SET,
		KNOB,
	}
	
	CircleCursorMode mode;
	double xRadius;
	double yRadius;
	CircleCursorShape shape;
	
	final Knob ulKnob;
	final Knob brKnob;
	
	Knob knob;
	
	public CircleCursor() {
		mode = CircleCursorMode.FREE;
		yRadius = Vertex.INIT_VERTEX_RADIUS;
		xRadius = Vertex.INIT_VERTEX_RADIUS;
		
		ulKnob = new Knob() {
			public void drag(Point p) {
				
				Point newPoint = APP.world.getPoint(p);
				
				Dim offset = shape.c1.aabb.dim.multiply(0.5);
				
				Point newPoint2 = new Point(newPoint.x + offset.width, newPoint.y + offset.height);
				
				CircleCursor.this.setPoint(newPoint2);
			}
		};
		
		brKnob = new Knob() {
			public void drag(Point p) {
				
				Point newPoint = APP.world.getPoint(p);
				
				Point diff = new Point(newPoint.x - this.p.x, newPoint.y - this.p.y);
				
				xRadius += diff.x/2;
				yRadius += diff.y/2;
				
				Point newPoint1 = CircleCursor.this.p.plus(diff.multiply(0.5));
				
				CircleCursor.this.setPoint(newPoint1);
			}
		};
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new CircleCursorShape(p, xRadius, yRadius);
			ulKnob.setPoint(shape.c1.aabb.ul);
			brKnob.setPoint(shape.c1.aabb.br);
		} else {
			shape = null;
		}	
	}
	
	public void setXRadius(double r) {
		this.xRadius = r;
		
		shape = new CircleCursorShape(p, xRadius, yRadius);
	}
	
	public void setYRadius(double r) {
		this.yRadius = r;
		
		shape = new CircleCursorShape(p, xRadius, yRadius);
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
			APP.world.cursor = new RegularCursor();
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case SET:
			mode = CircleCursorMode.FREE;
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void aKey() {
		switch (mode) {
		case FREE:
			mode = CircleCursorMode.SET;
			VIEW.repaintCanvas();
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke();
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			s.processNewStroke();
			
			APP.world.cursor = new RegularCursor();
			
			APP.world.cursor.setPoint(APP.world.lastMovedWorldPoint);
			
			APP.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case KNOB:
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			Entity closest = APP.world.hitTest(APP.world.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				APP.world.hilited = closest;
			}
			
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			
			VIEW.repaintCanvas();
			break;
		case SET:
		case KNOB:
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(APP.world.lastDraggedWorldPoint);
			break;
		case SET:
			if (!APP.world.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(ulKnob.hitTest(APP.world.lastPressedWorldPoint) ||
					brKnob.hitTest(APP.world.lastPressedWorldPoint))) {
				break;
			}
			if (ulKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = CircleCursorMode.KNOB;
				knob = ulKnob;
				origKnobCenter = knob.p;
			} else if (brKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = CircleCursorMode.KNOB;
				knob = brKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			
			Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
			
			knob.drag(origKnobCenter.plus(diff));
			
			VIEW.repaintCanvas();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
		case SET:
			break;
		case KNOB:	
			mode = CircleCursorMode.SET;
			VIEW.repaintCanvas();
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
		ctxt.setWorldPixelStroke(1);
		
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
		
	}

}
