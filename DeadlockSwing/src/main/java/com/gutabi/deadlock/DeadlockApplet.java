package com.gutabi.deadlock;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.examples.OneByOneWorld;

@SuppressWarnings("serial")
public class DeadlockApplet extends JApplet {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(DeadlockApplet app) throws Exception {
		
		MODEL.init();
		MODEL.world = new OneByOneWorld();
		MODEL.world.init();
		
		VIEW.codebase = app.getCodeBase();
		VIEW.setupApplet(app);
		VIEW.init();
		
		CONTROLLER.init();
		
		CONTROLLER.mode = ControlMode.IDLE;
		
		VIEW.renderWorldBackground();
		
		app.setVisible(true);
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
