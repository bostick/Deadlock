package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class BypassMenuPanel extends Panel {
	
	public void postDisplay(int width, int height) {
		Menu menu = (Menu)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		double x = menu.aabb.x;
		double y = menu.aabb.y;
		
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
			
		}
		
		menu.setLocation(x, y);
	}
	
//	Transform origTransform = APP.platform.createTransform();
//	Transform menuTrans = APP.platform.createTransform();
	
	public void paint(RenderingContext ctxt) {
		Menu menu = (Menu)APP.model;
		
		menu.lock.lock();
		try {
			
			if (!menu.ready) {
				
				return;
			}
			
			ctxt.pushTransform();
			
			ctxt.translate(aabb.x, aabb.y);
			
			ctxt.setColor(Color.DARK_GRAY);
			ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
			
			ctxt.pushTransform();
			
			ctxt.translate(
					aabb.width/2 - BYPASSAPP.titleBackground.getWidth()/2,
					aabb.height/2 - BYPASSAPP.titleBackground.getHeight()/2);
			
			ctxt.paintImage(BYPASSAPP.titleBackground,
					0, 0, BYPASSAPP.titleBackground.getWidth(), BYPASSAPP.titleBackground.getHeight(),
					0, 0, BYPASSAPP.titleBackground.getWidth(), BYPASSAPP.titleBackground.getHeight());
			
			ctxt.popTransform();
			
			ctxt.pushTransform();
			
			int TITLE_CENTER_Y = 120 + BYPASSAPP.title_white.getHeight()/2;
			int COPYRIGHT_CENTER_Y = (int)aabb.height - 100 - BYPASSAPP.copyright.getHeight()/2;
			
			ctxt.translate(aabb.width/2 - BYPASSAPP.title_white.getWidth()/2, TITLE_CENTER_Y - BYPASSAPP.title_white.getHeight()/2);
			ctxt.paintImage(BYPASSAPP.title_white,
					0, 0, BYPASSAPP.title_white.getWidth(), BYPASSAPP.title_white.getHeight(),
					0, 0, BYPASSAPP.title_white.getWidth(), BYPASSAPP.title_white.getHeight());
			
			ctxt.popTransform();
			
			ctxt.pushTransform();
			
			ctxt.translate(aabb.width/2 - BYPASSAPP.copyright.getWidth()/2, COPYRIGHT_CENTER_Y - BYPASSAPP.copyright.getHeight()/2);
			ctxt.paintImage(BYPASSAPP.copyright,
					0, 0, BYPASSAPP.copyright.getWidth(), BYPASSAPP.copyright.getHeight(),
					0, 0, BYPASSAPP.copyright.getWidth(), BYPASSAPP.copyright.getHeight());
			
			ctxt.popTransform();
			
			menu.paint_panel(ctxt);
			
			ctxt.popTransform();
			
		} finally {
			menu.lock.unlock();
		}
		
	}
}
