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

public class DeadlockMain  {
	
static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI() throws Exception {
		
		MODEL.menu = new MainMenu();
		MODEL.init();
//		MODEL.world = new World();
//		MODEL.world = new FourByFourGridWorld();
//		MODEL.world = new OneByOneWorld();
//		MODEL.world.init();
		
		VIEW.codebase = new URL("file:.");
		JFrame frame = VIEW.setupFrame();
		VIEW.init();
		
//		VIEW.PIXELS_PER_METER_DEBUG = 12.5;
//		
//		VIEW.worldViewport = new AABB(
//				-(VIEW.CANVAS_WIDTH / VIEW.PIXELS_PER_METER_DEBUG) / 2 + MODEL.world.worldWidth/2 ,
//				-(VIEW.CANVAS_HEIGHT / VIEW.PIXELS_PER_METER_DEBUG) / 2 + MODEL.world.worldHeight/2,
//				VIEW.CANVAS_WIDTH / VIEW.PIXELS_PER_METER_DEBUG,
//				VIEW.CANVAS_HEIGHT / VIEW.PIXELS_PER_METER_DEBUG);
		
		CONTROLLER.init();
		
		
		
//		CONTROLLER.mode = ControlMode.IDLE;
		CONTROLLER.mode = ControlMode.MENU;
		
//		VIEW.renderWorldBackground();
		VIEW.renderMenu();
		
		frame.setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
		VIEW.canvas.postDisplay();
		
		VIEW.repaintCanvas();
//		VIEW.repaintControlPanel();
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
