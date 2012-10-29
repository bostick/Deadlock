package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.Vertex;

@SuppressWarnings("static-access")
public class Stroke {
	
	private Point lastPoint;
	
//	public List<Point> curPanelStroke = new ArrayList<Point>();
	public List<Point> curStroke = new ArrayList<Point>();
	
	private List<List<Point>> allStrokes = new ArrayList<List<Point>>();

	public void clear() {
		lastPoint = null;
		
		/*
		 * used to just do clear() for the lists, but that lead to a subtle bug
		 * the actual curWorldStroke reference was being stored inside of Edges
		 * and were being used when recomputing Edge properties. So clearing it
		 * obviously caused problems.
		 * 
		 * always better to treat lists as immutable as possible
		 */
		
//		curPanelStroke = null;
		curStroke = null;
	}
	
	
	private Rect aabb;
	
	public final Rect getAABB() {
		return aabb;
	}
	
	public void press(Point p) {
//		lastPanelPoint = p;
		lastPoint = p;
	}
	
	public void move(Point p) {
//		lastPanelPoint = null;
		lastPoint = p;
	}
	
	public void start(Point p) {
//		lastPanelPoint = p;
		lastPoint = p;
		
//		curPanelStroke = new ArrayList<Point>();
		curStroke = new ArrayList<Point>();
		
		curStroke.add(p);
//		curWorldStroke.add(VIEW.panelToWorld(p));
		
		allStrokes.add(curStroke);
		
		computeAABB();
	}
	
	public void drag(Point p) {
		lastPoint = p;
		
		curStroke.add(p);
//		curWorldStroke.add(VIEW.panelToWorld(p));
		
		computeAABB();
	}
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Point p : curStroke) {
			aabb = Rect.union(aabb, new Rect(p.x-Vertex.INIT_VERTEX_RADIUS, p.y-Vertex.INIT_VERTEX_RADIUS, 2*Vertex.INIT_VERTEX_RADIUS, 2*Vertex.INIT_VERTEX_RADIUS));
		}
		
	}
	
	public void paint(Graphics2D g2) {
		
		if (!MODEL.DEBUG_DRAW) {
			
			paintStroke(g2);
			
		} else {
			
			paintStroke(g2);
			paintAABB(g2);
			
		}
		
	}
	
	private void paintStroke(Graphics2D g2) {
		
		if (curStroke != null && !curStroke.isEmpty()) {
			
			g2.setColor(Color.RED);
			
			int size = curStroke.size();
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			for (int i = 0; i < size; i++) {
				Point p = curStroke.get(i);
				xPoints[i] = (int)(p.x * MODEL.PIXELS_PER_METER);
				yPoints[i] = (int)(p.y * MODEL.PIXELS_PER_METER);
			}
			
			g2.drawPolyline(xPoints, yPoints, size);
			
			Point start = curStroke.get(0);
			g2.drawOval(
					(int)((start.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
					(int)((start.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
					(int)((2 * Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
					(int)((2 * Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
		}
		
		if (lastPoint != null) {
			g2.setColor(Color.RED);
			g2.drawOval(
					(int)((lastPoint.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
					(int)((lastPoint.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
					(int)((2 * Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
					(int)((2 * Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
			
		}
		
	}
	
	private void paintAABB(Graphics2D g2) {
		
		if (curStroke != null && !curStroke.isEmpty()) {
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(
					(int)(aabb.x * MODEL.PIXELS_PER_METER),
					(int)(aabb.y * MODEL.PIXELS_PER_METER),
					(int)(aabb.width * MODEL.PIXELS_PER_METER),
					(int)(aabb.height * MODEL.PIXELS_PER_METER));
			
		}
		
	}
	
}
