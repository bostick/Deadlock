package com.gutabi.deadlock;

import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.ContentPaneImpl;
import com.gutabi.deadlock.ui.ImageEngine;
import com.gutabi.deadlock.ui.ImageEngineImpl;
import com.gutabi.deadlock.ui.paint.FontEngine;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class PlatformImpl extends Platform {

	MainView container;
	
	public RenderingContext createRenderingContext(Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	public FontEngine createFontEngine(Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageEngine createImageEngine(Object... args) {
		return new ImageEngineImpl();
	}

	public ContentPane createContentPane(Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setupScreen(Object... args) {
		
//		RootPaneContainer container = (RootPaneContainer)args[0];
		ContentPaneImpl content = (ContentPaneImpl)args[0];
		
		container.setContentPane(content);
		
	}

	public ShapeEngine createShapeEngine(Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	public void exit() {
		
	}
	
}
