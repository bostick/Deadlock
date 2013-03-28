package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainMenuContentPane implements ContentPane {
	
	public ContentPane cp;
	public MainMenuScreen screen;
	
	public MenuPanel panel;
	
	public MainMenuContentPane(MainMenuScreen screen) {
		this.cp = APP.platform.createContentPane(null, screen);
		this.screen = screen;
		
		panel = new MenuPanel(screen) {{
			setLocation(0, 0);
		}};
		
		
		cp.getChildren().add(panel);
		
	}
	
	public List<Panel> getChildren() {
		return cp.getChildren();
	}
	
	public void postDisplay() {
		cp.postDisplay();
	}
	
	public void repaint() {
		cp.repaint();
	}
	
	public Point getLastMovedContentPanePoint() {
		return cp.getLastMovedContentPanePoint();
	}
	
	public void clicked(InputEvent ev) {
		cp.clicked(ev);
	}
	
	public void paint(RenderingContext ctxt) {
		cp.paint(ctxt);
	}

	@Override
	public void pressed(InputEvent ev) {
		cp.pressed(ev);
	}

	@Override
	public void released(InputEvent ev) {
		cp.released(ev);
	}

	@Override
	public void moved(InputEvent ev) {
		cp.moved(ev);
	}

	@Override
	public void dragged(InputEvent ev) {
		cp.dragged(ev);
	}
	
	
}
