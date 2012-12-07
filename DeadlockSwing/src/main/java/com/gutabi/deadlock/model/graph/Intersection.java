package com.gutabi.deadlock.model.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	public Intersection(Point p) {
		super(p);
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public boolean supportsStopSigns() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		carQueue.clear();
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		shape.paint(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (MODEL.DEBUG_DRAW) {
				
				shape.getAABB().draw(ctxt);
				
			}
		}
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		;
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setWorldPixelStroke(1);
		
		shape.draw(ctxt);
		
	}
}
