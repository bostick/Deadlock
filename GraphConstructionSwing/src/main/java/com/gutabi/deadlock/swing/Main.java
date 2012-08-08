package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.LoggerFactory.LOGGERFACTORY;
import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.controller.DeadlockController;
import com.gutabi.deadlock.core.view.DeadlockView;
import com.gutabi.deadlock.swing.controller.PlatformController;
import com.gutabi.deadlock.swing.view.PlatformView;
import com.gutabi.deadlock.swing.view.SwingWindowInfo;

public class Main  {
	
	public static PlatformView PLATFORMVIEW;
	public static PlatformController PLATFORMCONTROLLER;
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		LOGGERFACTORY = new PlatformLoggerFactory();
		
		PLATFORMVIEW = new PlatformView();
		PLATFORMCONTROLLER = new PlatformController();
		VIEW = new DeadlockView(new SwingWindowInfo());
		CONTROLLER = new DeadlockController();
		
		PLATFORMVIEW.init();
		PLATFORMCONTROLLER.init();
		
//		VIEW.window = new PlatformWindow();
//		VIEW.logger = new PlatformLogger(VIEW.getClass());
		VIEW.init();
		
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
