package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.Main.PLATFORMCONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.core.Edge;
import com.gutabi.core.Point;

public class TestDraggingFuzz {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		PLATFORMVIEW.init();
		
		PLATFORMCONTROLLER.init();
		
		PLATFORMVIEW.frame.setVisible(true);
		PLATFORMVIEW.panel.requestFocusInWindow();
		
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
	public void testFuzz() throws Exception {
		
//		mc.pressed_M(new Point(5, 5));
//		mc.dragged_M(new Point(6, 6));
//		mc.dragged_M(new Point(7, 7));
//		mc.released_M();
		
		int a = 0;
		while (true) {
			System.out.println("iteration: " + a);
			
			long c = System.currentTimeMillis();
			
			int m = randomInt();
			
			for (int ii = 0; ii < m; ii++) {
				
				int n = randomInt();
				
				Point p = randomPoint();
				PLATFORMCONTROLLER.mc.pressed_M(p);
				PLATFORMVIEW.repaint();
				
				for (int i = 0; i < n; i++) {
					p = randomPoint();
					PLATFORMCONTROLLER.mc.dragged_M(p);
					PLATFORMVIEW.repaint();
				}
				
				PLATFORMCONTROLLER.mc.released_M();
				
				PLATFORMVIEW.repaint();
				
			}
			
			SwingUtilities.invokeAndWait(new Runnable(){

				@Override
				public void run() {
					MODEL.clear();
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
