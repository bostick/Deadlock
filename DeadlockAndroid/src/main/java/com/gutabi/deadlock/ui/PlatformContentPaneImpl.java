package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.MainActivity;

import android.view.View;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	View container;
	Runnable repaintRunnable;
	
	public PlatformContentPaneImpl(final View container) {
		this.container = container;
		
		repaintRunnable = new Runnable() {
			public void run() {
				container.invalidate();
			}
		};
		
	}
	
	public void repaint() {
		MainActivity.MAINACTIVITY.runOnUiThread(repaintRunnable);
	}
	
}
