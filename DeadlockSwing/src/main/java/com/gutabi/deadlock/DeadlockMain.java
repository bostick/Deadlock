package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.DeadlockController.ControlMode;
import com.gutabi.deadlock.menu.MainMenu;

public class DeadlockMain  {
	
static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI() throws Exception {
		
		APP.codebase = new URL("file:.");
		
		CONTROLLER.mode = ControlMode.MENU;
		APP.menu = new MainMenu();
		
		APP.init();
		
		VIEW.setupFrame();
		VIEW.init();
		
		APP.menu.render();
		
		((JFrame)VIEW.container).setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
		VIEW.postDisplay();
		
		VIEW.repaint();
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
