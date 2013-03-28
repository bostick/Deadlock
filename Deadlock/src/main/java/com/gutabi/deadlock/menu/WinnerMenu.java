package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;

public class WinnerMenu extends Menu {
	
	public WinnerMenu() {
		
		MenuItem nextMenuItem = new MenuItem(WinnerMenu.this,"Next") {
			public void action() {
				
			}
		};
		add(nextMenuItem);
		
		MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back") {
			public void action() {
				
				APP.platform.unshowDebuggerScreen();
				
				MainMenuScreen s = new MainMenuScreen();
				
				APP.platform.setupAppScreen(s.contentPane.cp);
				
				s.postDisplay();
				s.contentPane.panel.render();
				s.contentPane.repaint();
			}
		};
		add(backMenuItem);
		
	}
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(APP.WINDOW_WIDTH/2 - aabb.width/2, APP.WINDOW_HEIGHT/2 - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
