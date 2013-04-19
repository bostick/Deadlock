package com.gutabi.bypass;

import java.io.InputStream;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.animation.Animation;

import com.gutabi.bypass.ResourceImpl.ResourceType;
import com.gutabi.bypass.geom.CubicCurveImpl;
import com.gutabi.bypass.geom.EllipseImpl;
import com.gutabi.bypass.geom.LineImpl;
import com.gutabi.bypass.geom.MutablePolygonImpl;
import com.gutabi.bypass.geom.OBBImpl;
import com.gutabi.bypass.geom.PolygonImpl;
import com.gutabi.bypass.geom.PolylineImpl;
import com.gutabi.bypass.geom.QuadCurveImpl;
import com.gutabi.bypass.geom.TriangleImpl;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.BypassWorldActivity;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.bypass.menu.LevelMenuActivity;
import com.gutabi.bypass.menu.MainMenu;
import com.gutabi.bypass.menu.MainMenuActivity;
import com.gutabi.bypass.ui.ImageImpl;
import com.gutabi.bypass.ui.PlatformContentPaneImpl;
import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.bypass.ui.paint.TransformImpl;
import com.gutabi.deadlock.Platform;
import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.Ellipse;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.geom.MutablePolygon;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Polygon;
import com.gutabi.deadlock.geom.Polyline;
import com.gutabi.deadlock.geom.QuadCurve;
import com.gutabi.deadlock.geom.Triangle;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class PlatformImpl implements Platform {
	
	public static BypassActivity CURRENTACTIVITY;
	
//	MainView container;
	
	Resources resources;
	
	public PlatformImpl(Resources resources) {
		this.resources = resources;
//		this.container = container;
	}
	
	public RenderingContext createRenderingContext() {
		return new RenderingContextImpl();
	}
	
	
	
	public void setRenderingContextFields1(RenderingContext a, Object arg0) {
		
		RenderingContextImpl ctxt = (RenderingContextImpl)a;
		
		ImageImpl img = (ImageImpl)arg0;
		Bitmap b = img.b;
		Canvas c = new Canvas(b);
		
		ctxt.canvas = c;
		ctxt.paint = imgPaint;
		
	}
	
	Paint imgPaint = new Paint();
	
	public void setRenderingContextFields2(RenderingContext a, Object arg0, Object arg1) {
		
		RenderingContextImpl ctxt = (RenderingContextImpl)a;
		
		if (arg0 instanceof Canvas) {
			
			Canvas c = (Canvas)arg0;
			Paint p = (Paint)arg1;
			
			ctxt.canvas = c;
			ctxt.paint = p;
			
		} else {
			
			ImageImpl img = (ImageImpl)arg0;
			Bitmap b = img.b;
			Canvas c = new Canvas(b);
			
			ctxt.canvas = c;
			ctxt.paint = imgPaint;
			
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
		try {
			
			Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			
			return new ImageImpl(b);
			
		} catch (Error e) {
			
			Log.e("image", "error with creating " + width + "x" + height + " image");
			
			throw e;
		} catch (RuntimeException e) {
			
			Log.e("image", "error with creating " + width + "x" + height + " image");
			
			throw e;
		}
	}
	
	public Image createTransparentImage(int width, int height) {
		try {
			
			Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			
			return new ImageImpl(b);
			
		} catch (Error e) {
			
			Log.e("image", "error with creating " + width + "x" + height + " image");
			
			throw e;
		} catch (RuntimeException e) {
			
			Log.e("image", "error with creating " + width + "x" + height + " image");
			
			throw e;
		}
	}
	
	public Image readImage(Resource res) throws Exception {
		
		ResourceImpl impl = ((ResourceImpl)res);
		
		assert impl.resType == ResourceType.DRAWABLE;
		
		Integer id = ((ResourceImpl)res).resId;
		
		Bitmap b = BitmapFactory.decodeResource(resources, id);
		
		return new ImageImpl(b);
	}
	
	
	
	
	public PlatformContentPane createPlatformContentPane() {
		return new PlatformContentPaneImpl(PlatformImpl.CURRENTACTIVITY.v);
	}

	public void setupAppScreen(Object... args) {
		
		PlatformContentPaneImpl pcp = (PlatformContentPaneImpl)args[0];
		
		BypassView v = PlatformImpl.CURRENTACTIVITY.v;
		
		v.paintable = pcp;
		
	}
	
	public void setupDebuggerScreen(Object... args) {
		
	}
	
	public void showAppScreen() {
		
	}
	
	public void showDebuggerScreen() {
		
	}
	
	public void unshowAppScreen() {
		
	}

	public void unshowDebuggerScreen() {
		
	}



	

	public void exit() {
		
	}

	public Resource imageResource(String name) {
		
		if (name.equals("carsheet")) {
			return new ResourceImpl(R.drawable.carsheet, ResourceType.DRAWABLE);
		} else if (name.equals("spritesheet")) {
			return new ResourceImpl(R.drawable.spritesheet, ResourceType.DRAWABLE);
		} else if (name.equals("title_background")) {
			return new ResourceImpl(R.drawable.title_background, ResourceType.DRAWABLE);
		} else if (name.equals("title_white")) {
			return new ResourceImpl(R.drawable.title_white, ResourceType.DRAWABLE);
		} else if (name.equals("copyright")) {
			return new ResourceImpl(R.drawable.copyright, ResourceType.DRAWABLE);
		} else if (name.equals("logo605x132")) {
			return new ResourceImpl(R.drawable.logo605x132, ResourceType.DRAWABLE);
		}
		
		throw new AssertionError();
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
	
	public InputStream openResourceInputStream(Resource res) {
		
		ResourceImpl impl = (ResourceImpl)res;
		
		if (impl.resType == ResourceType.RAW) {
			return resources.openRawResource(impl.resId);
		}
		
		return null;
	}
	
	
	
	
//	public Circle createCircle(Point center, double radius) {
//		return new CircleImpl(center, radius);
//	}
	
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
	
	public Polygon createPolygon4(Point p0, Point p1, Point p2, Point p3) {
		return new PolygonImpl(p0, p1, p2, p3);
	}
	
	public MutablePolygon createMutablePolygon() {
		return new MutablePolygonImpl();
	}
	
	
	public Transform createTransform() {
		return new TransformImpl();
	}
	
	
	
	public void action(@SuppressWarnings("rawtypes") Class clazz, Object... args) {
		
		if (clazz == MainMenu.class) {
			
			Intent intent = new Intent(PlatformImpl.CURRENTACTIVITY, MainMenuActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			PlatformImpl.CURRENTACTIVITY.startActivity(intent);
//			PlatformImpl.CURRENTACTIVITY.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
			PlatformImpl.CURRENTACTIVITY.overridePendingTransition(0, 0);
			
		} else if (clazz == LevelMenu.class) {
			
			Intent intent = new Intent(PlatformImpl.CURRENTACTIVITY, LevelMenuActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			PlatformImpl.CURRENTACTIVITY.startActivity(intent);
//			PlatformImpl.CURRENTACTIVITY.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
			PlatformImpl.CURRENTACTIVITY.overridePendingTransition(0, 0);
			
		} else if (clazz == BypassWorld.class) {
			
			int ii = (Integer)args[0];
			
			Intent intent = new Intent(PlatformImpl.CURRENTACTIVITY, BypassWorldActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.putExtra("com.gutabi.bypass.level.Index", ii);
			PlatformImpl.CURRENTACTIVITY.startActivity(intent);
//			PlatformImpl.CURRENTACTIVITY.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
			PlatformImpl.CURRENTACTIVITY.overridePendingTransition(0, 0);
			
		} else {
			throw new AssertionError();
		}
		
	}
	
	public void finishAction() {
		PlatformImpl.CURRENTACTIVITY.finish();
//		PlatformImpl.CURRENTACTIVITY.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
		PlatformImpl.CURRENTACTIVITY.overridePendingTransition(0, 0);
		
	}
	
}
