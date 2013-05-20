package com.brentonbostick.capsloc;

import java.io.InputStream;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.GeometryPath;
import com.brentonbostick.capsloc.ui.Image;
import com.brentonbostick.capsloc.ui.PlatformContentPane;
import com.brentonbostick.capsloc.ui.paint.FontStyle;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

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
	
//	public abstract void setupDebuggerScreen(Object... args);
	
	public abstract void showAppScreen();
	
//	public abstract void showDebuggerScreen();
	
	public abstract void unshowAppScreen();
	
//	public abstract void unshowDebuggerScreen();
	
	/*
	 * resource engine
	 */
	public abstract Resource imageResource(String name);
	
	public abstract Resource fontResource(String name);
	
	public abstract String resourceName(Resource res);
	
	public abstract InputStream openResourceInputStream(Resource res) throws Exception;
	
	
	
	public abstract GeometryPath createGeometryPath();
	
	
	public abstract void exit();
	
	
	
	public abstract void action(@SuppressWarnings("rawtypes")Class newClazz, Object... args);
	public abstract void finishAction();
	
	public long monotonicClockMillis();
	
}
