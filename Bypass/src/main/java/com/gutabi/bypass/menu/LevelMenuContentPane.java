package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class LevelMenuContentPane extends ContentPane {
	
	public LevelMenuContentPane() {
		
		pcp = APP.platform.createPlatformContentPane();
		
		LevelMenuPanel panel = new LevelMenuPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(panel);
		
	}
	
}
