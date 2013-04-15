package com.gutabi.bypass;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.R;
import com.gutabi.deadlock.math.Point;

public class MainActivity extends Activity {
	
	public static MainActivity MAINACTIVITY;
	
	private GestureDetector gDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		BypassApplication app = new BypassApplication();
		APP = app;
		BYPASSAPP = app;
		
		MAINACTIVITY = this;
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		gDetector = new GestureDetector(this, simpleOnGestureListener);
		
		MainView v = (MainView)findViewById(R.id.bypass);
		
		PlatformImpl platform = new PlatformImpl(getResources(), v);
		APP.platform = platform;
		
		try {
			APP.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	int[] touchOut = new int[2];
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		MainView v = (MainView)findViewById(R.id.bypass);
		
		v.getLocationInWindow(touchOut);
		
		int act = ev.getAction();
		
		float x = ev.getRawX() - touchOut[0];
		float y = ev.getRawY() - touchOut[1];
		Point p = new Point(x, y);
		
		switch (act) {
		case MotionEvent.ACTION_DOWN:
			Log.d("motion", "down " + p);
			APP.appScreen.contentPane.pcp.pressedDriver(p);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("motion", "move " + p);
			APP.appScreen.contentPane.pcp.draggedDriver(p);
			break;
		case MotionEvent.ACTION_UP:
			Log.d("motion", "up " + p);
			APP.appScreen.contentPane.pcp.releasedDriver(p);
			break;
		}
		
		gDetector.onTouchEvent(ev);
		
		return true;
	}
	
	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener(){
		
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			
			MainView v = (MainView)findViewById(R.id.bypass);
			
			v.getLocationInWindow(touchOut);
			
			float x = ev.getRawX() - touchOut[0];
			float y = ev.getRawY() - touchOut[1];
			Point p = new Point(x, y);
			
			APP.appScreen.contentPane.pcp.clickedDriver(p);
			
			return super.onSingleTapUp(ev);
		}
	};
	
}