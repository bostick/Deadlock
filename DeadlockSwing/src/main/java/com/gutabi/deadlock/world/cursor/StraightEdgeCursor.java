package com.gutabi.deadlock.world.cursor;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;

public class StraightEdgeCursor extends CursorBase {
	
	enum StraightEdgeCursorMode {
		FREE,
		SET,
		KNOB,
	}
	
	StraightEdgeCursorMode mode;
	
	public Point start;
	
	StraightEdgeCursorShape shape;
	
	final Knob startKnob;
	final Knob endKnob;
	
	Knob knob;
	
	public StraightEdgeCursor(World world) {
		super(world);
		
		mode = StraightEdgeCursorMode.FREE;
		
		startKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				StraightEdgeCursor.this.setStart(newPoint);
			}
		};
		
		endKnob = new Knob(world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				StraightEdgeCursor.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new StraightEdgeCursorShape(start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		} else {
			shape = null;
		}
		
	}
	
	public void setStart(Point start) {
		this.start = start;
		if (start != null && p != null) {
			shape = new StraightEdgeCursorShape(start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		if (start != null && p != null) {
			shape = new StraightEdgeCursorShape(start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			world.cursor = new RegularCursor(world);
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
			break;
		case SET:
			mode = StraightEdgeCursorMode.FREE;
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			world.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void qKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			mode = StraightEdgeCursorMode.SET;
			world.repaint();
			break;
		case SET:
			
			Stroke s = new Stroke(world);
			s.add(start);
			s.add(p);
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
			mode = StraightEdgeCursorMode.SET;
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
					endKnob.hitTest(world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = StraightEdgeCursorMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = StraightEdgeCursorMode.KNOB;
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
			endKnob.draw(ctxt);
			break;
		}
		
	}
}
