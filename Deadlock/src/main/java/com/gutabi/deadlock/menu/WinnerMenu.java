package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class WinnerMenu extends Menu {
	
	public WinnerMenu() {
		
		MenuItem oneMenuItem = new MenuItem(WinnerMenu.this,"Next") {
			public void action() {
				
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(WinnerMenu.this, "Back") {
			public void action() {
				
				APP.platform.unshowDebuggerScreen();
				
				MainMenuScreen s = new MainMenuScreen();
				
				APP.platform.setupAppScreen(s.contentPane.cp);
				
				s.postDisplay();
				s.contentPane.panel.render();
				s.contentPane.repaint();
			}
		};
		add(fourMenuItem);
		
	}
	
}
