package com.gutabi.deadlock.menu;

import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class MainMenuContentPane extends ContentPane {
	
	public MainMenu screen;
	
	public MenuPanel panel;
	
	public MainMenuContentPane(MainMenu screen) {
		
		panel = new MenuPanel(screen) {{
			setLocation(0, 0);
		}};
		
		children.add(panel);
		
	}
	
	public void downKey() {
		
		if (screen.hilited == null) {
			
			screen.hilited = screen.firstMenuItem;
			
		} else {
			
			screen.hilited = screen.hilited.down;
			
		}
		
		while (!screen.hilited.active) {
			screen.hilited = screen.hilited.down;
		}
		
		screen.contentPane.repaint();
	}
	
	public void upKey() {
		
		if (screen.hilited == null) {
			
			screen.hilited = screen.firstMenuItem;
			
		} else {
			
			screen.hilited = screen.hilited.up;
			
		}
		
		while (!screen.hilited.active) {
			screen.hilited = screen.hilited.up;
		}
		
		screen.contentPane.repaint();
	}
	
	public void enterKey() {
		
		if (screen.hilited != null && screen.hilited.active) {
			screen.hilited.action();
		}
		
	}
	
}
