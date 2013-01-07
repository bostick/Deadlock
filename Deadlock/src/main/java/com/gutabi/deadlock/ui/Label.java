package com.gutabi.deadlock.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.math.geom.AABB;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Label {
	
	public String text;
	
	public Font font;
	public Color color = Color.BLACK;
	
	TextLayout layout;
	Point ul;
	public AABB localAABB;
	public AABB aabb;
	
	BufferedImage img;
	
	public Label(String text) {
		this.text = text;
	}
	
	public void setLocation(double x, double y) {
		ul = new Point(x, y);
	}
	
	public void setDimension(double w, double h) {
		localAABB = new AABB(localAABB.x, localAABB.y, w, h);
	}
	
	public int getWidth() {
		return img.getWidth();
	}
	
	public int getHeight() {
		return img.getHeight();
	}
	
	public void renderLocal() {
//		FontRenderContext frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		FontRenderContext frc = new FontRenderContext(null, false, false);
		layout = new TextLayout(text, font, frc);
		Rectangle2D bounds = layout.getBounds();
		localAABB = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	public void render() {
		
		aabb = new AABB(ul.x, ul.y, localAABB.width, localAABB.height);
		
		Point baseline = new Point(-localAABB.x, -localAABB.y);
		
		img = new BufferedImage((int)aabb.width, (int)aabb.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(color);
		layout.draw(g2, (float)baseline.x, (float)baseline.y);
		g2.dispose();
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintImage(img, (int)aabb.x, (int)aabb.y, (int)(aabb.x + aabb.width), (int)(aabb.y + aabb.height), 0, 0, img.getWidth(), img.getHeight());
	}
	
}
