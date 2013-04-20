package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;

public abstract class MenuPanel extends Panel {
	
	public void postDisplay() {
		Menu menu = (Menu)APP.model;
		
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
	
}
