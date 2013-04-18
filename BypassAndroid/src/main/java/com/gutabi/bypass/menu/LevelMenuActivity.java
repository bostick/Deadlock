package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;

import com.gutabi.bypass.PlatformImpl;
import com.gutabi.bypass.R;
import com.gutabi.deadlock.math.Point;

public class LevelMenuActivity extends Activity {
	
	public LevelMenuView v;
	
	private GestureDetector gDetector;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("levelmenu", "create");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_levelmenu);
		
		PlatformImpl.CURRENTACTIVITY = this;
		
		gDetector = new GestureDetector(this, simpleOnGestureListener);
		
		v = (LevelMenuView)findViewById(R.id.view_levelmenu);
		
		v.ctxt = APP.platform.createRenderingContext();
		
	}
	
	protected void onStart() {
		super.onStart();
		
		Log.d("levelmenu", "start");
		
		PlatformImpl.CURRENTACTIVITY = this;
		
		LevelMenu.action();
	}
    
    protected void onRestart() {
    	super.onRestart();
    	
    	Log.d("levelmenu", "restart");
    }

    protected void onResume() {
    	super.onResume();
    	
    	Log.d("levelmenu", "resume");
    }

    protected void onPause() {
    	super.onPause();
    	
    	Log.d("levelmenu", "pause");
    }

    protected void onStop() {
    	super.onStop();
    	
    	Log.d("levelmenu", "stop");
    }

    protected void onDestroy() {
    	super.onDestroy();
    	
    	Log.d("levelmenu", "destroy");
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
