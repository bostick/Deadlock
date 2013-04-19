package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.gutabi.deadlock.math.Point;

public class BypassActivity extends Activity {
	
	public BypassView v;
	public ActivityState state;
	
	protected String name;
	
	private GestureDetector gDetector;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("bypassactivity", name + " create");
		state = ActivityState.CREATE;
		
		PlatformImpl.CURRENTACTIVITY = this;
		
		gDetector = new GestureDetector(this, simpleOnGestureListener);
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
		
		Log.d("bypassactivity", name + " destroy");
		state = ActivityState.DESTROY;
		
//		PlatformImpl.CURRENTACTIVITY = this;
		
	}
	
	protected void onStart() {
		super.onStart();
		
		Log.d("bypassactivity", name + " start");
		state = ActivityState.START;
		
		PlatformImpl.CURRENTACTIVITY = this;
		
	}
    
	protected void onStop() {
		super.onStop();
		
		Log.d("bypassactivity", name + " stop");
		state = ActivityState.STOP;
		
//		PlatformImpl.CURRENTACTIVITY = this;
		
	}
	
	protected void onRestart() {
		super.onRestart();
		
		Log.d("bypassactivity", name + " restart");
		state = ActivityState.RESTART;
		
//		PlatformImpl.CURRENTACTIVITY = this;
		
	}
	
	protected void onResume() {
		super.onResume();
		
		Log.d("bypassactivity", name + " resume");
		state = ActivityState.RESUME;
		
		PlatformImpl.CURRENTACTIVITY = this;
		
//		Animation a = v.getAnimation();
//		a.reset();
		
	}
	
	protected void onPause() {
		super.onResume();
		
		Log.d("bypassactivity", name + " pause");
		state = ActivityState.PAUSE;
		
//		PlatformImpl.CURRENTACTIVITY = this;
		
		PlatformImpl.CURRENTACTIVITY.overridePendingTransition(0, 0);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	int[] touchOut = new int[2];
	
	public boolean onTouchEvent(MotionEvent ev) {
		
		v.getLocationInWindow(touchOut);
		
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
	
	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener(){
		
		public boolean onSingleTapUp(MotionEvent ev) {
			
			v.getLocationInWindow(touchOut);
			
			float x = ev.getRawX() - touchOut[0];
			float y = ev.getRawY() - touchOut[1];
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.pcp.clickedDriver(p);
			
			return super.onSingleTapUp(ev);
		}
	};
	
}
