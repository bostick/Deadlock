package com.gutabi.deadlock.world.tool;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

public class QuadTool extends ToolBase {
	
	enum QuadToolMode {
		FREE,
		SET,
		KNOB,
	}
	
	QuadToolMode mode;
	
	public Point start;
	public Point c;
	
	QuadToolShape shape;
	
	final Knob startKnob;
	final Knob controlKnob;
	final Knob endKnob;
	
	Knob knob;
	
	public QuadTool(final WorldScreen screen) {
		super(screen);
		
		mode = QuadToolMode.FREE;
		
		startKnob = new Knob(screen.world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				QuadTool.this.setStart(newPoint);
			}
		};
		
		controlKnob = new Knob(screen.world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				QuadTool.this.setControl(newPoint);
			}
		};
		
		endKnob = new Knob(screen.world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				QuadTool.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			Point middle = start.plus(p.minus(start).multiply(0.5));
			c = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
			shape = new QuadToolShape(screen.world, start, c, p);
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
			shape = new QuadToolShape(screen.world, start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl(Point c) {
		this.c = c;
		if (start != null && p != null && c != null) {
			shape = new QuadToolShape(screen.world, start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		if (start != null && p != null && c != null) {
			shape = new QuadToolShape(screen.world, start, c, p);
			startKnob.setPoint(start);
			controlKnob.setPoint(c);
			endKnob.setPoint(p);
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			screen.tool = new RegularTool(screen);
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaint();
			break;
		case SET:
			mode = QuadToolMode.FREE;
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void sKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			mode = QuadToolMode.SET;
			screen.repaint();
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke(screen.world, screen.world.graph);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke();
			screen.world.graph.computeVertexRadii(affected);
			
			screen.tool = new RegularTool(screen);
			
			screen.tool.setPoint(screen.lastMovedOrDraggedWorldPoint);
			
			screen.render();
			screen.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaint();
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
			mode = QuadToolMode.SET;
			screen.repaint();
			break;
		}
	}
	
	Point origKnobCenter;
	
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(screen.lastDraggedWorldPoint);
			break;
		case SET:
			if (!screen.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(startKnob.hitTest(screen.lastPressedWorldPoint) ||
					controlKnob.hitTest(screen.lastPressedWorldPoint) ||
					endKnob.hitTest(screen.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = QuadToolMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (controlKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = QuadToolMode.KNOB;
				knob = controlKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = QuadToolMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(screen.lastDraggedWorldPoint.x - screen.lastPressedWorldPoint.x, screen.lastDraggedWorldPoint.y - screen.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			screen.repaint();
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
		ctxt.setPixelStroke(screen.world.cam.pixelsPerMeter, 1);
		
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
