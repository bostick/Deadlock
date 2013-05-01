package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.Panel;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.sprites.SpriteSheet.SpriteSheetSprite;

public class BypassMenuPanel extends Panel {
	
	public void postDisplay(int width, int height) {
		Menu menu = (Menu)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		menu.postDisplay(width, height);
	}
	
	public void paint(RenderingContext ctxt) {
		Menu menu = (Menu)APP.model;
		
		menu.lock.lock();
		try {
			
			ctxt.pushTransform();
			
			ctxt.translate(aabb.x, aabb.y);
			
			for (int i = 0; i < (int)Math.ceil(aabb.height / 32); i++) {
				for (int j = 0; j < (int)Math.ceil(aabb.width / 32); j++) {
					APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASSTILE, 32 * j, 32 * i, 32 * j + 32, 32 * i + 32);
				}
			}
			
			ctxt.pushTransform();
			
			ctxt.translate(0, 0);
			ctxt.paintImage(BYPASSAPP.logo,
					0, 0, (int)aabb.width, (int)((aabb.width * BYPASSAPP.logo.getHeight()) / BYPASSAPP.logo.getWidth()),
					0, 0, BYPASSAPP.logo.getWidth(), BYPASSAPP.logo.getHeight());
			
			ctxt.popTransform();
			
			ctxt.pushTransform();
			
			int w = (int)Math.min(BYPASSAPP.copyright.getWidth(), aabb.width);
			int h = (w * BYPASSAPP.copyright.getHeight()) / BYPASSAPP.copyright.getWidth();
			ctxt.translate(aabb.width/2 - w/2, aabb.height - h);
			ctxt.paintImage(BYPASSAPP.copyright,
					0, 0, w, h,
					0, 0, BYPASSAPP.copyright.getWidth(), BYPASSAPP.copyright.getHeight());
			
			ctxt.popTransform();
			
			ctxt.pushTransform();
			
			menu.paint_panel(ctxt);
			
			ctxt.popTransform();
			
			ctxt.popTransform();
			
		} finally {
			menu.lock.unlock();
		}
		
	}
}
