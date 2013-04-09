package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.Window;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;

public class MainActivity extends Activity {
	
	public static MainActivity MAINACTIVITY;
	
	private GestureDetector gDetector;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MAINACTIVITY = this;
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		gDetector = new GestureDetector(this, simpleOnGestureListener);
		
		MainView v = (MainView)findViewById(R.id.deadlock);
		
		PlatformImpl platform = new PlatformImpl(getResources(), v);
		APP.platform = platform;
		
		try {
			APP.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	int[] touchOut = new int[2];
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		MainView v = (MainView)findViewById(R.id.deadlock);
		
		v.getLocationInWindow(touchOut);
		
		int act = ev.getAction();
		
		float x = ev.getRawX() - touchOut[0];
		float y = ev.getRawY() - touchOut[1];
		Point p = new Point(x, y);
		
		switch (act) {
		case MotionEvent.ACTION_DOWN:
			Log.d("motion", "down");
			APP.appScreen.contentPane.pressed(new InputEvent(p));
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("motion", "move");
			APP.appScreen.contentPane.dragged(new InputEvent(p));
			break;
		case MotionEvent.ACTION_UP:
			Log.d("motion", "up");
			APP.appScreen.contentPane.released(new InputEvent(p));
			break;
		}
		
		gDetector.onTouchEvent(ev);
		
		return true;
	}
	
	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener(){
		
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			
			MainView v = (MainView)findViewById(R.id.deadlock);
			
			v.getLocationInWindow(touchOut);
			
			float x = ev.getRawX() - touchOut[0];
			float y = ev.getRawY() - touchOut[1];
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.clicked(new InputEvent(p));
			
			return super.onSingleTapUp(ev);
		}
	};
	
}
