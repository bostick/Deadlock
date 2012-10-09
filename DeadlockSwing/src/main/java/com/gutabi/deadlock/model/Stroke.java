package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;

public class Stroke {
	
	private Point lastPanelPoint;
	private List<Point> curPanelStroke = new ArrayList<Point>();
	private List<List<Point>> allPanelStrokes = new ArrayList<List<Point>>();

	public void clear() {
		lastPanelPoint = null;
		curPanelStroke.clear();
	}
	
	public void clearAll() {
		lastPanelPoint = null;
		curPanelStroke.clear();
		allPanelStrokes.clear();
	}
	
	public void start(Point p) {
		lastPanelPoint = p;
		curPanelStroke.add(p);
		allPanelStrokes.add(new ArrayList<Point>());
		allPanelStrokes.get(allPanelStrokes.size()-1).add(p);
	}
	
	public void move(Point p) {
		curPanelStroke.add(p);
		lastPanelPoint = p;
		allPanelStrokes.get(allPanelStrokes.size()-1).add(p);
	}
	
	public List<Point> getPoints() {
		return curPanelStroke;
	}
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(Color.RED);
		int size = curPanelStroke.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = curPanelStroke.get(i);
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		
		g2.setStroke(new DraftingStroke());
		g2.drawPolyline(xPoints, yPoints, size);
		
		g2.setColor(Color.RED);
		if (lastPanelPoint != null) {
			
			g2.fillOval(
					(int)(lastPanelPoint.getX() - MODEL.world.VERTEX_RADIUS * MODEL.world.PIXELS_PER_METER),
					(int)(lastPanelPoint.getY() - MODEL.world.VERTEX_RADIUS * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.VERTEX_RADIUS * 2 * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.VERTEX_RADIUS * 2 * MODEL.world.PIXELS_PER_METER));
			
		}
		
	}
	
	public static class DraftingStroke extends BasicStroke {
		
		public DraftingStroke() {
			super(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}
		
	}
	
}
