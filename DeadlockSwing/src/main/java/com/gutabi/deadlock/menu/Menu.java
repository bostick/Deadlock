package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;

//@SuppressWarnings("static-access")
public abstract class Menu {
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	double widest;
	
	public BufferedImage canvasMenuImage;
	
	public Menu() {
		
		canvasMenuImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		
	}
	
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
	
	public void enterKey() {
		
		if (hilited != null && hilited.active) {
			hilited.action();
		}
		
	}
	
	public Point canvasToMenu(Point p) {
		return new Point(p.x - (VIEW.canvas.getWidth()/2 - 800/2), p.y - (VIEW.canvas.getHeight()/2 - 600/2));
	}
	
	public Point lastMovedMenuPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		Point p = ev.p;
		
		lastMovedMenuPoint = canvasToMenu(p);
		
		MenuItem hit = hitTest(lastMovedMenuPoint);
		if (hit != null && hit.active) {
			hilited = hit;
		} else {
			hilited = null;
		}
		
		VIEW.repaintCanvas();
		
	}
	
	Point lastClickedMenuPoint;
	
	public void clicked(InputEvent ev) {
		
		lastClickedMenuPoint = canvasToMenu(ev.p);
		
		MenuItem item = hitTest(lastClickedMenuPoint);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
	public abstract void render();
	
	public abstract void repaint();
	
}
