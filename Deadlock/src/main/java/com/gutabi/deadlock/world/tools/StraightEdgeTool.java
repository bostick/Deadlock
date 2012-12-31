package com.gutabi.deadlock.world.tools;

import java.awt.Color;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
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
	
	public void escKey() {
		switch (mode) {
		case FREE:
			screen.tool = new RegularTool(screen);
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.contentPane.worldPanel.lastMovedOrDraggedWorldPoint));
			screen.contentPane.repaint();
			break;
		case SET:
			mode = StraightEdgeToolMode.FREE;
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.contentPane.worldPanel.lastMovedOrDraggedWorldPoint));
			screen.contentPane.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void qKey() {
		switch (mode) {
		case FREE:
			mode = StraightEdgeToolMode.SET;
			screen.contentPane.repaint();
			break;
		case SET:
			
			Stroke s = new Stroke(screen);
			s.add(start);
			s.add(p);
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke();
			screen.world.graph.computeVertexRadii(affected);
			
			screen.tool = new RegularTool(screen);
			
			screen.tool.setPoint(screen.contentPane.worldPanel.lastMovedOrDraggedWorldPoint);
			
			screen.world.render_worldPanel();
			screen.world.render_preview();
			screen.contentPane.repaint();
//			screen.controlPanel.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.contentPane.worldPanel.lastMovedOrDraggedWorldPoint));
			screen.contentPane.repaint();
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
			screen.contentPane.repaint();
			break;
		}
	}
	
	Point origKnobCenter;
	
	@SuppressWarnings("fallthrough")
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(screen.contentPane.worldPanel.lastDraggedWorldPoint);
			break;
		case SET:
			if (!screen.contentPane.worldPanel.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(startKnob.hitTest(screen.contentPane.worldPanel.lastPressedWorldPoint) ||
					endKnob.hitTest(screen.contentPane.worldPanel.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(screen.contentPane.worldPanel.lastPressedWorldPoint)) {
				mode = StraightEdgeToolMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(screen.contentPane.worldPanel.lastPressedWorldPoint)) {
				mode = StraightEdgeToolMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(screen.contentPane.worldPanel.lastDraggedWorldPoint.x - screen.contentPane.worldPanel.lastPressedWorldPoint.x, screen.contentPane.worldPanel.lastDraggedWorldPoint.y - screen.contentPane.worldPanel.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			screen.contentPane.repaint();
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
		ctxt.setPixelStroke(1.0);
		
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
		
		ctxt.setPaintMode();
	}
}
