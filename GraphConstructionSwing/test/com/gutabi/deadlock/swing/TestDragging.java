package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.swing.controller.MouseController;

public class TestDragging {
	
	static MouseController mc;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		
		mc = new MouseController();
		mc.init();
		
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
		MODEL.clear();
	}
	
	
	List<Edge> edges;
	
	@Test
	public void test1() throws Exception {
		
		mc.pressed_M(new Point(5, 5));
		mc.dragged_M(new Point(6, 6));
		mc.dragged_M(new Point(7, 7));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.graph.getEdges();
			}
		});
		
		assertEquals(edges.size(), 1);
		Edge e = edges.get(0);
		assertEquals(e.size(), 2);
		assertEquals(new Point(5, 5), e.getPoint(0));
		assertEquals(new Point(7, 7), e.getPoint(1));
		
	}
	
	@Test
	public void test2() throws Exception {
		
		mc.pressed_M(new Point(5, 5));
		mc.dragged_M(new Point(6, 6));
		mc.dragged_M(new Point(4, 4));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.graph.getEdges();
			}
		});
		 
		assertEquals(edges.size(),  1);
		Edge e = edges.get(0);
		assertEquals(new Point(6, 6), e.getStart().getPoint());
		assertEquals(new Point(4, 4), e.getEnd().getPoint());
		
	}
	
	@Test
	public void test3() throws Exception {
		
		mc.pressed_M(new Point(267, 581));
		mc.dragged_M(new Point(267, 580));
		mc.dragged_M(new Point(267, 582));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test4() throws Exception {
		
		mc.pressed_M(new Point(2, 2));
		mc.dragged_M(new Point(1, 2));
		mc.dragged_M(new Point(1, 1));
		mc.dragged_M(new Point(1, 3));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test5() throws Exception {
		
		mc.pressed_M(new Point(532, 627));
		mc.dragged_M(new Point(515, 620));
		mc.dragged_M(new Point(500, 612));
		mc.dragged_M(new Point(480, 598));
		mc.dragged_M(new Point(443, 565));
		mc.dragged_M(new Point(415, 534));
		mc.dragged_M(new Point(397, 513));
		mc.dragged_M(new Point(383, 493));
		mc.dragged_M(new Point(377, 473));
		mc.dragged_M(new Point(372, 446));
		mc.dragged_M(new Point(372, 424));
		mc.dragged_M(new Point(372, 410));
		mc.dragged_M(new Point(372, 396));
		mc.dragged_M(new Point(372, 380));
		mc.dragged_M(new Point(373, 363));
		mc.dragged_M(new Point(380, 343));
		mc.dragged_M(new Point(390, 331));
		mc.dragged_M(new Point(403, 320));
		mc.dragged_M(new Point(420, 314));
		mc.dragged_M(new Point(434, 310));
		mc.dragged_M(new Point(449, 306));
		mc.dragged_M(new Point(460, 306));
		mc.dragged_M(new Point(471, 306));
		mc.dragged_M(new Point(498, 306));
		mc.dragged_M(new Point(518, 309));
		mc.dragged_M(new Point(540, 316));
		mc.dragged_M(new Point(556, 331));
		mc.dragged_M(new Point(571, 344));
		mc.dragged_M(new Point(582, 359));
		mc.dragged_M(new Point(592, 367));
		mc.dragged_M(new Point(597, 374));
		mc.dragged_M(new Point(604, 382));
		mc.dragged_M(new Point(607, 386));
		mc.dragged_M(new Point(610, 389));
		mc.dragged_M(new Point(614, 398));
		mc.dragged_M(new Point(616, 412));
		mc.dragged_M(new Point(618, 453));
		mc.dragged_M(new Point(618, 472));
		mc.dragged_M(new Point(617, 486));
		mc.dragged_M(new Point(613, 506));
		mc.dragged_M(new Point(606, 533));
		mc.dragged_M(new Point(599, 556));
		mc.dragged_M(new Point(590, 571));
		mc.dragged_M(new Point(580, 583));
		mc.dragged_M(new Point(565, 599));
		mc.dragged_M(new Point(542, 615));
		mc.dragged_M(new Point(521, 635));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test6() throws Exception {
		
		mc.pressed_M(new Point(5, 5));
		mc.dragged_M(new Point(7, 7));
		mc.released_M();
		
		mc.pressed_M(new Point(6, 6));
		mc.dragged_M(new Point(4, 4));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test7() throws Exception {
		
		mc.pressed_M(new Point(5, 5));
		mc.dragged_M(new Point(6, 6));
		mc.dragged_M(new Point(7, 7));
		mc.released_M();
		
		mc.pressed_M(new Point(5, 5));
		mc.pressed_M(new Point(6, 6));
		mc.dragged_M(new Point(4, 4));
		mc.released_M();
		
		mc.pressed_M(new Point(267, 581));
		mc.dragged_M(new Point(267, 580));
		mc.dragged_M(new Point(267, 582));
		mc.released_M();
		
		mc.pressed_M(new Point(2, 2));
		mc.dragged_M(new Point(1, 2));
		mc.dragged_M(new Point(1, 1));
		mc.dragged_M(new Point(1, 3));
		mc.released_M();
		
		mc.pressed_M(new Point(532, 627));
		mc.dragged_M(new Point(515, 620));
		mc.dragged_M(new Point(500, 612));
		mc.dragged_M(new Point(480, 598));
		mc.dragged_M(new Point(443, 565));
		mc.dragged_M(new Point(415, 534));
		mc.dragged_M(new Point(397, 513));
		mc.dragged_M(new Point(383, 493));
		mc.dragged_M(new Point(377, 473));
		mc.dragged_M(new Point(372, 446));
		mc.dragged_M(new Point(372, 424));
		mc.dragged_M(new Point(372, 410));
		mc.dragged_M(new Point(372, 396));
		mc.dragged_M(new Point(372, 380));
		mc.dragged_M(new Point(373, 363));
		mc.dragged_M(new Point(380, 343));
		mc.dragged_M(new Point(390, 331));
		mc.dragged_M(new Point(403, 320));
		mc.dragged_M(new Point(420, 314));
		mc.dragged_M(new Point(434, 310));
		mc.dragged_M(new Point(449, 306));
		mc.dragged_M(new Point(460, 306));
		mc.dragged_M(new Point(471, 306));
		mc.dragged_M(new Point(498, 306));
		mc.dragged_M(new Point(518, 309));
		mc.dragged_M(new Point(540, 316));
		mc.dragged_M(new Point(556, 331));
		mc.dragged_M(new Point(571, 344));
		mc.dragged_M(new Point(582, 359));
		mc.dragged_M(new Point(592, 367));
		mc.dragged_M(new Point(597, 374));
		mc.dragged_M(new Point(604, 382));
		mc.dragged_M(new Point(607, 386));
		mc.dragged_M(new Point(610, 389));
		mc.dragged_M(new Point(614, 398));
		mc.dragged_M(new Point(616, 412));
		mc.dragged_M(new Point(618, 453));
		mc.dragged_M(new Point(618, 472));
		mc.dragged_M(new Point(617, 486));
		mc.dragged_M(new Point(613, 506));
		mc.dragged_M(new Point(606, 533));
		mc.dragged_M(new Point(599, 556));
		mc.dragged_M(new Point(590, 571));
		mc.dragged_M(new Point(580, 583));
		mc.dragged_M(new Point(565, 599));
		mc.dragged_M(new Point(542, 615));
		mc.dragged_M(new Point(521, 635));
		mc.released_M();
		
		mc.pressed_M(new Point(841, 101));
		mc.dragged_M(new Point(838, 102));
		mc.dragged_M(new Point(830, 104));
		mc.dragged_M(new Point(823, 105));
		mc.dragged_M(new Point(814, 109));
		mc.dragged_M(new Point(804, 115));
		mc.dragged_M(new Point(798, 118));
		mc.dragged_M(new Point(792, 125));
		mc.dragged_M(new Point(787, 135));
		mc.dragged_M(new Point(779, 145));
		mc.dragged_M(new Point(772, 150));
		mc.dragged_M(new Point(762, 158));
		mc.dragged_M(new Point(752, 168));
		mc.dragged_M(new Point(742, 178));
		mc.dragged_M(new Point(730, 201));
		mc.dragged_M(new Point(715, 241));
		mc.dragged_M(new Point(711, 255));
		mc.dragged_M(new Point(705, 276));
		mc.dragged_M(new Point(705, 287));
		mc.dragged_M(new Point(703, 306));
		mc.dragged_M(new Point(703, 320));
		mc.dragged_M(new Point(703, 328));
		mc.dragged_M(new Point(703, 335));
		mc.dragged_M(new Point(707, 345));
		mc.dragged_M(new Point(711, 348));
		mc.dragged_M(new Point(718, 352));
		mc.dragged_M(new Point(729, 356));
		mc.dragged_M(new Point(739, 359));
		mc.dragged_M(new Point(748, 363));
		mc.dragged_M(new Point(755, 366));
		mc.dragged_M(new Point(761, 368));
		mc.dragged_M(new Point(767, 370));
		mc.dragged_M(new Point(770, 371));
		mc.dragged_M(new Point(773, 372));
		mc.dragged_M(new Point(774, 372));
		mc.dragged_M(new Point(775, 372));
		mc.released_M();
		
		mc.pressed_M(new Point(775, 335));
		mc.dragged_M(new Point(774, 333));
		mc.dragged_M(new Point(771, 333));
		mc.dragged_M(new Point(767, 332));
		mc.dragged_M(new Point(760, 330));
		mc.dragged_M(new Point(754, 329));
		mc.dragged_M(new Point(748, 327));
		mc.dragged_M(new Point(743, 324));
		mc.dragged_M(new Point(737, 323));
		mc.dragged_M(new Point(732, 319));
		mc.dragged_M(new Point(728, 318));
		mc.dragged_M(new Point(723, 316));
		mc.dragged_M(new Point(717, 315));
		mc.dragged_M(new Point(708, 313));
		mc.dragged_M(new Point(699, 311));
		mc.dragged_M(new Point(690, 310));
		mc.dragged_M(new Point(684, 308));
		mc.dragged_M(new Point(673, 306));
		mc.dragged_M(new Point(670, 303));
		mc.dragged_M(new Point(666, 302));
		mc.dragged_M(new Point(661, 302));
		mc.dragged_M(new Point(659, 301));
		mc.dragged_M(new Point(656, 299));
		mc.dragged_M(new Point(653, 298));
		mc.dragged_M(new Point(650, 296));
		mc.dragged_M(new Point(649, 295));
		mc.dragged_M(new Point(647, 294));
		mc.dragged_M(new Point(645, 293));
		mc.dragged_M(new Point(643, 293));
		mc.dragged_M(new Point(643, 292));
		mc.dragged_M(new Point(642, 292));
		mc.dragged_M(new Point(643, 292));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test8() throws Exception {
		
		mc.pressed_M(new Point(532, 627));
		mc.dragged_M(new Point(515, 620));
		mc.dragged_M(new Point(500, 612));
		mc.dragged_M(new Point(480, 598));
		mc.dragged_M(new Point(443, 565));
		mc.dragged_M(new Point(415, 534));
		mc.dragged_M(new Point(397, 513));
		mc.dragged_M(new Point(383, 493));
		mc.dragged_M(new Point(377, 473));
		mc.dragged_M(new Point(372, 446));
		mc.dragged_M(new Point(372, 424));
		mc.dragged_M(new Point(372, 410));
		mc.dragged_M(new Point(372, 396));
		mc.dragged_M(new Point(372, 380));
		mc.dragged_M(new Point(373, 363));
		mc.dragged_M(new Point(380, 343));
		mc.dragged_M(new Point(390, 331));
		mc.dragged_M(new Point(403, 320));
		mc.dragged_M(new Point(420, 314));
		mc.dragged_M(new Point(434, 310));
		mc.dragged_M(new Point(449, 306));
		mc.dragged_M(new Point(460, 306));
		mc.dragged_M(new Point(471, 306));
		mc.dragged_M(new Point(498, 306));
		mc.dragged_M(new Point(518, 309));
		mc.dragged_M(new Point(540, 316));
		mc.dragged_M(new Point(556, 331));
		mc.dragged_M(new Point(571, 344));
		mc.dragged_M(new Point(582, 359));
		mc.dragged_M(new Point(592, 367));
		mc.dragged_M(new Point(597, 374));
		mc.dragged_M(new Point(604, 382));
		mc.dragged_M(new Point(607, 386));
		mc.dragged_M(new Point(610, 389));
		mc.dragged_M(new Point(614, 398));
		mc.dragged_M(new Point(616, 412));
		mc.dragged_M(new Point(618, 453));
		mc.dragged_M(new Point(618, 472));
		mc.dragged_M(new Point(617, 486));
		mc.dragged_M(new Point(613, 506));
		mc.dragged_M(new Point(606, 533));
		mc.dragged_M(new Point(599, 556));
		mc.dragged_M(new Point(590, 571));
		mc.dragged_M(new Point(580, 583));
		mc.dragged_M(new Point(565, 599));
		mc.dragged_M(new Point(542, 615));
		mc.dragged_M(new Point(521, 635));
		mc.released_M();
		
		// break the loop
		mc.pressed_M(new Point(619, 448));
		mc.dragged_M(new Point(580, 423));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test9() throws Exception {
		
		mc.pressed_M(new Point(0, 0));
		mc.dragged_M(new Point(150, 450));
		mc.released_M();
		
		mc.pressed_M(new Point(200, 0));
		mc.dragged_M(new Point(0, 50));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testLoop() throws Exception {
		
		mc.pressed_M(new Point(2, 0));
		mc.dragged_M(new Point(1, 0));
		mc.dragged_M(new Point(1, 1));
		mc.dragged_M(new Point(3, 0));
		mc.dragged_M(new Point(2, 0));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testLoop2() throws Exception {
		
		mc.pressed_M(new Point(2, 5));
		mc.dragged_M(new Point(1, 5));
		mc.dragged_M(new Point(1, 10));
		mc.dragged_M(new Point(12, 5));
		mc.dragged_M(new Point(2, 5));
		mc.released_M();
		
		mc.pressed_M(new Point(3, 0));
		mc.dragged_M(new Point(3, 6));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testIntersection() throws Exception {
		
		mc.pressed_M(new Point(4, 0));
		mc.dragged_M(new Point(10, 12));
		mc.released_M();

		mc.pressed_M(new Point(0, 12));
		mc.dragged_M(new Point(7, 7));
		mc.dragged_M(new Point(16, 1));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug1() throws Exception {
		
		mc.pressed_M(new Point(532, 419));
		mc.dragged_M(new Point(583, 442));
		mc.dragged_M(new Point(583, 447));
		mc.dragged_M(new Point(584, 448));
		mc.dragged_M(new Point(584, 449));
		mc.dragged_M(new Point(584, 450));
		mc.dragged_M(new Point(585, 450));
		mc.released_M(true);

		mc.pressed_M(new Point(555, 448));
		mc.dragged_M(new Point(556, 448));
		mc.dragged_M(new Point(579, 444));
		mc.dragged_M(new Point(580, 444));
		mc.released_M(true);

		mc.pressed_M(new Point(580, 444));
		mc.dragged_M(new Point(581, 444));
		mc.dragged_M(new Point(592, 448));
		mc.dragged_M(new Point(593, 448));
		mc.dragged_M(new Point(594, 448));
		mc.released_M(true);

		mc.pressed_M(new Point(594, 448));
		mc.dragged_M(new Point(597, 451));
		mc.dragged_M(new Point(598, 454));
		mc.dragged_M(new Point(603, 455));
		mc.dragged_M(new Point(604, 456));
		mc.dragged_M(new Point(607, 458));
		mc.dragged_M(new Point(608, 458));
		mc.released_M(true);

		mc.pressed_M(new Point(608, 458));
		mc.dragged_M(new Point(608, 459));
		mc.dragged_M(new Point(607, 459));
		mc.dragged_M(new Point(592, 463));
		mc.dragged_M(new Point(587, 463));
		mc.dragged_M(new Point(583, 463));
		mc.released_M(true);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug() throws Exception {

		mc.pressed_M(new Point(639, 420));
		mc.dragged_M(new Point(637, 420));
		mc.dragged_M(new Point(631, 420));
		mc.released_M(false);

		mc.pressed_M(new Point(631, 420));
		mc.released_M(false);

		mc.pressed_M(new Point(631, 420));
		mc.dragged_M(new Point(630, 422));
		mc.dragged_M(new Point(629, 424));
		mc.dragged_M(new Point(623, 424));
		mc.released_M(true);

		mc.pressed_M(new Point(623, 424));
		mc.dragged_M(new Point(621, 421));
		mc.dragged_M(new Point(619, 420));
		mc.dragged_M(new Point(618, 420));
		mc.dragged_M(new Point(618, 419));
		mc.released_M(false);

		mc.pressed_M(new Point(618, 419));
		mc.dragged_M(new Point(626, 419));
		mc.dragged_M(new Point(627, 418));
		mc.dragged_M(new Point(629, 417));
		mc.released_M(false);

		mc.pressed_M(new Point(629, 417));
		mc.dragged_M(new Point(632, 417));
		mc.dragged_M(new Point(640, 415));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testEdgeCountNotEqual2Bug2() throws Exception {

		mc.pressed_M(new Point(49, 100));
		mc.dragged_M(new Point(44, 25));
		mc.released_M(false);

		mc.pressed_M(new Point(0, 13));
		mc.dragged_M(new Point(54, 42));
		mc.released_M(false);
		
		mc.pressed_M(new Point(54, 42));
		mc.dragged_M(new Point(44, 25));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug2() throws Exception {

		mc.pressed_M(new Point(84, 86));
		mc.dragged_M(new Point(33, 97));
		mc.released_M(false);
		
		mc.pressed_M(new Point(52, 8));
		mc.dragged_M(new Point(30, 65));
		mc.dragged_M(new Point(19, 22));
		mc.dragged_M(new Point(77, 95));
		mc.dragged_M(new Point(30, 64));
		mc.released_M(false);

		mc.pressed_M(new Point(51, 35));
		mc.dragged_M(new Point(34, 74));
		mc.dragged_M(new Point(29, 90));
		mc.dragged_M(new Point(66, 99));
		mc.dragged_M(new Point(11, 22));
		mc.dragged_M(new Point(56, 85));
		mc.released_M(false);

		mc.pressed_M(new Point(58, 29));
		mc.dragged_M(new Point(31, 45));
		mc.dragged_M(new Point(44, 5));
		mc.dragged_M(new Point(50, 35));
		mc.dragged_M(new Point(40, 39));
		mc.dragged_M(new Point(50, 29));
		mc.released_M(false);

		mc.pressed_M(new Point(73, 26));
		mc.dragged_M(new Point(43, 96));
		mc.dragged_M(new Point(46, 49));
		mc.dragged_M(new Point(36, 4));
		mc.dragged_M(new Point(52, 66));
		mc.dragged_M(new Point(73, 10));
		mc.dragged_M(new Point(97, 69));
		mc.dragged_M(new Point(5, 66));
		mc.dragged_M(new Point(44, 89));
		mc.dragged_M(new Point(82, 80));
		mc.dragged_M(new Point(95, 89));
		mc.released_M(false);

		mc.pressed_M(new Point(31, 77));
		mc.dragged_M(new Point(45, 98));
		mc.dragged_M(new Point(5, 19));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug1() throws Exception {

		mc.pressed_M(new Point(84, 86));
		mc.dragged_M(new Point(33, 97));
		mc.released_M(false);

		mc.pressed_M(new Point(73, 26));
		mc.dragged_M(new Point(43, 96));
		mc.released_M(false);
		
		mc.pressed_M(new Point(43, 96));
		mc.dragged_M(new Point(46, 49));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug2() throws Exception {

		mc.pressed_M(new Point(60, 78));
		mc.dragged_M(new Point(1, 72));
		mc.dragged_M(new Point(47, 60));
		mc.released_M(false);

		mc.pressed_M(new Point(7, 17));
		mc.dragged_M(new Point(2, 78));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testXBCEqual0Bug() throws Exception {

		mc.pressed_M(new Point(45, 47));
		mc.dragged_M(new Point(7, 41));
		mc.released_M(false);
		
		mc.pressed_M(new Point(7, 41));
		mc.dragged_M(new Point(45, 81));
		mc.released_M(false);

		mc.pressed_M(new Point(13, 56));
		mc.dragged_M(new Point(4, 17));
		mc.released_M(false);

		mc.pressed_M(new Point(25, 62));
		mc.dragged_M(new Point(3, 34));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug3() throws Exception {

		mc.pressed_M(new Point(71, 26));
		mc.dragged_M(new Point(6, 91));
		mc.dragged_M(new Point(76, 24));
		mc.released_M(false);

		mc.pressed_M(new Point(91, 98));
		mc.dragged_M(new Point(3, 84));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testYBCEqual0Bug() throws Exception {

		mc.pressed_M(new Point(13, 47));
		mc.dragged_M(new Point(53, 43));
		mc.released_M(false);

		mc.pressed_M(new Point(17, 38));
		mc.dragged_M(new Point(36, 59));
		mc.dragged_M(new Point(100, 27));
		mc.released_M(false);

		mc.pressed_M(new Point(0, 59));
		mc.dragged_M(new Point(37, 36));
		mc.released_M(false);

		mc.pressed_M(new Point(19, 4));
		mc.dragged_M(new Point(26, 80));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug4() throws Exception {
		
		mc.pressed_M(new Point(21, 11));
		mc.dragged_M(new Point(23, 4));
		mc.released_M(false);
		
		mc.pressed_M(new Point(18, 11));
		mc.dragged_M(new Point(21, 11));
		mc.released_M(false);
		
		mc.pressed_M(new Point(21, 11));
		mc.dragged_M(new Point(21, 9));
		mc.released_M(false);
		
		mc.pressed_M(new Point(21, 9));
		mc.dragged_M(new Point(44, 94));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testInconsistent1() throws Exception {
		
		mc.pressed_M(new Point(21, 11));
		mc.dragged_M(new Point(23, 4));
		mc.released_M(false);
		
		mc.pressed_M(new Point(18, 11));
		mc.dragged_M(new Point(21, 11));
		mc.released_M(false);
		
		mc.pressed_M(new Point(18, 10));
		mc.dragged_M(new Point(21, 10));
		mc.released_M(false);
		
		mc.pressed_M(new Point(21, 11));
		mc.dragged_M(new Point(21, 9));
		mc.released_M(false);
		
		mc.pressed_M(new Point(21, 9));
		mc.dragged_M(new Point(44, 94));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testInconsistent2() throws Exception {
		
		mc.pressed_M(new Point(18, 11));
		mc.dragged_M(new Point(21, 11));
		mc.released_M(false);
		
		mc.pressed_M(new Point(18, 10));
		mc.dragged_M(new Point(21, 10));
		mc.released_M(false);
		
		mc.pressed_M(new Point(21, 11));
		mc.dragged_M(new Point(21, 9));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	
	@Test
	public void testRemoved() throws Exception {
		
		mc.pressed_M(new Point(703, 306));
		mc.dragged_M(new Point(703, 335));
		mc.released_M();
		
		mc.pressed_M(new Point(754, 329));
		mc.dragged_M(new Point(699, 311));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAssertFalse() throws Exception {
		
		mc.pressed_M(new Point(703, 306));
		mc.dragged_M(new Point(703, 320));
		mc.released_M();
		
		mc.pressed_M(new Point(717, 315));
		mc.dragged_M(new Point(684, 308));
		mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testAssertFalse2() throws Exception {
		
		mc.pressed_M(new Point(583, 442));
		mc.dragged_M(new Point(583, 447));
		mc.dragged_M(new Point(584, 449));
		mc.dragged_M(new Point(584, 450));
		mc.dragged_M(new Point(583, 442));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug3() throws Exception {

		mc.pressed_M(new Point(84, 86));
		mc.dragged_M(new Point(33, 97));
		mc.released_M(false);
		
		mc.pressed_M(new Point(52, 8));
		mc.dragged_M(new Point(30, 65));
		mc.dragged_M(new Point(19, 22));
		mc.dragged_M(new Point(77, 95));
		mc.dragged_M(new Point(30, 64));
		mc.released_M(false);

		mc.pressed_M(new Point(29, 90));
		mc.dragged_M(new Point(66, 99));
		mc.dragged_M(new Point(11, 22));
		mc.dragged_M(new Point(56, 85));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testCAndDAreEqualBug() throws Exception {

		mc.pressed_M(new Point(30, 65));
		mc.dragged_M(new Point(19, 22));
		mc.released_M(false);

		mc.pressed_M(new Point(66, 99));
		mc.dragged_M(new Point(11, 22));
		mc.released_M(false);
		
		mc.pressed_M(new Point(11, 22));
		mc.dragged_M(new Point(56, 85));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testNoEdgesShouldIntersectBug1() throws Exception {

		mc.pressed_M(new Point(73, 26));
		mc.dragged_M(new Point(43, 96));
		mc.dragged_M(new Point(46, 49));
		mc.released_M(false);

		mc.pressed_M(new Point(45, 98));
		mc.dragged_M(new Point(5, 19));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug1() throws Exception {
		
		mc.pressed_M(new Point(52, 8));
		mc.dragged_M(new Point(30, 65));
		mc.released_M(false);

		mc.pressed_M(new Point(51, 35));
		mc.dragged_M(new Point(34, 74));
		mc.dragged_M(new Point(66, 99));
		mc.dragged_M(new Point(11, 22));
		mc.released_M(false);
		
		
		mc.pressed_M(new Point(11, 22));
		mc.dragged_M(new Point(56, 85));
		mc.released_M(false);
		
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

				mc.pressed(new Point(42, 45));
				mc.dragged(new Point(43, 42));
				mc.released();
				
				mc.pressed(new Point(44, 44));
				mc.dragged(new Point(44, 37));
				mc.released();
				
				mc.pressed(new Point(30, 23));
				mc.dragged(new Point(63, 73));
				mc.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug3() throws Exception {

		mc.pressed_M(new Point(40, 19));
		mc.dragged_M(new Point(43, 61));
		mc.released_M(false);
		
		mc.pressed_M(new Point(99, 95));
		mc.dragged_M(new Point(38, 31));
		mc.released_M(false);

		mc.pressed_M(new Point(40, 35));
		mc.dragged_M(new Point(82, 8));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug4() throws Exception {

		mc.pressed_M(new Point(351, 140));
		mc.dragged_M(new Point(454, 11));
		mc.released_M(false);
		
		mc.pressed_M(new Point(454, 11));
		mc.dragged_M(new Point(436, 146));
		mc.released_M(false);
		
		mc.pressed_M(new Point(389, 36));
		mc.dragged_M(new Point(462, 273));
		mc.released_M(false);
		
		mc.pressed_M(new Point(216, 161));
		mc.dragged_M(new Point(491, 36));
		mc.released_M(false);
		
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
				
				mc.pressed(new Point(463, 221));
				mc.dragged(new Point(25, 28));
				mc.released();
				
				mc.pressed(new Point(274, 24));
				mc.dragged(new Point(34, 38));
				mc.released();
				
				mc.pressed(new Point(34, 38));
				mc.dragged(new Point(428, 78));
				mc.released();
				
				mc.pressed(new Point(46, 40));
				mc.dragged(new Point(364, 52));
				mc.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug6() throws Exception {
		
		mc.pressed_M(new Point(390, 91));
		mc.dragged_M(new Point(32, 324));
		mc.dragged_M(new Point(19, 246));
		mc.dragged_M(new Point(116, 147));
		mc.dragged_M(new Point(185, 261));
		mc.dragged_M(new Point(211, 398));
		mc.dragged_M(new Point(118, 34));
		mc.released_M(false);

		mc.pressed_M(new Point(100, 216));
		mc.dragged_M(new Point(262, 444));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAndroidBug() throws Exception {
		
		mc.pressed_M(new Point(135,455));
		mc.dragged_M(new Point(135,455));
		mc.dragged_M(new Point(130,445));
		mc.dragged_M(new Point(130,440));
		mc.dragged_M(new Point(130,440));
		mc.dragged_M(new Point(130,435));
		mc.dragged_M(new Point(125,430));
		mc.dragged_M(new Point(125,420));
		mc.dragged_M(new Point(125,420));
		mc.dragged_M(new Point(125,405));
		mc.dragged_M(new Point(125,400));
		mc.dragged_M(new Point(125,390));
		mc.dragged_M(new Point(125,385));
		mc.dragged_M(new Point(125,380));
		mc.dragged_M(new Point(125,375));
		mc.dragged_M(new Point(125,370));
		mc.dragged_M(new Point(125,360));
		mc.dragged_M(new Point(130,350));
		mc.dragged_M(new Point(135,330));
		mc.dragged_M(new Point(135,320));
		mc.dragged_M(new Point(140,305));
		mc.dragged_M(new Point(140,295));
		mc.dragged_M(new Point(150,280));
		mc.dragged_M(new Point(155,270));
		mc.dragged_M(new Point(160,260));
		mc.dragged_M(new Point(165,250));
		mc.dragged_M(new Point(170,245));
		mc.dragged_M(new Point(180,240));
		mc.dragged_M(new Point(185,235));
		mc.dragged_M(new Point(195,225));
		mc.dragged_M(new Point(195,225));
		mc.dragged_M(new Point(200,220));
		mc.dragged_M(new Point(205,220));
		mc.dragged_M(new Point(215,215));
		mc.dragged_M(new Point(215,215));
		mc.dragged_M(new Point(220,210));
		mc.dragged_M(new Point(220,210));
		mc.dragged_M(new Point(225,210));
		mc.dragged_M(new Point(225,210));
		mc.dragged_M(new Point(225,210));
		mc.dragged_M(new Point(225,210));
		mc.dragged_M(new Point(225,210));
		mc.dragged_M(new Point(225,210));
		mc.dragged_M(new Point(230,205));
		mc.released_M(true);
		mc.pressed_M(new Point(95,655));
		mc.dragged_M(new Point(95,655));
		mc.dragged_M(new Point(95,655));
		mc.dragged_M(new Point(95,655));
		mc.dragged_M(new Point(110,655));
		mc.dragged_M(new Point(125,655));
		mc.dragged_M(new Point(170,650));
		mc.dragged_M(new Point(180,650));
		mc.dragged_M(new Point(200,645));
		mc.dragged_M(new Point(230,635));
		mc.dragged_M(new Point(235,635));
		mc.dragged_M(new Point(235,635));
		mc.dragged_M(new Point(235,635));
		mc.dragged_M(new Point(235,635));
		mc.dragged_M(new Point(235,635));
		mc.dragged_M(new Point(240,635));
		mc.dragged_M(new Point(240,635));
		mc.dragged_M(new Point(240,635));
		mc.dragged_M(new Point(240,635));
		mc.dragged_M(new Point(240,635));
		mc.dragged_M(new Point(240,635));
		mc.dragged_M(new Point(240,635));
		mc.released_M(true);
		mc.pressed_M(new Point(245,640));
		mc.dragged_M(new Point(245,640));
		mc.dragged_M(new Point(245,640));
		mc.dragged_M(new Point(245,640));
		mc.dragged_M(new Point(250,640));
		mc.dragged_M(new Point(260,635));
		mc.dragged_M(new Point(265,635));
		mc.dragged_M(new Point(270,635));
		mc.dragged_M(new Point(280,635));
		mc.dragged_M(new Point(290,630));
		mc.dragged_M(new Point(305,630));
		mc.dragged_M(new Point(310,625));
		mc.dragged_M(new Point(315,625));
		mc.dragged_M(new Point(330,620));
		mc.dragged_M(new Point(330,615));
		mc.dragged_M(new Point(330,615));
		mc.dragged_M(new Point(330,615));
		mc.dragged_M(new Point(330,615));
		mc.dragged_M(new Point(330,615));
		mc.dragged_M(new Point(335,615));
		mc.released_M(true);
		mc.pressed_M(new Point(230,555));
		mc.dragged_M(new Point(235,545));
		mc.dragged_M(new Point(235,530));
		mc.dragged_M(new Point(235,515));
		mc.dragged_M(new Point(240,500));
		mc.dragged_M(new Point(240,485));
		mc.dragged_M(new Point(255,445));
		mc.dragged_M(new Point(255,445));
		mc.dragged_M(new Point(275,420));
		mc.dragged_M(new Point(280,420));
		mc.dragged_M(new Point(290,415));
		mc.dragged_M(new Point(295,410));
		mc.dragged_M(new Point(300,410));
		mc.dragged_M(new Point(300,410));
		mc.dragged_M(new Point(305,410));
		mc.dragged_M(new Point(305,410));
		mc.released_M(true);
		mc.pressed_M(new Point(320,420));
		mc.dragged_M(new Point(320,420));
		mc.dragged_M(new Point(320,420));
		mc.dragged_M(new Point(320,420));
		mc.dragged_M(new Point(320,405));
		mc.dragged_M(new Point(320,390));
		mc.dragged_M(new Point(325,380));
		mc.dragged_M(new Point(325,370));
		mc.dragged_M(new Point(330,350));
		mc.dragged_M(new Point(335,315));
		mc.dragged_M(new Point(335,310));
		mc.dragged_M(new Point(335,305));
		mc.dragged_M(new Point(340,285));
		mc.dragged_M(new Point(340,280));
		mc.dragged_M(new Point(350,260));
		mc.dragged_M(new Point(360,250));
		mc.dragged_M(new Point(360,240));
		mc.dragged_M(new Point(365,235));
		mc.dragged_M(new Point(365,235));
		mc.released_M(true);
		mc.pressed_M(new Point(240,195));
		mc.dragged_M(new Point(240,195));
		mc.dragged_M(new Point(240,195));
		mc.dragged_M(new Point(240,195));
		mc.dragged_M(new Point(240,180));
		mc.dragged_M(new Point(240,180));
		mc.dragged_M(new Point(245,165));
		mc.dragged_M(new Point(250,150));
		mc.dragged_M(new Point(260,130));
		mc.dragged_M(new Point(265,125));
		mc.dragged_M(new Point(265,125));
		mc.dragged_M(new Point(280,110));
		mc.dragged_M(new Point(280,110));
		mc.dragged_M(new Point(280,110));
		mc.dragged_M(new Point(285,105));
		mc.dragged_M(new Point(305,90));
		mc.dragged_M(new Point(305,90));
		mc.dragged_M(new Point(310,85));
		mc.dragged_M(new Point(315,85));
		mc.dragged_M(new Point(315,85));
		mc.released_M(true);
		mc.pressed_M(new Point(190,80));
		mc.dragged_M(new Point(190,80));
		mc.dragged_M(new Point(205,80));
		mc.dragged_M(new Point(210,75));
		mc.dragged_M(new Point(215,75));
		mc.dragged_M(new Point(220,75));
		mc.dragged_M(new Point(225,75));
		mc.dragged_M(new Point(245,75));
		mc.dragged_M(new Point(265,75));
		mc.dragged_M(new Point(275,75));
		mc.dragged_M(new Point(285,75));
		mc.dragged_M(new Point(290,75));
		mc.dragged_M(new Point(295,80));
		mc.dragged_M(new Point(300,80));
		mc.dragged_M(new Point(310,80));
		mc.dragged_M(new Point(310,80));
		mc.dragged_M(new Point(315,80));
		mc.dragged_M(new Point(315,85));
		mc.dragged_M(new Point(320,85));
		mc.dragged_M(new Point(320,85));
		mc.released_M(true);
		mc.pressed_M(new Point(385,220));
		mc.dragged_M(new Point(385,220));
		mc.dragged_M(new Point(385,220));
		mc.dragged_M(new Point(380,225));
		mc.dragged_M(new Point(375,225));
		mc.dragged_M(new Point(370,225));
		mc.dragged_M(new Point(365,225));
		mc.dragged_M(new Point(360,225));
		mc.dragged_M(new Point(355,230));
		mc.dragged_M(new Point(345,230));
		mc.dragged_M(new Point(330,235));
		mc.dragged_M(new Point(320,240));
		mc.dragged_M(new Point(315,240));
		mc.dragged_M(new Point(310,245));
		mc.dragged_M(new Point(300,245));
		mc.dragged_M(new Point(295,250));
		mc.dragged_M(new Point(280,255));
		mc.dragged_M(new Point(270,260));
		mc.dragged_M(new Point(265,265));
		mc.dragged_M(new Point(265,265));
		mc.dragged_M(new Point(260,265));
		mc.dragged_M(new Point(255,270));
		mc.dragged_M(new Point(250,270));
		mc.dragged_M(new Point(250,275));
		mc.dragged_M(new Point(245,275));
		mc.dragged_M(new Point(245,275));
		mc.dragged_M(new Point(245,275));
		mc.dragged_M(new Point(240,280));
		mc.dragged_M(new Point(240,280));
		mc.dragged_M(new Point(240,280));
		mc.dragged_M(new Point(240,280));
		mc.dragged_M(new Point(240,280));
		mc.dragged_M(new Point(240,285));
		mc.released_M(true);
		mc.pressed_M(new Point(140,460));
		mc.dragged_M(new Point(140,460));
		mc.dragged_M(new Point(150,450));
		mc.dragged_M(new Point(150,440));
		mc.dragged_M(new Point(160,435));
		mc.dragged_M(new Point(170,420));
		mc.dragged_M(new Point(180,405));
		mc.dragged_M(new Point(185,395));
		mc.dragged_M(new Point(190,390));
		mc.dragged_M(new Point(190,385));
		mc.dragged_M(new Point(195,380));
		mc.dragged_M(new Point(200,375));
		mc.dragged_M(new Point(200,370));
		mc.dragged_M(new Point(200,365));
		mc.dragged_M(new Point(205,365));
		mc.dragged_M(new Point(205,360));
		mc.dragged_M(new Point(210,355));
		mc.dragged_M(new Point(215,350));
		mc.dragged_M(new Point(220,345));
		mc.dragged_M(new Point(225,345));
		mc.dragged_M(new Point(230,340));
		mc.dragged_M(new Point(235,330));
		mc.dragged_M(new Point(235,325));
		mc.dragged_M(new Point(240,320));
		mc.dragged_M(new Point(245,315));
		mc.dragged_M(new Point(245,315));
		mc.dragged_M(new Point(245,310));
		mc.dragged_M(new Point(250,310));
		mc.dragged_M(new Point(250,310));
		mc.dragged_M(new Point(250,310));
		mc.dragged_M(new Point(250,305));
		mc.dragged_M(new Point(250,305));
		mc.released_M(true);
		mc.pressed_M(new Point(195,80));
		mc.dragged_M(new Point(195,80));
		mc.dragged_M(new Point(195,80));
		mc.dragged_M(new Point(180,95));
		mc.dragged_M(new Point(170,100));
		mc.dragged_M(new Point(155,115));
		mc.dragged_M(new Point(145,125));
		mc.dragged_M(new Point(135,135));
		mc.dragged_M(new Point(125,145));
		mc.dragged_M(new Point(120,155));
		mc.dragged_M(new Point(115,160));
		mc.dragged_M(new Point(110,165));
		mc.dragged_M(new Point(110,170));
		mc.dragged_M(new Point(105,185));
		mc.dragged_M(new Point(100,190));
		mc.dragged_M(new Point(100,195));
		mc.dragged_M(new Point(95,200));
		mc.dragged_M(new Point(95,205));
		mc.dragged_M(new Point(90,210));
		mc.dragged_M(new Point(90,220));
		mc.dragged_M(new Point(85,240));
		mc.dragged_M(new Point(80,245));
		mc.dragged_M(new Point(80,250));
		mc.dragged_M(new Point(75,265));
		mc.dragged_M(new Point(75,275));
		mc.dragged_M(new Point(75,285));
		mc.dragged_M(new Point(70,305));
		mc.dragged_M(new Point(70,315));
		mc.dragged_M(new Point(70,325));
		mc.dragged_M(new Point(65,350));
		mc.dragged_M(new Point(65,360));
		mc.dragged_M(new Point(65,370));
		mc.dragged_M(new Point(60,390));
		mc.dragged_M(new Point(60,400));
		mc.dragged_M(new Point(60,410));
		mc.dragged_M(new Point(60,415));
		mc.dragged_M(new Point(60,425));
		mc.dragged_M(new Point(55,435));
		mc.dragged_M(new Point(55,445));
		mc.dragged_M(new Point(55,455));
		mc.dragged_M(new Point(55,465));
		mc.dragged_M(new Point(60,475));
		mc.dragged_M(new Point(60,485));
		mc.dragged_M(new Point(60,495));
		mc.dragged_M(new Point(60,505));
		mc.dragged_M(new Point(65,515));
		mc.dragged_M(new Point(70,520));
		mc.dragged_M(new Point(70,530));
		mc.dragged_M(new Point(75,535));
		mc.dragged_M(new Point(75,535));
		mc.dragged_M(new Point(80,540));
		mc.dragged_M(new Point(80,540));
		mc.dragged_M(new Point(85,545));
		mc.dragged_M(new Point(85,545));
		mc.dragged_M(new Point(85,550));
		mc.dragged_M(new Point(90,550));
		mc.dragged_M(new Point(90,550));
		mc.released_M(true);
		mc.pressed_M(new Point(100,655));
		mc.dragged_M(new Point(100,655));
		mc.dragged_M(new Point(100,645));
		mc.dragged_M(new Point(100,640));
		mc.dragged_M(new Point(95,630));
		mc.dragged_M(new Point(95,620));
		mc.dragged_M(new Point(95,615));
		mc.dragged_M(new Point(95,610));
		mc.dragged_M(new Point(95,600));
		mc.dragged_M(new Point(95,595));
		mc.dragged_M(new Point(95,590));
		mc.dragged_M(new Point(95,585));
		mc.dragged_M(new Point(95,580));
		mc.dragged_M(new Point(90,580));
		mc.dragged_M(new Point(90,575));
		mc.dragged_M(new Point(90,575));
		mc.dragged_M(new Point(90,575));
		mc.dragged_M(new Point(90,570));
		mc.dragged_M(new Point(90,570));
		mc.dragged_M(new Point(90,570));
		mc.dragged_M(new Point(90,570));
		mc.dragged_M(new Point(90,570));
		mc.dragged_M(new Point(90,570));
		mc.released_M(true);
		mc.pressed_M(new Point(245,565));
		mc.dragged_M(new Point(245,565));
		mc.dragged_M(new Point(250,570));
		mc.dragged_M(new Point(265,575));
		mc.dragged_M(new Point(265,575));
		mc.dragged_M(new Point(275,575));
		mc.dragged_M(new Point(280,580));
		mc.dragged_M(new Point(290,585));
		mc.dragged_M(new Point(300,590));
		mc.dragged_M(new Point(305,590));
		mc.dragged_M(new Point(310,590));
		mc.dragged_M(new Point(315,595));
		mc.dragged_M(new Point(315,595));
		mc.dragged_M(new Point(320,600));
		mc.dragged_M(new Point(320,600));
		mc.dragged_M(new Point(325,600));
		mc.dragged_M(new Point(325,600));
		mc.dragged_M(new Point(325,600));
		mc.dragged_M(new Point(330,600));
		mc.dragged_M(new Point(330,605));
		mc.released_M(true);
		mc.pressed_M(new Point(390,510));
		mc.dragged_M(new Point(385,500));
		mc.dragged_M(new Point(380,495));
		mc.dragged_M(new Point(375,495));
		mc.dragged_M(new Point(355,475));
		mc.dragged_M(new Point(335,450));
		mc.dragged_M(new Point(320,445));
		mc.dragged_M(new Point(300,425));
		mc.dragged_M(new Point(275,415));
		mc.dragged_M(new Point(265,405));
		mc.dragged_M(new Point(260,400));
		mc.dragged_M(new Point(245,390));
		mc.dragged_M(new Point(230,385));
		mc.dragged_M(new Point(210,370));
		mc.dragged_M(new Point(195,365));
		mc.dragged_M(new Point(180,360));
		mc.dragged_M(new Point(170,355));
		mc.dragged_M(new Point(160,345));
		mc.dragged_M(new Point(140,340));
		mc.dragged_M(new Point(125,325));
		mc.dragged_M(new Point(115,320));
		mc.dragged_M(new Point(105,315));
		mc.dragged_M(new Point(100,310));
		mc.dragged_M(new Point(90,305));
		mc.dragged_M(new Point(75,300));
		mc.dragged_M(new Point(75,300));
		mc.dragged_M(new Point(65,295));
		mc.dragged_M(new Point(65,295));
		mc.dragged_M(new Point(60,290));
		mc.dragged_M(new Point(60,290));
		mc.dragged_M(new Point(60,290));
		mc.released_M(true);


		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug7() throws Exception {

		mc.pressed_M(new Point(661, 568));
		mc.dragged_M(new Point(664, 568));
		mc.dragged_M(new Point(664, 567));
		mc.dragged_M(new Point(665, 567));
		mc.released(false);
		
		mc.pressed_M(new Point(665, 567));
		mc.dragged_M(new Point(661, 570));
		mc.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
}
