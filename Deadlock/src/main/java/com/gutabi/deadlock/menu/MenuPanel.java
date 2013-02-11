package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MenuPanel extends PanelBase {
	
	MainMenu screen;
	
	public MenuPanel(final MainMenu screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, APP.MENUPANEL_WIDTH, APP.MENUPANEL_HEIGHT);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
	}
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	public Point lastMovedMenuPoint;
	Point lastClickedPanelPoint;
	
	Point lastClickedMenuPoint;
	
	public Point panelToMenu(Point p) {
		return new Point(p.x - (aabb.width/2 - APP.MENU_WIDTH/2), p.y - (aabb.height/2 - APP.MENU_HEIGHT/2));
	}
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : screen.items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
		
		Point p = ev.p;
		
		lastMovedMenuPoint = panelToMenu(p);
		
		MenuItem hit = hitTest(lastMovedMenuPoint);
		if (hit != null && hit.active) {
			screen.hilited = hit;
		} else {
			screen.hilited = null;
		}
		
		screen.contentPane.repaint();
	}
	
	public void clicked(InputEvent ev) {
		
		lastClickedMenuPoint = panelToMenu(ev.p);
		
		MenuItem item = hitTest(lastClickedMenuPoint);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
	public void render() {
		
		synchronized (APP) {
			
			for (MenuItem item : screen.items) {
				if ((int)item.localAABB.width > screen.menuItemWidest) {
					screen.menuItemWidest = (int)item.localAABB.width;
				}
				screen.totalMenuItemHeight += (int)item.localAABB.height;
			}
			
			screen.menuWidth = screen.menuItemWidest;
			screen.menuHeight = screen.totalMenuItemHeight + 10 * (screen.items.size() - 1);
			
			Image tmpImg = APP.platform.createImageEngine().createImage(APP.MENU_WIDTH, APP.MENU_HEIGHT);
			
			RenderingContext ctxt = APP.platform.createRenderingContext(tmpImg);
			
			Transform origTransform = ctxt.getTransform();
			
			ctxt.translate(APP.MENU_WIDTH/2 - screen.menuWidth/2, APP.MENU_CENTER_Y - screen.menuHeight/2);
			
			for (MenuItem item : screen.items) {
				item.render(ctxt);
				ctxt.translate(0, item.localAABB.height + 10);
			}
			
			ctxt.setTransform(origTransform);
			
			ctxt.dispose();
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		Transform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.DARK_GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		ctxt.translate(aabb.width/2 - APP.MENU_WIDTH/2, aabb.height/2 - APP.MENU_HEIGHT/2);
		
		Transform menuTrans = ctxt.getTransform();
		
		ctxt.translate(APP.MENU_WIDTH/2 - APP.titleBackground.getWidth()/2, APP.MENU_HEIGHT/2 - APP.titleBackground.getHeight()/2);
		ctxt.paintImage(APP.titleBackground,
				0, 0, APP.titleBackground.getWidth(), APP.titleBackground.getHeight(),
				0, 0, APP.titleBackground.getWidth(), APP.titleBackground.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(APP.MENU_WIDTH/2 - APP.title_white.getWidth()/2, APP.TITLE_CENTER_Y - APP.title_white.getHeight()/2);
		ctxt.paintImage(APP.title_white,
				0, 0, APP.title_white.getWidth(), APP.title_white.getHeight(),
				0, 0, APP.title_white.getWidth(), APP.title_white.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(APP.MENU_WIDTH/2 - APP.copyright.getWidth()/2, APP.COPYRIGHT_CENTER_Y - APP.copyright.getHeight()/2);
		ctxt.paintImage(APP.copyright,
				0, 0, APP.copyright.getWidth(), APP.copyright.getHeight(),
				0, 0, APP.copyright.getWidth(), APP.copyright.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.setColor(Color.menuBackground);
		ctxt.fillRect(
				(int)(APP.MENU_WIDTH/2 - screen.menuItemWidest/2 - 5),
				(int)(APP.MENU_CENTER_Y - screen.menuHeight/2 - 5),
				(int)(screen.menuWidth + 5 + 5),
				(int)(screen.menuHeight + 5 + 5));
		
		for (MenuItem item : screen.items) {
			item.paint(ctxt);
		}
		
		if (screen.hilited != null) {
			screen.hilited.paintHilited(ctxt);			
		}
		
		ctxt.setTransform(origTrans);
		
	}
	
}
