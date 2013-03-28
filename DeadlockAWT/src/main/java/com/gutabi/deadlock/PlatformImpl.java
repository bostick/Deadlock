package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;

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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.geom.ShapeEngineImpl;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.ContentPaneImpl;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.world.WorldCamera;

public class PlatformImpl implements Platform {
	
	public RootPaneContainer appContainer;
	public RootPaneContainer debuggerContainer;
	
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
		
//		Object foo = (Object)args[0];
		KeyListener kl = (KeyListener)args[0];
		
		return new ContentPaneImpl(kl);
	}
	
	public void setupAppScreen(Object... args) {
		
		ContentPaneImpl content = (ContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(APP.WINDOW_WIDTH, APP.WINDOW_HEIGHT));
		
		appContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).pack();
//			((JFrame)appContainer).setVisible(true);
		}
		
	}
	
	public void showAppScreen(Object... args) {
		
		if (appContainer instanceof JFrame) {
//			((JFrame)appContainer).pack();
			((JFrame)appContainer).setVisible(true);
		}
		
	}
	
	public void setupDebuggerScreen(Object... args) {
		
		ContentPaneImpl content = (ContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT));
		
		debuggerContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).pack();
//			((JFrame)debuggerContainer).setVisible(true);
		}
		
	}
	
	public void showDebuggerScreen(Object... args) {
		
		if (debuggerContainer instanceof JFrame) {
//			((JFrame)debuggerContainer).pack();
			((JFrame)debuggerContainer).setVisible(true);
		}
		
	}
	
	public void unshowAppScreen(Object... args) {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(false);
		}
		
	}

	public void unshowDebuggerScreen(Object... args) {
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).setVisible(false);
		}
		
	}
	
	/*
	 * font engine
	 */
	
	FontRenderContext frc = new FontRenderContext(null, false, false);
	Font visitorReal;
	{
		
		InputStream is = this.getClass().getResourceAsStream("/fonts/visitor1.ttf");
		try {
			visitorReal = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public AABB bounds(String text, Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		AABB aabb = null;
		
		ResourceImpl r = (ResourceImpl)fontFile;
		
		if (r.name.equals("/fonts/visitor1.ttf")) {
			
			int s = -1;
			switch (fontStyle) {
			case PLAIN:
				s = Font.PLAIN;
				break;
			}
			
			Font ttfReal = visitorReal.deriveFont(s, fontSize);
			
			TextLayout layout = new TextLayout(text, ttfReal, frc);
			Rectangle2D bounds = layout.getBounds();
			aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			
		} else {
			
			InputStream is = this.getClass().getResourceAsStream(r.name);
			try {
				Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
				
				int s = -1;
				switch (fontStyle) {
				case PLAIN:
					s = Font.PLAIN;
					break;
				}
				
				Font ttfReal = ttfBase.deriveFont(s, fontSize);
				
				TextLayout layout = new TextLayout(text, ttfReal, frc);
				Rectangle2D bounds = layout.getBounds();
				aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
				
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return aabb;
	}
	
	/**
	 * image engine
	 */
	public Image readImage(Resource res) throws Exception {
		
		BufferedImage img = ImageIO.read(this.getClass().getResource(((ResourceImpl)res).name));
		
		return new ImageImpl(img);
	}
	
	public Image createImage(int width, int height) {
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		return new ImageImpl(img);
	}
	
	public Image createTransparentImage(int width, int height) {
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		return new ImageImpl(img);
	}
	
	public ShapeEngine createShapeEngine(Object... args) {
		return new ShapeEngineImpl();
	}
	
	public ResourceEngine createResourceEngine(Object... args) {
		return new ResourceEngineImpl();
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
}
