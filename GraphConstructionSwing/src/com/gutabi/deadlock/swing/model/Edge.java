package com.gutabi.deadlock.swing.model;

import java.awt.Graphics2D;
import java.util.List;

import com.gutabi.deadlock.swing.utils.Point;

public interface Edge {
	
	public void setStart(Vertex v);
	
	public Vertex getStart();
	
	public void setEnd(Vertex v);
	
	public Vertex getEnd();
	
	public List<Point> getPoints();
	
	public void paint(Graphics2D g);
	
}
