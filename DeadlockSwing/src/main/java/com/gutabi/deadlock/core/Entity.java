package com.gutabi.deadlock.core;

import java.awt.Graphics2D;

//@SuppressWarnings("static-access")
public interface Entity {
	
	boolean hitTest(Point p);
	
	boolean isDeleteable();
	
	void preStart();
	
	void postStop();
	
	void preStep(double t);
	
	/**
	 * return true if car should persist after time step
	 */
	boolean postStep(double t);
	
	void paint(Graphics2D g2);
	
	void paintHilite(Graphics2D g2);
	
	Rect getAABB();
	
}
