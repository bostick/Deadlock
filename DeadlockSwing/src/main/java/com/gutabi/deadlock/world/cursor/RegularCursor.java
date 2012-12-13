package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.graph.Direction;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class RegularCursor extends CursorBase {
	
	enum RegularCursorMode {
		FREE,
		DRAFTING
	}
	
	RegularCursorMode mode;
	
	Circle shape;
	
	Stroke stroke;
	Stroke debugStroke;
	Stroke debugStroke2;
	
	public RegularCursor() {
		mode = RegularCursorMode.FREE;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void d1Key() {
		if (APP.world.hilited != null) {
			
			if (APP.world.hilited instanceof Road) {
				Road r = (Road)APP.world.hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				APP.render();
				VIEW.repaintCanvas();
				
			} else if (APP.world.hilited instanceof Fixture) {
				Fixture f = (Fixture)APP.world.hilited;
				
				Fixture g = f.match;
				
				if (f.getType() != null) {
					f.setType(f.getType().other());
				}
				if (g.getType() != null) {
					g.setType(g.getType().other());
				}
				
				f.setSide(f.getSide().other());
				g.setSide(g.getSide().other());
				
				APP.render();
				VIEW.repaintCanvas();
				
			}
			
		}
	}
	
	public void d2Key() {
		if (APP.world.hilited != null) {
			
			if (APP.world.hilited instanceof Road) {
				Road r = (Road)APP.world.hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				APP.render();
				VIEW.repaintCanvas();
				
			}
			
		}
	}
	
	public void d3Key() {
		if (APP.world.hilited != null) {
			
			if (APP.world.hilited instanceof Road) {
				Road r = (Road)APP.world.hilited;
				
				r.setDirection(null, null);
			
				APP.render();
				VIEW.repaintCanvas();
				
			}
			
		}
	}

	
	public void qKey() {
		APP.world.cursor = new StraightEdgeCursor(APP.world.lastMovedOrDraggedWorldPoint);
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		VIEW.repaintCanvas();
	}
	
	public void wKey() {
		APP.world.cursor = new FixtureCursor();
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		VIEW.repaintCanvas();
	}
	
	public void aKey() {
		
		APP.world.hilited = null;
		
		APP.world.cursor = new CircleCursor();
		
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		
		VIEW.repaintCanvas();
	}
	
	public void sKey() {
		QuadCursor q = new QuadCursor();
		q.setStart(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		q.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
//		Point middle = q.start.plus(q.p.minus(q.start).multiply(0.5));
//		Point c = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
//		q.setControl(c);
		APP.world.cursor = q;
		VIEW.repaintCanvas();
	}
	
	public void dKey() {
		CubicCursor c = new CubicCursor();
		c.setStart(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		c.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
//		Point middle = c.start.plus(c.p.minus(c.start).multiply(0.5));
//		Point control = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
//		c.setControl(control);
		APP.world.cursor = c;
		VIEW.repaintCanvas();
	}
	
	public void insertKey() {
		if (APP.world.hilited != null) {
			
			if (APP.world.hilited instanceof StopSign) {
				StopSign s = (StopSign)APP.world.hilited;
				
				s.setEnabled(true);
				
				APP.render();
				VIEW.repaintCanvas();
			}
			
		} else {
			
			APP.world.hilited = null;
			
			APP.world.cursor = new MergerCursor();
			
			APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
			
			VIEW.repaintCanvas();
		}
	}
	
	public void plusKey() {
		Entity closest = APP.world.hitTest(APP.world.lastMovedOrDraggedWorldPoint);
		
		synchronized (APP) {
			APP.world.hilited = closest;
		}
		
		APP.world.map.computeGridSpacing();
		
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		
		APP.render();
		VIEW.repaintCanvas();
		VIEW.repaintControlPanel();
	}
	
	public void minusKey() {
		Entity closest = APP.world.hitTest(APP.world.lastMovedOrDraggedWorldPoint);
		
		synchronized (APP) {
			APP.world.hilited = closest;
		}
		
		APP.world.map.computeGridSpacing();
		
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		
		APP.render();
		VIEW.repaintCanvas();
		VIEW.repaintControlPanel();
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
		case DRAFTING:
			assert false;
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		switch (mode) {
		case FREE:
			APP.world.cursor.setPoint(APP.world.lastDraggedWorldPoint);
			
			if (APP.world.lastDraggedWorldPointWasNull) {
				// first drag
				draftStart(APP.world.lastPressedWorldPoint);
				draftMove(APP.world.lastDraggedWorldPoint);
				
				VIEW.repaintCanvas();
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			APP.world.cursor.setPoint(APP.world.lastDraggedWorldPoint);
			
			draftMove(APP.world.lastDraggedWorldPoint);
			
			VIEW.repaintCanvas();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			APP.render();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			break;
		}
	}
	
	public void exited() {
		APP.world.cursor.setPoint(null);
		VIEW.repaintCanvas();
	}
	
	public void draftStart(Point p) {
		
		mode = RegularCursorMode.DRAFTING;
		
		APP.world.hilited = null;
		
		stroke = new Stroke();
		stroke.add(p);
			
	}
	
	public void draftMove(Point p) {

		stroke.add(p);
	}
	
	public void draftEnd() {
		
		stroke.finish();
		
		stroke.processNewStroke();
		
		assert APP.world.checkConsistency();
		
		debugStroke2 = debugStroke;
		debugStroke = stroke;
		stroke = null;
		
		mode = RegularCursorMode.FREE;
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		if (stroke != null) {
			stroke.paint(ctxt);
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setWorldPixelStroke(1);
		
		shape.draw(ctxt);
		
	}
	
}
