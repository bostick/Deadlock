package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import static org.junit.Assert.assertEquals;
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
import com.gutabi.deadlock.core.Segment;


public class TestDragging {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
//		VIEW = new DeadlockView();
//		CONTROLLER = new DeadlockController();
		
//		VIEW.window = new Window();
//		VIEW.logger = new Logger(VIEW.getClass());
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
		
		testPressed(new Point(5, 5));
		testDragged(new Point(6, 6));
		testDragged(new Point(7, 7));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
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
		
		CONTROLLER.strat = MassageStrategy.NONE;
		
		testPressed(new Point(5, 5));
		testDragged(new Point(6, 6));
		testDragged(new Point(4, 4));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
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
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(267, 581));
		testDragged(new Point(267, 580));
		testDragged(new Point(267, 582));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test4() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(2, 2));
		testDragged(new Point(1, 2));
		testDragged(new Point(1, 1));
		testDragged(new Point(1, 3));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test5() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(532, 627));
		testDragged(new Point(515, 620));
		testDragged(new Point(500, 612));
		testDragged(new Point(480, 598));
		testDragged(new Point(443, 565));
		testDragged(new Point(415, 534));
		testDragged(new Point(397, 513));
		testDragged(new Point(383, 493));
		testDragged(new Point(377, 473));
		testDragged(new Point(372, 446));
		testDragged(new Point(372, 424));
		testDragged(new Point(372, 410));
		testDragged(new Point(372, 396));
		testDragged(new Point(372, 380));
		testDragged(new Point(373, 363));
		testDragged(new Point(380, 343));
		testDragged(new Point(390, 331));
		testDragged(new Point(403, 320));
		testDragged(new Point(420, 314));
		testDragged(new Point(434, 310));
		testDragged(new Point(449, 306));
		testDragged(new Point(460, 306));
		testDragged(new Point(471, 306));
		testDragged(new Point(498, 306));
		testDragged(new Point(518, 309));
		testDragged(new Point(540, 316));
		testDragged(new Point(556, 331));
		testDragged(new Point(571, 344));
		testDragged(new Point(582, 359));
		testDragged(new Point(592, 367));
		testDragged(new Point(597, 374));
		testDragged(new Point(604, 382));
		testDragged(new Point(607, 386));
		testDragged(new Point(610, 389));
		testDragged(new Point(614, 398));
		testDragged(new Point(616, 412));
		testDragged(new Point(618, 453));
		testDragged(new Point(618, 472));
		testDragged(new Point(617, 486));
		testDragged(new Point(613, 506));
		testDragged(new Point(606, 533));
		testDragged(new Point(599, 556));
		testDragged(new Point(590, 571));
		testDragged(new Point(580, 583));
		testDragged(new Point(565, 599));
		testDragged(new Point(542, 615));
		testDragged(new Point(521, 635));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test6() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(5, 5));
		testDragged(new Point(7, 7));
		testReleased();
		
		testPressed(new Point(6, 6));
		testDragged(new Point(4, 4));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test7() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(5, 5));
		testDragged(new Point(6, 6));
		testDragged(new Point(7, 7));
		testReleased();
		
		testPressed(new Point(5, 5));
		testPressed(new Point(6, 6));
		testDragged(new Point(4, 4));
		testReleased();
		
		testPressed(new Point(267, 581));
		testDragged(new Point(267, 580));
		testDragged(new Point(267, 582));
		testReleased();
		
		testPressed(new Point(2, 2));
		testDragged(new Point(1, 2));
		testDragged(new Point(1, 1));
		testDragged(new Point(1, 3));
		testReleased();
		
		testPressed(new Point(532, 627));
		testDragged(new Point(515, 620));
		testDragged(new Point(500, 612));
		testDragged(new Point(480, 598));
		testDragged(new Point(443, 565));
		testDragged(new Point(415, 534));
		testDragged(new Point(397, 513));
		testDragged(new Point(383, 493));
		testDragged(new Point(377, 473));
		testDragged(new Point(372, 446));
		testDragged(new Point(372, 424));
		testDragged(new Point(372, 410));
		testDragged(new Point(372, 396));
		testDragged(new Point(372, 380));
		testDragged(new Point(373, 363));
		testDragged(new Point(380, 343));
		testDragged(new Point(390, 331));
		testDragged(new Point(403, 320));
		testDragged(new Point(420, 314));
		testDragged(new Point(434, 310));
		testDragged(new Point(449, 306));
		testDragged(new Point(460, 306));
		testDragged(new Point(471, 306));
		testDragged(new Point(498, 306));
		testDragged(new Point(518, 309));
		testDragged(new Point(540, 316));
		testDragged(new Point(556, 331));
		testDragged(new Point(571, 344));
		testDragged(new Point(582, 359));
		testDragged(new Point(592, 367));
		testDragged(new Point(597, 374));
		testDragged(new Point(604, 382));
		testDragged(new Point(607, 386));
		testDragged(new Point(610, 389));
		testDragged(new Point(614, 398));
		testDragged(new Point(616, 412));
		testDragged(new Point(618, 453));
		testDragged(new Point(618, 472));
		testDragged(new Point(617, 486));
		testDragged(new Point(613, 506));
		testDragged(new Point(606, 533));
		testDragged(new Point(599, 556));
		testDragged(new Point(590, 571));
		testDragged(new Point(580, 583));
		testDragged(new Point(565, 599));
		testDragged(new Point(542, 615));
		testDragged(new Point(521, 635));
		testReleased();
		
		testPressed(new Point(841, 101));
		testDragged(new Point(838, 102));
		testDragged(new Point(830, 104));
		testDragged(new Point(823, 105));
		testDragged(new Point(814, 109));
		testDragged(new Point(804, 115));
		testDragged(new Point(798, 118));
		testDragged(new Point(792, 125));
		testDragged(new Point(787, 135));
		testDragged(new Point(779, 145));
		testDragged(new Point(772, 150));
		testDragged(new Point(762, 158));
		testDragged(new Point(752, 168));
		testDragged(new Point(742, 178));
		testDragged(new Point(730, 201));
		testDragged(new Point(715, 241));
		testDragged(new Point(711, 255));
		testDragged(new Point(705, 276));
		testDragged(new Point(705, 287));
		testDragged(new Point(703, 306));
		testDragged(new Point(703, 320));
		testDragged(new Point(703, 328));
		testDragged(new Point(703, 335));
		testDragged(new Point(707, 345));
		testDragged(new Point(711, 348));
		testDragged(new Point(718, 352));
		testDragged(new Point(729, 356));
		testDragged(new Point(739, 359));
		testDragged(new Point(748, 363));
		testDragged(new Point(755, 366));
		testDragged(new Point(761, 368));
		testDragged(new Point(767, 370));
		testDragged(new Point(770, 371));
		testDragged(new Point(773, 372));
		testDragged(new Point(774, 372));
		testDragged(new Point(775, 372));
		testReleased();
		
		testPressed(new Point(775, 335));
		testDragged(new Point(774, 333));
		testDragged(new Point(771, 333));
		testDragged(new Point(767, 332));
		testDragged(new Point(760, 330));
		testDragged(new Point(754, 329));
		testDragged(new Point(748, 327));
		testDragged(new Point(743, 324));
		testDragged(new Point(737, 323));
		testDragged(new Point(732, 319));
		testDragged(new Point(728, 318));
		testDragged(new Point(723, 316));
		testDragged(new Point(717, 315));
		testDragged(new Point(708, 313));
		testDragged(new Point(699, 311));
		testDragged(new Point(690, 310));
		testDragged(new Point(684, 308));
		testDragged(new Point(673, 306));
		testDragged(new Point(670, 303));
		testDragged(new Point(666, 302));
		testDragged(new Point(661, 302));
		testDragged(new Point(659, 301));
		testDragged(new Point(656, 299));
		testDragged(new Point(653, 298));
		testDragged(new Point(650, 296));
		testDragged(new Point(649, 295));
		testDragged(new Point(647, 294));
		testDragged(new Point(645, 293));
		testDragged(new Point(643, 293));
		testDragged(new Point(643, 292));
		testDragged(new Point(642, 292));
		testDragged(new Point(643, 292));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test8() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(532, 627));
		testDragged(new Point(515, 620));
		testDragged(new Point(500, 612));
		testDragged(new Point(480, 598));
		testDragged(new Point(443, 565));
		testDragged(new Point(415, 534));
		testDragged(new Point(397, 513));
		testDragged(new Point(383, 493));
		testDragged(new Point(377, 473));
		testDragged(new Point(372, 446));
		testDragged(new Point(372, 424));
		testDragged(new Point(372, 410));
		testDragged(new Point(372, 396));
		testDragged(new Point(372, 380));
		testDragged(new Point(373, 363));
		testDragged(new Point(380, 343));
		testDragged(new Point(390, 331));
		testDragged(new Point(403, 320));
		testDragged(new Point(420, 314));
		testDragged(new Point(434, 310));
		testDragged(new Point(449, 306));
		testDragged(new Point(460, 306));
		testDragged(new Point(471, 306));
		testDragged(new Point(498, 306));
		testDragged(new Point(518, 309));
		testDragged(new Point(540, 316));
		testDragged(new Point(556, 331));
		testDragged(new Point(571, 344));
		testDragged(new Point(582, 359));
		testDragged(new Point(592, 367));
		testDragged(new Point(597, 374));
		testDragged(new Point(604, 382));
		testDragged(new Point(607, 386));
		testDragged(new Point(610, 389));
		testDragged(new Point(614, 398));
		testDragged(new Point(616, 412));
		testDragged(new Point(618, 453));
		testDragged(new Point(618, 472));
		testDragged(new Point(617, 486));
		testDragged(new Point(613, 506));
		testDragged(new Point(606, 533));
		testDragged(new Point(599, 556));
		testDragged(new Point(590, 571));
		testDragged(new Point(580, 583));
		testDragged(new Point(565, 599));
		testDragged(new Point(542, 615));
		testDragged(new Point(521, 635));
		testReleased();
		
		// break the loop
		testPressed(new Point(619, 448));
		testDragged(new Point(580, 423));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test9() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(0, 0));
		testDragged(new Point(150, 450));
		testReleased();
		
		testPressed(new Point(200, 0));
		testDragged(new Point(0, 50));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testLoop() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(2, 0));
		testDragged(new Point(1, 0));
		testDragged(new Point(1, 1));
		testDragged(new Point(3, 0));
		testDragged(new Point(2, 0));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testLoop2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(2, 5));
		testDragged(new Point(1, 5));
		testDragged(new Point(1, 10));
		testDragged(new Point(12, 5));
		testDragged(new Point(2, 5));
		testReleased();
		
		testPressed(new Point(3, 0));
		testDragged(new Point(3, 6));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testIntersection() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(4, 0));
		testDragged(new Point(10, 12));
		testReleased();

		testPressed(new Point(0, 12));
		testDragged(new Point(7, 7));
		testDragged(new Point(16, 1));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(532, 419));
		testDragged(new Point(583, 442));
		testDragged(new Point(583, 447));
		testDragged(new Point(584, 448));
		testDragged(new Point(584, 449));
		testDragged(new Point(584, 450));
		testDragged(new Point(585, 450));
		testReleased();

		testPressed(new Point(555, 448));
		testDragged(new Point(556, 448));
		testDragged(new Point(579, 444));
		testDragged(new Point(580, 444));
		testReleased();

		testPressed(new Point(580, 444));
		testDragged(new Point(581, 444));
		testDragged(new Point(592, 448));
		testDragged(new Point(593, 448));
		testDragged(new Point(594, 448));
		testReleased();

		testPressed(new Point(594, 448));
		testDragged(new Point(597, 451));
		testDragged(new Point(598, 454));
		testDragged(new Point(603, 455));
		testDragged(new Point(604, 456));
		testDragged(new Point(607, 458));
		testDragged(new Point(608, 458));
		testReleased();

		testPressed(new Point(608, 458));
		testDragged(new Point(608, 459));
		testDragged(new Point(607, 459));
		testDragged(new Point(592, 463));
		testDragged(new Point(587, 463));
		testDragged(new Point(583, 463));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(639, 420));
		testDragged(new Point(637, 420));
		testDragged(new Point(631, 420));
		testReleased();

		testPressed(new Point(631, 420));
		testReleased();

		testPressed(new Point(631, 420));
		testDragged(new Point(630, 422));
		testDragged(new Point(629, 424));
		testDragged(new Point(623, 424));
		testReleased();

		testPressed(new Point(623, 424));
		testDragged(new Point(621, 421));
		testDragged(new Point(619, 420));
		testDragged(new Point(618, 420));
		testDragged(new Point(618, 419));
		testReleased();

		testPressed(new Point(618, 419));
		testDragged(new Point(626, 419));
		testDragged(new Point(627, 418));
		testDragged(new Point(629, 417));
		testReleased();

		testPressed(new Point(629, 417));
		testDragged(new Point(632, 417));
		testDragged(new Point(640, 415));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testEdgeCountNotEqual2Bug2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(49, 100));
		testDragged(new Point(44, 25));
		testReleased();

		testPressed(new Point(0, 13));
		testDragged(new Point(54, 42));
		testReleased();
		
		testPressed(new Point(54, 42));
		testDragged(new Point(44, 25));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(84, 86));
		testDragged(new Point(33, 97));
		testReleased();
		
		testPressed(new Point(52, 8));
		testDragged(new Point(30, 65));
		testDragged(new Point(19, 22));
		testDragged(new Point(77, 95));
		testDragged(new Point(30, 64));
		testReleased();

		testPressed(new Point(51, 35));
		testDragged(new Point(34, 74));
		testDragged(new Point(29, 90));
		testDragged(new Point(66, 99));
		testDragged(new Point(11, 22));
		testDragged(new Point(56, 85));
		testReleased();

		testPressed(new Point(58, 29));
		testDragged(new Point(31, 45));
		testDragged(new Point(44, 5));
		testDragged(new Point(50, 35));
		testDragged(new Point(40, 39));
		testDragged(new Point(50, 29));
		testReleased();

		testPressed(new Point(73, 26));
		testDragged(new Point(43, 96));
		testDragged(new Point(46, 49));
		testDragged(new Point(36, 4));
		testDragged(new Point(52, 66));
		testDragged(new Point(73, 10));
		testDragged(new Point(97, 69));
		testDragged(new Point(5, 66));
		testDragged(new Point(44, 89));
		testDragged(new Point(82, 80));
		testDragged(new Point(95, 89));
		testReleased();

		testPressed(new Point(31, 77));
		testDragged(new Point(45, 98));
		testDragged(new Point(5, 19));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(84, 86));
		testDragged(new Point(33, 97));
		testReleased();

		testPressed(new Point(73, 26));
		testDragged(new Point(43, 96));
		testReleased();
		
		testPressed(new Point(43, 96));
		testDragged(new Point(46, 49));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(60, 78));
		testDragged(new Point(1, 72));
		testDragged(new Point(47, 60));
		testReleased();

		testPressed(new Point(7, 17));
		testDragged(new Point(2, 78));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testXBCEqual0Bug() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(45, 47));
		testDragged(new Point(7, 41));
		testReleased();
		
		testPressed(new Point(7, 41));
		testDragged(new Point(45, 81));
		testReleased();

		testPressed(new Point(13, 56));
		testDragged(new Point(4, 17));
		testReleased();

		testPressed(new Point(25, 62));
		testDragged(new Point(3, 34));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug3() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(71, 26));
		testDragged(new Point(6, 91));
		testDragged(new Point(76, 24));
		testReleased();

		testPressed(new Point(91, 98));
		testDragged(new Point(3, 84));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testYBCEqual0Bug() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(13, 47));
		testDragged(new Point(53, 43));
		testReleased();

		testPressed(new Point(17, 38));
		testDragged(new Point(36, 59));
		testDragged(new Point(100, 27));
		testReleased();

		testPressed(new Point(0, 59));
		testDragged(new Point(37, 36));
		testReleased();

		testPressed(new Point(19, 4));
		testDragged(new Point(26, 80));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug4() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(21, 11));
		testDragged(new Point(23, 4));
		testReleased();
		
		testPressed(new Point(18, 11));
		testDragged(new Point(21, 11));
		testReleased();
		
		testPressed(new Point(21, 11));
		testDragged(new Point(21, 9));
		testReleased();
		
		testPressed(new Point(21, 9));
		testDragged(new Point(44, 94));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testInconsistent1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(21, 11));
		testDragged(new Point(23, 4));
		testReleased();
		
		testPressed(new Point(18, 11));
		testDragged(new Point(21, 11));
		testReleased();
		
		testPressed(new Point(18, 10));
		testDragged(new Point(21, 10));
		testReleased();
		
		testPressed(new Point(21, 11));
		testDragged(new Point(21, 9));
		testReleased();
		
		testPressed(new Point(21, 9));
		testDragged(new Point(44, 94));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testInconsistent2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(18, 11));
		testDragged(new Point(21, 11));
		testReleased();
		
		testPressed(new Point(18, 10));
		testDragged(new Point(21, 10));
		testReleased();
		
		testPressed(new Point(21, 11));
		testDragged(new Point(21, 9));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	
	@Test
	public void testRemoved() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(703, 306));
		testDragged(new Point(703, 335));
		testReleased();
		
		testPressed(new Point(754, 329));
		testDragged(new Point(699, 311));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAssertFalse() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(703, 306));
		testDragged(new Point(703, 320));
		testReleased();
		
		testPressed(new Point(717, 315));
		testDragged(new Point(684, 308));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testAssertFalse2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(583, 442));
		testDragged(new Point(583, 447));
		testDragged(new Point(584, 449));
		testDragged(new Point(584, 450));
		testDragged(new Point(583, 442));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug3() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(84, 86));
		testDragged(new Point(33, 97));
		testReleased();
		
		testPressed(new Point(52, 8));
		testDragged(new Point(30, 65));
		testDragged(new Point(19, 22));
		testDragged(new Point(77, 95));
		testDragged(new Point(30, 64));
		testReleased();

		testPressed(new Point(29, 90));
		testDragged(new Point(66, 99));
		testDragged(new Point(11, 22));
		testDragged(new Point(56, 85));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testCAndDAreEqualBug() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(30, 65));
		testDragged(new Point(19, 22));
		testReleased();

		testPressed(new Point(66, 99));
		testDragged(new Point(11, 22));
		testReleased();
		
		testPressed(new Point(11, 22));
		testDragged(new Point(56, 85));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testNoEdgesShouldIntersectBug1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(73, 26));
		testDragged(new Point(43, 96));
		testDragged(new Point(46, 49));
		testReleased();

		testPressed(new Point(45, 98));
		testDragged(new Point(5, 19));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(52, 8));
		testDragged(new Point(30, 65));
		testReleased();

		testPressed(new Point(51, 35));
		testDragged(new Point(34, 74));
		testDragged(new Point(66, 99));
		testDragged(new Point(11, 22));
		testReleased();
		
		
		testPressed(new Point(11, 22));
		testDragged(new Point(56, 85));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(42, 45));
		testDragged(new Point(43, 42));
		testReleased();
		
		testPressed(new Point(44, 44));
		testDragged(new Point(44, 37));
		testReleased();
		
		testPressed(new Point(30, 23));
		testDragged(new Point(63, 73));
		testReleased();
		
	}
	
	@Test
	public void testBug3() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(40, 19));
		testDragged(new Point(43, 61));
		testReleased();
		
		testPressed(new Point(99, 95));
		testDragged(new Point(38, 31));
		testReleased();

		testPressed(new Point(40, 35));
		testDragged(new Point(82, 8));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug4() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(351, 140));
		testDragged(new Point(454, 11));
		testReleased();
		
		testPressed(new Point(454, 11));
		testDragged(new Point(436, 146));
		testReleased();
		
		testPressed(new Point(389, 36));
		testDragged(new Point(462, 273));
		testReleased();
		
		testPressed(new Point(216, 161));
		testDragged(new Point(491, 36));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug5() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(463, 221));
		testDragged(new Point(25, 28));
		testReleased();
		
		testPressed(new Point(274, 24));
		testDragged(new Point(34, 38));
		testReleased();
		
		testPressed(new Point(34, 38));
		testDragged(new Point(428, 78));
		testReleased();
		
		testPressed(new Point(46, 40));
		testDragged(new Point(364, 52));
		testReleased();
		
	}
	
	@Test
	public void testBug6() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(390, 91));
		testDragged(new Point(32, 324));
		testDragged(new Point(19, 246));
		testDragged(new Point(116, 147));
		testDragged(new Point(185, 261));
		testDragged(new Point(211, 398));
		testDragged(new Point(118, 34));
		testReleased();

		testPressed(new Point(100, 216));
		testDragged(new Point(262, 444));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAndroidBug() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(135,455));
		testDragged(new Point(135,455));
		testDragged(new Point(130,445));
		testDragged(new Point(130,440));
		testDragged(new Point(130,440));
		testDragged(new Point(130,435));
		testDragged(new Point(125,430));
		testDragged(new Point(125,420));
		testDragged(new Point(125,420));
		testDragged(new Point(125,405));
		testDragged(new Point(125,400));
		testDragged(new Point(125,390));
		testDragged(new Point(125,385));
		testDragged(new Point(125,380));
		testDragged(new Point(125,375));
		testDragged(new Point(125,370));
		testDragged(new Point(125,360));
		testDragged(new Point(130,350));
		testDragged(new Point(135,330));
		testDragged(new Point(135,320));
		testDragged(new Point(140,305));
		testDragged(new Point(140,295));
		testDragged(new Point(150,280));
		testDragged(new Point(155,270));
		testDragged(new Point(160,260));
		testDragged(new Point(165,250));
		testDragged(new Point(170,245));
		testDragged(new Point(180,240));
		testDragged(new Point(185,235));
		testDragged(new Point(195,225));
		testDragged(new Point(195,225));
		testDragged(new Point(200,220));
		testDragged(new Point(205,220));
		testDragged(new Point(215,215));
		testDragged(new Point(215,215));
		testDragged(new Point(220,210));
		testDragged(new Point(220,210));
		testDragged(new Point(225,210));
		testDragged(new Point(225,210));
		testDragged(new Point(225,210));
		testDragged(new Point(225,210));
		testDragged(new Point(225,210));
		testDragged(new Point(225,210));
		testDragged(new Point(230,205));
		testReleased();
		testPressed(new Point(95,655));
		testDragged(new Point(95,655));
		testDragged(new Point(95,655));
		testDragged(new Point(95,655));
		testDragged(new Point(110,655));
		testDragged(new Point(125,655));
		testDragged(new Point(170,650));
		testDragged(new Point(180,650));
		testDragged(new Point(200,645));
		testDragged(new Point(230,635));
		testDragged(new Point(235,635));
		testDragged(new Point(235,635));
		testDragged(new Point(235,635));
		testDragged(new Point(235,635));
		testDragged(new Point(235,635));
		testDragged(new Point(240,635));
		testDragged(new Point(240,635));
		testDragged(new Point(240,635));
		testDragged(new Point(240,635));
		testDragged(new Point(240,635));
		testDragged(new Point(240,635));
		testDragged(new Point(240,635));
		testReleased();
		testPressed(new Point(245,640));
		testDragged(new Point(245,640));
		testDragged(new Point(245,640));
		testDragged(new Point(245,640));
		testDragged(new Point(250,640));
		testDragged(new Point(260,635));
		testDragged(new Point(265,635));
		testDragged(new Point(270,635));
		testDragged(new Point(280,635));
		testDragged(new Point(290,630));
		testDragged(new Point(305,630));
		testDragged(new Point(310,625));
		testDragged(new Point(315,625));
		testDragged(new Point(330,620));
		testDragged(new Point(330,615));
		testDragged(new Point(330,615));
		testDragged(new Point(330,615));
		testDragged(new Point(330,615));
		testDragged(new Point(330,615));
		testDragged(new Point(335,615));
		testReleased();
		testPressed(new Point(230,555));
		testDragged(new Point(235,545));
		testDragged(new Point(235,530));
		testDragged(new Point(235,515));
		testDragged(new Point(240,500));
		testDragged(new Point(240,485));
		testDragged(new Point(255,445));
		testDragged(new Point(255,445));
		testDragged(new Point(275,420));
		testDragged(new Point(280,420));
		testDragged(new Point(290,415));
		testDragged(new Point(295,410));
		testDragged(new Point(300,410));
		testDragged(new Point(300,410));
		testDragged(new Point(305,410));
		testDragged(new Point(305,410));
		testReleased();
		testPressed(new Point(320,420));
		testDragged(new Point(320,420));
		testDragged(new Point(320,420));
		testDragged(new Point(320,420));
		testDragged(new Point(320,405));
		testDragged(new Point(320,390));
		testDragged(new Point(325,380));
		testDragged(new Point(325,370));
		testDragged(new Point(330,350));
		testDragged(new Point(335,315));
		testDragged(new Point(335,310));
		testDragged(new Point(335,305));
		testDragged(new Point(340,285));
		testDragged(new Point(340,280));
		testDragged(new Point(350,260));
		testDragged(new Point(360,250));
		testDragged(new Point(360,240));
		testDragged(new Point(365,235));
		testDragged(new Point(365,235));
		testReleased();
		testPressed(new Point(240,195));
		testDragged(new Point(240,195));
		testDragged(new Point(240,195));
		testDragged(new Point(240,195));
		testDragged(new Point(240,180));
		testDragged(new Point(240,180));
		testDragged(new Point(245,165));
		testDragged(new Point(250,150));
		testDragged(new Point(260,130));
		testDragged(new Point(265,125));
		testDragged(new Point(265,125));
		testDragged(new Point(280,110));
		testDragged(new Point(280,110));
		testDragged(new Point(280,110));
		testDragged(new Point(285,105));
		testDragged(new Point(305,90));
		testDragged(new Point(305,90));
		testDragged(new Point(310,85));
		testDragged(new Point(315,85));
		testDragged(new Point(315,85));
		testReleased();
		testPressed(new Point(190,80));
		testDragged(new Point(190,80));
		testDragged(new Point(205,80));
		testDragged(new Point(210,75));
		testDragged(new Point(215,75));
		testDragged(new Point(220,75));
		testDragged(new Point(225,75));
		testDragged(new Point(245,75));
		testDragged(new Point(265,75));
		testDragged(new Point(275,75));
		testDragged(new Point(285,75));
		testDragged(new Point(290,75));
		testDragged(new Point(295,80));
		testDragged(new Point(300,80));
		testDragged(new Point(310,80));
		testDragged(new Point(310,80));
		testDragged(new Point(315,80));
		testDragged(new Point(315,85));
		testDragged(new Point(320,85));
		testDragged(new Point(320,85));
		testReleased();
		testPressed(new Point(385,220));
		testDragged(new Point(385,220));
		testDragged(new Point(385,220));
		testDragged(new Point(380,225));
		testDragged(new Point(375,225));
		testDragged(new Point(370,225));
		testDragged(new Point(365,225));
		testDragged(new Point(360,225));
		testDragged(new Point(355,230));
		testDragged(new Point(345,230));
		testDragged(new Point(330,235));
		testDragged(new Point(320,240));
		testDragged(new Point(315,240));
		testDragged(new Point(310,245));
		testDragged(new Point(300,245));
		testDragged(new Point(295,250));
		testDragged(new Point(280,255));
		testDragged(new Point(270,260));
		testDragged(new Point(265,265));
		testDragged(new Point(265,265));
		testDragged(new Point(260,265));
		testDragged(new Point(255,270));
		testDragged(new Point(250,270));
		testDragged(new Point(250,275));
		testDragged(new Point(245,275));
		testDragged(new Point(245,275));
		testDragged(new Point(245,275));
		testDragged(new Point(240,280));
		testDragged(new Point(240,280));
		testDragged(new Point(240,280));
		testDragged(new Point(240,280));
		testDragged(new Point(240,280));
		testDragged(new Point(240,285));
		testReleased();
		testPressed(new Point(140,460));
		testDragged(new Point(140,460));
		testDragged(new Point(150,450));
		testDragged(new Point(150,440));
		testDragged(new Point(160,435));
		testDragged(new Point(170,420));
		testDragged(new Point(180,405));
		testDragged(new Point(185,395));
		testDragged(new Point(190,390));
		testDragged(new Point(190,385));
		testDragged(new Point(195,380));
		testDragged(new Point(200,375));
		testDragged(new Point(200,370));
		testDragged(new Point(200,365));
		testDragged(new Point(205,365));
		testDragged(new Point(205,360));
		testDragged(new Point(210,355));
		testDragged(new Point(215,350));
		testDragged(new Point(220,345));
		testDragged(new Point(225,345));
		testDragged(new Point(230,340));
		testDragged(new Point(235,330));
		testDragged(new Point(235,325));
		testDragged(new Point(240,320));
		testDragged(new Point(245,315));
		testDragged(new Point(245,315));
		testDragged(new Point(245,310));
		testDragged(new Point(250,310));
		testDragged(new Point(250,310));
		testDragged(new Point(250,310));
		testDragged(new Point(250,305));
		testDragged(new Point(250,305));
		testReleased();
		testPressed(new Point(195,80));
		testDragged(new Point(195,80));
		testDragged(new Point(195,80));
		testDragged(new Point(180,95));
		testDragged(new Point(170,100));
		testDragged(new Point(155,115));
		testDragged(new Point(145,125));
		testDragged(new Point(135,135));
		testDragged(new Point(125,145));
		testDragged(new Point(120,155));
		testDragged(new Point(115,160));
		testDragged(new Point(110,165));
		testDragged(new Point(110,170));
		testDragged(new Point(105,185));
		testDragged(new Point(100,190));
		testDragged(new Point(100,195));
		testDragged(new Point(95,200));
		testDragged(new Point(95,205));
		testDragged(new Point(90,210));
		testDragged(new Point(90,220));
		testDragged(new Point(85,240));
		testDragged(new Point(80,245));
		testDragged(new Point(80,250));
		testDragged(new Point(75,265));
		testDragged(new Point(75,275));
		testDragged(new Point(75,285));
		testDragged(new Point(70,305));
		testDragged(new Point(70,315));
		testDragged(new Point(70,325));
		testDragged(new Point(65,350));
		testDragged(new Point(65,360));
		testDragged(new Point(65,370));
		testDragged(new Point(60,390));
		testDragged(new Point(60,400));
		testDragged(new Point(60,410));
		testDragged(new Point(60,415));
		testDragged(new Point(60,425));
		testDragged(new Point(55,435));
		testDragged(new Point(55,445));
		testDragged(new Point(55,455));
		testDragged(new Point(55,465));
		testDragged(new Point(60,475));
		testDragged(new Point(60,485));
		testDragged(new Point(60,495));
		testDragged(new Point(60,505));
		testDragged(new Point(65,515));
		testDragged(new Point(70,520));
		testDragged(new Point(70,530));
		testDragged(new Point(75,535));
		testDragged(new Point(75,535));
		testDragged(new Point(80,540));
		testDragged(new Point(80,540));
		testDragged(new Point(85,545));
		testDragged(new Point(85,545));
		testDragged(new Point(85,550));
		testDragged(new Point(90,550));
		testDragged(new Point(90,550));
		testReleased();
		testPressed(new Point(100,655));
		testDragged(new Point(100,655));
		testDragged(new Point(100,645));
		testDragged(new Point(100,640));
		testDragged(new Point(95,630));
		testDragged(new Point(95,620));
		testDragged(new Point(95,615));
		testDragged(new Point(95,610));
		testDragged(new Point(95,600));
		testDragged(new Point(95,595));
		testDragged(new Point(95,590));
		testDragged(new Point(95,585));
		testDragged(new Point(95,580));
		testDragged(new Point(90,580));
		testDragged(new Point(90,575));
		testDragged(new Point(90,575));
		testDragged(new Point(90,575));
		testDragged(new Point(90,570));
		testDragged(new Point(90,570));
		testDragged(new Point(90,570));
		testDragged(new Point(90,570));
		testDragged(new Point(90,570));
		testDragged(new Point(90,570));
		testReleased();
		testPressed(new Point(245,565));
		testDragged(new Point(245,565));
		testDragged(new Point(250,570));
		testDragged(new Point(265,575));
		testDragged(new Point(265,575));
		testDragged(new Point(275,575));
		testDragged(new Point(280,580));
		testDragged(new Point(290,585));
		testDragged(new Point(300,590));
		testDragged(new Point(305,590));
		testDragged(new Point(310,590));
		testDragged(new Point(315,595));
		testDragged(new Point(315,595));
		testDragged(new Point(320,600));
		testDragged(new Point(320,600));
		testDragged(new Point(325,600));
		testDragged(new Point(325,600));
		testDragged(new Point(325,600));
		testDragged(new Point(330,600));
		testDragged(new Point(330,605));
		testReleased();
		testPressed(new Point(390,510));
		testDragged(new Point(385,500));
		testDragged(new Point(380,495));
		testDragged(new Point(375,495));
		testDragged(new Point(355,475));
		testDragged(new Point(335,450));
		testDragged(new Point(320,445));
		testDragged(new Point(300,425));
		testDragged(new Point(275,415));
		testDragged(new Point(265,405));
		testDragged(new Point(260,400));
		testDragged(new Point(245,390));
		testDragged(new Point(230,385));
		testDragged(new Point(210,370));
		testDragged(new Point(195,365));
		testDragged(new Point(180,360));
		testDragged(new Point(170,355));
		testDragged(new Point(160,345));
		testDragged(new Point(140,340));
		testDragged(new Point(125,325));
		testDragged(new Point(115,320));
		testDragged(new Point(105,315));
		testDragged(new Point(100,310));
		testDragged(new Point(90,305));
		testDragged(new Point(75,300));
		testDragged(new Point(75,300));
		testDragged(new Point(65,295));
		testDragged(new Point(65,295));
		testDragged(new Point(60,290));
		testDragged(new Point(60,290));
		testDragged(new Point(60,290));
		testReleased();


		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug7() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(661, 568));
		testDragged(new Point(664, 568));
		testDragged(new Point(664, 567));
		testDragged(new Point(665, 567));
		testReleased();
		
		testPressed(new Point(665, 567));
		testDragged(new Point(661, 570));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				CONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testBug8() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(56, 10));
		testDragged(new Point(57, 11));
		testDragged(new Point(58, 11));
		testDragged(new Point(54, 11));
		testDragged(new Point(54, 12));
		testReleased();
		
		testPressed(new Point(54, 12));
		testDragged(new Point(58, 11));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				CONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testBug9() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(775, 457));
		testDragged(new Point(772, 467));
		testDragged(new Point(772, 469));
		testDragged(new Point(773, 465));
		testReleased();
		
		testPressed(new Point(773, 465));
		testDragged(new Point(775, 457));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				CONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testBug10() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(775, 457));
		testDragged(new Point(772, 467));
		testDragged(new Point(772, 469));
		testDragged(new Point(775, 457));
		testDragged(new Point(778, 449));
		testDragged(new Point(798, 448));
		testReleased();
		
		testPressed(new Point(798, 448));
		testDragged(new Point(760, 460));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				CONTROLLER.mc.hashCode();
			}
		});
		
	}
	
	@Test
	public void testEdgeDetection() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.NONE;
		
		testPressed(new Point(2, 5));
		testDragged(new Point(12, 5));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
		Point a = new Point(3, 5);
		List<Segment> in = MODEL.findAllSegments(a);
		
		assertEquals(1, in.size());
		
		assertTrue(MODEL.checkConsistency());
	}
	
	@Test
	public void testCatastrophicAdjusting() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.STRATEGY1;
		
		testPressed(new Point(934., 578.));
		testDragged(new Point(723., 554.));
		testDragged(new Point(830., 561.));
		testReleased();
		
		testPressed(new Point(830., 561.));
		testDragged(new Point(707., 553.));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug11() throws Exception {
		
		//CONTROLLER.strat = MassageStrategy.STRATEGY1;
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(161., 580.));
		testDragged(new Point(161., 582.));
		testDragged(new Point(160., 587.));
		testDragged(new Point(158., 606.));
		testDragged(new Point(156., 628.));
		testDragged(new Point(154., 658.));
		testDragged(new Point(154., 703.));
		testDragged(new Point(154., 733.));
		testDragged(new Point(154., 747.));
		testDragged(new Point(154., 751.));
		testDragged(new Point(154., 752.));
		testDragged(new Point(154., 718.));
		testDragged(new Point(163., 654.));
		testDragged(new Point(174., 562.));
		testDragged(new Point(185., 482.));
		testDragged(new Point(192., 424.));
		testDragged(new Point(192., 396.));
		testDragged(new Point(192., 393.));
		testDragged(new Point(192., 392.));
		testDragged(new Point(193., 396.));
		testDragged(new Point(193., 401.));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
		/*
		 * test that <193, 401> is at the end
		 */
		
	}
	
	@Test
	public void testBug12() throws Exception {
		
		//CONTROLLER.strat = MassageStrategy.STRATEGY1;
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(529., 453.));
		testDragged(new Point(525., 449.));
		testDragged(new Point(522., 444.));
		testDragged(new Point(517., 435.));
		testDragged(new Point(514., 429.));
		testDragged(new Point(510., 417.));
		testDragged(new Point(508., 392.));
		testDragged(new Point(508., 370.));
		testDragged(new Point(508., 354.));
		testDragged(new Point(508., 340.));
		testDragged(new Point(510., 331.));
		testDragged(new Point(518., 321.));
		testDragged(new Point(536., 308.));
		testDragged(new Point(554., 292.));
		testDragged(new Point(574., 279.));
		testDragged(new Point(592., 268.));
		testDragged(new Point(609., 261.));
		testDragged(new Point(655., 259.));
		testDragged(new Point(702., 269.));
		testDragged(new Point(720., 283.));
		testDragged(new Point(735., 293.));
		testDragged(new Point(748., 304.));
		testDragged(new Point(758., 312.));
		testDragged(new Point(763., 319.));
		testDragged(new Point(766., 326.));
		testDragged(new Point(771., 337.));
		testDragged(new Point(771., 352.));
		testDragged(new Point(771., 395.));
		testDragged(new Point(771., 412.));
		testDragged(new Point(770., 426.));
		testDragged(new Point(762., 460.));
		testDragged(new Point(757., 477.));
		testDragged(new Point(751., 491.));
		testDragged(new Point(740., 510.));
		testDragged(new Point(732., 520.));
		testDragged(new Point(715., 531.));
		testDragged(new Point(706., 537.));
		testDragged(new Point(691., 541.));
		testDragged(new Point(665., 547.));
		testDragged(new Point(657., 549.));
		testDragged(new Point(650., 551.));
		testDragged(new Point(646., 551.));
		testDragged(new Point(634., 551.));
		testDragged(new Point(619., 550.));
		testDragged(new Point(605., 548.));
		testDragged(new Point(576., 540.));
		testDragged(new Point(561., 534.));
		testDragged(new Point(554., 531.));
		testDragged(new Point(545., 528.));
		testDragged(new Point(542., 525.));
		testDragged(new Point(537., 520.));
		testDragged(new Point(534., 517.));
		testDragged(new Point(528., 508.));
		testDragged(new Point(525., 503.));
		testDragged(new Point(519., 496.));
		testDragged(new Point(517., 492.));
		testDragged(new Point(514., 490.));
		testDragged(new Point(513., 487.));
		testDragged(new Point(510., 486.));
		testDragged(new Point(509., 484.));
		testDragged(new Point(508., 483.));
		testDragged(new Point(507., 483.));
		testDragged(new Point(507., 482.));
		testDragged(new Point(507., 481.));
		testDragged(new Point(508., 481.));
		testDragged(new Point(511., 478.));
		testDragged(new Point(512., 477.));
		testDragged(new Point(514., 476.));
		testDragged(new Point(516., 475.));
		testDragged(new Point(516., 474.));
		testDragged(new Point(517., 474.));
		testDragged(new Point(517., 472.));
		testDragged(new Point(517., 471.));
		testDragged(new Point(518., 471.));
		testDragged(new Point(519., 470.));
		testDragged(new Point(519., 469.));
		testDragged(new Point(519., 468.));
		testDragged(new Point(520., 468.));
		testDragged(new Point(520., 467.));
		testDragged(new Point(520., 465.));
		testDragged(new Point(520., 464.));
		testDragged(new Point(520., 463.));
		testDragged(new Point(521., 463.));
		testDragged(new Point(521., 462.));
		testDragged(new Point(522., 462.));
		testDragged(new Point(522., 461.));
		testDragged(new Point(522., 460.));
		testDragged(new Point(524., 460.));
		testDragged(new Point(525., 458.));
		testDragged(new Point(526., 458.));
		testDragged(new Point(526., 457.));
		testReleased();

		testPressed(new Point(405., 417.));
		testDragged(new Point(408., 417.));
		testDragged(new Point(421., 411.));
		testDragged(new Point(453., 404.));
		testDragged(new Point(491., 396.));
		testDragged(new Point(566., 385.));
		testDragged(new Point(651., 378.));
		testDragged(new Point(734., 378.));
		testDragged(new Point(834., 378.));
		testDragged(new Point(915., 392.));
		testDragged(new Point(997., 406.));
		testDragged(new Point(1063., 424.));
		testDragged(new Point(1100., 429.));
		testReleased();
		
		CONTROLLER.queueAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
		/*
		 * test that <193, 401> is at the end
		 */
		
	}
	
	@Test
	public void testBug13() throws Exception {
		
		//CONTROLLER.strat = MassageStrategy.STRATEGY1;
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(532., 476.));
		testDragged(new Point(532., 478.));
		testDragged(new Point(530., 479.));
		testDragged(new Point(526., 482.));
		testDragged(new Point(513., 484.));
		testDragged(new Point(496., 484.));
		testDragged(new Point(479., 481.));
		testDragged(new Point(462., 472.));
		testDragged(new Point(442., 459.));
		testDragged(new Point(422., 447.));
		testDragged(new Point(401., 431.));
		testDragged(new Point(382., 408.));
		testDragged(new Point(360., 352.));
		testDragged(new Point(356., 333.));
		testDragged(new Point(351., 305.));
		testDragged(new Point(351., 278.));
		testDragged(new Point(351., 257.));
		testDragged(new Point(380., 216.));
		testDragged(new Point(401., 203.));
		testDragged(new Point(418., 198.));
		testDragged(new Point(445., 198.));
		testDragged(new Point(457., 204.));
		testDragged(new Point(472., 214.));
		testDragged(new Point(487., 225.));
		testDragged(new Point(510., 271.));
		testDragged(new Point(512., 293.));
		testDragged(new Point(502., 327.));
		testDragged(new Point(488., 349.));
		testDragged(new Point(448., 405.));
		testDragged(new Point(428., 420.));
		testDragged(new Point(359., 449.));
		testDragged(new Point(320., 458.));
		testDragged(new Point(314., 460.));
		testDragged(new Point(305., 461.));
		testDragged(new Point(304., 461.));
		testReleased();

		testPressed(new Point(788., 555.));
		testDragged(new Point(787., 555.));
		testDragged(new Point(785., 551.));
		testDragged(new Point(778., 544.));
		testDragged(new Point(772., 532.));
		testDragged(new Point(765., 507.));
		testDragged(new Point(758., 480.));
		testDragged(new Point(756., 460.));
		testDragged(new Point(754., 430.));
		testDragged(new Point(754., 397.));
		testDragged(new Point(754., 373.));
		testDragged(new Point(754., 354.));
		testDragged(new Point(764., 309.));
		testDragged(new Point(775., 289.));
		testDragged(new Point(818., 269.));
		testDragged(new Point(843., 264.));
		testDragged(new Point(864., 264.));
		testDragged(new Point(912., 279.));
		testDragged(new Point(946., 298.));
		testDragged(new Point(966., 311.));
		testDragged(new Point(992., 337.));
		testDragged(new Point(998., 346.));
		testDragged(new Point(998., 382.));
		testDragged(new Point(988., 402.));
		testDragged(new Point(967., 418.));
		testDragged(new Point(942., 430.));
		testDragged(new Point(892., 441.));
		testDragged(new Point(870., 443.));
		testDragged(new Point(840., 443.));
		testDragged(new Point(813., 443.));
		testDragged(new Point(791., 440.));
		testDragged(new Point(759., 427.));
		testDragged(new Point(748., 412.));
		testDragged(new Point(727., 384.));
		testDragged(new Point(714., 367.));
		testDragged(new Point(712., 356.));
		testDragged(new Point(712., 345.));
		testDragged(new Point(712., 326.));
		testDragged(new Point(718., 301.));
		testDragged(new Point(735., 266.));
		testDragged(new Point(744., 251.));
		testDragged(new Point(756., 238.));
		testDragged(new Point(790., 205.));
		testDragged(new Point(816., 191.));
		testDragged(new Point(841., 179.));
		testDragged(new Point(861., 173.));
		testDragged(new Point(875., 168.));
		testDragged(new Point(892., 166.));
		testDragged(new Point(921., 167.));
		testDragged(new Point(938., 176.));
		testDragged(new Point(956., 192.));
		testDragged(new Point(985., 227.));
		testDragged(new Point(993., 240.));
		testDragged(new Point(1008., 272.));
		testDragged(new Point(1023., 350.));
		testDragged(new Point(1023., 366.));
		testDragged(new Point(1023., 391.));
		testDragged(new Point(1023., 415.));
		testDragged(new Point(1022., 432.));
		testDragged(new Point(1020., 449.));
		testDragged(new Point(1016., 463.));
		testDragged(new Point(1012., 485.));
		testDragged(new Point(1007., 507.));
		testDragged(new Point(1001., 525.));
		testDragged(new Point(995., 537.));
		testDragged(new Point(977., 552.));
		testDragged(new Point(960., 557.));
		testDragged(new Point(949., 558.));
		testDragged(new Point(935., 560.));
		testDragged(new Point(921., 560.));
		testDragged(new Point(907., 560.));
		testDragged(new Point(893., 560.));
		testDragged(new Point(882., 557.));
		testDragged(new Point(872., 552.));
		testDragged(new Point(861., 537.));
		testDragged(new Point(846., 472.));
		testDragged(new Point(846., 445.));
		testDragged(new Point(854., 362.));
		testDragged(new Point(858., 337.));
		testDragged(new Point(860., 315.));
		testDragged(new Point(865., 269.));
		testDragged(new Point(865., 252.));
		testDragged(new Point(865., 233.));
		testDragged(new Point(865., 221.));
		testDragged(new Point(861., 202.));
		testDragged(new Point(854., 174.));
		testDragged(new Point(842., 145.));
		testDragged(new Point(839., 138.));
		testDragged(new Point(836., 134.));
		testDragged(new Point(828., 124.));
		testDragged(new Point(827., 123.));
		testDragged(new Point(826., 122.));
		testDragged(new Point(826., 121.));
		testDragged(new Point(824., 120.));
		testDragged(new Point(823., 120.));
		testDragged(new Point(819., 112.));
		testDragged(new Point(815., 105.));
		testDragged(new Point(814., 102.));
		testDragged(new Point(812., 100.));
		testDragged(new Point(812., 98.));
		testDragged(new Point(812., 99.));
		testReleased();

		testPressed(new Point(637., 125.));
		testDragged(new Point(637., 129.));
		testDragged(new Point(635., 135.));
		testDragged(new Point(633., 144.));
		testDragged(new Point(633., 152.));
		testDragged(new Point(633., 169.));
		testDragged(new Point(633., 188.));
		testDragged(new Point(643., 233.));
		testDragged(new Point(654., 256.));
		testDragged(new Point(666., 281.));
		testDragged(new Point(679., 301.));
		testDragged(new Point(697., 317.));
		testDragged(new Point(720., 328.));
		testDragged(new Point(778., 350.));
		testDragged(new Point(798., 355.));
		testDragged(new Point(832., 365.));
		testDragged(new Point(852., 374.));
		testDragged(new Point(877., 381.));
		testDragged(new Point(902., 386.));
		testDragged(new Point(940., 390.));
		testDragged(new Point(984., 397.));
		testDragged(new Point(1012., 397.));
		testDragged(new Point(1061., 401.));
		testDragged(new Point(1113., 408.));
		testDragged(new Point(1138., 410.));
		testDragged(new Point(1159., 410.));
		testReleased();

		testPressed(new Point(1135., 107.));
		testDragged(new Point(1133., 107.));
		testDragged(new Point(1126., 110.));
		testDragged(new Point(1117., 116.));
		testDragged(new Point(1104., 127.));
		testDragged(new Point(1091., 144.));
		testDragged(new Point(1077., 165.));
		testDragged(new Point(1064., 180.));
		testDragged(new Point(1027., 222.));
		testDragged(new Point(998., 266.));
		testDragged(new Point(948., 336.));
		testDragged(new Point(932., 369.));
		testDragged(new Point(923., 392.));
		testDragged(new Point(904., 425.));
		testDragged(new Point(898., 437.));
		testDragged(new Point(887., 452.));
		testDragged(new Point(880., 474.));
		testDragged(new Point(869., 497.));
		testDragged(new Point(854., 526.));
		testDragged(new Point(846., 536.));
		testDragged(new Point(837., 557.));
		testDragged(new Point(832., 572.));
		testDragged(new Point(829., 584.));
		testDragged(new Point(823., 604.));
		testDragged(new Point(821., 609.));
		testDragged(new Point(818., 616.));
		testDragged(new Point(817., 617.));
		testDragged(new Point(816., 623.));
		testDragged(new Point(816., 625.));
		testDragged(new Point(815., 629.));
		testDragged(new Point(815., 630.));
		testDragged(new Point(815., 631.));
		testReleased();

		testPressed(new Point(246., 205.));
		testDragged(new Point(246., 206.));
		testDragged(new Point(251., 209.));
		testDragged(new Point(264., 217.));
		testDragged(new Point(278., 224.));
		testDragged(new Point(293., 230.));
		testDragged(new Point(305., 236.));
		testDragged(new Point(325., 240.));
		testDragged(new Point(352., 247.));
		testDragged(new Point(383., 252.));
		testDragged(new Point(433., 268.));
		testDragged(new Point(462., 277.));
		testDragged(new Point(522., 303.));
		testDragged(new Point(542., 312.));
		testDragged(new Point(574., 327.));
		testDragged(new Point(601., 337.));
		testDragged(new Point(615., 339.));
		testDragged(new Point(626., 341.));
		testDragged(new Point(638., 345.));
		testDragged(new Point(646., 345.));
		testDragged(new Point(654., 347.));
		testDragged(new Point(655., 347.));
		testDragged(new Point(652., 349.));
		testDragged(new Point(647., 350.));
		testDragged(new Point(643., 352.));
		testDragged(new Point(638., 353.));
		testDragged(new Point(634., 356.));
		testDragged(new Point(629., 361.));
		testDragged(new Point(622., 368.));
		testDragged(new Point(611., 381.));
		testDragged(new Point(594., 392.));
		testDragged(new Point(542., 418.));
		testDragged(new Point(522., 431.));
		testDragged(new Point(504., 449.));
		testDragged(new Point(475., 488.));
		testDragged(new Point(464., 500.));
		testDragged(new Point(447., 513.));
		testDragged(new Point(437., 521.));
		testDragged(new Point(430., 526.));
		testDragged(new Point(416., 536.));
		testDragged(new Point(415., 536.));
		testDragged(new Point(415., 537.));
		testDragged(new Point(412., 543.));
		testDragged(new Point(409., 547.));
		testDragged(new Point(406., 552.));
		testDragged(new Point(404., 555.));
		testDragged(new Point(403., 555.));
		testDragged(new Point(403., 556.));
		testDragged(new Point(402., 554.));
		testDragged(new Point(402., 548.));
		testDragged(new Point(402., 538.));
		testDragged(new Point(402., 521.));
		testDragged(new Point(402., 494.));
		testDragged(new Point(402., 448.));
		testDragged(new Point(402., 418.));
		testDragged(new Point(402., 347.));
		testDragged(new Point(402., 325.));
		testDragged(new Point(403., 282.));
		testDragged(new Point(403., 268.));
		testDragged(new Point(403., 257.));
		testDragged(new Point(407., 239.));
		testDragged(new Point(407., 228.));
		testDragged(new Point(407., 214.));
		testDragged(new Point(407., 200.));
		testDragged(new Point(408., 183.));
		testDragged(new Point(408., 177.));
		testDragged(new Point(408., 171.));
		testDragged(new Point(410., 164.));
		testDragged(new Point(411., 158.));
		testDragged(new Point(411., 136.));
		testDragged(new Point(411., 125.));
		testDragged(new Point(411., 118.));
		testDragged(new Point(411., 113.));
		testDragged(new Point(411., 112.));
		testDragged(new Point(411., 113.));
		testDragged(new Point(411., 116.));
		testDragged(new Point(411., 124.));
		testDragged(new Point(407., 133.));
		testDragged(new Point(390., 163.));
		testDragged(new Point(372., 197.));
		testDragged(new Point(348., 235.));
		testDragged(new Point(329., 260.));
		testDragged(new Point(301., 316.));
		testDragged(new Point(290., 342.));
		testDragged(new Point(283., 359.));
		testDragged(new Point(277., 371.));
		testDragged(new Point(268., 392.));
		testDragged(new Point(264., 404.));
		testDragged(new Point(260., 416.));
		testDragged(new Point(256., 425.));
		testDragged(new Point(253., 432.));
		testDragged(new Point(248., 437.));
		testDragged(new Point(237., 443.));
		testDragged(new Point(230., 446.));
		testDragged(new Point(223., 446.));
		testDragged(new Point(222., 446.));
		testDragged(new Point(224., 445.));
		testDragged(new Point(230., 440.));
		testDragged(new Point(238., 432.));
		testDragged(new Point(248., 424.));
		testDragged(new Point(255., 419.));
		testDragged(new Point(272., 410.));
		testDragged(new Point(333., 393.));
		testDragged(new Point(362., 384.));
		testDragged(new Point(391., 374.));
		testDragged(new Point(420., 364.));
		testDragged(new Point(465., 353.));
		testDragged(new Point(492., 346.));
		testDragged(new Point(534., 337.));
		testDragged(new Point(556., 337.));
		testDragged(new Point(597., 338.));
		testDragged(new Point(614., 342.));
		testDragged(new Point(623., 345.));
		testDragged(new Point(624., 345.));
		testDragged(new Point(625., 345.));
		testDragged(new Point(625., 344.));
		testDragged(new Point(625., 343.));
		testDragged(new Point(625., 332.));
		testDragged(new Point(622., 320.));
		testDragged(new Point(601., 292.));
		testDragged(new Point(564., 226.));
		testDragged(new Point(550., 205.));
		testDragged(new Point(537., 163.));
		testDragged(new Point(533., 151.));
		testDragged(new Point(531., 142.));
		testDragged(new Point(531., 141.));
		testDragged(new Point(530., 141.));
		testDragged(new Point(530., 143.));
		testDragged(new Point(530., 147.));
		testDragged(new Point(529., 153.));
		testDragged(new Point(527., 165.));
		testDragged(new Point(522., 187.));
		testDragged(new Point(520., 241.));
		testDragged(new Point(520., 299.));
		testDragged(new Point(520., 349.));
		testDragged(new Point(525., 407.));
		testDragged(new Point(535., 487.));
		testDragged(new Point(537., 501.));
		testDragged(new Point(541., 516.));
		testDragged(new Point(541., 522.));
		testDragged(new Point(541., 528.));
		testDragged(new Point(541., 536.));
		testDragged(new Point(541., 542.));
		testDragged(new Point(541., 545.));
		testDragged(new Point(541., 546.));
		testDragged(new Point(541., 548.));
		testDragged(new Point(541., 550.));
		testDragged(new Point(541., 554.));
		testDragged(new Point(541., 556.));
		testDragged(new Point(541., 559.));
		testDragged(new Point(541., 560.));
		testDragged(new Point(541., 562.));
		testReleased();

		testPressed(new Point(624., 576.));
		testDragged(new Point(624., 577.));
		testDragged(new Point(624., 581.));
		testDragged(new Point(624., 589.));
		testDragged(new Point(625., 596.));
		testDragged(new Point(626., 605.));
		testDragged(new Point(626., 606.));
		testDragged(new Point(626., 607.));
		testDragged(new Point(626., 608.));
		testDragged(new Point(623., 606.));
		testDragged(new Point(620., 602.));
		testDragged(new Point(609., 574.));
		testDragged(new Point(602., 559.));
		testDragged(new Point(598., 515.));
		testDragged(new Point(598., 491.));
		testDragged(new Point(598., 474.));
		testDragged(new Point(610., 446.));
		testDragged(new Point(623., 429.));
		testDragged(new Point(633., 424.));
		testDragged(new Point(658., 420.));
		testDragged(new Point(681., 428.));
		testDragged(new Point(698., 437.));
		testDragged(new Point(713., 445.));
		testDragged(new Point(726., 460.));
		testDragged(new Point(726., 466.));
		testDragged(new Point(721., 486.));
		testDragged(new Point(712., 501.));
		testDragged(new Point(675., 523.));
		testDragged(new Point(658., 530.));
		testDragged(new Point(630., 532.));
		testDragged(new Point(610., 515.));
		testDragged(new Point(599., 502.));
		testDragged(new Point(589., 476.));
		testDragged(new Point(587., 448.));
		testDragged(new Point(587., 423.));
		testDragged(new Point(595., 384.));
		testDragged(new Point(602., 366.));
		testDragged(new Point(608., 342.));
		testDragged(new Point(610., 323.));
		testDragged(new Point(612., 287.));
		testDragged(new Point(612., 268.));
		testDragged(new Point(612., 205.));
		testDragged(new Point(612., 181.));
		testDragged(new Point(611., 140.));
		testDragged(new Point(609., 99.));
		testDragged(new Point(607., 84.));
		testDragged(new Point(607., 77.));
		testDragged(new Point(607., 76.));
		testDragged(new Point(606., 75.));
		testDragged(new Point(606., 74.));
		testDragged(new Point(584., 5.));
		testDragged(new Point(582., -9.));
		testDragged(new Point(582., -15.));
		testDragged(new Point(579., -19.));
		testDragged(new Point(581., -19.));
		testDragged(new Point(582., -19.));
		testDragged(new Point(582., -16.));
		testDragged(new Point(582., -8.));
		testDragged(new Point(579., 9.));
		testDragged(new Point(570., 51.));
		testDragged(new Point(544., 137.));
		testDragged(new Point(528., 184.));
		testDragged(new Point(520., 216.));
		testDragged(new Point(517., 219.));
		testDragged(new Point(517., 221.));
		testDragged(new Point(516., 221.));
		testDragged(new Point(516., 220.));
		testDragged(new Point(516., 219.));
		testDragged(new Point(515., 221.));
		testDragged(new Point(511., 227.));
		testDragged(new Point(504., 235.));
		testDragged(new Point(491., 250.));
		testDragged(new Point(382., 354.));
		testDragged(new Point(339., 394.));
		testDragged(new Point(297., 431.));
		testDragged(new Point(272., 449.));
		testDragged(new Point(261., 458.));
		testDragged(new Point(257., 460.));
		testDragged(new Point(256., 460.));
		testDragged(new Point(256., 463.));
		testDragged(new Point(254., 467.));
		testDragged(new Point(252., 476.));
		testDragged(new Point(250., 490.));
		testDragged(new Point(246., 502.));
		testDragged(new Point(244., 516.));
		testDragged(new Point(234., 574.));
		testDragged(new Point(227., 587.));
		testDragged(new Point(226., 589.));
		testReleased();

		testPressed(new Point(226., 592.));
		testDragged(new Point(225., 592.));
		testDragged(new Point(222., 592.));
		testDragged(new Point(210., 590.));
		testDragged(new Point(194., 585.));
		testDragged(new Point(183., 580.));
		testDragged(new Point(168., 575.));
		testDragged(new Point(132., 563.));
		testDragged(new Point(117., 558.));
		testDragged(new Point(107., 552.));
		testDragged(new Point(98., 546.));
		testDragged(new Point(87., 543.));
		testDragged(new Point(78., 541.));
		testDragged(new Point(63., 537.));
		testDragged(new Point(52., 536.));
		testDragged(new Point(45., 534.));
		testDragged(new Point(40., 533.));
		testDragged(new Point(36., 532.));
		testDragged(new Point(35., 532.));
		testDragged(new Point(34., 532.));
		testDragged(new Point(33., 532.));
		testDragged(new Point(32., 532.));
		testDragged(new Point(30., 532.));
		testDragged(new Point(29., 532.));
		testDragged(new Point(28., 532.));
		testDragged(new Point(20., 530.));
		testDragged(new Point(14., 529.));
		testDragged(new Point(12., 528.));
		testDragged(new Point(11., 528.));
		testDragged(new Point(9., 527.));
		testDragged(new Point(6., 527.));
		testDragged(new Point(5., 526.));
		testDragged(new Point(4., 526.));
		testDragged(new Point(4., 524.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
		
	}
	
	@Test
	public void testBug14() throws Exception {
		
		//CONTROLLER.strat = MassageStrategy.STRATEGY1;
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(600., 365.));
		testDragged(new Point(600., 364.));
		testDragged(new Point(607., 361.));
		testDragged(new Point(644., 348.));
		testDragged(new Point(694., 342.));
		testDragged(new Point(747., 336.));
		testDragged(new Point(800., 330.));
		testDragged(new Point(856., 330.));
		testDragged(new Point(885., 330.));
		testDragged(new Point(904., 330.));
		testDragged(new Point(911., 330.));
		testDragged(new Point(913., 330.));
		testDragged(new Point(901., 335.));
		testDragged(new Point(870., 342.));
		testDragged(new Point(836., 352.));
		testDragged(new Point(786., 365.));
		testDragged(new Point(709., 383.));
		testDragged(new Point(637., 401.));
		testDragged(new Point(488., 441.));
		testDragged(new Point(445., 450.));
		testDragged(new Point(423., 454.));
		testDragged(new Point(414., 456.));
		testDragged(new Point(410., 456.));
		testDragged(new Point(421., 440.));
		testDragged(new Point(464., 411.));
		testDragged(new Point(522., 387.));
		testDragged(new Point(574., 368.));
		testDragged(new Point(718., 351.));
		testDragged(new Point(726., 351.));
		testDragged(new Point(730., 351.));
		testDragged(new Point(732., 351.));
		testDragged(new Point(733., 351.));
		testDragged(new Point(677., 359.));
		testDragged(new Point(617., 373.));
		testDragged(new Point(546., 387.));
		testDragged(new Point(474., 405.));
		testDragged(new Point(438., 422.));
		testDragged(new Point(416., 431.));
		testDragged(new Point(415., 431.));
		testDragged(new Point(433., 429.));
		testDragged(new Point(475., 426.));
		testDragged(new Point(520., 426.));
		testDragged(new Point(576., 445.));
		testDragged(new Point(629., 468.));
		testDragged(new Point(652., 482.));
		testDragged(new Point(660., 497.));
		testDragged(new Point(660., 511.));
		testDragged(new Point(650., 528.));
		testDragged(new Point(628., 542.));
		testDragged(new Point(581., 546.));
		testDragged(new Point(551., 532.));
		testDragged(new Point(512., 505.));
		testDragged(new Point(488., 451.));
		testDragged(new Point(495., 413.));
		testDragged(new Point(509., 392.));
		testDragged(new Point(524., 382.));
		testDragged(new Point(535., 382.));
		testDragged(new Point(561., 427.));
		testDragged(new Point(568., 460.));
		testDragged(new Point(573., 485.));
		testDragged(new Point(573., 509.));
		testDragged(new Point(573., 523.));
		testDragged(new Point(573., 524.));
		testDragged(new Point(571., 485.));
		testDragged(new Point(571., 413.));
		testDragged(new Point(571., 373.));
		testDragged(new Point(571., 360.));
		testDragged(new Point(571., 354.));
		testDragged(new Point(571., 352.));
		testDragged(new Point(571., 351.));
		testDragged(new Point(577., 397.));
		testDragged(new Point(592., 442.));
		testDragged(new Point(612., 499.));
		testDragged(new Point(638., 547.));
		testDragged(new Point(657., 572.));
		testDragged(new Point(681., 584.));
		testDragged(new Point(687., 584.));
		testDragged(new Point(689., 564.));
		testDragged(new Point(691., 534.));
		testDragged(new Point(691., 501.));
		testDragged(new Point(661., 363.));
		testDragged(new Point(640., 321.));
		testDragged(new Point(626., 299.));
		testDragged(new Point(623., 297.));
		testDragged(new Point(621., 318.));
		testDragged(new Point(621., 363.));
		testDragged(new Point(636., 414.));
		testDragged(new Point(656., 451.));
		testDragged(new Point(676., 470.));
		testDragged(new Point(694., 481.));
		testDragged(new Point(710., 481.));
		testDragged(new Point(725., 475.));
		testDragged(new Point(750., 405.));
		testDragged(new Point(755., 368.));
		testDragged(new Point(755., 341.));
		testDragged(new Point(755., 327.));
		testDragged(new Point(755., 323.));
		testDragged(new Point(759., 339.));
		testDragged(new Point(773., 387.));
		testDragged(new Point(796., 440.));
		testDragged(new Point(813., 476.));
		testDragged(new Point(826., 507.));
		testDragged(new Point(837., 527.));
		testDragged(new Point(844., 534.));
		testDragged(new Point(853., 534.));
		testDragged(new Point(861., 523.));
		testDragged(new Point(870., 505.));
		testDragged(new Point(874., 464.));
		testDragged(new Point(871., 444.));
		testDragged(new Point(867., 435.));
		testDragged(new Point(863., 435.));
		testDragged(new Point(853., 448.));
		testDragged(new Point(842., 470.));
		testDragged(new Point(828., 493.));
		testDragged(new Point(810., 524.));
		testDragged(new Point(788., 557.));
		testDragged(new Point(767., 582.));
		testDragged(new Point(766., 582.));
		testDragged(new Point(764., 582.));
		testDragged(new Point(778., 554.));
		testDragged(new Point(807., 509.));
		testDragged(new Point(829., 476.));
		testDragged(new Point(845., 455.));
		testDragged(new Point(850., 448.));
		testDragged(new Point(853., 448.));
		testDragged(new Point(855., 460.));
		testDragged(new Point(855., 479.));
		testDragged(new Point(848., 540.));
		testDragged(new Point(844., 557.));
		testDragged(new Point(843., 564.));
		testDragged(new Point(841., 567.));
		testDragged(new Point(841., 568.));
		testDragged(new Point(857., 524.));
		testDragged(new Point(909., 431.));
		testDragged(new Point(967., 357.));
		testDragged(new Point(999., 326.));
		testDragged(new Point(1012., 313.));
		testDragged(new Point(1016., 310.));
		testDragged(new Point(1019., 307.));
		testDragged(new Point(989., 333.));
		testDragged(new Point(948., 368.));
		testDragged(new Point(919., 389.));
		testDragged(new Point(901., 398.));
		testDragged(new Point(895., 401.));
		testDragged(new Point(890., 401.));
		testDragged(new Point(889., 401.));
		testDragged(new Point(887., 353.));
		testDragged(new Point(887., 270.));
		testDragged(new Point(887., 209.));
		testDragged(new Point(888., 187.));
		testDragged(new Point(890., 178.));
		testDragged(new Point(890., 175.));
		testDragged(new Point(890., 174.));
		testDragged(new Point(884., 192.));
		testDragged(new Point(868., 225.));
		testDragged(new Point(825., 279.));
		testDragged(new Point(814., 292.));
		testDragged(new Point(810., 295.));
		testDragged(new Point(808., 295.));
		testDragged(new Point(821., 284.));
		testDragged(new Point(838., 257.));
		testDragged(new Point(849., 240.));
		testDragged(new Point(852., 235.));
		testDragged(new Point(841., 252.));
		testDragged(new Point(823., 273.));
		testDragged(new Point(810., 290.));
		testDragged(new Point(805., 295.));
		testDragged(new Point(804., 296.));
		testDragged(new Point(803., 297.));
		testDragged(new Point(803., 303.));
		testDragged(new Point(803., 305.));
		testDragged(new Point(799., 327.));
		testDragged(new Point(794., 347.));
		testDragged(new Point(792., 361.));
		testDragged(new Point(791., 367.));
		testDragged(new Point(789., 367.));
		testDragged(new Point(789., 368.));
		testDragged(new Point(789., 365.));
		testDragged(new Point(790., 361.));
		testDragged(new Point(790., 360.));
		testDragged(new Point(791., 360.));
		testDragged(new Point(791., 364.));
		testDragged(new Point(791., 377.));
		testDragged(new Point(790., 393.));
		testDragged(new Point(786., 408.));
		testDragged(new Point(784., 422.));
		testDragged(new Point(780., 439.));
		testDragged(new Point(778., 450.));
		testDragged(new Point(776., 453.));
		testDragged(new Point(776., 454.));
		testDragged(new Point(775., 451.));
		testDragged(new Point(773., 443.));
		testDragged(new Point(773., 434.));
		testDragged(new Point(772., 432.));
		testDragged(new Point(772., 425.));
		testDragged(new Point(772., 421.));
		testDragged(new Point(772., 417.));
		testDragged(new Point(772., 416.));
		testDragged(new Point(767., 416.));
		testDragged(new Point(760., 416.));
		testDragged(new Point(756., 418.));
		testDragged(new Point(755., 419.));
		testDragged(new Point(757., 412.));
		testDragged(new Point(763., 398.));
		testDragged(new Point(771., 388.));
		testDragged(new Point(776., 383.));
		testDragged(new Point(780., 383.));
		testDragged(new Point(783., 383.));
		testDragged(new Point(784., 386.));
		testDragged(new Point(784., 397.));
		testDragged(new Point(784., 405.));
		testDragged(new Point(784., 410.));
		testDragged(new Point(784., 412.));
		testDragged(new Point(784., 414.));
		testDragged(new Point(796., 411.));
		testDragged(new Point(807., 407.));
		testDragged(new Point(812., 404.));
		testDragged(new Point(815., 404.));
		testDragged(new Point(813., 404.));
		testDragged(new Point(809., 405.));
		testDragged(new Point(807., 406.));
		testDragged(new Point(807., 407.));
		testDragged(new Point(810., 407.));
		testDragged(new Point(821., 406.));
		testDragged(new Point(830., 405.));
		testDragged(new Point(834., 405.));
		testDragged(new Point(835., 405.));
		testDragged(new Point(837., 405.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
		
	}
	
	@Test
	public void testBug15() throws Exception {
		
		//CONTROLLER.strat = MassageStrategy.STRATEGY1;
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(348., 284.));
		testDragged(new Point(347., 285.));
		testDragged(new Point(345., 290.));
		testDragged(new Point(342., 302.));
		testDragged(new Point(341., 307.));
		testDragged(new Point(341., 308.));
		testDragged(new Point(341., 310.));
		testDragged(new Point(341., 313.));
		testDragged(new Point(345., 314.));
		testDragged(new Point(349., 314.));
		testDragged(new Point(360., 312.));
		testDragged(new Point(365., 307.));
		testDragged(new Point(370., 296.));
		testDragged(new Point(370., 290.));
		testDragged(new Point(360., 285.));
		testDragged(new Point(345., 283.));
		testDragged(new Point(336., 283.));
		testDragged(new Point(331., 284.));
		testDragged(new Point(328., 285.));
		testDragged(new Point(328., 286.));
		testDragged(new Point(329., 289.));
		testDragged(new Point(334., 294.));
		testDragged(new Point(337., 296.));
		testDragged(new Point(340., 301.));
		testDragged(new Point(340., 302.));
		testDragged(new Point(341., 302.));
		testDragged(new Point(341., 303.));
		testDragged(new Point(345., 300.));
		testDragged(new Point(353., 288.));
		testDragged(new Point(371., 272.));
		testDragged(new Point(374., 271.));
		testDragged(new Point(377., 271.));
		testDragged(new Point(377., 275.));
		testDragged(new Point(375., 280.));
		testDragged(new Point(365., 291.));
		testDragged(new Point(362., 296.));
		testDragged(new Point(358., 300.));
		testDragged(new Point(357., 300.));
		testDragged(new Point(357., 290.));
		testDragged(new Point(357., 285.));
		testDragged(new Point(357., 284.));
		testDragged(new Point(357., 288.));
		testDragged(new Point(356., 295.));
		testDragged(new Point(351., 302.));
		testDragged(new Point(348., 304.));
		testDragged(new Point(348., 305.));
		testDragged(new Point(352., 300.));
		testDragged(new Point(353., 295.));
		testDragged(new Point(355., 292.));
		testDragged(new Point(354., 294.));
		testDragged(new Point(353., 295.));
		testDragged(new Point(352., 297.));
		testDragged(new Point(352., 298.));
		testDragged(new Point(352., 296.));
		testDragged(new Point(351., 296.));
		testDragged(new Point(350., 296.));
		testDragged(new Point(347., 297.));
		testDragged(new Point(345., 298.));
		testDragged(new Point(345., 292.));
		testDragged(new Point(345., 286.));
		testDragged(new Point(345., 283.));
		testDragged(new Point(345., 281.));
		testDragged(new Point(344., 283.));
		testDragged(new Point(342., 284.));
		testDragged(new Point(341., 284.));
		testDragged(new Point(341., 281.));
		testDragged(new Point(341., 279.));
		testDragged(new Point(341., 278.));
		testDragged(new Point(341., 284.));
		testDragged(new Point(340., 290.));
		testDragged(new Point(338., 295.));
		testDragged(new Point(338., 297.));
		testDragged(new Point(338., 295.));
		testDragged(new Point(338., 293.));
		testDragged(new Point(338., 292.));
		testDragged(new Point(338., 293.));
		testDragged(new Point(338., 294.));
		testDragged(new Point(337., 296.));
		testDragged(new Point(337., 293.));
		testDragged(new Point(337., 290.));
		testDragged(new Point(337., 289.));
		testDragged(new Point(334., 290.));
		testDragged(new Point(332., 293.));
		testDragged(new Point(330., 294.));
		testDragged(new Point(329., 294.));
		testDragged(new Point(329., 296.));
		testDragged(new Point(333., 296.));
		testDragged(new Point(336., 296.));
		testDragged(new Point(340., 296.));
		testDragged(new Point(340., 297.));
		testDragged(new Point(340., 298.));
		testDragged(new Point(341., 298.));
		testDragged(new Point(342., 298.));
		testDragged(new Point(345., 298.));
		testDragged(new Point(346., 298.));
		testDragged(new Point(348., 298.));
		testDragged(new Point(349., 298.));
		testDragged(new Point(352., 298.));
		testDragged(new Point(353., 298.));
		testDragged(new Point(355., 298.));
		testDragged(new Point(361., 298.));
		testDragged(new Point(362., 298.));
		testDragged(new Point(364., 300.));
		testDragged(new Point(364., 301.));
		testReleased();

		testPressed(new Point(350., 289.));
		testDragged(new Point(350., 290.));
		testDragged(new Point(350., 296.));
		testDragged(new Point(350., 297.));
		testDragged(new Point(349., 299.));
		testDragged(new Point(348., 300.));
		testDragged(new Point(346., 303.));
		testDragged(new Point(345., 306.));
		testDragged(new Point(343., 315.));
		testDragged(new Point(342., 318.));
		testDragged(new Point(341., 319.));
		testDragged(new Point(341., 316.));
		testDragged(new Point(341., 311.));
		testDragged(new Point(341., 305.));
		testDragged(new Point(346., 299.));
		testDragged(new Point(346., 298.));
		testDragged(new Point(347., 298.));
		testDragged(new Point(347., 300.));
		testDragged(new Point(346., 302.));
		testDragged(new Point(345., 304.));
		testDragged(new Point(345., 305.));
		testDragged(new Point(344., 305.));
		testDragged(new Point(346., 305.));
		testDragged(new Point(358., 305.));
		testDragged(new Point(358., 306.));
		testDragged(new Point(358., 310.));
		testDragged(new Point(358., 314.));
		testDragged(new Point(358., 315.));
		testDragged(new Point(358., 318.));
		testDragged(new Point(358., 322.));
		testDragged(new Point(357., 325.));
		testDragged(new Point(353., 329.));
		testDragged(new Point(352., 330.));
		testDragged(new Point(351., 331.));
		testDragged(new Point(350., 331.));
		testDragged(new Point(350., 321.));
		testDragged(new Point(350., 310.));
		testDragged(new Point(350., 303.));
		testDragged(new Point(350., 310.));
		testDragged(new Point(345., 323.));
		testDragged(new Point(342., 326.));
		testDragged(new Point(341., 329.));
		testDragged(new Point(337., 330.));
		testDragged(new Point(335., 330.));
		testDragged(new Point(335., 328.));
		testDragged(new Point(338., 317.));
		testDragged(new Point(345., 303.));
		testDragged(new Point(348., 301.));
		testDragged(new Point(352., 303.));
		testDragged(new Point(353., 310.));
		testDragged(new Point(353., 322.));
		testDragged(new Point(353., 326.));
		testDragged(new Point(353., 329.));
		testDragged(new Point(353., 330.));
		testDragged(new Point(353., 326.));
		testDragged(new Point(353., 313.));
		testDragged(new Point(353., 305.));
		testDragged(new Point(355., 302.));
		testDragged(new Point(355., 301.));
		testDragged(new Point(355., 302.));
		testDragged(new Point(348., 316.));
		testDragged(new Point(344., 317.));
		testDragged(new Point(341., 317.));
		testDragged(new Point(341., 311.));
		testDragged(new Point(341., 297.));
		testDragged(new Point(343., 292.));
		testDragged(new Point(347., 290.));
		testDragged(new Point(348., 290.));
		testDragged(new Point(352., 298.));
		testDragged(new Point(354., 302.));
		testDragged(new Point(354., 308.));
		testDragged(new Point(352., 310.));
		testDragged(new Point(345., 310.));
		testDragged(new Point(341., 310.));
		testDragged(new Point(332., 307.));
		testDragged(new Point(330., 306.));
		testDragged(new Point(329., 306.));
		testDragged(new Point(329., 305.));
		testDragged(new Point(328., 305.));
		testDragged(new Point(328., 307.));
		testDragged(new Point(331., 310.));
		testDragged(new Point(332., 311.));
		testDragged(new Point(334., 313.));
		testDragged(new Point(334., 314.));
		testDragged(new Point(331., 314.));
		testDragged(new Point(322., 311.));
		testDragged(new Point(306., 302.));
		testDragged(new Point(299., 299.));
		testDragged(new Point(293., 294.));
		testDragged(new Point(292., 294.));
		testDragged(new Point(292., 293.));
		testDragged(new Point(292., 292.));
		testDragged(new Point(302., 291.));
		testDragged(new Point(330., 287.));
		testDragged(new Point(339., 287.));
		testDragged(new Point(351., 287.));
		testDragged(new Point(356., 285.));
		testDragged(new Point(358., 285.));
		testDragged(new Point(359., 287.));
		testDragged(new Point(362., 291.));
		testDragged(new Point(362., 296.));
		testDragged(new Point(363., 298.));
		testDragged(new Point(363., 302.));
		testDragged(new Point(364., 298.));
		testDragged(new Point(368., 293.));
		testDragged(new Point(370., 292.));
		testDragged(new Point(374., 292.));
		testDragged(new Point(374., 293.));
		testDragged(new Point(374., 296.));
		testDragged(new Point(374., 300.));
		testDragged(new Point(374., 304.));
		testDragged(new Point(374., 305.));
		testDragged(new Point(374., 306.));
		testDragged(new Point(373., 306.));
		testDragged(new Point(371., 305.));
		testDragged(new Point(367., 303.));
		testDragged(new Point(360., 300.));
		testDragged(new Point(357., 299.));
		testDragged(new Point(356., 299.));
		testDragged(new Point(359., 302.));
		testDragged(new Point(362., 305.));
		testDragged(new Point(365., 308.));
		testDragged(new Point(365., 310.));
		testReleased();

		testPressed(new Point(352., 305.));
		testDragged(new Point(351., 305.));
		testDragged(new Point(348., 305.));
		testDragged(new Point(347., 305.));
		testDragged(new Point(342., 305.));
		testDragged(new Point(339., 305.));
		testDragged(new Point(336., 303.));
		testDragged(new Point(334., 302.));
		testDragged(new Point(331., 299.));
		testDragged(new Point(328., 295.));
		testDragged(new Point(328., 294.));
		testDragged(new Point(328., 290.));
		testDragged(new Point(328., 289.));
		testDragged(new Point(334., 289.));
		testDragged(new Point(336., 289.));
		testDragged(new Point(342., 294.));
		testDragged(new Point(345., 307.));
		testDragged(new Point(345., 313.));
		testDragged(new Point(345., 322.));
		testDragged(new Point(345., 324.));
		testDragged(new Point(342., 323.));
		testDragged(new Point(339., 318.));
		testDragged(new Point(334., 307.));
		testDragged(new Point(334., 304.));
		testDragged(new Point(334., 303.));
		testDragged(new Point(333., 303.));
		testDragged(new Point(328., 303.));
		testDragged(new Point(322., 303.));
		testDragged(new Point(316., 303.));
		testDragged(new Point(311., 303.));
		testDragged(new Point(308., 302.));
		testDragged(new Point(307., 299.));
		testDragged(new Point(307., 294.));
		testDragged(new Point(307., 293.));
		testDragged(new Point(308., 291.));
		testDragged(new Point(309., 289.));
		testDragged(new Point(314., 289.));
		testDragged(new Point(317., 290.));
		testDragged(new Point(324., 298.));
		testDragged(new Point(327., 302.));
		testDragged(new Point(330., 307.));
		testDragged(new Point(331., 307.));
		testDragged(new Point(328., 307.));
		testDragged(new Point(326., 307.));
		testDragged(new Point(323., 307.));
		testDragged(new Point(323., 306.));
		testDragged(new Point(328., 306.));
		testDragged(new Point(332., 306.));
		testDragged(new Point(336., 306.));
		testDragged(new Point(345., 306.));
		testDragged(new Point(346., 306.));
		testDragged(new Point(346., 307.));
		testDragged(new Point(346., 305.));
		testDragged(new Point(346., 302.));
		testDragged(new Point(347., 299.));
		testDragged(new Point(350., 297.));
		testDragged(new Point(353., 297.));
		testDragged(new Point(357., 297.));
		testDragged(new Point(359., 300.));
		testDragged(new Point(359., 303.));
		testDragged(new Point(359., 306.));
		testDragged(new Point(357., 304.));
		testDragged(new Point(356., 298.));
		testDragged(new Point(356., 295.));
		testDragged(new Point(356., 292.));
		testDragged(new Point(357., 292.));
		testDragged(new Point(358., 294.));
		testDragged(new Point(360., 295.));
		testDragged(new Point(362., 299.));
		testDragged(new Point(362., 301.));
		testDragged(new Point(362., 302.));
		testDragged(new Point(362., 304.));
		testDragged(new Point(361., 304.));
		testDragged(new Point(361., 302.));
		testDragged(new Point(361., 299.));
		testDragged(new Point(361., 294.));
		testDragged(new Point(362., 293.));
		testDragged(new Point(365., 293.));
		testDragged(new Point(366., 293.));
		testDragged(new Point(368., 298.));
		testDragged(new Point(368., 302.));
		testDragged(new Point(368., 307.));
		testDragged(new Point(368., 313.));
		testDragged(new Point(368., 318.));
		testDragged(new Point(368., 320.));
		testDragged(new Point(370., 315.));
		testDragged(new Point(370., 311.));
		testDragged(new Point(371., 308.));
		testDragged(new Point(372., 308.));
		testDragged(new Point(372., 311.));
		testDragged(new Point(372., 318.));
		testDragged(new Point(370., 323.));
		testDragged(new Point(367., 326.));
		testDragged(new Point(366., 326.));
		testDragged(new Point(365., 317.));
		testDragged(new Point(365., 311.));
		testDragged(new Point(365., 307.));
		testDragged(new Point(365., 306.));
		testDragged(new Point(365., 305.));
		testDragged(new Point(364., 305.));
		testDragged(new Point(361., 305.));
		testDragged(new Point(358., 305.));
		testDragged(new Point(356., 306.));
		testDragged(new Point(355., 306.));
		testDragged(new Point(355., 305.));
		testDragged(new Point(355., 302.));
		testDragged(new Point(355., 299.));
		testDragged(new Point(356., 299.));
		testDragged(new Point(358., 299.));
		testDragged(new Point(358., 302.));
		testDragged(new Point(358., 305.));
		testDragged(new Point(358., 307.));
		testDragged(new Point(358., 308.));
		testDragged(new Point(358., 305.));
		testDragged(new Point(358., 303.));
		testDragged(new Point(358., 300.));
		testDragged(new Point(355., 299.));
		testDragged(new Point(352., 299.));
		testDragged(new Point(348., 299.));
		testDragged(new Point(347., 299.));
		testDragged(new Point(346., 299.));
		testDragged(new Point(346., 301.));
		testDragged(new Point(346., 304.));
		testDragged(new Point(345., 309.));
		testDragged(new Point(345., 314.));
		testDragged(new Point(345., 315.));
		testDragged(new Point(346., 313.));
		testDragged(new Point(351., 310.));
		testDragged(new Point(355., 309.));
		testDragged(new Point(357., 308.));
		testDragged(new Point(359., 306.));
		testDragged(new Point(359., 310.));
		testDragged(new Point(359., 314.));
		testDragged(new Point(357., 317.));
		testDragged(new Point(355., 319.));
		testDragged(new Point(353., 319.));
		testDragged(new Point(351., 319.));
		testDragged(new Point(350., 319.));
		testDragged(new Point(349., 319.));
		testDragged(new Point(348., 319.));
		testDragged(new Point(346., 319.));
		testDragged(new Point(347., 319.));
		testDragged(new Point(350., 319.));
		testDragged(new Point(352., 319.));
		testDragged(new Point(356., 319.));
		testDragged(new Point(355., 319.));
		testDragged(new Point(350., 319.));
		testDragged(new Point(346., 319.));
		testDragged(new Point(336., 309.));
		testDragged(new Point(330., 300.));
		testDragged(new Point(323., 286.));
		testDragged(new Point(321., 281.));
		testDragged(new Point(321., 280.));
		testDragged(new Point(323., 280.));
		testDragged(new Point(330., 280.));
		testDragged(new Point(340., 280.));
		testDragged(new Point(345., 280.));
		testDragged(new Point(350., 283.));
		testDragged(new Point(351., 284.));
		testDragged(new Point(351., 285.));
		testDragged(new Point(351., 286.));
		testDragged(new Point(349., 286.));
		testDragged(new Point(346., 286.));
		testDragged(new Point(342., 286.));
		testDragged(new Point(341., 282.));
		testDragged(new Point(341., 278.));
		testDragged(new Point(341., 276.));
		testDragged(new Point(341., 273.));
		testDragged(new Point(342., 272.));
		testDragged(new Point(345., 272.));
		testDragged(new Point(347., 272.));
		testDragged(new Point(363., 275.));
		testDragged(new Point(365., 275.));
		testDragged(new Point(362., 280.));
		testDragged(new Point(355., 282.));
		testDragged(new Point(351., 285.));
		testDragged(new Point(345., 286.));
		testDragged(new Point(344., 286.));
		testDragged(new Point(344., 285.));
		testDragged(new Point(344., 282.));
		testDragged(new Point(344., 279.));
		testDragged(new Point(344., 277.));
		testDragged(new Point(345., 277.));
		testDragged(new Point(347., 277.));
		testDragged(new Point(349., 279.));
		testDragged(new Point(355., 284.));
		testDragged(new Point(361., 289.));
		testDragged(new Point(363., 289.));
		testDragged(new Point(367., 289.));
		testDragged(new Point(368., 289.));
		testDragged(new Point(366., 289.));
		testDragged(new Point(363., 289.));
		testDragged(new Point(360., 290.));
		testDragged(new Point(356., 298.));
		testDragged(new Point(351., 302.));
		testDragged(new Point(346., 305.));
		testDragged(new Point(340., 305.));
		testDragged(new Point(339., 305.));
		testDragged(new Point(339., 299.));
		testDragged(new Point(340., 293.));
		testDragged(new Point(341., 290.));
		testDragged(new Point(341., 288.));
		testDragged(new Point(338., 288.));
		testDragged(new Point(337., 288.));
		testDragged(new Point(337., 289.));
		testDragged(new Point(337., 293.));
		testDragged(new Point(337., 296.));
		testDragged(new Point(337., 297.));
		testDragged(new Point(337., 298.));
		testDragged(new Point(337., 296.));
		testDragged(new Point(339., 292.));
		testDragged(new Point(342., 290.));
		testDragged(new Point(344., 290.));
		testDragged(new Point(346., 290.));
		testDragged(new Point(347., 290.));
		testReleased();

		testPressed(new Point(364., 290.));
		testDragged(new Point(366., 290.));
		testDragged(new Point(371., 290.));
		testDragged(new Point(385., 290.));
		testDragged(new Point(391., 290.));
		testDragged(new Point(400., 290.));
		testDragged(new Point(405., 290.));
		testDragged(new Point(406., 290.));
		testDragged(new Point(405., 290.));
		testDragged(new Point(400., 289.));
		testDragged(new Point(394., 289.));
		testDragged(new Point(382., 289.));
		testDragged(new Point(377., 289.));
		testDragged(new Point(373., 289.));
		testDragged(new Point(372., 290.));
		testDragged(new Point(372., 294.));
		testDragged(new Point(374., 295.));
		testDragged(new Point(379., 297.));
		testDragged(new Point(389., 299.));
		testDragged(new Point(395., 300.));
		testDragged(new Point(400., 302.));
		testDragged(new Point(403., 303.));
		testDragged(new Point(404., 304.));
		testDragged(new Point(398., 306.));
		testDragged(new Point(386., 308.));
		testDragged(new Point(375., 310.));
		testDragged(new Point(370., 310.));
		testDragged(new Point(366., 310.));
		testDragged(new Point(365., 309.));
		testDragged(new Point(365., 308.));
		testDragged(new Point(365., 301.));
		testDragged(new Point(370., 294.));
		testDragged(new Point(374., 294.));
		testDragged(new Point(376., 294.));
		testDragged(new Point(376., 296.));
		testDragged(new Point(376., 301.));
		testDragged(new Point(376., 305.));
		testDragged(new Point(370., 311.));
		testDragged(new Point(370., 312.));
		testDragged(new Point(369., 312.));
		testDragged(new Point(369., 304.));
		testDragged(new Point(369., 298.));
		testDragged(new Point(369., 295.));
		testDragged(new Point(369., 294.));
		testDragged(new Point(369., 293.));
		testDragged(new Point(369., 296.));
		testDragged(new Point(368., 302.));
		testDragged(new Point(367., 311.));
		testDragged(new Point(363., 318.));
		testDragged(new Point(363., 320.));
		testDragged(new Point(363., 310.));
		testDragged(new Point(364., 301.));
		testDragged(new Point(366., 297.));
		testDragged(new Point(367., 294.));
		testDragged(new Point(367., 293.));
		testDragged(new Point(364., 295.));
		testDragged(new Point(357., 297.));
		testDragged(new Point(348., 298.));
		testDragged(new Point(346., 298.));
		testDragged(new Point(343., 296.));
		testDragged(new Point(341., 292.));
		testDragged(new Point(341., 287.));
		testDragged(new Point(341., 277.));
		testDragged(new Point(341., 271.));
		testDragged(new Point(341., 268.));
		testDragged(new Point(340., 268.));
		testDragged(new Point(338., 271.));
		testDragged(new Point(335., 274.));
		testDragged(new Point(332., 279.));
		testDragged(new Point(329., 286.));
		testDragged(new Point(329., 287.));
		testDragged(new Point(329., 288.));
		testDragged(new Point(332., 287.));
		testDragged(new Point(336., 282.));
		testDragged(new Point(341., 278.));
		testDragged(new Point(341., 277.));
		testDragged(new Point(339., 278.));
		testDragged(new Point(334., 283.));
		testDragged(new Point(320., 292.));
		testDragged(new Point(313., 299.));
		testDragged(new Point(307., 307.));
		testDragged(new Point(306., 308.));
		testDragged(new Point(304., 308.));
		testDragged(new Point(304., 300.));
		testDragged(new Point(304., 287.));
		testDragged(new Point(304., 284.));
		testDragged(new Point(304., 282.));
		testDragged(new Point(303., 281.));
		testDragged(new Point(298., 281.));
		testDragged(new Point(296., 281.));
		testDragged(new Point(292., 281.));
		testDragged(new Point(293., 281.));
		testDragged(new Point(298., 279.));
		testDragged(new Point(302., 279.));
		testDragged(new Point(304., 279.));
		testDragged(new Point(306., 282.));
		testDragged(new Point(307., 286.));
		testDragged(new Point(307., 305.));
		testDragged(new Point(307., 314.));
		testDragged(new Point(307., 321.));
		testDragged(new Point(307., 322.));
		testDragged(new Point(308., 322.));
		testDragged(new Point(315., 316.));
		testDragged(new Point(321., 301.));
		testDragged(new Point(327., 291.));
		testDragged(new Point(328., 284.));
		testDragged(new Point(326., 284.));
		testDragged(new Point(321., 284.));
		testDragged(new Point(319., 284.));
		testDragged(new Point(314., 292.));
		testDragged(new Point(312., 301.));
		testDragged(new Point(316., 312.));
		testDragged(new Point(325., 316.));
		testDragged(new Point(345., 318.));
		testDragged(new Point(351., 318.));
		testDragged(new Point(364., 303.));
		testDragged(new Point(369., 290.));
		testDragged(new Point(370., 287.));
		testDragged(new Point(368., 298.));
		testDragged(new Point(366., 309.));
		testDragged(new Point(365., 315.));
		testDragged(new Point(363., 321.));
		testDragged(new Point(363., 316.));
		testDragged(new Point(363., 310.));
		testDragged(new Point(363., 305.));
		testDragged(new Point(362., 304.));
		testDragged(new Point(361., 304.));
		testDragged(new Point(354., 309.));
		testDragged(new Point(347., 312.));
		testDragged(new Point(343., 315.));
		testDragged(new Point(336., 315.));
		testDragged(new Point(338., 305.));
		testDragged(new Point(342., 293.));
		testDragged(new Point(346., 288.));
		testDragged(new Point(347., 288.));
		testDragged(new Point(347., 287.));
		testDragged(new Point(346., 290.));
		testDragged(new Point(338., 297.));
		testDragged(new Point(335., 299.));
		testDragged(new Point(334., 300.));
		testDragged(new Point(334., 292.));
		testDragged(new Point(335., 285.));
		testDragged(new Point(336., 281.));
		testDragged(new Point(336., 279.));
		testDragged(new Point(332., 279.));
		testDragged(new Point(329., 279.));
		testDragged(new Point(325., 285.));
		testDragged(new Point(324., 287.));
		testDragged(new Point(324., 289.));
		testDragged(new Point(324., 290.));
		testDragged(new Point(324., 295.));
		testDragged(new Point(324., 296.));
		testDragged(new Point(324., 297.));
		testDragged(new Point(324., 293.));
		testDragged(new Point(324., 290.));
		testDragged(new Point(324., 289.));
		testDragged(new Point(324., 291.));
		testDragged(new Point(324., 293.));
		testDragged(new Point(324., 294.));
		testDragged(new Point(324., 300.));
		testDragged(new Point(325., 302.));
		testDragged(new Point(335., 305.));
		testDragged(new Point(338., 305.));
		testDragged(new Point(340., 305.));
		testDragged(new Point(341., 305.));
		testDragged(new Point(344., 305.));
		testDragged(new Point(346., 305.));
		testDragged(new Point(349., 305.));
		testDragged(new Point(351., 305.));
		testDragged(new Point(353., 305.));
		testDragged(new Point(354., 305.));
		testDragged(new Point(356., 305.));
		testDragged(new Point(361., 305.));
		testDragged(new Point(365., 305.));
		testReleased();

		testPressed(new Point(365., 305.));
		testDragged(new Point(365., 307.));
		testDragged(new Point(365., 310.));
		testDragged(new Point(364., 316.));
		testDragged(new Point(361., 329.));
		testDragged(new Point(360., 332.));
		testDragged(new Point(356., 338.));
		testDragged(new Point(354., 340.));
		testDragged(new Point(353., 341.));
		testDragged(new Point(352., 340.));
		testDragged(new Point(350., 334.));
		testDragged(new Point(350., 301.));
		testDragged(new Point(350., 288.));
		testDragged(new Point(350., 287.));
		testDragged(new Point(350., 286.));
		testDragged(new Point(345., 288.));
		testDragged(new Point(341., 293.));
		testDragged(new Point(340., 295.));
		testDragged(new Point(343., 295.));
		testDragged(new Point(347., 292.));
		testDragged(new Point(365., 289.));
		testDragged(new Point(373., 289.));
		testDragged(new Point(377., 291.));
		testDragged(new Point(377., 295.));
		testDragged(new Point(360., 306.));
		testDragged(new Point(345., 310.));
		testDragged(new Point(328., 312.));
		testDragged(new Point(322., 312.));
		testDragged(new Point(319., 310.));
		testDragged(new Point(317., 306.));
		testDragged(new Point(317., 299.));
		testDragged(new Point(317., 291.));
		testDragged(new Point(319., 282.));
		testDragged(new Point(320., 281.));
		testDragged(new Point(320., 280.));
		testDragged(new Point(311., 280.));
		testDragged(new Point(295., 287.));
		testDragged(new Point(291., 291.));
		testDragged(new Point(288., 292.));
		testDragged(new Point(295., 288.));
		testDragged(new Point(305., 282.));
		testDragged(new Point(319., 274.));
		testDragged(new Point(321., 274.));
		testDragged(new Point(324., 275.));
		testDragged(new Point(324., 279.));
		testDragged(new Point(318., 293.));
		testDragged(new Point(314., 305.));
		testDragged(new Point(311., 314.));
		testDragged(new Point(316., 297.));
		testDragged(new Point(325., 282.));
		testDragged(new Point(329., 271.));
		testDragged(new Point(330., 266.));
		testDragged(new Point(328., 266.));
		testDragged(new Point(327., 268.));
		testDragged(new Point(319., 279.));
		testDragged(new Point(316., 285.));
		testDragged(new Point(315., 286.));
		testDragged(new Point(319., 281.));
		testDragged(new Point(323., 276.));
		testDragged(new Point(325., 274.));
		testDragged(new Point(326., 272.));
		testDragged(new Point(326., 275.));
		testDragged(new Point(326., 286.));
		testDragged(new Point(325., 295.));
		testDragged(new Point(322., 308.));
		testDragged(new Point(322., 311.));
		testDragged(new Point(328., 301.));
		testDragged(new Point(334., 289.));
		testDragged(new Point(337., 284.));
		testDragged(new Point(340., 280.));
		testDragged(new Point(340., 283.));
		testDragged(new Point(340., 289.));
		testDragged(new Point(338., 302.));
		testDragged(new Point(338., 304.));
		testDragged(new Point(337., 305.));
		testDragged(new Point(335., 307.));
		testDragged(new Point(335., 295.));
		testDragged(new Point(336., 283.));
		testDragged(new Point(340., 271.));
		testDragged(new Point(343., 267.));
		testDragged(new Point(344., 262.));
		testDragged(new Point(344., 263.));
		testDragged(new Point(344., 268.));
		testDragged(new Point(336., 291.));
		testDragged(new Point(330., 314.));
		testDragged(new Point(327., 319.));
		testDragged(new Point(327., 321.));
		testDragged(new Point(330., 309.));
		testDragged(new Point(334., 292.));
		testDragged(new Point(338., 280.));
		testDragged(new Point(339., 277.));
		testDragged(new Point(341., 275.));
		testDragged(new Point(341., 273.));
		testDragged(new Point(341., 275.));
		testDragged(new Point(340., 283.));
		testDragged(new Point(338., 292.));
		testDragged(new Point(338., 298.));
		testDragged(new Point(337., 301.));
		testDragged(new Point(337., 302.));
		testDragged(new Point(339., 292.));
		testDragged(new Point(345., 277.));
		testDragged(new Point(349., 271.));
		testDragged(new Point(352., 266.));
		testDragged(new Point(352., 264.));
		testDragged(new Point(353., 264.));
		testDragged(new Point(353., 267.));
		testDragged(new Point(349., 275.));
		testDragged(new Point(345., 290.));
		testDragged(new Point(339., 308.));
		testDragged(new Point(339., 310.));
		testDragged(new Point(339., 312.));
		testDragged(new Point(342., 301.));
		testDragged(new Point(346., 290.));
		testDragged(new Point(354., 275.));
		testDragged(new Point(354., 274.));
		testDragged(new Point(356., 274.));
		testDragged(new Point(356., 277.));
		testDragged(new Point(355., 286.));
		testDragged(new Point(353., 294.));
		testDragged(new Point(348., 304.));
		testDragged(new Point(348., 307.));
		testDragged(new Point(347., 308.));
		testDragged(new Point(353., 294.));
		testDragged(new Point(359., 279.));
		testDragged(new Point(367., 267.));
		testDragged(new Point(369., 262.));
		testDragged(new Point(370., 261.));
		testDragged(new Point(371., 261.));
		testDragged(new Point(368., 266.));
		testDragged(new Point(364., 275.));
		testDragged(new Point(360., 287.));
		testDragged(new Point(357., 294.));
		testDragged(new Point(355., 298.));
		testDragged(new Point(354., 300.));
		testDragged(new Point(358., 296.));
		testDragged(new Point(364., 283.));
		testDragged(new Point(372., 271.));
		testDragged(new Point(377., 266.));
		testDragged(new Point(378., 264.));
		testDragged(new Point(379., 262.));
		testDragged(new Point(378., 262.));
		testDragged(new Point(373., 268.));
		testDragged(new Point(366., 283.));
		testDragged(new Point(360., 295.));
		testDragged(new Point(351., 311.));
		testDragged(new Point(350., 316.));
		testDragged(new Point(349., 317.));
		testDragged(new Point(350., 317.));
		testDragged(new Point(353., 313.));
		testDragged(new Point(356., 309.));
		testDragged(new Point(359., 306.));
		testDragged(new Point(359., 305.));
		testDragged(new Point(359., 306.));
		testDragged(new Point(357., 309.));
		testDragged(new Point(354., 312.));
		testDragged(new Point(351., 315.));
		testDragged(new Point(347., 318.));
		testDragged(new Point(347., 310.));
		testDragged(new Point(350., 295.));
		testDragged(new Point(352., 289.));
		testDragged(new Point(352., 286.));
		testDragged(new Point(352., 288.));
		testDragged(new Point(351., 293.));
		testDragged(new Point(348., 299.));
		testDragged(new Point(345., 304.));
		testDragged(new Point(342., 308.));
		testDragged(new Point(338., 314.));
		testDragged(new Point(337., 315.));
		testDragged(new Point(337., 316.));
		testDragged(new Point(337., 315.));
		testDragged(new Point(339., 304.));
		testDragged(new Point(343., 290.));
		testDragged(new Point(347., 272.));
		testDragged(new Point(348., 268.));
		testDragged(new Point(346., 271.));
		testDragged(new Point(333., 290.));
		testDragged(new Point(323., 302.));
		testDragged(new Point(298., 319.));
		testDragged(new Point(297., 319.));
		testDragged(new Point(297., 320.));
		testDragged(new Point(300., 313.));
		testDragged(new Point(320., 278.));
		testDragged(new Point(324., 269.));
		testDragged(new Point(326., 265.));
		testDragged(new Point(326., 264.));
		testDragged(new Point(323., 266.));
		testDragged(new Point(316., 273.));
		testDragged(new Point(311., 280.));
		testDragged(new Point(307., 289.));
		testDragged(new Point(303., 295.));
		testDragged(new Point(303., 296.));
		testDragged(new Point(305., 297.));
		testDragged(new Point(319., 297.));
		testDragged(new Point(330., 293.));
		testDragged(new Point(331., 292.));
		testDragged(new Point(332., 292.));
		testDragged(new Point(334., 292.));
		testDragged(new Point(334., 295.));
		testDragged(new Point(334., 308.));
		testReleased();

		testPressed(new Point(334., 308.));
		testDragged(new Point(334., 309.));
		testDragged(new Point(335., 310.));
		testDragged(new Point(347., 312.));
		testDragged(new Point(352., 313.));
		testDragged(new Point(359., 313.));
		testDragged(new Point(359., 314.));
		testDragged(new Point(342., 316.));
		testDragged(new Point(323., 316.));
		testDragged(new Point(304., 316.));
		testDragged(new Point(268., 315.));
		testDragged(new Point(266., 315.));
		testDragged(new Point(268., 314.));
		testDragged(new Point(286., 309.));
		testDragged(new Point(300., 309.));
		testDragged(new Point(324., 309.));
		testDragged(new Point(349., 312.));
		testDragged(new Point(368., 314.));
		testDragged(new Point(374., 314.));
		testDragged(new Point(377., 317.));
		testDragged(new Point(345., 317.));
		testDragged(new Point(315., 317.));
		testDragged(new Point(309., 317.));
		testDragged(new Point(305., 317.));
		testDragged(new Point(323., 325.));
		testDragged(new Point(354., 346.));
		testDragged(new Point(378., 365.));
		testDragged(new Point(395., 379.));
		testDragged(new Point(395., 381.));
		testDragged(new Point(381., 383.));
		testDragged(new Point(351., 383.));
		testDragged(new Point(303., 363.));
		testDragged(new Point(288., 348.));
		testDragged(new Point(282., 341.));
		testDragged(new Point(279., 338.));
		testDragged(new Point(279., 332.));
		testDragged(new Point(285., 322.));
		testDragged(new Point(351., 314.));
		testDragged(new Point(371., 317.));
		testDragged(new Point(387., 323.));
		testDragged(new Point(382., 323.));
		testDragged(new Point(367., 320.));
		testDragged(new Point(327., 300.));
		testDragged(new Point(311., 280.));
		testDragged(new Point(311., 274.));
		testDragged(new Point(313., 261.));
		testDragged(new Point(318., 258.));
		testDragged(new Point(331., 262.));
		testDragged(new Point(333., 274.));
		testDragged(new Point(333., 282.));
		testDragged(new Point(328., 289.));
		testDragged(new Point(314., 300.));
		testDragged(new Point(303., 304.));
		testDragged(new Point(302., 304.));
		testDragged(new Point(300., 292.));
		testDragged(new Point(301., 267.));
		testDragged(new Point(303., 263.));
		testDragged(new Point(307., 274.));
		testDragged(new Point(307., 285.));
		testDragged(new Point(306., 298.));
		testDragged(new Point(302., 305.));
		testDragged(new Point(302., 306.));
		testDragged(new Point(304., 303.));
		testDragged(new Point(311., 296.));
		testDragged(new Point(317., 282.));
		testDragged(new Point(328., 262.));
		testDragged(new Point(329., 261.));
		testDragged(new Point(331., 261.));
		testDragged(new Point(319., 281.));
		testDragged(new Point(310., 300.));
		testDragged(new Point(307., 307.));
		testDragged(new Point(305., 313.));
		testDragged(new Point(307., 308.));
		testDragged(new Point(314., 300.));
		testDragged(new Point(322., 293.));
		testDragged(new Point(328., 287.));
		testDragged(new Point(329., 286.));
		testDragged(new Point(327., 291.));
		testDragged(new Point(322., 295.));
		testDragged(new Point(318., 305.));
		testDragged(new Point(317., 311.));
		testDragged(new Point(316., 312.));
		testDragged(new Point(316., 313.));
		testDragged(new Point(321., 305.));
		testDragged(new Point(332., 292.));
		testDragged(new Point(339., 282.));
		testDragged(new Point(344., 278.));
		testDragged(new Point(346., 275.));
		testDragged(new Point(346., 276.));
		testDragged(new Point(339., 284.));
		testDragged(new Point(331., 292.));
		testDragged(new Point(330., 293.));
		testDragged(new Point(329., 294.));
		testDragged(new Point(332., 294.));
		testDragged(new Point(338., 292.));
		testDragged(new Point(348., 285.));
		testDragged(new Point(351., 284.));
		testDragged(new Point(351., 285.));
		testDragged(new Point(351., 290.));
		testDragged(new Point(348., 298.));
		testDragged(new Point(331., 336.));
		testDragged(new Point(322., 348.));
		testDragged(new Point(319., 355.));
		testDragged(new Point(318., 356.));
		testDragged(new Point(327., 342.));
		testDragged(new Point(346., 307.));
		testDragged(new Point(360., 281.));
		testDragged(new Point(375., 244.));
		testDragged(new Point(379., 234.));
		testDragged(new Point(380., 234.));
		testDragged(new Point(380., 233.));
		testDragged(new Point(378., 238.));
		testDragged(new Point(367., 252.));
		testDragged(new Point(356., 266.));
		testDragged(new Point(354., 268.));
		testDragged(new Point(354., 269.));
		testDragged(new Point(359., 266.));
		testDragged(new Point(366., 258.));
		testDragged(new Point(371., 253.));
		testDragged(new Point(372., 253.));
		testDragged(new Point(370., 258.));
		testDragged(new Point(367., 267.));
		testDragged(new Point(357., 291.));
		testDragged(new Point(355., 294.));
		testDragged(new Point(354., 296.));
		testDragged(new Point(358., 289.));
		testDragged(new Point(361., 284.));
		testDragged(new Point(364., 281.));
		testDragged(new Point(364., 280.));
		testDragged(new Point(365., 280.));
		testDragged(new Point(365., 283.));
		testDragged(new Point(365., 297.));
		testDragged(new Point(360., 304.));
		testDragged(new Point(356., 309.));
		testDragged(new Point(352., 318.));
		testDragged(new Point(361., 312.));
		testDragged(new Point(371., 302.));
		testDragged(new Point(380., 288.));
		testDragged(new Point(382., 285.));
		testDragged(new Point(382., 288.));
		testDragged(new Point(381., 291.));
		testDragged(new Point(376., 300.));
		testDragged(new Point(375., 304.));
		testDragged(new Point(373., 304.));
		testDragged(new Point(375., 302.));
		testDragged(new Point(377., 298.));
		testDragged(new Point(379., 292.));
		testDragged(new Point(379., 296.));
		testDragged(new Point(377., 300.));
		testDragged(new Point(376., 305.));
		testDragged(new Point(366., 317.));
		testDragged(new Point(363., 319.));
		testDragged(new Point(362., 321.));
		testDragged(new Point(362., 316.));
		testDragged(new Point(362., 314.));
		testDragged(new Point(362., 311.));
		testDragged(new Point(357., 307.));
		testDragged(new Point(353., 307.));
		testDragged(new Point(341., 305.));
		testDragged(new Point(335., 305.));
		testDragged(new Point(333., 305.));
		testDragged(new Point(327., 304.));
		testDragged(new Point(322., 304.));
		testDragged(new Point(319., 303.));
		testDragged(new Point(318., 303.));
		testDragged(new Point(318., 301.));
		testDragged(new Point(322., 301.));
		testDragged(new Point(327., 301.));
		testDragged(new Point(335., 301.));
		testDragged(new Point(355., 302.));
		testDragged(new Point(361., 302.));
		testDragged(new Point(362., 302.));
		testDragged(new Point(352., 302.));
		testDragged(new Point(334., 299.));
		testDragged(new Point(329., 296.));
		testDragged(new Point(323., 288.));
		testDragged(new Point(323., 287.));
		testDragged(new Point(323., 284.));
		testDragged(new Point(323., 283.));
		testDragged(new Point(325., 283.));
		testDragged(new Point(325., 282.));
		testDragged(new Point(325., 281.));
		testDragged(new Point(325., 277.));
		testDragged(new Point(327., 273.));
		testDragged(new Point(332., 273.));
		testDragged(new Point(339., 273.));
		testDragged(new Point(341., 274.));
		testDragged(new Point(341., 276.));
		testDragged(new Point(342., 276.));
		testDragged(new Point(336., 271.));
		testDragged(new Point(333., 267.));
		testDragged(new Point(332., 266.));
		testDragged(new Point(332., 265.));
		testDragged(new Point(333., 265.));
		testDragged(new Point(345., 265.));
		testDragged(new Point(347., 265.));
		testDragged(new Point(348., 265.));
		testDragged(new Point(352., 265.));
		testDragged(new Point(353., 265.));
		testDragged(new Point(354., 265.));
		testDragged(new Point(355., 265.));
		testDragged(new Point(355., 266.));
		testDragged(new Point(355., 273.));
		testDragged(new Point(355., 284.));
		testDragged(new Point(353., 287.));
		testDragged(new Point(353., 288.));
		testDragged(new Point(352., 288.));
		testReleased();

		testPressed(new Point(352., 288.));
		testDragged(new Point(351., 288.));
		testDragged(new Point(350., 288.));
		testDragged(new Point(346., 288.));
		testDragged(new Point(339., 288.));
		testDragged(new Point(335., 291.));
		testDragged(new Point(324., 310.));
		testDragged(new Point(320., 323.));
		testDragged(new Point(320., 326.));
		testDragged(new Point(324., 330.));
		testDragged(new Point(351., 330.));
		testDragged(new Point(368., 326.));
		testDragged(new Point(388., 312.));
		testDragged(new Point(388., 311.));
		testDragged(new Point(385., 311.));
		testDragged(new Point(341., 321.));
		testDragged(new Point(319., 335.));
		testDragged(new Point(311., 342.));
		testDragged(new Point(304., 358.));
		testDragged(new Point(304., 360.));
		testDragged(new Point(311., 352.));
		testDragged(new Point(322., 334.));
		testDragged(new Point(335., 302.));
		testDragged(new Point(337., 300.));
		testDragged(new Point(327., 312.));
		testDragged(new Point(314., 327.));
		testDragged(new Point(311., 334.));
		testDragged(new Point(308., 338.));
		testDragged(new Point(307., 341.));
		testDragged(new Point(312., 332.));
		testDragged(new Point(319., 318.));
		testDragged(new Point(321., 317.));
		testDragged(new Point(321., 316.));
		testDragged(new Point(310., 326.));
		testDragged(new Point(303., 331.));
		testDragged(new Point(295., 337.));
		testDragged(new Point(294., 338.));
		testDragged(new Point(294., 339.));
		testDragged(new Point(292., 339.));
		testDragged(new Point(287., 339.));
		testDragged(new Point(279., 339.));
		testDragged(new Point(273., 339.));
		testDragged(new Point(272., 339.));
		testDragged(new Point(271., 339.));
		testDragged(new Point(271., 341.));
		testDragged(new Point(271., 345.));
		testDragged(new Point(279., 359.));
		testDragged(new Point(294., 374.));
		testDragged(new Point(298., 376.));
		testDragged(new Point(299., 376.));
		testDragged(new Point(299., 377.));
		testDragged(new Point(299., 371.));
		testDragged(new Point(298., 357.));
		testDragged(new Point(296., 346.));
		testDragged(new Point(295., 338.));
		testDragged(new Point(295., 336.));
		testDragged(new Point(293., 336.));
		testDragged(new Point(292., 336.));
		testDragged(new Point(295., 339.));
		testDragged(new Point(301., 346.));
		testDragged(new Point(308., 355.));
		testDragged(new Point(310., 357.));
		testDragged(new Point(310., 358.));
		testDragged(new Point(311., 358.));
		testDragged(new Point(308., 358.));
		testDragged(new Point(301., 354.));
		testDragged(new Point(289., 343.));
		testDragged(new Point(271., 332.));
		testDragged(new Point(258., 322.));
		testDragged(new Point(251., 316.));
		testDragged(new Point(246., 312.));
		testDragged(new Point(244., 312.));
		testDragged(new Point(251., 316.));
		testDragged(new Point(266., 322.));
		testDragged(new Point(295., 337.));
		testDragged(new Point(305., 340.));
		testDragged(new Point(306., 340.));
		testDragged(new Point(304., 334.));
		testDragged(new Point(296., 324.));
		testDragged(new Point(286., 314.));
		testDragged(new Point(271., 287.));
		testDragged(new Point(267., 281.));
		testDragged(new Point(267., 280.));
		testDragged(new Point(272., 282.));
		testDragged(new Point(282., 290.));
		testDragged(new Point(289., 297.));
		testDragged(new Point(294., 302.));
		testDragged(new Point(297., 305.));
		testDragged(new Point(298., 306.));
		testDragged(new Point(297., 305.));
		testDragged(new Point(295., 303.));
		testDragged(new Point(295., 306.));
		testDragged(new Point(295., 311.));
		testDragged(new Point(296., 313.));
		testDragged(new Point(297., 316.));
		testDragged(new Point(298., 317.));
		testDragged(new Point(301., 317.));
		testDragged(new Point(301., 316.));
		testDragged(new Point(301., 315.));
		testReleased();

		testPressed(new Point(301., 315.));
		testDragged(new Point(301., 316.));
		testDragged(new Point(301., 319.));
		testDragged(new Point(301., 326.));
		testDragged(new Point(301., 334.));
		testDragged(new Point(308., 340.));
		testDragged(new Point(326., 344.));
		testDragged(new Point(340., 344.));
		testDragged(new Point(346., 344.));
		testDragged(new Point(347., 342.));
		testDragged(new Point(347., 317.));
		testDragged(new Point(347., 313.));
		testDragged(new Point(347., 312.));
		testDragged(new Point(347., 314.));
		testDragged(new Point(347., 320.));
		testDragged(new Point(347., 332.));
		testDragged(new Point(347., 341.));
		testDragged(new Point(347., 348.));
		testDragged(new Point(347., 350.));
		testDragged(new Point(345., 351.));
		testDragged(new Point(345., 355.));
		testDragged(new Point(344., 362.));
		testDragged(new Point(344., 365.));
		testDragged(new Point(341., 369.));
		testDragged(new Point(341., 370.));
		testDragged(new Point(341., 371.));
		testDragged(new Point(335., 375.));
		testDragged(new Point(327., 379.));
		testDragged(new Point(326., 381.));
		testDragged(new Point(324., 382.));
		testDragged(new Point(318., 382.));
		testDragged(new Point(316., 382.));
		testDragged(new Point(313., 382.));
		testDragged(new Point(311., 382.));
		testDragged(new Point(308., 382.));
		testDragged(new Point(305., 379.));
		testDragged(new Point(304., 375.));
		testDragged(new Point(304., 374.));
		testDragged(new Point(310., 372.));
		testDragged(new Point(324., 372.));
		testDragged(new Point(341., 372.));
		testDragged(new Point(352., 372.));
		testDragged(new Point(362., 373.));
		testDragged(new Point(364., 373.));
		testDragged(new Point(365., 373.));
		testDragged(new Point(359., 373.));
		testDragged(new Point(344., 368.));
		testDragged(new Point(332., 359.));
		testDragged(new Point(319., 351.));
		testDragged(new Point(309., 343.));
		testDragged(new Point(305., 340.));
		testDragged(new Point(298., 339.));
		testDragged(new Point(297., 339.));
		testDragged(new Point(297., 344.));
		testDragged(new Point(299., 349.));
		testDragged(new Point(317., 356.));
		testDragged(new Point(348., 360.));
		testDragged(new Point(353., 361.));
		testDragged(new Point(356., 361.));
		testDragged(new Point(348., 361.));
		testDragged(new Point(338., 359.));
		testDragged(new Point(332., 357.));
		testDragged(new Point(327., 357.));
		testDragged(new Point(326., 357.));
		testDragged(new Point(326., 358.));
		testDragged(new Point(327., 361.));
		testDragged(new Point(330., 364.));
		testDragged(new Point(336., 367.));
		testDragged(new Point(336., 368.));
		testDragged(new Point(333., 368.));
		testDragged(new Point(325., 368.));
		testDragged(new Point(319., 368.));
		testDragged(new Point(308., 368.));
		testDragged(new Point(304., 366.));
		testDragged(new Point(303., 366.));
		testDragged(new Point(307., 366.));
		testDragged(new Point(314., 367.));
		testDragged(new Point(323., 369.));
		testDragged(new Point(332., 370.));
		testDragged(new Point(333., 370.));
		testDragged(new Point(330., 370.));
		testDragged(new Point(323., 368.));
		testDragged(new Point(310., 361.));
		testDragged(new Point(302., 357.));
		testDragged(new Point(301., 355.));
		testDragged(new Point(304., 357.));
		testDragged(new Point(313., 361.));
		testDragged(new Point(333., 364.));
		testDragged(new Point(340., 366.));
		testDragged(new Point(342., 366.));
		testDragged(new Point(333., 366.));
		testDragged(new Point(322., 363.));
		testDragged(new Point(294., 339.));
		testDragged(new Point(292., 337.));
		testDragged(new Point(292., 336.));
		testDragged(new Point(292., 335.));
		testDragged(new Point(308., 335.));
		testDragged(new Point(342., 339.));
		testDragged(new Point(405., 354.));
		testDragged(new Point(424., 357.));
		testDragged(new Point(426., 357.));
		testDragged(new Point(419., 343.));
		testDragged(new Point(405., 321.));
		testDragged(new Point(401., 317.));
		testDragged(new Point(406., 334.));
		testDragged(new Point(415., 349.));
		testDragged(new Point(420., 359.));
		testDragged(new Point(420., 360.));
		testDragged(new Point(414., 360.));
		testDragged(new Point(397., 360.));
		testDragged(new Point(380., 360.));
		testDragged(new Point(368., 360.));
		testDragged(new Point(361., 360.));
		testDragged(new Point(359., 360.));
		testDragged(new Point(358., 360.));
		testDragged(new Point(361., 360.));
		testDragged(new Point(370., 362.));
		testDragged(new Point(379., 365.));
		testDragged(new Point(386., 370.));
		testDragged(new Point(387., 371.));
		testDragged(new Point(379., 367.));
		testDragged(new Point(367., 361.));
		testDragged(new Point(359., 355.));
		testDragged(new Point(358., 355.));
		testDragged(new Point(361., 357.));
		testDragged(new Point(372., 362.));
		testDragged(new Point(404., 380.));
		testDragged(new Point(408., 380.));
		testDragged(new Point(397., 375.));
		testDragged(new Point(363., 359.));
		testDragged(new Point(346., 352.));
		testDragged(new Point(344., 352.));
		testDragged(new Point(347., 357.));
		testDragged(new Point(360., 368.));
		testDragged(new Point(370., 375.));
		testDragged(new Point(370., 376.));
		testDragged(new Point(364., 376.));
		testDragged(new Point(354., 373.));
		testDragged(new Point(347., 368.));
		testDragged(new Point(333., 359.));
		testDragged(new Point(329., 358.));
		testDragged(new Point(328., 358.));
		testDragged(new Point(328., 361.));
		testDragged(new Point(329., 365.));
		testDragged(new Point(331., 371.));
		testDragged(new Point(333., 374.));
		testDragged(new Point(333., 375.));
		testDragged(new Point(333., 372.));
		testDragged(new Point(333., 371.));
		testDragged(new Point(333., 369.));
		testDragged(new Point(333., 371.));
		testDragged(new Point(333., 372.));
		testDragged(new Point(334., 375.));
		testDragged(new Point(336., 377.));
		testDragged(new Point(336., 375.));
		testDragged(new Point(336., 371.));
		testReleased();

		testPressed(new Point(326., 337.));
		testDragged(new Point(326., 340.));
		testDragged(new Point(325., 343.));
		testDragged(new Point(318., 345.));
		testDragged(new Point(312., 345.));
		testDragged(new Point(308., 336.));
		testDragged(new Point(308., 323.));
		testDragged(new Point(308., 317.));
		testDragged(new Point(328., 307.));
		testDragged(new Point(355., 318.));
		testDragged(new Point(379., 351.));
		testDragged(new Point(387., 375.));
		testDragged(new Point(387., 378.));
		testDragged(new Point(387., 380.));
		testDragged(new Point(375., 357.));
		testDragged(new Point(375., 353.));
		testDragged(new Point(375., 352.));
		testDragged(new Point(375., 362.));
		testDragged(new Point(376., 373.));
		testDragged(new Point(379., 382.));
		testDragged(new Point(377., 375.));
		testDragged(new Point(373., 366.));
		testDragged(new Point(371., 360.));
		testDragged(new Point(371., 356.));
		testDragged(new Point(371., 357.));
		testDragged(new Point(371., 363.));
		testDragged(new Point(362., 370.));
		testDragged(new Point(354., 370.));
		testDragged(new Point(350., 370.));
		testDragged(new Point(341., 354.));
		testDragged(new Point(339., 337.));
		testDragged(new Point(342., 309.));
		testDragged(new Point(348., 294.));
		testDragged(new Point(348., 292.));
		testDragged(new Point(348., 302.));
		testDragged(new Point(348., 310.));
		testDragged(new Point(347., 321.));
		testDragged(new Point(347., 323.));
		testDragged(new Point(346., 325.));
		testDragged(new Point(346., 313.));
		testDragged(new Point(347., 301.));
		testDragged(new Point(350., 297.));
		testDragged(new Point(351., 295.));
		testDragged(new Point(351., 298.));
		testDragged(new Point(351., 304.));
		testDragged(new Point(349., 311.));
		testDragged(new Point(337., 319.));
		testDragged(new Point(326., 324.));
		testDragged(new Point(318., 324.));
		testDragged(new Point(306., 324.));
		testDragged(new Point(298., 313.));
		testDragged(new Point(295., 291.));
		testDragged(new Point(295., 272.));
		testDragged(new Point(294., 260.));
		testDragged(new Point(292., 249.));
		testDragged(new Point(288., 248.));
		testDragged(new Point(285., 248.));
		testDragged(new Point(278., 251.));
		testDragged(new Point(273., 261.));
		testDragged(new Point(272., 281.));
		testDragged(new Point(272., 287.));
		testDragged(new Point(272., 291.));
		testDragged(new Point(274., 294.));
		testDragged(new Point(288., 294.));
		testDragged(new Point(302., 290.));
		testDragged(new Point(304., 290.));
		testDragged(new Point(300., 296.));
		testDragged(new Point(292., 305.));
		testDragged(new Point(278., 330.));
		testDragged(new Point(269., 349.));
		testDragged(new Point(267., 353.));
		testDragged(new Point(266., 354.));
		testDragged(new Point(274., 333.));
		testReleased();

		testPressed(new Point(296., 365.));
		testDragged(new Point(303., 348.));
		testDragged(new Point(314., 325.));
		testDragged(new Point(336., 300.));
		testDragged(new Point(337., 299.));
		testDragged(new Point(339., 301.));
		testDragged(new Point(341., 309.));
		testDragged(new Point(347., 351.));
		testDragged(new Point(349., 359.));
		testDragged(new Point(349., 360.));
		testDragged(new Point(349., 357.));
		testDragged(new Point(344., 332.));
		testDragged(new Point(336., 308.));
		testDragged(new Point(332., 300.));
		testDragged(new Point(331., 300.));
		testDragged(new Point(329., 300.));
		testDragged(new Point(329., 306.));
		testDragged(new Point(329., 317.));
		testDragged(new Point(333., 328.));
		testDragged(new Point(336., 328.));
		testDragged(new Point(345., 306.));
		testDragged(new Point(343., 295.));
		testDragged(new Point(330., 291.));
		testDragged(new Point(318., 291.));
		testDragged(new Point(309., 291.));
		testDragged(new Point(304., 291.));
		testDragged(new Point(300., 291.));
		testDragged(new Point(299., 291.));
		testDragged(new Point(297., 279.));
		testDragged(new Point(297., 271.));
		testDragged(new Point(297., 265.));
		testDragged(new Point(297., 263.));
		testDragged(new Point(296., 262.));
		testDragged(new Point(295., 261.));
		testDragged(new Point(294., 261.));
		testDragged(new Point(292., 261.));
		testDragged(new Point(288., 261.));
		testDragged(new Point(285., 261.));
		testDragged(new Point(285., 260.));
		testDragged(new Point(285., 258.));
		testDragged(new Point(284., 258.));
		testDragged(new Point(284., 257.));
		testDragged(new Point(283., 256.));
		testDragged(new Point(282., 255.));
		testDragged(new Point(281., 255.));
		testDragged(new Point(281., 256.));
		testDragged(new Point(278., 265.));
		testDragged(new Point(278., 266.));
		testDragged(new Point(278., 267.));
		testDragged(new Point(278., 269.));
		testDragged(new Point(278., 272.));
		testDragged(new Point(278., 273.));
		testDragged(new Point(278., 274.));
		testDragged(new Point(278., 277.));
		testDragged(new Point(276., 283.));
		testDragged(new Point(275., 285.));
		testDragged(new Point(275., 284.));
		testDragged(new Point(274., 277.));
		testDragged(new Point(274., 267.));
		testDragged(new Point(274., 263.));
		testDragged(new Point(274., 262.));
		testDragged(new Point(275., 261.));
		testDragged(new Point(276., 261.));
		testDragged(new Point(279., 261.));
		testDragged(new Point(281., 261.));
		testDragged(new Point(282., 261.));
		testDragged(new Point(283., 264.));
		testDragged(new Point(283., 270.));
		testDragged(new Point(284., 273.));
		testDragged(new Point(284., 274.));
		testDragged(new Point(287., 275.));
		testDragged(new Point(288., 276.));
		testDragged(new Point(290., 276.));
		testDragged(new Point(290., 277.));
		testDragged(new Point(292., 277.));
		testDragged(new Point(293., 277.));
		testDragged(new Point(294., 277.));
		testDragged(new Point(295., 277.));
		testDragged(new Point(296., 277.));
		testDragged(new Point(297., 277.));
		testDragged(new Point(299., 277.));
		testDragged(new Point(300., 277.));
		testDragged(new Point(300., 278.));
		testDragged(new Point(301., 278.));
		testReleased();

		testPressed(new Point(301., 278.));
		testDragged(new Point(302., 278.));
		testDragged(new Point(303., 278.));
		testDragged(new Point(304., 278.));
		testDragged(new Point(306., 278.));
		testDragged(new Point(307., 278.));
		testDragged(new Point(308., 278.));
		testDragged(new Point(309., 278.));
		testDragged(new Point(311., 278.));
		testDragged(new Point(314., 278.));
		testDragged(new Point(315., 278.));
		testDragged(new Point(316., 278.));
		testDragged(new Point(317., 278.));
		testDragged(new Point(318., 278.));
		testReleased();

		testPressed(new Point(318., 278.));
		testDragged(new Point(321., 278.));
		testDragged(new Point(324., 278.));
		testDragged(new Point(331., 278.));
		testDragged(new Point(335., 278.));
		testDragged(new Point(339., 278.));
		testDragged(new Point(342., 278.));
		testDragged(new Point(349., 280.));
		testDragged(new Point(350., 281.));
		testReleased();

		testPressed(new Point(350., 281.));
		testDragged(new Point(350., 283.));
		testDragged(new Point(350., 285.));
		testDragged(new Point(350., 287.));
		testDragged(new Point(350., 289.));
		testDragged(new Point(353., 314.));
		testDragged(new Point(354., 324.));
		testDragged(new Point(354., 332.));
		testDragged(new Point(354., 339.));
		testDragged(new Point(354., 347.));
		testReleased();

		testPressed(new Point(354., 347.));
		testDragged(new Point(359., 341.));
		testDragged(new Point(364., 333.));
		testDragged(new Point(375., 321.));
		testDragged(new Point(378., 319.));
		testDragged(new Point(382., 313.));
		testDragged(new Point(388., 307.));
		testDragged(new Point(391., 301.));
		testDragged(new Point(392., 300.));
		testDragged(new Point(393., 299.));
		testDragged(new Point(389., 299.));
		testDragged(new Point(382., 299.));
		testDragged(new Point(370., 299.));
		testDragged(new Point(364., 300.));
		testDragged(new Point(355., 300.));
		testDragged(new Point(344., 300.));
		testDragged(new Point(334., 300.));
		testDragged(new Point(333., 300.));
		testDragged(new Point(338., 300.));
		testDragged(new Point(342., 300.));
		testDragged(new Point(357., 301.));
		testDragged(new Point(414., 323.));
		testDragged(new Point(438., 340.));
		testDragged(new Point(447., 354.));
		testDragged(new Point(448., 357.));
		testDragged(new Point(450., 357.));
		testDragged(new Point(439., 354.));
		testDragged(new Point(422., 346.));
		testDragged(new Point(393., 333.));
		testDragged(new Point(373., 322.));
		testDragged(new Point(366., 320.));
		testDragged(new Point(365., 319.));
		testDragged(new Point(363., 321.));
		testDragged(new Point(363., 343.));
		testDragged(new Point(366., 357.));
		testDragged(new Point(389., 376.));
		testDragged(new Point(417., 382.));
		testDragged(new Point(426., 382.));
		testDragged(new Point(411., 367.));
		testDragged(new Point(377., 325.));
		testDragged(new Point(359., 315.));
		testDragged(new Point(359., 313.));
		testDragged(new Point(359., 323.));
		testDragged(new Point(378., 356.));
		testDragged(new Point(415., 381.));
		testDragged(new Point(419., 382.));
		testDragged(new Point(396., 359.));
		testDragged(new Point(371., 316.));
		testDragged(new Point(368., 313.));
		testDragged(new Point(367., 312.));
		testDragged(new Point(367., 318.));
		testDragged(new Point(367., 335.));
		testDragged(new Point(370., 363.));
		testDragged(new Point(373., 372.));
		testDragged(new Point(374., 373.));
		testDragged(new Point(376., 370.));
		testDragged(new Point(376., 344.));
		testDragged(new Point(376., 337.));
		testDragged(new Point(376., 336.));
		testDragged(new Point(376., 341.));
		testDragged(new Point(377., 355.));
		testDragged(new Point(379., 361.));
		testDragged(new Point(379., 364.));
		testDragged(new Point(380., 365.));
		testDragged(new Point(380., 362.));
		testDragged(new Point(380., 356.));
		testDragged(new Point(380., 349.));
		testDragged(new Point(380., 351.));
		testDragged(new Point(378., 355.));
		testDragged(new Point(372., 365.));
		testDragged(new Point(369., 368.));
		testDragged(new Point(364., 372.));
		testDragged(new Point(358., 372.));
		testDragged(new Point(357., 369.));
		testDragged(new Point(357., 352.));
		testDragged(new Point(357., 340.));
		testDragged(new Point(354., 334.));
		testDragged(new Point(350., 332.));
		testDragged(new Point(331., 332.));
		testDragged(new Point(320., 336.));
		testDragged(new Point(313., 336.));
		testDragged(new Point(309., 338.));
		testDragged(new Point(308., 339.));
		testDragged(new Point(317., 340.));
		testDragged(new Point(342., 349.));
		testDragged(new Point(406., 379.));
		testDragged(new Point(411., 383.));
		testDragged(new Point(408., 386.));
		testDragged(new Point(380., 388.));
		testDragged(new Point(344., 384.));
		testDragged(new Point(334., 379.));
		testDragged(new Point(329., 358.));
		testDragged(new Point(329., 347.));
		testDragged(new Point(329., 346.));
		testDragged(new Point(328., 352.));
		testDragged(new Point(326., 364.));
		testDragged(new Point(322., 373.));
		testDragged(new Point(312., 400.));
		testDragged(new Point(309., 405.));
		testDragged(new Point(306., 399.));
		testDragged(new Point(298., 373.));
		testDragged(new Point(286., 332.));
		testDragged(new Point(282., 325.));
		testDragged(new Point(274., 322.));
		testDragged(new Point(268., 320.));
		testDragged(new Point(257., 319.));
		testDragged(new Point(250., 317.));
		testDragged(new Point(247., 296.));
		testDragged(new Point(247., 260.));
		testDragged(new Point(247., 250.));
		testDragged(new Point(247., 248.));
		testDragged(new Point(247., 262.));
		testDragged(new Point(241., 290.));
		testDragged(new Point(238., 308.));
		testDragged(new Point(236., 312.));
		testDragged(new Point(236., 314.));
		testDragged(new Point(239., 304.));
		testDragged(new Point(259., 264.));
		testDragged(new Point(274., 242.));
		testDragged(new Point(278., 239.));
		testDragged(new Point(278., 264.));
		testDragged(new Point(265., 305.));
		testDragged(new Point(264., 311.));
		testDragged(new Point(262., 311.));
		testDragged(new Point(266., 307.));
		testDragged(new Point(297., 286.));
		testDragged(new Point(303., 286.));
		testDragged(new Point(304., 287.));
		testDragged(new Point(304., 292.));
		testDragged(new Point(301., 312.));
		testDragged(new Point(290., 349.));
		testDragged(new Point(289., 357.));
		testDragged(new Point(289., 359.));
		testDragged(new Point(298., 342.));
		testDragged(new Point(311., 312.));
		testDragged(new Point(316., 303.));
		testDragged(new Point(317., 302.));
		testDragged(new Point(313., 310.));
		testDragged(new Point(304., 328.));
		testDragged(new Point(295., 344.));
		testDragged(new Point(295., 345.));
		testDragged(new Point(304., 322.));
		testDragged(new Point(316., 298.));
		testDragged(new Point(318., 292.));
		testDragged(new Point(317., 294.));
		testDragged(new Point(310., 302.));
		testDragged(new Point(290., 334.));
		testDragged(new Point(300., 312.));
		testDragged(new Point(311., 292.));
		testDragged(new Point(328., 243.));
		testDragged(new Point(328., 241.));
		testDragged(new Point(328., 242.));
		testDragged(new Point(326., 257.));
		testDragged(new Point(314., 281.));
		testDragged(new Point(300., 302.));
		testDragged(new Point(289., 324.));
		testDragged(new Point(296., 314.));
		testDragged(new Point(326., 276.));
		testDragged(new Point(346., 234.));
		testDragged(new Point(348., 238.));
		testDragged(new Point(348., 246.));
		testDragged(new Point(347., 269.));
		testDragged(new Point(340., 313.));
		testDragged(new Point(335., 335.));
		testDragged(new Point(329., 361.));
		testDragged(new Point(328., 367.));
		testDragged(new Point(332., 353.));
		testDragged(new Point(354., 292.));
		testDragged(new Point(356., 287.));
		testDragged(new Point(350., 301.));
		testDragged(new Point(340., 337.));
		testDragged(new Point(331., 359.));
		testDragged(new Point(334., 347.));
		testDragged(new Point(344., 307.));
		testDragged(new Point(347., 300.));
		testDragged(new Point(346., 300.));
		testDragged(new Point(336., 319.));
		testDragged(new Point(322., 339.));
		testDragged(new Point(321., 339.));
		testDragged(new Point(325., 323.));
		testDragged(new Point(334., 308.));
		testDragged(new Point(338., 300.));
		testDragged(new Point(338., 299.));
		testDragged(new Point(336., 303.));
		testDragged(new Point(322., 330.));
		testDragged(new Point(314., 340.));
		testDragged(new Point(312., 342.));
		testDragged(new Point(315., 329.));
		testDragged(new Point(327., 300.));
		testDragged(new Point(330., 294.));
		testDragged(new Point(330., 293.));
		testDragged(new Point(329., 293.));
		testDragged(new Point(313., 297.));
		testDragged(new Point(308., 299.));
		testDragged(new Point(307., 300.));
		testDragged(new Point(310., 295.));
		testDragged(new Point(324., 273.));
		testDragged(new Point(327., 271.));
		testDragged(new Point(327., 272.));
		testDragged(new Point(327., 278.));
		testDragged(new Point(314., 300.));
		testDragged(new Point(305., 325.));
		testDragged(new Point(294., 354.));
		testDragged(new Point(293., 360.));
		testDragged(new Point(306., 345.));
		testDragged(new Point(338., 304.));
		testDragged(new Point(352., 280.));
		testDragged(new Point(353., 277.));
		testDragged(new Point(354., 277.));
		testDragged(new Point(352., 280.));
		testDragged(new Point(345., 289.));
		testDragged(new Point(318., 333.));
		testDragged(new Point(306., 352.));
		testDragged(new Point(305., 355.));
		testDragged(new Point(305., 356.));
		testDragged(new Point(332., 317.));
		testDragged(new Point(355., 276.));
		testDragged(new Point(360., 264.));
		testDragged(new Point(360., 263.));
		testDragged(new Point(357., 275.));
		testDragged(new Point(349., 304.));
		testDragged(new Point(341., 335.));
		testDragged(new Point(341., 340.));
		testDragged(new Point(341., 342.));
		testDragged(new Point(349., 324.));
		testDragged(new Point(357., 306.));
		testDragged(new Point(368., 288.));
		testDragged(new Point(368., 287.));
		testDragged(new Point(369., 287.));
		testDragged(new Point(369., 289.));
		testDragged(new Point(368., 313.));
		testDragged(new Point(364., 336.));
		testDragged(new Point(362., 340.));
		testDragged(new Point(362., 332.));
		testDragged(new Point(363., 321.));
		testDragged(new Point(366., 313.));
		testDragged(new Point(366., 323.));
		testDragged(new Point(361., 346.));
		testDragged(new Point(356., 362.));
		testDragged(new Point(355., 365.));
		testReleased();

		testPressed(new Point(355., 365.));
		testDragged(new Point(351., 365.));
		testDragged(new Point(343., 362.));
		testDragged(new Point(336., 354.));
		testDragged(new Point(331., 349.));
		testDragged(new Point(294., 328.));
		testDragged(new Point(291., 327.));
		testDragged(new Point(290., 327.));
		testDragged(new Point(301., 327.));
		testDragged(new Point(322., 334.));
		testDragged(new Point(357., 346.));
		testDragged(new Point(398., 363.));
		testDragged(new Point(399., 365.));
		testDragged(new Point(372., 357.));
		testDragged(new Point(343., 342.));
		testDragged(new Point(305., 326.));
		testDragged(new Point(289., 322.));
		testDragged(new Point(291., 322.));
		testDragged(new Point(299., 322.));
		testDragged(new Point(322., 323.));
		testDragged(new Point(353., 340.));
		testDragged(new Point(356., 342.));
		testDragged(new Point(347., 338.));
		testDragged(new Point(315., 325.));
		testDragged(new Point(280., 310.));
		testDragged(new Point(273., 307.));
		testDragged(new Point(273., 306.));
		testDragged(new Point(284., 306.));
		testDragged(new Point(334., 322.));
		testDragged(new Point(340., 324.));
		testDragged(new Point(327., 321.));
		testDragged(new Point(292., 306.));
		testDragged(new Point(262., 280.));
		testDragged(new Point(256., 271.));
		testDragged(new Point(256., 270.));
		testDragged(new Point(281., 275.));
		testDragged(new Point(311., 292.));
		testDragged(new Point(327., 301.));
		testDragged(new Point(328., 302.));
		testDragged(new Point(325., 281.));
		testDragged(new Point(311., 242.));
		testDragged(new Point(311., 240.));
		testDragged(new Point(310., 239.));
		testDragged(new Point(310., 237.));
		testDragged(new Point(311., 261.));
		testDragged(new Point(311., 299.));
		testDragged(new Point(307., 321.));
		testDragged(new Point(289., 369.));
		testDragged(new Point(267., 385.));
		testDragged(new Point(247., 362.));
		testDragged(new Point(253., 301.));
		testDragged(new Point(265., 269.));
		testDragged(new Point(267., 257.));
		testDragged(new Point(255., 263.));
		testDragged(new Point(235., 286.));
		testDragged(new Point(219., 305.));
		testDragged(new Point(218., 308.));
		testDragged(new Point(218., 300.));
		testDragged(new Point(227., 266.));
		testDragged(new Point(246., 225.));
		testDragged(new Point(249., 220.));
		testDragged(new Point(247., 224.));
		testDragged(new Point(244., 230.));
		testDragged(new Point(233., 257.));
		testDragged(new Point(230., 266.));
		testDragged(new Point(230., 267.));
		testDragged(new Point(238., 253.));
		testDragged(new Point(251., 235.));
		testDragged(new Point(271., 217.));
		testDragged(new Point(274., 214.));
		testDragged(new Point(275., 214.));
		testDragged(new Point(272., 234.));
		testDragged(new Point(266., 273.));
		testDragged(new Point(266., 301.));
		testDragged(new Point(273., 315.));
		testDragged(new Point(279., 315.));
		testDragged(new Point(305., 300.));
		testDragged(new Point(315., 290.));
		testDragged(new Point(317., 289.));
		testDragged(new Point(317., 290.));
		testDragged(new Point(314., 305.));
		testDragged(new Point(310., 333.));
		testDragged(new Point(309., 341.));
		testDragged(new Point(311., 333.));
		testDragged(new Point(321., 304.));
		testDragged(new Point(328., 294.));
		testDragged(new Point(329., 292.));
		testDragged(new Point(329., 314.));
		testDragged(new Point(329., 339.));
		testDragged(new Point(329., 347.));
		testDragged(new Point(333., 349.));
		testDragged(new Point(366., 326.));
		testDragged(new Point(384., 294.));
		testDragged(new Point(386., 290.));
		testDragged(new Point(388., 302.));
		testDragged(new Point(388., 330.));
		testDragged(new Point(388., 349.));
		testDragged(new Point(388., 363.));
		testDragged(new Point(391., 360.));
		testDragged(new Point(394., 353.));
		testDragged(new Point(396., 352.));
		testReleased();

		testPressed(new Point(364., 312.));
		testDragged(new Point(363., 317.));
		testDragged(new Point(363., 338.));
		testDragged(new Point(363., 342.));
		testDragged(new Point(376., 321.));
		testDragged(new Point(395., 286.));
		testDragged(new Point(405., 270.));
		testDragged(new Point(406., 269.));
		testDragged(new Point(406., 282.));
		testDragged(new Point(403., 318.));
		testDragged(new Point(398., 346.));
		testDragged(new Point(397., 355.));
		testDragged(new Point(399., 352.));
		testDragged(new Point(410., 314.));
		testDragged(new Point(412., 307.));
		testDragged(new Point(408., 317.));
		testDragged(new Point(394., 334.));
		testDragged(new Point(364., 348.));
		testDragged(new Point(352., 348.));
		testDragged(new Point(343., 333.));
		testDragged(new Point(339., 264.));
		testDragged(new Point(337., 246.));
		testDragged(new Point(334., 243.));
		testDragged(new Point(293., 262.));
		testDragged(new Point(265., 283.));
		testDragged(new Point(252., 300.));
		testDragged(new Point(276., 268.));
		testDragged(new Point(302., 212.));
		testDragged(new Point(315., 188.));
		testDragged(new Point(290., 226.));
		testDragged(new Point(250., 269.));
		testDragged(new Point(233., 296.));
		testDragged(new Point(230., 301.));
		testDragged(new Point(234., 290.));
		testDragged(new Point(249., 256.));
		testDragged(new Point(257., 239.));
		testDragged(new Point(257., 237.));
		testDragged(new Point(247., 257.));
		testDragged(new Point(230., 283.));
		testDragged(new Point(227., 291.));
		testDragged(new Point(230., 279.));
		testDragged(new Point(248., 239.));
		testDragged(new Point(256., 219.));
		testDragged(new Point(253., 230.));
		testDragged(new Point(245., 256.));
		testDragged(new Point(239., 284.));
		testDragged(new Point(239., 285.));
		testDragged(new Point(270., 268.));
		testDragged(new Point(320., 213.));
		testDragged(new Point(328., 212.));
		testDragged(new Point(331., 251.));
		testDragged(new Point(333., 284.));
		testDragged(new Point(340., 317.));
		testDragged(new Point(350., 316.));
		testDragged(new Point(381., 276.));
		testDragged(new Point(392., 265.));
		testDragged(new Point(395., 263.));
		testDragged(new Point(391., 271.));
		testDragged(new Point(374., 332.));
		testDragged(new Point(373., 335.));
		testDragged(new Point(380., 325.));
		testDragged(new Point(395., 295.));
		testDragged(new Point(398., 288.));
		testDragged(new Point(399., 287.));
		testDragged(new Point(397., 292.));
		testDragged(new Point(386., 324.));
		testDragged(new Point(381., 332.));
		testDragged(new Point(381., 334.));
		testDragged(new Point(381., 335.));
		testDragged(new Point(400., 300.));
		testDragged(new Point(413., 270.));
		testDragged(new Point(413., 269.));
		testDragged(new Point(410., 272.));
		testDragged(new Point(387., 287.));
		testDragged(new Point(378., 283.));
		testDragged(new Point(377., 236.));
		testDragged(new Point(377., 191.));
		testDragged(new Point(373., 198.));
		testDragged(new Point(354., 245.));
		testDragged(new Point(344., 264.));
		testDragged(new Point(344., 267.));
		testDragged(new Point(349., 255.));
		testDragged(new Point(362., 230.));
		testDragged(new Point(362., 234.));
		testDragged(new Point(354., 258.));
		testDragged(new Point(350., 270.));
		testDragged(new Point(343., 303.));
		testDragged(new Point(347., 302.));
		testDragged(new Point(348., 302.));
		testDragged(new Point(347., 313.));
		testDragged(new Point(335., 335.));
		testDragged(new Point(307., 366.));
		testDragged(new Point(305., 368.));
		testDragged(new Point(305., 362.));
		testDragged(new Point(305., 321.));
		testDragged(new Point(305., 314.));
		testDragged(new Point(300., 312.));
		testDragged(new Point(288., 312.));
		testDragged(new Point(274., 312.));
		testDragged(new Point(267., 313.));
		testDragged(new Point(254., 313.));
		testDragged(new Point(251., 290.));
		testDragged(new Point(257., 227.));
		testDragged(new Point(260., 216.));
		testDragged(new Point(263., 218.));
		testDragged(new Point(266., 226.));
		testDragged(new Point(270., 241.));
		testDragged(new Point(292., 295.));
		testDragged(new Point(337., 354.));
		testDragged(new Point(370., 393.));
		testDragged(new Point(376., 399.));
		testDragged(new Point(380., 383.));
		testDragged(new Point(382., 331.));
		testDragged(new Point(375., 288.));
		testDragged(new Point(350., 246.));
		testDragged(new Point(338., 248.));
		testDragged(new Point(332., 279.));
		testDragged(new Point(356., 322.));
		testDragged(new Point(390., 326.));
		testDragged(new Point(409., 326.));
		testDragged(new Point(413., 325.));
		testDragged(new Point(413., 324.));
		testDragged(new Point(403., 324.));
		testDragged(new Point(309., 339.));
		testDragged(new Point(297., 349.));
		testDragged(new Point(296., 353.));
		testDragged(new Point(296., 354.));
		testDragged(new Point(308., 361.));
		testDragged(new Point(327., 370.));
		testDragged(new Point(329., 374.));
		testDragged(new Point(320., 378.));
		testDragged(new Point(294., 373.));
		testDragged(new Point(276., 355.));
		testDragged(new Point(242., 317.));
		testDragged(new Point(231., 300.));
		testDragged(new Point(230., 298.));
		testDragged(new Point(229., 298.));
		testDragged(new Point(228., 300.));
		testDragged(new Point(228., 309.));
		testDragged(new Point(242., 328.));
		testDragged(new Point(252., 338.));
		testDragged(new Point(259., 341.));
		testDragged(new Point(263., 343.));
		testDragged(new Point(268., 343.));
		testDragged(new Point(269., 343.));
		testDragged(new Point(270., 343.));
		testDragged(new Point(268., 336.));
		testDragged(new Point(255., 324.));
		testDragged(new Point(249., 319.));
		testDragged(new Point(248., 318.));
		testDragged(new Point(251., 321.));
		testDragged(new Point(269., 341.));
		testDragged(new Point(299., 356.));
		testDragged(new Point(306., 359.));
		testDragged(new Point(301., 340.));
		testDragged(new Point(293., 322.));
		testDragged(new Point(292., 320.));
		testReleased();

		testPressed(new Point(292., 320.));
		testDragged(new Point(292., 322.));
		testDragged(new Point(292., 326.));
		testDragged(new Point(297., 327.));
		testDragged(new Point(305., 327.));
		testDragged(new Point(327., 293.));
		testDragged(new Point(280., 291.));
		testDragged(new Point(268., 311.));
		testDragged(new Point(296., 327.));
		testDragged(new Point(365., 328.));
		testDragged(new Point(373., 323.));
		testDragged(new Point(348., 294.));
		testDragged(new Point(319., 293.));
		testDragged(new Point(306., 313.));
		testDragged(new Point(330., 317.));
		testDragged(new Point(363., 317.));
		testDragged(new Point(379., 314.));
		testDragged(new Point(386., 297.));
		testDragged(new Point(347., 274.));
		testDragged(new Point(333., 269.));
		testDragged(new Point(331., 268.));
		testDragged(new Point(334., 263.));
		testDragged(new Point(345., 232.));
		testDragged(new Point(348., 227.));
		testDragged(new Point(348., 226.));
		testDragged(new Point(338., 226.));
		testDragged(new Point(327., 219.));
		testDragged(new Point(324., 215.));
		testDragged(new Point(322., 204.));
		testDragged(new Point(329., 184.));
		testDragged(new Point(332., 184.));
		testDragged(new Point(332., 208.));
		testDragged(new Point(338., 261.));
		testDragged(new Point(341., 269.));
		testDragged(new Point(348., 249.));
		testDragged(new Point(355., 227.));
		testDragged(new Point(363., 198.));
		testDragged(new Point(363., 194.));
		testDragged(new Point(363., 198.));
		testDragged(new Point(352., 258.));
		testDragged(new Point(350., 277.));
		testDragged(new Point(354., 281.));
		testDragged(new Point(367., 265.));
		testDragged(new Point(378., 228.));
		testDragged(new Point(383., 219.));
		testDragged(new Point(383., 226.));
		testDragged(new Point(383., 259.));
		testDragged(new Point(382., 267.));
		testDragged(new Point(382., 248.));
		testDragged(new Point(368., 183.));
		testDragged(new Point(338., 163.));
		testDragged(new Point(319., 171.));
		testDragged(new Point(324., 171.));
		testDragged(new Point(349., 173.));
		testDragged(new Point(359., 176.));
		testDragged(new Point(340., 225.));
		testDragged(new Point(320., 239.));
		testDragged(new Point(319., 223.));
		testDragged(new Point(324., 195.));
		testDragged(new Point(322., 193.));
		testDragged(new Point(294., 210.));
		testDragged(new Point(273., 239.));
		testDragged(new Point(269., 240.));
		testDragged(new Point(272., 209.));
		testDragged(new Point(277., 191.));
		testDragged(new Point(275., 194.));
		testDragged(new Point(269., 241.));
		testDragged(new Point(269., 257.));
		testDragged(new Point(271., 257.));
		testDragged(new Point(310., 231.));
		testDragged(new Point(322., 215.));
		testDragged(new Point(321., 252.));
		testDragged(new Point(315., 287.));
		testDragged(new Point(319., 280.));
		testDragged(new Point(334., 253.));
		testDragged(new Point(335., 251.));
		testDragged(new Point(335., 253.));
		testDragged(new Point(335., 260.));
		testDragged(new Point(335., 261.));
		testDragged(new Point(341., 261.));
		testDragged(new Point(347., 261.));
		testDragged(new Point(365., 287.));
		testDragged(new Point(370., 299.));
		testDragged(new Point(372., 301.));
		testDragged(new Point(372., 291.));
		testDragged(new Point(372., 260.));
		testDragged(new Point(365., 247.));
		testDragged(new Point(360., 252.));
		testDragged(new Point(365., 274.));
		testDragged(new Point(400., 291.));
		testDragged(new Point(433., 291.));
		testDragged(new Point(438., 291.));
		testDragged(new Point(420., 266.));
		testDragged(new Point(363., 235.));
		testDragged(new Point(354., 231.));
		testDragged(new Point(364., 233.));
		testDragged(new Point(397., 257.));
		testDragged(new Point(418., 311.));
		testDragged(new Point(418., 312.));
		testDragged(new Point(412., 296.));
		testDragged(new Point(396., 273.));
		testDragged(new Point(372., 242.));
		testDragged(new Point(372., 245.));
		testDragged(new Point(377., 270.));
		testDragged(new Point(387., 300.));
		testDragged(new Point(388., 302.));
		testDragged(new Point(386., 285.));
		testDragged(new Point(373., 258.));
		testDragged(new Point(368., 252.));
		testDragged(new Point(367., 255.));
		testDragged(new Point(367., 279.));
		testDragged(new Point(373., 303.));
		testDragged(new Point(378., 309.));
		testDragged(new Point(379., 309.));
		testDragged(new Point(376., 290.));
		testDragged(new Point(367., 271.));
		testDragged(new Point(365., 267.));
		testDragged(new Point(363., 273.));
		testDragged(new Point(363., 298.));
		testDragged(new Point(367., 311.));
		testDragged(new Point(369., 312.));
		testDragged(new Point(369., 304.));
		testDragged(new Point(366., 278.));
		testDragged(new Point(359., 269.));
		testDragged(new Point(355., 266.));
		testDragged(new Point(354., 266.));
		testDragged(new Point(353., 266.));
		testDragged(new Point(353., 269.));
		testDragged(new Point(353., 278.));
		testDragged(new Point(353., 282.));
		testDragged(new Point(356., 280.));
		testDragged(new Point(356., 269.));
		testDragged(new Point(356., 264.));
		testDragged(new Point(356., 262.));
		testDragged(new Point(356., 260.));
		testDragged(new Point(355., 260.));
		testDragged(new Point(352., 260.));
		testDragged(new Point(352., 255.));
		testDragged(new Point(352., 244.));
		testDragged(new Point(352., 240.));
		testDragged(new Point(352., 241.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
		
	}
	
	@Test
	public void testBug16() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;		

		testPressed(new Point(823., 613.));
		testDragged(new Point(823., 615.));
		testDragged(new Point(819., 655.));
		testDragged(new Point(819., 656.));
		testDragged(new Point(817., 648.));
		testDragged(new Point(814., 591.));
		testDragged(new Point(814., 583.));
		testDragged(new Point(814., 543.));
		testDragged(new Point(815., 569.));
		testDragged(new Point(826., 676.));
		testDragged(new Point(827., 670.));
		testDragged(new Point(815., 575.));
		testDragged(new Point(820., 614.));
		testDragged(new Point(826., 684.));
		testDragged(new Point(823., 636.));
		testDragged(new Point(821., 509.));
		testDragged(new Point(819., 561.));
		testDragged(new Point(819., 633.));
		testDragged(new Point(817., 577.));
		testDragged(new Point(815., 540.));
		testDragged(new Point(815., 641.));
		testDragged(new Point(815., 658.));
		testDragged(new Point(815., 539.));
		testDragged(new Point(815., 520.));
		testDragged(new Point(815., 581.));
		testDragged(new Point(815., 567.));
		testDragged(new Point(815., 533.));
		testDragged(new Point(815., 598.));
		testDragged(new Point(816., 612.));
		testDragged(new Point(822., 624.));
		testDragged(new Point(819., 513.));
		testDragged(new Point(816., 526.));
		testDragged(new Point(814., 572.));
		testDragged(new Point(814., 558.));
		testDragged(new Point(814., 530.));
		testDragged(new Point(814., 533.));
		testDragged(new Point(814., 560.));
		testDragged(new Point(814., 560.));
		testDragged(new Point(814., 535.));
		testDragged(new Point(811., 553.));
		testDragged(new Point(811., 582.));
		testDragged(new Point(811., 577.));
		testDragged(new Point(811., 550.));
		testDragged(new Point(811., 555.));
		testDragged(new Point(812., 603.));
		testReleased();

		testPressed(new Point(823., 595.));
		testDragged(new Point(821., 612.));
		testDragged(new Point(818., 663.));
		testDragged(new Point(829., 691.));
		testDragged(new Point(829., 692.));
		testDragged(new Point(820., 654.));
		testDragged(new Point(816., 645.));
		testDragged(new Point(816., 643.));
		testDragged(new Point(824., 667.));
		testDragged(new Point(828., 641.));
		testDragged(new Point(828., 656.));
		testDragged(new Point(828., 651.));
		testDragged(new Point(829., 641.));
		testReleased();

		testPressed(new Point(803., 551.));
		testDragged(new Point(810., 529.));
		testDragged(new Point(814., 512.));
		testReleased();

		testPressed(new Point(814., 512.));
		testDragged(new Point(814., 513.));
		testDragged(new Point(814., 516.));
		testDragged(new Point(814., 518.));
		testDragged(new Point(814., 523.));
		testDragged(new Point(814., 526.));
		testDragged(new Point(812., 530.));
		testDragged(new Point(812., 536.));
		testDragged(new Point(810., 547.));
		testDragged(new Point(810., 558.));
		testDragged(new Point(810., 562.));
		testDragged(new Point(810., 565.));
		testDragged(new Point(810., 566.));
		testDragged(new Point(810., 560.));
		testReleased();

		testPressed(new Point(815., 573.));
		testDragged(new Point(815., 583.));
		testDragged(new Point(823., 676.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	/*
	 * drawing too close to another road
	 */
	@Test
	public void testBug17() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(296., 266.));
		testDragged(new Point(298., 266.));
		testDragged(new Point(301., 266.));
		testDragged(new Point(305., 266.));
		testDragged(new Point(316., 266.));
		testDragged(new Point(332., 266.));
		testDragged(new Point(349., 263.));
		testDragged(new Point(371., 263.));
		testDragged(new Point(408., 263.));
		testDragged(new Point(453., 263.));
		testDragged(new Point(497., 263.));
		testDragged(new Point(559., 263.));
		testDragged(new Point(620., 263.));
		testDragged(new Point(678., 269.));
		testDragged(new Point(734., 269.));
		testDragged(new Point(795., 269.));
		testDragged(new Point(840., 269.));
		testDragged(new Point(932., 264.));
		testDragged(new Point(978., 261.));
		testDragged(new Point(1042., 256.));
		testDragged(new Point(1069., 256.));
		testDragged(new Point(1123., 256.));
		testDragged(new Point(1162., 259.));
		testDragged(new Point(1176., 261.));
		testDragged(new Point(1193., 263.));
		testDragged(new Point(1204., 265.));
		testDragged(new Point(1221., 265.));
		testDragged(new Point(1243., 267.));
		testDragged(new Point(1252., 267.));
		testDragged(new Point(1253., 267.));
		testDragged(new Point(1254., 267.));
		testDragged(new Point(1256., 267.));
		testReleased();

		testPressed(new Point(410., 273.));
		testDragged(new Point(411., 273.));
		testDragged(new Point(413., 273.));
		testDragged(new Point(414., 273.));
		testDragged(new Point(415., 273.));
		testDragged(new Point(416., 273.));
		testDragged(new Point(417., 273.));
		testDragged(new Point(420., 273.));
		testDragged(new Point(421., 273.));
		testDragged(new Point(422., 273.));
		testDragged(new Point(424., 273.));
		testDragged(new Point(426., 273.));
		testDragged(new Point(427., 273.));
		testDragged(new Point(428., 274.));
		testDragged(new Point(429., 274.));
		testDragged(new Point(430., 274.));
		testDragged(new Point(431., 274.));
		testDragged(new Point(433., 274.));
		testDragged(new Point(434., 274.));
		testDragged(new Point(435., 274.));
		testDragged(new Point(436., 274.));
		testDragged(new Point(437., 274.));
		testDragged(new Point(438., 274.));
		testDragged(new Point(440., 274.));
		testDragged(new Point(441., 274.));
		testDragged(new Point(442., 274.));
		testDragged(new Point(443., 274.));
		testDragged(new Point(444., 274.));
		testDragged(new Point(447., 274.));
		testDragged(new Point(448., 274.));
		testDragged(new Point(450., 274.));
		testDragged(new Point(451., 274.));
		testDragged(new Point(453., 275.));
		testDragged(new Point(454., 275.));
		testDragged(new Point(456., 275.));
		testDragged(new Point(457., 275.));
		testDragged(new Point(459., 275.));
		testDragged(new Point(460., 275.));
		testDragged(new Point(461., 275.));
		testDragged(new Point(462., 275.));
		testDragged(new Point(463., 275.));
		testDragged(new Point(464., 275.));
		testDragged(new Point(466., 275.));
		testDragged(new Point(467., 275.));
		testDragged(new Point(468., 275.));
		testDragged(new Point(469., 275.));
		testDragged(new Point(470., 275.));
		testDragged(new Point(471., 275.));
		testDragged(new Point(473., 275.));
		testDragged(new Point(474., 275.));
		testDragged(new Point(475., 275.));
		testDragged(new Point(476., 275.));
		testDragged(new Point(477., 275.));
		testDragged(new Point(478., 275.));
		testDragged(new Point(479., 275.));
		testDragged(new Point(481., 275.));
		testDragged(new Point(482., 275.));
		testDragged(new Point(483., 275.));
		testDragged(new Point(484., 275.));
		testDragged(new Point(485., 275.));
		testDragged(new Point(486., 275.));
		testDragged(new Point(488., 275.));
		testDragged(new Point(489., 275.));
		testDragged(new Point(490., 275.));
		testDragged(new Point(491., 275.));
		testDragged(new Point(492., 275.));
		testDragged(new Point(493., 275.));
		testDragged(new Point(495., 275.));
		testDragged(new Point(496., 275.));
		testDragged(new Point(497., 275.));
		testDragged(new Point(498., 275.));
		testDragged(new Point(499., 275.));
		testDragged(new Point(500., 275.));
		testDragged(new Point(502., 275.));
		testDragged(new Point(503., 275.));
		testDragged(new Point(504., 275.));
		testDragged(new Point(505., 275.));
		testDragged(new Point(506., 275.));
		testDragged(new Point(507., 275.));
		testDragged(new Point(509., 275.));
		testDragged(new Point(510., 275.));
		testDragged(new Point(511., 275.));
		testDragged(new Point(512., 275.));
		testDragged(new Point(513., 275.));
		testDragged(new Point(514., 274.));
		testDragged(new Point(516., 274.));
		testDragged(new Point(517., 274.));
		testDragged(new Point(518., 274.));
		testDragged(new Point(519., 274.));
		testDragged(new Point(520., 274.));
		testDragged(new Point(520., 273.));
		testDragged(new Point(521., 273.));
		testDragged(new Point(523., 273.));
		testDragged(new Point(524., 273.));
		testDragged(new Point(525., 273.));
		testDragged(new Point(526., 273.));
		testDragged(new Point(527., 273.));
		testDragged(new Point(528., 273.));
		testDragged(new Point(530., 273.));
		testDragged(new Point(531., 273.));
		testDragged(new Point(532., 273.));
		testDragged(new Point(533., 273.));
		testDragged(new Point(534., 273.));
		testDragged(new Point(535., 273.));
		testDragged(new Point(537., 273.));
		testDragged(new Point(538., 273.));
		testDragged(new Point(539., 273.));
		testDragged(new Point(540., 273.));
		testDragged(new Point(541., 273.));
		testDragged(new Point(542., 273.));
		testDragged(new Point(544., 273.));
		testDragged(new Point(545., 273.));
		testDragged(new Point(546., 273.));
		testDragged(new Point(547., 273.));
		testDragged(new Point(548., 273.));
		testDragged(new Point(549., 273.));
		testDragged(new Point(551., 273.));
		testDragged(new Point(552., 273.));
		testDragged(new Point(554., 273.));
		testDragged(new Point(555., 273.));
		testDragged(new Point(556., 273.));
		testDragged(new Point(558., 273.));
		testDragged(new Point(559., 273.));
		testDragged(new Point(560., 273.));
		testDragged(new Point(561., 273.));
		testDragged(new Point(562., 273.));
		testDragged(new Point(563., 273.));
		testDragged(new Point(565., 273.));
		testDragged(new Point(566., 273.));
		testDragged(new Point(567., 273.));
		testDragged(new Point(568., 273.));
		testDragged(new Point(569., 273.));
		testDragged(new Point(570., 273.));
		testDragged(new Point(573., 273.));
		testDragged(new Point(574., 273.));
		testDragged(new Point(576., 273.));
		testDragged(new Point(578., 273.));
		testDragged(new Point(579., 273.));
		testDragged(new Point(580., 273.));
		testDragged(new Point(581., 273.));
		testDragged(new Point(582., 273.));
		testDragged(new Point(583., 273.));
		testDragged(new Point(585., 273.));
		testDragged(new Point(586., 273.));
		testDragged(new Point(587., 273.));
		testDragged(new Point(588., 273.));
		testDragged(new Point(589., 274.));
		testDragged(new Point(590., 274.));
		testDragged(new Point(592., 275.));
		testDragged(new Point(593., 275.));
		testDragged(new Point(594., 275.));
		testDragged(new Point(595., 275.));
		testDragged(new Point(596., 276.));
		testDragged(new Point(597., 276.));
		testDragged(new Point(600., 276.));
		testDragged(new Point(601., 276.));
		testDragged(new Point(602., 276.));
		testDragged(new Point(605., 276.));
		testDragged(new Point(606., 276.));
		testDragged(new Point(607., 278.));
		testDragged(new Point(608., 278.));
		testDragged(new Point(609., 278.));
		testDragged(new Point(611., 278.));
		testDragged(new Point(612., 278.));
		testDragged(new Point(613., 279.));
		testDragged(new Point(614., 279.));
		testDragged(new Point(615., 279.));
		testDragged(new Point(616., 279.));
		testDragged(new Point(618., 280.));
		testDragged(new Point(619., 280.));
		testDragged(new Point(621., 280.));
		testDragged(new Point(624., 281.));
		testDragged(new Point(627., 283.));
		testDragged(new Point(630., 284.));
		testDragged(new Point(634., 286.));
		testDragged(new Point(639., 287.));
		testDragged(new Point(643., 290.));
		testDragged(new Point(646., 292.));
		testDragged(new Point(649., 293.));
		testDragged(new Point(652., 294.));
		testDragged(new Point(657., 297.));
		testDragged(new Point(658., 297.));
		testDragged(new Point(660., 298.));
		testDragged(new Point(661., 298.));
		testDragged(new Point(662., 298.));
		testDragged(new Point(663., 298.));
		testDragged(new Point(664., 298.));
		testDragged(new Point(665., 300.));
		testDragged(new Point(667., 300.));
		testDragged(new Point(669., 300.));
		testDragged(new Point(670., 300.));
		testDragged(new Point(671., 300.));
		testDragged(new Point(672., 300.));
		testDragged(new Point(673., 300.));
		testDragged(new Point(676., 300.));
		testDragged(new Point(677., 300.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	/*
	 * drawing too close to another road
	 */
	@Test
	public void testBug18() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(459., 258.));
		testDragged(new Point(476., 258.));
		testDragged(new Point(492., 258.));
		testDragged(new Point(541., 258.));
		testReleased();
		
		testPressed(new Point(464., 212.));
		testDragged(new Point(484., 253.));
		testDragged(new Point(493., 267.));
		testDragged(new Point(509., 318.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
}
