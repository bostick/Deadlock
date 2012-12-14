package com.gutabi.deadlock.world.cursor;

import java.awt.Color;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;

//@SuppressWarnings("static-access")
public class MergerCursor extends CursorBase {
	
	MergerCursorShape shape;
	
	public MergerCursor(World world) {
		super(world);
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new MergerCursorShape(p);
		} else {
			shape = null;
		}	
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey() {
		
		world.cursor = new RegularCursor(world);
		
		world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
		world.repaint();
	}
	
	public void insertKey() {
		if (world.quadrantMap.completelyContains(shape)) {
			
			if (world.graph.pureGraphBestHitTest(shape) == null) {
				
				world.insertMergerTop(p);
				
				world.cursor = new RegularCursor(world);
				
				world.cursor.setPoint(world.lastMovedWorldPoint);
				
				world.render();
				world.repaint();
			}
			
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
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		
		shape.draw(ctxt);
		
	}

}
