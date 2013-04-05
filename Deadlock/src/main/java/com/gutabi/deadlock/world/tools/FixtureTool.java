package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.HashSet;
import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.Vertex;

public class FixtureTool extends WorldToolBase {
	
	Quadrant currentQuadrant;
	Quadrant top;
	Quadrant left;
	Quadrant right;
	Quadrant bottom;
	
	double distToTopOrBottom;
	double distToLeftOrRight;
	
	private Axis axis;
	
	private FixtureToolShape shape;
	
	public FixtureTool() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		World world = (World)APP.model;
		
		if (p != null) {
			currentQuadrant = world.quadrantMap.findQuadrant(p);
			
			if (currentQuadrant == null) {
				shape = null;
				axis = null;
				return;
			}
			
			if (!currentQuadrant.active) {
				shape = null;
				axis = null;
				return;
			}
			
			top = world.quadrantMap.upFixPoint(currentQuadrant);
			Point topCenter = top.center();
			
			bottom = world.quadrantMap.downFixPoint(currentQuadrant);
			Point bottomCenter = bottom.center();
			
			left = world.quadrantMap.leftFixPoint(currentQuadrant);
			Point leftCenter = left.center();
			
			right = world.quadrantMap.rightFixPoint(currentQuadrant);
			Point rightCenter = right.center();
			
			double distToTop = Math.abs(p.y - (topCenter.y - QuadrantMap.QUADRANT_HEIGHT/2));
			double distToBottom = Math.abs(p.y - (bottomCenter.y + QuadrantMap.QUADRANT_HEIGHT/2));
			double distToLeft = Math.abs(p.x - (leftCenter.x - QuadrantMap.QUADRANT_WIDTH/2));
			double distToRight = Math.abs(p.x - (rightCenter.x + QuadrantMap.QUADRANT_WIDTH/2));
			
			distToTopOrBottom = Math.min(distToTop, distToBottom);
			
			distToLeftOrRight = Math.min(distToLeft, distToRight);
			
			if (distToTopOrBottom < distToLeftOrRight) {
				axis = Axis.TOPBOTTOM;
			} else {
				axis = Axis.LEFTRIGHT;
			}
			
			switch (axis) {
			case LEFTRIGHT:
				shape = new FixtureToolShape(p, leftCenter, rightCenter, axis);
				break;
			case TOPBOTTOM:
				shape = new FixtureToolShape(p, topCenter, bottomCenter, axis);
				break;
			default:
				assert false;
				break;
			}
		} else {
			currentQuadrant = null;
			top = null;
			left = null;
			right = null;
			bottom = null;
			
			distToTopOrBottom = -1;
			distToLeftOrRight = -1;
			
			axis = null;
			
			shape = null;
		}
		
	}
	
	public Axis getAxis() {
		return axis;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Point getSourcePoint() {
		return shape.worldSource;
	}
	
	public Point getSinkPoint() {
		return shape.worldSink;
	}
	
	public void escKey() {
		World world = (World)APP.model;
		
		APP.tool = new RegularTool();
		
		APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
		APP.appScreen.contentPane.repaint();
	}
	
	public void wKey() {
		World world = (World)APP.model;
		
		if (world.graph.pureGraphIntersect(shape) == null) {
			
			Fixture source = new Fixture(world, getSourcePoint(), axis);
			Fixture sink = new Fixture(world, getSinkPoint(), axis);
			
			source.setType(FixtureType.SOURCE);
			sink.setType(FixtureType.SINK);
			
			source.match = sink;
			sink.match = source;
			
			switch (axis) {
			case TOPBOTTOM:
				source.setFacingSide(Side.BOTTOM);
				sink.setFacingSide(Side.BOTTOM);
				break;
			case LEFTRIGHT:
				source.setFacingSide(Side.RIGHT);
				sink.setFacingSide(Side.RIGHT);
				break;
			}
			
			Set<Vertex> affected = new HashSet<Vertex>();
			Set<Vertex> res = world.addFixture(source);
			affected.addAll(res);
			res = world.addFixture(sink);
			affected.addAll(res);
			world.graph.computeVertexRadii(affected);
			
			APP.tool = new RegularTool();
			APP.tool.setPoint(world.lastMovedWorldPoint);
			
			world.render_worldPanel();
			world.render_preview();
			APP.appScreen.contentPane.repaint();
		}
	}
	
	public void moved(InputEvent ignore) {
		super.moved(ignore);
		
		World world = (World)APP.model;
		
		APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		APP.appScreen.contentPane.repaint();
	}
	
	public void paint_panel(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		if (p == null) {
			return;
		}
		
		if (axis == null) {
			
		} else {
			
			ctxt.setColor(Color.WHITE);
			ctxt.setXORMode(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			
			Transform origTransform = ctxt.getTransform();
			
			ctxt.scale(world.worldCamera.pixelsPerMeter);
			ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
			
			shape.draw(ctxt);
			
			ctxt.setTransform(origTransform);
			
			ctxt.setPaintMode();
		}
		
	}
	
}
