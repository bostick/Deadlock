package com.gutabi.deadlock;

import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Platform {

	public abstract RenderingContext createRenderingContext(Object... args);
	
	public abstract ContentPane createContentPane(Object... args);
	
	public abstract void setupScreen(Object... args);
	
}
