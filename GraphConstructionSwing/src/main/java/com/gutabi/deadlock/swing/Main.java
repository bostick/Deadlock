package com.gutabi.deadlock.swing;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.controller.PlatformController;
import com.gutabi.deadlock.swing.view.PlatformView;

public class Main  {
	
	public static PlatformView PLATFORMVIEW = new PlatformView();
	public static PlatformController PLATFORMCONTROLLER = new PlatformController();
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		PLATFORMVIEW.init();
		
		PLATFORMCONTROLLER.init();
		
		PLATFORMVIEW.frame.setVisible(true);
		PLATFORMVIEW.panel.requestFocusInWindow();
		
	}
	
	static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable t) {
			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					createAndShowGUI(args);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}
	
}
