package com.gutabi.deadlock.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class MenuItem {
	
	public final Menu parent;
	public final String text;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	private TextLayout layout;
	
	public AABB localAABB;
	public Point baseline;
	public AABB aabb;
	
	public Point ul;
	
	public boolean active = true;
	
	static private Font f = new Font("Visitor TT1 BRK", Font.PLAIN, 48);
	
	public MenuItem(Menu parent, String text) {
		this.parent = parent;
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
		
		aabb = new AABB(x, y, Math.max(localAABB.width, parent.widest), localAABB.height);
		
		baseline = new Point(x - localAABB.x, y - localAABB.y);
		
	}
	
	public void paint(RenderingContext ctxt) {
		if (active) {
			ctxt.setColor(Color.WHITE);
		} else {
			ctxt.setColor(Color.GRAY);
		}
		ctxt.draw(layout, baseline.x, baseline.y);
	}
	
	public void paintHilited(RenderingContext ctxt) {
		ctxt.setColor(Color.RED);
		ctxt.setPixelStroke(1);
		aabb.draw(ctxt);
	}
	
}
