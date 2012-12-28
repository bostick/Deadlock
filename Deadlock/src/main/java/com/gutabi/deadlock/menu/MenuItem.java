package com.gutabi.deadlock.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.RenderingContext;

public abstract class MenuItem {
	
	public final MainMenu menu;
	
	Label lab;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	public AABB localAABB;
	public AABB aabb;
	
	public Point ul;
	
	public boolean active = true;
	
	static private Font f = new Font("Visitor TT1 BRK", Font.PLAIN, 48);
	
	public MenuItem(MainMenu menu, String text) {
		this.menu = menu;
		lab = new Label(text);
		lab.font = f;
		lab.renderLocal();
		localAABB = lab.localAABB;
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
	public void render(RenderingContext ctxt) {
		
		AffineTransform trans = ctxt.getTransform();
		
		int x = (int)trans.getTranslateX();
		int y = (int)trans.getTranslateY();
		
		lab.setLocation(x, y);
		lab.setDimension(menu.widest, localAABB.height);
		
		if (active) {
			lab.color = Color.WHITE;
		} else {
			lab.color = Color.GRAY;
		}
		
		lab.render();
		aabb = lab.aabb;
	}
	
	public void paint(RenderingContext ctxt) {
		lab.paint(ctxt);
	}
	
	public void paintHilited(RenderingContext ctxt) {
		ctxt.setColor(Color.RED);
		ctxt.setPixelStroke(1);
		aabb.draw(ctxt);
	}
	
}
