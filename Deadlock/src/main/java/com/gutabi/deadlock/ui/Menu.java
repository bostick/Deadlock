package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Menu {
	
	private List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	int rows;
	int cols;
	public double[] menuItemWidest;
	public double[] menuHeight;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	public Point lastMovedMenuPoint;
	public Point lastClickedMenuPoint;
	
	boolean isRendered;
	
	public Menu() {
		menuItemWidest = new double[0];
		menuHeight = new double[cols];
	}
	
	public void add(MenuItem item, int r, int c) {
		
		item.r = r;
		item.c = c;
		
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
		
		if (r == rows) {
			rows++;
		}
		if (c == cols) {
			cols++;
			menuItemWidest = new double[cols];
			menuHeight = new double[cols];
		}
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
			for (MenuItem item : items) {
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
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		Image tmpImg = APP.platform.createImage((int)aabb.width, (int)aabb.height);
		
		RenderingContext ctxt = APP.platform.createRenderingContext(tmpImg);
		
		Transform origTransform = ctxt.getTransform();
		
		for (int i = 0; i < cols; i++) {
			
			ctxt.setTransform(origTransform);
			
			for (int j = 0; j < i; j++) {
				ctxt.translate(menuItemWidest[j] + 10, 0);
			}
			
			boolean itemFound = false;
			int curRow = 0;
			while (true) {
				itemFound = false;
				for (MenuItem item : items) {
					if (item.c != i) {
						continue;
					}
					if (item.r != curRow) {
						continue;
					}
					itemFound = true;
					item.render(ctxt);
					ctxt.translate(0, item.localAABB.height + 10);
				}
				if (!itemFound) {
					break;
				} else {
					curRow++;
				}
			}
		}
		
		ctxt.dispose();
		
		isRendered = true;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		assert isRendered;
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.menuBackground);
		ctxt.fillRect(
				(int)(-5),
				(int)(-5),
				(int)(aabb.width + 5 + 5),
				(int)(aabb.height + 5 + 5));
		
		for (MenuItem item : items) {
			item.paint(ctxt);
		}
		
		if (hilited != null) {
			hilited.paintHilited(ctxt);			
		}
		
		ctxt.setTransform(origTransform);
	}
}
