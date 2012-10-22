package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class Stroke {
	
	public static final double MOUSE_RADIUS_PIXELS = Math.sqrt(2 * 0.5 * 0.5) * MODEL.PIXELS_PER_METER;
	public static final double MOUSE_RADIUS_METERS = Math.sqrt(2 * 0.5 * 0.5);
	
	private Point lastPanelPoint;
	
	private List<Point> curPanelStroke = new ArrayList<Point>();
	private List<Point> curWorldStroke = new ArrayList<Point>();
	
	private List<List<Point>> allPanelStrokes = new ArrayList<List<Point>>();

	public void clear() {
		lastPanelPoint = null;
		curPanelStroke.clear();
	}
	
	public double getRadius() {
		return MOUSE_RADIUS_METERS;
	}
	
	public void start(Point p) {
		lastPanelPoint = p;
		
		curPanelStroke.add(p);
		curWorldStroke.add(VIEW.panelToWorld(p));
		
		allPanelStrokes.add(curPanelStroke);
	}
	
	public void move(Point p) {
		lastPanelPoint = p;
		
		curPanelStroke.add(p);
		curWorldStroke.add(VIEW.panelToWorld(p));
	}
	
	public int size() {
		return curPanelStroke.size();
	}
	
	public List<Point> getPanelPoints() {
		return curPanelStroke;
	}
	
	public List<Point> getWorldPoints() {
		return curWorldStroke;
	}
	
	public Point getPanelPoint(int i) {
		return curPanelStroke.get(i);
	}
	
	public Point getWorldPoint(int i) {
		return curWorldStroke.get(i);
	}
	
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(Color.RED);
		int size = curPanelStroke.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = curPanelStroke.get(i);
			xPoints[i] = (int)p.x;
			yPoints[i] = (int)p.y;
		}
		
		g2.drawPolyline(xPoints, yPoints, size);
		
		g2.setColor(Color.RED);
		if (lastPanelPoint != null) {
			
			g2.drawOval(
					(int)(lastPanelPoint.x - MOUSE_RADIUS_PIXELS),
					(int)(lastPanelPoint.y - MOUSE_RADIUS_PIXELS),
					(int)(2 * MOUSE_RADIUS_PIXELS),
					(int)(2 * MOUSE_RADIUS_PIXELS));
			
		}
		
	}
	
}
