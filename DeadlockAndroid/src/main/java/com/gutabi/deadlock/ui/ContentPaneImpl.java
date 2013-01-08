package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.MainView;

public class ContentPaneImpl extends ContentPaneBase {
	
	MainView container;
	
	public void repaint() {
		container.invalidate();
	}
	
}
