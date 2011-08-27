package com.gutabi.deadlock.model;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public interface Edge {
	
	public void setStart(Vertex v);
	
	public Vertex getStart();
	
	public void setEnd(Vertex v);
	
	public Vertex getEnd();
	
	public List<PointF> getPoints();
	
	public void paint(Canvas canvas, Paint paint1, Paint paint2);
	
}
