package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MenuItem;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Menu {
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	public double menuItemWidest;
	public int totalMenuItemHeight;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
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
	
	private MenuItem hitTest(Point p) {
		
		for (MenuItem item : items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void moved(Point mP) {
		
		MenuItem hit = hitTest(mP);
		if (hit != null && hit.active) {
			hilited = hit;
		} else {
			hilited = null;
		}
		
	}
	
	public void clicked(Point mP) {
		
		MenuItem item = hitTest(mP);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
	public void render() {
		
		for (MenuItem item : items) {
			if ((int)item.localAABB.width > menuItemWidest) {
				menuItemWidest = (int)item.localAABB.width;
			}
			totalMenuItemHeight += (int)item.localAABB.height;
		}
		
		double menuHeight = totalMenuItemHeight + 10 * (items.size() - 1);
		
		aabb = new AABB(aabb.x, aabb.y, menuItemWidest, menuHeight);
		
		Image tmpImg = APP.platform.createImage((int)aabb.width, (int)aabb.height);
		
		RenderingContext ctxt = APP.platform.createRenderingContext(tmpImg);
		
		for (MenuItem item : items) {
			item.render(ctxt);
			ctxt.translate(0, item.localAABB.height + 10);
		}
		
		ctxt.dispose();
		
	}
	
	public void paint_pixels(RenderingContext ctxt) {
		
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
