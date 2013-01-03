package com.gutabi.deadlock.world.tools;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.WorldScreen;
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
	
	public FixtureTool(WorldScreen screen) {
		super(screen);
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			currentQuadrant = screen.world.quadrantMap.findQuadrant(p);
			
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
			
			top = screen.world.quadrantMap.upFixPoint(currentQuadrant);
			Point topCenter = top.center();
			
			bottom = screen.world.quadrantMap.downFixPoint(currentQuadrant);
			Point bottomCenter = bottom.center();
			
			left = screen.world.quadrantMap.leftFixPoint(currentQuadrant);
			Point leftCenter = left.center();
			
			right = screen.world.quadrantMap.rightFixPoint(currentQuadrant);
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
		
		screen.tool = new RegularTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.contentPane.worldPanel.lastMovedOrDraggedWorldPoint));
		
		screen.contentPane.repaint();
	}
	
	public void wKey() {
		
		if (screen.world.graph.pureGraphIntersect(shape) == null) {
			
			Fixture source = new Fixture(screen.world, getSourcePoint(), axis);
			Fixture sink = new Fixture(screen.world, getSinkPoint(), axis);
			
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
			Set<Vertex> res = screen.world.addFixture(source);
			affected.addAll(res);
			res = screen.world.addFixture(sink);
			affected.addAll(res);
			screen.world.graph.computeVertexRadii(affected);
			
			screen.tool = new RegularTool(screen);
			screen.tool.setPoint(screen.contentPane.worldPanel.lastMovedWorldPoint);
			
			screen.world.render_worldPanel();
			screen.world.render_preview();
			screen.contentPane.repaint();
		}
	}
	
	public void moved(InputEvent ev) {
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.contentPane.worldPanel.lastMovedOrDraggedWorldPoint));
		screen.contentPane.repaint();
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		if (axis == null) {
			
		} else {
			
			ctxt.setColor(Color.WHITE);
			ctxt.setXORMode(Color.BLACK);
			ctxt.setPixelStroke(1.0);
			
			shape.draw(ctxt);
			
			ctxt.setPaintMode();
		}
		
	}
	
}
