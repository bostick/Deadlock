package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.menu.MainMenu;

public class DeadlockMain  {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI() throws Exception {
		
		APP.codebase = new URL("file:.");
		
		MainMenu s = new MainMenu();
		APP.screen = s;
		
		VIEW.setupFrame();
		VIEW.init();
		
		((JFrame)VIEW.container).setVisible(true);
		
		s.postDisplay();
		s.render();
//		APP.screen.repaint();
//		VIEW.canvas.repaint();
		s.repaintCanvas();
//		VIEW.previewPanel.repaint();
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable t) {
			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public static void main(final String[] args) throws UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		
	}
	
}
