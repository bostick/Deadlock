package com.brentonbostick.capsloc.ui;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.Resource;
import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.FontStyle;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public class Label {
	
	public String text;
	
	public Resource fontFile;
	public FontStyle fontStyle;
	public int fontSize;
	
	public Color color = Color.BLACK;
	
	public AABB localAABB = new AABB();
	public AABB aabb = new AABB();
	
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
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void setDimension(double w, double h) {
		aabb = new AABB(aabb.x, aabb.y, w, h);
	}
	
//	public int getWidth() {
//		return img.getWidth();
//	}
//	
//	public int getHeight() {
//		return img.getHeight();
//	}
	
	public void renderLocal() {
		localAABB = APP.platform.bounds(text, fontFile, fontStyle, fontSize);
	}
	
	
//	static RenderingContext ctxt = APP.platform.createRenderingContext();
//	
//	Image img;
//	
//	public void render() {
//		
//		aabb = new AABB(aabb.x, aabb.y, localAABB.width, localAABB.height);
//		
//		double baselineX = -localAABB.x;
//		double baselineY = -localAABB.y;
//		
//		img = APP.platform.createTransparentImage((int)aabb.width, (int)aabb.height);
//		
//		APP.platform.setRenderingContextFields1(ctxt, img);
//		
//		ctxt.setColor(color);
//		
//		ctxt.setFont(fontFile, fontStyle, fontSize);
//		ctxt.paintString(baselineX, baselineY, 1.0, text);
//		
//		ctxt.dispose();
//	}
//	
//	public void paint(RenderingContext ctxt) {
//		
//		ctxt.paintImage(img, 1.0, aabb.x, aabb.y, aabb.x+aabb.width, aabb.y+aabb.height, 0, 0, img.getWidth(), img.getHeight());
//		
//	}
	
	public void render() {
		aabb = new AABB(aabb.x, aabb.y, localAABB.width, localAABB.height);
	}
	
	public void paint(RenderingContext ctxt) {
		
		double baselineX = -localAABB.x;
		double baselineY = -localAABB.y;
		
		ctxt.setColor(color);
		
		ctxt.setFont(fontFile, fontStyle, fontSize);
		ctxt.paintString(aabb.x+baselineX, aabb.y+baselineY, 1.0, text);
	
	}
	
}
