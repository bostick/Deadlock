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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.geom.ShapeEngineImpl;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.PlatformContentPaneImpl;
import com.gutabi.deadlock.ui.paint.FontStyle;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

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
	
	public PlatformContentPane createPlatformContentPane(Object... args) {
		
		KeyListener kl = (KeyListener)args[0];
		
		return new PlatformContentPaneImpl(kl);
	}
	
	public void setupAppScreen(Object... args) {
		
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(APP.WINDOW_WIDTH, APP.WINDOW_HEIGHT));
		
		appContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).pack();
		}
		
	}
	
	public void showAppScreen(Object... args) {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(true);
		}
		
	}
	
	public void setupDebuggerScreen(Object... args) {
		
		PlatformContentPaneImpl content = (PlatformContentPaneImpl)args[0];
		
		content.j.setLayout(null);
		
		content.j.setPreferredSize(new Dimension(APP.CONTROLPANEL_WIDTH, APP.CONTROLPANEL_HEIGHT));
		
		debuggerContainer.setContentPane(content.j);
		content.j.setFocusable(true);
		content.j.requestFocusInWindow();
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).pack();
		}
		
	}
	
	public void showDebuggerScreen(Object... args) {
		
		if (debuggerContainer instanceof JFrame) {
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
	Font visitorPlain36;
	Font visitorPlain16;
	Font visitorPlain48;
	{
		
		InputStream is = this.getClass().getResourceAsStream("/fonts/visitor1.ttf");
		try {
			Font visitorReal = Font.createFont(Font.TRUETYPE_FONT, is);
			visitorPlain36 = visitorReal.deriveFont(Font.PLAIN, 36);
			visitorPlain16 = visitorReal.deriveFont(Font.PLAIN, 16);
			visitorPlain48 = visitorReal.deriveFont(Font.PLAIN, 48);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public java.awt.Font getRealFont(Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		ResourceImpl r = (ResourceImpl)fontFile;
		
		if (r.name.equals("/fonts/visitor1.ttf")) {
			
			if (fontStyle == FontStyle.PLAIN) {
				
				if (fontSize == 36) {
					
					return visitorPlain36;
					
				} else if (fontSize == 16) {
					
					return visitorPlain16;
					
				} else if (fontSize == 48) {
					
					return visitorPlain48;
					
				}
				
			}
			
		}
		
		assert false;
		return null;
	}
	
	public AABB bounds(String preText, Resource fontFile, FontStyle fontStyle, int fontSize) {
		
		AABB aabb = null;
		
		/*
		 * layout returns height of 0 for " "
		 */
		String text;
		if (preText.equals(" ")) {
			text = "X";
		} else {
			text = preText;
		}
		
		Font ttfReal = getRealFont(fontFile, fontStyle, fontSize);
		
		TextLayout layout = new TextLayout(text, ttfReal, frc);
		Rectangle2D bounds = layout.getBounds();
		aabb = new AABB(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		
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
	
	/*
	 * resource engine
	 */
	public Resource imageResource(String name) {
		
		String full = "/img/" + name + ".png";
		
		return new ResourceImpl(full);
	}
	
	public Resource fontResource(String name) {
		
		String full = "/fonts/" + name + ".ttf";
		
		return new ResourceImpl(full);
	}
	
	public Resource boardResource(String name) {
		
		String full = "/boards/" + name + ".txt";
		
		return new ResourceImpl(full);
	}
	
	/*
	 * board engine
	 */
	public char[][] readBoard(Resource res) throws Exception {
		
		char[][] board;
		
		URL url = this.getClass().getResource(((ResourceImpl)res).name);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		int rows = 0;
		int cols = 0;
		
		StringBuilder builder = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
			cols = inputLine.length();
			rows++;
		}
		in.close();
		String s = builder.toString();
		
		board = new char[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				board[i][j] = s.charAt(i * cols + j);
			}
		}
		
		return board;
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
