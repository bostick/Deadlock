package com.gutabi.deadlock;

import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.geom.ShapeEngineImpl;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.ContentPaneImpl;
import com.gutabi.deadlock.ui.ImageEngine;
import com.gutabi.deadlock.ui.ImageEngineImpl;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.paint.FontEngine;
import com.gutabi.deadlock.ui.paint.FontEngineImpl;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PlatformImpl extends Platform {
	
	public RootPaneContainer container;
	
	public RenderingContext createRenderingContext(Object... args) {
		
		if (args[0] instanceof Graphics2D) {
			
			Graphics2D g2 = (Graphics2D)args[0];
			
			return new RenderingContextImpl(g2);
		} else {
			
			ImageImpl img = (ImageImpl)args[0];
			Graphics2D g2 = img.img.createGraphics();
			
			return new RenderingContextImpl(g2); 
		}
		
	}
	
	public ContentPane createContentPane(Object... args) {
		
		ContentPane cp = (ContentPane)args[0];
		
		return new ContentPaneImpl(cp);
	}
	
	public void setupScreen(Object... args) {
		
//		RootPaneContainer container = (RootPaneContainer)args[0];
		ContentPaneImpl content = (ContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		container.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (container instanceof JFrame) {
			((JFrame)container).setVisible(true);
		}
	}

	public FontEngine createFontEngine(Object... args) {
		return new FontEngineImpl();
	}
	
	public ImageEngine createImageEngine(Object... args) {
		return new ImageEngineImpl();
	}
	
	public ShapeEngine createShapeEngine(Object... args) {
		return new ShapeEngineImpl();
	}
	
}
