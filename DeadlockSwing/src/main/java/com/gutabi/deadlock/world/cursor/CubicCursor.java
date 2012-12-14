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
	
	public CubicCursor(final World world) {
		super(world);
		
		mode = CubicCursorMode.FREE;
		
		startKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicCursor.this.setStart(newPoint);
			}
		};
		
		control0Knob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicCursor.this.setControl0(newPoint);
			}
		};
		
		control1Knob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicCursor.this.setControl1(newPoint);
			}
		};
		
		endKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
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
	
	public void escKey() {
		switch (mode) {
		case FREE:
			world.cursor = new RegularCursor(world);
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
			break;
		case SET:
			mode = CubicCursorMode.FREE;
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
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
					control0Knob.hitTest(world.lastPressedWorldPoint) ||
					control1Knob.hitTest(world.lastPressedWorldPoint) ||
					endKnob.hitTest(world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (control0Knob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
				knob = control0Knob;
				origKnobCenter = knob.p;
			} else if (control1Knob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
				knob = control1Knob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicCursorMode.KNOB;
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
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case SET:
			break;
		case KNOB:
			mode = CubicCursorMode.SET;
			world.repaint();
			break;
		}
	}
	
	public void exited(InputEvent ev) {
		world.cursor.setPoint(null);
		world.repaint();
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
			control0Knob.draw(ctxt);
			control1Knob.draw(ctxt);
			endKnob.draw(ctxt);
			break;
		}
		
	}
}
