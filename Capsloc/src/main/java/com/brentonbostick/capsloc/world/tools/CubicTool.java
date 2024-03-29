package com.brentonbostick.capsloc.world.tools;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.List;
import java.util.Set;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.InputEvent;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.Stroke;
import com.brentonbostick.capsloc.world.World;
import com.brentonbostick.capsloc.world.graph.Vertex;

public class CubicTool extends WorldToolBase {
	
	enum CubicToolMode {
		FREE,
		SET,
		KNOB,
	}
	
	CubicToolMode mode;
	
	public Point start;
	public Point c0;
	public Point c1;
	
	CubicToolShape shape;
	
	final Knob startKnob;
	final Knob control0Knob;
	final Knob control1Knob;
	final Knob endKnob;
	
	Knob knob;
	
	public CubicTool() {
		
		mode = CubicToolMode.FREE;
		
		startKnob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicTool.this.setStart(newPoint);
			}
		};
		
		control0Knob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicTool.this.setControl0(newPoint);
			}
		};
		
		control1Knob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicTool.this.setControl1(newPoint);
			}
		};
		
		endKnob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				CubicTool.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		World world = (World)APP.model;
		
		if (p != null) {
			Point middle = start.plus(p.minus(start).multiply(0.5));
			c0 = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
			c1 = middle.plus(new Point(0, 4 * Vertex.INIT_VERTEX_RADIUS));
			shape = new CubicToolShape(world, start, c0, c1, p);
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
		
		World world = (World)APP.model;
		
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl0(Point c) {
		this.c0 = c;
		
		World world = (World)APP.model;
		
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl1(Point c) {
		this.c1 = c;
		
		World world = (World)APP.model;
		
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		
		World world = (World)APP.model;
		
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public Object getShape() {
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
			mode = CubicToolMode.FREE;
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void dKey() {
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			mode = CubicToolMode.SET;
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke(world);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke(false);
			world.graph.computeVertexRadii(affected);
			
			APP.tool = new RegularTool();
			
			APP.tool.setPoint(world.lastMovedOrDraggedWorldPoint);
			
			world.render();
//			world.render_preview();
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
					control0Knob.hitTest(world.lastPressedWorldPoint) ||
					control1Knob.hitTest(world.lastPressedWorldPoint) ||
					endKnob.hitTest(world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (control0Knob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = control0Knob;
				origKnobCenter = knob.p;
			} else if (control1Knob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = control1Knob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
			//$FALL-THROUGH$
		case KNOB:
			Point diff = new Point(world.lastDraggedWorldPoint.x - world.lastPressedWorldPoint.x, world.lastDraggedWorldPoint.y - world.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
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
			mode = CubicToolMode.SET;
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
		
		ctxt.scale(world.worldCamera.pixelsPerMeter, world.worldCamera.pixelsPerMeter);
		ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
		
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
		
		ctxt.popTransform();
		
		ctxt.clearXORMode();
	}
}
