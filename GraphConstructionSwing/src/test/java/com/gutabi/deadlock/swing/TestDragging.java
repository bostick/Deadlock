package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;
import static com.gutabi.deadlock.swing.Main.PLATFORMCONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.core.DPoint;
import com.gutabi.core.Edge;
import com.gutabi.core.Point;
import com.gutabi.core.QuadTree.SegmentIndex;
import com.gutabi.deadlock.core.controller.DeadlockController;
import com.gutabi.deadlock.core.controller.MassageStrategy;
import com.gutabi.deadlock.core.view.DeadlockView;
import com.gutabi.deadlock.swing.controller.PlatformController;
import com.gutabi.deadlock.swing.view.PlatformView;
import com.gutabi.deadlock.swing.view.SwingWindowInfo;

public class TestDragging {
	
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
		
		assertTrue(MODEL.checkConsistency());
		
		MODEL.clear();
	}
	
	
	List<Edge> edges;
	
	@Test
	public void test1() throws Exception {
		
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(7, 7));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.getEdges();
			}
		});
		
		assertEquals(edges.size(), 1);
		Edge e = edges.get(0);
		assertEquals(e.size(), 2);
		assertTrue(Point.equals(new Point(5, 5), e.getPoint(0)));
		assertTrue(Point.equals(new Point(7, 7), e.getPoint(1)));
		
	}
	
	@Test
	public void test2() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(4, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.getEdges();
			}
		});
		 
		assertEquals(edges.size(),  1);
		Edge e = edges.get(0);
		assertTrue(Point.equals(new Point(6, 6), e.getStart().getPoint()));
		assertTrue(Point.equals(new Point(4, 4), e.getEnd().getPoint()));
		
	}
	
	@Test
	public void test3() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(267, 581));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(267, 580));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(267, 582));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test4() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(2, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 1));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 3));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test5() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(532, 627));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(515, 620));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(500, 612));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(480, 598));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(443, 565));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(415, 534));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(397, 513));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(383, 493));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(377, 473));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 446));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 396));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 380));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(373, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(380, 343));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(390, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(403, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(420, 314));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(434, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(449, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(460, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(471, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(498, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(518, 309));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(540, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(556, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(571, 344));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(582, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(592, 367));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(597, 374));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(604, 382));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(607, 386));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(610, 389));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(614, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(616, 412));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 453));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 472));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(617, 486));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(613, 506));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(606, 533));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(599, 556));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(590, 571));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(580, 583));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(565, 599));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(542, 615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(521, 635));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test6() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(7, 7));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(4, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test7() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(7, 7));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(5, 5));
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(4, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(267, 581));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(267, 580));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(267, 582));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(2, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 1));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 3));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(532, 627));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(515, 620));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(500, 612));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(480, 598));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(443, 565));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(415, 534));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(397, 513));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(383, 493));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(377, 473));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 446));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 396));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 380));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(373, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(380, 343));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(390, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(403, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(420, 314));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(434, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(449, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(460, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(471, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(498, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(518, 309));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(540, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(556, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(571, 344));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(582, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(592, 367));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(597, 374));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(604, 382));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(607, 386));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(610, 389));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(614, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(616, 412));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 453));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 472));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(617, 486));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(613, 506));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(606, 533));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(599, 556));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(590, 571));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(580, 583));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(565, 599));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(542, 615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(521, 635));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(841, 101));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(838, 102));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(830, 104));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(823, 105));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(814, 109));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(804, 115));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(798, 118));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(792, 125));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(787, 135));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(779, 145));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(772, 150));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(762, 158));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(752, 168));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(742, 178));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(730, 201));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(715, 241));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(711, 255));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(705, 276));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(705, 287));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(703, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(703, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(703, 328));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(703, 335));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(707, 345));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(711, 348));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(718, 352));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(729, 356));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(739, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(748, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(755, 366));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(761, 368));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(767, 370));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(770, 371));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(773, 372));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(774, 372));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(775, 372));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(775, 335));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(774, 333));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(771, 333));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(767, 332));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(760, 330));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(754, 329));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(748, 327));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(743, 324));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(737, 323));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(732, 319));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(728, 318));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(723, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(717, 315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(708, 313));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(699, 311));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(690, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(684, 308));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(673, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(670, 303));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(666, 302));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(661, 302));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(659, 301));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(656, 299));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(653, 298));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(650, 296));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(649, 295));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(647, 294));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(645, 293));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(643, 293));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(643, 292));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(642, 292));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(643, 292));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test8() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(532, 627));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(515, 620));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(500, 612));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(480, 598));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(443, 565));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(415, 534));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(397, 513));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(383, 493));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(377, 473));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 446));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 396));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(372, 380));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(373, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(380, 343));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(390, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(403, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(420, 314));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(434, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(449, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(460, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(471, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(498, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(518, 309));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(540, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(556, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(571, 344));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(582, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(592, 367));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(597, 374));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(604, 382));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(607, 386));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(610, 389));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(614, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(616, 412));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 453));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 472));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(617, 486));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(613, 506));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(606, 533));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(599, 556));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(590, 571));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(580, 583));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(565, 599));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(542, 615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(521, 635));
		PLATFORMCONTROLLER.mc.released_M();
		
		// break the loop
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(619, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(580, 423));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test9() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(0, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(150, 450));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(200, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(0, 50));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testLoop() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(2, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 1));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(3, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(2, 0));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testLoop2() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(2, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(12, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(2, 5));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(3, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(3, 6));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testIntersection() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(4, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(10, 12));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(0, 12));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(7, 7));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(16, 1));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug1() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(532, 419));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(583, 442));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(583, 447));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(584, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(584, 449));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(584, 450));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(585, 450));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(555, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(556, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(579, 444));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(580, 444));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(580, 444));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(581, 444));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(592, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(593, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(594, 448));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(594, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(597, 451));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(598, 454));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(603, 455));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(604, 456));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(607, 458));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(608, 458));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(608, 458));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(608, 459));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(607, 459));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(592, 463));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(587, 463));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(583, 463));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(639, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(637, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(631, 420));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(631, 420));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(631, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(630, 422));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(629, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(623, 424));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(623, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(621, 421));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(619, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(618, 419));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(618, 419));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(626, 419));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(627, 418));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(629, 417));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(629, 417));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(632, 417));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(640, 415));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testEdgeCountNotEqual2Bug2() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(49, 100));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(44, 25));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(0, 13));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(54, 42));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(54, 42));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(44, 25));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug2() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(84, 86));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(33, 97));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(52, 8));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(30, 65));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(19, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(77, 95));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(30, 64));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(51, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(34, 74));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(29, 90));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(56, 85));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(58, 29));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(31, 45));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(44, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(50, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(40, 39));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(50, 29));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(73, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(43, 96));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(46, 49));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(36, 4));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(52, 66));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(73, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(97, 69));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(5, 66));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(44, 89));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(82, 80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95, 89));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(31, 77));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(45, 98));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(5, 19));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug1() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(84, 86));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(33, 97));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(73, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(43, 96));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(43, 96));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(46, 49));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug2() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(60, 78));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(1, 72));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(47, 60));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(7, 17));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(2, 78));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testXBCEqual0Bug() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(45, 47));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(7, 41));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(7, 41));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(45, 81));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(13, 56));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(4, 17));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(25, 62));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(3, 34));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug3() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(71, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(6, 91));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(76, 24));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(91, 98));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(3, 84));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testYBCEqual0Bug() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(13, 47));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(53, 43));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(17, 38));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(36, 59));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100, 27));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(0, 59));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(37, 36));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(19, 4));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(26, 80));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug4() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(23, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(18, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 9));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 9));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(44, 94));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testInconsistent1() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(23, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(18, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(18, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 10));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 9));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 9));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(44, 94));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testInconsistent2() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(18, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(18, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 10));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(21, 9));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	
	@Test
	public void testRemoved() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(703, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(703, 335));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(754, 329));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(699, 311));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAssertFalse() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(703, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(703, 320));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(717, 315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(684, 308));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testAssertFalse2() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(583, 442));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(583, 447));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(584, 449));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(584, 450));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(583, 442));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug3() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(84, 86));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(33, 97));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(52, 8));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(30, 65));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(19, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(77, 95));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(30, 64));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(29, 90));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(56, 85));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testCAndDAreEqualBug() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(30, 65));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(19, 22));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(11, 22));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(56, 85));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testNoEdgesShouldIntersectBug1() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(73, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(43, 96));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(46, 49));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(45, 98));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(5, 19));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug1() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(52, 8));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(30, 65));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(51, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(34, 74));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(11, 22));
		PLATFORMCONTROLLER.mc.released_M();
		
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(56, 85));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug2() throws Exception {
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {

				PLATFORMCONTROLLER.mc.pressed(new DPoint(42, 45));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(43, 42));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new DPoint(44, 44));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(44, 37));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new DPoint(30, 23));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(63, 73));
				PLATFORMCONTROLLER.mc.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug3() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(40, 19));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(43, 61));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(99, 95));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(38, 31));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(40, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(82, 8));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug4() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(351, 140));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(454, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(454, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(436, 146));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(389, 36));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(462, 273));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(216, 161));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(491, 36));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug5() throws Exception {
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				
				PLATFORMCONTROLLER.mc.pressed(new DPoint(463, 221));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(25, 28));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new DPoint(274, 24));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(34, 38));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new DPoint(34, 38));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(428, 78));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new DPoint(46, 40));
				PLATFORMCONTROLLER.mc.dragged(new DPoint(364, 52));
				PLATFORMCONTROLLER.mc.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug6() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(390, 91));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(32, 324));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(19, 246));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(116, 147));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(185, 261));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(211, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(118, 34));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(100, 216));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(262, 444));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAndroidBug() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(135,455));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(135,455));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(130,445));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(130,440));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(130,440));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(130,435));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,430));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,405));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,400));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,390));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,385));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,380));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,375));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,370));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,360));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(130,350));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(135,330));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(135,320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(140,305));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(140,295));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(150,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(155,270));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(160,260));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(165,250));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(170,245));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(180,240));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(185,235));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(195,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(195,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(200,220));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(205,220));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(215,215));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(215,215));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(220,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(220,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(230,205));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(110,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(170,650));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(180,650));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(200,645));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(230,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,635));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,640));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(260,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(270,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,635));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(290,630));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(305,630));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(310,625));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,625));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,620));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(335,615));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(230,555));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,545));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,530));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,515));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,500));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,485));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(255,445));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(255,445));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(275,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(290,415));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(295,410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(300,410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(300,410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(305,410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(305,410));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,405));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,390));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(325,380));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(325,370));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,350));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(335,315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(335,310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(335,305));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(340,285));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(340,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(350,260));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(360,250));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(360,240));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(365,235));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(365,235));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,180));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,180));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,165));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,150));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(260,130));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,125));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,125));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,110));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,110));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,110));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(285,105));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(305,90));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(305,90));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(310,85));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,85));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,85));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(190,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(190,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(205,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(210,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(215,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(220,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(275,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(285,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(290,75));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(295,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(300,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(310,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(310,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,85));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,85));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,85));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(385,220));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(385,220));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(385,220));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(380,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(375,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(370,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(365,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(360,225));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(355,230));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(345,230));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,235));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,240));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,240));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(310,245));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(300,245));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(295,250));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,255));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(270,260));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,265));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,265));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(260,265));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(255,270));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,270));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,275));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,275));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,275));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,275));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,285));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(140,460));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(140,460));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(150,450));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(150,440));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(160,435));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(170,420));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(180,405));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(185,395));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(190,390));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(190,385));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(195,380));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(200,375));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(200,370));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(200,365));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(205,365));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(205,360));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(210,355));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(215,350));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(220,345));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(225,345));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(230,340));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,330));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(235,325));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(240,320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,305));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,305));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(195,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(195,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(195,80));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(180,95));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(170,100));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(155,115));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(145,125));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(135,135));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,145));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(120,155));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(115,160));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(110,165));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(110,170));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(105,185));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100,190));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100,195));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,200));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,205));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,210));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,220));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(85,240));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(80,245));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(80,250));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,265));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,275));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,285));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(70,305));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(70,315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(70,325));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(65,350));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(65,360));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(65,370));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,390));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,400));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,410));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,415));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,425));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(55,435));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(55,445));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(55,455));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(55,465));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,475));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,485));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,495));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,505));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(65,515));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(70,520));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(70,530));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,535));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,535));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(80,540));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(80,540));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(85,545));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(85,545));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(85,550));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,550));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,550));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(100,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100,655));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100,645));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100,640));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,630));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,620));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,615));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,610));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,595));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,590));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,585));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(95,580));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,580));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,575));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,575));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,575));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,570));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(245,565));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,565));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(250,570));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,575));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,575));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(275,575));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(280,580));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(290,585));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(300,590));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(305,590));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(310,590));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,595));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(315,595));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(325,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(325,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(325,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,600));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(330,605));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(390,510));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(385,500));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(380,495));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(375,495));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(355,475));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(335,450));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(320,445));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(300,425));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(275,415));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(265,405));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(260,400));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(245,390));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(230,385));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(210,370));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(195,365));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(180,360));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(170,355));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(160,345));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(140,340));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(125,325));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(115,320));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(105,315));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(100,310));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(90,305));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,300));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(75,300));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(65,295));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(65,295));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,290));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,290));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(60,290));
		PLATFORMCONTROLLER.mc.released_M();


		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug7() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(661, 568));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(664, 568));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(664, 567));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(665, 567));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(665, 567));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(661, 570));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				PLATFORMCONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testBug8() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(56, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(57, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(58, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(54, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(54, 12));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(54, 12));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(58, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				PLATFORMCONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testBug9() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(775, 457));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(772, 467));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(772, 469));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(773, 465));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(773, 465));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(775, 457));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				PLATFORMCONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testBug10() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(775, 457));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(772, 467));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(772, 469));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(775, 457));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(778, 449));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(798, 448));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(798, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(760, 460));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				PLATFORMCONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testEdgeDetection() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(2, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(12, 5));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
		Point a = new Point(3, 5);
		List<SegmentIndex> in = MODEL.findAllSegments(a);
		
		assertEquals(1, in.size());
		
		assertTrue(MODEL.checkConsistency());
	}
	
	@Test
	public void testCatastrophicAdjusting() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(934., 578.));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(723., 554.));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(830., 561.));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new DPoint(830., 561.));
		PLATFORMCONTROLLER.mc.dragged_M(new DPoint(707., 553.));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
}
