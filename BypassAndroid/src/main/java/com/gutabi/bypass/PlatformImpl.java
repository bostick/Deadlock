package com.gutabi.bypass;

import java.io.InputStream;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.gutabi.bypass.R;
import com.gutabi.bypass.ResourceImpl.ResourceType;
import com.gutabi.bypass.geom.CircleImpl;
import com.gutabi.bypass.geom.CubicCurveImpl;
import com.gutabi.bypass.geom.EllipseImpl;
import com.gutabi.bypass.geom.LineImpl;
import com.gutabi.bypass.geom.OBBImpl;
import com.gutabi.bypass.geom.PolygonImpl;
import com.gutabi.bypass.geom.PolylineImpl;
import com.gutabi.bypass.geom.QuadCurveImpl;
import com.gutabi.bypass.geom.TriangleImpl;
import com.gutabi.bypass.ui.ImageImpl;
import com.gutabi.bypass.ui.PlatformContentPaneImpl;
import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.Platform;
import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.Ellipse;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Polygon;
import com.gutabi.deadlock.geom.Polyline;
import com.gutabi.deadlock.geom.QuadCurve;
import com.gutabi.deadlock.geom.Triangle;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

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
	
	
	
	
	public PlatformContentPane createPlatformContentPane() {
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
	
	
	
	
	public Circle createCircle(Point center, double radius) {
		return new CircleImpl(center, radius);
	}
	
	public Line createLine(Point p0, Point p1) {
		return new LineImpl(p0, p1);
	}

	public Polyline createPolyline(Point... pts) {
		return new PolylineImpl(pts);
	}

	public OBB createOBB(Point center, double preA, double xExtant, double yExtant) {
		return new OBBImpl(center, preA, xExtant, yExtant);
	}

	public Ellipse createEllipse(Point center, double x, double y) {
		return new EllipseImpl(center, x, y);
	}

	public Triangle createTriangle(Point p0, Point p1, Point p2) {
		return new TriangleImpl(p0, p1, p2);
	}

	public QuadCurve createQuadCurve(Point start, Point c0, Point end) {
		return new QuadCurveImpl(start, c0, end);
	}

	public CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end) {
		return new CubicCurveImpl(start, c0, c1, end);
	}
	
	public Polygon createPolygon(Point... pts) {
		return new PolygonImpl(pts);
	}
	
}
