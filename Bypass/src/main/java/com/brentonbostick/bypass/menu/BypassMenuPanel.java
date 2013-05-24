package com.brentonbostick.bypass.menu;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.Resource;
import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.Image;
import com.brentonbostick.capsloc.ui.Menu;
import com.brentonbostick.capsloc.ui.Panel;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.sprites.SpriteSheet.SpriteSheetSprite;

public class BypassMenuPanel extends Panel {
	
	public Image logo;
	public Image copyright;
	
	double logoAdjustedWidth;
	double logoAdjustedHeight;
	double copyAdjustedWidth;
	double copyAdjustedHeight;
	
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
		
		logoAdjustedWidth = aabb.width;
		logoAdjustedHeight = (aabb.width * logo.getHeight()) / logo.getWidth();
		
		copyAdjustedWidth = Math.min(copyright.getWidth(), aabb.width);
		copyAdjustedHeight = (copyAdjustedWidth * copyright.getHeight()) / copyright.getWidth();
		
		if (menu.panelOffset == null) {
			/*
			 * initialize panelOffset and aabb with proper values
			 */
			
			menu.panelOffset = new Point(0.0, logoAdjustedHeight);
			menu.panelOffsetBR = new Point(0.0 + width, logoAdjustedHeight + ((height - copyAdjustedHeight) - logoAdjustedHeight));
			
			menu.aabb = new AABB(menu.panelOffset.x, menu.panelOffset.y, menu.aabb.width, menu.aabb.height);
			
		}
		
		menu.postDisplay(width, (int)((height - copyAdjustedHeight) - logoAdjustedHeight));
	}
	
	public void paint(RenderingContext ctxt) {
		BypassMenu menu = (BypassMenu)APP.model;
		
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
			
			ctxt.translate(aabb.width/2 - logoAdjustedWidth/2, 0);
			
			ctxt.setAntialiasing();
			ctxt.paintImage(logo, 1.0,
					0, 0, logoAdjustedWidth, logoAdjustedHeight,
					0, 0, logo.getWidth(), logo.getHeight());
			ctxt.clearAntialiasing();
			
			ctxt.popTransform();
			
			ctxt.pushTransform();
			
			ctxt.translate(aabb.width/2 - copyAdjustedWidth/2, aabb.height - copyAdjustedHeight);
			ctxt.paintImage(copyright, 1.0,
					0, 0, copyAdjustedWidth, copyAdjustedHeight,
					0, 0, copyright.getWidth(), copyright.getHeight());
			
			ctxt.popTransform();
			
			menu.paint(ctxt);
			
			ctxt.popTransform();
			
		} finally {
			menu.lock.unlock();
		}
		
	}
}
