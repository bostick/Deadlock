package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.graph.Merger;

@SuppressWarnings("static-access")
public class Cursor {
	
	public final double r;
	private Point p;
	
	private Rect aabb;
	
	public Cursor(double r) {
		this.r = r;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		computeAABB();
	}
	
	public Point getPoint() {
		return p;
	}
	
	public void computeAABB() {
		
		assert p != null;
		
		switch (CONTROLLER.mode) {
		case DRAFTING:
		case IDLE:
		case PAUSED:
		case RUNNING:
			aabb = new Rect(p.x - r, p.y - r, 2*r, 2*r);
			break;
		case MERGEROUTLINE:
			
			aabb = Merger.outlineAABB(p);
			
			break;
		}
		
	}
	
	public Rect getAABB() {
		return aabb;
	}
	
	public void paint(Graphics2D g2) {
		
		paintCursor(g2);
		
		if (MODEL.DEBUG_DRAW) {
			
			paintAABB(g2);
			
		}
		
	}
	
	
	public static java.awt.Stroke dashedOutlineStroke = new BasicStroke(7.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{15.0f}, 0.0f);
	public static java.awt.Stroke solidOutlineStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	private void paintCursor(Graphics2D g2) {
		
		if (p != null) {
			
			switch (CONTROLLER.mode) {
			case RUNNING:
				return;
			case DRAFTING:
			case IDLE:
			case PAUSED:
				
				java.awt.Stroke origStroke = g2.getStroke();
				
				g2.setColor(Color.GRAY);
				g2.setStroke(solidOutlineStroke);
				
				g2.drawOval(
						(int)((p.x - r) * MODEL.PIXELS_PER_METER),
						(int)((p.y - r) * MODEL.PIXELS_PER_METER),
						(int)((2 * r) * MODEL.PIXELS_PER_METER),
						(int)((2 * r) * MODEL.PIXELS_PER_METER));
				
				g2.setStroke(origStroke);
				
				break;
			case MERGEROUTLINE:
				
				Merger.paintOutline(p, g2);
				
				break;
			}
			
		}
		
	}
	
	private void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabb.x * MODEL.PIXELS_PER_METER),
				(int)(aabb.y * MODEL.PIXELS_PER_METER),
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
}
