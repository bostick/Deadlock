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
		
		VIEW.canvas.postDisplay();
		
		VIEW.renderBackgroundFresh();
	}
	
	@Before
	public void setUp() throws Exception {
		
		VIEW.canvas.removeMouseListener(CONTROLLER.mc);
		VIEW.canvas.removeMouseMotionListener(CONTROLLER.mc);
		
		MODEL.init();
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		assertTrue(MODEL.world.checkConsistency());
		
		Thread.sleep(2000);
		
		VIEW.canvas.addMouseListener(CONTROLLER.mc);
		VIEW.canvas.addMouseMotionListener(CONTROLLER.mc);
		
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
	
	@Test
	public void test5() throws Exception {
		
		testWorldPressed(new Point(0.9375, 7.65625));
		testWorldDragged(new Point(1., 7.65625));
		testWorldDragged(new Point(1.1875, 7.65625));
		testWorldDragged(new Point(1.375, 7.65625));
		testWorldDragged(new Point(1.625, 7.65625));
		testWorldDragged(new Point(1.90625, 7.65625));
		testWorldDragged(new Point(2.15625, 7.65625));
		testWorldDragged(new Point(2.6875, 7.625));
		testWorldDragged(new Point(3.28125, 7.625));
		testWorldDragged(new Point(3.78125, 7.625));
		testWorldDragged(new Point(4.3125, 7.625));
		testWorldDragged(new Point(4.65625, 7.625));
		testWorldDragged(new Point(5.15625, 7.625));
		testWorldDragged(new Point(5.6875, 7.625));
		testWorldDragged(new Point(6.28125, 7.625));
		testWorldDragged(new Point(6.875, 7.625));
		testWorldDragged(new Point(7.3125, 7.65625));
		testWorldDragged(new Point(7.75, 7.65625));
		testWorldDragged(new Point(8.09375, 7.65625));
		testWorldDragged(new Point(8.53125, 7.71875));
		testWorldDragged(new Point(8.96875, 7.71875));
		testWorldDragged(new Point(9.46875, 7.78125));
		testWorldDragged(new Point(9.9375, 7.84375));
		testWorldDragged(new Point(10.3438, 7.84375));
		testWorldDragged(new Point(10.6875, 7.84375));
		testWorldDragged(new Point(11.0625, 7.90625));
		testWorldDragged(new Point(11.3125, 7.90625));
		testWorldDragged(new Point(11.5938, 7.96875));
		testWorldDragged(new Point(11.9375, 7.96875));
		testWorldDragged(new Point(12.2188, 7.96875));
		testWorldDragged(new Point(12.5625, 7.96875));
		testWorldDragged(new Point(12.75, 7.96875));
		testWorldDragged(new Point(13., 7.96875));
		testWorldDragged(new Point(13.2813, 7.96875));
		testWorldDragged(new Point(13.4063, 7.96875));
		testWorldDragged(new Point(13.5313, 7.96875));
		testWorldDragged(new Point(13.625, 7.96875));
		testWorldDragged(new Point(13.7188, 7.96875));
		testWorldDragged(new Point(13.7813, 7.96875));
		testWorldDragged(new Point(13.875, 7.96875));
		testWorldDragged(new Point(13.9063, 8.));
		testWorldDragged(new Point(14., 8.));
		testWorldDragged(new Point(14.0625, 8.));
		testWorldDragged(new Point(14.0938, 8.));
		testWorldDragged(new Point(14.1875, 8.));
		testWorldDragged(new Point(14.2188, 8.));
		testWorldDragged(new Point(14.25, 8.));
		testWorldDragged(new Point(14.3438, 8.));
		testWorldDragged(new Point(14.375, 8.));
		testWorldDragged(new Point(14.4375, 8.));
		testWorldDragged(new Point(14.5313, 8.));
		testWorldDragged(new Point(14.5625, 8.));
		testWorldDragged(new Point(14.5938, 8.));
		testWorldDragged(new Point(14.6875, 8.));
		testWorldDragged(new Point(14.7188, 8.));
		testWorldDragged(new Point(14.75, 8.));
		testWorldReleased();
		
		testWorldPressed(new Point(8.03125, 0.46875));
		testWorldDragged(new Point(8.03125, 0.5));
		testWorldDragged(new Point(8.03125, 0.6875));
		testWorldDragged(new Point(8.03125, 0.875));
		testWorldDragged(new Point(8.03125, 1.0625));
		testWorldDragged(new Point(8.03125, 1.40625));
		testWorldDragged(new Point(8.03125, 2.));
		testWorldDragged(new Point(8.03125, 2.59375));
		testWorldDragged(new Point(7.9375, 3.1875));
		testWorldDragged(new Point(7.875, 3.96875));
		testWorldDragged(new Point(7.71875, 5.0625));
		testWorldDragged(new Point(7.65625, 5.8125));
		testWorldDragged(new Point(7.59375, 6.5));
		testWorldDragged(new Point(7.53125, 7.375));
		testWorldDragged(new Point(7.53125, 8.28125));
		testWorldDragged(new Point(7.53125, 8.96875));
		testWorldDragged(new Point(7.53125, 9.5625));
		testWorldDragged(new Point(7.53125, 10.));
		testWorldDragged(new Point(7.53125, 10.3438));
		testWorldDragged(new Point(7.625, 10.9688));
		testWorldDragged(new Point(7.75, 11.5));
		testWorldDragged(new Point(7.875, 11.9375));
		testWorldDragged(new Point(8., 12.3125));
		testWorldDragged(new Point(8.125, 12.5938));
		testWorldDragged(new Point(8.21875, 12.75));
		testWorldDragged(new Point(8.25, 12.875));
		testWorldDragged(new Point(8.25, 12.9688));
		testWorldDragged(new Point(8.25, 13.0938));
		testWorldDragged(new Point(8.3125, 13.25));
		testWorldDragged(new Point(8.3125, 13.4375));
		testWorldDragged(new Point(8.3125, 13.5625));
		testWorldDragged(new Point(8.3125, 13.7188));
		testWorldDragged(new Point(8.3125, 13.7813));
		testWorldDragged(new Point(8.3125, 13.875));
		testWorldDragged(new Point(8.3125, 13.9375));
		testWorldDragged(new Point(8.3125, 14.0625));
		testWorldDragged(new Point(8.3125, 14.1563));
		testWorldDragged(new Point(8.34375, 14.1875));
		testWorldDragged(new Point(8.34375, 14.2188));
		testWorldDragged(new Point(8.34375, 14.2813));
		testWorldDragged(new Point(8.375, 14.2813));
		testWorldDragged(new Point(8.375, 14.375));
		testWorldDragged(new Point(8.375, 14.4063));
		testWorldDragged(new Point(8.375, 14.5));
		testWorldDragged(new Point(8.375, 14.5625));
		testWorldDragged(new Point(8.375, 14.6563));
		testWorldDragged(new Point(8.375, 14.7813));
		testWorldDragged(new Point(8.375, 14.875));
		testWorldDragged(new Point(8.375, 14.9375));
		testWorldDragged(new Point(8.375, 15.));
		testWorldDragged(new Point(8.375, 15.0313));
		testWorldDragged(new Point(8.375, 15.0625));
		testWorldDragged(new Point(8.375, 15.0938));
		testWorldDragged(new Point(8.375, 15.125));
		testWorldReleased();
		
	}
	
//	@SuppressWarnings("static-access")
	@Test
	public void test6() throws Exception {
		
//		MODEL.DEBUG_DRAW = true;
		
		testWorldPressed(new Point(1.21875, 3.90625));
		testWorldDragged(new Point(1.3125, 3.90625));
		testWorldDragged(new Point(1.625, 3.90625));
		testWorldDragged(new Point(2., 3.875));
		testWorldDragged(new Point(2.25, 3.875));
		testWorldDragged(new Point(2.53125, 3.8125));
		testWorldDragged(new Point(2.65625, 3.8125));
		testWorldDragged(new Point(2.875, 3.8125));
		testWorldDragged(new Point(3.125, 3.8125));
		testWorldDragged(new Point(3.46875, 3.8125));
		testWorldDragged(new Point(3.96875, 3.8125));
		testWorldDragged(new Point(4.40625, 3.75));
		testWorldDragged(new Point(4.75, 3.75));
		testWorldDragged(new Point(5.03125, 3.75));
		testWorldDragged(new Point(5.3125, 3.71875));
		testWorldDragged(new Point(5.65625, 3.71875));
		testWorldDragged(new Point(6.09375, 3.65625));
		testWorldDragged(new Point(6.6875, 3.65625));
		testWorldDragged(new Point(7.125, 3.65625));
		testWorldDragged(new Point(7.5625, 3.65625));
		testWorldDragged(new Point(7.8125, 3.65625));
		testWorldDragged(new Point(8.09375, 3.65625));
		testWorldDragged(new Point(8.34375, 3.65625));
		testWorldDragged(new Point(8.53125, 3.65625));
		testWorldDragged(new Point(8.78125, 3.65625));
		testWorldDragged(new Point(9.125, 3.65625));
		testWorldDragged(new Point(9.5625, 3.65625));
		testWorldDragged(new Point(9.9375, 3.65625));
		testWorldDragged(new Point(10.2813, 3.65625));
		testWorldDragged(new Point(10.5313, 3.65625));
		testWorldDragged(new Point(10.8125, 3.71875));
		testWorldDragged(new Point(11., 3.71875));
		testWorldDragged(new Point(11.1875, 3.78125));
		testWorldDragged(new Point(11.4688, 3.84375));
		testWorldDragged(new Point(11.8125, 3.84375));
		testWorldDragged(new Point(12.1875, 3.875));
		testWorldDragged(new Point(12.5313, 3.875));
		testWorldDragged(new Point(12.9063, 3.9375));
		testWorldDragged(new Point(13.1563, 3.9375));
		testWorldDragged(new Point(13.4375, 4.));
		testWorldDragged(new Point(13.625, 4.));
		testWorldDragged(new Point(13.875, 4.0625));
		testWorldDragged(new Point(14.0938, 4.0625));
		testWorldDragged(new Point(14.2813, 4.09375));
		testWorldDragged(new Point(14.5313, 4.09375));
		testWorldDragged(new Point(14.875, 4.09375));
		testWorldDragged(new Point(15.1563, 4.15625));
		testWorldDragged(new Point(15.5, 4.15625));
		testWorldDragged(new Point(15.7188, 4.21875));
		testWorldDragged(new Point(15.9688, 4.21875));
		testWorldDragged(new Point(16.1563, 4.25));
		testWorldDragged(new Point(16.3125, 4.25));
		testWorldDragged(new Point(16.4063, 4.3125));
		testWorldDragged(new Point(16.5313, 4.3125));
		testWorldDragged(new Point(16.625, 4.34375));
		testWorldDragged(new Point(16.7188, 4.40625));
		testWorldDragged(new Point(16.7813, 4.4375));
		testWorldDragged(new Point(16.8438, 4.4375));
		testWorldDragged(new Point(16.875, 4.46875));
		testWorldDragged(new Point(16.9063, 4.46875));
		testWorldReleased();

		testWorldPressed(new Point(8.25, 0.84375));
		testWorldDragged(new Point(8.28125, 0.84375));
		testWorldDragged(new Point(8.3125, 0.84375));
		testWorldDragged(new Point(8.34375, 0.875));
		testWorldDragged(new Point(8.40625, 0.90625));
		testWorldDragged(new Point(8.46875, 0.96875));
		testWorldDragged(new Point(8.625, 1.));
		testWorldDragged(new Point(8.75, 1.0625));
		testWorldDragged(new Point(8.90625, 1.09375));
		testWorldDragged(new Point(9.03125, 1.1875));
		testWorldDragged(new Point(9.1875, 1.28125));
		testWorldDragged(new Point(9.3125, 1.4375));
		testWorldDragged(new Point(9.4375, 1.59375));
		testWorldDragged(new Point(9.53125, 1.71875));
		testWorldDragged(new Point(9.5625, 1.875));
		testWorldDragged(new Point(9.71875, 2.));
		testWorldDragged(new Point(9.8125, 2.09375));
		testWorldDragged(new Point(9.90625, 2.25));
		testWorldDragged(new Point(10., 2.34375));
		testWorldDragged(new Point(10.125, 2.5));
		testWorldDragged(new Point(10.2813, 2.59375));
		testWorldDragged(new Point(10.375, 2.71875));
		testWorldDragged(new Point(10.4688, 2.875));
		testWorldDragged(new Point(10.625, 3.09375));
		testWorldDragged(new Point(10.7813, 3.21875));
		testWorldDragged(new Point(10.8438, 3.4375));
		testWorldDragged(new Point(10.9375, 3.625));
		testWorldDragged(new Point(11.0313, 3.78125));
		testWorldDragged(new Point(11.125, 3.9375));
		testWorldDragged(new Point(11.1875, 4.0625));
		testWorldDragged(new Point(11.25, 4.15625));
		testWorldDragged(new Point(11.3125, 4.3125));
		testWorldDragged(new Point(11.375, 4.5));
		testWorldDragged(new Point(11.4063, 4.84375));
		testWorldDragged(new Point(11.4063, 5.1875));
		testWorldDragged(new Point(11.4688, 5.5625));
		testWorldDragged(new Point(11.4688, 5.8125));
		testWorldDragged(new Point(11.4688, 5.96875));
		testWorldDragged(new Point(11.4688, 6.15625));
		testWorldDragged(new Point(11.4688, 6.34375));
		testWorldDragged(new Point(11.4063, 6.6875));
		testWorldDragged(new Point(11.3438, 7.1875));
		testWorldDragged(new Point(11.2188, 7.65625));
		testWorldDragged(new Point(11., 8.03125));
		testWorldDragged(new Point(10.8438, 8.25));
		testWorldDragged(new Point(10.7188, 8.53125));
		testWorldDragged(new Point(10.4688, 9.));
		testWorldDragged(new Point(10.25, 9.71875));
		testWorldDragged(new Point(10.0625, 10.3125));
		testWorldDragged(new Point(9.78125, 10.9375));
		testWorldDragged(new Point(9.5, 11.4063));
		testWorldDragged(new Point(9.28125, 12.1875));
		testWorldDragged(new Point(9.09375, 12.9688));
		testWorldDragged(new Point(8.9375, 13.75));
		testWorldDragged(new Point(8.8125, 14.1875));
		testWorldDragged(new Point(8.625, 14.5625));
		testWorldDragged(new Point(8.5625, 14.9375));
		testWorldDragged(new Point(8.5, 15.4688));
		testWorldDragged(new Point(8.4375, 15.9688));
		testWorldDragged(new Point(8.375, 16.4063));
		testWorldDragged(new Point(8.3125, 16.625));
		testWorldDragged(new Point(8.28125, 16.8125));
		testWorldDragged(new Point(8.28125, 16.8438));
		testWorldDragged(new Point(8.25, 16.875));
		testWorldDragged(new Point(8.25, 16.9375));
		testWorldReleased();
		
		
	}
	
	
	@SuppressWarnings("static-access")
	@Test
	public void test7() throws Exception {
		
		MODEL.DEBUG_DRAW = true;
		
		testWorldPressed(new Point(0.71875, 3.90625));
		testWorldDragged(new Point(0.78125, 3.90625));
		testWorldDragged(new Point(1.03125, 3.90625));
		testWorldDragged(new Point(1.3125, 3.90625));
		testWorldDragged(new Point(1.65625, 3.90625));
		testWorldDragged(new Point(2.09375, 3.875));
		testWorldDragged(new Point(2.78125, 3.8125));
		testWorldDragged(new Point(3.71875, 3.71875));
		testWorldDragged(new Point(4.5625, 3.65625));
		testWorldDragged(new Point(5.25, 3.65625));
		testWorldDragged(new Point(5.9375, 3.65625));
		testWorldDragged(new Point(6.625, 3.53125));
		testWorldDragged(new Point(7.46875, 3.4375));
		testWorldDragged(new Point(8.15625, 3.375));
		testWorldDragged(new Point(8.84375, 3.375));
		testWorldDragged(new Point(9.375, 3.25));
		testWorldDragged(new Point(9.8125, 3.25));
		testWorldDragged(new Point(10.4063, 3.25));
		testWorldDragged(new Point(11., 3.25));
		testWorldDragged(new Point(11.5938, 3.25));
		testWorldDragged(new Point(12.0313, 3.25));
		testWorldDragged(new Point(12.4375, 3.25));
		testWorldDragged(new Point(12.6563, 3.25));
		testWorldDragged(new Point(12.9063, 3.25));
		testWorldDragged(new Point(13.0938, 3.25));
		testWorldDragged(new Point(13.2188, 3.25));
		testWorldDragged(new Point(13.4063, 3.25));
		testWorldDragged(new Point(13.5625, 3.25));
		testWorldDragged(new Point(13.6875, 3.3125));
		testWorldDragged(new Point(13.9375, 3.3125));
		testWorldDragged(new Point(14.25, 3.4375));
		testWorldDragged(new Point(14.4375, 3.46875));
		testWorldDragged(new Point(14.625, 3.46875));
		testWorldDragged(new Point(14.8125, 3.46875));
		testWorldDragged(new Point(14.9063, 3.53125));
		testWorldDragged(new Point(15.0313, 3.53125));
		testWorldDragged(new Point(15.125, 3.53125));
		testWorldDragged(new Point(15.2188, 3.53125));
		testWorldDragged(new Point(15.3125, 3.5625));
		testWorldDragged(new Point(15.3438, 3.5625));
		testWorldDragged(new Point(15.375, 3.59375));
		testWorldDragged(new Point(15.4063, 3.59375));
		testWorldDragged(new Point(15.5, 3.65625));
		testWorldDragged(new Point(15.5313, 3.65625));
		testWorldDragged(new Point(15.5625, 3.65625));
		testWorldReleased();

		testWorldPressed(new Point(8.625, 3.59375));
		testWorldDragged(new Point(8.625, 3.65625));
		testWorldDragged(new Point(8.625, 3.84375));
		testWorldDragged(new Point(8.625, 4.09375));
		testWorldDragged(new Point(8.625, 4.375));
		testWorldDragged(new Point(8.625, 4.8125));
		testWorldDragged(new Point(8.625, 5.40625));
		testWorldDragged(new Point(8.625, 6.0625));
		testWorldDragged(new Point(8.625, 6.75));
		testWorldDragged(new Point(8.625, 7.53125));
		testWorldDragged(new Point(8.625, 8.375));
		testWorldDragged(new Point(8.625, 9.03125));
		testWorldDragged(new Point(8.625, 9.46875));
		testWorldDragged(new Point(8.625, 9.75));
		testWorldDragged(new Point(8.625, 10.0938));
		testWorldDragged(new Point(8.625, 10.3438));
		testWorldDragged(new Point(8.53125, 10.875));
		testWorldDragged(new Point(8.53125, 11.375));
		testWorldDragged(new Point(8.53125, 11.8125));
		testWorldDragged(new Point(8.53125, 12.0938));
		testWorldDragged(new Point(8.53125, 12.3438));
		testWorldDragged(new Point(8.53125, 12.5938));
		testWorldDragged(new Point(8.53125, 12.875));
		testWorldDragged(new Point(8.53125, 13.0625));
		testWorldDragged(new Point(8.53125, 13.3125));
		testWorldDragged(new Point(8.53125, 13.5938));
		testWorldDragged(new Point(8.53125, 13.7813));
		testWorldDragged(new Point(8.53125, 13.9063));
		testWorldDragged(new Point(8.53125, 14.0938));
		testWorldDragged(new Point(8.53125, 14.25));
		testWorldDragged(new Point(8.53125, 14.3125));
		testWorldDragged(new Point(8.5, 14.4063));
		testWorldDragged(new Point(8.5, 14.5));
		testWorldDragged(new Point(8.5, 14.625));
		testWorldDragged(new Point(8.46875, 14.7188));
		testWorldDragged(new Point(8.46875, 14.8438));
		testWorldDragged(new Point(8.46875, 15.0313));
		testWorldDragged(new Point(8.40625, 15.1875));
		testWorldDragged(new Point(8.40625, 15.375));
		testWorldDragged(new Point(8.40625, 15.5625));
		testWorldDragged(new Point(8.40625, 15.6875));
		testWorldDragged(new Point(8.40625, 15.7813));
		testWorldDragged(new Point(8.40625, 15.8125));
		testWorldDragged(new Point(8.40625, 15.8438));
		testWorldDragged(new Point(8.40625, 15.9063));
		testWorldDragged(new Point(8.40625, 15.9375));
		testWorldReleased();

		testWorldPressed(new Point(8.0625, 3.8125));
		testWorldDragged(new Point(8.0625, 3.875));
		testWorldDragged(new Point(8., 4.));
		testWorldDragged(new Point(7.8125, 4.15625));
		testWorldDragged(new Point(7.5, 4.40625));
		testWorldDragged(new Point(7.09375, 4.75));
		testWorldDragged(new Point(6.6875, 5.21875));
		testWorldDragged(new Point(6.25, 5.9375));
		testWorldDragged(new Point(5.90625, 6.625));
		testWorldDragged(new Point(5.5, 7.1875));
		testWorldDragged(new Point(5.15625, 7.65625));
		testWorldDragged(new Point(4.84375, 8.0625));
		testWorldDragged(new Point(4.4375, 8.53125));
		testWorldDragged(new Point(4., 9.));
		testWorldDragged(new Point(3.6875, 9.46875));
		testWorldDragged(new Point(3.4375, 9.71875));
		testWorldDragged(new Point(3.34375, 9.8125));
		testWorldDragged(new Point(3.28125, 9.84375));
		testWorldDragged(new Point(3.28125, 9.875));
		testWorldDragged(new Point(3.21875, 9.90625));
		testWorldReleased();
		
		
	}
	
	
	@Test
	public void test8() throws Exception {
		
		testWorldPressed(new Point(0.875, 8.03125));
		testWorldDragged(new Point(1., 8.03125));
		testWorldDragged(new Point(1.125, 8.03125));
		testWorldDragged(new Point(1.3125, 8.03125));
		testWorldDragged(new Point(1.5, 8.03125));
		testWorldDragged(new Point(1.78125, 8.03125));
		testWorldDragged(new Point(2.03125, 8.03125));
		testWorldDragged(new Point(2.375, 8.03125));
		testWorldDragged(new Point(2.96875, 8.03125));
		testWorldDragged(new Point(3.5, 8.03125));
		testWorldDragged(new Point(3.84375, 8.03125));
		testWorldDragged(new Point(4.25, 8.03125));
		testWorldDragged(new Point(4.6875, 8.03125));
		testWorldDragged(new Point(5.03125, 8.03125));
		testWorldDragged(new Point(5.46875, 8.03125));
		testWorldDragged(new Point(6., 8.03125));
		testWorldDragged(new Point(6.34375, 8.03125));
		testWorldDragged(new Point(6.6875, 8.03125));
		testWorldDragged(new Point(7.03125, 8.03125));
		testWorldDragged(new Point(7.28125, 8.03125));
		testWorldDragged(new Point(7.46875, 8.03125));
		testWorldDragged(new Point(7.75, 8.03125));
		testWorldDragged(new Point(7.9375, 8.03125));
		testWorldDragged(new Point(8.125, 7.96875));
		testWorldDragged(new Point(8.4375, 7.84375));
		testWorldDragged(new Point(8.625, 7.8125));
		testWorldDragged(new Point(8.875, 7.8125));
		testWorldDragged(new Point(9.09375, 7.75));
		testWorldDragged(new Point(9.28125, 7.75));
		testWorldDragged(new Point(9.40625, 7.71875));
		testWorldDragged(new Point(9.59375, 7.71875));
		testWorldDragged(new Point(9.75, 7.625));
		testWorldDragged(new Point(9.9375, 7.625));
		testWorldDragged(new Point(10.0938, 7.625));
		testWorldDragged(new Point(10.2813, 7.625));
		testWorldDragged(new Point(10.4688, 7.625));
		testWorldDragged(new Point(10.5938, 7.625));
		testWorldDragged(new Point(10.7813, 7.625));
		testWorldDragged(new Point(10.9688, 7.625));
		testWorldDragged(new Point(11.125, 7.625));
		testWorldDragged(new Point(11.3125, 7.5625));
		testWorldDragged(new Point(11.4375, 7.5625));
		testWorldDragged(new Point(11.5938, 7.5625));
		testWorldDragged(new Point(11.7188, 7.59375));
		testWorldDragged(new Point(11.875, 7.59375));
		testWorldDragged(new Point(12., 7.625));
		testWorldDragged(new Point(12.1875, 7.625));
		testWorldDragged(new Point(12.3438, 7.6875));
		testWorldDragged(new Point(12.4688, 7.71875));
		testWorldDragged(new Point(12.6563, 7.71875));
		testWorldDragged(new Point(12.8125, 7.71875));
		testWorldDragged(new Point(13.0625, 7.71875));
		testWorldDragged(new Point(13.1875, 7.71875));
		testWorldDragged(new Point(13.375, 7.71875));
		testWorldDragged(new Point(13.5313, 7.71875));
		testWorldDragged(new Point(13.7188, 7.71875));
		testWorldDragged(new Point(13.8438, 7.71875));
		testWorldDragged(new Point(14., 7.71875));
		testWorldDragged(new Point(14.0625, 7.71875));
		testWorldDragged(new Point(14.2813, 7.78125));
		testWorldDragged(new Point(14.3438, 7.78125));
		testWorldDragged(new Point(14.5, 7.8125));
		testWorldDragged(new Point(14.625, 7.8125));
		testWorldDragged(new Point(14.7188, 7.875));
		testWorldDragged(new Point(14.8438, 7.875));
		testWorldDragged(new Point(14.9375, 7.90625));
		testWorldDragged(new Point(15.0625, 7.90625));
		testWorldDragged(new Point(15.1563, 7.90625));
		testWorldDragged(new Point(15.25, 7.90625));
		testWorldDragged(new Point(15.2813, 7.90625));
		testWorldDragged(new Point(15.3125, 7.90625));
		testWorldDragged(new Point(15.4063, 7.90625));
		testWorldReleased();

		testWorldPressed(new Point(8.03125, 3.46875));
		testWorldDragged(new Point(8.03125, 3.5));
		testWorldDragged(new Point(8.03125, 3.5625));
		testWorldDragged(new Point(8.03125, 3.59375));
		testWorldDragged(new Point(8.03125, 3.71875));
		testWorldDragged(new Point(8.03125, 4.03125));
		testWorldDragged(new Point(8.03125, 4.375));
		testWorldDragged(new Point(7.96875, 4.8125));
		testWorldDragged(new Point(7.90625, 5.1875));
		testWorldDragged(new Point(7.8125, 5.875));
		testWorldDragged(new Point(7.8125, 6.625));
		testWorldDragged(new Point(7.75, 7.21875));
		testWorldDragged(new Point(7.75, 7.59375));
		testWorldDragged(new Point(7.6875, 8.03125));
		testWorldDragged(new Point(7.6875, 8.6875));
		testWorldDragged(new Point(7.6875, 9.3125));
		testWorldDragged(new Point(7.6875, 9.71875));
		testWorldDragged(new Point(7.6875, 10.));
		testWorldDragged(new Point(7.625, 10.2813));
		testWorldDragged(new Point(7.625, 10.4688));
		testWorldDragged(new Point(7.625, 10.6563));
		testWorldDragged(new Point(7.625, 10.8438));
		testWorldDragged(new Point(7.625, 11.0313));
		testWorldDragged(new Point(7.625, 11.2813));
		testWorldDragged(new Point(7.59375, 11.5));
		testWorldDragged(new Point(7.59375, 11.6875));
		testWorldDragged(new Point(7.59375, 11.8125));
		testWorldDragged(new Point(7.59375, 11.9375));
		testWorldDragged(new Point(7.59375, 12.0313));
		testWorldDragged(new Point(7.59375, 12.0625));
		testWorldDragged(new Point(7.59375, 12.0938));
		testWorldDragged(new Point(7.59375, 12.1563));
		testWorldDragged(new Point(7.5625, 12.1875));
		testWorldDragged(new Point(7.5625, 12.2188));
		testWorldDragged(new Point(7.5625, 12.25));
		testWorldDragged(new Point(7.5625, 12.2813));
		testWorldDragged(new Point(7.5625, 12.3125));
		testWorldReleased();
		
	}
	
	
	
	@Test
	public void test9() throws Exception {
		
		testWorldPressed(new Point(15.9688, 0.4375));
		testWorldDragged(new Point(15.9688, 0.5625));
		testWorldDragged(new Point(15.9688, 0.6875));
		testWorldDragged(new Point(15.9688, 0.875));
		testWorldDragged(new Point(15.9688, 1.03125));
		testWorldDragged(new Point(15.9688, 1.15625));
		testWorldDragged(new Point(15.9688, 1.34375));
		testWorldDragged(new Point(15.9688, 1.53125));
		testWorldDragged(new Point(15.9688, 1.9375));
		testWorldDragged(new Point(15.9688, 2.4375));
		testWorldDragged(new Point(15.9688, 2.78125));
		testWorldDragged(new Point(15.9375, 3.21875));
		testWorldDragged(new Point(15.8125, 3.6875));
		testWorldDragged(new Point(15.6875, 4.46875));
		testWorldDragged(new Point(15.4688, 5.0625));
		testWorldDragged(new Point(15.3438, 5.4375));
		testWorldDragged(new Point(15.1563, 5.75));
		testWorldDragged(new Point(14.9375, 5.96875));
		testWorldDragged(new Point(14.625, 6.28125));
		testWorldDragged(new Point(14.2188, 6.625));
		testWorldDragged(new Point(13.75, 6.96875));
		testWorldDragged(new Point(13.2813, 7.5));
		testWorldDragged(new Point(12.875, 8.));
		testWorldDragged(new Point(12.4688, 8.3125));
		testWorldDragged(new Point(12.0938, 8.5));
		testWorldDragged(new Point(11.4688, 8.71875));
		testWorldDragged(new Point(10.625, 8.78125));
		testWorldDragged(new Point(9.6875, 8.84375));
		testWorldDragged(new Point(8.84375, 8.84375));
		testWorldDragged(new Point(8.15625, 8.84375));
		testWorldDragged(new Point(7.21875, 8.84375));
		testWorldDragged(new Point(6.15625, 8.71875));
		testWorldDragged(new Point(5.15625, 8.59375));
		testWorldDragged(new Point(4.5625, 8.4375));
		testWorldDragged(new Point(4.09375, 8.3125));
		testWorldDragged(new Point(3.65625, 8.1875));
		testWorldDragged(new Point(3.375, 8.0625));
		testWorldDragged(new Point(3.03125, 8.0625));
		testWorldDragged(new Point(2.65625, 8.));
		testWorldDragged(new Point(2.3125, 7.9375));
		testWorldDragged(new Point(1.9375, 7.84375));
		testWorldDragged(new Point(1.65625, 7.71875));
		testWorldDragged(new Point(1.5, 7.625));
		testWorldDragged(new Point(1.34375, 7.5625));
		testWorldDragged(new Point(1.28125, 7.53125));
		testWorldDragged(new Point(1.21875, 7.4375));
		testWorldDragged(new Point(1.15625, 7.4375));
		testWorldDragged(new Point(1.09375, 7.4375));
		testWorldDragged(new Point(1.0625, 7.4375));
		testWorldDragged(new Point(1.0625, 7.40625));
		testWorldDragged(new Point(1.03125, 7.40625));
		testWorldDragged(new Point(1., 7.40625));
		testWorldDragged(new Point(0.96875, 7.40625));
		testWorldDragged(new Point(0.9375, 7.40625));
		testWorldReleased();

		testWorldPressed(new Point(11.1563, 8.71875));
		testWorldDragged(new Point(11.1875, 8.71875));
		testWorldDragged(new Point(11.25, 8.8125));
		testWorldDragged(new Point(11.4063, 9.03125));
		testWorldDragged(new Point(11.6563, 9.25));
		testWorldDragged(new Point(11.9688, 9.5));
		testWorldDragged(new Point(12.25, 9.75));
		testWorldDragged(new Point(12.6563, 10.0625));
		testWorldDragged(new Point(13.0625, 10.4688));
		testWorldDragged(new Point(13.7188, 11.2188));
		testWorldDragged(new Point(14.8125, 12.0313));
		testWorldDragged(new Point(15.9688, 12.6563));
		testWorldDragged(new Point(16.7813, 13.0313));
		testWorldDragged(new Point(17.4688, 13.3125));
		testWorldDragged(new Point(18.1875, 13.5313));
		testWorldDragged(new Point(18.9688, 13.7188));
		testWorldDragged(new Point(19.9375, 13.875));
		testWorldDragged(new Point(20.6875, 14.0313));
		testWorldDragged(new Point(21.4063, 14.1563));
		testWorldDragged(new Point(21.9063, 14.1563));
		testWorldDragged(new Point(22.3438, 14.1563));
		testWorldDragged(new Point(22.7813, 14.0625));
		testWorldDragged(new Point(23.25, 13.9375));
		testWorldDragged(new Point(23.6875, 13.75));
		testWorldDragged(new Point(24.1563, 13.5313));
		testWorldDragged(new Point(24.4688, 13.2188));
		testWorldReleased();
		
	}
	
	
}
