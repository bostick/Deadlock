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

public class TestDragging {

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
	
	@Test
	public void test1() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(5, 5));
		CONTROLLER.mouseController.dragged_M(new Point(6, 6));
		CONTROLLER.mouseController.dragged_M(new Point(7, 7));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.getEdges();
			}
		});
		
		assertEquals(edges.size(), 1);
		Edge e = edges.get(0);
		assertEquals(e.getPointsSize(), 2);
		assertEquals(new Point(5, 5), e.getPoint(0));
		assertEquals(new Point(7, 7), e.getPoint(1));
		
	}
	
	@Test
	public void test2() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(5, 5));
		CONTROLLER.mouseController.dragged_M(new Point(6, 6));
		CONTROLLER.mouseController.dragged_M(new Point(4, 4));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.getEdges();
			}
		});
		 
		assertEquals(edges.size(),  1);
		Edge e = edges.get(0);
		assertEquals(new Point(6, 6), e.getStart().getPoint());
		assertEquals(new Point(4, 4), e.getEnd().getPoint());
		
	}
	
	@Test
	public void test3() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(267, 581));
		CONTROLLER.mouseController.dragged_M(new Point(267, 580));
		CONTROLLER.mouseController.dragged_M(new Point(267, 582));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test4() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(2, 2));
		CONTROLLER.mouseController.dragged_M(new Point(1, 2));
		CONTROLLER.mouseController.dragged_M(new Point(1, 1));
		CONTROLLER.mouseController.dragged_M(new Point(1, 3));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test5() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(532, 627));
		CONTROLLER.mouseController.dragged_M(new Point(515, 620));
		CONTROLLER.mouseController.dragged_M(new Point(500, 612));
		CONTROLLER.mouseController.dragged_M(new Point(480, 598));
		CONTROLLER.mouseController.dragged_M(new Point(443, 565));
		CONTROLLER.mouseController.dragged_M(new Point(415, 534));
		CONTROLLER.mouseController.dragged_M(new Point(397, 513));
		CONTROLLER.mouseController.dragged_M(new Point(383, 493));
		CONTROLLER.mouseController.dragged_M(new Point(377, 473));
		CONTROLLER.mouseController.dragged_M(new Point(372, 446));
		CONTROLLER.mouseController.dragged_M(new Point(372, 424));
		CONTROLLER.mouseController.dragged_M(new Point(372, 410));
		CONTROLLER.mouseController.dragged_M(new Point(372, 396));
		CONTROLLER.mouseController.dragged_M(new Point(372, 380));
		CONTROLLER.mouseController.dragged_M(new Point(373, 363));
		CONTROLLER.mouseController.dragged_M(new Point(380, 343));
		CONTROLLER.mouseController.dragged_M(new Point(390, 331));
		CONTROLLER.mouseController.dragged_M(new Point(403, 320));
		CONTROLLER.mouseController.dragged_M(new Point(420, 314));
		CONTROLLER.mouseController.dragged_M(new Point(434, 310));
		CONTROLLER.mouseController.dragged_M(new Point(449, 306));
		CONTROLLER.mouseController.dragged_M(new Point(460, 306));
		CONTROLLER.mouseController.dragged_M(new Point(471, 306));
		CONTROLLER.mouseController.dragged_M(new Point(498, 306));
		CONTROLLER.mouseController.dragged_M(new Point(518, 309));
		CONTROLLER.mouseController.dragged_M(new Point(540, 316));
		CONTROLLER.mouseController.dragged_M(new Point(556, 331));
		CONTROLLER.mouseController.dragged_M(new Point(571, 344));
		CONTROLLER.mouseController.dragged_M(new Point(582, 359));
		CONTROLLER.mouseController.dragged_M(new Point(592, 367));
		CONTROLLER.mouseController.dragged_M(new Point(597, 374));
		CONTROLLER.mouseController.dragged_M(new Point(604, 382));
		CONTROLLER.mouseController.dragged_M(new Point(607, 386));
		CONTROLLER.mouseController.dragged_M(new Point(610, 389));
		CONTROLLER.mouseController.dragged_M(new Point(614, 398));
		CONTROLLER.mouseController.dragged_M(new Point(616, 412));
		CONTROLLER.mouseController.dragged_M(new Point(618, 453));
		CONTROLLER.mouseController.dragged_M(new Point(618, 472));
		CONTROLLER.mouseController.dragged_M(new Point(617, 486));
		CONTROLLER.mouseController.dragged_M(new Point(613, 506));
		CONTROLLER.mouseController.dragged_M(new Point(606, 533));
		CONTROLLER.mouseController.dragged_M(new Point(599, 556));
		CONTROLLER.mouseController.dragged_M(new Point(590, 571));
		CONTROLLER.mouseController.dragged_M(new Point(580, 583));
		CONTROLLER.mouseController.dragged_M(new Point(565, 599));
		CONTROLLER.mouseController.dragged_M(new Point(542, 615));
		CONTROLLER.mouseController.dragged_M(new Point(521, 635));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test6() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(5, 5));
		CONTROLLER.mouseController.dragged_M(new Point(7, 7));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(6, 6));
		CONTROLLER.mouseController.dragged_M(new Point(4, 4));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test7() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(5, 5));
		CONTROLLER.mouseController.dragged_M(new Point(6, 6));
		CONTROLLER.mouseController.dragged_M(new Point(7, 7));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(5, 5));
		CONTROLLER.mouseController.pressed_M(new Point(6, 6));
		CONTROLLER.mouseController.dragged_M(new Point(4, 4));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(267, 581));
		CONTROLLER.mouseController.dragged_M(new Point(267, 580));
		CONTROLLER.mouseController.dragged_M(new Point(267, 582));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(2, 2));
		CONTROLLER.mouseController.dragged_M(new Point(1, 2));
		CONTROLLER.mouseController.dragged_M(new Point(1, 1));
		CONTROLLER.mouseController.dragged_M(new Point(1, 3));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(532, 627));
		CONTROLLER.mouseController.dragged_M(new Point(515, 620));
		CONTROLLER.mouseController.dragged_M(new Point(500, 612));
		CONTROLLER.mouseController.dragged_M(new Point(480, 598));
		CONTROLLER.mouseController.dragged_M(new Point(443, 565));
		CONTROLLER.mouseController.dragged_M(new Point(415, 534));
		CONTROLLER.mouseController.dragged_M(new Point(397, 513));
		CONTROLLER.mouseController.dragged_M(new Point(383, 493));
		CONTROLLER.mouseController.dragged_M(new Point(377, 473));
		CONTROLLER.mouseController.dragged_M(new Point(372, 446));
		CONTROLLER.mouseController.dragged_M(new Point(372, 424));
		CONTROLLER.mouseController.dragged_M(new Point(372, 410));
		CONTROLLER.mouseController.dragged_M(new Point(372, 396));
		CONTROLLER.mouseController.dragged_M(new Point(372, 380));
		CONTROLLER.mouseController.dragged_M(new Point(373, 363));
		CONTROLLER.mouseController.dragged_M(new Point(380, 343));
		CONTROLLER.mouseController.dragged_M(new Point(390, 331));
		CONTROLLER.mouseController.dragged_M(new Point(403, 320));
		CONTROLLER.mouseController.dragged_M(new Point(420, 314));
		CONTROLLER.mouseController.dragged_M(new Point(434, 310));
		CONTROLLER.mouseController.dragged_M(new Point(449, 306));
		CONTROLLER.mouseController.dragged_M(new Point(460, 306));
		CONTROLLER.mouseController.dragged_M(new Point(471, 306));
		CONTROLLER.mouseController.dragged_M(new Point(498, 306));
		CONTROLLER.mouseController.dragged_M(new Point(518, 309));
		CONTROLLER.mouseController.dragged_M(new Point(540, 316));
		CONTROLLER.mouseController.dragged_M(new Point(556, 331));
		CONTROLLER.mouseController.dragged_M(new Point(571, 344));
		CONTROLLER.mouseController.dragged_M(new Point(582, 359));
		CONTROLLER.mouseController.dragged_M(new Point(592, 367));
		CONTROLLER.mouseController.dragged_M(new Point(597, 374));
		CONTROLLER.mouseController.dragged_M(new Point(604, 382));
		CONTROLLER.mouseController.dragged_M(new Point(607, 386));
		CONTROLLER.mouseController.dragged_M(new Point(610, 389));
		CONTROLLER.mouseController.dragged_M(new Point(614, 398));
		CONTROLLER.mouseController.dragged_M(new Point(616, 412));
		CONTROLLER.mouseController.dragged_M(new Point(618, 453));
		CONTROLLER.mouseController.dragged_M(new Point(618, 472));
		CONTROLLER.mouseController.dragged_M(new Point(617, 486));
		CONTROLLER.mouseController.dragged_M(new Point(613, 506));
		CONTROLLER.mouseController.dragged_M(new Point(606, 533));
		CONTROLLER.mouseController.dragged_M(new Point(599, 556));
		CONTROLLER.mouseController.dragged_M(new Point(590, 571));
		CONTROLLER.mouseController.dragged_M(new Point(580, 583));
		CONTROLLER.mouseController.dragged_M(new Point(565, 599));
		CONTROLLER.mouseController.dragged_M(new Point(542, 615));
		CONTROLLER.mouseController.dragged_M(new Point(521, 635));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(841, 101));
		CONTROLLER.mouseController.dragged_M(new Point(838, 102));
		CONTROLLER.mouseController.dragged_M(new Point(830, 104));
		CONTROLLER.mouseController.dragged_M(new Point(823, 105));
		CONTROLLER.mouseController.dragged_M(new Point(814, 109));
		CONTROLLER.mouseController.dragged_M(new Point(804, 115));
		CONTROLLER.mouseController.dragged_M(new Point(798, 118));
		CONTROLLER.mouseController.dragged_M(new Point(792, 125));
		CONTROLLER.mouseController.dragged_M(new Point(787, 135));
		CONTROLLER.mouseController.dragged_M(new Point(779, 145));
		CONTROLLER.mouseController.dragged_M(new Point(772, 150));
		CONTROLLER.mouseController.dragged_M(new Point(762, 158));
		CONTROLLER.mouseController.dragged_M(new Point(752, 168));
		CONTROLLER.mouseController.dragged_M(new Point(742, 178));
		CONTROLLER.mouseController.dragged_M(new Point(730, 201));
		CONTROLLER.mouseController.dragged_M(new Point(715, 241));
		CONTROLLER.mouseController.dragged_M(new Point(711, 255));
		CONTROLLER.mouseController.dragged_M(new Point(705, 276));
		CONTROLLER.mouseController.dragged_M(new Point(705, 287));
		CONTROLLER.mouseController.dragged_M(new Point(703, 306));
		CONTROLLER.mouseController.dragged_M(new Point(703, 320));
		CONTROLLER.mouseController.dragged_M(new Point(703, 328));
		CONTROLLER.mouseController.dragged_M(new Point(703, 335));
		CONTROLLER.mouseController.dragged_M(new Point(707, 345));
		CONTROLLER.mouseController.dragged_M(new Point(711, 348));
		CONTROLLER.mouseController.dragged_M(new Point(718, 352));
		CONTROLLER.mouseController.dragged_M(new Point(729, 356));
		CONTROLLER.mouseController.dragged_M(new Point(739, 359));
		CONTROLLER.mouseController.dragged_M(new Point(748, 363));
		CONTROLLER.mouseController.dragged_M(new Point(755, 366));
		CONTROLLER.mouseController.dragged_M(new Point(761, 368));
		CONTROLLER.mouseController.dragged_M(new Point(767, 370));
		CONTROLLER.mouseController.dragged_M(new Point(770, 371));
		CONTROLLER.mouseController.dragged_M(new Point(773, 372));
		CONTROLLER.mouseController.dragged_M(new Point(774, 372));
		CONTROLLER.mouseController.dragged_M(new Point(775, 372));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(775, 335));
		CONTROLLER.mouseController.dragged_M(new Point(774, 333));
		CONTROLLER.mouseController.dragged_M(new Point(771, 333));
		CONTROLLER.mouseController.dragged_M(new Point(767, 332));
		CONTROLLER.mouseController.dragged_M(new Point(760, 330));
		CONTROLLER.mouseController.dragged_M(new Point(754, 329));
		CONTROLLER.mouseController.dragged_M(new Point(748, 327));
		CONTROLLER.mouseController.dragged_M(new Point(743, 324));
		CONTROLLER.mouseController.dragged_M(new Point(737, 323));
		CONTROLLER.mouseController.dragged_M(new Point(732, 319));
		CONTROLLER.mouseController.dragged_M(new Point(728, 318));
		CONTROLLER.mouseController.dragged_M(new Point(723, 316));
		CONTROLLER.mouseController.dragged_M(new Point(717, 315));
		CONTROLLER.mouseController.dragged_M(new Point(708, 313));
		CONTROLLER.mouseController.dragged_M(new Point(699, 311));
		CONTROLLER.mouseController.dragged_M(new Point(690, 310));
		CONTROLLER.mouseController.dragged_M(new Point(684, 308));
		CONTROLLER.mouseController.dragged_M(new Point(673, 306));
		CONTROLLER.mouseController.dragged_M(new Point(670, 303));
		CONTROLLER.mouseController.dragged_M(new Point(666, 302));
		CONTROLLER.mouseController.dragged_M(new Point(661, 302));
		CONTROLLER.mouseController.dragged_M(new Point(659, 301));
		CONTROLLER.mouseController.dragged_M(new Point(656, 299));
		CONTROLLER.mouseController.dragged_M(new Point(653, 298));
		CONTROLLER.mouseController.dragged_M(new Point(650, 296));
		CONTROLLER.mouseController.dragged_M(new Point(649, 295));
		CONTROLLER.mouseController.dragged_M(new Point(647, 294));
		CONTROLLER.mouseController.dragged_M(new Point(645, 293));
		CONTROLLER.mouseController.dragged_M(new Point(643, 293));
		CONTROLLER.mouseController.dragged_M(new Point(643, 292));
		CONTROLLER.mouseController.dragged_M(new Point(642, 292));
		CONTROLLER.mouseController.dragged_M(new Point(643, 292));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test8() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(532, 627));
		CONTROLLER.mouseController.dragged_M(new Point(515, 620));
		CONTROLLER.mouseController.dragged_M(new Point(500, 612));
		CONTROLLER.mouseController.dragged_M(new Point(480, 598));
		CONTROLLER.mouseController.dragged_M(new Point(443, 565));
		CONTROLLER.mouseController.dragged_M(new Point(415, 534));
		CONTROLLER.mouseController.dragged_M(new Point(397, 513));
		CONTROLLER.mouseController.dragged_M(new Point(383, 493));
		CONTROLLER.mouseController.dragged_M(new Point(377, 473));
		CONTROLLER.mouseController.dragged_M(new Point(372, 446));
		CONTROLLER.mouseController.dragged_M(new Point(372, 424));
		CONTROLLER.mouseController.dragged_M(new Point(372, 410));
		CONTROLLER.mouseController.dragged_M(new Point(372, 396));
		CONTROLLER.mouseController.dragged_M(new Point(372, 380));
		CONTROLLER.mouseController.dragged_M(new Point(373, 363));
		CONTROLLER.mouseController.dragged_M(new Point(380, 343));
		CONTROLLER.mouseController.dragged_M(new Point(390, 331));
		CONTROLLER.mouseController.dragged_M(new Point(403, 320));
		CONTROLLER.mouseController.dragged_M(new Point(420, 314));
		CONTROLLER.mouseController.dragged_M(new Point(434, 310));
		CONTROLLER.mouseController.dragged_M(new Point(449, 306));
		CONTROLLER.mouseController.dragged_M(new Point(460, 306));
		CONTROLLER.mouseController.dragged_M(new Point(471, 306));
		CONTROLLER.mouseController.dragged_M(new Point(498, 306));
		CONTROLLER.mouseController.dragged_M(new Point(518, 309));
		CONTROLLER.mouseController.dragged_M(new Point(540, 316));
		CONTROLLER.mouseController.dragged_M(new Point(556, 331));
		CONTROLLER.mouseController.dragged_M(new Point(571, 344));
		CONTROLLER.mouseController.dragged_M(new Point(582, 359));
		CONTROLLER.mouseController.dragged_M(new Point(592, 367));
		CONTROLLER.mouseController.dragged_M(new Point(597, 374));
		CONTROLLER.mouseController.dragged_M(new Point(604, 382));
		CONTROLLER.mouseController.dragged_M(new Point(607, 386));
		CONTROLLER.mouseController.dragged_M(new Point(610, 389));
		CONTROLLER.mouseController.dragged_M(new Point(614, 398));
		CONTROLLER.mouseController.dragged_M(new Point(616, 412));
		CONTROLLER.mouseController.dragged_M(new Point(618, 453));
		CONTROLLER.mouseController.dragged_M(new Point(618, 472));
		CONTROLLER.mouseController.dragged_M(new Point(617, 486));
		CONTROLLER.mouseController.dragged_M(new Point(613, 506));
		CONTROLLER.mouseController.dragged_M(new Point(606, 533));
		CONTROLLER.mouseController.dragged_M(new Point(599, 556));
		CONTROLLER.mouseController.dragged_M(new Point(590, 571));
		CONTROLLER.mouseController.dragged_M(new Point(580, 583));
		CONTROLLER.mouseController.dragged_M(new Point(565, 599));
		CONTROLLER.mouseController.dragged_M(new Point(542, 615));
		CONTROLLER.mouseController.dragged_M(new Point(521, 635));
		CONTROLLER.mouseController.released_M();
		
		// break the loop
		CONTROLLER.mouseController.pressed_M(new Point(619, 448));
		CONTROLLER.mouseController.dragged_M(new Point(580, 423));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void test9() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(0, 0));
		CONTROLLER.mouseController.dragged_M(new Point(150, 450));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(200, 0));
		CONTROLLER.mouseController.dragged_M(new Point(0, 50));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testLoop() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(2, 0));
		CONTROLLER.mouseController.dragged_M(new Point(1, 0));
		CONTROLLER.mouseController.dragged_M(new Point(1, 1));
		CONTROLLER.mouseController.dragged_M(new Point(3, 0));
		CONTROLLER.mouseController.dragged_M(new Point(2, 0));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testLoop2() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(2, 5));
		CONTROLLER.mouseController.dragged_M(new Point(1, 5));
		CONTROLLER.mouseController.dragged_M(new Point(1, 10));
		CONTROLLER.mouseController.dragged_M(new Point(12, 5));
		CONTROLLER.mouseController.dragged_M(new Point(2, 5));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(3, 0));
		CONTROLLER.mouseController.dragged_M(new Point(3, 6));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testIntersection() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(4, 0));
		CONTROLLER.mouseController.dragged_M(new Point(10, 12));
		CONTROLLER.mouseController.released_M();

		CONTROLLER.mouseController.pressed_M(new Point(0, 12));
		CONTROLLER.mouseController.dragged_M(new Point(7, 7));
		CONTROLLER.mouseController.dragged_M(new Point(16, 1));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug1() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(532, 419));
		CONTROLLER.mouseController.dragged_M(new Point(583, 442));
		CONTROLLER.mouseController.dragged_M(new Point(583, 447));
		CONTROLLER.mouseController.dragged_M(new Point(584, 448));
		CONTROLLER.mouseController.dragged_M(new Point(584, 449));
		CONTROLLER.mouseController.dragged_M(new Point(584, 450));
		CONTROLLER.mouseController.dragged_M(new Point(585, 450));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(555, 448));
		CONTROLLER.mouseController.dragged_M(new Point(556, 448));
		CONTROLLER.mouseController.dragged_M(new Point(579, 444));
		CONTROLLER.mouseController.dragged_M(new Point(580, 444));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(580, 444));
		CONTROLLER.mouseController.dragged_M(new Point(581, 444));
		CONTROLLER.mouseController.dragged_M(new Point(592, 448));
		CONTROLLER.mouseController.dragged_M(new Point(593, 448));
		CONTROLLER.mouseController.dragged_M(new Point(594, 448));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(594, 448));
		CONTROLLER.mouseController.dragged_M(new Point(597, 451));
		CONTROLLER.mouseController.dragged_M(new Point(598, 454));
		CONTROLLER.mouseController.dragged_M(new Point(603, 455));
		CONTROLLER.mouseController.dragged_M(new Point(604, 456));
		CONTROLLER.mouseController.dragged_M(new Point(607, 458));
		CONTROLLER.mouseController.dragged_M(new Point(608, 458));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(608, 458));
		CONTROLLER.mouseController.dragged_M(new Point(608, 459));
		CONTROLLER.mouseController.dragged_M(new Point(607, 459));
		CONTROLLER.mouseController.dragged_M(new Point(592, 463));
		CONTROLLER.mouseController.dragged_M(new Point(587, 463));
		CONTROLLER.mouseController.dragged_M(new Point(583, 463));
		CONTROLLER.mouseController.released_M(true);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(639, 420));
		CONTROLLER.mouseController.dragged_M(new Point(637, 420));
		CONTROLLER.mouseController.dragged_M(new Point(631, 420));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(631, 420));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(631, 420));
		CONTROLLER.mouseController.dragged_M(new Point(630, 422));
		CONTROLLER.mouseController.dragged_M(new Point(629, 424));
		CONTROLLER.mouseController.dragged_M(new Point(623, 424));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(623, 424));
		CONTROLLER.mouseController.dragged_M(new Point(621, 421));
		CONTROLLER.mouseController.dragged_M(new Point(619, 420));
		CONTROLLER.mouseController.dragged_M(new Point(618, 420));
		CONTROLLER.mouseController.dragged_M(new Point(618, 419));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(618, 419));
		CONTROLLER.mouseController.dragged_M(new Point(626, 419));
		CONTROLLER.mouseController.dragged_M(new Point(627, 418));
		CONTROLLER.mouseController.dragged_M(new Point(629, 417));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(629, 417));
		CONTROLLER.mouseController.dragged_M(new Point(632, 417));
		CONTROLLER.mouseController.dragged_M(new Point(640, 415));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testEdgeCountNotEqual2Bug2() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(49, 100));
		CONTROLLER.mouseController.dragged_M(new Point(44, 25));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(0, 13));
		CONTROLLER.mouseController.dragged_M(new Point(54, 42));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(54, 42));
		CONTROLLER.mouseController.dragged_M(new Point(44, 25));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testXEqualsZBug2() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(84, 86));
		CONTROLLER.mouseController.dragged_M(new Point(33, 97));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(52, 8));
		CONTROLLER.mouseController.dragged_M(new Point(30, 65));
		CONTROLLER.mouseController.dragged_M(new Point(19, 22));
		CONTROLLER.mouseController.dragged_M(new Point(77, 95));
		CONTROLLER.mouseController.dragged_M(new Point(30, 64));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(51, 35));
		CONTROLLER.mouseController.dragged_M(new Point(34, 74));
		CONTROLLER.mouseController.dragged_M(new Point(29, 90));
		CONTROLLER.mouseController.dragged_M(new Point(66, 99));
		CONTROLLER.mouseController.dragged_M(new Point(11, 22));
		CONTROLLER.mouseController.dragged_M(new Point(56, 85));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(58, 29));
		CONTROLLER.mouseController.dragged_M(new Point(31, 45));
		CONTROLLER.mouseController.dragged_M(new Point(44, 5));
		CONTROLLER.mouseController.dragged_M(new Point(50, 35));
		CONTROLLER.mouseController.dragged_M(new Point(40, 39));
		CONTROLLER.mouseController.dragged_M(new Point(50, 29));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(73, 26));
		CONTROLLER.mouseController.dragged_M(new Point(43, 96));
		CONTROLLER.mouseController.dragged_M(new Point(46, 49));
		CONTROLLER.mouseController.dragged_M(new Point(36, 4));
		CONTROLLER.mouseController.dragged_M(new Point(52, 66));
		CONTROLLER.mouseController.dragged_M(new Point(73, 10));
		CONTROLLER.mouseController.dragged_M(new Point(97, 69));
		CONTROLLER.mouseController.dragged_M(new Point(5, 66));
		CONTROLLER.mouseController.dragged_M(new Point(44, 89));
		CONTROLLER.mouseController.dragged_M(new Point(82, 80));
		CONTROLLER.mouseController.dragged_M(new Point(95, 89));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(31, 77));
		CONTROLLER.mouseController.dragged_M(new Point(45, 98));
		CONTROLLER.mouseController.dragged_M(new Point(5, 19));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug1() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(84, 86));
		CONTROLLER.mouseController.dragged_M(new Point(33, 97));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(73, 26));
		CONTROLLER.mouseController.dragged_M(new Point(43, 96));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(43, 96));
		CONTROLLER.mouseController.dragged_M(new Point(46, 49));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug2() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(60, 78));
		CONTROLLER.mouseController.dragged_M(new Point(1, 72));
		CONTROLLER.mouseController.dragged_M(new Point(47, 60));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(7, 17));
		CONTROLLER.mouseController.dragged_M(new Point(2, 78));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testXBCEqual0Bug() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(45, 47));
		CONTROLLER.mouseController.dragged_M(new Point(7, 41));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(7, 41));
		CONTROLLER.mouseController.dragged_M(new Point(45, 81));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(13, 56));
		CONTROLLER.mouseController.dragged_M(new Point(4, 17));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(25, 62));
		CONTROLLER.mouseController.dragged_M(new Point(3, 34));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testColinearBug3() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(71, 26));
		CONTROLLER.mouseController.dragged_M(new Point(6, 91));
		CONTROLLER.mouseController.dragged_M(new Point(76, 24));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(91, 98));
		CONTROLLER.mouseController.dragged_M(new Point(3, 84));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testYBCEqual0Bug() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(13, 47));
		CONTROLLER.mouseController.dragged_M(new Point(53, 43));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(17, 38));
		CONTROLLER.mouseController.dragged_M(new Point(36, 59));
		CONTROLLER.mouseController.dragged_M(new Point(100, 27));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(0, 59));
		CONTROLLER.mouseController.dragged_M(new Point(37, 36));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(19, 4));
		CONTROLLER.mouseController.dragged_M(new Point(26, 80));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testColinearBug4() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 11));
		CONTROLLER.mouseController.dragged_M(new Point(23, 4));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(18, 11));
		CONTROLLER.mouseController.dragged_M(new Point(21, 11));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 11));
		CONTROLLER.mouseController.dragged_M(new Point(21, 9));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 9));
		CONTROLLER.mouseController.dragged_M(new Point(44, 94));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testInconsistent1() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 11));
		CONTROLLER.mouseController.dragged_M(new Point(23, 4));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(18, 11));
		CONTROLLER.mouseController.dragged_M(new Point(21, 11));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(18, 10));
		CONTROLLER.mouseController.dragged_M(new Point(21, 10));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 11));
		CONTROLLER.mouseController.dragged_M(new Point(21, 9));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 9));
		CONTROLLER.mouseController.dragged_M(new Point(44, 94));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testInconsistent2() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(18, 11));
		CONTROLLER.mouseController.dragged_M(new Point(21, 11));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(18, 10));
		CONTROLLER.mouseController.dragged_M(new Point(21, 10));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(21, 11));
		CONTROLLER.mouseController.dragged_M(new Point(21, 9));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	
	@Test
	public void testRemoved() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(703, 306));
		CONTROLLER.mouseController.dragged_M(new Point(703, 335));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(754, 329));
		CONTROLLER.mouseController.dragged_M(new Point(699, 311));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testAssertFalse() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(703, 306));
		CONTROLLER.mouseController.dragged_M(new Point(703, 320));
		CONTROLLER.mouseController.released_M();
		
		CONTROLLER.mouseController.pressed_M(new Point(717, 315));
		CONTROLLER.mouseController.dragged_M(new Point(684, 308));
		CONTROLLER.mouseController.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testAssertFalse2() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(583, 442));
		CONTROLLER.mouseController.dragged_M(new Point(583, 447));
		CONTROLLER.mouseController.dragged_M(new Point(584, 449));
		CONTROLLER.mouseController.dragged_M(new Point(584, 450));
		CONTROLLER.mouseController.dragged_M(new Point(583, 442));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testEdgeCountNotEqual2Bug3() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(84, 86));
		CONTROLLER.mouseController.dragged_M(new Point(33, 97));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(52, 8));
		CONTROLLER.mouseController.dragged_M(new Point(30, 65));
		CONTROLLER.mouseController.dragged_M(new Point(19, 22));
		CONTROLLER.mouseController.dragged_M(new Point(77, 95));
		CONTROLLER.mouseController.dragged_M(new Point(30, 64));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(29, 90));
		CONTROLLER.mouseController.dragged_M(new Point(66, 99));
		CONTROLLER.mouseController.dragged_M(new Point(11, 22));
		CONTROLLER.mouseController.dragged_M(new Point(56, 85));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testCAndDAreEqualBug() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(30, 65));
		CONTROLLER.mouseController.dragged_M(new Point(19, 22));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(66, 99));
		CONTROLLER.mouseController.dragged_M(new Point(11, 22));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(11, 22));
		CONTROLLER.mouseController.dragged_M(new Point(56, 85));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	
	@Test
	public void testNoEdgesShouldIntersectBug1() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(73, 26));
		CONTROLLER.mouseController.dragged_M(new Point(43, 96));
		CONTROLLER.mouseController.dragged_M(new Point(46, 49));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(45, 98));
		CONTROLLER.mouseController.dragged_M(new Point(5, 19));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
	
	@Test
	public void testBug1() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(52, 8));
		CONTROLLER.mouseController.dragged_M(new Point(30, 65));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(51, 35));
		CONTROLLER.mouseController.dragged_M(new Point(34, 74));
		CONTROLLER.mouseController.dragged_M(new Point(66, 99));
		CONTROLLER.mouseController.dragged_M(new Point(11, 22));
		CONTROLLER.mouseController.released_M(false);
		
		
		CONTROLLER.mouseController.pressed_M(new Point(11, 22));
		CONTROLLER.mouseController.dragged_M(new Point(56, 85));
		CONTROLLER.mouseController.released_M(false);
		
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

				CONTROLLER.mouseController.pressed(new Point(42, 45));
				CONTROLLER.mouseController.dragged(new Point(43, 42));
				CONTROLLER.mouseController.released();
				
				CONTROLLER.mouseController.pressed(new Point(44, 44));
				CONTROLLER.mouseController.dragged(new Point(44, 37));
				CONTROLLER.mouseController.released();
				
				CONTROLLER.mouseController.pressed(new Point(30, 23));
				CONTROLLER.mouseController.dragged(new Point(63, 73));
				CONTROLLER.mouseController.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug3() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(40, 19));
		CONTROLLER.mouseController.dragged_M(new Point(43, 61));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(99, 95));
		CONTROLLER.mouseController.dragged_M(new Point(38, 31));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(40, 35));
		CONTROLLER.mouseController.dragged_M(new Point(82, 8));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug4() throws Exception {

		CONTROLLER.mouseController.pressed_M(new Point(351, 140));
		CONTROLLER.mouseController.dragged_M(new Point(454, 11));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(454, 11));
		CONTROLLER.mouseController.dragged_M(new Point(436, 146));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(389, 36));
		CONTROLLER.mouseController.dragged_M(new Point(462, 273));
		CONTROLLER.mouseController.released_M(false);
		
		CONTROLLER.mouseController.pressed_M(new Point(216, 161));
		CONTROLLER.mouseController.dragged_M(new Point(491, 36));
		CONTROLLER.mouseController.released_M(false);
		
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
				
				CONTROLLER.mouseController.pressed(new Point(463, 221));
				CONTROLLER.mouseController.dragged(new Point(25, 28));
				CONTROLLER.mouseController.released();
				
				CONTROLLER.mouseController.pressed(new Point(274, 24));
				CONTROLLER.mouseController.dragged(new Point(34, 38));
				CONTROLLER.mouseController.released();
				
				CONTROLLER.mouseController.pressed(new Point(34, 38));
				CONTROLLER.mouseController.dragged(new Point(428, 78));
				CONTROLLER.mouseController.released();
				
				CONTROLLER.mouseController.pressed(new Point(46, 40));
				CONTROLLER.mouseController.dragged(new Point(364, 52));
				CONTROLLER.mouseController.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug6() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(390, 91));
		CONTROLLER.mouseController.dragged_M(new Point(32, 324));
		CONTROLLER.mouseController.dragged_M(new Point(19, 246));
		CONTROLLER.mouseController.dragged_M(new Point(116, 147));
		CONTROLLER.mouseController.dragged_M(new Point(185, 261));
		CONTROLLER.mouseController.dragged_M(new Point(211, 398));
		CONTROLLER.mouseController.dragged_M(new Point(118, 34));
		CONTROLLER.mouseController.released_M(false);

		CONTROLLER.mouseController.pressed_M(new Point(100, 216));
		CONTROLLER.mouseController.dragged_M(new Point(262, 444));
		CONTROLLER.mouseController.released_M(false);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
}
