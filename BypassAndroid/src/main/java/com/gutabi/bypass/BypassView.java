package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Paintable;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class BypassView extends SurfaceView implements SurfaceHolder.Callback {
	
	public BypassActivity activity;
	
	public SurfaceHolder holder;
	
	private GestureDetector gDetector;
	
	public Paintable paintable;
	
	public BypassView(Context c, AttributeSet s) {
		super(c, s);
		
		gDetector = new GestureDetector(c, simpleOnGestureListener);
		
		holder = getHolder();

		holder.addCallback(this);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
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
			APP.appScreen.contentPane.pcp.pressedDriver(p);
			break;
		case MotionEvent.ACTION_MOVE:
			APP.appScreen.contentPane.pcp.draggedDriver(p);
			break;
		case MotionEvent.ACTION_UP:
			APP.appScreen.contentPane.pcp.releasedDriver(p);
			break;
		}
		
		gDetector.onTouchEvent(ev);
		
		return true;
	}

	Paint paint = new Paint();
	public RenderingContext ctxt;
	
	public void doDraw(Canvas canvas) {
		
		if (activity.state != ActivityState.PAUSE) {
			
			APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
			
			paintable.paint(ctxt);
			
		} else {
			
		}
		
	}
	
}
