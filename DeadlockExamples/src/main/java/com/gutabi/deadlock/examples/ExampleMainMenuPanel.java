package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import static com.gutabi.deadlock.examples.ExampleApplication.EXAMPLEAPP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class ExampleMainMenuPanel extends Panel {
	
	int TITLE_CENTER_Y = 165;
	int COPYRIGHT_CENTER_Y = 800;
	
	public ExampleMainMenuPanel() {
		
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
	Transform menuTransform = APP.platform.createTransform();
	
	public void paint(RenderingContext ctxt) {
		Menu menu = (Menu)APP.model;
		
		ctxt.getTransform(origTransform);
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.DARK_GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		ctxt.getTransform(menuTransform);
		
		ctxt.translate(
				aabb.width/2 - Math.min(EXAMPLEAPP.titleBackground.getWidth(), aabb.width)/2,
				aabb.height/2 - EXAMPLEAPP.titleBackground.getHeight()/2);
		
		ctxt.paintImage(EXAMPLEAPP.titleBackground,
				0, 0, Math.min(EXAMPLEAPP.titleBackground.getWidth(), (int)aabb.width), EXAMPLEAPP.titleBackground.getHeight(),
				0, 0, Math.min(EXAMPLEAPP.titleBackground.getWidth(), (int)aabb.width), EXAMPLEAPP.titleBackground.getHeight());
		
		ctxt.setTransform(menuTransform);
		
		ctxt.translate(aabb.width/2 - EXAMPLEAPP.title_white.getWidth()/2, TITLE_CENTER_Y - EXAMPLEAPP.title_white.getHeight()/2);
		ctxt.paintImage(EXAMPLEAPP.title_white,
				0, 0, EXAMPLEAPP.title_white.getWidth(), EXAMPLEAPP.title_white.getHeight(),
				0, 0, EXAMPLEAPP.title_white.getWidth(), EXAMPLEAPP.title_white.getHeight());
		
		ctxt.setTransform(menuTransform);
		
		ctxt.translate(aabb.width/2 - EXAMPLEAPP.copyright.getWidth()/2, COPYRIGHT_CENTER_Y - EXAMPLEAPP.copyright.getHeight()/2);
		ctxt.paintImage(EXAMPLEAPP.copyright,
				0, 0, EXAMPLEAPP.copyright.getWidth(), EXAMPLEAPP.copyright.getHeight(),
				0, 0, EXAMPLEAPP.copyright.getWidth(), EXAMPLEAPP.copyright.getHeight());
		
		ctxt.setTransform(menuTransform);
		
		menu.paint_panel(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
}
