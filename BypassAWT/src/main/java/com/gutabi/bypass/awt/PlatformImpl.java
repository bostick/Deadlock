package com.gutabi.bypass.awt;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.bypass.BypassPlatform;
import com.gutabi.bypass.awt.geom.GeometryPathImpl;
import com.gutabi.bypass.awt.ui.ImageImpl;
import com.gutabi.bypass.awt.ui.PlatformContentPaneImpl;
import com.gutabi.bypass.awt.ui.paint.RenderingContextImpl;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.Level;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.bypass.menu.BypassMenu;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.bypass.menu.MainMenu;
import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.GeometryPath;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.PlatformContentPane;
import com.gutabi.capsloc.ui.paint.FontStyle;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class PlatformImpl implements BypassPlatform {
	
	public static final int MAINWINDOW_WIDTH = 800;
	public static final int MAINWINDOW_HEIGHT = 600;
	
	public static final int CONTROLPANEL_WIDTH = 200;
	public static final int CONTROLPANEL_HEIGHT = 600;
	
	
	public RootPaneContainer appContainer;
	public RootPaneContainer debuggerContainer;
	
	public RenderingContext createRenderingContext() {
		
		return new RenderingContextImpl();
	}
	
	public void setRenderingContextFields1(RenderingContext a, Object arg0) {
		
		RenderingContextImpl ctxt = (RenderingContextImpl)a;
		
		if (arg0 == null) {
			
			throw new AssertionError();
			
		} if (arg0 instanceof Graphics2D) {
			
			Graphics2D g2 = (Graphics2D)arg0;
			
			ctxt.g2 = g2;
			
		} else {
			
			ImageImpl img = (ImageImpl)arg0;
			Graphics2D g2 = img.img.createGraphics();
			
			ctxt.g2 = g2; 
		}
		
	}
	
	public void setRenderingContextFields2(RenderingContext a, Object arg0, Object arg1) {
		assert false;
	}
	
	public PlatformContentPane createPlatformContentPane() {
		return new PlatformContentPaneImpl();
	}
	
	public void setupAppScreen(Object... args) {
		
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(PlatformImpl.MAINWINDOW_WIDTH, PlatformImpl.MAINWINDOW_HEIGHT));
		
		appContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).pack();
		}
		
	}
	
	public void showAppScreen() {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(true);
		}
		
	}
	
//	public void setupDebuggerScreen(Object... args) {
//		
//		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
//		
//		content.j.setLayout(null);
//		
//		content.j.setPreferredSize(new Dimension(PlatformImpl.CONTROLPANEL_WIDTH, PlatformImpl.CONTROLPANEL_HEIGHT));
//		
//		debuggerContainer.setContentPane(content.j);
//		content.j.setFocusable(true);
//		content.j.requestFocusInWindow();
//		
//		if (debuggerContainer instanceof JFrame) {
//			((JFrame)debuggerContainer).pack();
//		}
//		
//	}
	
//	public void showDebuggerScreen() {
//		
//		if (debuggerContainer instanceof JFrame) {
//			((JFrame)debuggerContainer).setVisible(true);
//		}
//		
//	}
	
	public void unshowAppScreen() {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(false);
		}
		
	}

