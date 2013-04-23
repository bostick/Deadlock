package com.gutabi.bypass.ui;

import android.view.View;

import com.gutabi.bypass.PlatformImpl;
import com.gutabi.deadlock.ui.PlatformContentPane;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	View v;
	Runnable repaintRunnable;
	
	public PlatformContentPaneImpl(final View v) {
		this.v = v;
		
		repaintRunnable = new Runnable() {
			public void run() {
				
				v.invalidate();
			}
		};
		
	}
	
	public void repaint() {
		PlatformImpl.CURRENTACTIVITY.runOnUiThread(repaintRunnable);
	}
	
}
