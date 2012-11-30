package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

//@SuppressWarnings("static-access")
public class RenderingContext {
	
	private final Graphics2D g2;
	public final RenderingContextType type;
	
	public RenderingContext(Graphics2D g2, RenderingContextType type) {
		this.g2 = g2;
		this.type = type;
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
	
	public void setPixelStroke() {
		g2.setStroke(new BasicStroke((float)(1.0 / g2.getTransform().getScaleX()), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
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
	
	public void setFont(Font f) {
		g2.setFont(f);
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
		g2.translate(x, y);
		g2.scale(1 / VIEW.PIXELS_PER_METER_DEBUG, 1 / VIEW.PIXELS_PER_METER_DEBUG);
		g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		g2.setTransform(origTransform);
	}
	
	public void draw(Line2D line) {
		g2.draw(line);
	}
	
	public void fillRect(int x, int y, int width, int height) {
		g2.fillRect(x, y, width, height);
	}
	
	public void fill(Ellipse2D e) {
		g2.fill(e);
	}
	
	public void draw(Ellipse2D e) {
		g2.draw(e);
	}
	
	public void fill(GeneralPath e) {
		g2.fill(e);
	}
	
	public void draw(GeneralPath e) {
		g2.draw(e);
	}
	
	public void fill(Rectangle2D e) {
		g2.fill(e);
	}
	
	public void draw(Rectangle2D e) {
		g2.draw(e);
	}
	
}
