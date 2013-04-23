package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.gutabi.bypass.BypassActivity;
import com.gutabi.bypass.R;

public class LevelMenuActivity extends BypassActivity {
	
	{
		name = "levelmenu";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_levelmenu);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		v = (LevelMenuView)findViewById(R.id.view_levelmenu);
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		LevelMenu.create();
		
	}
	
	protected void onStart() {
    	super.onStart();
    	
    	LevelMenu.start();
    }
	
	protected void onStop() {
		super.onStop();
		
		LevelMenu.stop();
	}
	
	protected void onResume() {
    	super.onResume();
    	
    	LevelMenu.resume();
    }
	
	protected void onPause() {
		super.onPause();
		
		LevelMenu.pause();
	}
	
//	int[] touchOut = new int[2];
//	
//	public boolean onTouchEvent(MotionEvent ev) {
//		
//		v.getLocationInWindow(touchOut);
//		
//		int act = ev.getAction();
//		
//		float x = ev.getRawX() - touchOut[0];
//		float y = ev.getRawY() - touchOut[1];
//		Point p = new Point(x, y);
//		
//		switch (act) {
//		case MotionEvent.ACTION_DOWN:
////			Log.d("motion", "down " + p);
//			APP.appScreen.contentPane.pcp.pressedDriver(p);
//			break;
//		case MotionEvent.ACTION_MOVE:
////			Log.d("motion", "move " + p);
//			APP.appScreen.contentPane.pcp.draggedDriver(p);
//			break;
//		case MotionEvent.ACTION_UP:
////			Log.d("motion", "up " + p);
//			APP.appScreen.contentPane.pcp.releasedDriver(p);
//			break;
//		}
//		
//		gDetector.onTouchEvent(ev);
//		
//		return true;
//	}
//	
//	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener(){
//		
//		public boolean onSingleTapUp(MotionEvent ev) {
//			
//			v.getLocationInWindow(touchOut);
//			
//			float x = ev.getRawX() - touchOut[0];
//			float y = ev.getRawY() - touchOut[1];
//			Point p = new Point(x, y);
//			
//			APP.appScreen.contentPane.pcp.clickedDriver(p);
//			
//			return super.onSingleTapUp(ev);
//		}
//	};
	
}
