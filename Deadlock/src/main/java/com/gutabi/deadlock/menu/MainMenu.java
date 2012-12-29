package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.WorldA;

public class MainMenu extends ScreenBase {
	
	public final int MENU_WIDTH = 800;
	public final int MENU_HEIGHT = 600;
	
	public MenuCanvas canvas;
	
	int canvasWidth;
	int canvasHeight;
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	double widest;
	int totalHeight;
	
	static Logger logger = Logger.getLogger(MainMenu.class);
	
	public MainMenu() {
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				teardown(APP.container);
				
				WorldScreen s = new WorldScreen();
				s.world = OneByOneWorld.createOneByOneWorld(s);
				s.init();
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_canvas();
				s.world.render_preview();
				s.canvas.repaint();
				s.controlPanel.repaint();
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				teardown(APP.container);
				
				WorldScreen s = new WorldScreen();
				s.world = FourByFourGridWorld.createFourByFourGridWorld(s);
				s.init();
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_canvas();
				s.world.render_preview();
				s.canvas.repaint();
				s.controlPanel.repaint();
				
			}
		};
		add(fourMenuItem);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				teardown(APP.container);
				
				WorldScreen s = new WorldScreen();
				s.world = WorldA.createWorldA(s);
				s.init();
				
				s.setup(APP.container);
				
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_canvas();
				s.world.render_preview();
				s.canvas.repaint();
				s.controlPanel.repaint();
				
			}
		};
		add(aMenuItem);
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
			public void action() {
				
				teardown(APP.container);
				
				QuadrantEditor s = new QuadrantEditor();
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				s.worldScreen.world.render_canvas();
				s.canvas.repaint();
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
		
		canvas = new MenuCanvas(this);
		
	}
	
	public void setup(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		
		cp.setLayout(null);
		
		cp.add(canvas.java());
		
		Dimension size = canvas.java().getSize();
		canvas.java().setBounds(0, 0, size.width, size.height);
		
	}
	
	public void teardown(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		cp.remove(canvas.java());
		
		canvas = null;
	}
	
	public void postDisplay() {
		
		Dim canvasDim = canvas.postDisplay();
		canvasWidth = (int)canvasDim.width;
		canvasHeight = (int)canvasDim.height;
		
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
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void downKey(InputEvent ev) {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.down;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.down;
		}
		
		canvas.repaint();
	}
	
	public void upKey(InputEvent ev) {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.up;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.up;
		}
		
		canvas.repaint();
	}
	
	public void enterKey(InputEvent ev) {
		
		if (hilited != null && hilited.active) {
			hilited.action();
		}
		
	}
	
	public Point canvasToMenu(Point p) {
		return new Point(p.x - (canvas.getWidth()/2 - MENU_WIDTH/2), p.y - (canvas.getHeight()/2 - MENU_HEIGHT/2));
	}
	
	public Point lastMovedMenuPoint;
	
	public void moved(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedMenuPoint = canvasToMenu(p);
		
		MenuItem hit = hitTest(lastMovedMenuPoint);
		if (hit != null && hit.active) {
			hilited = hit;
		} else {
			hilited = null;
		}
		
		canvas.repaint();
	}
	
	Point lastClickedMenuPoint;
	
	public void clicked(InputEvent ev) {
		
		lastClickedMenuPoint = canvasToMenu(ev.p);
		
		MenuItem item = hitTest(lastClickedMenuPoint);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
}
