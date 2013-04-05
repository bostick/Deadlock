package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class DebuggerScreenContentPane extends ContentPane {
	
	public DebuggerScreenContentPane(DebuggerScreen debuggerScreen) {
		this.pcp = APP.platform.createPlatformContentPane(debuggerScreen);
		
		ControlPanel controlPanel = new ControlPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(controlPanel);
	}
	
}
