package com.gutabi.deadlock.view;

import java.awt.Graphics2D;

public class RenderingContext {
	
	public final Graphics2D g2;
	public final RenderingContextType type;
	
	public RenderingContext(Graphics2D g2, RenderingContextType type) {
		this.g2 = g2;
		this.type = type;
	}
	
}
