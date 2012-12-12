package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.List;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldMode;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleCursor extends Cursor {
	
	enum CircleCursorMode {
		
		FREE,
		SET,
		UL,
		BR
		
	}
	
	CircleCursorMode mode;
	double xRadius;
	double yRadius;
	CircleCursorShape shape;
	
	public CircleCursor() {
		mode = CircleCursorMode.FREE;
		yRadius = Vertex.INIT_VERTEX_RADIUS;
		xRadius = Vertex.INIT_VERTEX_RADIUS;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new CircleCursorShape(p, xRadius, yRadius);
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
	
	public AABB ulKnob() {
		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
		AABB rect = new AABB(shape.c1.aabb.ul.x + -2 * pixel, shape.c1.aabb.ul.y + -2 * pixel, 5 * pixel, 5 * pixel);
		return rect;
	}
	
	public void setULKnob(Point ulCenter) {
		
		Dim offset = shape.c1.aabb.dim.multiply(0.5);
		
		Point newPoint = new Point(ulCenter.x + offset.width, ulCenter.y + offset.height);
		
		setPoint(newPoint);
		
	}
	
	public void setBRKnob(Point brCenter) {
		
		AABB brKnob = brKnob();
		
		Point diff = new Point(brCenter.x - brKnob.center.x, brCenter.y - brKnob.center.y);
		
		xRadius += diff.x/2;
		yRadius += diff.y/2;
		p = p.plus(diff.multiply(0.5));
		
		shape = new CircleCursorShape(p, xRadius, yRadius);
		
	}
	
	public AABB brKnob() {
		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
		AABB rect = new AABB(shape.c1.aabb.br.x + -2 * pixel, shape.c1.aabb.br.y + -2 * pixel, 5 * pixel, 5 * pixel);
		return rect;
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
			
			APP.world.processNewStroke(s);
			
			APP.world.mode = WorldMode.IDLE;
			
			APP.world.cursor = new RegularCursor();
			
			APP.world.cursor.setPoint(APP.world.lastMovedWorldPoint);
			
			APP.world.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case UL:
		case BR:
			break;
		}
	}
	
	public void escKey() {
		switch (mode) {
		case FREE:
			
			APP.world.mode = WorldMode.IDLE;
			
			APP.world.cursor = new RegularCursor();
			
			APP.world.cursor.setPoint(APP.world.lastMovedWorldPoint);
			
			APP.world.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case SET:
			
			mode = CircleCursorMode.FREE;
			
			setPoint(lastMovedCursorPoint);
			
			VIEW.repaintCanvas();
			break;
		case UL:
		case BR:
			break;
		}
	}
	
	Point lastPressCursorPoint;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressCursorPoint = APP.world.canvasToWorld(p);
		lastDragCursorPoint = null;
		
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case SET:
			break;
		case UL:
			
			mode = CircleCursorMode.SET;
			
			VIEW.repaintCanvas();
			
			break;
		case BR:
			
			mode = CircleCursorMode.SET;
			
			VIEW.repaintCanvas();
			
			break;
		}
	}
	
	Point lastDragCursorPoint;
	Point origULKnobCenter;
	Point origBRKnobCenter;
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		boolean lastDragCursorPointWasNull = (lastDragCursorPoint == null);
		lastDragCursorPoint = APP.world.canvasToWorld(p);
		
		switch (mode) {
		case FREE:
			setPoint(lastDragCursorPoint);
			break;
		case SET:
			
			if (lastDragCursorPointWasNull) {
				
				AABB ulKnob = ulKnob();
				AABB brKnob = brKnob();
				
				if (ulKnob.hitTest(lastPressCursorPoint)) {
					
					mode = CircleCursorMode.UL;
					
					Point diff = new Point(lastDragCursorPoint.x - lastPressCursorPoint.x, lastDragCursorPoint.y - lastPressCursorPoint.y);
					
					origULKnobCenter = ulKnob.center;
					
					setULKnob(origULKnobCenter.plus(diff));
					
				} else if (brKnob.hitTest(lastPressCursorPoint)) {
					
					mode = CircleCursorMode.BR;
					
					Point diff = new Point(lastDragCursorPoint.x - lastPressCursorPoint.x, lastDragCursorPoint.y - lastPressCursorPoint.y);
					
					origBRKnobCenter = brKnob.center;
					
					setBRKnob(origBRKnobCenter.plus(diff));
					
				}
				
				VIEW.repaintCanvas();
				
			}
			
			break;
		case UL: {
			
			Point diff = new Point(lastDragCursorPoint.x - lastPressCursorPoint.x, lastDragCursorPoint.y - lastPressCursorPoint.y);
			
			setULKnob(origULKnobCenter.plus(diff));
			
			VIEW.repaintCanvas();
			break;
		}
		case BR: {
			
			Point diff = new Point(lastDragCursorPoint.x - lastPressCursorPoint.x, lastDragCursorPoint.y - lastPressCursorPoint.y);
			
			setBRKnob(origBRKnobCenter.plus(diff));
			
			VIEW.repaintCanvas();
			break;
		}
		}
	}
	
	Point lastMovedCursorPoint;
	
	public void moved(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedCursorPoint = APP.world.canvasToWorld(p);
		
		switch (mode) {
		case FREE:
			if (APP.world.grid) {
				
				Point closestGridPoint = new Point(2 * Math.round(0.5 * APP.world.lastMovedWorldPoint.x), 2 * Math.round(0.5 * APP.world.lastMovedWorldPoint.y));
				setPoint(closestGridPoint);
				
			} else {
				setPoint(lastMovedCursorPoint);
			}
			
			VIEW.repaintCanvas();
			break;
		case SET:
			break;
		case UL:
		case BR:
			break;
		}
	}
	
	public void entered(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedCursorPoint = APP.world.canvasToWorld(p);
		
		switch (mode) {
		case FREE:
			if (APP.world.grid) {
				
				Point closestGridPoint = new Point(2 * Math.round(0.5 * APP.world.lastMovedWorldPoint.x), 2 * Math.round(0.5 * APP.world.lastMovedWorldPoint.y));
				setPoint(closestGridPoint);
				
			} else {
				setPoint(lastMovedCursorPoint);
			}
			
			VIEW.repaintCanvas();
			break;
		case SET:
			break;
		case UL:
		case BR:
			break;
		}
	}
	
	public void exited(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(null);
			
			VIEW.repaintCanvas();
			break;
		case SET:
			break;
		case UL:
		case BR:
			break;
		}
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.GRAY);
		ctxt.setWorldPixelStroke(1);
		
		shape.draw(ctxt);
		
		if (mode == CircleCursorMode.SET) {
			
			AABB ulKnob = ulKnob();
			AABB brKnob = brKnob();
			
			ctxt.setColor(Color.WHITE);
			ctxt.setWorldPixelStroke(1);
			
			ulKnob.draw(ctxt);
			
			brKnob.draw(ctxt);
			
		}
		
	}

}
