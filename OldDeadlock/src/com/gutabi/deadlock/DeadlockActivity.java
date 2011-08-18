package com.gutabi.deadlock;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.gutabi.deadlock.controller.DeadlockController;
import com.gutabi.deadlock.model.DeadlockModel;
import com.gutabi.deadlock.view.DeadlockView;

public class DeadlockActivity extends Activity {
	
	//Mode mode = Mode.DRAFTING;
	
	public TextView status;
	
	public DeadlockModel model;
	public DeadlockView view;
	public DeadlockController controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		view = (DeadlockView) findViewById(R.id.deadlock);
		status = (TextView) findViewById(R.id.status);
		
		model = new DeadlockModel();
		controller = new DeadlockController(model, view);
		
		view.setController(controller);
		view.setModel(model);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.game_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.clear:
	    	controller.menuClear();
	    	return true;
	    case R.id.new_game:
	        
	        return true;
	    case R.id.quit:
	        
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
//		MenuItem contextModeItem = menu.findItem(R.id.toggle_mode);
//		
//		if (mode == Mode.DRAFTING) {
//			contextModeItem.setTitle("Start Simulating");
//		} else if (mode == Mode.SIMULATING) {
//			contextModeItem.setTitle("Stop (Back to Drafting)");
//		}
		
		return true;
	}
	
}
