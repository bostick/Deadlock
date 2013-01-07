package com.gutabi.deadlock;

import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Platform {

	public abstract RenderingContext createRenderingContext(Object... args);
	
}
