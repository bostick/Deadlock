package com.gutabi.deadlock;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.geom.ShapeEngineImpl;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageEngineImpl;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.PlatformContentPaneImpl;
import com.gutabi.deadlock.ui.paint.FontEngineImpl;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PlatformImpl implements Platform {

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

	public Image createImage(int width, int height) {
		
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return new ImageImpl(b);
	}

	public PlatformContentPane createPlatformContentPane(Object... args) {
		return new PlatformContentPaneImpl(container);
	}

	public void setupAppScreen(Object... args) {
		
//		RootPaneContainer container = (RootPaneContainer)args[0];
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		container.setContentPane(content);
	}

	public ShapeEngine createShapeEngine(Object... args) {
		return new ShapeEngineImpl();
	}

	public void exit() {
		
	}

	public Resource imageResource(String name) {
		
		if (name.equals("carsheet")) {
			return new ResourceImpl(R.drawable.carsheet);
		} else if (name.equals("spritesheet")) {
			return new ResourceImpl(R.drawable.spritesheet);
		} else if (name.equals("explosionsheet")) {
			return new ResourceImpl(R.drawable.explosionsheet);
		} else if (name.equals("title_background")) {
			return new ResourceImpl(R.drawable.title_background);
		} else if (name.equals("title_white")) {
			return new ResourceImpl(R.drawable.title_white);
		} else if (name.equals("copyright")) {
			return new ResourceImpl(R.drawable.copyright);
		}
		
		return null;
	}

	public Resource fontResource(String name) {
		
		AssetManager am = resources.getAssets();
		
		if (name.equals("visitor1")) {
			return new ResourceImpl(Typeface.createFromAsset(am, "fonts/" + name + ".ttf"));
		}
		
		return null;
	}
	
	public Resource boardResource(String name) {
		
	}
	
}
