package com.gutabi.bypass.ui.paint;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Stack;

import com.gutabi.bypass.PlatformImpl;
import com.gutabi.bypass.geom.GeometryPathImpl;
import com.gutabi.bypass.ui.ImageImpl;
import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.GeometryPath;
import com.gutabi.capsloc.geom.Line;
import com.gutabi.capsloc.geom.MutableAABB;
import com.gutabi.capsloc.math.Dim;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.FontStyle;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class RenderingContextImpl extends RenderingContext {
	
	public Graphics2D g2;
	
	public RenderingContextImpl() {
		
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
	
	public void clearXORMode() {
		g2.setPaintMode();
	}
	
	public void setFont(Resource file, FontStyle style, int size) {
		
		Font ttfReal = ((PlatformImpl)APP.platform).getRealFont(file, style, size);
		
		g2.setFont(ttfReal);
		
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
	
	public void clip(AABB a) {
		
		Rectangle2D r = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		
		g2.clip(r);
	}
	
	
	
	Stack<AffineTransform> transformStack = new Stack<AffineTransform>();
	Stack<Rectangle> clipStack = new Stack<Rectangle>();
	
	public void pushTransform() {
		
		AffineTransform t = g2.getTransform();
		
		transformStack.push(t);
	}
	
	public void popTransform() {
		
		AffineTransform t = transformStack.pop();
		
		g2.setTransform(t);
	}
	
	public void pushClip() {
		
		Rectangle r = g2.getClipBounds();
		
		clipStack.push(r);
	}
	
	public void popClip() {
		
		Rectangle r = clipStack.pop();
		
		g2.setClip(r);
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
		pushTransform();
		
		g2.translate(x, y);
		g2.scale(s, s);
		g2.drawString(str, 0, 0);
		
		popTransform();
	}
	
	public void paintImage(Image img, double orig, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2) {
		pushTransform();
		
		g2.scale(1 / orig, 1 / orig);
		paintImage(img,
				(int)Math.ceil(dx1 * orig), (int)Math.ceil(dy1 * orig), (int)Math.ceil(dx2 * orig), (int)Math.ceil(dy2 * orig),
				sx1, sy1, sx2, sy2);
		
		popTransform();
	}
	
	public void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		BufferedImage bi = ((ImageImpl)img).img;
		
		g2.drawImage(bi, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
	
	public void drawAABB(AABB a) {
		Rectangle2D rect = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		g2.draw(rect);
	}
	
	public void paintAABB(AABB a) {
		Rectangle2D rect = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		g2.fill(rect);
	}
	
	public void drawAABB(MutableAABB a) {
		Rectangle2D rect = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		g2.draw(rect);
	}
	
	public void paintAABB(MutableAABB a) {
		Rectangle2D rect = new Rectangle2D.Double(a.x, a.y, a.width, a.height);
		g2.fill(rect);
	}
	
	public void drawPath(GeometryPath p) {
		
		GeometryPathImpl pp = (GeometryPathImpl)p;
		
		RenderingHints h = g2.getRenderingHints();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.draw(pp.path);
		
		g2.setRenderingHints(h);
	}
	
	public void paintPath(GeometryPath p) {
		
		GeometryPathImpl pp = (GeometryPathImpl)p;
		
		RenderingHints h = g2.getRenderingHints();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.fill(pp.path);
		
		g2.setRenderingHints(h);
	}
	
	
	public void drawLine(Line a) {
		Line2D l = new Line2D.Double(a.p0.x, a.p0.y, a.p1.x, a.p1.y);
		g2.draw(l);
	}
	
	public void drawCircle(Circle c) {
		Ellipse2D e = new Ellipse2D.Double(c.center.x - c.radius, c.center.y - c.radius, 2 * c.radius, 2 * c.radius);
		
		RenderingHints h = g2.getRenderingHints();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.draw(e);
		
		g2.setRenderingHints(h);
	}
	
	public void paintCircle(Circle c) {
		Ellipse2D e = new Ellipse2D.Double(c.center.x - c.radius, c.center.y - c.radius, 2 * c.radius, 2 * c.radius);
		
		RenderingHints h = g2.getRenderingHints();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.fill(e);
		
		g2.setRenderingHints(h);
	}
	
	public void dispose() {
		g2.dispose();
	}
	
}
