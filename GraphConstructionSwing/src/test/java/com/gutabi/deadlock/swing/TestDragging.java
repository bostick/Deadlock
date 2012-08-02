package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.Main.PLATFORMCONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;
import static org.junit.Assert.*;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.core.Edge;
import com.gutabi.core.Point;

public class TestDragging {
	
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
	
	@Test
	public void test1() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(7, 7));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.graph.getEdges();
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(4, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = MODEL.graph.getEdges();
			}
		});
		 
		assertEquals(edges.size(),  1);
		Edge e = edges.get(0);
		assertTrue(Point.equals(new Point(6, 6), e.getStart().getPoint()));
		assertTrue(Point.equals(new Point(4, 4), e.getEnd().getPoint()));
		
	}
	
	@Test
	public void test3() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(267, 581));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(267, 580));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(267, 582));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(2, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 1));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 3));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(532, 627));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(515, 620));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(500, 612));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(480, 598));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(443, 565));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(415, 534));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(397, 513));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(383, 493));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(377, 473));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 446));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 396));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 380));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(373, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(380, 343));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(390, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(403, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(420, 314));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(434, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(449, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(460, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(471, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(498, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(518, 309));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(540, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(556, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(571, 344));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(582, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(592, 367));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(597, 374));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(604, 382));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(607, 386));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(610, 389));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(614, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(616, 412));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 453));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 472));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(617, 486));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(613, 506));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(606, 533));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(599, 556));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(590, 571));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(580, 583));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(565, 599));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(542, 615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(521, 635));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(7, 7));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(4, 4));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(5, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(7, 7));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(5, 5));
		PLATFORMCONTROLLER.mc.pressed_M(new Point(6, 6));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(4, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(267, 581));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(267, 580));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(267, 582));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(2, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 2));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 1));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 3));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(532, 627));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(515, 620));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(500, 612));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(480, 598));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(443, 565));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(415, 534));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(397, 513));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(383, 493));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(377, 473));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 446));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 396));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 380));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(373, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(380, 343));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(390, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(403, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(420, 314));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(434, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(449, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(460, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(471, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(498, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(518, 309));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(540, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(556, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(571, 344));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(582, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(592, 367));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(597, 374));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(604, 382));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(607, 386));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(610, 389));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(614, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(616, 412));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 453));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 472));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(617, 486));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(613, 506));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(606, 533));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(599, 556));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(590, 571));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(580, 583));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(565, 599));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(542, 615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(521, 635));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(841, 101));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(838, 102));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(830, 104));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(823, 105));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(814, 109));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(804, 115));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(798, 118));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(792, 125));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(787, 135));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(779, 145));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(772, 150));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(762, 158));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(752, 168));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(742, 178));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(730, 201));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(715, 241));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(711, 255));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(705, 276));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(705, 287));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(703, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(703, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(703, 328));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(703, 335));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(707, 345));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(711, 348));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(718, 352));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(729, 356));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(739, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(748, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(755, 366));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(761, 368));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(767, 370));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(770, 371));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(773, 372));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(774, 372));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(775, 372));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(775, 335));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(774, 333));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(771, 333));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(767, 332));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(760, 330));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(754, 329));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(748, 327));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(743, 324));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(737, 323));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(732, 319));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(728, 318));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(723, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(717, 315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(708, 313));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(699, 311));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(690, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(684, 308));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(673, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(670, 303));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(666, 302));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(661, 302));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(659, 301));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(656, 299));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(653, 298));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(650, 296));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(649, 295));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(647, 294));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(645, 293));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(643, 293));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(643, 292));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(642, 292));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(643, 292));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(532, 627));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(515, 620));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(500, 612));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(480, 598));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(443, 565));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(415, 534));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(397, 513));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(383, 493));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(377, 473));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 446));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 396));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(372, 380));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(373, 363));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(380, 343));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(390, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(403, 320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(420, 314));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(434, 310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(449, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(460, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(471, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(498, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(518, 309));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(540, 316));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(556, 331));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(571, 344));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(582, 359));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(592, 367));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(597, 374));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(604, 382));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(607, 386));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(610, 389));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(614, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(616, 412));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 453));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 472));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(617, 486));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(613, 506));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(606, 533));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(599, 556));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(590, 571));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(580, 583));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(565, 599));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(542, 615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(521, 635));
		PLATFORMCONTROLLER.mc.released_M();
		
		// break the loop
		PLATFORMCONTROLLER.mc.pressed_M(new Point(619, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(580, 423));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(0, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(150, 450));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(200, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(0, 50));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(2, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 1));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(3, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(2, 0));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(2, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(12, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(2, 5));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(3, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(3, 6));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(4, 0));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(10, 12));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(0, 12));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(7, 7));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(16, 1));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(532, 419));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(583, 442));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(583, 447));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(584, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(584, 449));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(584, 450));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(585, 450));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(555, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(556, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(579, 444));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(580, 444));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(580, 444));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(581, 444));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(592, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(593, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(594, 448));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(594, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(597, 451));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(598, 454));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(603, 455));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(604, 456));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(607, 458));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(608, 458));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(608, 458));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(608, 459));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(607, 459));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(592, 463));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(587, 463));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(583, 463));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(639, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(637, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(631, 420));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(631, 420));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(631, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(630, 422));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(629, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(623, 424));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(623, 424));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(621, 421));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(619, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(618, 419));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(618, 419));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(626, 419));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(627, 418));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(629, 417));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(629, 417));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(632, 417));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(640, 415));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(49, 100));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(44, 25));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(0, 13));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(54, 42));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(54, 42));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(44, 25));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(84, 86));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(33, 97));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(52, 8));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(30, 65));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(19, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(77, 95));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(30, 64));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(51, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(34, 74));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(29, 90));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(56, 85));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(58, 29));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(31, 45));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(44, 5));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(50, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(40, 39));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(50, 29));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(73, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(43, 96));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(46, 49));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(36, 4));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(52, 66));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(73, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(97, 69));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(5, 66));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(44, 89));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(82, 80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95, 89));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(31, 77));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(45, 98));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(5, 19));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(84, 86));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(33, 97));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(73, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(43, 96));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(43, 96));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(46, 49));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(60, 78));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(1, 72));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(47, 60));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(7, 17));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(2, 78));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(45, 47));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(7, 41));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(7, 41));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(45, 81));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(13, 56));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(4, 17));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(25, 62));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(3, 34));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(71, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(6, 91));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(76, 24));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(91, 98));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(3, 84));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(13, 47));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(53, 43));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(17, 38));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(36, 59));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100, 27));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(0, 59));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(37, 36));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(19, 4));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(26, 80));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(23, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(18, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 9));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 9));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(44, 94));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(23, 4));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(18, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(18, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 10));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 9));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 9));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(44, 94));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(18, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(18, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 10));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(21, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(21, 9));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(703, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(703, 335));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(754, 329));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(699, 311));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(703, 306));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(703, 320));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(717, 315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(684, 308));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(583, 442));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(583, 447));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(584, 449));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(584, 450));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(583, 442));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(84, 86));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(33, 97));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(52, 8));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(30, 65));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(19, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(77, 95));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(30, 64));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(29, 90));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(56, 85));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(30, 65));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(19, 22));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(11, 22));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(56, 85));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(73, 26));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(43, 96));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(46, 49));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(45, 98));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(5, 19));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(52, 8));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(30, 65));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(51, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(34, 74));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(66, 99));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(11, 22));
		PLATFORMCONTROLLER.mc.released_M();
		
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(11, 22));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(56, 85));
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

				PLATFORMCONTROLLER.mc.pressed(new Point(42, 45));
				PLATFORMCONTROLLER.mc.dragged(new Point(43, 42));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new Point(44, 44));
				PLATFORMCONTROLLER.mc.dragged(new Point(44, 37));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new Point(30, 23));
				PLATFORMCONTROLLER.mc.dragged(new Point(63, 73));
				PLATFORMCONTROLLER.mc.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug3() throws Exception {

		PLATFORMCONTROLLER.mc.pressed_M(new Point(40, 19));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(43, 61));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(99, 95));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(38, 31));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(40, 35));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(82, 8));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(351, 140));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(454, 11));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(454, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(436, 146));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(389, 36));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(462, 273));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(216, 161));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(491, 36));
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
				
				PLATFORMCONTROLLER.mc.pressed(new Point(463, 221));
				PLATFORMCONTROLLER.mc.dragged(new Point(25, 28));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new Point(274, 24));
				PLATFORMCONTROLLER.mc.dragged(new Point(34, 38));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new Point(34, 38));
				PLATFORMCONTROLLER.mc.dragged(new Point(428, 78));
				PLATFORMCONTROLLER.mc.released();
				
				PLATFORMCONTROLLER.mc.pressed(new Point(46, 40));
				PLATFORMCONTROLLER.mc.dragged(new Point(364, 52));
				PLATFORMCONTROLLER.mc.released();
				
				String.class.getClass();
			}
		});
		
	}
	
	@Test
	public void testBug6() throws Exception {
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(390, 91));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(32, 324));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(19, 246));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(116, 147));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(185, 261));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(211, 398));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(118, 34));
		PLATFORMCONTROLLER.mc.released_M();

		PLATFORMCONTROLLER.mc.pressed_M(new Point(100, 216));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(262, 444));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(135,455));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(135,455));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(130,445));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(130,440));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(130,440));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(130,435));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,430));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,405));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,400));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,390));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,385));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,380));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,375));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,370));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,360));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(130,350));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(135,330));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(135,320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(140,305));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(140,295));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(150,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(155,270));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(160,260));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(165,250));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(170,245));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(180,240));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(185,235));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(195,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(195,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(200,220));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(205,220));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(215,215));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(215,215));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(220,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(220,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(230,205));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(110,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(170,650));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(180,650));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(200,645));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(230,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,635));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,640));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,640));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(260,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(270,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,635));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(290,630));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(305,630));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(310,625));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,625));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,620));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(335,615));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(230,555));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,545));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,530));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,515));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,500));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,485));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(255,445));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(255,445));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(275,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(290,415));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(295,410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(300,410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(300,410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(305,410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(305,410));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,405));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,390));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(325,380));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(325,370));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,350));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(335,315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(335,310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(335,305));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(340,285));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(340,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(350,260));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(360,250));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(360,240));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(365,235));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(365,235));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,195));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,180));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,180));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,165));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,150));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(260,130));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,125));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,125));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,110));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,110));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,110));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(285,105));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(305,90));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(305,90));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(310,85));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,85));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,85));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(190,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(190,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(205,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(210,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(215,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(220,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(275,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(285,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(290,75));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(295,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(300,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(310,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(310,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,85));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,85));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,85));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(385,220));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(385,220));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(385,220));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(380,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(375,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(370,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(365,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(360,225));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(355,230));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(345,230));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,235));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,240));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,240));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(310,245));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(300,245));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(295,250));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,255));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(270,260));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,265));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,265));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(260,265));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(255,270));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,270));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,275));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,275));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,275));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,275));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,280));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,285));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(140,460));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(140,460));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(150,450));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(150,440));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(160,435));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(170,420));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(180,405));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(185,395));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(190,390));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(190,385));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(195,380));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(200,375));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(200,370));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(200,365));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(205,365));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(205,360));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(210,355));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(215,350));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(220,345));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(225,345));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(230,340));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,330));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(235,325));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(240,320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,305));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,305));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(195,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(195,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(195,80));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(180,95));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(170,100));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(155,115));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(145,125));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(135,135));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,145));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(120,155));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(115,160));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(110,165));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(110,170));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(105,185));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100,190));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100,195));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,200));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,205));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,210));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,220));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(85,240));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(80,245));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(80,250));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,265));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,275));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,285));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(70,305));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(70,315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(70,325));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(65,350));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(65,360));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(65,370));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,390));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,400));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,410));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,415));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,425));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(55,435));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(55,445));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(55,455));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(55,465));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,475));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,485));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,495));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,505));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(65,515));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(70,520));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(70,530));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,535));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,535));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(80,540));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(80,540));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(85,545));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(85,545));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(85,550));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,550));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,550));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(100,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100,655));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100,645));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100,640));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,630));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,620));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,615));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,610));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,595));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,590));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,585));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(95,580));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,580));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,575));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,575));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,575));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,570));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,570));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(245,565));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,565));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(250,570));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,575));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,575));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(275,575));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(280,580));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(290,585));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(300,590));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(305,590));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(310,590));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,595));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(315,595));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(325,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(325,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(325,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,600));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(330,605));
		PLATFORMCONTROLLER.mc.released_M();
		PLATFORMCONTROLLER.mc.pressed_M(new Point(390,510));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(385,500));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(380,495));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(375,495));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(355,475));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(335,450));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(320,445));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(300,425));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(275,415));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(265,405));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(260,400));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(245,390));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(230,385));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(210,370));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(195,365));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(180,360));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(170,355));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(160,345));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(140,340));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(125,325));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(115,320));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(105,315));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(100,310));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(90,305));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,300));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(75,300));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(65,295));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(65,295));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,290));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,290));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(60,290));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(661, 568));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(664, 568));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(664, 567));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(665, 567));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(665, 567));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(661, 570));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(56, 10));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(57, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(58, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(54, 11));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(54, 12));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(54, 12));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(58, 11));
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

		PLATFORMCONTROLLER.mc.pressed_M(new Point(775, 457));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(772, 467));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(772, 469));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(773, 465));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(773, 465));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(775, 457));
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
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(775, 457));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(772, 467));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(772, 469));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(775, 457));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(778, 449));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(798, 448));
		PLATFORMCONTROLLER.mc.released_M();
		
		PLATFORMCONTROLLER.mc.pressed_M(new Point(798, 448));
		PLATFORMCONTROLLER.mc.dragged_M(new Point(760, 460));
		PLATFORMCONTROLLER.mc.released_M();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				PLATFORMCONTROLLER.mc.hashCode();
			}
		});
		
	}
	
}
