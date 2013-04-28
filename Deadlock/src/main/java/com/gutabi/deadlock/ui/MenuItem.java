package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class MenuItem {
	
	public final Menu menu;
	
	protected Label lab;
	protected Label auxLab;
	
	public boolean border = true;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	public AABB localAABB;
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	public boolean active = true;
	
	public int r;
	public int c;
	
	public MenuItem(Menu menu, String text) {
		this.menu = menu;
		lab = new Label(text);
		lab.fontFile = APP.platform.fontResource("visitor1");
		lab.fontStyle = FontStyle.PLAIN;
		lab.fontSize = 72;
		lab.renderLocal();
		localAABB = new AABB(0, 0, lab.localAABB.width, 20 + lab.localAABB.height + 20);
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
	
	public void render() {
		
		lab.aabb = new AABB(aabb.x + menu.columnWidth[c]/2 - lab.localAABB.width/2, aabb.y + (20 + lab.localAABB.height + 20)/2 - lab.localAABB.height/2, lab.localAABB.width, lab.localAABB.height);
		
		aabb = new AABB(aabb.x, aabb.y, menu.columnWidth[c], 20 + lab.aabb.height + 20);
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.pushTransform();
		
		if (active) {
			lab.color = Color.WHITE;
		} else {
			lab.color = Color.GRAY;
		}
		
		lab.paint(ctxt);
		
		ctxt.popTransform();
		
		if (auxLab != null) {
			auxLab.paint(ctxt);
		}
		
		if (border) {
			ctxt.setColor(Color.BLUE);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			aabb.draw(ctxt);
		}
	}
	
	public void paintHilited(RenderingContext ctxt) {
		
		ctxt.setColor(Color.RED);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		aabb.draw(ctxt);
		
	}
	
}
