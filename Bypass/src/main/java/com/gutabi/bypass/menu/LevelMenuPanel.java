package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.bypass.BypassApplication.BYPASSAPP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class LevelMenuPanel extends Panel {
	
	int TITLE_CENTER_Y = 165;
	int COPYRIGHT_CENTER_Y = 800;
	
	public LevelMenuPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT);
	}
	
	/**
	 * set menu location using panel dimensions
	 */
	public void postDisplay() {
		Menu menu = (Menu)APP.model;
		
		double x;
		double y;
		
		if (DMath.lessThanEquals(menu.aabb.width, aabb.width)) {
			/*
			 * no scrolling
			 */
			
			menu.hScrollable = false;
			
			x = aabb.width/2 - menu.aabb.width/2;
		} else {
			/*
			 * will be scrolling
			 */
			
			menu.hScrollable = true;
			
			x = 0;
		}
		
		if (DMath.lessThanEquals(menu.aabb.height, aabb.height)) {
			/*
			 * no scrolling
			 */
			
			menu.vScrollable = false;
			
			y = aabb.height/2 - menu.aabb.height/2;
		} else {
			/*
			 * will be scrolling
			 */
			
			menu.vScrollable = true;
			
			y = 0;
		}
		
		menu.setLocation(x, y);
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
		
//		int x = (int)menuTrans.getTranslateX();
//		int y = (int)menuTrans.getTranslateY();
		
		menu.paint_panel(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
}
