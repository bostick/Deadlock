package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.math.geom.AABB;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontEngine;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Label {
	
	public String text;
	
	public String fontName;
	public FontStyle fontStyle;
	public int fontSize;
	
	public Color color = Color.BLACK;
	
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
		FontEngine engine = APP.platform.createFontEngine();
		localAABB = engine.bounds(text, fontName, fontStyle, fontSize);
	}
	
	public void render() {
		
		aabb = new AABB(ul.x, ul.y, localAABB.width, localAABB.height);
		
		Point baseline = new Point(-localAABB.x, -localAABB.y);
		
		img = new BufferedImage((int)aabb.width, (int)aabb.height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2 = img.createGraphics();
		RenderingContext ctxt = APP.platform.createRenderingContext(g2);
		
		ctxt.setColor(color);
		
		ctxt.setFont(fontName, fontStyle, fontSize);
		ctxt.paintString(baseline.x, baseline.y, 1.0, text);
		
		g2.dispose();
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintImage(img, (int)aabb.x, (int)aabb.y, (int)(aabb.x + aabb.width), (int)(aabb.y + aabb.height), 0, 0, img.getWidth(), img.getHeight());
	}
	
}
