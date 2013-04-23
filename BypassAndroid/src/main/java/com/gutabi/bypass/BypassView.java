package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Paintable;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class BypassView extends View {
	
	public BypassActivity activity;
	
	private GestureDetector gDetector;
	
	public Paintable paintable;
	
	public BypassView(Context c, AttributeSet s) {
		super(c, s);
		
		gDetector = new GestureDetector(c, simpleOnGestureListener);
	}
	
	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
		
		public boolean onSingleTapUp(MotionEvent ev) {
			
			getLocationInWindow(touchOut);
			
			float x = ev.getRawX() - touchOut[0];
			float y = ev.getRawY() - touchOut[1];
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.pcp.clickedDriver(p);
			
			return super.onSingleTapUp(ev);
		}
		
//		public boolean onLongClick(MotionEvent ev) {
//			
//			v.getLocationInWindow(touchOut);
//			
//			float x = ev.getRawX() - touchOut[0];
//			float y = ev.getRawY() - touchOut[1];
//			Point p = new Point(x, y);
//			
//			APP.appScreen.contentPane.pcp.longPressDriver(p);
//			
//			return super.onLo
//		}
	};
	
	
	int[] touchOut = new int[2];
	
	public boolean onTouchEvent(MotionEvent ev) {
		
		getLocationInWindow(touchOut);
		
		int act = ev.getAction();
		
		float x = ev.getRawX() - touchOut[0];
		float y = ev.getRawY() - touchOut[1];
		Point p = new Point(x, y);
		
		switch (act) {
		case MotionEvent.ACTION_DOWN:
//			Log.d("motion", "down " + p);
			APP.appScreen.contentPane.pcp.pressedDriver(p);
			break;
		case MotionEvent.ACTION_MOVE:
//			Log.d("motion", "move " + p);
			APP.appScreen.contentPane.pcp.draggedDriver(p);
			break;
		case MotionEvent.ACTION_UP:
//			Log.d("motion", "up " + p);
			APP.appScreen.contentPane.pcp.releasedDriver(p);
			break;
		}
		
		gDetector.onTouchEvent(ev);
		
		return true;
	}

	Paint paint = new Paint();
	public RenderingContext ctxt;
	
	public void onDraw(Canvas canvas) {
		
		if (activity.state != ActivityState.PAUSE) {
			
			APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
			
			paintable.paint(ctxt);
			
		} else {
			
//			APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
//			
//			paintable.paint(ctxt);
			
		}
		
	}
	
}
