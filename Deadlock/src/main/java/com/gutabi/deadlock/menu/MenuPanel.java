package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MenuPanel extends PanelBase {
		
	public final int MENU_WIDTH = 800;
	public final int MENU_HEIGHT = 600;
	
	MainMenu screen;
	
//	static Logger logger = Logger.getLogger(MenuPanel.class);
	
	public MenuPanel(final MainMenu screen) {
		this.screen = screen;
		
		aabb = APP.platform.createShapeEngine().createAABB(aabb.x, aabb.y, APP.MENUPANEL_WIDTH, APP.MENUPANEL_HEIGHT);
	}
	
	public void setLocation(double x, double y) {
		aabb = APP.platform.createShapeEngine().createAABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
	}
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	public Point lastMovedMenuPoint;
	Point lastClickedPanelPoint;
	
	Point lastClickedMenuPoint;
	
	public Point panelToMenu(Point p) {
		return new Point(p.x - (aabb.width/2 - MENU_WIDTH/2), p.y - (aabb.height/2 - MENU_HEIGHT/2));
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
				if ((int)item.localAABB.width > screen.widest) {
					screen.widest = (int)item.localAABB.width;
				}
				screen.totalHeight += (int)item.localAABB.height;
			}
			
			Image tmpImg = APP.platform.createImageEngine().createImage(MENU_WIDTH, MENU_HEIGHT);
			
			RenderingContext ctxt = APP.platform.createRenderingContext(tmpImg);
			
			Transform origTransform = ctxt.getTransform();
			
			ctxt.translate(MENU_WIDTH/2 - screen.widest/2, 150);
			
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
		
		ctxt.translate(aabb.width/2 - MENU_WIDTH/2, aabb.height/2 - MENU_HEIGHT/2);
		
		Transform menuTrans = ctxt.getTransform();
		
		ctxt.paintImage(APP.titleBackground, 0, 0, MENU_WIDTH, MENU_HEIGHT, 0, 0, MENU_WIDTH, MENU_HEIGHT);
		
		ctxt.translate(MENU_WIDTH/2 - 498/2, 20);
		ctxt.paintImage(APP.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(MENU_WIDTH/2 - 432/2, 550);
		ctxt.paintImage(APP.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
		
		ctxt.setTransform(menuTrans);
		
		ctxt.setColor(Color.menuBackground);
		ctxt.fillRect((int)(MENU_WIDTH/2 - screen.widest/2 - 5), 150 - 5, (int)(screen.widest + 10), screen.totalHeight + 10 * (screen.items.size() - 1) + 5 + 5);
		
		for (MenuItem item : screen.items) {
			item.paint(ctxt);
		}
		
		if (screen.hilited != null) {
			screen.hilited.paintHilited(ctxt);			
		}
		
		ctxt.setTransform(origTrans);
		
	}
	
}
