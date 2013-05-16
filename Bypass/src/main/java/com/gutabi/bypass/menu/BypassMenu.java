package com.gutabi.bypass.menu;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Menu;

public abstract class BypassMenu extends Menu implements Model {
	
	public ReentrantLock lock = new ReentrantLock(true);
	public boolean rendered;
	
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
