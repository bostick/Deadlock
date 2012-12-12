package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.List;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldMode;
import com.gutabi.deadlock.world.graph.Vertex;

public class QuadCursor extends CursorBase {
	
	enum QuadCursorMode {
		FREE,
		SET,
		START,
		CONTROL,
		END,
	}
	
	QuadCursorMode mode;
	
	public Point start;
	public Point c;
	
	QuadCursorShape shape;
	
	public QuadCursor() {
		mode = QuadCursorMode.FREE;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			Point middle = start.plus(p.minus(start).multiply(0.5));
			c = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
			shape = new QuadCursorShape(start, c, p);
		} else {
			shape = null;
		}
		
	}
	
	public void setStart(Point start) {
		this.start = start;
		if (start != null && p != null && c != null) {
			shape = new QuadCursorShape(start, c, p);
		}		
	}
	
	public void setControl(Point c) {
		this.c = c;
		if (start != null && p != null && c != null) {
			shape = new QuadCursorShape(start, c, p);
		}
	}
	public Shape getShape() {
		return shape;
	}
	
	public AABB startKnob() {
		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
		AABB rect = new AABB(start.x + -2 * pixel, start.y + -2 * pixel, 5 * pixel, 5 * pixel);
		return rect;
	}
	
	public AABB controlKnob() {
		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
		AABB rect = new AABB(c.x + -2 * pixel, c.y + -2 * pixel, 5 * pixel, 5 * pixel);
		return rect;
	}
	
	public AABB endKnob() {
		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
		AABB rect = new AABB(p.x + -2 * pixel, p.y + -2 * pixel, 5 * pixel, 5 * pixel);
		return rect;
	}
	
	public void setStartKnob(Point startCenter) {
		start = startCenter;
		shape = new QuadCursorShape(start, c, p);	
	}
	
	public void setControlKnob(Point controlCenter) {
		c = controlCenter;
		shape = new QuadCursorShape(start, c, p);	
	}
	
	public void setEndKnob(Point endCenter) {
		p = endCenter;
		shape = new QuadCursorShape(start, c, p);	
	}
	
	public void escKey() {
		switch (mode) {
		case FREE:
			APP.world.mode = WorldMode.REGULAR;
			APP.world.cursor = new RegularCursor();
			APP.world.cursor.setPoint(APP.world.lastMovedOrDraggedWorldPoint);
			VIEW.repaintCanvas();
			break;
		case SET:
			mode = QuadCursorMode.FREE;
			APP.world.map.setCursorPoint(this, APP.world.lastMovedOrDraggedWorldPoint);
			VIEW.repaintCanvas();
			break;
		case START:
		case CONTROL:
		case END:
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
			
			APP.world.processNewStroke(s);
			
			APP.world.mode = WorldMode.REGULAR;
			
			APP.world.cursor = new RegularCursor();
			
			APP.world.cursor.setPoint(APP.world.lastMovedOrDraggedWorldPoint);
			
			APP.world.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case START:
		case CONTROL:
		case END:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			APP.world.map.setCursorPoint(this, APP.world.lastMovedOrDraggedWorldPoint);
			VIEW.repaintCanvas();
			break;
		case SET:
		case START:
		case CONTROL:
		case END:
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case SET:
			break;
		case START:
		case CONTROL:
		case END:
			mode = QuadCursorMode.SET;
			VIEW.repaintCanvas();
			break;
		}
	}
	
//	Point lastDragCursorPoint;
	Point origStartKnobCenter;
	Point origControlKnobCenter;
	Point origEndKnobCenter;
	
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
			
			if (APP.world.lastDraggedWorldPointWasNull) {
				
				AABB startKnob = startKnob();
				AABB controlKnob = controlKnob();
				AABB endKnob = endKnob();
				
				if (startKnob.hitTest(APP.world.lastPressedWorldPoint)) {
					
					mode = QuadCursorMode.START;
					
					Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
					
					origStartKnobCenter = startKnob.center;
					
					setStartKnob(origStartKnobCenter.plus(diff));
					
				} else if (controlKnob.hitTest(APP.world.lastPressedWorldPoint)) {
					
					mode = QuadCursorMode.CONTROL;
					
					Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
					
					origControlKnobCenter = controlKnob.center;
					
					setControlKnob(origControlKnobCenter.plus(diff));
					
				} else if (endKnob.hitTest(APP.world.lastPressedWorldPoint)) {
					
					mode = QuadCursorMode.END;
					
					Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
					
					origEndKnobCenter = endKnob.center;
					
					setEndKnob(origEndKnobCenter.plus(diff));
					
				}
				
				VIEW.repaintCanvas();
				
			}
			
			break;
		case START: {
			Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
			setStartKnob(origStartKnobCenter.plus(diff));
			VIEW.repaintCanvas();
			break;
		}
		case CONTROL: {
			Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
			setControlKnob(origControlKnobCenter.plus(diff));
			VIEW.repaintCanvas();
			break;
		}
		case END: {
			Point diff = new Point(APP.world.lastDraggedWorldPoint.x - APP.world.lastPressedWorldPoint.x, APP.world.lastDraggedWorldPoint.y - APP.world.lastPressedWorldPoint.y);
			setEndKnob(origEndKnobCenter.plus(diff));
			VIEW.repaintCanvas();
			break;
		}
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
		case START:
		case CONTROL:
		case END:
			AABB startKnob = startKnob();
			startKnob.draw(ctxt);
			AABB controlKnob = controlKnob();
			controlKnob.draw(ctxt);
			AABB endKnob = endKnob();
			endKnob.draw(ctxt);
			break;
		}
		
	}
}
