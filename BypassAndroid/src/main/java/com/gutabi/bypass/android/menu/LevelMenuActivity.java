package com.gutabi.bypass.android.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import java.lang.reflect.Constructor;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gutabi.bypass.android.ActivityState;
import com.gutabi.bypass.android.BypassActivity;
import com.gutabi.bypass.android.BypassAndroidPlatform;
import com.gutabi.bypass.android.BypassView;
import com.gutabi.bypass.android.R;
import com.gutabi.bypass.menu.BypassMenu;
import com.gutabi.bypass.menu.LevelMenu;
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
			try {
				
				String platformImplName = (String)getIntent().getExtras().get("com.gutabi.bypass.android.PlatformImplClassName");
				
				Class<?> cArg = Class.forName(platformImplName);
				Constructor<?> platformCtor = cArg.getConstructor(Resources.class);
				
				BypassAndroidPlatform platform = (BypassAndroidPlatform)platformCtor.newInstance(getResources());
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
			String name = savedInstanceState.getString("com.gutabi.bypass.menu.LevelDB");
			LevelMenu.levelDB = BYPASSAPP.bypassPlatform.levelDB(name);
			
			Point loc = (Point)savedInstanceState.getSerializable("com.gutabi.bypass.menu.LevelMenuLoc");
			LevelMenu.levelDB.loc = loc;
		}
		
		v = (BypassView)findViewById(R.id.view_levelmenu);
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		LevelMenu.create();
	}
	
	protected void onDestroy() {
    	super.onDestroy();
    	
    	LevelMenu.destroy();
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
		
		if (state != ActivityState.RESUME) {
			/*
			 * locking the screen causes surface change after pause, so ignore that
			 */
			return;
		}
		
		LevelMenu.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		LevelMenu.pause();
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString("com.gutabi.bypass.menu.LevelDB", LevelMenu.levelDB.resourceName);
		outState.putSerializable("com.gutabi.bypass.menu.LevelMenuLoc", new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y));
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.levelmenu_context_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.btn_clear_scores) {
			BYPASSAPP.bypassPlatform.clearScores(LevelMenu.levelDB);
			return true;
		} else if (item.getItemId() == R.id.btn_toggle_info) {
			LevelMenu.showInfo = !LevelMenu.showInfo;
			Point loc = new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y);
			BypassMenu.BYPASSMENU.lock.lock();
			BypassMenu.BYPASSMENU.render();
			BypassMenu.BYPASSMENU.setLocation(loc);
			BypassMenu.BYPASSMENU.lock.unlock();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
