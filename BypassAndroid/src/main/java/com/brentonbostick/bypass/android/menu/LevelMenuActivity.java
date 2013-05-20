package com.brentonbostick.bypass.android.menu;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.lang.reflect.Constructor;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.brentonbostick.bypass.android.ActivityState;
import com.brentonbostick.bypass.android.BypassActivity;
import com.brentonbostick.bypass.android.BypassAndroidPlatform;
import com.brentonbostick.bypass.android.BypassView;
import com.brentonbostick.bypass.android.R;
import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.bypass.menu.BypassMenu;
import com.brentonbostick.bypass.menu.LevelMenu;
import com.brentonbostick.capsloc.math.Point;

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
				
				String platformImplName = (String)getIntent().getExtras().get("com.brentonbostick.bypass.android.PlatformImplClassName");
				
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
			String name = savedInstanceState.getString("com.brentonbostick.bypass.menu.LevelDBName");
			LevelMenu.levelDBName = name;
			
			LevelMenu menu = LevelMenu.map.get(name);
			
			Point panelOffset = (Point)savedInstanceState.getSerializable("com.brentonbostick.bypass.menu.LevelMenuPanelOffset");
			menu.panelOffset = panelOffset;
			
			Point loc = (Point)savedInstanceState.getSerializable("com.brentonbostick.bypass.menu.LevelMenuLoc");
			menu.setLocation(loc);
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
    	
    	BypassMenu.resume();
    }
	
	protected void onSurfaceChanged(int width, int height) {
		
		Log.d("bypassactivity", name + " surfaceChanged");
		
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
		
		outState.putString("com.brentonbostick.bypass.menu.LevelDBName", LevelMenu.levelDBName);
		Point loc = new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y);
		outState.putSerializable("com.brentonbostick.bypass.menu.LevelMenuLoc", loc);
		outState.putSerializable("com.brentonbostick.bypass.menu.LevelMenuPanelOffset", BypassMenu.BYPASSMENU.panelOffset);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.levelmenu_context_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.btn_levelmenu_clearScores) {
	    	
	    	LevelDB levelDB = BYPASSAPP.bypassPlatform.levelDB(LevelMenu.levelDBName);
	    	
			BYPASSAPP.bypassPlatform.clearScores(levelDB);
			return true;
		} else if (item.getItemId() == R.id.btn_levelmenu_toggleInfo) {
			((LevelMenu)BypassMenu.BYPASSMENU).showInfo = !((LevelMenu)BypassMenu.BYPASSMENU).showInfo;
			Point loc = new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y);
			BypassMenu.BYPASSMENU.lock.lock();
			try {
				
				BypassMenu.BYPASSMENU.render();
				BypassMenu.BYPASSMENU.setLocation(loc);
				
			} finally {
				BypassMenu.BYPASSMENU.lock.unlock();
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
