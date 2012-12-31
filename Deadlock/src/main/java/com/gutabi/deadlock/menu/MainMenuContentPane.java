package com.gutabi.deadlock.menu;

import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class MainMenuContentPane extends ContentPane {
	
	public MenuCanvas canvas;
	
	public MainMenuContentPane(MainMenu screen) {
		super(screen);
		
		canvas = new MenuCanvas(screen) {{
			setLocation(0, 0);
		}};
		
		children.add(canvas);
		
	}
	
}
