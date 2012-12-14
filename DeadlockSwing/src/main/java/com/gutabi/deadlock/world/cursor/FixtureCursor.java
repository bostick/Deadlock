package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Side;

@SuppressWarnings("static-access")
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
	
	public FixtureCursor(World world) {
		super(world);
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
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
			
			double distToTop = Math.abs(p.y - (topCenter.y - APP.QUADRANT_HEIGHT/2));
			double distToBottom = Math.abs(p.y - (bottomCenter.y + APP.QUADRANT_HEIGHT/2));
			double distToLeft = Math.abs(p.x - (leftCenter.x - APP.QUADRANT_WIDTH/2));
			double distToRight = Math.abs(p.x - (rightCenter.x + APP.QUADRANT_WIDTH/2));
			
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
	
	public void escKey() {
		
		world.cursor = new RegularCursor(world);
		
		world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
		world.repaint();
	}
	
	public void wKey() {
		
		if (world.graph.pureGraphBestHitTest(shape) == null) {
			
			Fixture source = new Fixture(world, getSourcePoint(), axis);
			Fixture sink = new Fixture(world, getSinkPoint(), axis);
			
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
			
			world.addVertexTop(source);
			
			world.addVertexTop(sink);
			
			world.cursor = new RegularCursor(world);
			world.cursor.setPoint(world.lastMovedWorldPoint);
			
			world.render();
			world.repaint();		
		}
	}
	
	public void moved(InputEvent ev) {
		world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		world.repaint();
	}
	
	public void exited(InputEvent ev) {
		world.cursor.setPoint(null);
		world.repaint();
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
