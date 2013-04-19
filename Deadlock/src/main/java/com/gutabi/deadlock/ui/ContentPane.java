package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public class ContentPane {
	
	public PlatformContentPane pcp = APP.platform.createPlatformContentPane();
	
	public ContentPane(Panel panel) {
		pcp.getChildren().add(panel);
	}
	
	public List<Panel> getChildren() {
		return pcp.getChildren();
	}
	
	public void postDisplay() {
		pcp.postDisplay();
	}
	
	public void repaint() {
		pcp.repaint();
	}
	
	public Point getLastMovedContentPanePoint() {
		return pcp.getLastMovedContentPanePoint();
	}
	
}
