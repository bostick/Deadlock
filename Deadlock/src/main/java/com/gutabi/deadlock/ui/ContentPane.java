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
	
//	public void clicked(InputEvent ev) {
//		pcp.clicked(ev);
//	}
//	
//	public void paint(RenderingContext ctxt) {
//		pcp.paint(ctxt);
//	}
//
//	public void pressed(InputEvent ev) {
//		pcp.pressed(ev);
//	}
//
//	public void released(InputEvent ev) {
//		pcp.released(ev);
//	}
//
//	public void moved(InputEvent ev) {
//		pcp.moved(ev);
//	}
//
//	public void dragged(InputEvent ev) {
//		pcp.dragged(ev);
//	}
	
}
