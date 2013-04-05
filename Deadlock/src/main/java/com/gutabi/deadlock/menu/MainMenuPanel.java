package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainMenuPanel extends Panel {
	
	int TITLE_CENTER_Y = 165;
	int COPYRIGHT_CENTER_Y = 800;
	
	public MainMenuPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT);
	}
	
	public void postDisplay() {
		Menu menu = (Menu)APP.model;
		
		menu.aabb = new AABB(aabb.width/2 - menu.aabb.width/2, aabb.height/2 - menu.aabb.height/2, menu.aabb.width, menu.aabb.height);
	}
	
	public void paint(RenderingContext ctxt) {
		Menu menu = (Menu)APP.model;
		
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
		
		menu.paint_panel(ctxt);
		
		ctxt.setTransform(origTrans);
		
	}
	
}
