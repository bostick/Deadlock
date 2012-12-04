package com.gutabi.deadlock;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.examples.OneByOneWorld;

public class DeadlockMain  {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		MODEL.init();
//		MODEL.world = new World();
//		MODEL.world = new FourByFourGridWorld();
		MODEL.world = new OneByOneWorld();
		MODEL.world.init();
		
		VIEW.codebase = new URL("file:.");
		JFrame frame = VIEW.setupFrame();
		VIEW.init();
		
		CONTROLLER.init();
		
		
		
		CONTROLLER.mode = ControlMode.IDLE;
		
		VIEW.renderWorldBackground();
		
		frame.setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
		VIEW.canvas.postDisplay();
		
		VIEW.repaintCanvas();
		VIEW.repaintControlPanel();
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable t) {
			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		SwingUtilities.invokeLater(new Runnable() {
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
