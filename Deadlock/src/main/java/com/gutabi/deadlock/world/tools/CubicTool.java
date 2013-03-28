package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

public class CubicTool extends ToolBase {
	
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
	
	public CubicTool(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		super(worldScreen, debuggerScreen);
		
		mode = CubicToolMode.FREE;
		
		startKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = CubicTool.this.worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(p);
				CubicTool.this.setStart(newPoint);
			}
		};
		
		control0Knob = new Knob() {
			public void drag(Point p) {
				Point newPoint = CubicTool.this.worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(p);
				CubicTool.this.setControl0(newPoint);
			}
		};
		
		control1Knob = new Knob() {
			public void drag(Point p) {
				Point newPoint = CubicTool.this.worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(p);
				CubicTool.this.setControl1(newPoint);
			}
		};
		
		endKnob = new Knob() {
			public void drag(Point p) {
				Point newPoint = CubicTool.this.worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(p);
				CubicTool.this.setEnd(newPoint);
			}
		};
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			Point middle = start.plus(p.minus(start).multiply(0.5));
			c0 = middle.plus(new Point(0, -4 * Vertex.INIT_VERTEX_RADIUS));
			c1 = middle.plus(new Point(0, 4 * Vertex.INIT_VERTEX_RADIUS));
			shape = new CubicToolShape(worldScreen.contentPane.worldPanel.world, start, c0, c1, p);
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
			shape = new CubicToolShape(worldScreen.contentPane.worldPanel.world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl0(Point c) {
		this.c0 = c;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(worldScreen.contentPane.worldPanel.world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setControl1(Point c) {
		this.c1 = c;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(worldScreen.contentPane.worldPanel.world, start, c0, c1, p);
			startKnob.setPoint(start);
			control0Knob.setPoint(c0);
			control1Knob.setPoint(c1);
			endKnob.setPoint(p);
		}
	}
	
	public void setEnd(Point p) {
		this.p = p;
		if (start != null && p != null && c0 != null && c1 != null) {
			shape = new CubicToolShape(worldScreen.contentPane.worldPanel.world, start, c0, c1, p);
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
			APP.tool = new RegularTool(worldScreen, debuggerScreen);
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
			worldScreen.contentPane.repaint();
			break;
		case SET:
			mode = CubicToolMode.FREE;
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
			worldScreen.contentPane.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void dKey() {
		switch (mode) {
		case FREE:
			mode = CubicToolMode.SET;
			worldScreen.contentPane.repaint();
			break;
		case SET:
			
			List<Point> pts = shape.skeleton;
			Stroke s = new Stroke(worldScreen.contentPane.worldPanel.world);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke();
			worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
			
			APP.tool = new RegularTool(worldScreen, debuggerScreen);
			
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint);
			
			worldScreen.contentPane.worldPanel.world.render_worldPanel();
			worldScreen.contentPane.worldPanel.world.render_preview();
			worldScreen.contentPane.repaint();
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
			worldScreen.contentPane.repaint();
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
			setPoint(worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint);
			break;
		case SET:
			if (!worldScreen.contentPane.worldPanel.world.lastDraggedWorldPointWasNull) {
				break;
			}
			if (!(startKnob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint) ||
					control0Knob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint) ||
					control1Knob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint) ||
					endKnob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint))) {
				break;
			}
			
			if (startKnob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = startKnob;
				origKnobCenter = knob.p;
			} else if (control0Knob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = control0Knob;
				origKnobCenter = knob.p;
			} else if (control1Knob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = control1Knob;
				origKnobCenter = knob.p;
			} else if (endKnob.hitTest(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint)) {
				mode = CubicToolMode.KNOB;
				knob = endKnob;
				origKnobCenter = knob.p;
			}
		case KNOB:
			Point diff = new Point(worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint.x - worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint.x, worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint.y - worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint.y);
			knob.drag(origKnobCenter.plus(diff));
			worldScreen.contentPane.repaint();
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
			mode = CubicToolMode.SET;
			worldScreen.contentPane.repaint();
			break;
		}
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
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
		
		ctxt.setPaintMode();
	}
}
