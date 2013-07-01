package com.brentonbostick.bypass.menu;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.brentonbostick.capsloc.Model;
import com.brentonbostick.capsloc.SimulationRunnable;
import com.brentonbostick.capsloc.ui.Menu;

public abstract class BypassMenu extends Menu implements Model {
	
	public ReentrantLock lock = new ReentrantLock(true);
	public boolean rendered;
	
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
			
			if (APP.model != BypassMenu.BYPASSMENU) {
				throw new AssertionError(APP.model);
			}
			
			APP.appScreen.postDisplay(width, height);
			
			BypassMenu.BYPASSMENU.render();
			
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
