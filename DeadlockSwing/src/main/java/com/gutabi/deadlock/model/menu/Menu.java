package com.gutabi.deadlock.model.menu;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class Menu {
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
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
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void click(Point p) {
		
		MenuItem item = hitTest(p);
		
		if (item != null) {
			item.action();
		}
		
	}
	
	public void downKey() {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.down;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.down;
		}
		
		VIEW.repaintCanvas();
		
	}
	
	public void upKey() {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.up;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.up;
		}
		
		VIEW.repaintCanvas();
		
	}
	
	public void move(Point p) {
		MenuItem hit = hitTest(p);
		if (hit != null && hit.active) {
			hilited = hit;
		}
		
		VIEW.repaintCanvas();
	}
	
	public abstract void render(RenderingContext ctxt);
	
	public abstract void paint(RenderingContext ctxt);
	
}
