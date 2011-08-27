package com.gutabi.deadlock.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.gutabi.deadlock.model.DeadlockModel;
import com.gutabi.deadlock.model.Edge;
import com.gutabi.deadlock.model.Vertex;

public class Renderer {
	
	static Paint rPaint1;
	static Paint rPaint2;
	static Paint interPaint;
	static Paint tracePaint;
	
	int paintMark;
	
	DeadlockModel model;
	
	public Renderer(DeadlockModel model) {
		
		this.model = model;
		
		rPaint1 = new Road1Paint();
		rPaint2 = new Road2Paint();
		interPaint = new IntersectionPaint();
		tracePaint = new TracePaint();
		
	}
	
	public void paintCanas(Canvas canvas) {
			
		canvas.drawColor(0xFFdddddd);
		
		Set<Vertex> vs = new HashSet<Vertex>();
		
		for (Edge e : model.getEdges()) {
			vs.add(e.getStart());
			vs.add(e.getEnd());
			e.paint(canvas, rPaint1, rPaint2);
		}
		for (Vertex v : model.getVertices()) {
			v.paint(canvas, interPaint);
		}
		
		List<PointF> points = model.getPointsToBeProcessed();
		if (points != null) {
			for (int j = 1; j < points.size(); j++) {
				PointF prev = points.get(j-1);
				PointF cur = points.get(j);
				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, tracePaint);
			}
		}
		
	}
	
//	void walkAndPaint(Vertex v, Canvas canvas) {
//		Set<Edge> edges = v.getEdges();
//		for (Edge e : edges) {
//			Vertex start = e.getStart();
//			Vertex end = e.getEnd();
//			if (e.getMark() != paintMark) {
//				e.paint(canvas, rPaint1);
//				e.paint(canvas, rPaint2);
//				e.setMark(paintMark);
//			}
//			if (start.getMark() != paintMark) {
//				start.paint(canvas, interPaint);
//				start.setMark(paintMark);
//				walkAndPaint(start, canvas);
//			}
//			if (end.getMark() != paintMark) {
//				end.paint(canvas, interPaint);
//				end.setMark(paintMark);
//				walkAndPaint(end, canvas);
//			}
//			
//		}
//	}
	
	public class Road1Paint extends Paint {
		
		public Road1Paint() {
			setAntiAlias(true);
			setDither(true);
			setColor(0xff888888);
			setStyle(Paint.Style.STROKE);
			setStrokeJoin(Paint.Join.ROUND);
			setStrokeCap(Paint.Cap.ROUND);
			setStrokeWidth(10);
		}
		
	}
	
	public class Road2Paint extends Paint {
		
		public Road2Paint() {
			setAntiAlias(true);
			setDither(true);
			setColor(Color.YELLOW);
			setStyle(Paint.Style.STROKE);
			setStrokeJoin(Paint.Join.ROUND);
			setStrokeCap(Paint.Cap.ROUND);
			setStrokeWidth(1);
		}

	}
	
	public class IntersectionPaint extends Paint {
		
		public IntersectionPaint() {
			setAntiAlias(true);
			setDither(true);
			setStrokeCap(Paint.Cap.ROUND);
			setColor(Color.RED);
			setStrokeWidth(10);
		}
		
	}
	
	public class TracePaint extends Paint {
		public TracePaint() {
			setAntiAlias(true);
			setDither(true);
			setColor(0x88444444);
			setStyle(Paint.Style.STROKE);
			setStrokeJoin(Paint.Join.ROUND);
			setStrokeCap(Paint.Cap.ROUND);
			setStrokeWidth(10);
		}
	}
	
}
