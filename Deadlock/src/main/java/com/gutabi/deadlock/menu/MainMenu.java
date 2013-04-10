package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.WorldA;

public class MainMenu extends Menu {
	
	public MainMenu() {
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				OneByOneWorld.action();
			}
		};
		add(oneMenuItem, 0, 0);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				FourByFourGridWorld.action();
			}
		};
		add(fourMenuItem, 1, 0);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				WorldA.action();
			}
		};
		add(aMenuItem, 2, 0);
		
		MenuItem rMenuItem = new MenuItem(MainMenu.this, "Rush Hour") {
			public void action() {
				
				LevelMenu.action();
			}
		};
		add(rMenuItem, 3, 0);
		
//		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
//			public void action() {
//				
//				QuadrantEditor.action();
//			}
//		};
//		add(dialogMenuItem, 4, 0);
		
//		MenuItem puzzleMenuItem = new MenuItem(MainMenu.this,  "Puzzle Mode") {
//			public void action() {
//				
//			}
//		};
//		puzzleMenuItem.active = false;
//		add(puzzleMenuItem, 5, 0);
		
//		MenuItem loadMenuItem = new MenuItem(MainMenu.this, "Load...") {
//			public void action() {
//				
//			}
//		};
//		loadMenuItem.active = false;
//		add(loadMenuItem, 6, 0);
		
//		MenuItem captureMenuItem = new MenuItem(MainMenu.this, "Capture the Flag") {
//			public void action() {
//				
//			}
//		};
//		captureMenuItem.active = false;
//		add(captureMenuItem, 7, 0);
		
//		MenuItem quitMenuItem = new MenuItem(MainMenu.this, "Quit") {
//			public void action() {
//				
//				APP.exit();
//				
//			}
//		};
//		add(quitMenuItem, 8, 0);
	}
	
	public static void action() {
		
		MainMenu mainMenu = new MainMenu();
		APP.model = mainMenu;
		
		MainMenuScreen s = new MainMenuScreen();
		APP.appScreen = s;
		
		APP.tool = new MenuTool();
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		mainMenu.render();
		
		s.postDisplay();
		
		s.contentPane.repaint();
		
	}
	
	public void escape() {
		
	}
	
}
