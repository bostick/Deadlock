package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;

public class StraightEdgeCursor extends CursorBase {
	
	public final Point first;
	
	StraightEdgeCursorShape shape;
	
	public StraightEdgeCursor(Point first) {
		this.first = first;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new StraightEdgeCursorShape(first, p);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey() {
		
		APP.world.cursor = new RegularCursor();
		
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		
		VIEW.repaint();
	}
	
	public void qKey() {
		
		Stroke s = new Stroke();
		s.add(first);
		s.add(p);
		s.finish();
		s.processNewStroke();
		assert APP.world.checkConsistency();
		
		APP.render();
		
		APP.world.cursor = new RegularCursor();
		APP.world.cursor.setPoint(APP.world.lastMovedOrDraggedWorldPoint);
		VIEW.repaint();
	}
	
	public void moved(InputEvent ev) {
		APP.world.cursor.setPoint(APP.world.getPoint(APP.world.lastMovedOrDraggedWorldPoint));
		VIEW.repaint();
	}
	
	public void exited(InputEvent ev) {
		APP.world.cursor.setPoint(null);
		VIEW.repaint();
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
