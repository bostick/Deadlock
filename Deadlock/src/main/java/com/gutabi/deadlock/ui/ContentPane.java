package com.gutabi.deadlock.ui;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class ContentPane {
	
	public PlatformContentPane pcp;
	
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
