package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.Panel;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.sprites.SpriteSheet.SpriteSheetSprite;

public class BypassMenuPanel extends Panel {
	
	public Image logo;
	public Image copyright;
	
	public BypassMenuPanel() {
		
		Resource logoRes = APP.platform.imageResource("logo");
		Resource copyRes = APP.platform.imageResource("copyright");
		
		try {
			
			logo = APP.platform.readImage(logoRes);
			copyright = APP.platform.readImage(copyRes);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void postDisplay(int width, int height) {
		Menu menu = (Menu)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		menu.postDisplay(width, height);
	}
	
	public void paint(RenderingContext ctxt) {
		Menu menu = (Menu)APP.model;
		
		menu.lock.lock();
		try {
			
			if (!menu.rendered) {
				return;
			}
			
			ctxt.pushTransform();
			
			ctxt.translate(aabb.x, aabb.y);
			
			for (int i = 0; i < (int)Math.ceil(aabb.height / 32); i++) {
				for (int j = 0; j < (int)Math.ceil(aabb.width / 32); j++) {
					APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASSTILE, 32 * j, 32 * i, 32 * j + 32, 32 * i + 32);
				}
			}
			
			ctxt.pushTransform();
			
			ctxt.translate(0, 0);
			ctxt.paintImage(logo,
					0, 0, (int)aabb.width, (int)((aabb.width * logo.getHeight()) / logo.getWidth()),
					0, 0, logo.getWidth(), logo.getHeight());
			
			ctxt.popTransform();
			
			ctxt.pushTransform();
			
			int w = (int)Math.min(copyright.getWidth(), aabb.width);
			int h = (w * copyright.getHeight()) / copyright.getWidth();
			ctxt.translate(aabb.width/2 - w/2, aabb.height - h);
			ctxt.paintImage(copyright,
					0, 0, w, h,
					0, 0, copyright.getWidth(), copyright.getHeight());
			
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
