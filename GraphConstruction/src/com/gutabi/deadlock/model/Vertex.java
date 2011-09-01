package com.gutabi.deadlock.model;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public interface Vertex {
	
	public void add(Edge ed);
	
	public void remove(Edge ed);
	
	public List<Edge> getEdges();
	
	public Edge getOnlyEdge();
	
	//public void setPointF(PointF p);
	
	public PointF getPointF();
	
	public void paint(Canvas canvas, Paint paint);
	
}
