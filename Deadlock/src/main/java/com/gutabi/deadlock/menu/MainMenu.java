package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.Screen;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.RushHourWorld;
import com.gutabi.deadlock.world.examples.WorldA;

public class MainMenu extends Screen {
	
	public MainMenuContentPane contentPane;
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	double widest;
	int totalHeight;
	
	static Logger logger = Logger.getLogger(MainMenu.class);
	
	public MainMenu() {
		
		contentPane = new MainMenuContentPane(this);
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				WorldScreen s = new WorldScreen();
				s.world = OneByOneWorld.createOneByOneWorld(s);
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_worldPanel();
				s.world.render_preview();
				s.contentPane.repaint();
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				WorldScreen s = new WorldScreen();
				s.world = FourByFourGridWorld.createFourByFourGridWorld(s);
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_worldPanel();
				s.world.render_preview();
				s.contentPane.repaint();	
			}
		};
		add(fourMenuItem);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				WorldScreen s = new WorldScreen();
				s.world = WorldA.createWorldA(s);
				
				s.setup(APP.container);
				
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_worldPanel();
				s.world.render_preview();
				s.contentPane.repaint();
			}
		};
		add(aMenuItem);
		
		MenuItem rMenuItem = new MenuItem(MainMenu.this, "Rush Hour") {
			public void action() {
				
				WorldScreen s = new WorldScreen();
				s.world = RushHourWorld.createRushHourWorld(s);
				
				s.setup(APP.container);
				
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_worldPanel();
				s.world.render_preview();
				s.contentPane.repaint();
			}
		};
		add(rMenuItem);
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
			public void action() {
				
				QuadrantEditor s = new QuadrantEditor();
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				s.contentPane.panel.worldScreen.world.render_worldPanel();
				s.contentPane.repaint();
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
	
	public void setup(RootPaneContainer container) {
		
		contentPane.setLayout(null);
		
		container.setContentPane(contentPane);
		contentPane.setFocusable(true);
		contentPane.requestFocusInWindow();
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
	
}
