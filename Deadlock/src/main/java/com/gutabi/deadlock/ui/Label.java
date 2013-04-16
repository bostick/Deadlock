package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Label {
	
	public String text;
	
	public Resource fontFile;
	public FontStyle fontStyle;
	public int fontSize;
	
	public Color color = Color.BLACK;
	
	public AABB localAABB;
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	Image img;
	
	public Label(String text) {
		this.text = text;
	}
	
	public Label(String text, double x, double y) {
		this.text = text;
		this.aabb = new AABB(x, y, 0, 0);
	}
	
	public Label(String text, Point ul) {
		this.text = text;
		this.aabb = new AABB(ul.x, ul.y, 0, 0);
	}
	
	public void setLocation(double x, double y) {
		this.aabb = new AABB(x, y, aabb.width, aabb.height);
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
		localAABB = APP.platform.bounds(text, fontFile, fontStyle, fontSize);
	}
	
	
	RenderingContext ctxt = APP.platform.createRenderingContext();
	
	public void render() {
		
		aabb = new AABB(aabb.x, aabb.y, localAABB.width, localAABB.height);
		
		Point baseline = new Point(-localAABB.x, -localAABB.y);
		
		img = APP.platform.createTransparentImage((int)aabb.width, (int)aabb.height);
		
		APP.platform.setRenderingContextFields1(ctxt, img);
		
		ctxt.setColor(color);
		
		ctxt.setFont(fontFile, fontStyle, fontSize);
		ctxt.paintString(baseline.x, baseline.y, 1.0, text);
		
		ctxt.dispose();
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintImage(img,
				(int)aabb.x, (int)aabb.y, (int)(aabb.x + aabb.width), (int)(aabb.y + aabb.height),
				0, 0, img.getWidth(), img.getHeight());
	}
	
}
