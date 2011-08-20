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
	
	
	/*
	 * round a to nearest multiple of b
	 */
	static float round(float a, int b) {
		return Math.round(a / b) * b;
	}
	
	/*
	 * round coords from event to nearest multiple of:
	 */
	int roundingFactor = 40;
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		x = round(x, roundingFactor);
		y = round(y, roundingFactor);
		
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
		
		PointF a = new PointF(x, y);
		
		//outer:
		for (List<PointF> seg : model.roadSegments) {
			PointF c = null;
			//inner:
			for (PointF d : seg) {
				try {
					if (c == null) {
						if (intersection(a, d)) {
							PointF inter = a;
							Log.d("touch", String.format("INTR <%f, %f>   <%f, %f> <%f, %f>", inter.x, inter.y, a.x, a.y, d.x, d.y));
							Log.d("touch test", String.format("TEST 3 <%f, %f>   <%f, %f> <%f, %f>", inter.x, inter.y, a.x, a.y, d.x, d.y));
							assert !model.intersContains(inter.x, inter.y) : "inters already contains <" + inter.x + ", " + inter.y + ">";
							model.inters.add(inter);
						}
					} else {
						if (intersection(a, c, d)) {
							PointF inter = a;
							if (!(inter.equals(c.x, c.y))) {
								Log.d("touch", String.format("INTR <%f, %f>   <%f, %f> <<%f, %f>, <%f, %f>>", inter.x, inter.y, a.x, a.y, c.x, c.y, d.x, d.y));
								if (inter.equals(d.x, d.y)) {
									PointF last = seg.get(seg.size() - 1);
									if (d == last) {
										Log.d("touch test", String.format("TEST 4 <%f, %f>   <%f, %f> <<%f, %f>, <%f, %f>>", inter.x, inter.y, a.x, a.y, c.x, c.y, d.x, d.y));
									} else {
										Log.d("touch test", String.format("TEST 1 <%f, %f>   <%f, %f> <<%f, %f>, <%f, %f>>", inter.x, inter.y, a.x, a.y, c.x, c.y, d.x, d.y));
									}
								} else {
									Log.d("touch test", String.format("TEST 2 <%f, %f>   <%f, %f> <<%f, %f>, <%f, %f>>", inter.x, inter.y, a.x, a.y, c.x, c.y, d.x, d.y));
								}
								assert !model.intersContains(inter.x, inter.y) : "inters already contains <" + inter.x + ", " + inter.y + ">";
								model.inters.add(inter);
							}
						}
					}
				} finally {
					c = d;
				}
			}
		}
		
		List<PointF> seg = new ArrayList<PointF>();
		seg.add(a);
		model.curPoint = a;
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
			 * gotcha: sometimes the first move after down is the same point
			 * this will help filter out those repeated points
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
			PointF c = null;
			//inner:
			for (PointF d : seg) {
				try {
					if (c == null) {
						/*
						 * start of segment
						 */
						
					} else {
						PointF inter = intersection(a, b, c, d);
						if (inter != null) {
							if (!(inter.equals(a.x, a.y) || inter.equals(c.x, c.y))) {
								/*
								 * gotcha: the intersection could be equal to a, so ignore since it was equal to b of last segment
								 * 			the intersection could be equal to c, so ignore since it was equal to d of last segment
								 */
								Log.d("touch",
										String.format(
												"INTR <%f, %f>   <<%f, %f>, <%f, %f>> <<%f, %f>, <%f, %f>>",
												x, y, a.x, a.y, b.x, b.y, c.x,
												c.y, d.x, d.y));
								assert !model.intersContains(inter.x, inter.y) : "inters already contains <" + inter.x + ", " + inter.y + ">";
								model.inters.add(inter);
								/*
								 * gotcha: a single move could intersect with more than one segment, so don't break after first intersection
								 */
								//break outer;
							}
						}
					}
				} finally {
					c = d;
				}
			}
		}
		
		model.curPoint = b;
		model.curSeg.add(b);
		
	}

	private void touchUp() {
		
		model.curPoint = null;
		
	}
	
	static boolean intersection(PointF a, PointF c) {
		return ((a.x == c.x) && (a.y == c.y));
	}
	
	static boolean intersection(PointF a, PointF c, PointF d) {
		float xac = (a.x - c.x);
		float xdc = (d.x - c.x);
		float yac = (a.y - c.y);
		float ydc = (d.y - c.y);
		float denom = (xdc * xdc + ydc * ydc);
		float u = ((xac * xdc) + (yac * ydc)) / denom;
		if (0 <= u && u <= 1) {
			float x = c.x + u * xdc;
			float y = c.y + u * ydc;
			return ((a.x == x) && (a.y == y));
		} else {
			return false;
		}
	}
	
	static PointF intersection(PointF a, PointF b, PointF c, PointF d) {
		if (a.equals(b.x, b.y)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (c.equals(d.x, d.y)) {
			throw new IllegalArgumentException("c and d are equal");
		}
		float ydc = (d.y - c.y);
		float xba = (b.x - a.x);
		float xdc = (d.x - c.x);
		float yba = (b.y - a.y);
		float yac = (a.y - c.y);
		float xac = (a.x - c.x);
		float denom = (ydc * xba) - (xdc * yba);
		float u12 = ((xdc * yac) - (ydc * xac)) / denom;
		float ucd = ((xba * yac) - (yba * xac)) / denom;
		if ((0 <= u12 && u12 <= 1) && (0 <= ucd && ucd <= 1)) {
			float x = a.x + u12 * xba;
			float y = a.y + u12 * yba;
			return new PointF(x, y);
		} else {
			return null;
		}
	}
}
