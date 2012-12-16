package com.gutabi.deadlock.world.cursor;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;

//@SuppressWarnings("static-access")
public class MergerCursor extends CursorBase {
	
	MergerCursorShape shape;
	
	public MergerCursor(WorldScreen screen) {
		super(screen);
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
	
	public void escKey(InputEvent ev) {
		
		screen.cursor = new RegularCursor(screen);
		
		screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		
		screen.repaint();
	}
	
	public void insertKey(InputEvent ev) {
		if (screen.world.quadrantMap.completelyContains(shape)) {
			
			if (screen.world.graph.pureGraphBestHitTest(shape) == null) {
				
				screen.world.insertMergerTop(p);
				
				screen.cursor = new RegularCursor(screen);
				
				screen.cursor.setPoint(screen.lastMovedWorldPoint);
				
				screen.render();
				screen.repaint();
			}
			
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
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		
		shape.draw(ctxt);
		
	}

}
