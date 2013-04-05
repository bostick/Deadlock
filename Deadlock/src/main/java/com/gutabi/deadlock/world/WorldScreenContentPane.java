package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class WorldScreenContentPane extends ContentPane {
	
	public WorldScreenContentPane(WorldScreen screen) {
		this.pcp = APP.platform.createPlatformContentPane(screen);
		
		WorldPanel worldPanel = new WorldPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(worldPanel);
	}
	
}
