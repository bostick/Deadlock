package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.menu.MainMenu;

@SuppressWarnings("serial")
public class DeadlockApplet extends JApplet {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(DeadlockApplet app) throws Exception {
		
		APP.codebase = app.getCodeBase();
		
		APP.screen = new MainMenu();
		
		APP.screen.init();
		
		VIEW.setupFrame();
		VIEW.init();
		
		APP.screen.render();
		
		((JFrame)VIEW.container).setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
		VIEW.postDisplay();
		
		APP.screen.repaint();
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable t) {
			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public void init() {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		try {
			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						createAndShowGUI(DeadlockApplet.this);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			});
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
}
