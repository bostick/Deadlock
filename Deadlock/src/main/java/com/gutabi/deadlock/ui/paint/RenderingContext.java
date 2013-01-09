package com.gutabi.deadlock.ui.paint;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.Transform;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public abstract class RenderingContext extends DebugDraw {
	
	public RenderingContext() {
		super(new OBBViewportTransform());
		
		m_drawFlags = DebugDraw.e_dynamicTreeBit;
	}
	
//	public abstract double getAlpha();
	
	public abstract void setAlpha(double a);
	
//	public abstract Composite getTransparentComposite();
	
	public abstract void setStroke(double width, Cap cap, Join join);
	
	public abstract Color getColor();
	
	public abstract void setColor(Color c);
	
	public abstract void setXORMode(Color c);
	
	public abstract void setPaintMode();
	
	public abstract void setFont(String name, FontStyle style, int size);
	
	public abstract Transform getTransform();
	
	public abstract void scale(double s);
	
	public abstract void translate(double tx, double ty);
	
	public abstract void translate(Point p);
	
//	public abstract void translate(int tx, int ty);
	
	public abstract void setTransform(Transform t);
	
	public abstract void rotate(double a);
	
	public abstract void rotate(double a, Point p);
	
	public abstract void rotate(double a, Dim d);
	
	public abstract void paintString(double x, double y, double s, String str);
	
	public abstract void paintImage(Image img, double orig, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2);
	
	public abstract void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2);
	
	public abstract void fillRect(int x, int y, int width, int height);
	
	public abstract void dispose();
	
	
	
	
	
	
	

	
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
		setColor(Color.WHITE);
		Line line = APP.platform.createShapeEngine().createLine(Point.point(p1), Point.point(p2));
		line.draw(this);
	}
	
	public void drawTransform(org.jbox2d.common.Transform xf) {
		assert false;
	}

	public void drawString(float x, float y, String s, Color3f color) {
		
	}
	
	
	
	
}
