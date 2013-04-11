package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.menu.MainMenuScreen;
import com.gutabi.bypass.ui.WindowInfo;

@SuppressWarnings("serial")
public class Applet extends JApplet {
	
	static void createAndShowGUI(Applet applet) throws Exception {
		
		BypassApplication app = new BypassApplication();
		APP = app;
		
		APP.init();
		
//		APP.codebase = app.getCodeBase();
		
		MainMenuScreen s = new MainMenuScreen();
		
		applet.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		applet.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
		
//		((JFrame)APP.container).setVisible(true);
		
		s.postDisplay();
//		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable t) {
//			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public void init() {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		try {
			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						createAndShowGUI(Applet.this);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			});
			
		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
		}
		
	}
	
}
