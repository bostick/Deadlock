package com.gutabi.deadlock;

import java.io.InputStream;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.Ellipse;
import com.gutabi.deadlock.geom.MutablePolygon;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Polyline;
import com.gutabi.deadlock.geom.QuadCurve;
import com.gutabi.deadlock.geom.Triangle;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public interface Platform {

	public abstract RenderingContext createRenderingContext();
	
	public abstract void setRenderingContextFields1(RenderingContext ctxt, Object arg0);
	
	public abstract void setRenderingContextFields2(RenderingContext ctxt, Object arg0, Object arg1);
	
	
	/*
	 * font engine
	 */
	AABB bounds(String text, Resource fontFile, FontStyle fontStyle, int fontSize);
	
	/*
	 * image engine
	 */
	public Image readImage(Resource res) throws Exception;
	
	public Image createImage(int w, int h);
	
	public Image createTransparentImage(int w, int h);
	
	public abstract PlatformContentPane createPlatformContentPane();
	
	public abstract void setupAppScreen(Object... args);
	
	public abstract void setupDebuggerScreen(Object... args);
	
	public abstract void showAppScreen(Object... args);
	
	public abstract void showDebuggerScreen(Object... args);
	
	public abstract void unshowAppScreen(Object... args);
	
	public abstract void unshowDebuggerScreen(Object... args);
	
	/*
	 * resource engine
	 */
	public abstract Resource imageResource(String name);
	
	public abstract Resource fontResource(String name);
	
	public abstract Resource levelDBResource(String name);
	
	public abstract InputStream openResourceInputStream(Resource res) throws Exception;
	
	
	
	public abstract void exit();
	
	
	
	
	
//	public abstract Circle createCircle(Point center, double radius);
	
	public abstract Polyline createPolyline(Point... pts);
	
	public abstract OBB createOBB(Point center, double a, double xExtant, double yExtant);
	
	public abstract Ellipse createEllipse(Point center, double x, double y);
	
	public abstract Triangle createTriangle(Point p0, Point p1, Point p2);
	
	public abstract QuadCurve createQuadCurve(Point start, Point c0, Point end);
	
	public abstract CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end);
	
//	public abstract Polygon createPolygon4(Point p0, Point p1, Point p2, Point p3);
	
	public abstract MutablePolygon createMutablePolygon();
	
	public abstract Transform createTransform();
	
}
