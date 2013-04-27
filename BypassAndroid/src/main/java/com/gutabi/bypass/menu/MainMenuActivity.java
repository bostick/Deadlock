package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.gutabi.bypass.BypassActivity;
import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.BypassView;
import com.gutabi.bypass.PlatformImpl;
import com.gutabi.bypass.R;

public class MainMenuActivity extends BypassActivity {
	
	{
		name = "mainmenu";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mainmenu);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if (BYPASSAPP == null) {
			PlatformImpl platform = new PlatformImpl(getResources());
			try {
				BypassApplication.create(platform);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		v = (BypassView)findViewById(R.id.view_mainmenu);
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		MainMenu.create();
	}
	
	protected void onStart() {
    	super.onStart();
    	
    	MainMenu.start();
    }
	
	protected void onStop() {
		super.onStop();
		
		MainMenu.stop();
	}
	
	protected void onResume() {
    	super.onResume();
    	
    	Log.d("bypass", "resume 1");
    	
    	MainMenu.resume();
    	
    	Log.d("bypass", "resume 2");
    }
	
	protected void onSurfaceChanged(int width, int height) {
		
		Log.d("bypass", "surface 1");
		
		MainMenu.surfaceChanged(width, height);
		
		Log.d("bypass", "surface 2");
	}
	
	protected void onPause() {
		super.onPause();
		
		Log.d("bypass", "pause 1");
		
		MainMenu.pause();
		
		Log.d("bypass", "pause 2");
	}
	
}
