package com.gutabi.capsloc.ui;

import static com.gutabi.capsloc.CapslocApplication.APP;

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
	
	public boolean rendered;
	
	public Lock lock = new ReentrantLock(true);
	
	public List<List<MenuItem>> tree = new ArrayList<List<MenuItem>>();
	public List<AABB> colAABBs = new ArrayList<AABB>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	public MenuItem shimmeringMenuItem;
	
	int rows;
	int cols;
	public double[] columnWidth;
	public double[] columnHeight;
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	int parWidth;
	int parHeight;
	
	/*
	 * fraction of panel that menu takes
	 */
	public double widthFraction = 0.666;
	public double scale = -1;
	public double menuWidth;
	public double menuHeight;
	
	
	public boolean hScrollable;
	public boolean vScrollable;
	
	protected Shimmer shimmer;
	
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
		
		if (tree.isEmpty()) {
			firstMenuItem = item;
			shimmeringMenuItem = item;
		}
		
		List<MenuItem> col;
		if (tree.size() <= c) {
			col = new ArrayList<MenuItem>();
			tree.add(c, col);
		} else {
			col = tree.get(c);
		}
		
		col.add(r, item);
		
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
		
		APP.appScreen.contentPane.repaint();
	}
	
	public void released(InputEvent ev) {
		
		MenuItem item = hitTest(ev.p);
		
		if (item != null && item == hilited && item.active) {
			hilited = null;
			item.action();
		} else {
			hilited = null;
			APP.appScreen.contentPane.repaint();
		}
	}
	
	public void dragToNewLocation(Point newLoc) {
		
		hilited = null;
		
		setLocation(newLoc);
		
		APP.appScreen.contentPane.repaint();
	}
	
	public void moved(InputEvent ev) {
		
	}
	
	public void clicked(InputEvent ev) {
		
	}
	
	public MenuItem hitTest(Point p) {
		
		for (int i = 0; i < cols; i++) {
			AABB a = colAABBs.get(i);
			if (a.hitTest(p)) {
				List<MenuItem> col = tree.get(i);
				for (int j = 0; j < col.size(); j++) {
					MenuItem item = col.get(j);
					if (item.hitTest(p)) {
						return item;
					}
				}
				break;
			}
		}
		
		return null;
	}
	
	public abstract void escape();
	
	public void postDisplay(int width, int height) {
		
		parWidth = width;
		parHeight = height;
		
	}
	
	public void render() {
		
		for (int i = 0; i < cols; i++) {
			List<MenuItem> col = tree.get(i);
			double totalMenuItemHeight = 0.0;
			for (int j = 0; j < col.size(); j++) {
				MenuItem item = col.get(j);
				if ((int)item.localAABB.width > columnWidth[i]) {
					columnWidth[i] = (int)item.localAABB.width;
				}
				totalMenuItemHeight += (int)item.localAABB.height;
			}
			if (columnWidth[i] < MenuItem.minimumWidth.localAABB.width) {
				columnWidth[i] = (int)MenuItem.minimumWidth.localAABB.width;
			}
			
			columnHeight[i] = totalMenuItemHeight + 10 * (col.size() - 1);
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
			
			List<MenuItem> col = tree.get(i);
			
			for (int j = 0; j < col.size(); j++) {
				MenuItem item = col.get(j);
				item.aabb = new AABB(x, y, item.aabb.width, item.aabb.height);
				item.render();
				y = y + (int)item.aabb.height + 10;
			}
			
			colAABBs.add(i, AABB.union(col.get(0).aabb, col.get(col.size()-1).aabb));
		}
		
		setAABBAndScrolling();
		
		shimmer = new Shimmer(System.currentTimeMillis());
		shimmer.setShape(shimmeringMenuItem.aabb);
		
		rendered = true;
	}
	
	private void setAABBAndScrolling() {
		
		double x = 0;
		double y = 0;
		
		double s = (widthFraction * parWidth) / menuWidth;
		
		scale = s;
		aabb = new AABB(-1, -1, scale * menuWidth, scale * menuHeight);
		
		if (DMath.lessThanEquals(aabb.width, parWidth)) {
			/*
			 * no scrolling
			 */
			
			hScrollable = false;
			
			x = parWidth/2 - aabb.width/2;
			
		} else {
			/*
			 * will be scrolling
			 */
			
			hScrollable = true;
			
		}
		
		if (DMath.lessThanEquals(aabb.height, parHeight)) {
			/*
			 * no scrolling
			 */
			
			vScrollable = false;
			
			y = parHeight/2 - aabb.height/2;
			
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
		
		/*
		 * for MenuItem borders
		 */
//		ctxt.setColor(Color.BLUE);
//		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		for (int i = 0; i < tree.size(); i++) {
			List<MenuItem> col = tree.get(i);
			for (int j = 0; j < col.size(); j++) {
				MenuItem item = col.get(j);
				item.paint(ctxt);
			}
		}
		
		if (hilited != null) {
			hilited.paintHilited(ctxt);			
		}
		
		shimmer.paint(ctxt);
		
	}
}
