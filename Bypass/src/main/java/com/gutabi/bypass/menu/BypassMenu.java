package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Menu;

public abstract class BypassMenu extends Menu implements Model {
	
	public ReentrantLock lock = new ReentrantLock(true);
	public boolean rendered;
	
	public static Point tmpLoc;
	public static Point tmpPanelOffset;
	
	public static BypassMenu BYPASSMENU;
	
	public Menu getMenu() {
		return this;
	}
	
	public double getTime() {
		return 0.0;
	}
	
	
	static AtomicBoolean simThreadTrigger = new AtomicBoolean();
	static Thread simThread;
	
	public static void resume() {
		
		simThreadTrigger.set(true);
		
		simThread = new Thread(new SimulationRunnable(simThreadTrigger));
		simThread.start();
		
	}
	
	public static void surfaceChanged(int width, int height) {
		
		BypassMenu.BYPASSMENU.lock.lock();
		try {
			
			APP.appScreen.postDisplay(width, height);
			
			if (tmpPanelOffset != null) {
				BypassMenu.BYPASSMENU.panelOffset = tmpPanelOffset;
			}
			BypassMenu.BYPASSMENU.render();
			if (tmpLoc != null) {
				BypassMenu.BYPASSMENU.setLocation(tmpLoc);
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
		
		BypassMenu.BYPASSMENU.lock.lock();
		try {
			
			if (BypassMenu.BYPASSMENU.rendered) {
				tmpLoc = new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y);
			}
			
		} finally {
			BypassMenu.BYPASSMENU.lock.unlock();
		}
		
	}
	
	public boolean integrate(double t) {
		
		if (!rendered) {
			return false;
		}
		
		boolean res = false;
		
		res = res | super.integrate(t);
		
		return res;
	}
	
	public void postDisplay(int width, int height) {
		if (!lock.isHeldByCurrentThread()) {
			throw new AssertionError();
		}
		
		super.postDisplay(width, height);
	}
	
	public void render() {
		if (!lock.isHeldByCurrentThread()) {
			throw new AssertionError();
		}
		
		super.render();
		
		rendered = true;
	}
	
}
