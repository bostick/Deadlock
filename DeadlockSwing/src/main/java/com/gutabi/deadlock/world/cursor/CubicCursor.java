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

public class CubicCursor extends CursorBase {
	
	enum CubicCursorMode {
		FREE,
		SET,
		KNOB,
	}
	
	CubicCursorMode mode;
	
	public Point start;
	public Point c0;
	public Point c1;
	
	CubicCursorShape shape;
	
	final Knob startKnob;
	final Knob control0Knob;
	final Knob control1Knob;
	final Knob endKnob;
	
	Knob knob;
	
	public CubicCursor() {
		mode = CubicCursorMode.FREE;
		
		startKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				CubicCursor.this.setStart(newPoint);
			}
		};
		
		control0Knob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				CubicCursor.this.setControl0(newPoint);
			}
		};
		
		control1Knob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				CubicCursor.this.setControl1(newPoint);
			}
		};
		
		endKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = APP.world.getPoint(p);
				CubicCursor.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			Point middle = start.plus(p.minus(start).multiply(0.5));
			c0 = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
			c1 = middle.plus(new Point(0, 4 * Vertex.INIT_VERTEX_RADIUS));
			shape = new CubicCursorShape(start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		} else {
			shape = null;
		}
		
	}
	
	public void setStart(Point start) {
		this.start = start;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicCursorShape(start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl0(Point c) {
		this.c0 = c;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicCursorShape(start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl1(Point c) {
		this.c1 = c;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicCursorShape(start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicCursorShape(start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
//	public AABB startKnob() {
//		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
//		AABB rect = new AABB(start.x + -2 * pixel, start.y + -2 * pixel, 5 * pixel, 5 * pixel);
//		return rect;
//	}
//	
//	public AABB controlKnob() {
//		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
//		AABB rect = new AABB(c.x + -2 * pixel, c.y + -2 * pixel, 5 * pixel, 5 * pixel);
//		return rect;
//	}
//	
//	public AABB endKnob() {
//		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
//		AABB rect = new AABB(p.x + -2 * pixel, p.y + -2 * pixel, 5 * pixel, 5 * pixel);
//		return rect;
//	}
	
//	public void setStartKnob(Point startCenter) {
//		start = startCenter;
//		shape = new QuadCursorShape(start, c, p);	
//	}
//	
//	public void setControlKnob(Point controlCenter) {
//		c = controlCenter;
//		shape = new QuadCursorShape(start, c, p);	
//	}
//	
//	public void setEndKnob(Point endCenter) {
//		p = endCenter;
//		shape = new QuadCursorShape(start, c, p);	
//	}
	
	public void escKey() {
		switch (mode) {
		case FREE:
			APP.world.cursor = new RegularCursor();
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case SET:
			mode = CubicCursorMode.FREE;
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			VIEW.repaintCanvas();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void dKey() {
		switch (mode) {
		case FREE:
			mode = CubicCursorMode.SET;
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
			mode = CubicCursorMode.SET;
			VIEW.repaintCanvas();
			break;
		}
	}
	
//	Point lastDragCursorPoint;
	Point origKnobCenter;
//	Point origControlKnobCenter;
//	Point origEndKnobCenter;
	
	public void dragged(InputEvent ev) {
//		Point p = ev.p;
		
//		boolean lastDragCursorPointWasNull = (lastDragCursorPoint == null);
//		lastDragCursorPoint = APP.world.canvasToWorld(p);
//		lastMovedOrDraggedCursorPoint = lastDragCursorPoint;
		
		switch (mode) {
		case FREE:
			setPoint(APP.world.lastDraggedWorldPoint);
			break;
		case SET:
			if (!APP.world.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(startKnob.hitTest(APP.world.lastPressedWorldPoint) ||
					control0Knob.hitTest(APP.world.lastPressedWorldPoint) ||
					control1Knob.hitTest(APP.world.lastPressedWorldPoint) ||
					endKnob.hitTest(APP.world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (control0Knob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
				knob = control0Knob;
				origKnobCenter = knob.p;
			} else if (control1Knob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
				knob = control1Knob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(APP.world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
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
	
//	Point lastMovedCursorPoint;
//	Point lastMovedOrDraggedCursorPoint;
	
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
			control0Knob.draw(ctxt);
			control1Knob.draw(ctxt);
			endKnob.draw(ctxt);
			break;
		}
		
	}
}
