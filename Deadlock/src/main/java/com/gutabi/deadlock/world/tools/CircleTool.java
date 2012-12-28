package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleTool extends ToolBase {
	
	enum CircleToolMode {
		FREE,
		SET,
		KNOB,
	}
	
	CircleToolMode mode;
	double xRadius;
	double yRadius;
	CircleToolShape shape;
	
	final Knob ulKnob;
	final Knob brKnob;
	
	Knob knob;
	
	public CircleTool(WorldScreen screen) {
		super(screen);
		
		mode = CircleToolMode.FREE;
		yRadius = Vertex.INIT_VERTEX_RADIUS;
		xRadius = Vertex.INIT_VERTEX_RADIUS;
		
		ulKnob = new Knob(screen.cam, screen.world) {
			public void drag(Point p) {
				
				Point newPoint = world.quadrantMap.getPoint(p);
				
				Dim offset = shape.c1.aabb.dim.multiply(0.5);
				
				Point newPoint2 = new Point(newPoint.x + offset.width, newPoint.y + offset.height);
				
				CircleTool.this.setPoint(newPoint2);
			}
		};
		
		brKnob = new Knob(screen.cam, screen.world) {
			public void drag(Point p) {
				
				Point newPoint = world.quadrantMap.getPoint(p);
				
				Point diff = new Point(newPoint.x - this.p.x, newPoint.y - this.p.y);
				
				xRadius += diff.x/2;
				yRadius += diff.y/2;
				
				Point newPoint1 = CircleTool.this.p.plus(diff.multiply(0.5));
				
				CircleTool.this.setPoint(newPoint1);
			}
		};
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new CircleToolShape(p, xRadius, yRadius);
			ulKnob.setPoint(shape.c1.aabb.ul);
			brKnob.setPoint(shape.c1.aabb.br);
		} else {
			shape = null;
		}	
	}
	
	public void setXRadius(double r) {
		this.xRadius = r;
		
		shape = new CircleToolShape(p, xRadius, yRadius);
	}
	
	public void setYRadius(double r) {
		this.yRadius = r;
		
		shape = new CircleToolShape(p, xRadius, yRadius);
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Point getBR() {
		return shape.c1.aabb.br;
	}
	
	public void escKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			screen.tool = new RegularTool(screen);
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaintCanvas();
			break;
		case SET:
			mode = CircleToolMode.FREE;
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			screen.repaintCanvas();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void aKey(InputEvent ev) {
		switch (mode) {
		case FREE:
			mode = CircleToolMode.SET;
			screen.repaintCanvas();
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke(screen);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke();
			screen.world.graph.computeVertexRadii(affected);
			
			screen.tool = new RegularTool(screen);
			
			screen.tool.setPoint(screen.lastMovedWorldPoint);
			
			screen.render();
			screen.repaintCanvas();
			screen.repaintControlPanel();
			break;
		case KNOB:
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			Entity closest = screen.world.hitTest(screen.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				screen.hilited = closest;
			}
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			
			screen.repaintCanvas();
			break;
		case SET:
		case KNOB:
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		
		switch (mode) {
		case FREE:
			setPoint(screen.lastDraggedWorldPoint);
			break;
		case SET:
			if (!screen.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(ulKnob.hitTest(screen.lastPressedWorldPoint) ||
					brKnob.hitTest(screen.lastPressedWorldPoint))) {
				break;
			}
			if (ulKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = CircleToolMode.KNOB;
				knob = ulKnob;
				origKnobCenter = knob.p;
			} else if (brKnob.hitTest(screen.lastPressedWorldPoint)) {
				mode = CircleToolMode.KNOB;
				knob = brKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			
			Point diff = new Point(screen.lastDraggedWorldPoint.x - screen.lastPressedWorldPoint.x, screen.lastDraggedWorldPoint.y - screen.lastPressedWorldPoint.y);
			
			knob.drag(origKnobCenter.plus(diff));
			
			screen.repaintCanvas();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
		case SET:
			break;
		case KNOB:	
			mode = CircleToolMode.SET;
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
	
	Point origKnobCenter;
	
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
			ulKnob.draw(ctxt);
			brKnob.draw(ctxt);
			break;
		}
		
	}

}
