package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.controller.DeadlockController;
import com.gutabi.deadlock.controller.MassageStrategy;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.view.DeadlockView;

public class TestDraggingFuzz {
	
	static Point OFFSET = new Point(0, 0);
	
	Runnable empty = new Runnable(){
		@Override
		public void run() {
			;
		}};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW = new DeadlockView();
		CONTROLLER = new DeadlockController();
		
//		VIEW.window = new Window();
//		VIEW.logger = new Logger(VIEW.getClass());
		VIEW.init();
		
		VIEW.frame.setVisible(true);
		VIEW.panel.requestFocusInWindow();
		
	}
	
	public void testPressed(Point p) throws Exception {
		Point pp = p.add(OFFSET);
		CONTROLLER.mc.pressed(new MouseEvent(null, 0, 0, 0, (int)pp.getX(), (int)pp.getY(), 0, false));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testDragged(Point p) throws Exception {
		Point pp = p.add(OFFSET);
		CONTROLLER.mc.dragged(new MouseEvent(null, 0, 0, 0, (int)pp.getX(), (int)pp.getY(), 0, false));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testReleased() throws Exception {
		CONTROLLER.mc.released(new MouseEvent(null, 0, 0, 0, 0, 0, 0, false));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
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
	
	Point randomPoint() {
		int x = 0 + (int)(Math.random() * ((100 - 0) + 1));
		int y = 0 + (int)(Math.random() * ((100 - 0) + 1));
		return new Point(x, y);
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
				
				Point p = randomPoint();
				testPressed(p);
				VIEW.repaint();
				
				for (int i = 0; i < n; i++) {
					double rad = randomRadian();
					double d = randomDist();
					p = new Point((int)(Math.cos(rad) * d) + p.getX(), (int)(Math.sin(rad) * d) + p.getY());
					testDragged(p);
					VIEW.repaint();
				}
				
				testReleased();
				
				VIEW.repaint();
				
			}
			
			SwingUtilities.invokeAndWait(new Runnable(){

				@Override
				public void run() {
					MODEL.clear();
					//CONTROLLER.allStrokes.clear();
				}});
			
			VIEW.repaint();
			
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
