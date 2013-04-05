package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Tool;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MenuTool extends Tool {
	
	public void upKey() {
		Menu menu = (Menu)APP.model;
		
		if (menu.hilited == null) {
			
			menu.hilited = menu.firstMenuItem;
			
		} else {
			
			menu.hilited = menu.hilited.up;
			
		}
		
		while (!menu.hilited.active) {
			menu.hilited = menu.hilited.up;
		}
		
		APP.appScreen.contentPane.repaint();
	}
	
	public void downKey() {
		Menu menu = (Menu)APP.model;
		
		if (menu.hilited == null) {
			
			menu.hilited = menu.firstMenuItem;
			
		} else {
			
			menu.hilited = menu.hilited.down;
			
		}
		
		while (!menu.hilited.active) {
			menu.hilited = menu.hilited.down;
		}
		
		APP.appScreen.contentPane.repaint();
	}
	
	public void enterKey() {
		Menu menu = (Menu)APP.model;
		
		if (menu.hilited != null && menu.hilited.active) {
			menu.hilited.action();
		}
	}
	
	public void moved(InputEvent ev) {
		Menu menu = (Menu)APP.model;
		
		menu.lastMovedMenuPoint = Point.panelToMenu(ev.p, menu);
		
		MenuItem hit = menu.hitTest(ev.p);
		if (hit != null && hit.active) {
			menu.hilited = hit;
		} else {
			menu.hilited = null;
		}
		
		APP.appScreen.contentPane.repaint();
	}
	
	public void clicked(InputEvent ev) {
		Menu menu = (Menu)APP.model;
		
		menu.lastClickedMenuPoint = Point.panelToMenu(ev.p, menu);
		
		MenuItem item = menu.hitTest(ev.p);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
	public void setPoint(Point p) {
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
