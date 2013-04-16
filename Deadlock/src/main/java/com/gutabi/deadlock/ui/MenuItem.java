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
	
	private Label lab;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	public AABB localAABB;
	public AABB aabb;
	
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
	
	
	
	Transform origTransformRender = APP.platform.createTransform();
	
	public void render(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransformRender);
		
		int x = (int)origTransformRender.getTranslateX();
		int y = (int)origTransformRender.getTranslateY();
		
		lab.setLocation(0, 20);
		lab.setDimension(menu.menuItemWidest[c], lab.localAABB.height);
		
		if (active) {
			lab.color = Color.WHITE;
		} else {
			lab.color = Color.GRAY;
		}
		
		lab.render();
		aabb = new AABB(x, y, lab.aabb.width, 20 + lab.aabb.height + 20);
	}
	
	
	
	Transform origTransformPaint = APP.platform.createTransform();
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransformPaint);
		
		ctxt.translate(aabb.x, aabb.y);
		
		lab.paint(ctxt);
		
		ctxt.setTransform(origTransformPaint);
		
		ctxt.setColor(Color.BLUE);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		aabb.draw(ctxt);
	}
	
	
	
	Transform origTransformPaintHilited = APP.platform.createTransform();
	
	public void paintHilited(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransformPaintHilited);
		
		ctxt.setColor(Color.RED);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		aabb.draw(ctxt);
		
		ctxt.setTransform(origTransformPaintHilited);
	}
	
}
