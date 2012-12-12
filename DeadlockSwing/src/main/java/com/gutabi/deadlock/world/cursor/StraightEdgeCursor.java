package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.WorldMode;
import com.gutabi.deadlock.world.graph.Vertex;

public class StraightEdgeCursor extends Cursor {
	
	public final Vertex first;
	
	StraightEdgeCursorShape shape;
	
	public StraightEdgeCursor(Vertex first) {
		this.first = first;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new StraightEdgeCursorShape(first.p, p);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey() {
		
		APP.world.mode = WorldMode.IDLE;
		
		APP.world.cursor = new RegularCursor();
		
		APP.world.cursor.setPoint(APP.world.lastMovedWorldPoint);
		
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
