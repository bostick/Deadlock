package com.gutabi.bypass.ui;

import android.graphics.Canvas;

import com.gutabi.bypass.BypassView;
import com.gutabi.capsloc.ui.PlatformContentPane;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	BypassView v;
	
	public PlatformContentPaneImpl(BypassView v) {
		this.v = v;
	}
	
	public void repaint() {
		
		if (!v.surfaceValid) {
			return;
		}
		
		Canvas c = v.holder.lockCanvas();
		if (c == null) {
			return;
		}
		try {
			
			v.doDraw(c);
			
		} finally {
			v.holder.unlockCanvasAndPost(c);
		}
		
	}
	
}
