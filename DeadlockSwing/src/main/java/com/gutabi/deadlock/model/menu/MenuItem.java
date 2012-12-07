package com.gutabi.deadlock.model.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class MenuItem {
	
	public final String text;
//	public Point p;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	private TextLayout layout;
	
	public AABB localAABB;
	public Point baseline;
	public AABB aabb;
	
	public Point ul;
	
	static private Font f = new Font("Times", Font.PLAIN, 96);
	
	public MenuItem(String text) {
		this.text = text;
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
	public void renderLocal(RenderingContext ctxt) {
		FontRenderContext frc = ctxt.getFontRenderContext();
		layout = new TextLayout(text, f, frc);
		Rectangle2D bounds = layout.getBounds();
		localAABB = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	public void render(RenderingContext ctxt) {
		
		AffineTransform trans = ctxt.getTransform();
		
		double x = trans.getTranslateX();
		double y = trans.getTranslateY();
		
		ul = new Point(x, y);
		
		aabb = new AABB(x, y, localAABB.width, localAABB.height);
		
		baseline = new Point(x - localAABB.x, y - localAABB.y);
		
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.setColor(Color.WHITE);
		ctxt.draw(layout, baseline.x, baseline.y);
	}
	
	public void paintHilited(RenderingContext ctxt) {
		ctxt.setColor(Color.RED);
		ctxt.setPixelStroke(1);
		aabb.draw(ctxt);
	}
	
}
