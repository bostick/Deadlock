package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.swing.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.utils.Point;

public class Test1 {
	
	static Logger logger = Logger.getLogger("test");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		MODEL.init();
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		
		CONTROLLER.mouseController.pressed(new Point(5, 5));
		CONTROLLER.mouseController.dragged(new Point(15, 15));
		CONTROLLER.mouseController.dragged(new Point(25, 25));
		CONTROLLER.mouseController.dragged(new Point(35, 35));
		CONTROLLER.mouseController.dragged(new Point(4, 4));
		CONTROLLER.mouseController.released();
		
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
