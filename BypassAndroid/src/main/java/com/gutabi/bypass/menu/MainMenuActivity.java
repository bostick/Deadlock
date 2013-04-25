package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.gutabi.bypass.BypassActivity;
import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.BypassView;
import com.gutabi.bypass.PlatformImpl;
import com.gutabi.bypass.R;
import com.gutabi.bypass.level.Level;

public class MainMenuActivity extends BypassActivity {
	
	{
		name = "mainmenu";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_mainmenu);
		
		BypassApplication app = new BypassApplication();
		APP = app;
		BYPASSAPP = app;
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		v = (BypassView)findViewById(R.id.view_mainmenu);
		
		PlatformImpl platform = new PlatformImpl(getResources());
		APP.platform = platform;
		BYPASSAPP.bypassPlatform = platform;
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		try {
			
			APP.DEBUG_DRAW = false;
			
			BYPASSAPP.init();
			
			SharedPreferences grades = getSharedPreferences("grades", 0);
			
			for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
				String grade = grades.getString(Integer.toString(i), null);
				if (grade != null) {
					Level l = BYPASSAPP.levelDB.readLevel(i);
					l.isWon = true;
					l.grade = grade;
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
    	
    	MainMenu.resume();
    }
	
	protected void onPause() {
		super.onPause();
		
		MainMenu.pause();
	}
	
}
