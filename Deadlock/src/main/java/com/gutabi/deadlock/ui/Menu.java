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
	
	public double menuItemWidestCol0;
	public int totalMenuItemHeightCol0;
	public double menuItemWidestCol1;
	public int totalMenuItemHeightCol1;
	public double menuItemWidestCol2;
	public int totalMenuItemHeightCol2;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	public Point lastMovedMenuPoint;
	public Point lastClickedMenuPoint;
	
	public Menu() {
		
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
		
	}
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void render() {
		
		/*
		 * col 0
		 */
		int itemsCol0 = 0;
		for (MenuItem item : items) {
			if (item.c != 0) {
				continue;
			}
			if ((int)item.localAABB.width > menuItemWidestCol0) {
				menuItemWidestCol0 = (int)item.localAABB.width;
			}
			totalMenuItemHeightCol0 += (int)item.localAABB.height;
			itemsCol0++;
		}
		
		double menuHeightCol0 = totalMenuItemHeightCol0 + 10 * (itemsCol0 - 1);
		
		
		/*
		 * col 1
		 */
		int itemsCol1 = 0;
		for (MenuItem item : items) {
			if (item.c != 1) {
				continue;
			}
			if ((int)item.localAABB.width > menuItemWidestCol1) {
				menuItemWidestCol1 = (int)item.localAABB.width;
			}
			totalMenuItemHeightCol1 += (int)item.localAABB.height;
			itemsCol1++;
		}
		
		double menuHeightCol1 = totalMenuItemHeightCol1 + 10 * (itemsCol1 - 1);
		
		
		/*
		 * col 2
		 */
		int itemsCol2 = 0;
		for (MenuItem item : items) {
			if (item.c != 2) {
				continue;
			}
			if ((int)item.localAABB.width > menuItemWidestCol2) {
				menuItemWidestCol2 = (int)item.localAABB.width;
			}
			totalMenuItemHeightCol2 += (int)item.localAABB.height;
			itemsCol2++;
		}
		
		double menuHeightCol2 = totalMenuItemHeightCol2 + 10 * (itemsCol2 - 1);
		
		
		aabb = new AABB(aabb.x, aabb.y, menuItemWidestCol0 + 10 + menuItemWidestCol1 + 10 + menuItemWidestCol2, Math.max(Math.max(menuHeightCol0, menuHeightCol1), menuHeightCol2));
		
		Image tmpImg = APP.platform.createImage((int)aabb.width, (int)aabb.height);
		
		RenderingContext ctxt = APP.platform.createRenderingContext(tmpImg);
		
		Transform origTransform = ctxt.getTransform();
		
		/*
		 * col 0
		 */
		boolean itemFound = false;
		int curRow = 0;
		while (true) {
			itemFound = false;
			for (MenuItem item : items) {
				if (item.c != 0) {
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
		
		ctxt.setTransform(origTransform);
		ctxt.translate(menuItemWidestCol0 + 10, 0);
		
		/*
		 * col 1
		 */
		curRow = 0;
		while (true) {
			itemFound = false;
			for (MenuItem item : items) {
				if (item.c != 1) {
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
		
		ctxt.setTransform(origTransform);
		ctxt.translate(menuItemWidestCol0 + 10, 0);
		ctxt.translate(menuItemWidestCol1 + 10, 0);
		
		/*
		 * col 2
		 */
		curRow = 0;
		while (true) {
			itemFound = false;
			for (MenuItem item : items) {
				if (item.c != 2) {
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
		
		ctxt.dispose();
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
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
