package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuPanel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainMenuPanel extends MenuPanel {
	
	int TITLE_CENTER_Y = 165;
	int COPYRIGHT_CENTER_Y = 800;
	
	public MainMenuPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT);
	}
	
	Transform origTransform = APP.platform.createTransform();
	Transform menuTrans = APP.platform.createTransform();
	
	public void paint(RenderingContext ctxt) {
		Menu menu = (Menu)APP.model;
		
		ctxt.getTransform(origTransform);
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.DARK_GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		ctxt.getTransform(menuTrans);
		
		ctxt.translate(
				aabb.width/2 - Math.min(BYPASSAPP.titleBackground.getWidth(), aabb.width)/2,
				aabb.height/2 - BYPASSAPP.titleBackground.getHeight()/2);
		
		ctxt.paintImage(BYPASSAPP.titleBackground,
				0, 0, Math.min(BYPASSAPP.titleBackground.getWidth(), (int)aabb.width), BYPASSAPP.titleBackground.getHeight(),
				0, 0, Math.min(BYPASSAPP.titleBackground.getWidth(), (int)aabb.width), BYPASSAPP.titleBackground.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(aabb.width/2 - BYPASSAPP.title_white.getWidth()/2, TITLE_CENTER_Y - BYPASSAPP.title_white.getHeight()/2);
		ctxt.paintImage(BYPASSAPP.title_white,
				0, 0, BYPASSAPP.title_white.getWidth(), BYPASSAPP.title_white.getHeight(),
				0, 0, BYPASSAPP.title_white.getWidth(), BYPASSAPP.title_white.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(aabb.width/2 - BYPASSAPP.copyright.getWidth()/2, COPYRIGHT_CENTER_Y - BYPASSAPP.copyright.getHeight()/2);
		ctxt.paintImage(BYPASSAPP.copyright,
				0, 0, BYPASSAPP.copyright.getWidth(), BYPASSAPP.copyright.getHeight(),
				0, 0, BYPASSAPP.copyright.getWidth(), BYPASSAPP.copyright.getHeight());
		
		ctxt.setTransform(menuTrans);
		
		menu.paint_panel(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
}
