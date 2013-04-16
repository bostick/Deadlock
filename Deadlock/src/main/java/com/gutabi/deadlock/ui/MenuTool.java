package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Tool;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MenuTool extends Tool {
	
	public void upKey() {
		Menu menu = APP.model.getMenu();
		
		if (menu.hilited == null) {
			
			menu.hilited = menu.firstMenuItem;
			
		} else {
			
			menu.hilited = menu.hilited.up;
			
		}
		
		while (!menu.hilited.active) {
			menu.hilited = menu.hilited.up;
		}
		
//		APP.appScreen.contentPane.repaint();
	}
	
	public void downKey() {
		Menu menu = APP.model.getMenu();
		
		if (menu.hilited == null) {
			
			menu.hilited = menu.firstMenuItem;
			
		} else {
			
			menu.hilited = menu.hilited.down;
			
		}
		
		while (!menu.hilited.active) {
			menu.hilited = menu.hilited.down;
		}
		
//		APP.appScreen.contentPane.repaint();
	}
	
	public void enterKey() {
		Menu menu = APP.model.getMenu();
		
		if (menu.hilited != null && menu.hilited.active) {
			menu.hilited.action();
		}
	}
	
	public void escKey() {
		Menu menu = APP.model.getMenu();
		
		menu.escape();
	}
	
	public void moved(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		menu.moved(new InputEvent(ignore.panel, Point.panelToMenu(ignore.p, menu)));
	}
	
	public void clicked(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		menu.clicked(new InputEvent(ignore.panel, Point.panelToMenu(ignore.p, menu)));
	}
	
	
	Point origMenuUL;
	Point origPressed;
	
	public void pressed(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		origMenuUL = menu.aabb.ul;
		origPressed = ignore.p;
		
		menu.pressed(new InputEvent(ignore.panel, Point.panelToMenu(ignore.p, menu)));
	}
	
	public void dragged(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		if (origPressed == null) {
			return;
		}
		
		Point motionDiff = ignore.p.minus(origPressed);
		
		if (!menu.hScrollable && !menu.vScrollable) {
			return;
		}
		
		boolean h;
		if (menu.hScrollable && menu.vScrollable) {
			
			if (Math.abs(motionDiff.x) > Math.abs(motionDiff.y)) {
				h = true;
			} else {
				h = false;
			}
			
		} else if (menu.hScrollable) {
			h = true;
		} else {
			h = false;
		}
		
		Point newLoc;
		if (h) {
			
			double newX = origMenuUL.x + motionDiff.x;
			if (newX > 0) {
				newX = 0;
			} else if (newX + menu.aabb.width < ignore.panel.aabb.width) {
				newX = ignore.panel.aabb.width - menu.aabb.width;
			}
			newLoc = new Point(newX, menu.aabb.y);
			
		} else {
			double newY = origMenuUL.y + motionDiff.y;
			if (newY > 0) {
				newY = 0;
			} else if (newY + menu.aabb.height < ignore.panel.aabb.height) {
				newY = ignore.panel.aabb.height - menu.aabb.height;
			}
			newLoc = new Point(menu.aabb.x, newY);
		}
		
		menu.dragToNewLocation(newLoc);
	}
	
	public void released(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		if (origPressed == null) {
			return;
		}
		
		origPressed = null;
		
		menu.released(new InputEvent(ignore.panel, Point.panelToMenu(ignore.p, menu)));
	}
	
	public void setPoint(Point p) {
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
