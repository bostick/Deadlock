package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.PlatformContentPane;

public class WorldScreenContentPane extends ContentPane {
	
	public PlatformContentPane pcp;
	
	public WorldScreenContentPane(WorldScreen screen) {
		this.pcp = APP.platform.createPlatformContentPane(screen);
		
		WorldPanel worldPanel = new WorldPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(worldPanel);
	}
	
}
