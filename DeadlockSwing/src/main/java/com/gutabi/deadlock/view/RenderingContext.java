package com.gutabi.deadlock.view;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

//@SuppressWarnings("static-access")
public class RenderingContext {
	
	public final Graphics2D g2;
	public final RenderingContextType type;
	
	public RenderingContext(Graphics2D g2, RenderingContextType type) {
		this.g2 = g2;
		this.type = type;
	}
	
	public void paintString(double x, double y, String str) {
		AffineTransform origTransform = g2.getTransform();
		g2.translate(x, y);
		g2.scale(1 / VIEW.PIXELS_PER_METER_DEBUG, 1 / VIEW.PIXELS_PER_METER_DEBUG);
		g2.drawString(str, 0, 0);
		g2.setTransform(origTransform);
	}
	
	public void paintImage(double x, double y, Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		AffineTransform origTransform = g2.getTransform();
		
//		g2.translate(VIEW.viewport.x, VIEW.viewport.y);
		
		g2.translate(x, y);
//		g2.translate(-VIEW.viewport.x, -VIEW.viewport.y);
		g2.translate(VIEW.viewport.x, VIEW.viewport.y);
		g2.scale(1 / VIEW.PIXELS_PER_METER_DEBUG, 1 / VIEW.PIXELS_PER_METER_DEBUG);
		
//		g2.translate(-VIEW.viewport.x, -VIEW.viewport.y);
		
//		g2.translate(-VIEW.viewport.x * VIEW.PIXELS_PER_METER_DEBUG, -VIEW.viewport.y * VIEW.PIXELS_PER_METER_DEBUG);
//		g2.translate(VIEW.viewport.x * VIEW.PIXELS_PER_METER_DEBUG, VIEW.viewport.y * VIEW.PIXELS_PER_METER_DEBUG);
		g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		g2.setTransform(origTransform);
	}
	
}
