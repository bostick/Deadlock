package com.gutabi.deadlock.model.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class MenuItem {
	
	public final String text;
	public final Point p;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	public AABB aabb;
	
	public MenuItem(String text, Point p) {
		this.text = text;
		this.p = p;
	}
	
	public void paint(RenderingContext ctxt) {
		
		Font f = ctxt.getFont();
		FontRenderContext frc = ctxt.getFontRenderContext();
		
		TextLayout l = new TextLayout(text, f, frc);
		
		ctxt.draw(l, p.x, p.y);
		
	}
	
	public void paintHilited(RenderingContext ctxt) {
		
		Font f = ctxt.getFont();
		FontRenderContext frc = ctxt.getFontRenderContext();
		
		TextLayout l = new TextLayout(text, f, frc);
		
		Rectangle2D bounds = l.getBounds();
		AABB aabb = new AABB(p.x + bounds.getX(), p.y + bounds.getY(), bounds.getWidth(), bounds.getHeight());
		
		ctxt.setColor(Color.RED);
		ctxt.setPixelStroke(1);
		aabb.draw(ctxt);
		
	}
	
}
