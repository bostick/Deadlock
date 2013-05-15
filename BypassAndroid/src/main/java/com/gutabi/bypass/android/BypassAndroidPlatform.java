package com.gutabi.bypass.android;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.Log;

import com.gutabi.bypass.BypassPlatform;
import com.gutabi.bypass.android.geom.GeometryPathImpl;
import com.gutabi.bypass.android.ui.ImageImpl;
import com.gutabi.bypass.android.ui.PlatformContentPaneImpl;
import com.gutabi.bypass.android.ui.paint.RenderingContextImpl;
import com.gutabi.bypass.level.Level;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.GeometryPath;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.PlatformContentPane;
import com.gutabi.capsloc.ui.paint.FontStyle;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class BypassAndroidPlatform implements BypassPlatform {
	
	public static BypassActivity CURRENTACTIVITY;
	
	Resources resources;
	
	ResourceImpl visitorFontResource;
	Paint visitorPlain36 = new Paint();
	Paint visitorPlain16 = new Paint();
	Paint visitorPlain48 = new Paint();
	Paint visitorPlain72 = new Paint();
	
	protected BypassAndroidPlatform(Resources resources) {
		this.resources = resources;
		
		visitorFontResource = new ResourceImpl(Typeface.createFromAsset(resources.getAssets(), "fonts/" + "visitor1" + ".ttf"));
		
		visitorPlain36.setTypeface(visitorFontResource.face);
		visitorPlain36.setTextSize(36);
		visitorPlain16.setTypeface(visitorFontResource.face);
		visitorPlain16.setTextSize(16);
		visitorPlain48.setTypeface(visitorFontResource.face);
		visitorPlain48.setTextSize(48);
		visitorPlain72.setTypeface(visitorFontResource.face);
		visitorPlain72.setTextSize(72);
		
	}
	
	public RenderingContext createRenderingContext() {
		return new RenderingContextImpl();
	}
	
	
	
	public void setRenderingContextFields1(RenderingContext a, Object arg0) {
		
		RenderingContextImpl ctxt = (RenderingContextImpl)a;
		
		if (arg0 == null) {
			
			throw new AssertionError();
			
		} else {
			
			ImageImpl img = (ImageImpl)arg0;
			Bitmap b = img.b;
			Canvas c = new Canvas(b);
			
			ctxt.canvas = c;
			ctxt.paint = imgPaint;
			
		}
		
	}
	
	Paint imgPaint = new Paint();
	
	public void setRenderingContextFields2(RenderingContext a, Object arg0, Object arg1) {
		
		RenderingContextImpl ctxt = (RenderingContextImpl)a;
		
		if (arg0 == null) {
			
			throw new AssertionError();
			
		} else if (arg0 instanceof Canvas) {
			
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
	
	private Paint getFontPaint(Resource font, FontStyle fontStyle, int fontSize) {
		
		if (font == visitorFontResource) {
			
			if (fontStyle == FontStyle.PLAIN) {
				
				if (fontSize == 16) {
					return visitorPlain16;	
				} else if (fontSize == 48) {
					return visitorPlain48;	
				} else if (fontSize == 72) {
					return visitorPlain72;	
				} else if (fontSize == 36) {
					return visitorPlain36;	
				}
				
			}
			
		}
		
		assert false;
		return null;
	}
	
	
	Map<Paint, Map<String, AABB>> fontBoundsCache = new HashMap<Paint, Map<String, AABB>>();
	
	/*
	 * font engine
	 */
	public AABB bounds(String preText, Resource font, FontStyle fontStyle, int fontSize) {
		
		/*
		 * layout returns height of 0 for " "
		 */
		String text = preText.replace(' ', 'X');
		
		assert fontStyle == FontStyle.PLAIN;
		Paint textPaint = getFontPaint(font, fontStyle, fontSize);
		
		Map<String, AABB> boundsCache = fontBoundsCache.get(textPaint);
		if (boundsCache == null) {
			boundsCache = new HashMap<String, AABB>();
			fontBoundsCache.put(textPaint, boundsCache);
		}
		
		AABB aabb = boundsCache.get(preText);
		if (aabb == null) {
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			assert bounds.width() > 0;
			assert bounds.height() > 0;
			
			aabb = new AABB(bounds.left, bounds.top, bounds.width(), bounds.height());
		}
		
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
		
		ResourceImpl impl = (ResourceImpl)res;
		
		assert impl.resType == ResourceType.DRAWABLE;
		
		Integer id = impl.resId;
		
		Bitmap b = BitmapFactory.decodeResource(resources, id);
		
		return new ImageImpl(b);
	}
	
	
	
	
	public PlatformContentPane createPlatformContentPane() {
		return new PlatformContentPaneImpl(CURRENTACTIVITY.v);
	}

	public void setupAppScreen(Object... args) {
		
		PlatformContentPaneImpl pcp = (PlatformContentPaneImpl)args[0];
		
		BypassView v = CURRENTACTIVITY.v;
		
		v.paintable = pcp;
		
	}
	
//	public void setupDebuggerScreen(Object... args) {
//		
//	}
	
	public void showAppScreen() {
		
	}
	
//	public void showDebuggerScreen() {
//		
//	}
	
	public void unshowAppScreen() {
		
	}

//	public void unshowDebuggerScreen() {
//		
//	}



	

	public void exit() {
		
	}
	
	public Resource fontResource(String name) {
		
		if (name.equals("visitor1")) {
			return visitorFontResource;
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
	
	public void finishAction() {
		CURRENTACTIVITY.finish();
	}
	
	public void loadScores(LevelDB levelDB) throws Exception {
		
		SharedPreferences grades = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-grades", 0);
		SharedPreferences userMoves = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-userMoves", 0);
		
		levelDB.percentage = 0.0;
		for (int i = 0; i < levelDB.levelCount; i++) {
			String grade = grades.getString(Integer.toString(i), null);
			int moves = userMoves.getInt(Integer.toString(i), -1);
			if (grade != null) {
				Level l = levelDB.getLevel(i);
				l.isWon = true;
				l.grade = grade;
				l.userMoves = moves;
			}
		}
		
		levelDB.computePercentageComplete();
		
	}
	
	public void saveScore(LevelDB levelDB, Level l) {
		
		SharedPreferences grades = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-grades", 0);
		SharedPreferences.Editor editor = grades.edit();
		editor.putString(Integer.toString(l.index), l.grade);
		editor.commit();
		
		SharedPreferences userMoves = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-userMoves", 0);
		editor = userMoves.edit();
		editor.putInt(Integer.toString(l.index), l.userMoves);
		editor.commit();
		
		SharedPreferences userTime = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-userTime", 0);
		editor = userTime.edit();
		editor.putLong(Integer.toString(l.index), l.userTime);
		editor.commit();
	}
	
	public void clearScores(LevelDB levelDB) {
		
		LevelMenu menu = (LevelMenu)APP.model;
		
		menu.lock.lock();
		try {
			
			SharedPreferences grades = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-grades", 0);
			SharedPreferences.Editor editor = grades.edit();
			editor.clear();
			editor.commit();
			
			SharedPreferences userMoves = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-userMoves", 0);
			editor = userMoves.edit();
			editor.clear();
			editor.commit();
			
			SharedPreferences userTime = CURRENTACTIVITY.getSharedPreferences(levelDB.resourceName+"-userTime", 0);
			editor = userTime.edit();
			editor.clear();
			editor.commit();
			
			levelDB.clearLevels();
			
			menu.shimmeringMenuItem = menu.tree.get(0).get(0);
			
			menu.render();
			
		} finally {
			menu.lock.unlock();
		}
	}
	
	public long monotonicClockMillis() {
		return SystemClock.uptimeMillis();
	}
	
	public GeometryPath createGeometryPath() {
		return new GeometryPathImpl();
	}
	
}
