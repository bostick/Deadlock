package com.gutabi.deadlock.model;

import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public interface Vertex {
	
	public void add(Edge ed);
	
	public Set<Edge> getEdges();
	
	public void setPointF(PointF p);
	
	public PointF getPointF();
	
	public void paint(Canvas canvas, Paint paint);
	
}
