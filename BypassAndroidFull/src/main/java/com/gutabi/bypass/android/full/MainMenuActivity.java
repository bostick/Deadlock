package com.gutabi.bypass.android.full;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.gutabi.bypass.android.ActivityState;
import com.gutabi.bypass.android.BypassActivity;
import com.gutabi.bypass.android.BypassAndroidPlatform;
import com.gutabi.bypass.android.BypassView;
import com.gutabi.bypass.menu.BypassMenu;
import com.gutabi.bypass.menu.MainMenu;
import com.gutabi.capsloc.math.Point;

public class MainMenuActivity extends BypassActivity {
	
	{
		name = "mainmenu";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mainmenu);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if (BYPASSAPP == null) {
			try {
				
				BypassAndroidPlatform platform = new BypassAndroidFullPlatform(getResources());
				try {
					platform.createApplication();
				} catch (Exception e) {
					Log.e("bypass", e.getMessage(), e);
				}
				
			} catch (Exception e) {
				Log.e("bypass", e.getMessage(), e);
			}
		}
		
		if (savedInstanceState != null) {
			
			Point loc = (Point)savedInstanceState.getSerializable("com.gutabi.bypass.menu.MainMenuLoc");
			BypassMenu.tmpLoc = loc;
			
			Point panelOffset = (Point)savedInstanceState.getSerializable("com.gutabi.bypass.menu.MainMenuPanelOffset");
			BypassMenu.tmpPanelOffset = panelOffset;
		}
		
		v = (BypassView)findViewById(R.id.view_mainmenu);
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		APP.DEBUG_DRAW = false;
		
		MainMenu.create();
	}
	
	protected void onDestroy() {
    	super.onDestroy();
    	
    	MainMenu.destroy();
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
    	
    	BypassMenu.resume();
    }
	
	protected void onSurfaceChanged(int width, int height) {
		
		Log.d("bypassactivity", name + " surfaceChanged " + width + " " + height);
		
		if (state != ActivityState.RESUME) {
			/*
			 * locking the screen causes surface change after pause, so ignore that
			 */
			return;
		}
		
		BypassMenu.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		BypassMenu.pause();
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("com.gutabi.bypass.menu.MainMenuLoc", new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y));
		outState.putSerializable("com.gutabi.bypass.menu.MainMenuPanelOffset", BypassMenu.BYPASSMENU.panelOffset);
	}
	
}
