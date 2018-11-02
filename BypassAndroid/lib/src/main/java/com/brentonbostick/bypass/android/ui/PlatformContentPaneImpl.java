package com.brentonbostick.bypass.android.ui;

import android.graphics.Canvas;

import com.brentonbostick.bypass.android.BypassView;
import com.brentonbostick.capsloc.ui.PlatformContentPane;

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
