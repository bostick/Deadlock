package com.gutabi.deadlock.controller;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gutabi.deadlock.model.DeadlockModel;
import com.gutabi.deadlock.view.DeadlockView;

public class DeadlockController implements OnTouchListener {
	
	static final double SQRT2 = Math.sqrt(2);
	
	private DeadlockModel model;
	private DeadlockView view;
	
	public DeadlockController(DeadlockModel model, DeadlockView view) {
		this.model = model;
		this.view = view;
	}
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchStart(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touchUp();
			view.invalidate();
			break;
		}
		return true;
	}
	
	private void touchStart(final float x, final float y) {
		
		PointF point = new PointF(x, y);
		model.curRawPoint = point;
		
		point = DeadlockModel.roundToGrid(point);
		
		model.curRoundedPoint = point;
		
		List<PointF> road = new ArrayList<PointF>();
		road.add(point);
		
		model.curRoad = road;
		model.roads.add(road);
	}
	
	private void touchMove(float x, float y) {
		
		PointF lastRoundedPoint = model.curRoundedPoint;
		//PointF lastRawPoint = model.curRawPoint;
		
		PointF point = new PointF(x, y);
		model.curRawPoint = point;
		
		point = DeadlockModel.roundToGrid(point);
		
		model.curRoundedPoint = point;
		
		double dx = model.curRawPoint.x - model.curRoundedPoint.x;
		double dy = model.curRawPoint.y - model.curRoundedPoint.y;
		double diffDist = Math.sqrt((dx * dx) + (dy * dy));
		
		//model.curDist = diffDist;
		
		//double radius = (SQRT2 * DeadlockModel.GRID_DELTA / 2);
		double radius = 10;
		
		if (diffDist <= radius) {
			
			dx = point.x - lastRoundedPoint.y;
			dy = point.y - lastRoundedPoint.y;
			diffDist = Math.sqrt((dx * dx) + (dy * dy));
			
			model.totalDist += diffDist;
			
			model.curRoad.add(point);
			
		}
		
	}

	private void touchUp() {
		
		model.curRawPoint = null;
		model.curRoundedPoint = null;
		
		/*
		 * TODO: when done with touch, just add the rounded point,
		 * even if it woldn't have been added if the touch was continuing
		 */
		
	}
	
	public void menuClear() {
		
		model.roads.clear();
		view.invalidate();
	}
}
