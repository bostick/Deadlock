package com.gutabi.deadlock.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

//@SuppressWarnings("static-access")
public class RenderingContext extends DebugDraw {
	
	private final Graphics2D g2;
	public final RenderingContextType type;
	
	public RenderingContext(Graphics2D g2, RenderingContextType type) {
		super(new OBBViewportTransform());
		
		this.g2 = g2;
		this.type = type;
		
		m_drawFlags = DebugDraw.e_dynamicTreeBit;
	}
	
	public FontRenderContext getFontRenderContext() {
		return g2.getFontRenderContext();
	}
	
	public Color getColor() {
		return g2.getColor();
	}
	
	public void setColor(Color c) {
		g2.setColor(c);
	}
	
	public Stroke getStroke() {
		return g2.getStroke();
	}
	
	public void setStroke(Stroke s) {
		g2.setStroke(s);
	}
	
	public void setPixelStroke(double origS, int pix) {
		g2.setStroke(new BasicStroke((float)(pix / origS), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
	}
	
	public AffineTransform getTransform() {
		return g2.getTransform();
	}
	
	public void scale(double s) {
		g2.scale(s, s);
	}
	
	public void translate(double tx, double ty) {
		g2.translate(tx, ty);
	}
	
	public void translate(int tx, int ty) {
		g2.translate(tx, ty);
	}
	
	public void setTransform(AffineTransform t) {
		g2.setTransform(t);
	}
	
	public Composite getComposite() {
		return g2.getComposite();
	}
	
	public void setComposite(Composite c) {
		g2.setComposite(c);
	}
	
	public void rotate(double a) {
		g2.rotate(a);
	}
	
	public void rotate(double a, double x, double y) {
		g2.rotate(a, x, y);
	}
	
	public void paintString(double origS, double x, double y, double s, String str) {
		AffineTransform origTransform = g2.getTransform();
		
		g2.translate(x, y);
		g2.scale(1 / origS, 1 / origS);
		g2.scale(s, s);
		g2.drawString(str, 0, 0);
		
		g2.setTransform(origTransform);
	}
	
	public void paintImage(double origS, Image img, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2) {
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(1 / origS, 1 / origS);
		g2.drawImage(
				img,
				(int)Math.ceil(dx1 * origS), (int)Math.ceil(dy1 * origS), (int)Math.ceil(dx2 * origS), (int)Math.ceil(dy2 * origS),
				sx1, sy1, sx2, sy2, null);
		
		g2.setTransform(origTransform);
	}
	
	public void draw(java.awt.Shape s) {
		g2.draw(s);
	}
	
	public void fill(java.awt.Shape s) {
		g2.fill(s);
	}
	
	public void fillRect(int x, int y, int width, int height) {
		g2.fillRect(x, y, width, height);
	}
	
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
		assert false;
	}

	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		assert false;
	}

	public void drawCircle(Vec2 center, float radius, Color3f color) {
		assert false;
	}

	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
		assert false;
	}
	
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		g2.setColor(Color.WHITE);
		Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
		g2.draw(line);
	}
	
	public void drawTransform(Transform xf) {
		assert false;
	}

	public void drawString(float x, float y, String s, Color3f color) {
		
	}
	
	public void draw(TextLayout layout, double baselineX, double baselineY) {
		layout.draw(g2, (float)baselineX, (float)baselineY);
	}
	
	public void setXORMode(Color c) {
		g2.setXORMode(c);
	}
	
}