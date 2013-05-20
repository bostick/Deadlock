package com.brentonbostick.capsloc.world.tools;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.Set;

import com.brentonbostick.capsloc.math.Dim;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.InputEvent;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.Stroke;
import com.brentonbostick.capsloc.world.World;
import com.brentonbostick.capsloc.world.graph.Vertex;

public class CircleTool extends WorldToolBase {
	
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
	
	public CircleTool() {
		
		mode = CircleToolMode.FREE;
		yRadius = Vertex.INIT_VERTEX_RADIUS;
		xRadius = Vertex.INIT_VERTEX_RADIUS;
		
		ulKnob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
				Point newPoint = world.quadrantMap.getPoint(p);
				
				Dim offset = shape.c1.aabb.dim.multiply(0.5);
				
				Point newPoint2 = new Point(newPoint.x + offset.width, newPoint.y + offset.height);
				
				CircleTool.this.setPoint(newPoint2);
			}
		};
		
		brKnob = new Knob() {
			public void drag(Point p) {
				World world = (World)APP.model;
				
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
			ulKnob.setPoint(shape.c1.aabb.x, shape.c1.aabb.y);
			brKnob.setPoint(shape.c1.aabb.brX, shape.c1.aabb.brY);
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
	
	public Object getShape() {
		return shape;
	}
	
	public Point getBR() {
		return new Point(shape.c1.aabb.brX, shape.c1.aabb.brY);
	}
	
	public void escKey() {
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			APP.tool = new RegularTool();
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			break;
		case SET:
			mode = CircleToolMode.FREE;
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			break;
		case KNOB:
			assert false;
			break;
		}
	}
	
	public void aKey() {
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			mode = CircleToolMode.SET;
			break;
		case SET:
			
			Point[] pts = shape.skeleton;
			Stroke s = new Stroke(world);
			for (Point p : pts) {
				s.add(p);
			}
			s.finish();
			
			Set<Vertex> affected = s.processNewStroke(true);
			world.graph.computeVertexRadii(affected);
			
			APP.tool = new RegularTool();
			
			APP.tool.setPoint(world.lastMovedWorldPoint);
			
			world.render();
//			world.render_preview();
			break;
		case KNOB:
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
			if (!(ulKnob.hitTest(world.lastPressedWorldPoint) ||
					brKnob.hitTest(world.lastPressedWorldPoint))) {
				break;
			}
			if (ulKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = CircleToolMode.KNOB;
				knob = ulKnob;
				origKnobCenter = knob.p;
			} else if (brKnob.hitTest(world.lastPressedWorldPoint)) {
				mode = CircleToolMode.KNOB;
				knob = brKnob;
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
		case SET:
			break;
		case KNOB:	
			mode = CircleToolMode.SET;
			break;
		}
	}
	
	Point origKnobCenter;
	
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
			ulKnob.draw(ctxt);
			brKnob.draw(ctxt);
			break;
		}
		
		ctxt.popTransform();
		
		ctxt.clearXORMode();
	}

}
