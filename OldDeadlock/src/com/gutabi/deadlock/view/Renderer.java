package com.gutabi.deadlock.view;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextPaint;

import com.gutabi.deadlock.model.DeadlockModel;

public class Renderer {
	
	Paint gPaint, rPaint1, rPaint2, curPointPaint, textPaint;
	
	long dTime;
	
	DeadlockModel model;
	
	public Renderer(DeadlockModel model) {
		
		this.model = model;
		
		gPaint = new GridPaint();
		
		rPaint1 = new Road1Paint();
		rPaint2 = new Road2Paint();
		
		curPointPaint = new CursorPaint();
		//mBitmap = Bitmap.createBitmap(32, 48, Bitmap.Config.ARGB_8888);
		//mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		
		textPaint = new TextPaint();
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
		for (int i = 0; i < DeadlockModel.GRID_WIDTH; i+=DeadlockModel.GRID_DELTA) {
			canvas.drawLine(i, 0, i, DeadlockModel.GRID_HEIGHT, gPaint);
		}
		for (int i = 0; i < DeadlockModel.GRID_HEIGHT; i+=DeadlockModel.GRID_DELTA) {
			canvas.drawLine(0, i, DeadlockModel.GRID_WIDTH, i, gPaint);
		}
		
		//PointF lastRoadPoint;
		
		// draw roads
		PointF prev = null, cur = null;
		for (List<PointF> road : model.roads) {
			for (int j = 0; j < road.size(); j++) {
				try {
					cur = road.get(j);
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
		for (List<PointF> road : model.roads) {
			for (int j = 0; j < road.size(); j++) {
				try {
					cur = road.get(j);
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
		
		// draw current road and point
		if (model.curPoint != null) {
			canvas.drawLine(cur.x, cur.y, model.curPoint.x, model.curPoint.y, rPaint1);
			canvas.drawLine(cur.x, cur.y, model.curPoint.x, model.curPoint.y, rPaint2);
			canvas.drawPoint(model.curPoint.x, model.curPoint.y, curPointPaint);
		}
	}
	
}
