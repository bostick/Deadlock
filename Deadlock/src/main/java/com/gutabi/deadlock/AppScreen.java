package com.gutabi.deadlock;

import com.gutabi.deadlock.ui.ContentPane;

public class AppScreen {
	
	public ContentPane contentPane;
	
	public AppScreen(ContentPane contentPane) {
		this.contentPane = contentPane;
	}
	
	public void postDisplay(int width, int height) {
		contentPane.postDisplay(width, height);
	}
	
}
