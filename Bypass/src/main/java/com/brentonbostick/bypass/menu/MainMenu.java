package com.brentonbostick.bypass.menu;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.AppScreen;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.ContentPane;
import com.brentonbostick.capsloc.ui.MenuTool;

public abstract class MainMenu extends BypassMenu {
	
	public static MainMenu MAINMENU;
	
	public static Point tmpMainMenuLoc;
	
	public abstract void updateFirstUnplayed();
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void start() {
		
		APP.tool = new MenuTool();
		
		BypassMenu.BYPASSMENU = MainMenu.MAINMENU;
		APP.model = BypassMenu.BYPASSMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
	}
	
	public static void stop() {
		
	}
	
	public void escape() {
		
	}

}
