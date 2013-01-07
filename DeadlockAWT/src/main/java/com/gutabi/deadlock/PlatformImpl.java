package com.gutabi.deadlock;

import java.awt.Graphics2D;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.ContentPaneImpl;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PlatformImpl extends Platform {

	public RenderingContext createRenderingContext(Object... args) {
		
		Graphics2D g2 = (Graphics2D)args[0];
		
		return new RenderingContextImpl(g2);
	}
	
	public ContentPane createContentPane(Object... args) {
		
		ContentPane cp = (ContentPane)args[0];
		
		return new ContentPaneImpl(cp);
	}
	
	public void setupScreen(Object... args) {
		
		RootPaneContainer container = (RootPaneContainer)args[0];
		ContentPaneImpl content = (ContentPaneImpl)args[1];
		
		content.j.setLayout(null);
		
		container.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
	}
	
}
