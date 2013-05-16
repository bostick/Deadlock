package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.math.Point;

public abstract class MainMenu extends BypassMenu {
	
	public static Point tmpMainMenuLoc;
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void stop() {
		
	}
	
	public static void surfaceChanged(int width, int height) {
		
		BypassMenu.BYPASSMENU.lock.lock();
		try {
			
			APP.appScreen.postDisplay(width, height);
			
			if (tmpPanelOffset != null) {
				BypassMenu.BYPASSMENU.panelOffset = tmpPanelOffset;
				tmpPanelOffset = null;
			}
			BypassMenu.BYPASSMENU.render();
			if (tmpMainMenuLoc != null) {
				BypassMenu.BYPASSMENU.setLocation(tmpMainMenuLoc);
				tmpMainMenuLoc = null;
			}
			
		} finally {
			BypassMenu.BYPASSMENU.lock.unlock();
		}
		
		/*
		 * repaint once just in case there is nothing else driving repainting (like shimmering)
		 */
		APP.appScreen.contentPane.repaint();
	}
	
	public static void pause() {
		
		simThreadTrigger.set(false);
		
		try {
			simThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * we want to save tmpLoc when 
		 */
		BypassMenu.BYPASSMENU.lock.lock();
		try {
			
			if (BypassMenu.BYPASSMENU.rendered) {
				tmpMainMenuLoc = new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y);
			}
			
		} finally {
			BypassMenu.BYPASSMENU.lock.unlock();
		}
		
	}
	
	public void escape() {
		
	}

}
