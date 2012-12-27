package com.gutabi.deadlock.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

//@SuppressWarnings("static-access")
public abstract class MenuItem {
	
	public final MainMenu parent;
	public final String text;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
	private TextLayout layout;
	private BufferedImage img;
	
	public AABB localAABB;
	public Point baseline;
	public AABB aabb;
	
	public Point ul;
	
	public boolean active = true;
	
	static private Font f = new Font("Visitor TT1 BRK", Font.PLAIN, 48);
	
	public MenuItem(MainMenu parent, String text) {
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
		
		baseline = new Point(-localAABB.x, -localAABB.y);
		
		img = new BufferedImage((int)aabb.width, (int)aabb.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(VIEW.menuBackground);
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		if (active) {
			g2.setColor(Color.WHITE);
		} else {
			g2.setColor(Color.GRAY);
		}
		layout.draw(g2, (float)baseline.x, (float)baseline.y);
		g2.dispose();
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintImage(img, aabb.x, aabb.y, aabb.x + aabb.width, aabb.y + aabb.height, 0, 0, img.getWidth(), img.getHeight());
	}
	
	public void paintHilited(RenderingContext ctxt) {
		ctxt.setColor(Color.RED);
		ctxt.setPixelStroke(1);
		aabb.draw(ctxt);
	}
	
}
