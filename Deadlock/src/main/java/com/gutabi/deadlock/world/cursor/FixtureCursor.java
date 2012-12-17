package com.gutabi.deadlock.world.cursor;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class FixtureCursor extends CursorBase {
	
	Quadrant currentQuadrant;
	Quadrant top;
	Quadrant left;
	Quadrant right;
	Quadrant bottom;
	
	double distToTopOrBottom;
	double distToLeftOrRight;
	
	private Axis axis;
	
	private FixtureCursorShape shape;
	
	public FixtureCursor(WorldScreen screen) {
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
				shape = new FixtureCursorShape(p, leftCenter, rightCenter, axis);
				break;
			case TOPBOTTOM:
				shape = new FixtureCursorShape(p, topCenter, bottomCenter, axis);
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
	
	public void escKey(InputEvent ev) {
		
		screen.cursor = new RegularCursor(screen);
		
		screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		
		screen.repaint();
	}
	
	public void wKey(InputEvent ev) {
		
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
			Set<Vertex> res = screen.world.graph.addVertexTop(source);
			affected.addAll(res);
			res = screen.world.graph.addVertexTop(sink);
			affected.addAll(res);
			screen.world.graph.computeVertexRadii(affected);
			
			screen.cursor = new RegularCursor(screen);
			screen.cursor.setPoint(screen.lastMovedWorldPoint);
			
			screen.render();
			screen.repaint();		
		}
	}
	
	public void moved(InputEvent ev) {
		screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.repaint();
	}
	
	public void exited(InputEvent ev) {
		screen.cursor.setPoint(null);
		screen.repaint();
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		if (axis == null) {
			
		} else {
			
			ctxt.setColor(Color.WHITE);
			ctxt.setXORMode(Color.BLACK);
			ctxt.setPixelStroke(1);
			
			shape.draw(ctxt);
			
		}
		
	}
	
}