//	public void unshowDebuggerScreen() {
//		
//		if (debuggerContainer instanceof JFrame) {
//			((JFrame)debuggerContainer).setVisible(false);
//		}
//		
//	}
	
	/*
	 * font engine
	 */
	
	FontRenderContext frc = new FontRenderContext(null, false, false);
	Font visitorPlain36;
	Font visitorPlain16;
	Font visitorPlain48;
	Font visitorPlain72;
	{
		
		InputStream is = this.getClass().getResourceAsStream("/visitor1.ttf");
		try {
			Font visitorReal = Font.createFont(Font.TRUETYPE_FONT, is);
			visitorPlain36 = visitorReal.deriveFont(Font.PLAIN, 36);
			visitorPlain16 = visitorReal.deriveFont(Font.PLAIN, 16);
			visitorPlain48 = visitorReal.deriveFont(Font.PLAIN, 48);
			visitorPlain72 = visitorReal.deriveFont(Font.PLAIN, 72);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		
	}
	
	public java.awt.Font getRealFont(Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		ResourceImpl r = (ResourceImpl)fontFile;
		
		if (r.full.equals("/visitor1.ttf")) {
			
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
	
	Map<Font, Map<String, AABB>> fontBoundsCache = new HashMap<Font, Map<String, AABB>>();
	
	public AABB bounds(String preText, Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		Font ttfReal = getRealFont(fontFile, fontStyle, fontSize);
		
		Map<String, AABB> boundsCache = fontBoundsCache.get(ttfReal);
		if (boundsCache == null) {
			boundsCache = new HashMap<String, AABB>();
			fontBoundsCache.put(ttfReal, boundsCache);
		}
		
		AABB aabb = boundsCache.get(preText);
		if (aabb == null) {
			/*
			 * layout returns height of 0 for " "
			 */
			String text = preText.replace(' ', 'X');
			
			TextLayout layout = new TextLayout(text, ttfReal, frc);
			Rectangle2D bounds = layout.getBounds();
			aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}
		
		return aabb;
	}
	
	/**
	 * image engine
	 */
	public Image readImage(Resource res) throws Exception {
		
		try {
			BufferedImage img = ImageIO.read(this.getClass().getResource(((ResourceImpl)res).full));
			return new ImageImpl(img);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("IllegalArgumentException trying to read " + ((ResourceImpl)res).full, e);
		}
	}
	
	public Image createImage(int width, int height) {
		assert width > 0;
		assert height > 0;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		return new ImageImpl(img);
	}
	
	public Image createTransparentImage(int width, int height) {
		assert width > 0;
		assert height > 0;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		return new ImageImpl(img);
	}
	
	/*
	 * resource engine
	 */
	public Resource imageResource(String name) {
		
		ResourceImpl res = new ResourceImpl();
		
		res.given = name;
		res.full = "/" + name + ".png";
		
		return res;
	}
	
	public Resource fontResource(String name) {
		
		ResourceImpl res = new ResourceImpl();
		
		res.given = name;
		res.full = "/" + name + ".ttf";
		
		return res;
	}
	
	public Resource levelDBResource(String name) {
		
		String full = "/" + name + ".zip";
		URL url = APP.getClass().getResource(full);
		if (url == null) {
			throw new AssertionError("Resource " + full + " cannot be found");
		}
		
		ResourceImpl res = new ResourceImpl();
		
		res.given = name;
		res.url = url;
		
		return res;
	}
	
	public InputStream openResourceInputStream(Resource res) throws Exception {
		
		ResourceImpl impl = (ResourceImpl)res;
		
		return impl.url.openStream();
	}
	
	public String resourceName(Resource res) {
		
		ResourceImpl impl = (ResourceImpl)res;
		
		if (impl.given.equals("tutorial")) {
			return "tutorial";
		} else if (impl.given.equals("episode1")) {
			return "episode1";
		} else if (impl.given.equals("episode2")) {
			return "episode2";
		}
		
		throw new AssertionError();
	}
	




	public void exit() {
		
		try {
			 String pidstr = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		      String pid[] = pidstr.split("@");
		      Runtime.getRuntime().exec("taskkill /F /PID " + pid[0]).waitFor();
			  }
			  catch (Exception e) {
			  }
		
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public static Class CURRENTACTIVITYCLASS;
	
	public void action(@SuppressWarnings("rawtypes")Class newClazz, Object... args) {
		
		@SuppressWarnings("rawtypes")
		Class oldClazz = CURRENTACTIVITYCLASS;
		if (oldClazz == null) {
			
		} else if (oldClazz == MainMenu.class) {
			
			MainMenu.pause();
			
		} else if (oldClazz == LevelMenu.class) {
			
			LevelMenu.pause();
			
		} else if (oldClazz == BypassWorld.class) {
			
			BypassWorld.pause();
			
		} else {
			throw new AssertionError();
		}
		
		
		if (newClazz == MainMenu.class) {
			
			CURRENTACTIVITYCLASS = MainMenu.class;
			
			BypassMenu.create();
			BypassMenu.start();
			MainMenu.resume();
			MainMenu.surfaceChanged(PlatformImpl.MAINWINDOW_WIDTH, PlatformImpl.MAINWINDOW_HEIGHT);
			
		} else if (newClazz == LevelMenu.class) {
			
			CURRENTACTIVITYCLASS = LevelMenu.class;
			
			BypassMenu.create();
			BypassMenu.start();
			LevelMenu.resume();
			LevelMenu.surfaceChanged(PlatformImpl.MAINWINDOW_WIDTH, PlatformImpl.MAINWINDOW_HEIGHT);
			
		} else if (newClazz == BypassWorld.class) {
			
			CURRENTACTIVITYCLASS = BypassWorld.class;
			
			int ii = (Integer)args[0];
			
			BypassWorld.create(LevelMenu.levelDB, ii);
			BypassWorld.start();
			BypassWorld.resume();
			BypassWorld.surfaceChanged(PlatformImpl.MAINWINDOW_WIDTH, PlatformImpl.MAINWINDOW_HEIGHT);
			
		} else {
			throw new AssertionError();
		}
		
	}
	
	public void finishAction() {
		
		@SuppressWarnings("rawtypes")
		Class oldClazz = CURRENTACTIVITYCLASS;
		if (oldClazz == MainMenu.class) {
			
			MainMenu.pause();
			BypassMenu.stop();
			BypassMenu.destroy();
			
			CURRENTACTIVITYCLASS = null;
			
		} else if (oldClazz == LevelMenu.class) {
			
			LevelMenu.pause();
			BypassMenu.stop();
			BypassMenu.destroy();
			
			CURRENTACTIVITYCLASS = MainMenu.class;
			
			MainMenu.resume();
			MainMenu.surfaceChanged(PlatformImpl.MAINWINDOW_WIDTH, PlatformImpl.MAINWINDOW_HEIGHT);
			
		} else if (oldClazz == BypassWorld.class) {
			
			BypassWorld.pause();
			BypassWorld.stop();
			BypassWorld.destroy();
			
			CURRENTACTIVITYCLASS = LevelMenu.class;
			
			LevelMenu.resume();
			LevelMenu.surfaceChanged(PlatformImpl.MAINWINDOW_WIDTH, PlatformImpl.MAINWINDOW_HEIGHT);
			
		} else {
			throw new AssertionError();
		} 
		
	}
	
	public void loadScores(LevelDB levelDB) throws Exception {
		
	}
	
	public void saveScore(LevelDB levelDB, Level l) {
		
	}
	
	public void clearScores(LevelDB levelDB) {
		
	}
	
	public long monotonicClockMillis() {
		return System.currentTimeMillis();
	}

	public GeometryPath createGeometryPath() {
		return new GeometryPathImpl();
	}
}
