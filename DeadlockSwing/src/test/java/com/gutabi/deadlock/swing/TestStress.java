package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.swing.controller.PlatformController;
import com.gutabi.deadlock.swing.view.PlatformView;

public class TestStress {

	public static PlatformView PLATFORMVIEW;
	public static PlatformController PLATFORMCONTROLLER;
	
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
	
	DPoint randomPoint() {
		int x = 0 + (int)(Math.random() * ((500 - 0) + 1));
		int y = 0 + (int)(Math.random() * ((500 - 0) + 1));
		return new DPoint(x, y);
	}
	
	@Test
	public void testMemory() throws Exception {
		
		int m = 20;
		
		for (int ii = 0; ii < m; ii++) {
			
			DPoint p = randomPoint();
			PLATFORMCONTROLLER.mc.pressed(p);
			
			for (int i = 0; i < 20; i++) {
				p = randomPoint();
				PLATFORMCONTROLLER.mc.dragged(p);
			}
			
			PLATFORMCONTROLLER.mc.released();
			
		}
		
	}

}
