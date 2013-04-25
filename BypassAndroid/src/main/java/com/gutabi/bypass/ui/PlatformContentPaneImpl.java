package com.gutabi.bypass.ui;

import android.graphics.Canvas;
import android.util.Log;

import com.gutabi.bypass.BypassView;
import com.gutabi.deadlock.ui.PlatformContentPane;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	BypassView v;
//	Runnable repaintRunnable;
	
	public PlatformContentPaneImpl(final BypassView v) {
		this.v = v;
		
//		repaintRunnable = new Runnable() {
//			public void run() {
//				
//				v.invalidate();
//			}
//		};
		
	}
	
	public void repaint() {
		
		Canvas c = v.holder.lockCanvas();
		if (c == null) {
			return;
		}
		
//		Log.d("bypass", "repaint 1");
		
		v.doDraw(c);
		
//		Log.d("bypass", "repaint 2");
		
		v.holder.unlockCanvasAndPost(c);
		
//		PlatformImpl.CURRENTACTIVITY.runOnUiThread(repaintRunnable);
		
	}
	
}
