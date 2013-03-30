package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainMenuContentPane implements ContentPane {
	
	public PlatformContentPane pcp;
	public MainMenuScreen screen;
	
	public MainMenuPanel panel;
	
	public MainMenuContentPane(MainMenuScreen screen) {
		this.pcp = APP.platform.createPlatformContentPane(screen);
		this.screen = screen;
		
		panel = new MainMenuPanel(screen) {{
			setLocation(0, 0);
		}};
		
		
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
	
	public void clicked(InputEvent ev) {
		pcp.clicked(ev);
	}
	
	public void paint(RenderingContext ctxt) {
		pcp.paint(ctxt);
	}

	@Override
	public void pressed(InputEvent ev) {
		pcp.pressed(ev);
	}

	@Override
	public void released(InputEvent ev) {
		pcp.released(ev);
	}

	@Override
	public void moved(InputEvent ev) {
		pcp.moved(ev);
	}

	@Override
	public void dragged(InputEvent ev) {
		pcp.dragged(ev);
	}
	
	
}
