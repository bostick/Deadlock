package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class MainMenuContentPane extends ContentPane {
	
	public MainMenuContentPane() {
		
		pcp = APP.platform.createPlatformContentPane();
		
		MainMenuPanel panel = new MainMenuPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(panel);
		
	}
	
}
