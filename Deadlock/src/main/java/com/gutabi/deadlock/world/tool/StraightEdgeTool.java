package com.gutabi.deadlock.world.tool;

import java.awt.Color;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

public class StraightEdgeTool extends ToolBase {
	
	enum StraightEdgeToolMode {
		FREE,
		SET,
		KNOB,
	}
	
	StraightEdgeToolMode mode;
	
	public Point start;
	
	StraightEdgeToolShape shape;
	
	final Knob startKnob;
	final Knob endKnob;
	
	Knob knob;
	
	public StraightEdgeTool(WorldScreen screen) {
		super(screen);
		
		mode = StraightEdgeToolMode.FREE;
		
		startKnob = new Knob(screen.cam, screen.world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				StraightEdgeTool.this.setStart(newPoint);
			}
		};
		
		endKnob = new Knob(screen.cam, screen.world) {
			public void drag(Point p) {
				Point newPoint = world.quadrantMap.getPoint(p);
				StraightEdgeTool.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new StraightEdgeToolShape(screen.cam, screen.world, start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		} else {
			shape = null;
		}
		
	}
	
	public void setStart(Point start) {
		this.start = start;
		if (start != null && p != null) {
			shape = new StraightEdgeToolShape(screen.cam, screen.world, start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		if (start != null && p != null) {
			shape = new StraightEdgeToolShape(screen.cam, screen.world, start, p);
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
			screen.tool = new RegularTool(screen);
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaintCanvas();
			break;
		case SET:
			mode = StraightEdgeToolMode.FREE;
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaintCanvas();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void qKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			mode = StraightEdgeToolMode.SET;
			screen.repaintCanvas();
			break;
		case SET:
			
			Stroke s = new Stroke(screen.cam, screen.world, screen.world.graph);
			s.add(start);
			s.add(p);
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke();
			screen.world.graph.computeVertexRadii(affected);
			
			screen.tool = new RegularTool(screen);
			
			screen.tool.setPoint(screen.lastMovedOrDraggedWorldPoint);
			
			screen.render();
			screen.repaintCanvas();
			screen.repaintControlPanel();
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
			screen.repaintCanvas();
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
			mode = StraightEdgeToolMode.SET;
			screen.repaintCanvas();
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
					endKnob.hitTest(screen.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = StraightEdgeToolMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = StraightEdgeToolMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(screen.lastDraggedWorldPoint.x - screen.lastPressedWorldPoint.x, screen.lastDraggedWorldPoint.y - screen.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			screen.repaintCanvas();
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
		ctxt.setPixelStroke(screen.cam.pixelsPerMeter, 1);
		
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
