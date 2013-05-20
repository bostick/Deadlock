package com.brentonbostick.capsloc.ui;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.List;

import com.brentonbostick.capsloc.math.Point;

public class ContentPane {
	
	public PlatformContentPane pcp = APP.platform.createPlatformContentPane();
	
	public ContentPane(Panel panel) {
		pcp.getChildren().add(panel);
	}
	
	public List<Panel> getChildren() {
		return pcp.getChildren();
	}
	
	public void postDisplay(int width, int height) {
		pcp.postDisplay(width, height);
	}
	
	public void repaint() {
		pcp.repaint();
	}
	
	public Point getLastMovedContentPanePoint() {
		return pcp.getLastMovedContentPanePoint();
	}
	
}
