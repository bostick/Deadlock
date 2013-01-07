package com.gutabi.deadlock;

import java.awt.Graphics2D;

import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PlatformImpl extends Platform {

	public RenderingContext createRenderingContext(Object... args) {
		
		Graphics2D g2 = (Graphics2D)args[0];
		
		return new RenderingContextImpl(g2);
	}

}
