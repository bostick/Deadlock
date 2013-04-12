package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class DebuggerScreenContentPane extends ContentPane {
	
	public DebuggerScreenContentPane() {
		pcp = APP.platform.createPlatformContentPane();
	}
	
}
