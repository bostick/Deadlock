package com.gutabi.capsloc.ui.paint;

import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.GeometryPath;
import com.gutabi.capsloc.geom.Line;
import com.gutabi.capsloc.geom.MutableAABB;
import com.gutabi.capsloc.math.Dim;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.world.WorldCamera;

public abstract class RenderingContext {
	
	public WorldCamera cam;
	
	public RenderingContext() {
		
	}
	
	public abstract void setAlpha(double a);
	
	public abstract void setStroke(double width, Cap cap, Join join);
	
	public abstract Color getColor();
	
	public abstract void setColor(Color c);
	
	public abstract void setXORMode(Color c);
	
	public abstract void clearXORMode();
	
	public abstract void setFont(Resource fontFile, FontStyle style, int size);
	
	public abstract void scale(double s);
	
	public abstract void translate(double tx, double ty);
	
	public abstract void translate(Point p);
	
	public abstract void clip(AABB a);
	
	public abstract void pushTransform();
	
	public abstract void popTransform();
	
	public abstract void pushClip();
	
	public abstract void popClip();
	
	
	public abstract void rotate(double a);
	
	public abstract void rotate(double a, Point p);
	
	public abstract void rotate(double a, Dim d);
	
	public abstract void paintString(double x, double y, double s, String str);
	
	public abstract void paintImage(Image img, double orig, double dx1, double dy1, double dx2, double dy2, int sx1, int sy1, int sx2, int sy2);
	
	public abstract void paintImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2);
	
	public abstract void dispose();
	
//	public abstract void fillRect(int x, int y, int width, int height);
//	
	public abstract void drawAABB(AABB a);
	
	public abstract void paintAABB(AABB a);
	
	public abstract void drawAABB(MutableAABB a);
	
	public abstract void paintAABB(MutableAABB a);
	
	public abstract void paintPath(GeometryPath p);
	
	public abstract void drawPath(GeometryPath p);
	
	public abstract void drawLine(Line a);
	
	public abstract void drawCircle(Circle c);
	
	public abstract void paintCircle(Circle c);
	
}
