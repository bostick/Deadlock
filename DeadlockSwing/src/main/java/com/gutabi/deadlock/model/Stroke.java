package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;

@SuppressWarnings("static-access")
public class Stroke {
	
//	public static final double MOUSE_RADIUS_METERS = 0.4 * Math.sqrt(2 * 0.5 * 0.5);
//	public static final double MOUSE_RADIUS_PIXELS = MOUSE_RADIUS_METERS * MODEL.PIXELS_PER_METER;
	
	private Point lastPanelPoint;
	
	public List<Point> curPanelStroke = new ArrayList<Point>();
	public List<Point> curWorldStroke = new ArrayList<Point>();
	
	private List<List<Point>> allPanelStrokes = new ArrayList<List<Point>>();

	public void clear() {
		lastPanelPoint = null;
		
		/*
		 * used to just do clear() for the lists, but that lead to a subtle bug
		 * the actual curWorldStroke reference was being stored inside of Edges
		 * and were being used when recomputing Edge properties. So clearing it
		 * obviously caused problems.
		 * 
		 * always better to treat lists as immutable as possible
		 */
		
		curPanelStroke = null;
		curWorldStroke = null;
	}
	
//	public double getRadius() {
//		return MOUSE_RADIUS_METERS;
//	}
	
	public void press(Point p) {
//		lastPanelPoint = p;
	}
	
	public void move(Point p) {
//		lastPanelPoint = null;
	}
	
	public void start(Point p) {
		lastPanelPoint = p;
		
		curPanelStroke = new ArrayList<Point>();
		curWorldStroke = new ArrayList<Point>();
		
		curPanelStroke.add(p);
		curWorldStroke.add(VIEW.panelToWorld(p));
		
		allPanelStrokes.add(curPanelStroke);
	}
	
	public void drag(Point p) {
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
		
		if (curPanelStroke != null) {
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
		}
		
		if (lastPanelPoint != null) {
			g2.setColor(Color.RED);
			g2.drawOval(
					(int)(lastPanelPoint.x - (Vertex.INIT_VERTEX_RADIUS * MODEL.PIXELS_PER_METER)),
					(int)(lastPanelPoint.y - (Vertex.INIT_VERTEX_RADIUS * MODEL.PIXELS_PER_METER)),
					(int)(2 * (Vertex.INIT_VERTEX_RADIUS * MODEL.PIXELS_PER_METER)),
					(int)(2 * (Vertex.INIT_VERTEX_RADIUS * MODEL.PIXELS_PER_METER)));
			
		}
		
	}
	
}
