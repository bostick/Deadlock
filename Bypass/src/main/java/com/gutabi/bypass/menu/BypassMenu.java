package com.gutabi.bypass.menu;

import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.ui.Menu;

public abstract class BypassMenu extends Menu implements Model {
	
	public static BypassMenu BYPASSMENU;
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void start() {
		
	}
	
	public static void stop() {
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public double getTime() {
		return 0.0;
	}
	
	public boolean integrate(double t) {
		
		if (!rendered) {
			return false;
		}
		
		boolean res = false;
		
		res = res | shimmer.step();
		
		return res;
	}
	
}
