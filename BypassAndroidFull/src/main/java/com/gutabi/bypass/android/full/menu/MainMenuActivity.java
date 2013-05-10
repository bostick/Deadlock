package com.gutabi.bypass.android.full.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.android.ActivityState;
import com.gutabi.bypass.android.BypassActivity;
import com.gutabi.bypass.android.BypassView;
import com.gutabi.bypass.android.PlatformImpl;
import com.gutabi.bypass.android.full.R;
import com.gutabi.bypass.menu.BypassMenu;
import com.gutabi.bypass.menu.MainMenu;

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
		
		APP.DEBUG_DRAW = false;
		
		BypassMenu.create();
	}
	
	protected void onStart() {
    	super.onStart();
    	
    	BypassMenu.start();
    }
	
	protected void onStop() {
		super.onStop();
		
		BypassMenu.stop();
	}
	
	protected void onResume() {
    	super.onResume();
    	
    	MainMenu.resume();
    }
	
	protected void onSurfaceChanged(int width, int height) {
		
		Log.d("bypassactivity", name + " surfaceChanged " + width + " " + height);
		
		if (state == ActivityState.PAUSE) {
			/*
			 * locking the screen causes surface change after pause
			 */
			return;
		}
		
		MainMenu.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		MainMenu.pause();
	}
	
}