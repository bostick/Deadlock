package com.gutabi.deadlock.ui.paint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ResourceImpl;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.Transform;

public class RenderingContextImpl extends RenderingContext {
	
	public final Graphics2D g2;
	
	public RenderingContextImpl(Graphics2D g2) {
		this.g2 = g2;
	}
	
	public void setAlpha(double a) {
		
		AlphaComposite comp = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, (float)a);
		g2.setComposite(comp);
		
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
	
	public void setFont(Resource file, FontStyle style, int size) {
		
		ResourceImpl r = (ResourceImpl)file;
		InputStream is = this.getClass().getResourceAsStream(r.name);
		
	    Font ttfBase;
		try {
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
			
			int s = -1;
			switch (style) {
			case PLAIN:
				s = Font.PLAIN;
				break;
			}
			
			Font ttfReal = ttfBase.deriveFont(s, size);
			
			g2.setFont(ttfReal);
			
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Transform getTransform() {
		
		Transform t = new TransformImpl(g2.getTransform());
		
		return t;
	}
	
	public void scale(double s) {
		g2.scale(s, s);
	}
	
	public void translate(double tx, double ty) {
		g2.translate(tx, ty);
	}
	
	public void translate(Point p) {
		g2.translate(p.x, p.y);
	}
	
	public void setTransform(Transform t) {
		
		java.awt.geom.AffineTransform t2 = ((TransformImpl)t).t;
		
		g2.setTransform(t2);
	}
	
	public void rotate(double a) {
		g2.rotate(a);
	}
	
	public void rotate(double a, Point p) {
		g2.rotate(a, p.x, p.y);
	}
	
	public void rotate(double a, Dim d) {
		g2.rotate(a, d.width, d.height);
	}
	
	public void paintString(double x, double y, double s, String str) {
		java.awt.geom.AffineTransform origTransform = g2.getTransform();
		
		g2.translate(x, y);
		g2.scale(s, s);
		g2.drawString(str, 0, 0);
		
		g2.setTransform(origTransform);
	}
	
	public void paintImage(Image img, double orig, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2) {
		java.awt.geom.AffineTransform origTransform = g2.getTransform();
		
		
		
		g2.scale(1 / orig, 1 / orig);
		paintImage(img,
				(int)Math.ceil(dx1 * orig), (int)Math.ceil(dy1 * orig), (int)Math.ceil(dx2 * orig), (int)Math.ceil(dy2 * orig),
				sx1, sy1, sx2, sy2);
		
		g2.setTransform(origTransform);
	}
	
	public void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		BufferedImage bi = ((ImageImpl)img).img;
		
		g2.drawImage(bi, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
	
	public void fillRect(int x, int y, int width, int height) {
		g2.fillRect(x, y, width, height);
	}
	
	public void drawAABB(AABB a) {
		Rectangle2D rect = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		g2.draw(rect);
	}
	
	public void paintAABB(AABB a) {
		Rectangle2D rect = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		g2.fill(rect);
	}
	
	public void drawLine(Line a) {
		Line2D l = new Line2D.Double(a.p0.x, a.p0.y, a.p1.x, a.p1.y);
		g2.draw(l);
	}
	
	public void paintLine(Line a) {
		Line2D l = new Line2D.Double(a.p0.x, a.p0.y, a.p1.x, a.p1.y);
		g2.fill(l);
	}
	
	public void dispose() {
		g2.dispose();
	}
	
}
