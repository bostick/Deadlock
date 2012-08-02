package com.gutabi.deadlock.android.controller;

import static com.gutabi.deadlock.android.MainActivity.PLATFORMVIEW;

public class PlatformController {
	
	public TouchController tc;
	
	public void init() {
		
		tc = new TouchController();
		
		PLATFORMVIEW.setOnTouchListener(new TouchController());
	}
	
}
