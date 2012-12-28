package com.gutabi.deadlock.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class MenuItem {
	
	public final MainMenu menu;
//	public final String text;
	
	Label lab;
	
	public MenuItem up;
	public MenuItem left;
	public MenuItem right;
	public MenuItem down;
	
//	private TextLayout layout;
//	private BufferedImage img;
	
	public AABB localAABB;
//	public Point baseline;
	public AABB aabb;
	
	public Point ul;
	
	public boolean active = true;
	
	static private Font f = new Font("Visitor TT1 BRK", Font.PLAIN, 48);
	
	public MenuItem(MainMenu menu, String text) {
		this.menu = menu;
//		this.text = text;
		lab = new Label(text);
		lab.setFont(f);
		lab.renderLocal();
		localAABB = lab.localAABB;
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
//	public void renderLocal() {
//		FontRenderContext frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
//		layout = new TextLayout(text, f, frc);
//		Rectangle2D bounds = layout.getBounds();
//		localAABB = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
//	}
	
	public void render(RenderingContext ctxt) {
		
		AffineTransform trans = ctxt.getTransform();
		
		int x = (int)trans.getTranslateX();
		int y = (int)trans.getTranslateY();
		
//		ul = new Point(x, y);
		lab.setLocation(x, y);
		lab.setDimension(Math.max(localAABB.width, menu.widest), localAABB.height);
		
		if (active) {
			lab.setColor(Color.WHITE);
		} else {
			lab.setColor(Color.GRAY);
		}
		
		lab.render();
		aabb = lab.aabb;
		
//		aabb = new AABB(x, y, Math.max(localAABB.width, parent.widest), localAABB.height);
		
//		baseline = new Point(-localAABB.x, -localAABB.y);
//		
//		img = new BufferedImage((int)aabb.width, (int)aabb.height, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g2 = img.createGraphics();
//		g2.setColor(APP.menuBackground);
//		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
//		if (active) {
//			g2.setColor(Color.WHITE);
//		} else {
//			g2.setColor(Color.GRAY);
//		}
//		layout.draw(g2, (float)baseline.x, (float)baseline.y);
//		g2.dispose();
	}
	
	public void paint(RenderingContext ctxt) {
//		ctxt.paintImage(img, (int)aabb.x, (int)aabb.y, (int)(aabb.x + aabb.width), (int)(aabb.y + aabb.height), 0, 0, img.getWidth(), img.getHeight());
		lab.paint(ctxt);
	}
	
	public void paintHilited(RenderingContext ctxt) {
		ctxt.setColor(Color.RED);
		ctxt.setPixelStroke(1);
		aabb.draw(ctxt);
	}
	
}
