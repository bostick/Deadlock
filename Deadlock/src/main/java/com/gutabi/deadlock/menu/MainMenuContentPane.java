package com.gutabi.deadlock.menu;

import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class MainMenuContentPane extends ContentPane {
	
	public MenuPanel panel;
	
	public MainMenuContentPane(MainMenu screen) {
		super(screen);
		
		panel = new MenuPanel(screen) {{
			setLocation(0, 0);
		}};
		
		children.add(panel);
		
	}
	
}
