package com.gutabi.deadlock.android;

//import com.gutabi.deadlock.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.gutabi.deadlock.android.controller.TouchController;
import com.gutabi.deadlock.android.view.PlatformView;

public class MainActivity extends Activity {

	//public DeadlockModel model;
	public static PlatformView PLATFORMVIEW;
	//public DeadlockController controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		PLATFORMVIEW = (PlatformView) findViewById(R.id.deadlock);
		//status = (TextView) findViewById(R.id.status);
		
		//model = new DeadlockModel();
		//controller = new DeadlockController(view);
		
//		VIEW.setController(CONTROLLER);
//		VIEW.setModel(MODEL);
		
		PLATFORMVIEW.setOnTouchListener(new TouchController());
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
