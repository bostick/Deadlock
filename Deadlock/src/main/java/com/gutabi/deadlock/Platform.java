package com.gutabi.deadlock;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Image;
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
	
	public abstract ContentPane createContentPane(Object... args);
	
	public abstract void setupAppScreen(Object... args);
	
	public abstract void setupDebuggerScreen(Object... args);
	
	public abstract void showAppScreen(Object... args);
	
	public abstract void showDebuggerScreen(Object... args);
	
	public abstract void unshowAppScreen(Object... args);
	
	public abstract void unshowDebuggerScreen(Object... args);
	
	public abstract ShapeEngine createShapeEngine(Object... args);
	
	public abstract ResourceEngine createResourceEngine(Object... args);
	
	public abstract void exit();
	
}
