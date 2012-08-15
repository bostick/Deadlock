package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.controller.MassageStrategy;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;


public class TestSimulating {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		CONTROLLER.init();
		MODEL.init();
		
		VIEW.frame.setVisible(true);
		VIEW.panel.requestFocusInWindow();
		
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
		
		assertTrue(MODEL.checkConsistency());
		
		Thread.sleep(200);
		
		MODEL.clear();
	}
	
	Runnable empty = new Runnable(){
		@Override
		public void run() {
			;
		}};
	
	public void testPressed(Point p) throws Exception {
		CONTROLLER.mc.pressed(p);
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testDragged(Point p) throws Exception {
		CONTROLLER.mc.dragged(p);
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testReleased() throws Exception {
		CONTROLLER.mc.released();
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	
	List<Edge> edges;
	
	@Test
	public void test1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.NONE;
		
		MODEL.RANDOM.setSeed(1);
		
		testPressed(new Point(9., 251.));
		testReleased();
		
		testPressed(new Point(4., 324.));
		testDragged(new Point(7., 324.));
		testDragged(new Point(11., 323.));
		testDragged(new Point(22., 319.));
		testDragged(new Point(61., 311.));
		testDragged(new Point(127., 298.));
		testDragged(new Point(245., 290.));
		testDragged(new Point(367., 290.));
		testDragged(new Point(512., 301.));
		testDragged(new Point(642., 318.));
		testDragged(new Point(756., 334.));
		testDragged(new Point(831., 345.));
		testReleased();

		testPressed(new Point(831., 345.));
		testDragged(new Point(845., 345.));
		testDragged(new Point(861., 345.));
		testDragged(new Point(898., 345.));
		testDragged(new Point(955., 348.));
		testDragged(new Point(1040., 352.));
		testDragged(new Point(1141., 360.));
		testDragged(new Point(1226., 367.));
		testDragged(new Point(1307., 378.));
		testDragged(new Point(1361., 388.));
		testDragged(new Point(1386., 397.));
		testDragged(new Point(1395., 399.));
		testReleased();

		testPressed(new Point(1395., 399.));
		testReleased();

		testPressed(new Point(665., 9.));
		testDragged(new Point(665., 20.));
		testDragged(new Point(665., 37.));
		testDragged(new Point(665., 59.));
		testDragged(new Point(665., 88.));
		testDragged(new Point(665., 113.));
		testDragged(new Point(665., 143.));
		testDragged(new Point(665., 184.));
		testDragged(new Point(665., 218.));
		testDragged(new Point(665., 245.));
		testDragged(new Point(665., 286.));
		testDragged(new Point(665., 320.));
		testDragged(new Point(663., 344.));
		testDragged(new Point(663., 378.));
		testDragged(new Point(663., 422.));
		testDragged(new Point(663., 449.));
		testDragged(new Point(663., 474.));
		testDragged(new Point(663., 507.));
		testDragged(new Point(667., 534.));
		testDragged(new Point(669., 551.));
		testDragged(new Point(671., 565.));
		testDragged(new Point(671., 570.));
		testDragged(new Point(671., 572.));
		testReleased();

		testPressed(new Point(671., 572.));
		testDragged(new Point(671., 573.));
		testDragged(new Point(671., 577.));
		testDragged(new Point(671., 582.));
		testDragged(new Point(671., 588.));
		testDragged(new Point(673., 601.));
		testDragged(new Point(675., 620.));
		testDragged(new Point(679., 637.));
		testDragged(new Point(683., 651.));
		testDragged(new Point(687., 663.));
		testDragged(new Point(689., 682.));
		testDragged(new Point(691., 702.));
		testDragged(new Point(693., 716.));
		testDragged(new Point(693., 727.));
		testDragged(new Point(695., 736.));
		testDragged(new Point(695., 742.));
		testDragged(new Point(697., 750.));
		testDragged(new Point(699., 757.));
		testDragged(new Point(699., 765.));
		testDragged(new Point(700., 777.));
		testDragged(new Point(700., 785.));
		testDragged(new Point(700., 790.));
		testDragged(new Point(702., 794.));
		testDragged(new Point(702., 797.));
		testDragged(new Point(702., 798.));
		testDragged(new Point(702., 799.));
		testDragged(new Point(702., 800.));
		testDragged(new Point(703., 801.));
		testDragged(new Point(703., 802.));
		testDragged(new Point(703., 804.));
		testDragged(new Point(703., 808.));
		testDragged(new Point(703., 810.));
		testDragged(new Point(703., 813.));
		testDragged(new Point(703., 815.));
		testDragged(new Point(703., 817.));
		testDragged(new Point(703., 818.));
		testReleased();
		
		//CONTROLLER.startRunning();
//		CONTROLLER.queue(new Runnable(){
//			@Override
//			public void run() {
//				CONTROLLER.startRunning();
//			}}
//		);
		VIEW.controlPanel.simulationButton.doClick();
		
		String.class.getName();
		
	}
	
}
