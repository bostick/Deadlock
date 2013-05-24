package com.brentonbostick.bypass.android.lite;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.brentonbostick.bypass.android.BypassActivity;
import com.brentonbostick.bypass.android.BypassAndroidPlatform;
import com.brentonbostick.bypass.android.BypassView;
import com.brentonbostick.bypass.menu.BypassMenu;
import com.brentonbostick.bypass.menu.MainMenu;
import com.brentonbostick.capsloc.math.Point;

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
				
				BypassAndroidPlatform platform = new BypassAndroidLitePlatform(getResources());
				try {
					platform.createApplication();
				} catch (Exception e) {
					Log.e("bypass", e.getMessage(), e);
				}
				
			} catch (Exception e) {
				Log.e("bypass", e.getMessage(), e);
			}
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
		
		BypassMenu.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		BypassMenu.pause();
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("com.brentonbostick.bypass.menu.MainMenuLoc", new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y));
		outState.putSerializable("com.brentonbostick.bypass.menu.MainMenuPanelOffset", BypassMenu.BYPASSMENU.panelOffset);
	}
	
}
