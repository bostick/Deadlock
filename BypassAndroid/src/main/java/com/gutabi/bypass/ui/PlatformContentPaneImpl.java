package com.gutabi.bypass.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.util.Log;
import android.view.View;

import com.gutabi.bypass.PlatformImpl;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.deadlock.ui.PlatformContentPane;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	View v;
	Runnable repaintRunnable;
	
	public PlatformContentPaneImpl(final View v) {
		this.v = v;
		
		repaintRunnable = new Runnable() {
			public void run() {
				
//				if (APP.model instanceof BypassWorld) {
//					
//					BypassWorld world = (BypassWorld)APP.model;
//					Log.d("bypass", "car count: " + world.carMap.size());
//					
//				}
				
				v.invalidate();
			}
		};
		
	}
	
	public void repaint() {
		PlatformImpl.CURRENTACTIVITY.runOnUiThread(repaintRunnable);
	}
	
}
