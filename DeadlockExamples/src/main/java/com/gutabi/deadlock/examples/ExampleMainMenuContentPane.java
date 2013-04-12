package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class ExampleMainMenuContentPane extends ContentPane {
	
	public ExampleMainMenuContentPane() {
		
		pcp = APP.platform.createPlatformContentPane();
		
		ExampleMainMenuPanel panel = new ExampleMainMenuPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(panel);
		
	}
	
}
