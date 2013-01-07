package com.gutabi.deadlock.ui.paint;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;

import com.gutabi.deadlock.ui.Composite;

public class RenderingContextImpl extends RenderingContext {
	
	public static Composite aComp = new CompositeImpl(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.5f));
	
	public RenderingContextImpl(Graphics2D g2) {
		super(g2);
	}
	
	public Composite getComposite() {
		Composite c = new CompositeImpl(g2.getComposite());
		return c;
	}
	
	public void setComposite(Composite c) {
		g2.setComposite(((CompositeImpl)c).c);
	}
	
	public Composite getTransparentComposite() {
		return aComp;
	}
	
	public void setStroke(double width, Cap cap, Join join) {
		
		int c = -1;
		switch (cap) {
		case BUTT:
			c = BasicStroke.CAP_BUTT;
			break;
		case ROUND:
			c = BasicStroke.CAP_ROUND;
			break;
		case SQUARE:
			c = BasicStroke.CAP_SQUARE;
			break;
		}
		
		int j = -1;
		switch (join) {
		case BEVEL:
			j = BasicStroke.JOIN_BEVEL;
			break;
		case MITER:
			j = BasicStroke.JOIN_MITER;
			break;
		case ROUND:
			j = BasicStroke.JOIN_ROUND;
			break;
		}
		
		g2.setStroke(new BasicStroke((float)width, c, j));
	}
	
	
	
	public void setColor(Color c) {
		java.awt.Color c2 = new java.awt.Color(c.r, c.g, c.b, c.a);
		g2.setColor(c2);
	}
	
	public Color getColor() {
		java.awt.Color c = g2.getColor();
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	public void setXORMode(Color c) {
		java.awt.Color c2 = new java.awt.Color(c.r, c.g, c.b, c.a);
		g2.setXORMode(c2);
	}
	
	public void setPaintMode() {
		g2.setPaintMode();
	}
	
//	public void draw(TextLayout layout, Point baseline) {
//		layout.draw(g2, (float)baseline.x, (float)baseline.y);
//	}
	
	public void setFont(String name, FontStyle style, int size) {
		
		int s = -1;
		switch (style) {
		case PLAIN:
			s = Font.PLAIN;
			break;
		}
		
		Font f = new Font(name, s, size);
		g2.setFont(f);
	}
	
}
