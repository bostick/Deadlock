package com.gutabi.capsloc.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class Menu {
	
	public Lock lock = new ReentrantLock(true);
	
	public List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	public MenuItem shimmeringMenuItem;
	
	int rows;
	int cols;
	public double[] columnWidth;
	public double[] columnHeight;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	/*
	 * fraction of panel that menu takes
	 */
	public double widthFraction = 0.666;
	public double scale = -1;
	public double menuWidth;
	public double menuHeight;
	
	
	public boolean hScrollable;
	public boolean vScrollable;
	
	Shimmer shimmer;
	
	public Menu() {
		columnWidth = new double[0];
		columnHeight = new double[cols];
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
			columnWidth = new double[cols];
			columnHeight = new double[cols];
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
				if ((int)item.localAABB.width > columnWidth[i]) {
					columnWidth[i] = (int)item.localAABB.width;
				}
				totalMenuItemHeight += (int)item.localAABB.height;
				itemsCol++;
			}
			
			columnHeight[i] = totalMenuItemHeight + 10 * (itemsCol - 1);
		}
		
		double width = 0.0;
		for (int i = 0; i < cols; i++) {
			width += columnWidth[i];
			if (i < cols-1) {
				width += 10;
			}
		}
		double height = 0.0;
		for (int i = 0; i < cols; i++) {
			if (columnHeight[i] > height) {
				height = columnHeight[i];
			}
		}
		menuWidth = width+1;
		menuHeight = height+1;
		
		
		int x;
		int y;
		
		for (int i = 0; i < cols; i++) {
			
			x = 0;
			y = 0;
			
			for (int j = 0; j < i; j++) {
				x = x+(int)columnWidth[j] + 10;
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
		
		shimmer = new Shimmer(System.currentTimeMillis());
		shimmer.setShape(shimmeringMenuItem.aabb);
		
	}
	
	public void postDisplay(int width, int height) {
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		double x = aabb.x;
		double y = aabb.y;
		
		double s = (widthFraction * width) / menuWidth;
		
		scale = s;
		aabb = new AABB(aabb.x, aabb.y, scale * menuWidth, scale * menuHeight);
		
		if (DMath.lessThanEquals(aabb.width, width)) {
			/*
			 * no scrolling
			 */
			
			hScrollable = false;
			
			x = width/2 - aabb.width/2;
			
		} else {
			/*
			 * will be scrolling
			 */
			
			hScrollable = true;
			
		}
		
		if (DMath.lessThanEquals(aabb.height, height)) {
			/*
			 * no scrolling
			 */
			
			vScrollable = false;
			
			y = height/2 - aabb.height/2;
			
		} else {
			/*
			 * will be scrolling
			 */
			
			vScrollable = true;
			
		}
		
		setLocation(x, y);
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.menuBackground);
		ctxt.fillRect(
				(-5),
				(-5),
				(int)(aabb.width + 5 + 5),
				(int)(aabb.height + 5 + 5));
		
		ctxt.scale(scale);
		
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
