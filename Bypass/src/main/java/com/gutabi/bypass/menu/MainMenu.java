package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.MenuTool;

public abstract class MainMenu extends BypassMenu {
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void start() {
		
		APP.tool = new MenuTool();
		
		BypassMenu.BYPASSMENU = new MainMenuFull();
		APP.model = BypassMenu.BYPASSMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
	}
	
	public static void stop() {
		
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
		
		APP.appScreen.postDisplay(width, height);
		
		BypassMenu.BYPASSMENU.render();
		
		BypassMenu.BYPASSMENU.lock.unlock();
		
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
	
	public void escape() {
		
	}

}
