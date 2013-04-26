package com.gutabi.bypass.level;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gutabi.bypass.BypassActivity;
import com.gutabi.bypass.BypassView;
import com.gutabi.bypass.R;

public class BypassWorldActivity extends BypassActivity {
	
	{
		name = "bypassworld";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bypassworld);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		v = (BypassView)findViewById(R.id.view_bypassworld);
		if (v == null) {
			throw new AssertionError("R.id.view_bypassworld could not be found");
		}
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		int ii = (Integer)getIntent().getExtras().get("com.gutabi.bypass.level.Index");
		
		BypassWorld.create(ii);
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
