package com.gutabi.deadlock.view;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.gutabi.deadlock.model.DeadlockModel;

public class Renderer {
	
	Paint rPaint1, rPaint2, interPaint;	
	long dTime;
	
	DeadlockModel model;
	
	public Renderer(DeadlockModel model) {
		
		this.model = model;
		
		//gPaint = new GridPaint();
		
		rPaint1 = new Road1Paint();
		rPaint2 = new Road2Paint();
		interPaint = new IntersectionPaint();
		//curPointPaint = new CursorPaint();
		//mBitmap = Bitmap.createBitmap(32, 48, Bitmap.Config.ARGB_8888);
		//mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		
	}
	
//	public void update() {
//			
//		dTime = System.currentTimeMillis() - dTime;
//		
//		if (model.curPoint != null && model.curRoundedPoint != null) {
//			double dx = model.curRawPoint.x - model.curRoundedPoint.x;
//			double dy = model.curRawPoint.y - model.curRoundedPoint.y;
//			double diffDist = Math.sqrt((dx * dx) + (dy * dy));
//			
//			//String dist = "|" + Double.toString(model.totalDist) + "|";
//			String dist = "|" + Double.toString(diffDist) + "|";
////			PointF cp = model.curRawPoint;
////			String rawpos = (cp != null) ? "<" + cp.x + ", " + cp.y + ">" : "<>";
////			cp = model.curRoundedPoint;
////			String roundpos = (cp != null) ? "<" + cp.x + ", " + cp.y + ">" : "<>";
////			String text = dist + " " + rawpos + " " + roundpos;
//			String text = dist;
//			//model.setStatusText(text);
//		}
//		
//	}
	
	public void paintCanas(Canvas canvas) {
		// draw grid
		canvas.drawColor(0xFFdddddd);
//		for (int i = 0; i < DeadlockModel.GRID_WIDTH; i+=DeadlockModel.GRID_DELTA) {
//			canvas.drawLine(i, 0, i, DeadlockModel.GRID_HEIGHT, gPaint);
//		}
//		for (int i = 0; i < DeadlockModel.GRID_HEIGHT; i+=DeadlockModel.GRID_DELTA) {
//			canvas.drawLine(0, i, DeadlockModel.GRID_WIDTH, i, gPaint);
//		}
		
		//PointF lastRoadPoint;
		
		// draw roads
		PointF prev = null, cur = null;
		for (List<PointF> seg : model.roadSegments) {
			for (int j = 0; j < seg.size(); j++) {
				try {
					cur = seg.get(j);
					if (j == 0) {
						continue;
					} else {
						canvas.drawLine(prev.x, prev.y, cur.x, cur.y, rPaint1);
					}
				} finally {
					prev = cur;
				}
			}
		}
		for (List<PointF> seg : model.roadSegments) {
			for (int j = 0; j < seg.size(); j++) {
				try {
					cur = seg.get(j);
					if (j == 0) {
						continue;
					} else {
						canvas.drawLine(prev.x, prev.y, cur.x, cur.y, rPaint2);
					}
				} finally {
					prev = cur;
				}
			}
		}
		
		for (PointF p : model.inters) {
			canvas.drawPoint(p.x, p.y, interPaint);
		}
		
		// draw current road and point
		if (model.curPoint != null) {
			canvas.drawLine(cur.x, cur.y, model.curPoint.x, model.curPoint.y, rPaint1);
			canvas.drawLine(cur.x, cur.y, model.curPoint.x, model.curPoint.y, rPaint2);
			//canvas.drawPoint(model.curPoint.x, model.curPoint.y, curPointPaint);
		}
	}
	
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
	
}
