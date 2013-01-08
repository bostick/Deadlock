package com.gutabi.deadlock.ui.paint;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.AffineTransform;
import com.gutabi.deadlock.ui.Composite;
import com.gutabi.deadlock.ui.Image;

public abstract class RenderingContext extends DebugDraw {
	
	public RenderingContext() {
		super(new OBBViewportTransform());
		
		m_drawFlags = DebugDraw.e_dynamicTreeBit;
	}
	
	public abstract Composite getComposite();
	
	public abstract void setComposite(Composite c);
	
	public abstract Composite getTransparentComposite();
	
	public abstract void setStroke(double width, Cap cap, Join join);
	
	public abstract Color getColor();
	
	public abstract void setColor(Color c);
	
	public abstract void setXORMode(Color c);
	
	public abstract void setPaintMode();
	
	public abstract void setFont(String name, FontStyle style, int size);
	
	public abstract AffineTransform getTransform();
	
	public abstract void scale(double s);
	
	public abstract void translate(double tx, double ty);
	
	public abstract void translate(Point p);
	
	public abstract void translate(int tx, int ty);
	
	public abstract void setTransform(AffineTransform t);
	
	public abstract void rotate(double a);
	
	public abstract void rotate(double a, double x, double y);
	
	public abstract void paintString(double x, double y, double s, String str);
	
	public abstract void paintImage(Image img, double orig, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2);
	
	public abstract void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2);
	
	public abstract void fillRect(int x, int y, int width, int height);
	
	public abstract void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor);

	public abstract void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color);

	public abstract void drawCircle(Vec2 center, float radius, Color3f color);

	public abstract void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color);
	
	public abstract void drawSegment(Vec2 p1, Vec2 p2, Color3f color);
	
	public abstract void drawTransform(Transform xf);

	public abstract void drawString(float x, float y, String s, Color3f color);
	
	public abstract void dispose();
	
}
