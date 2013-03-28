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
import com.gutabi.deadlock.world.DebuggerScreen;
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
	
	public FixtureTool(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		super(worldScreen, debuggerScreen);
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			currentQuadrant = worldScreen.contentPane.worldPanel.world.quadrantMap.findQuadrant(p);
			
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
			
			top = worldScreen.contentPane.worldPanel.world.quadrantMap.upFixPoint(currentQuadrant);
			Point topCenter = top.center();
			
			bottom = worldScreen.contentPane.worldPanel.world.quadrantMap.downFixPoint(currentQuadrant);
			Point bottomCenter = bottom.center();
			
			left = worldScreen.contentPane.worldPanel.world.quadrantMap.leftFixPoint(currentQuadrant);
			Point leftCenter = left.center();
			
			right = worldScreen.contentPane.worldPanel.world.quadrantMap.rightFixPoint(currentQuadrant);
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
		
		APP.tool = new RegularTool(worldScreen, debuggerScreen);
		
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		
		worldScreen.contentPane.repaint();
	}
	
	public void wKey() {
		
		if (worldScreen.contentPane.worldPanel.world.graph.pureGraphIntersect(shape) == null) {
			
			Fixture source = new Fixture(worldScreen.contentPane.worldPanel.world, getSourcePoint(), axis);
			Fixture sink = new Fixture(worldScreen.contentPane.worldPanel.world, getSinkPoint(), axis);
			
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
			Set<Vertex> res = worldScreen.contentPane.worldPanel.world.addFixture(source);
			affected.addAll(res);
			res = worldScreen.contentPane.worldPanel.world.addFixture(sink);
			affected.addAll(res);
			worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
			
			APP.tool = new RegularTool(worldScreen, debuggerScreen);
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.lastMovedWorldPoint);
			
			worldScreen.contentPane.worldPanel.world.render_worldPanel();
			worldScreen.contentPane.worldPanel.world.render_preview();
			worldScreen.contentPane.repaint();
		}
	}
	
	public void moved(InputEvent ev) {
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		worldScreen.contentPane.repaint();
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
