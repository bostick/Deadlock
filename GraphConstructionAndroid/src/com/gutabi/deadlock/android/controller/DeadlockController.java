package com.gutabi.deadlock.android.controller;

import static com.gutabi.deadlock.android.MainActivity.VIEW;
import static com.gutabi.deadlock.android.model.DeadlockModel.MODEL;

import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gutabi.deadlock.core.Point;

public class DeadlockController implements OnTouchListener {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
//	private PointF curPoint;
//	private List<PointF> curStroke;
//	private List<List<PointF>> strokes = new ArrayList<List<PointF>>();
	
	public DeadlockController() {
		
	}
	
	
	/*
	 * round a to nearest multiple of b
	 */
	static float round(float a, int b) {
		return Math.round(a / b) * b;
	}
	
	/*
	 * round coords from event to nearest multiple of:
	 */
	int roundingFactor = 5;
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		x = round(x, roundingFactor);
		y = round(y, roundingFactor);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			touchStart(x, y);
			VIEW.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "MOVE <"+x+","+y+">");
			touchMove(x, y);
			VIEW.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "UP   <"+x+","+y+">");
			touchUp();
			VIEW.invalidate();
			break;
		}
		return true;
	}
	
	private void touchStart(float x, float y) {
//		curPoint = new PointF(x, y);
//		List<PointF> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
//		curStroke = new ArrayList<PointF>();
//		curStroke.add(curPoint);
//		strokes.add(curStroke);
//		pointsToBeProcessed.add(curPoint);
		
		Point p = new Point(Math.round(x), Math.round(y));
		
		MODEL.lastPointRaw = p;
		MODEL.curStrokeRaw.add(p);
		
	}
	
	private void touchMove(float x, float y) {
		
		Point p = new Point(Math.round(x), Math.round(y));
		
		if (!p.equals(MODEL.lastPointRaw)) {
			MODEL.curStrokeRaw.add(p);
			//MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
			MODEL.lastPointRaw = p;
			//VIEW.repaint();
		}
		
	}
	
	private void touchUp() {
		
		List<Point> curStroke1 = MODEL.curStrokeRaw;
		
		for (int i = 0; i < curStroke1.size()-1; i++) {
			MODEL.graph.addStroke(curStroke1.get(i), curStroke1.get(i+1));
		}
		
		assert MODEL.graph.checkConsistency();
		
		MODEL.lastPointRaw = null;
		MODEL.curStrokeRaw.clear();
		
	}
	
}
