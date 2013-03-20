package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.geom.ShapeEngine;
import com.gutabi.deadlock.geom.ShapeEngineImpl;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.ContentPaneImpl;
import com.gutabi.deadlock.ui.ImageEngine;
import com.gutabi.deadlock.ui.ImageEngineImpl;
import com.gutabi.deadlock.ui.ImageImpl;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.ui.paint.FontEngine;
import com.gutabi.deadlock.ui.paint.FontEngineImpl;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PlatformImpl extends Platform {
	
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
			((JFrame)debuggerContainer).setVisible(true);
		}
		
	}
	
	public void teardownAppScreen(Object... args) {
		
		if (appContainer instanceof JFrame) {
			((JFrame)appContainer).setVisible(false);
		}
		
	}

	public void teardownDebuggerScreen(Object... args) {
		
		if (debuggerContainer instanceof JFrame) {
			((JFrame)debuggerContainer).setVisible(false);
		}
		
	}
	
	public FontEngine createFontEngine(Object... args) {
		return new FontEngineImpl();
	}
	
	public ImageEngine createImageEngine(Object... args) {
		return new ImageEngineImpl();
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
