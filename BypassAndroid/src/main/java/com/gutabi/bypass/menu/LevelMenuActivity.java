package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gutabi.bypass.ActivityState;
import com.gutabi.bypass.BypassActivity;
import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.BypassView;
import com.gutabi.bypass.PlatformImpl;
import com.gutabi.bypass.R;
import com.gutabi.capsloc.math.Point;

public class LevelMenuActivity extends BypassActivity {
	
	{
		name = "levelmenu";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_levelmenu);
		
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
		
		if (savedInstanceState != null) {
			String name = savedInstanceState.getString("com.gutabi.bypass.menu.LevelDB");
			if (name.equals("tutorial")) {
				LevelMenu.levelDB = BYPASSAPP.tutorialLevelDB;
			} else if (name.equals("episode1")) {
				LevelMenu.levelDB = BYPASSAPP.episode1LevelDB;
			} else if (name.equals("episode2")) {
				LevelMenu.levelDB = BYPASSAPP.episode2LevelDB;
			} else {
				throw new AssertionError();
			}
			
			Point loc = (Point)savedInstanceState.getSerializable("com.gutabi.bypass.menu.LevelMenuLoc");
			LevelMenu.levelDB.loc = loc;
		}
		
		v = (BypassView)findViewById(R.id.view_levelmenu);
		
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
	
	protected void onSurfaceChanged(int width, int height) {
		
		Log.d("bypassactivity", name + " surfaceChanged");
		
		if (state == ActivityState.PAUSE) {
			/*
			 * locking the screen causes surface change after pause
			 */
			return;
		}
		
		LevelMenu.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		LevelMenu.pause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString("com.gutabi.bypass.menu.LevelDB", LevelMenu.levelDB.name);
		outState.putSerializable("com.gutabi.bypass.menu.LevelMenuLoc", new Point(LevelMenu.LEVELMENU.aabb.x, LevelMenu.LEVELMENU.aabb.y));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.levelmenu_context_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.btn_clear_scores:
	        	BYPASSAPP.bypassPlatform.clearScores(LevelMenu.levelDB);
	            return true;
	        case R.id.btn_toggle_info:
	        	LevelMenu.showInfo = !LevelMenu.showInfo;
	        	
	        	LevelMenu.LEVELMENU.lock.lock();
	        	
	        	LevelMenu.LEVELMENU.render();
	    		
	        	LevelMenu.LEVELMENU.lock.unlock();
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
