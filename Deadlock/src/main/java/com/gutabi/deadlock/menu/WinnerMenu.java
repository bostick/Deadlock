package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.Panel;

public class WinnerMenu extends Menu {
	
	public WinnerMenu(Panel parPanel) {
		super(parPanel);
		
		MenuItem nextMenuItem = new MenuItem(WinnerMenu.this,"Next", 36) {
			public void action() {
				
			}
		};
		add(nextMenuItem, 0, 0);
		
		MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back", 36) {
			public void action() {
				
				APP.platform.unshowDebuggerScreen();
				
				MainMenuScreen s = new MainMenuScreen();
				APP.setAppScreen(s);
				
				APP.platform.setupAppScreen(s.contentPane.pcp);
				
				s.postDisplay();
				s.contentPane.panel.render();
				s.contentPane.repaint();
			}
		};
		add(backMenuItem, 1, 0);
		
	}
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(parPanel.aabb.width/2 - aabb.width/2, parPanel.aabb.height/2 - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
