package com.gutabi.deadlock.android;

import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.gutabi.deadlock.android.controller.PlatformController;
import com.gutabi.deadlock.android.view.PlatformView;
import com.gutabi.deadlock.android.view.PlatformWindowInfo;
import com.gutabi.deadlock.core.view.DeadlockView;

public class MainActivity extends Activity {

	public static PlatformView PLATFORMVIEW;
	public static PlatformController PLATFORMCONTROLLER;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		PLATFORMVIEW = (PlatformView) findViewById(R.id.deadlock);
		PLATFORMCONTROLLER = new PlatformController();
		VIEW = new DeadlockView(new PlatformWindowInfo());
		
		PLATFORMVIEW.init();
		PLATFORMCONTROLLER.init();
		
		VIEW.init();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
