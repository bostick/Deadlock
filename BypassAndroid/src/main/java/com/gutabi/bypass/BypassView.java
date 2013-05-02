package com.gutabi.bypass;

import static com.gutabi.capsloc.CapslocApplication.APP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Paintable;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class BypassView extends SurfaceView implements SurfaceHolder.Callback {
	
	public BypassActivity activity;
	
	public SurfaceHolder holder;
	public boolean surfaceValid;
	
	public Paintable paintable;
	
	public BypassView(Context c, AttributeSet s) {
		super(c, s);
		
		holder = getHolder();

		holder.addCallback(this);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceValid = false;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		surfaceValid = true;
		
		activity.onSurfaceChanged(width, height);
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		
		int act = ev.getAction() & MotionEvent.ACTION_MASK;
		
		switch (act) {
		case MotionEvent.ACTION_DOWN: {
			
			float x = ev.getX();
			float y = ev.getY();
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.pcp.pressedDriver(p);
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			
			float x = ev.getX();
			float y = ev.getY();
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.pcp.draggedDriver(p);
			break;
		}
		case MotionEvent.ACTION_UP: {
			
			float x = ev.getX();
			float y = ev.getY();
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.pcp.releasedDriver(p);
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN:
			
			break;
		case MotionEvent.ACTION_POINTER_UP:
			
			break;
		default:
			throw new AssertionError(Integer.toString(act));
		}
		
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
