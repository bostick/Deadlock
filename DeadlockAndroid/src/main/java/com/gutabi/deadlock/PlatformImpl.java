package com.gutabi.deadlock;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

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

	MainView container;
	
	Resources resources;
	
	public PlatformImpl(Resources resources, MainView container) {
		this.resources = resources;
		this.container = container;
	}
	
	public RenderingContext createRenderingContext(Object... args) {
		
		if (args[0] instanceof Canvas) {
			
			Canvas c = (Canvas)args[0];
			
			return new RenderingContextImpl(c, new Paint());
			
		} else {
			
			ImageImpl img = (ImageImpl)args[0];
			Bitmap b = img.b;
			Canvas c = new Canvas(b);
			
			return new RenderingContextImpl(c, new Paint());
			
		}
		
	}

	public FontEngine createFontEngine(Object... args) {
		return new FontEngineImpl();
	}

	public ImageEngine createImageEngine(Object... args) {
		return new ImageEngineImpl(resources);
	}

	public ContentPane createContentPane(Object... args) {
		return new ContentPaneImpl(container);
	}

	public void setupScreen(Object... args) {
		
//		RootPaneContainer container = (RootPaneContainer)args[0];
		ContentPaneImpl content = (ContentPaneImpl)args[0];
		
		container.setContentPane(content);
	}

	public ShapeEngine createShapeEngine(Object... args) {
		return new ShapeEngineImpl();
	}

	public void exit() {
		
	}

	public ResourceEngine createResourceEngine(Object... args) {
		return new ResourceEngineImpl();
	}
	
}
