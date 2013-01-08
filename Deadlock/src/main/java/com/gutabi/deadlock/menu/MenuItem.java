package com.gutabi.deadlock.menu;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.AffineTransform;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;

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
	
	public MenuItem(MainMenu menu, String text) {
		this.menu = menu;
		lab = new Label(text);
		lab.fontName = "Visitor TT1 BRK";
		lab.fontStyle = FontStyle.PLAIN;
		lab.fontSize = 48;
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
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		aabb.draw(ctxt);
	}
	
}
