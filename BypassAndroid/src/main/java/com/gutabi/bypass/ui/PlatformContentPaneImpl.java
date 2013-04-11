package com.gutabi.bypass.ui;

import com.gutabi.bypass.MainActivity;
import com.gutabi.deadlock.ui.PlatformContentPane;

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
