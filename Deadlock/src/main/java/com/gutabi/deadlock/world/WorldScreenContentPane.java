package com.gutabi.deadlock.world;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class WorldScreenContentPane extends ContentPane {
	
	public WorldPanel worldPanel;
	public ControlPanel controlPanel;
	
	static Logger logger = Logger.getLogger(WorldScreenContentPane.class);
	
	public WorldScreenContentPane(WorldScreen screen) {
		super(screen);
		
		worldPanel = new WorldPanel(screen) {{
			setLocation(0, 0);
		}};
		
		controlPanel = new ControlPanel(screen) {{
			setLocation(0 + worldPanel.aabb.width, 0);
		}};
		
		children.add(worldPanel);
		children.add(controlPanel);
	}
	
}
