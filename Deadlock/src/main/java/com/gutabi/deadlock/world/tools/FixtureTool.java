package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.HashSet;
import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.Vertex;

public class FixtureTool extends ToolBase {
	
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
		super();
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			currentQuadrant = APP.worldScreen.world.quadrantMap.findQuadrant(p);
			
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
			
			top = APP.worldScreen.world.quadrantMap.upFixPoint(currentQuadrant);
			Point topCenter = top.center();
			
			bottom = APP.worldScreen.world.quadrantMap.downFixPoint(currentQuadrant);
			Point bottomCenter = bottom.center();
			
			left = APP.worldScreen.world.quadrantMap.leftFixPoint(currentQuadrant);
			Point leftCenter = left.center();
			
			right = APP.worldScreen.world.quadrantMap.rightFixPoint(currentQuadrant);
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
		
		APP.worldScreen.tool = new RegularTool();
		
		APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
		
		APP.worldScreen.contentPane.repaint();
	}
	
	public void wKey() {
		
		if (APP.worldScreen.world.graph.pureGraphIntersect(shape) == null) {
			
			Fixture source = new Fixture(APP.worldScreen.world, getSourcePoint(), axis);
			Fixture sink = new Fixture(APP.worldScreen.world, getSinkPoint(), axis);
			
			source.setType(FixtureType.SOURCE);
			sink.setType(FixtureType.SINK);
			
			source.match = sink;
			sink.match = source;
			
			switch (axis) {
			case TOPBOTTOM:
				source.setSide(Side.BOTTOM);
				sink.setSide(Side.BOTTOM);
				break;
			case LEFTRIGHT:
				source.setSide(Side.RIGHT);
				sink.setSide(Side.RIGHT);
				break;
			}
			
			Set<Vertex> affected = new HashSet<Vertex>();
			Set<Vertex> res = APP.worldScreen.world.addFixture(source);
			affected.addAll(res);
			res = APP.worldScreen.world.addFixture(sink);
			affected.addAll(res);
			APP.worldScreen.world.graph.computeVertexRadii(affected);
			
			APP.worldScreen.tool = new RegularTool();
			APP.worldScreen.tool.setPoint(APP.worldScreen.world.lastMovedWorldPoint);
			
			APP.worldScreen.world.render_worldPanel();
			APP.worldScreen.world.render_preview();
			APP.worldScreen.contentPane.repaint();
		}
	}
	
	public void moved(InputEvent ev) {
		APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
		APP.worldScreen.contentPane.repaint();
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		if (axis == null) {
			
		} else {
			
			ctxt.setColor(Color.WHITE);
			ctxt.setXORMode(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			
			shape.draw(ctxt);
			
			ctxt.setPaintMode();
		}
		
	}
	
}
