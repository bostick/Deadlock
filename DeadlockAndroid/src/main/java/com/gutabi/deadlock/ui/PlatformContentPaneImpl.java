package com.gutabi.deadlock.ui;

import android.view.View;

public class PlatformContentPaneImpl extends PlatformContentPane {
	
	View container;
	
	public PlatformContentPaneImpl(View container) {
		this.container = container;
	}
	
	public void repaint() {
		container.invalidate();
	}
	
}
