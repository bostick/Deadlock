package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainMenuPanel extends PanelBase {
	
	int TITLE_CENTER_Y = 165;
	int COPYRIGHT_CENTER_Y = 800;
	
	MainMenuScreen screen;
	
	public Menu menu;
	
	public MainMenuPanel(final MainMenuScreen screen) {
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
	
	public void moved(InputEvent ev) {
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
		
		Point p = ev.p;
		
		lastMovedMenuPoint = Point.panelToMenu(p, menu);
		
		menu.moved(lastMovedMenuPoint);
		
		screen.contentPane.repaint();
	}
	
	public void clicked(InputEvent ev) {
		
		lastClickedMenuPoint = Point.panelToMenu(ev.p, menu);
		
		menu.clicked(lastClickedMenuPoint);
		
	}
	
	public void render() {
		
		synchronized (APP) {
			
			menu.render();
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		Transform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.DARK_GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		Transform menuTrans = ctxt.getTransform();
		
		ctxt.translate(
				aabb.width/2 - Math.min(APP.titleBackground.getWidth(), aabb.width)/2,
				aabb.height/2 - APP.titleBackground.getHeight()/2);
		
		ctxt.paintImage(APP.titleBackground,
				0, 0, Math.min(APP.titleBackground.getWidth(), (int)aabb.width), APP.titleBackground.getHeight(),
				0, 0, Math.min(APP.titleBackground.getWidth(), (int)aabb.width), APP.titleBackground.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(aabb.width/2 - APP.title_white.getWidth()/2, TITLE_CENTER_Y - APP.title_white.getHeight()/2);
		ctxt.paintImage(APP.title_white,
				0, 0, APP.title_white.getWidth(), APP.title_white.getHeight(),
				0, 0, APP.title_white.getWidth(), APP.title_white.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(aabb.width/2 - APP.copyright.getWidth()/2, COPYRIGHT_CENTER_Y - APP.copyright.getHeight()/2);
		ctxt.paintImage(APP.copyright,
				0, 0, APP.copyright.getWidth(), APP.copyright.getHeight(),
				0, 0, APP.copyright.getWidth(), APP.copyright.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		menu.paint_pixels(ctxt);
		
		ctxt.setTransform(origTrans);
		
	}
	
}
