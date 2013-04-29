package com.gutabi.bypass.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;
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
import com.gutabi.deadlock.ui.UIAnimationRunnable;
import com.gutabi.deadlock.world.SimulationRunnable;
import com.gutabi.deadlock.world.WorldMode;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;

public class BypassWorldActivity extends BypassActivity {
	
	{
		name = "bypassworld";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bypassworld);
		
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
		
		v = (BypassView)findViewById(R.id.view_bypassworld);
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		int ii = (Integer)getIntent().getExtras().get("com.gutabi.bypass.level.Index");
		
		BypassWorld.create(ii);
		
		if (savedInstanceState != null) {
			
			get curLevel.userMoves
			get userStartTime
			
			get isWon
			
			if (isWon) {
				get userTime
				get grade
			}
			
			
			get movingCar.coastingVel;
		
			for (Car c : carMap.cars) {
				
				get c.center
				get c.angle
				
				get c.state;
				
			}
		
		}
		
	}
	
	protected void onStart() {
    	super.onStart();
    	
		BypassWorld.start();
    }
	
	protected void onStop() {
		super.onStop();
		
		BypassWorld.stop();
	}
	
	protected void onResume() {
    	super.onResume();
    	
		BypassWorld.resume();
    }
	
	protected void onSurfaceChanged(int width, int height) {
		
		BypassWorld.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		BypassWorld.pause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		BypassWorld.BYPASSWORLD.trigger.set(false);
		
		BypassWorld.BYPASSWORLD.mode = WorldMode.EDITING;
		
		try {
			BypassWorld.BYPASSWORLD.uiThread.join();
			
			BypassWorld.BYPASSWORLD.simThread.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		"com.gutabi.bypass.level.Index"
		
		
		put curLevel.userMoves
		put userStartTime
		
		put isWon
		
		if (isWon) {
			put userTime
			put grade
		}
		
		BypassCarTool tool = (BypassCarTool)APP.tool;
		
		BypassCar movingCar = tool.car;
		if (movingCar != null) {
			put movingCar.coastingVel;
		}
		
		for (Car c : carMap.cars) {
			
			put c.center
			put c.angle
			
			put c.state;
			
		}
		
		
		
		
		
		BypassWorld.BYPASSWORLD.mode = WorldMode.RUNNING;
		
		BypassWorld.BYPASSWORLD.simThread = new Thread(new SimulationRunnable());
		BypassWorld.BYPASSWORLD.simThread.start();
		
		BypassWorld.BYPASSWORLD.trigger.set(true);
		
		BypassWorld.BYPASSWORLD.uiThread = new Thread(new UIAnimationRunnable(BypassWorld.BYPASSWORLD.trigger));
		BypassWorld.BYPASSWORLD.uiThread.start();
		
	}
	
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		
//		int x;
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.world_context_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.btn_reset_level:
	        	BypassWorld.BYPASSWORLD.reset();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
