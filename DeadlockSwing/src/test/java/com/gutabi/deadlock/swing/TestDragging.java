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
	
}
