package com.gutabi.deadlock;

import java.io.InputStream;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.gutabi.deadlock.ResourceImpl.ResourceType;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.geom.ShapeEngineImpl;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.PlatformContentPaneImpl;
import com.gutabi.deadlock.ui.paint.FontStyle;
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

	/*
	 * font engine
	 */
	public AABB bounds(String preText, Resource font, FontStyle fontStyle, int fontSize) {
		
		/*
		 * layout returns height of 0 for " "
		 */
		String text;
		if (preText.equals(" ")) {
			text = "X";
		} else {
			text = preText;
		}
		
		assert fontStyle == FontStyle.PLAIN;
		Paint textPaint = new Paint();
		
		textPaint.setTypeface(((ResourceImpl)font).face);
		textPaint.setTextSize(fontSize);
		
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		assert bounds.width() > 0;
		assert bounds.height() > 0;
		
		AABB aabb = new AABB(bounds.left, bounds.top, bounds.width(), bounds.height());
		return aabb;
	}

	public Image createImage(int width, int height) {
		
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return new ImageImpl(b);
	}
	
	public Image createTransparentImage(int width, int height) {
		
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return new ImageImpl(b);
	}
	
	public Image readImage(Resource res) throws Exception {
		
		ResourceImpl impl = ((ResourceImpl)res);
		
		assert impl.resType == ResourceType.DRAWABLE;
		
		Integer id = ((ResourceImpl)res).resId;
		
		Bitmap b = BitmapFactory.decodeResource(resources, id);
		
		return new ImageImpl(b);
	}
	
	
	
	
	public PlatformContentPane createPlatformContentPane(Object... args) {
		return new PlatformContentPaneImpl(container);
	}

	public void setupAppScreen(Object... args) {
		
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		container.setContentPane(content);
	}
	
	public void setupDebuggerScreen(Object... args) {
		
	}
	
	public void showAppScreen(Object... args) {
		
	}
	
	public void showDebuggerScreen(Object... args) {
		
	}
	
	public void unshowAppScreen(Object... args) {
		
	}

	public void unshowDebuggerScreen(Object... args) {
		
	}



	
	public ShapeEngine createShapeEngine(Object... args) {
		return new ShapeEngineImpl();
	}

	public void exit() {
		
	}

	public Resource imageResource(String name) {
		
		if (name.equals("carsheet")) {
			return new ResourceImpl(R.drawable.carsheet, ResourceType.DRAWABLE);
		} else if (name.equals("spritesheet")) {
			return new ResourceImpl(R.drawable.spritesheet, ResourceType.DRAWABLE);
		} else if (name.equals("explosionsheet")) {
			return new ResourceImpl(R.drawable.explosionsheet, ResourceType.DRAWABLE);
		} else if (name.equals("title_background")) {
			return new ResourceImpl(R.drawable.title_background, ResourceType.DRAWABLE);
		} else if (name.equals("title_white")) {
			return new ResourceImpl(R.drawable.title_white, ResourceType.DRAWABLE);
		} else if (name.equals("copyright")) {
			return new ResourceImpl(R.drawable.copyright, ResourceType.DRAWABLE);
		}
		
		assert false;
		return null;
	}

	public Resource fontResource(String name) {
		
		AssetManager am = resources.getAssets();
		
		if (name.equals("visitor1")) {
			return new ResourceImpl(Typeface.createFromAsset(am, "fonts/" + name + ".ttf"));
		}
		
		assert false;
		return null;
	}
	
	public Resource levelDBResource(String name) {
		
		if (name.equals("levels")) {
			return new ResourceImpl(R.raw.levels, ResourceType.RAW);
		}
		
		assert false;
		return null;
	}
	
	public InputStream getResourceInputStream(Resource res) {
		
		ResourceImpl impl = (ResourceImpl)res;
		
		if (impl.resType == ResourceType.RAW) {
			return resources.openRawResource(impl.resId);
		}
		
		return null;
	}
	
}
