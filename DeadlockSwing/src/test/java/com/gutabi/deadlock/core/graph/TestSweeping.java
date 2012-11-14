package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.TestDragging.testWorldDragged;
import static com.gutabi.deadlock.swing.TestDragging.testWorldPressed;
import static com.gutabi.deadlock.swing.TestDragging.testWorldReleased;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.Point;

public class TestSweeping {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		CONTROLLER.init();
		MODEL.init();
		
		VIEW.frame.setVisible(true);
		VIEW.canvas.requestFocusInWindow();
		
	}
	
	@Before
	public void setUp() throws Exception {
		
		MODEL.init();
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		assertTrue(MODEL.world.checkConsistency());
		
		Thread.sleep(2000);
		
//		MODEL.
	}
	
	@Test
	public void test1() throws Exception {
		
		testWorldPressed(new Point(5, 5));
		testWorldDragged(new Point(10, 10));
		testWorldReleased();
		
//		testWorldPressed(new Point(9.0, 9.0));
//		testWorldDragged(new Point(9.0625, 9.03125));
//		testWorldDragged(new Point(9.125, 9.0625));
//		testWorldDragged(new Point(9.1875, 9.15625));
//		testWorldDragged(new Point(9.25, 9.25));
//		testWorldDragged(new Point(9.3125, 9.34375));
//		testWorldDragged(new Point(9.40625, 9.4375));
//		testWorldDragged(new Point(9.4375, 9.5625));
//		testWorldDragged(new Point(9.5, 9.71875));
//		testWorldDragged(new Point(9.53125, 9.84375));
//		testWorldDragged(new Point(9.5625, 9.90625)); 
//		testWorldDragged(new Point(9.625, 9.96875));
//		testWorldDragged(new Point(9.65625, 10.0625));
//		testWorldDragged(new Point(9.75, 10.15625));
//		testWorldDragged(new Point(9.84375, 10.28125));
//		testWorldDragged(new Point(9.9375, 10.4375));
//		testWorldDragged(new Point(10.09375, 10.65625));
//		testWorldDragged(new Point(10.15625, 10.84375));
//		testWorldDragged(new Point(10.1875, 11.03125));
//		testWorldDragged(new Point(10.25, 11.1875));
//		testWorldDragged(new Point(10.25, 11.25));
//		testWorldDragged(new Point(10.25, 11.34375));
//		testWorldDragged(new Point(10.25, 11.46875));
//		testWorldReleased();
		
		Thread.sleep(100000);
		
	}
	
	
	@Test
	public void test2() throws Exception {
		
		testWorldPressed(new Point(3, 3));
		testWorldDragged(new Point(13, 13));
		testWorldReleased();
		
		testWorldPressed(new Point(8.0, 3.0));
		testWorldDragged(new Point(8.0, 13.0));
		testWorldReleased();
		
	}
	
	@Test
	public void test3() throws Exception {
		
		testWorldPressed(new Point(0, 0));
		testWorldDragged(new Point(16, 16));
		testWorldReleased();
		
		testWorldPressed(new Point(16, 0));
		testWorldDragged(new Point(0, 16.0));
		testWorldReleased();
		
	}
	
	@Test
	public void test4() throws Exception {
		
		testWorldPressed(new Point(1.21875, 14.75));
		testWorldDragged(new Point(1.3125, 14.7188));
		testWorldDragged(new Point(1.4375, 14.625));
		testWorldDragged(new Point(1.59375, 14.5313));
		testWorldDragged(new Point(1.6875, 14.4063));
		testWorldDragged(new Point(1.84375, 14.125));
		testWorldDragged(new Point(2.0625, 13.6563));
		testWorldDragged(new Point(2.21875, 13.3438));
		testWorldDragged(new Point(2.40625, 13.0625));
		testWorldDragged(new Point(2.5625, 12.9063));
		testWorldDragged(new Point(2.71875, 12.6875));
		testWorldDragged(new Point(3.125, 12.375));
		testWorldDragged(new Point(3.6875, 11.8125));
		testWorldDragged(new Point(4.28125, 11.0625));
		testWorldDragged(new Point(4.75, 10.5));
		testWorldDragged(new Point(5.15625, 10.0313));
		testWorldDragged(new Point(5.5625, 9.6875));
		testWorldDragged(new Point(5.875, 9.375));
		testWorldDragged(new Point(6.28125, 8.96875));
		testWorldDragged(new Point(6.6875, 8.4375));
		testWorldDragged(new Point(7.1875, 7.875));
		testWorldDragged(new Point(7.65625, 7.375));
		testWorldDragged(new Point(8.21875, 7.03125));
		testWorldDragged(new Point(8.59375, 6.78125));
		testWorldDragged(new Point(8.90625, 6.53125));
		testWorldDragged(new Point(9.1875, 6.375));
		testWorldDragged(new Point(9.34375, 6.28125));
		testWorldDragged(new Point(9.5625, 6.09375));
		testWorldDragged(new Point(9.78125, 5.9375));
		testWorldDragged(new Point(9.9375, 5.84375));
		testWorldDragged(new Point(10.0938, 5.625));
		testWorldDragged(new Point(10.3125, 5.40625));
		testWorldDragged(new Point(10.625, 5.));
		testWorldDragged(new Point(11.0313, 4.6875));
		testWorldDragged(new Point(11.4063, 4.4375));
		testWorldDragged(new Point(11.7188, 4.25));
		testWorldDragged(new Point(12., 4.0625));
		testWorldDragged(new Point(12.3125, 3.96875));
		testWorldDragged(new Point(12.4375, 3.8125));
		testWorldDragged(new Point(12.5938, 3.71875));
		testWorldDragged(new Point(12.6875, 3.65625));
		testWorldDragged(new Point(12.8125, 3.5625));
		testWorldDragged(new Point(12.9063, 3.46875));
		testWorldDragged(new Point(13., 3.375));
		testWorldDragged(new Point(13.0313, 3.34375));
		testWorldDragged(new Point(13.125, 3.28125));
		testWorldDragged(new Point(13.1875, 3.125));
		testWorldDragged(new Point(13.2188, 3.09375));
		testWorldDragged(new Point(13.25, 2.96875));
		testWorldDragged(new Point(13.2813, 2.875));
		testWorldDragged(new Point(13.3438, 2.78125));
		testWorldDragged(new Point(13.375, 2.65625));
		testWorldDragged(new Point(13.4375, 2.5625));
		testWorldDragged(new Point(13.5313, 2.46875));
		testWorldDragged(new Point(13.5625, 2.375));
		testWorldDragged(new Point(13.6563, 2.28125));
		testWorldDragged(new Point(13.6875, 2.21875));
		testWorldDragged(new Point(13.7813, 2.125));
		testWorldDragged(new Point(13.875, 2.0625));
		testWorldDragged(new Point(13.9688, 1.96875));
		testWorldDragged(new Point(14., 1.9375));
		testWorldDragged(new Point(14.0938, 1.875));
		testWorldDragged(new Point(14.125, 1.78125));
		testWorldDragged(new Point(14.2188, 1.6875));
		testWorldDragged(new Point(14.2813, 1.59375));
		testWorldDragged(new Point(14.3125, 1.5));
		testWorldDragged(new Point(14.3125, 1.46875));
		testWorldDragged(new Point(14.3438, 1.40625));
		testWorldDragged(new Point(14.4063, 1.3125));
		testWorldDragged(new Point(14.4063, 1.28125));
		testWorldDragged(new Point(14.4063, 1.21875));
		testWorldReleased();
		
		
		
		
		testWorldPressed(new Point(1.03125, 1.));
		testWorldDragged(new Point(1.0625, 1.));
		testWorldDragged(new Point(1.09375, 1.09375));
		testWorldDragged(new Point(1.1875, 1.1875));
		testWorldDragged(new Point(1.3125, 1.375));
		testWorldDragged(new Point(1.4375, 1.53125));
		testWorldDragged(new Point(1.75, 1.78125));
		testWorldDragged(new Point(2.15625, 2.03125));
		testWorldDragged(new Point(2.53125, 2.28125));
		testWorldDragged(new Point(2.84375, 2.59375));
		testWorldDragged(new Point(3.28125, 3.25));
		testWorldDragged(new Point(3.6875, 3.875));
		testWorldDragged(new Point(4.1875, 4.4375));
		testWorldDragged(new Point(4.71875, 4.84375));
		testWorldDragged(new Point(5.375, 5.21875));
		testWorldDragged(new Point(5.90625, 5.5625));
		testWorldDragged(new Point(6.46875, 5.90625));
		testWorldDragged(new Point(6.875, 6.375));
		testWorldDragged(new Point(7.21875, 7.));
		testWorldDragged(new Point(7.625, 7.46875));
		testWorldDragged(new Point(7.96875, 7.9375));
		testWorldDragged(new Point(8.34375, 8.1875));
		testWorldDragged(new Point(8.65625, 8.5));
		testWorldDragged(new Point(9.0625, 8.78125));
		testWorldDragged(new Point(9.34375, 9.03125));
		testWorldDragged(new Point(10.0625, 9.65625));
		testWorldDragged(new Point(10.3438, 10.1875));
		testWorldDragged(new Point(10.5938, 10.5));
		testWorldDragged(new Point(10.8125, 10.8125));
		testWorldDragged(new Point(11., 11.125));
		testWorldDragged(new Point(11.2188, 11.2813));
		testWorldDragged(new Point(11.375, 11.4375));
		testWorldDragged(new Point(11.4688, 11.5313));
		testWorldDragged(new Point(11.625, 11.6563));
		testWorldDragged(new Point(11.75, 11.8125));
		testWorldDragged(new Point(11.9063, 11.9688));
		testWorldDragged(new Point(12.0625, 12.125));
		testWorldDragged(new Point(12.2188, 12.3438));
		testWorldDragged(new Point(12.4688, 12.5625));
		testWorldDragged(new Point(12.625, 12.7813));
		testWorldDragged(new Point(12.8438, 12.9063));
		testWorldDragged(new Point(13., 13.0313));
		testWorldDragged(new Point(13.125, 13.125));
		testWorldDragged(new Point(13.2813, 13.25));
		testWorldDragged(new Point(13.4063, 13.3438));
		testWorldDragged(new Point(13.5, 13.4375));
		testWorldDragged(new Point(13.5938, 13.4688));
		testWorldDragged(new Point(13.6875, 13.5));
		testWorldDragged(new Point(13.7188, 13.5));
		testWorldDragged(new Point(13.75, 13.5313));
		testWorldDragged(new Point(13.7813, 13.5938));
		testWorldDragged(new Point(13.8125, 13.5938));
		testWorldDragged(new Point(13.875, 13.625));
		testWorldDragged(new Point(13.9063, 13.625));
		testWorldDragged(new Point(13.9063, 13.6563));
		testWorldDragged(new Point(13.9375, 13.6563));
		testWorldDragged(new Point(13.9375, 13.6875));
		testWorldDragged(new Point(13.9688, 13.6875));
		testWorldDragged(new Point(13.9688, 13.7188));
		testWorldDragged(new Point(14., 13.7188));
		testWorldDragged(new Point(14., 13.75));
		testWorldDragged(new Point(14.0313, 13.75));
		testWorldReleased();
		
		
//		Thread.sleep(100000);
	}
	
}
