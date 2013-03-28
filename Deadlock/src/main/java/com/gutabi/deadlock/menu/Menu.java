package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Menu {
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	double menuItemWidest;
	int totalMenuItemHeight;
	
	public double menuWidth;
	public double menuHeight;
	
	public void add(MenuItem item) {
		
		if (items.isEmpty()) {
			firstMenuItem = item;
		}
		
		items.add(item);
		
		int i = items.size()-1;
		
		MenuItem prev = items.get((i-1 + items.size()) % items.size());
		MenuItem first = items.get(0);
		
		prev.down = item;
		
		item.up = prev;
		item.down = first;
		
		first.up = item;
		
	}
	
	public void paint_pixels(RenderingContext ctxt) {
		
		ctxt.setColor(Color.menuBackground);
		ctxt.fillRect(
				(int)(APP.MENU_WIDTH/2 - menuItemWidest/2 - 5),
				(int)(APP.MENU_CENTER_Y - menuHeight/2 - 5),
				(int)(menuWidth + 5 + 5),
				(int)(menuHeight + 5 + 5));
		
		for (MenuItem item : items) {
			item.paint(ctxt);
		}
		
		if (hilited != null) {
			hilited.paintHilited(ctxt);			
		}
	}
	
}
