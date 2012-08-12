package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Edge;

public class TestStress {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		VIEW.panel.requestFocusInWindow();
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		MODEL.clear();
	}
	
	
	List<Edge> edges;
	
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
			CONTROLLER.mc.pressed(p);
			
			for (int i = 0; i < 20; i++) {
				p = randomPoint();
				CONTROLLER.mc.dragged(p);
			}
			
			CONTROLLER.mc.released();
			
		}
		
	}

}