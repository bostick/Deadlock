package com.gutabi.deadlock.android.view;

import static com.gutabi.deadlock.android.model.DeadlockModel.MODEL;

import java.util.HashSet;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;

public class Renderer {
	
	static Paint rPaint1 = new Road1Paint();
	static Paint rPaint2 = new Road2Paint();
	static Paint interPaint = new IntersectionPaint();
	static Paint tracePaint = new TracePaint();
	static Paint touchPaint = new TouchPaint();
	
	int paintMark;
	
	public Renderer() {
		
	}
	
	public void paintCanas(Canvas canvas) {
			
		canvas.drawColor(0xFFdddddd);
		
		Set<Vertex> vs = new HashSet<Vertex>();
		
		for (Edge e : MODEL.graph.getEdges()) {
			vs.add(e.getStart());
			vs.add(e.getEnd());
			paintEdge(e, canvas, rPaint1, rPaint2);
		}
		for (Vertex v : MODEL.graph.getVertices()) {
			paintVertex(v, canvas, interPaint);
		}
		
//		List<PointF> points = MODEL.getPointsToBeProcessed();
//		if (points != null) {
//			for (int j = 1; j < points.size(); j++) {
//				PointF prev = points.get(j-1);
//				PointF cur = points.get(j);
//				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, tracePaint);
//			}
//		}
		
		//g2.setColor(Color.RED);
		for (int i = 0; i < MODEL.curStrokeRaw.size()-1; i++) {
			Point a = MODEL.curStrokeRaw.get(i);
			Point b = MODEL.curStrokeRaw.get(i+1);
			//g2.drawLine(a.x, a.y, b.x, b.y);
			canvas.drawLine(a.x, a.y, b.x, b.y, tracePaint);
		}
		
		//g2.setColor(Color.RED);
		if (MODEL.lastPointRaw != null) {
			//g2.fillOval(MODEL.lastPointRaw.x-5, MODEL.lastPointRaw.y-5, 10, 10);
			canvas.drawOval(new RectF(MODEL.lastPointRaw.x-5, MODEL.lastPointRaw.y-5, MODEL.lastPointRaw.x+5, MODEL.lastPointRaw.y+5), touchPaint);
		}
		
	}
	
	private static void paintEdge(Edge e, Canvas canvas, Paint p1, Paint p2) {
		
		for (int i = 0; i < e.size()-1; i++) {
			Point prev = e.getPoint(i);
			Point cur = e.getPoint(i+1);
			canvas.drawLine(prev.x, prev.y, cur.x, cur.y, p1);
		}
//		if (start == null && end == null) {
//			PointF prev = points.get(points.size()-1);
//			PointF cur = points.get(0);
//			canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
//		}
		for (int i = 0; i < e.size()-1; i++) {
			Point prev = e.getPoint(i);
			Point cur = e.getPoint(i+1);
			canvas.drawLine(prev.x, prev.y, cur.x, cur.y, p2);
		}
//		if (start == null && end == null) {
//			PointF prev = points.get(points.size()-1);
//			PointF cur = points.get(0);
//			canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint2);
//		}
		
	}
	
	private static void paintVertex(Vertex v, Canvas canvas, Paint paint) {
		Point p = v.getPoint();
		canvas.drawPoint(p.x, p.y, paint);
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
	
	public static class Road1Paint extends Paint {
		
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
	
	public static class Road2Paint extends Paint {
		
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
	
	public static class IntersectionPaint extends Paint {
		
		public IntersectionPaint() {
			setAntiAlias(true);
			setDither(true);
			setStrokeCap(Paint.Cap.ROUND);
			setColor(Color.RED);
			setStrokeWidth(10);
		}
		
	}
	
	public static class TracePaint extends Paint {
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
	
	public static class TouchPaint extends Paint {
		
		public TouchPaint() {
			setAntiAlias(true);
			setDither(true);
			setStrokeCap(Paint.Cap.ROUND);
			setColor(Color.GREEN);
			setStrokeWidth(15);
		}
		
	}
	
}
