package com.gutabi.deadlock.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Menu {
	
	public boolean ready;
	public Lock lock = new ReentrantLock(true);
	
	public List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	public MenuItem shimmeringMenuItem;
	
	int rows;
	int cols;
	public double[] menuItemWidest;
	public double[] menuHeight;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	public boolean hScrollable;
	public boolean vScrollable;
	
	Shimmer shimmer;
	
	public Menu() {
		menuItemWidest = new double[0];
		menuHeight = new double[cols];
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void setLocation(Point p) {
		aabb = new AABB(p.x, p.y, aabb.width, aabb.height);
	}
	
	public void add(MenuItem item, int r, int c) {
		
		item.r = r;
		item.c = c;
		
		if (items.isEmpty()) {
			firstMenuItem = item;
			shimmeringMenuItem = item;
		}
		
		items.add(item);
		
		int i = items.size()-1;
		
		MenuItem prev = items.get((i-1 + items.size()) % items.size());
		MenuItem first = items.get(0);
		
		prev.down = item;
		
		item.up = prev;
		item.down = first;
		
		first.up = item;
		
		if (r == rows) {
			rows++;
		}
		if (c == cols) {
			cols++;
			menuItemWidest = new double[cols];
			menuHeight = new double[cols];
		}
	}
	
	public void pressed(InputEvent ev) {
		
		MenuItem hit = hitTest(ev.p);
		if (hit != null && hit.active) {
			hilited = hit;
		} else {
			hilited = null;
		}
	}
	
	public void released(InputEvent ev) {
		
		MenuItem item = hitTest(ev.p);
		
		if (item != null && item == hilited && item.active) {
			hilited = null;
			item.action();
		} else {
			hilited = null;
		}
	}
	
	public void dragToNewLocation(Point newLoc) {
		
		hilited = null;
		
		setLocation(newLoc);
	}
	
	public void moved(InputEvent ev) {
		
	}
	
	public void clicked(InputEvent ev) {
		
	}
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public abstract void escape();
	
	
	
	public void render() {
		
		for (int i = 0; i < cols; i++) {
			int itemsCol = 0;
			double totalMenuItemHeight = 0.0;
			for (int j = 0; j < items.size(); j++) {
				MenuItem item = items.get(j);
				if (item.c != i) {
					continue;
				}
				if ((int)item.localAABB.width > menuItemWidest[i]) {
					menuItemWidest[i] = (int)item.localAABB.width;
				}
				totalMenuItemHeight += (int)item.localAABB.height;
				itemsCol++;
			}
			
			menuHeight[i] = totalMenuItemHeight + 10 * (itemsCol - 1);
		}
		
		double width = 0.0;
		for (int i = 0; i < cols; i++) {
			width += menuItemWidest[i];
			if (i < cols-1) {
				width += 10;
			}
		}
		double height = 0.0;
		for (int i = 0; i < cols; i++) {
			if (menuHeight[i] > height) {
				height = menuHeight[i];
			}
		}
		aabb = new AABB(aabb.x, aabb.y, width+1, height+1);
		
		int x;
		int y;
		
		for (int i = 0; i < cols; i++) {
			
			x = 0;
			y = 0;
			
			for (int j = 0; j < i; j++) {
				x = x+(int)menuItemWidest[j] + 10;
			}
			
			boolean itemFound = false;
			int curRow = 0;
			while (true) {
				itemFound = false;
				for (int j = 0; j < items.size(); j++) {
					MenuItem item = items.get(j);
					if (item.c != i) {
						continue;
					}
					if (item.r != curRow) {
						continue;
					}
					itemFound = true;
					item.aabb = new AABB(x, y, item.aabb.width, item.aabb.height);
					item.render();
					y = y + (int)item.aabb.height + 10;
				}
				if (!itemFound) {
					break;
				} else {
					curRow++;
				}
			}
		}
		
		shimmer = new Shimmer(shimmeringMenuItem.aabb, System.currentTimeMillis());
		
	}
	
	
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.menuBackground);
		ctxt.fillRect(
				(int)(-5),
				(int)(-5),
				(int)(aabb.width + 5 + 5),
				(int)(aabb.height + 5 + 5));
		
		for (int i = 0; i < items.size(); i++) {
			MenuItem item = items.get(i);
			item.paint(ctxt);
		}
		
		if (hilited != null) {
			hilited.paintHilited(ctxt);			
		}
		
		shimmer.paint(ctxt);
		
	}
}
