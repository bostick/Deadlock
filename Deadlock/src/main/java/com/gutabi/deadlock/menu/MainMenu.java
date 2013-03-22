package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.RushHourWorld;
import com.gutabi.deadlock.world.examples.WorldA;
import com.gutabi.deadlock.world.tools.CarTool;

public class MainMenu extends AppScreen implements KeyListener {
	
	public MainMenuContentPane contentPane;
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	double menuItemWidest;
	int totalMenuItemHeight;
	
	public double menuWidth;
	public double menuHeight;
	
	public MainMenu() {
		
		contentPane = new MainMenuContentPane(this);
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.appScreen = worldScreen;
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				worldScreen.world = OneByOneWorld.createOneByOneWorld(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.world.render_worldPanel();
				worldScreen.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.appScreen = worldScreen;
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				worldScreen.world = FourByFourGridWorld.createFourByFourGridWorld(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.world.render_worldPanel();
				worldScreen.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(fourMenuItem);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.appScreen = worldScreen;
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				worldScreen.world = WorldA.createWorldA(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.world.render_worldPanel();
				worldScreen.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(aMenuItem);
		
		MenuItem rMenuItem = new MenuItem(MainMenu.this, "Rush Hour") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.appScreen = worldScreen;
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				worldScreen.tool = new CarTool(worldScreen);
				worldScreen.world = RushHourWorld.createRushHourWorld(worldScreen, APP.debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				APP.debuggerScreen.postDisplay();
				
				worldScreen.startRunning();
				
				worldScreen.world.render_worldPanel();
				worldScreen.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(rMenuItem);
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
			public void action() {
				
				QuadrantEditor s = new QuadrantEditor();
				
				APP.platform.setupAppScreen(s.contentPane.cp);
				
				s.postDisplay();
				
				s.contentPane.panel.worldScreen.world.render_worldPanel();
				s.contentPane.repaint();
				
				APP.platform.showAppScreen();
			}
		};
		add(dialogMenuItem);
		
		MenuItem puzzleMenuItem = new MenuItem(MainMenu.this,  "Puzzle Mode") {
			public void action() {
				
			}
		};
		puzzleMenuItem.active = false;
		add(puzzleMenuItem);
		
		MenuItem loadMenuItem = new MenuItem(MainMenu.this, "Load...") {
			public void action() {
				
			}
		};
		loadMenuItem.active = false;
		add(loadMenuItem);
		
		MenuItem captureMenuItem = new MenuItem(MainMenu.this, "Capture the Flag") {
			public void action() {
				
			}
		};
		captureMenuItem.active = false;
		add(captureMenuItem);
		
		MenuItem quitMenuItem = new MenuItem(MainMenu.this, "Quit") {
			public void action() {
				
				APP.exit();
				
			}
		};
		add(quitMenuItem);
		
	}
	
	public void postDisplay() {
		contentPane.postDisplay();
	}
	
	public void add(MenuItem item) {
		
		if (items.isEmpty()) {
			firstMenuItem = item;
		}
		
		items.add(item);
		
		int i = items.size()-1;
		
		MenuItem prev = items.get((i-1 + items.size()) % items.size());
		MenuItem first = items.get(0);
		
		prev.down = item;
		
		item.up = prev;
		item.down = first;
		
		first.up = item;
		
	}
	
	public void downKey() {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.down;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.down;
		}
		
		contentPane.repaint();
	}
	
	public void upKey() {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.up;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.up;
		}
		
		contentPane.repaint();
	}
	
	public void enterKey() {
		
		if (hilited != null && hilited.active) {
			hilited.action();
		}
		
	}
	
	public void ctrlOKey() {
		;
	}

	public void dKey() {
		;
	}

	public void aKey() {
		;
	}

	public void sKey() {
		;
	}

	public void ctrlSKey() {
		;
	}

	public void minusKey() {
		;
	}

	public void plusKey() {
		;
	}

	public void d3Key() {
		;
	}

	public void d2Key() {
		;
	}

	public void d1Key() {
		;
	}

	public void gKey() {
		;
	}

	public void wKey() {
		;
	}

	public void qKey() {
		;
	}

	public void escKey() {
		;
	}

	public void deleteKey() {
		;
	}

	public void insertKey() {
		;
	}
	
	public void fKey() {
		
	}
	
}
