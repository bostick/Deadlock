package com.gutabi.deadlock.swing.model;

import java.awt.Graphics2D;
import java.util.List;

import com.gutabi.deadlock.swing.utils.Point;

public interface Vertex {
	
	public void add(Edge ed);
	
	public void remove(Edge ed);
	
	public List<Edge> getEdges();
	
	public Edge getOnlyEdge();
	
	//public void setPointF(PointF p);
	
	public Point getPoint();
	
	public void paint(Graphics2D g);
	
}
