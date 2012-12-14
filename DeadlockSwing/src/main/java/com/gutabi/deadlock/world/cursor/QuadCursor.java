package com.gutabi.deadlock.world.cursor;

import java.awt.Color;
import java.util.List;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;
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
	
	public QuadCursor(final World world) {
		super(world);
		
		mode = QuadCursorMode.FREE;
		
		startKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				QuadCursor.this.setStart(newPoint);
			}
		};
		
		controlKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				QuadCursor.this.setControl(newPoint);
			}
		};
		
		endKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
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
			world.cursor = new RegularCursor(world);
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
			break;
		case SET:
			mode = QuadCursorMode.FREE;
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
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
			world.repaint();
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke(world);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			s.processNewStroke();
			
			world.cursor = new RegularCursor(world);
			
			world.cursor.setPoint(world.lastMovedOrDraggedWorldPoint);
			
			world.render();
			world.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
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
			world.repaint();
			break;
		}
	}
	
	Point origKnobCenter;
	
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(world.lastDraggedWorldPoint);
			break;
		case SET:
			if (!world.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(startKnob.hitTest(world.lastPressedWorldPoint) ||
					controlKnob.hitTest(world.lastPressedWorldPoint) ||
					endKnob.hitTest(world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = QuadCursorMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (controlKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = QuadCursorMode.KNOB;
				knob = controlKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = QuadCursorMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(world.lastDraggedWorldPoint.x - world.lastPressedWorldPoint.x, world.lastDraggedWorldPoint.y - world.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			world.repaint();
			break;
		}
	}
	
	public void exited(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case SET:
			break;
		case KNOB:
			break;
		}
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		
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
