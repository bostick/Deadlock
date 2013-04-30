package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;
import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
			double[] a = savedInstanceState.getDoubleArray("com.gutabi.bypass.menu.LevelMenuLoc");
			Point loc = new Point(a[0], a[1]);
			LevelMenu.loc = loc;
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
		
		LevelMenu.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		LevelMenu.pause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		double[] a = new double[] { LevelMenu.loc.x, LevelMenu.loc.y };
		
		outState.putDoubleArray("com.gutabi.bypass.menu.LevelMenuLoc", a);
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
	        	BYPASSAPP.bypassPlatform.clearScores();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
