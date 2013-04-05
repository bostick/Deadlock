package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class MainMenuContentPane extends ContentPane {
	
	public MainMenuContentPane(MainMenuScreen screen) {
		
		pcp = APP.platform.createPlatformContentPane(screen);
		
		MainMenuPanel panel = new MainMenuPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(panel);
		
	}
	
}
