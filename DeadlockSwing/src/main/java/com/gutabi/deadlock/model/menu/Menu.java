package com.gutabi.deadlock.model.menu;

import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class Menu {
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	public abstract void paint(RenderingContext ctxt);
	
}
