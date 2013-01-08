package com.gutabi.deadlock;

import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.ImageEngine;
import com.gutabi.deadlock.ui.paint.FontEngine;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Platform {

	public abstract RenderingContext createRenderingContext(Object... args);
	
	public abstract FontEngine createFontEngine(Object... args);
	
	public abstract ImageEngine createImageEngine(Object... args);
	
	public abstract ContentPane createContentPane(Object... args);
	
	public abstract void setupScreen(Object... args);
	
	public abstract ShapeEngine createShapeEngine(Object... args);
	
	public abstract void exit();
	
}
