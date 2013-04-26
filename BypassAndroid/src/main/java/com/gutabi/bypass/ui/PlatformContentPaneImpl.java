package com.gutabi.bypass.ui;

import android.graphics.Canvas;

import com.gutabi.bypass.BypassView;
import com.gutabi.deadlock.ui.PlatformContentPane;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	BypassView v;
	
	public PlatformContentPaneImpl(final BypassView v) {
		this.v = v;
	}
	
	public void repaint() {
		
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
