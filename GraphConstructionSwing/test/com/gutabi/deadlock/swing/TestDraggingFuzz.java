package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.swing.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.utils.Point;
import static org.junit.Assert.*;

public class TestDraggingFuzz {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		MODEL.init();
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		MODEL.clear_M();
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
	public void testFuzz() throws Exception {
		
//		CONTROLLER.mouseController.pressed_M(new Point(5, 5));
//		CONTROLLER.mouseController.dragged_M(new Point(6, 6));
//		CONTROLLER.mouseController.dragged_M(new Point(7, 7));
//		CONTROLLER.mouseController.released_M();
		
		while (true) {
			
			int m = randomInt();
			
			for (int ii = 0; ii < m; ii++) {
				
				int n = randomInt();
				
				Point p = randomPoint();
				CONTROLLER.mouseController.pressed_M(p);
				
				for (int i = 0; i < n; i++) {
					p = randomPoint();
					CONTROLLER.mouseController.dragged_M(p);
				}
				
				CONTROLLER.mouseController.released_M();
				
				VIEW.repaint();
				
			}
			
			MODEL.clear_M();
			VIEW.repaint();
			
		}
		
//		SwingUtilities.invokeAndWait(new Runnable() {
//			@Override
//			public void run() {
//				edges = MODEL.getEdges();
//			}
//		});
		
	}
	
}
