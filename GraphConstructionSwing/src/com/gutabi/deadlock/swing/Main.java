package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.controller.MouseController;

public class Main  {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		//MODEL.init();
		
		VIEW.init();
		
		MouseController mc = new MouseController();
		mc.init();
		
		//CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		
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
