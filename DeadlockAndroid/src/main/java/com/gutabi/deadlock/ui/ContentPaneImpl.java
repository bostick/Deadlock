package com.gutabi.deadlock.ui;

import android.view.View;

public class ContentPaneImpl extends ContentPaneBase {
	
	View container;
	
	public ContentPaneImpl(View container) {
		this.container = container;
	}
	
	public void repaint() {
		container.invalidate();
	}
	
}
