package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.DeadlockModel.APP;
import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.TestDragging.testDragged;
import static com.gutabi.deadlock.swing.TestDragging.testPressed;
import static com.gutabi.deadlock.swing.TestDragging.testReleased;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.graph.Road;

public class TestStress {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		APP.init();
		
	}

	@After
	public void tearDown() throws Exception {
//		MODEL.clear();
	}
	
	
	List<Road> edges;
	
	int randomInt() {
		return 0 + (int)(Math.random() * ((20 - 0) + 1));
	}
	
	Point randomPoint() {
		int x = 0 + (int)(Math.random() * ((500 - 0) + 1));
		int y = 0 + (int)(Math.random() * ((500 - 0) + 1));
		return new Point(x, y);
	}
	
	@Test
	public void testMemory() throws Exception {
		
		int m = 20;
		
		for (int ii = 0; ii < m; ii++) {
			
			Point p = randomPoint();
			testPressed(p);
			
			for (int i = 0; i < 20; i++) {
				p = randomPoint();
				testDragged(p);
			}
			
			testReleased();
			
		}
		
	}

}
