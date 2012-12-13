package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.List;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.graph.Vertex;

public class QuadCursor extends CursorBase {
	
	enum QuadCursorMode {
		FREE,
		SET,
		KNOB,
	}
	
	QuadCursorMode mode;
	
	public Point start;
	public Point c;
	
	QuadCursorShape shape;
	
	final Knob startKnob;
	final Knob controlKnob;
	final Knob endKnob;
	
	Knob knob;
	
	public QuadCursor() {
		mode = QuadCursorMode.FREE;
		
		startKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				QuadCursor.this.setStart(newPoint);
			}
		};
		
		controlKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				QuadCursor.this.setControl(newPoint);
			}
		};
		
		endKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				QuadCursor.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			Point middle = start.plus(p.minus(start).multiply(0.5));
			c = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
			shape = new QuadCursorShape(start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		} else {
			shape = null;
		}
		
	}
	
	public void setStart(Point start) {
		this.start = start;
		if (start != null && p != null && c != null) {
			shape = new QuadCursorShape(start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl(Point c) {
		this.c = c;
		if (start != null && p != null && c != null) {
			shape = new QuadCursorShape(start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		if (start != null && p != null && c != null) {
			shape = new QuadCursorShape(start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey() {
		switch (mode) {
		case FREE:
			APP.world.cursor = new RegularCursor();
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case SET:
			mode = QuadCursorMode.FREE;
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void sKey() {
		switch (mode) {
		case FREE:
			mode = QuadCursorMode.SET;
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
			
			APP.world.cursor.setPoint(APP.world.lastMovedOrDraggedWorldPoint);
			
			APP.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case SET:
		case KNOB:
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case SET:
			break;
		case KNOB:
			mode = QuadCursorMode.SET;
			VIEW.repaintCanvas();
			break;
		}
	}
	
	Point origKnobCenter;
	
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(APP.world.lastDraggedWorldPoint);
			break;
		case SET:
			if (!APP.world.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(startKnob.hitTest(APP.world.lastPressedWorldPoint) ||
					controlKnob.hitTest(APP.world.lastPressedWorldPoint) ||
					endKnob.hitTest(APP.world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = QuadCursorMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (controlKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = QuadCursorMode.KNOB;
				knob = controlKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = QuadCursorMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			VIEW.repaintCanvas();
			break;
		}
	}
	
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
			startKnob.draw(ctxt);
			controlKnob.draw(ctxt);
			endKnob.draw(ctxt);
			break;
		}
		
	}
}
