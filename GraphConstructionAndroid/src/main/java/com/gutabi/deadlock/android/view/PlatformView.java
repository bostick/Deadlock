package com.gutabi.deadlock.android.view;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.gutabi.core.DPoint;
import com.gutabi.core.Edge;
import com.gutabi.core.Point;
import com.gutabi.core.Vertex;

public class PlatformView extends android.view.View {
	
	static Paint rPaint1 = new Road1Paint();
	static Paint rPaint2 = new Road2Paint();
	static Paint interPaint = new IntersectionPaint();
	static Paint tracePaint = new TracePaint();
	static Paint touchPaint = new TouchPaint();
	
	int paintMark;
	
	public PlatformView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	public void init() {
		//this.
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
		canvas.drawColor(0xFFdddddd);
		
		canvas.scale((float)VIEW.getZoom(), (float)VIEW.getZoom());
		canvas.translate((float)-VIEW.viewLoc.x, (float)-VIEW.viewLoc.y);
		
		for (Edge e : MODEL.graph.getEdges()) {
			paintEdge(e, canvas, rPaint1, rPaint2);
		}
		for (Vertex v : MODEL.graph.getVertices()) {
			paintVertex(v, canvas, interPaint);
		}
		
		for (int i = 0; i < CONTROLLER.curStrokeRaw.size()-1; i++) {
			DPoint a = CONTROLLER.curStrokeRaw.get(i);
			DPoint b = CONTROLLER.curStrokeRaw.get(i+1);
			canvas.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y, tracePaint);
		}
		
		if (CONTROLLER.lastPointRaw != null) {
			canvas.drawOval(new RectF((float)CONTROLLER.lastPointRaw.x-5, (float)CONTROLLER.lastPointRaw.y-5, (float)CONTROLLER.lastPointRaw.x+5, (float)CONTROLLER.lastPointRaw.y+5), touchPaint);
		}
		
	}
	
	private static void paintEdge(Edge e, Canvas canvas, Paint p1, Paint p2) {
		
		for (int i = 0; i < e.size()-1; i++) {
			Point prev = e.getPoint(i);
			Point cur = e.getPoint(i+1);
			canvas.drawLine(prev.x, prev.y, cur.x, cur.y, p1);
		}

		for (int i = 0; i < e.size()-1; i++) {
			Point prev = e.getPoint(i);
			Point cur = e.getPoint(i+1);
			canvas.drawLine(prev.x, prev.y, cur.x, cur.y, p2);
		}
		
	}
	
	private static void paintVertex(Vertex v, Canvas canvas, Paint paint) {
		Point p = v.getPoint();
		canvas.drawPoint(p.x, p.y, paint);
	}
	
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
