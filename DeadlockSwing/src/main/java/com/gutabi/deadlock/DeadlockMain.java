package com.gutabi.deadlock;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.model.menu.MainMenu;
import static com.gutabi.deadlock.DeadlockApplication.APP;

public class DeadlockMain  {
	
static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI() throws Exception {
		
		MODEL.menu = new MainMenu();
		
		APP.codebase = new URL("file:.");
		
		VIEW.setupFrame();
		VIEW.init();
		
		CONTROLLER.mode = ControlMode.MENU;
		
		MODEL.menu.render();
		
		((JFrame)VIEW.container).setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
		VIEW.postDisplay();
		
		VIEW.repaintCanvas();
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
