package com.brentonbostick.capsloc.ui;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.Tool;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public class MenuTool extends Tool {
	
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
		
		origMenuUL = new Point(menu.aabb.x, menu.aabb.y);
		origPressed = ignore.p;
		
		menu.pressed(new InputEvent(ignore.panel, Point.panelToMenu(ignore.p, menu)));
	}
	
	public void dragged(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		if (origPressed == null) {
			return;
		}
		
		Point motionDiff = ignore.p.minus(origPressed);
		if (motionDiff.length() < 15.0) {
			return;
		}
		
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
			if (newX - menu.panelOffset.x > 0) {
				newX = menu.panelOffset.x;
			} else if (newX + menu.aabb.width < ignore.panel.aabb.width) {
				newX = ignore.panel.aabb.width - menu.aabb.width;
			}
			newLoc = new Point(newX, menu.aabb.y);
			
		} else {
			double newY = origMenuUL.y + motionDiff.y;
			if (newY - menu.panelOffset.y > 0) {
				newY = menu.panelOffset.y;
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
	
	public void canceled(InputEvent ignore) {
		Menu menu = APP.model.getMenu();
		
		if (origPressed == null) {
			return;
		}
		
		origPressed = null;
		
		menu.canceled(new InputEvent(ignore.panel, null));
	}
	
	public void setPoint(Point p) {
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
