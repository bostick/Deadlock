package com.gutabi.bypass.android.lite;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.menu.BypassMenu;
import com.gutabi.bypass.menu.BypassMenuPanel;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.MenuTool;

public class MainMenu extends BypassMenu {
	
	public MainMenu() {
		
		MenuItem newMenuItem = new MenuItem(MainMenu.this, " Episode 1 ") {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.bypassPlatform.levelDB("episode1");
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(newMenuItem, 0, 0);
		
		MenuItem resumeMenuItem = new MenuItem(MainMenu.this, "Tutorial") {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.bypassPlatform.levelDB("tutorial");
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(resumeMenuItem, 1, 0);
		
	}
	
	static AtomicBoolean simThreadTrigger = new AtomicBoolean();
	static Thread simThread;
	
	public static void resume() {
		
		BypassMenu.BYPASSMENU = new MainMenu();
		APP.model = BypassMenu.BYPASSMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		APP.tool = new MenuTool();
		
		simThreadTrigger.set(true);
		
		simThread = new Thread(new SimulationRunnable(simThreadTrigger));
		simThread.start();
	}
	
	public static void surfaceChanged(int width, int height) {
		
		BypassMenu.BYPASSMENU.lock.lock();
		
		APP.appScreen.postDisplay(width, height);
		
		BypassMenu.BYPASSMENU.render();
		
		BypassMenu.BYPASSMENU.lock.unlock();
	}
	
	public static void pause() {
		
		simThreadTrigger.set(false);
		
		try {
			simThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		simThread = null;
		
		BypassMenu.BYPASSMENU = null;
		
	}
	
	public void escape() {
		
	}

}
