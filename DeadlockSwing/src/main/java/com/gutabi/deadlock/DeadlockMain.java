package com.gutabi.deadlock;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

public class DeadlockMain  {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		MODEL.init();
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		VIEW.panel.requestFocusInWindow();
		
		MODEL.world.renderBackground();
		VIEW.repaint();
		
		
//		Intersection start = new Intersection(VIEW.panelToWorld(new Point(550, 250)));
//		MODEL.world.graph.addIntersection(start);
//		
//		Intersection end = new Intersection(VIEW.panelToWorld(new Point(900, 550)));
//		MODEL.world.graph.addIntersection(end);
		
//		List<Point> pts = new ArrayList<Point>();
//		pts.add(VIEW.panelToWorld(new Point(550, 250)));
//		pts.add(VIEW.panelToWorld(new Point(900, 550)));
//		MODEL.world.graph.createEdgeTop(start, end, pts);
//		
//		MODEL.world.postDraftingTop();
//		
//		
//		
//		MODEL.world.renderBackground();
//		VIEW.repaint();
		
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
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
