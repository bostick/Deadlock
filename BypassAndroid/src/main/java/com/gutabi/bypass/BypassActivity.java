package com.gutabi.bypass;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public abstract class BypassActivity extends Activity {
	
	public BypassView v;
	public ActivityState state;
	
	protected String name;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("bypassactivity", name + " create");
		state = ActivityState.CREATE;
		
		PlatformImpl.CURRENTACTIVITY = this;
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
		
		Log.d("bypassactivity", name + " destroy");
		state = ActivityState.DESTROY;
		
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
		
	}
	
	protected void onRestart() {
		super.onRestart();
		
		Log.d("bypassactivity", name + " restart");
		state = ActivityState.RESTART;
		
	}
	
	protected void onResume() {
		super.onResume();
		
		Log.d("bypassactivity", name + " resume");
		state = ActivityState.RESUME;
		
		PlatformImpl.CURRENTACTIVITY = this;
		
	}
	
	protected void onPause() {
		super.onResume();
		
		Log.d("bypassactivity", name + " pause");
		state = ActivityState.PAUSE;
		
		PlatformImpl.CURRENTACTIVITY.overridePendingTransition(0, 0);
		
	}
	
	protected abstract void onSurfaceChanged(int width, int height);
	
}
