package com.gutabi.deadlock.world;

import com.gutabi.deadlock.AppScreen;

public class DebuggerScreen extends AppScreen {
	
	public DebuggerScreen(WorldScreen worldScreen) {
		contentPane = new DebuggerScreenContentPane(this);	
	}
	
}
