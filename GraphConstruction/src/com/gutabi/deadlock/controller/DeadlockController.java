package com.gutabi.deadlock.controller;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.util.Log;
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
		
		x = Math.round(x / 10) * 10;
		y = Math.round(y / 10) * 10;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			touchStart(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "MOVE <"+x+","+y+">");
			touchMove(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "UP   <"+x+","+y+">");
			touchUp();
			view.invalidate();
			break;
		}
		return true;
	}
	
	private void touchStart(final float x, final float y) {
		
		PointF point = new PointF(x, y);
		model.curPoint = point;
		
		//point = DeadlockModel.roundToGrid(point);
		
		//model.curRoundedPoint = point;
		
		List<PointF> seg = new ArrayList<PointF>();
		seg.add(point);
		
		model.curSeg = seg;
		model.roadSegments.add(seg);
	}
	
	private void touchMove(float x, float y) {
		
		PointF a = model.curPoint;
		
		PointF b = new PointF(x, y);
		
		/*
		 * TODO: not sure if PointF.equals(PointF) is defined
		 */
		if (a.equals(x, y)) {
			/*
			 * gotcha: sometimes the first move after a down event is the same point
			 * this will help filter out repeated points
			 */
			return;
		}
		
		//outer:
		for (List<PointF> seg : model.roadSegments) {
			/*
			 * <a, b> is the segment currently being drawn/moved
			 * <c, d> is the segment to be tested against
			 */
			/*
			 * gotcha: make sure that c and d are nulled at the beginning of each outer loop
			 */
			PointF c = null, d = null;
			for (PointF point : seg) {
				d = point;
				if (a == d) {
					/*
					 * gotcha: yes, technically the last segment drawn (which shares a point with the current segment) intersects,
					 * but we don't want to count that obviously
					 */
					continue;
				}
				if (c != null) {
					PointF inter = intersection(a, b, c, d);
					if (inter != null) {
						if (!(inter.equals(a.x, a.y))) {
							/*
							 * gotcha: the intersection could be the end point of last segment and start point of this segment
							 * only count it once
							 */
							Log.d("touch", String.format("INTR <%f, %f>   <<%f, %f>, <%f, %f>> <<%f, %f>, <%f, %f>>", x, y, a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y));
							model.inters.add(inter);
							/*
							 * gotcha: a single move could intersect with more than one segment, so don't break after first intersection
							 */
							//break outer;
						}
					}
				}
				c = d;
			}
		}
		
		model.curPoint = b;
		model.curSeg.add(b);
		
	}

	private void touchUp() {
		
		model.curPoint = null;
		
	}
	
	static PointF intersection(PointF a, PointF b, PointF c, PointF d) {
		float y43 = (d.y - c.y);
		float x21 = (b.x - a.x);
		float x43 = (d.x - c.x);
		float y21 = (b.y - a.y);
		float y13 = (a.y - c.y);
		float x13 = (a.x - c.x);
		float denom = (y43 * x21) - (x43 * y21);
		float u12 = ((x43 * y13) - (y43 * x13)) / denom;
		float u34 = ((x21 * y13) - (y21 * x13)) / denom;
		if ((0 <= u12 && u12 <= 1) && (0 <= u34 && u34 <= 1)) {
			float x = a.x + u12 * (b.x - a.x);
			float y = a.y + u12 * (b.y - a.y);
			return new PointF(x, y);
		} else {
			return null;
		}
	}
}
