package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;
import static com.gutabi.deadlock.swing.Main.PLATFORMCONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.controller.DeadlockController;
import com.gutabi.deadlock.core.controller.MassageStrategy;
import com.gutabi.deadlock.core.view.DeadlockView;
import com.gutabi.deadlock.swing.controller.PlatformController;
import com.gutabi.deadlock.swing.view.PlatformView;
import com.gutabi.deadlock.swing.view.SwingWindowInfo;

public class TestDraggingFuzz {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.NONE;
		
	}

	@After
	public void tearDown() throws Exception {
		MODEL.clear();
	}
	
	
	List<Edge> edges;
	
	int randomInt() {
		return 0 + (int)(Math.random() * ((20 - 0) + 1));
	}
	
	double randomRadian() {
		return Math.random() * Math.PI * 2;
	}
	
	double randomDist() {
		return 1.0 + (int)(Math.random() * (100.0 - 1.0));
	}
	
	DPoint randomPoint() {
		int x = 0 + (int)(Math.random() * ((100 - 0) + 1));
		int y = 0 + (int)(Math.random() * ((100 - 0) + 1));
		return new DPoint(x, y);
	}
	
	@Test
	public void testFuzz() throws Exception {
		
		int a = 0;
		while (true) {
			System.out.println("iteration: " + a);
			
			long c = System.currentTimeMillis();
			
			int m = randomInt();
			
			for (int ii = 0; ii < m; ii++) {
				
				int n = randomInt();
				
				DPoint p = randomPoint();
				PLATFORMCONTROLLER.mc.pressed(p);
				PLATFORMVIEW.repaint();
				
				for (int i = 0; i < n; i++) {
					double rad = randomRadian();
					double d = randomDist();
					p = new DPoint((int)(Math.cos(rad) * d) + p.x, (int)(Math.sin(rad) * d) + p.y);
					PLATFORMCONTROLLER.mc.dragged(p);
					PLATFORMVIEW.repaint();
				}
				
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMVIEW.repaint();
				
			}
			
			SwingUtilities.invokeAndWait(new Runnable(){

				@Override
				public void run() {
					MODEL.clear();
					//CONTROLLER.allStrokes.clear();
				}});
			
			PLATFORMVIEW.repaint();
			
			System.out.println("iteration " + a + " took " + (System.currentTimeMillis() - c) + " milliseconds");
			
			a++;
		}
		
//		SwingUtilities.invokeAndWait(new Runnable() {
//			@Override
//			public void run() {
//				edges = MODEL.getEdges();
//			}
//		});
		
	}
	
}
