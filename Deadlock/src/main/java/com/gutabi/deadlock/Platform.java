package com.gutabi.deadlock;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public interface Platform {

	public abstract RenderingContext createRenderingContext(Object... args);
	
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
	
	public abstract PlatformContentPane createPlatformContentPane(Object... args);
	
	public abstract void setupAppScreen(Object... args);
	
	public abstract void setupDebuggerScreen(Object... args);
	
	public abstract void showAppScreen(Object... args);
	
	public abstract void showDebuggerScreen(Object... args);
	
	public abstract void unshowAppScreen(Object... args);
	
	public abstract void unshowDebuggerScreen(Object... args);
	
	public abstract ShapeEngine createShapeEngine(Object... args);
	
	/*
	 * resource engine
	 */
	public abstract Resource imageResource(String name);
	
	public abstract Resource fontResource(String name);
	
//	public abstract File levelDBFile(String name);
	
	
	/*
	 * board engine
	 */
	
	
	
	
	public abstract void exit();
	
}
