package com.gutabi.capsloc.world.tools;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.Set;

import com.gutabi.capsloc.geom.Shape;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.InputEvent;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.Stroke;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.graph.Vertex;

public class StraightEdgeTool extends WorldToolBase {
	
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
	
	public StraightEdgeTool() {
		
		mode = StraightEdgeToolMode.FREE;
		
		startKnob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				StraightEdgeTool.this.setStart(newPoint);
			}
		};
		
		endKnob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				StraightEdgeTool.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		World world = (World)APP.model;
		
		if (p != null) {
			shape = new StraightEdgeToolShape(world, start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		} else {
			shape = null;
		}
		
	}
	
	public void setStart(Point start) {
		this.start = start;
		
		World world = (World)APP.model;
		
		if (start != null && p != null) {
			shape = new StraightEdgeToolShape(world, start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		
		World world = (World)APP.model;
		
		if (start != null && p != null) {
			shape = new StraightEdgeToolShape(world, start, p);
			startKnob.setPoint(start);
			endKnob.setPoint(p);
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey() {
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			APP.tool = new RegularTool();
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			break;
		case SET:
			mode = StraightEdgeToolMode.FREE;
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void qKey() {
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			mode = StraightEdgeToolMode.SET;
			break;
		case SET:
			
			Stroke s = new Stroke(world);
			s.add(start);
			s.add(p);
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke(false);
			world.graph.computeVertexRadii(affected);
			
			APP.tool = new RegularTool();
			
			APP.tool.setPoint(world.lastMovedOrDraggedWorldPoint);
			
			world.render_worldPanel();
			world.render_preview();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ignore) {
		super.moved(ignore);
		
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			break;
		case SET:
		case KNOB:
			break;
		}
	}
	
	public void released(InputEvent ignore) {
		super.released(ignore);
		
		switch (mode) {
		case FREE:
			break;
		case SET:
			break;
		case KNOB:
			mode = StraightEdgeToolMode.SET;
			break;
		}
	}
	
	Point origKnobCenter;
	
	public void dragged(InputEvent ignore) {
		super.dragged(ignore);
		
		World world = (World)APP.model;
		
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
				mode = StraightEdgeToolMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = StraightEdgeToolMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(world.lastDraggedWorldPoint.x - world.lastPressedWorldPoint.x, world.lastDraggedWorldPoint.y - world.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			break;
		}
	}
	
	public void paint_panel(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		ctxt.pushTransform();
		
		ctxt.scale(world.worldCamera.pixelsPerMeter);
		ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
		
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
		
		ctxt.popTransform();
		
		ctxt.clearXORMode();
	}
}
