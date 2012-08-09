package com.gutabi.deadlock.android;

import android.util.Log;

import com.gutabi.deadlock.core.Logger;

public class PlatformLogger implements Logger {
	
	private PlatformLogger() {
		;
	}
	
	@Override
	public void debug(String msg) {
		Log.d(null, msg);
	}
	
}
