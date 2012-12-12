package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class MergerCursor extends CursorBase {
	
	MergerCursorShape shape;
	
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
		
		APP.world.cursor = new RegularCursor();
		
		APP.world.cursor.setPoint(APP.world.lastMovedWorldPoint);
		
		VIEW.repaintCanvas();
		
	}
	
	public void insertKey() {
		if (APP.world.completelyContains(shape)) {
			
			if (APP.world.pureGraphBestHitTest(shape) == null) {
				
				APP.world.insertMergerTop(p);
				
				APP.world.cursor = new RegularCursor();
				
				APP.world.cursor.setPoint(APP.world.lastMovedWorldPoint);
				
				APP.render();
				VIEW.repaintCanvas();
				VIEW.repaintControlPanel();
				
			}
			
		}
	}
	
	public void moved(InputEvent ev) {
		APP.world.map.setCursorPoint(this, APP.world.lastMovedOrDraggedWorldPoint);
		VIEW.repaintCanvas();
	}
	
	public void draw(RenderingContext ctxt) {
	
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setWorldPixelStroke(1);
		
		shape.draw(ctxt);
		
	}

}
