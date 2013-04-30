package com.gutabi.capsloc;

import java.io.InputStream;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.CubicCurve;
import com.gutabi.capsloc.geom.Ellipse;
import com.gutabi.capsloc.geom.MutableOBB;
import com.gutabi.capsloc.geom.MutablePolygon;
import com.gutabi.capsloc.geom.OBB;
import com.gutabi.capsloc.geom.Polyline;
import com.gutabi.capsloc.geom.QuadCurve;
import com.gutabi.capsloc.geom.Triangle;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.PlatformContentPane;
import com.gutabi.capsloc.ui.paint.FontStyle;
import com.gutabi.capsloc.ui.paint.RenderingContext;

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
	
	public abstract void showAppScreen();
	
	public abstract void showDebuggerScreen();
	
	public abstract void unshowAppScreen();
	
	public abstract void unshowDebuggerScreen();
	
	/*
	 * resource engine
	 */
	public abstract Resource imageResource(String name);
	
	public abstract Resource fontResource(String name);
	
	public abstract Resource levelDBResource(String name);
	
	public abstract InputStream openResourceInputStream(Resource res) throws Exception;
	
	
	
	public abstract void exit();
	
	
	
	public abstract Polyline createPolyline(Point... pts);
	
	public abstract OBB createOBB(Point center, double a, double xExtant, double yExtant);
	
	public abstract Ellipse createEllipse(Point center, double x, double y);
	
	public abstract Triangle createTriangle(Point p0, Point p1, Point p2);
	
	public abstract QuadCurve createQuadCurve(Point start, Point c0, Point end);
	
	public abstract CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end);
	
	public abstract MutablePolygon createMutablePolygon();
	
	public abstract MutableOBB createMutableOBB();
	
//	public abstract Transform createTransform();
	
	
	
	public abstract void action(@SuppressWarnings("rawtypes")Class newClazz, Object... args);
	public abstract void finishAction();
	
	public long monotonicClockMillis();
	
}
