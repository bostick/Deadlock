package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.World;

//@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	public Intersection(World world, Point p) {
		super(world, p);
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
		driverQueue.clear();
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("start intersection \n");
		
		s.append("id " + id + "\n");
		
		s.append("point " + p.toString() + "\n");
		
		s.append("end intersection\n");
		
		return s.toString();
	}
	
	public static Intersection fromFileString(String s) {
		return null;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		shape.paint(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (APP.DEBUG_DRAW) {
				
				shape.getAABB().draw(ctxt);
				
			}
		}
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		;
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setPixelStroke(1);
		
		shape.draw(ctxt);
		
	}
}
