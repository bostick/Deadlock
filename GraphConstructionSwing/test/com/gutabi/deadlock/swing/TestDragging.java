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
		//List<Point> pts = e.getPoints();
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
		
//		CONTROLLER.mouseController.pressed_M(new Point(631., 681.));
//		CONTROLLER.mouseController.dragged_M(new Point(629., 681.));
//		CONTROLLER.mouseController.dragged_M(new Point(620., 679.));
//		CONTROLLER.mouseController.dragged_M(new Point(606., 673.));
//		CONTROLLER.mouseController.dragged_M(new Point(588., 662.));
//		CONTROLLER.mouseController.dragged_M(new Point(554., 643.));
//		CONTROLLER.mouseController.dragged_M(new Point(495., 616.));
//		CONTROLLER.mouseController.dragged_M(new Point(449., 576.));
//		CONTROLLER.mouseController.dragged_M(new Point(408., 529.));
//		CONTROLLER.mouseController.dragged_M(new Point(387., 500.));
//		CONTROLLER.mouseController.dragged_M(new Point(373., 474.));
//		CONTROLLER.mouseController.dragged_M(new Point(361., 424.));
//		CONTROLLER.mouseController.dragged_M(new Point(358., 383.));
//		CONTROLLER.mouseController.dragged_M(new Point(358., 344.));
//		CONTROLLER.mouseController.dragged_M(new Point(367., 280.));
//		CONTROLLER.mouseController.dragged_M(new Point(379., 235.));
//		CONTROLLER.mouseController.dragged_M(new Point(390., 212.));
//		CONTROLLER.mouseController.dragged_M(new Point(406., 197.));
//		CONTROLLER.mouseController.dragged_M(new Point(435., 185.));
//		CONTROLLER.mouseController.dragged_M(new Point(474., 185.));
//		CONTROLLER.mouseController.dragged_M(new Point(508., 185.));
//		CONTROLLER.mouseController.dragged_M(new Point(541., 191.));
//		CONTROLLER.mouseController.dragged_M(new Point(581., 206.));
//		CONTROLLER.mouseController.dragged_M(new Point(669., 278.));
//		CONTROLLER.mouseController.dragged_M(new Point(697., 318.));
//		CONTROLLER.mouseController.dragged_M(new Point(718., 360.));
//		CONTROLLER.mouseController.dragged_M(new Point(728., 419.));
//		CONTROLLER.mouseController.dragged_M(new Point(728., 459.));
//		CONTROLLER.mouseController.dragged_M(new Point(714., 499.));
//		CONTROLLER.mouseController.dragged_M(new Point(668., 556.));
//		CONTROLLER.mouseController.dragged_M(new Point(601., 601.));
//		CONTROLLER.mouseController.pressed_M(new Point(543., 628.));
//		CONTROLLER.mouseController.dragged_M(new Point(461., 643.));
//		CONTROLLER.mouseController.dragged_M(new Point(393., 647.));
//		CONTROLLER.mouseController.dragged_M(new Point(354., 647.));
//		CONTROLLER.mouseController.dragged_M(new Point(327., 643.));
//		CONTROLLER.mouseController.pressed_M(new Point(307., 634.));
//		CONTROLLER.mouseController.dragged_M(new Point(291., 619.));
//		CONTROLLER.mouseController.dragged_M(new Point(278., 601.));
//		CONTROLLER.mouseController.dragged_M(new Point(270., 589.));
//		CONTROLLER.mouseController.pressed_M(new Point(268., 584.));
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
		//CONTROLLER.mouseController.dragged_M(new Point(6.0, 6.0));
		CONTROLLER.mouseController.dragged_M(new Point(7, 7));
		CONTROLLER.mouseController.released_M();
		//CONTROLLER.mouseController.pressed_M(new Point(5.0, 5.0));
		CONTROLLER.mouseController.pressed_M(new Point(6, 6));
		CONTROLLER.mouseController.dragged_M(new Point(4, 4));
		CONTROLLER.mouseController.released_M();
//		CONTROLLER.mouseController.pressed_M(new Point(267.0, 581.0));
//		CONTROLLER.mouseController.dragged_M(new Point(267.0, 580.0));
//		CONTROLLER.mouseController.dragged_M(new Point(267.0, 582.0));
//		CONTROLLER.mouseController.released_M();
//		CONTROLLER.mouseController.pressed_M(new Point(2.0, 2.0));
//		CONTROLLER.mouseController.dragged_M(new Point(1.0, 2.0));
//		CONTROLLER.mouseController.dragged_M(new Point(1.0, 1.0));
//		CONTROLLER.mouseController.dragged_M(new Point(1.0, 3.0));
//		CONTROLLER.mouseController.released_M();
//		CONTROLLER.mouseController.pressed_M(new Point(532.0, 627.0));
//		CONTROLLER.mouseController.dragged_M(new Point(515.0, 620.0));
//		CONTROLLER.mouseController.dragged_M(new Point(500.0, 612.0));
//		CONTROLLER.mouseController.dragged_M(new Point(480.0, 598.0));
//		CONTROLLER.mouseController.dragged_M(new Point(443.0, 565.0));
//		CONTROLLER.mouseController.dragged_M(new Point(415.0, 534.0));
//		CONTROLLER.mouseController.dragged_M(new Point(397.0, 513.0));
//		CONTROLLER.mouseController.dragged_M(new Point(383.0, 493.0));
//		CONTROLLER.mouseController.dragged_M(new Point(377.0, 473.0));
//		CONTROLLER.mouseController.dragged_M(new Point(372.0, 446.0));
//		CONTROLLER.mouseController.dragged_M(new Point(372.0, 424.0));
//		CONTROLLER.mouseController.dragged_M(new Point(372.0, 410.0));
//		CONTROLLER.mouseController.dragged_M(new Point(372.0, 396.0));
//		CONTROLLER.mouseController.dragged_M(new Point(372.0, 380.0));
//		CONTROLLER.mouseController.dragged_M(new Point(373.0, 363.0));
//		CONTROLLER.mouseController.dragged_M(new Point(380.0, 343.0));
//		CONTROLLER.mouseController.dragged_M(new Point(390.0, 331.0));
//		CONTROLLER.mouseController.dragged_M(new Point(403.0, 320.0));
//		CONTROLLER.mouseController.dragged_M(new Point(420.0, 314.0));
//		CONTROLLER.mouseController.dragged_M(new Point(434.0, 310.0));
//		CONTROLLER.mouseController.dragged_M(new Point(449.0, 306.0));
//		CONTROLLER.mouseController.dragged_M(new Point(460.0, 306.0));
//		CONTROLLER.mouseController.dragged_M(new Point(471.0, 306.0));
//		CONTROLLER.mouseController.dragged_M(new Point(498.0, 306.0));
//		CONTROLLER.mouseController.dragged_M(new Point(518.0, 309.0));
//		CONTROLLER.mouseController.dragged_M(new Point(540.0, 316.0));
//		CONTROLLER.mouseController.dragged_M(new Point(556.0, 331.0));
//		CONTROLLER.mouseController.dragged_M(new Point(571.0, 344.0));
//		CONTROLLER.mouseController.dragged_M(new Point(582.0, 359.0));
//		CONTROLLER.mouseController.dragged_M(new Point(592.0, 367.0));
//		CONTROLLER.mouseController.dragged_M(new Point(597.0, 374.0));
//		CONTROLLER.mouseController.dragged_M(new Point(604.0, 382.0));
//		CONTROLLER.mouseController.dragged_M(new Point(607.0, 386.0));
//		CONTROLLER.mouseController.dragged_M(new Point(610.0, 389.0));
//		CONTROLLER.mouseController.dragged_M(new Point(614.0, 398.0));
//		CONTROLLER.mouseController.dragged_M(new Point(616.0, 412.0));
//		CONTROLLER.mouseController.dragged_M(new Point(618.0, 453.0));
//		CONTROLLER.mouseController.dragged_M(new Point(618.0, 472.0));
//		CONTROLLER.mouseController.dragged_M(new Point(617.0, 486.0));
//		CONTROLLER.mouseController.dragged_M(new Point(613.0, 506.0));
//		CONTROLLER.mouseController.dragged_M(new Point(606.0, 533.0));
//		CONTROLLER.mouseController.dragged_M(new Point(599.0, 556.0));
//		CONTROLLER.mouseController.dragged_M(new Point(590.0, 571.0));
//		CONTROLLER.mouseController.dragged_M(new Point(580.0, 583.0));
//		CONTROLLER.mouseController.dragged_M(new Point(565.0, 599.0));
//		CONTROLLER.mouseController.dragged_M(new Point(542.0, 615.0));
//		CONTROLLER.mouseController.dragged_M(new Point(521.0, 635.0));
//		CONTROLLER.mouseController.released_M();
//		CONTROLLER.mouseController.pressed_M(new Point(841.0, 101.0));
//		CONTROLLER.mouseController.dragged_M(new Point(838.0, 102.0));
//		CONTROLLER.mouseController.dragged_M(new Point(830.0, 104.0));
//		CONTROLLER.mouseController.dragged_M(new Point(823.0, 105.0));
//		CONTROLLER.mouseController.dragged_M(new Point(814.0, 109.0));
//		CONTROLLER.mouseController.dragged_M(new Point(804.0, 115.0));
//		CONTROLLER.mouseController.dragged_M(new Point(798.0, 118.0));
//		CONTROLLER.mouseController.dragged_M(new Point(792.0, 125.0));
//		CONTROLLER.mouseController.dragged_M(new Point(787.0, 135.0));
//		CONTROLLER.mouseController.dragged_M(new Point(779.0, 145.0));
//		CONTROLLER.mouseController.dragged_M(new Point(772.0, 150.0));
//		CONTROLLER.mouseController.dragged_M(new Point(762.0, 158.0));
//		CONTROLLER.mouseController.dragged_M(new Point(752.0, 168.0));
//		CONTROLLER.mouseController.dragged_M(new Point(742.0, 178.0));
//		CONTROLLER.mouseController.dragged_M(new Point(730.0, 201.0));
//		CONTROLLER.mouseController.dragged_M(new Point(715.0, 241.0));
//		CONTROLLER.mouseController.dragged_M(new Point(711.0, 255.0));
//		CONTROLLER.mouseController.dragged_M(new Point(705.0, 276.0));
//		CONTROLLER.mouseController.dragged_M(new Point(705.0, 287.0));
//		CONTROLLER.mouseController.dragged_M(new Point(703.0, 306.0));
//		CONTROLLER.mouseController.dragged_M(new Point(703.0, 320.0));
//		CONTROLLER.mouseController.dragged_M(new Point(703.0, 328.0));
//		CONTROLLER.mouseController.dragged_M(new Point(703.0, 335.0));
//		CONTROLLER.mouseController.dragged_M(new Point(707.0, 345.0));
//		CONTROLLER.mouseController.dragged_M(new Point(711.0, 348.0));
//		CONTROLLER.mouseController.dragged_M(new Point(718.0, 352.0));
//		CONTROLLER.mouseController.dragged_M(new Point(729.0, 356.0));
//		CONTROLLER.mouseController.dragged_M(new Point(739.0, 359.0));
//		CONTROLLER.mouseController.dragged_M(new Point(748.0, 363.0));
//		CONTROLLER.mouseController.dragged_M(new Point(755.0, 366.0));
//		CONTROLLER.mouseController.dragged_M(new Point(761.0, 368.0));
//		CONTROLLER.mouseController.dragged_M(new Point(767.0, 370.0));
//		CONTROLLER.mouseController.dragged_M(new Point(770.0, 371.0));
//		CONTROLLER.mouseController.dragged_M(new Point(773.0, 372.0));
//		CONTROLLER.mouseController.dragged_M(new Point(774.0, 372.0));
//		CONTROLLER.mouseController.dragged_M(new Point(775.0, 372.0));
//		CONTROLLER.mouseController.released_M();
//		CONTROLLER.mouseController.pressed_M(new Point(775.0, 335.0));
//		CONTROLLER.mouseController.dragged_M(new Point(774.0, 333.0));
//		CONTROLLER.mouseController.dragged_M(new Point(771.0, 333.0));
//		CONTROLLER.mouseController.dragged_M(new Point(767.0, 332.0));
//		CONTROLLER.mouseController.dragged_M(new Point(760.0, 330.0));
//		CONTROLLER.mouseController.dragged_M(new Point(754.0, 329.0));
//		CONTROLLER.mouseController.dragged_M(new Point(748.0, 327.0));
//		CONTROLLER.mouseController.dragged_M(new Point(743.0, 324.0));
//		CONTROLLER.mouseController.dragged_M(new Point(737.0, 323.0));
//		CONTROLLER.mouseController.dragged_M(new Point(732.0, 319.0));
//		CONTROLLER.mouseController.dragged_M(new Point(728.0, 318.0));
//		CONTROLLER.mouseController.dragged_M(new Point(723.0, 316.0));
//		CONTROLLER.mouseController.dragged_M(new Point(717.0, 315.0));
//		CONTROLLER.mouseController.dragged_M(new Point(708.0, 313.0));
//		CONTROLLER.mouseController.dragged_M(new Point(699.0, 311.0));
//		CONTROLLER.mouseController.dragged_M(new Point(690.0, 310.0));
//		CONTROLLER.mouseController.dragged_M(new Point(684.0, 308.0));
//		CONTROLLER.mouseController.dragged_M(new Point(673.0, 306.0));
//		CONTROLLER.mouseController.dragged_M(new Point(670.0, 303.0));
//		CONTROLLER.mouseController.dragged_M(new Point(666.0, 302.0));
//		CONTROLLER.mouseController.dragged_M(new Point(661.0, 302.0));
//		CONTROLLER.mouseController.dragged_M(new Point(659.0, 301.0));
//		CONTROLLER.mouseController.dragged_M(new Point(656.0, 299.0));
//		CONTROLLER.mouseController.dragged_M(new Point(653.0, 298.0));
//		CONTROLLER.mouseController.dragged_M(new Point(650.0, 296.0));
//		CONTROLLER.mouseController.dragged_M(new Point(649.0, 295.0));
//		CONTROLLER.mouseController.dragged_M(new Point(647.0, 294.0));
//		CONTROLLER.mouseController.dragged_M(new Point(645.0, 293.0));
//		CONTROLLER.mouseController.dragged_M(new Point(643.0, 293.0));
//		CONTROLLER.mouseController.dragged_M(new Point(643.0, 292.0));
//		CONTROLLER.mouseController.dragged_M(new Point(642, 292));
//		CONTROLLER.mouseController.dragged_M(new Point(643, 292));
		
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
		//CONTROLLER.mouseController.dragged_M(new Point(3, 7));
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
	public void testXEqualsZBug() throws Exception {
		
		CONTROLLER.mouseController.pressed_M(new Point(532, 419));
		CONTROLLER.mouseController.dragged_M(new Point(529, 419));
		CONTROLLER.mouseController.dragged_M(new Point(525, 419));
		CONTROLLER.mouseController.dragged_M(new Point(520, 421));
		CONTROLLER.mouseController.dragged_M(new Point(517, 422));
		CONTROLLER.mouseController.dragged_M(new Point(515, 422));
		CONTROLLER.mouseController.dragged_M(new Point(512, 422));
		CONTROLLER.mouseController.dragged_M(new Point(511, 422));
		CONTROLLER.mouseController.dragged_M(new Point(508, 421));
		CONTROLLER.mouseController.dragged_M(new Point(507, 420));
		CONTROLLER.mouseController.dragged_M(new Point(505, 417));
		CONTROLLER.mouseController.dragged_M(new Point(503, 414));
		CONTROLLER.mouseController.dragged_M(new Point(502, 411));
		CONTROLLER.mouseController.dragged_M(new Point(501, 410));
		CONTROLLER.mouseController.dragged_M(new Point(501, 409));
		CONTROLLER.mouseController.dragged_M(new Point(501, 408));
		CONTROLLER.mouseController.dragged_M(new Point(499, 405));
		CONTROLLER.mouseController.dragged_M(new Point(498, 402));
		CONTROLLER.mouseController.dragged_M(new Point(498, 400));
		CONTROLLER.mouseController.dragged_M(new Point(498, 395));
		CONTROLLER.mouseController.dragged_M(new Point(498, 391));
		CONTROLLER.mouseController.dragged_M(new Point(498, 385));
		CONTROLLER.mouseController.dragged_M(new Point(498, 381));
		CONTROLLER.mouseController.dragged_M(new Point(498, 376));
		CONTROLLER.mouseController.dragged_M(new Point(498, 374));
		CONTROLLER.mouseController.dragged_M(new Point(498, 372));
		CONTROLLER.mouseController.dragged_M(new Point(499, 371));
		CONTROLLER.mouseController.dragged_M(new Point(500, 371));
		CONTROLLER.mouseController.dragged_M(new Point(504, 370));
		CONTROLLER.mouseController.dragged_M(new Point(507, 370));
		CONTROLLER.mouseController.dragged_M(new Point(513, 370));
		CONTROLLER.mouseController.dragged_M(new Point(519, 370));
		CONTROLLER.mouseController.dragged_M(new Point(528, 371));
		CONTROLLER.mouseController.dragged_M(new Point(537, 376));
		CONTROLLER.mouseController.dragged_M(new Point(547, 386));
		CONTROLLER.mouseController.dragged_M(new Point(555, 396));
		CONTROLLER.mouseController.dragged_M(new Point(561, 411));
		CONTROLLER.mouseController.dragged_M(new Point(567, 420));
		CONTROLLER.mouseController.dragged_M(new Point(573, 428));
		CONTROLLER.mouseController.dragged_M(new Point(576, 432));
		CONTROLLER.mouseController.dragged_M(new Point(579, 435));
		CONTROLLER.mouseController.dragged_M(new Point(582, 440));
		CONTROLLER.mouseController.dragged_M(new Point(583, 442));
		CONTROLLER.mouseController.dragged_M(new Point(583, 447));
		CONTROLLER.mouseController.dragged_M(new Point(584, 448));
		CONTROLLER.mouseController.dragged_M(new Point(584, 449));
		CONTROLLER.mouseController.dragged_M(new Point(584, 450));
		CONTROLLER.mouseController.dragged_M(new Point(585, 450));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(555, 448));
		CONTROLLER.mouseController.dragged_M(new Point(556, 448));
		CONTROLLER.mouseController.dragged_M(new Point(561, 447));
		CONTROLLER.mouseController.dragged_M(new Point(565, 445));
		CONTROLLER.mouseController.dragged_M(new Point(568, 444));
		CONTROLLER.mouseController.dragged_M(new Point(572, 444));
		CONTROLLER.mouseController.dragged_M(new Point(575, 444));
		CONTROLLER.mouseController.dragged_M(new Point(578, 444));
		CONTROLLER.mouseController.dragged_M(new Point(579, 444));
		CONTROLLER.mouseController.dragged_M(new Point(580, 444));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(580, 444));
		CONTROLLER.mouseController.dragged_M(new Point(581, 444));
		CONTROLLER.mouseController.dragged_M(new Point(584, 444));
		CONTROLLER.mouseController.dragged_M(new Point(588, 446));
		CONTROLLER.mouseController.dragged_M(new Point(591, 447));
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
		CONTROLLER.mouseController.dragged_M(new Point(603, 460));
		CONTROLLER.mouseController.dragged_M(new Point(596, 462));
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
		
		CONTROLLER.mouseController.pressed_M(new Point(611, 452));
		CONTROLLER.mouseController.dragged_M(new Point(610, 452));
		CONTROLLER.mouseController.dragged_M(new Point(605, 452));
		CONTROLLER.mouseController.dragged_M(new Point(599, 450));
		CONTROLLER.mouseController.dragged_M(new Point(595, 449));
		CONTROLLER.mouseController.dragged_M(new Point(592, 446));
		CONTROLLER.mouseController.dragged_M(new Point(591, 445));
		CONTROLLER.mouseController.dragged_M(new Point(591, 444));
		CONTROLLER.mouseController.dragged_M(new Point(591, 440));
		CONTROLLER.mouseController.dragged_M(new Point(591, 432));
		CONTROLLER.mouseController.dragged_M(new Point(593, 425));
		CONTROLLER.mouseController.dragged_M(new Point(596, 420));
		CONTROLLER.mouseController.dragged_M(new Point(601, 416));
		CONTROLLER.mouseController.dragged_M(new Point(607, 412));
		CONTROLLER.mouseController.dragged_M(new Point(612, 409));
		CONTROLLER.mouseController.dragged_M(new Point(618, 408));
		CONTROLLER.mouseController.dragged_M(new Point(621, 408));
		CONTROLLER.mouseController.dragged_M(new Point(624, 406));
		CONTROLLER.mouseController.dragged_M(new Point(626, 406));
		CONTROLLER.mouseController.dragged_M(new Point(628, 406));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(628, 406));
		CONTROLLER.mouseController.dragged_M(new Point(628, 408));
		CONTROLLER.mouseController.dragged_M(new Point(628, 410));
		CONTROLLER.mouseController.dragged_M(new Point(627, 411));
		CONTROLLER.mouseController.dragged_M(new Point(625, 414));
		CONTROLLER.mouseController.dragged_M(new Point(624, 414));
		CONTROLLER.mouseController.dragged_M(new Point(621, 414));
		CONTROLLER.mouseController.dragged_M(new Point(620, 414));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(620, 414));
		CONTROLLER.mouseController.dragged_M(new Point(623, 414));
		CONTROLLER.mouseController.dragged_M(new Point(627, 414));
		CONTROLLER.mouseController.dragged_M(new Point(628, 414));
		CONTROLLER.mouseController.dragged_M(new Point(631, 415));
		CONTROLLER.mouseController.dragged_M(new Point(633, 415));
		CONTROLLER.mouseController.dragged_M(new Point(634, 416));
		CONTROLLER.mouseController.dragged_M(new Point(636, 416));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(636, 416));
		CONTROLLER.mouseController.dragged_M(new Point(637, 417));
		CONTROLLER.mouseController.dragged_M(new Point(638, 417));
		CONTROLLER.mouseController.dragged_M(new Point(639, 419));
		CONTROLLER.mouseController.dragged_M(new Point(639, 420));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(639, 420));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(639, 420));
		CONTROLLER.mouseController.dragged_M(new Point(637, 420));
		CONTROLLER.mouseController.dragged_M(new Point(636, 420));
		CONTROLLER.mouseController.dragged_M(new Point(635, 420));
		CONTROLLER.mouseController.dragged_M(new Point(634, 420));
		CONTROLLER.mouseController.dragged_M(new Point(633, 420));
		CONTROLLER.mouseController.dragged_M(new Point(632, 420));
		CONTROLLER.mouseController.dragged_M(new Point(630, 420));
		CONTROLLER.mouseController.dragged_M(new Point(631, 420));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(631, 420));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(631, 420));
		CONTROLLER.mouseController.dragged_M(new Point(630, 422));
		CONTROLLER.mouseController.dragged_M(new Point(629, 424));
		CONTROLLER.mouseController.dragged_M(new Point(627, 424));
		CONTROLLER.mouseController.dragged_M(new Point(625, 425));
		CONTROLLER.mouseController.dragged_M(new Point(624, 425));
		CONTROLLER.mouseController.dragged_M(new Point(623, 424));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(623, 424));
		CONTROLLER.mouseController.dragged_M(new Point(622, 423));
		CONTROLLER.mouseController.dragged_M(new Point(621, 421));
		CONTROLLER.mouseController.dragged_M(new Point(619, 420));
		CONTROLLER.mouseController.dragged_M(new Point(618, 420));
		CONTROLLER.mouseController.dragged_M(new Point(618, 419));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(618, 419));
		CONTROLLER.mouseController.dragged_M(new Point(620, 419));
		CONTROLLER.mouseController.dragged_M(new Point(621, 419));
		CONTROLLER.mouseController.dragged_M(new Point(622, 419));
		CONTROLLER.mouseController.dragged_M(new Point(623, 419));
		CONTROLLER.mouseController.dragged_M(new Point(626, 419));
		CONTROLLER.mouseController.dragged_M(new Point(627, 418));
		CONTROLLER.mouseController.dragged_M(new Point(629, 417));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(629, 417));
		CONTROLLER.mouseController.dragged_M(new Point(632, 417));
		CONTROLLER.mouseController.dragged_M(new Point(633, 417));
		CONTROLLER.mouseController.dragged_M(new Point(636, 417));
		CONTROLLER.mouseController.dragged_M(new Point(638, 417));
		CONTROLLER.mouseController.dragged_M(new Point(640, 415));
		CONTROLLER.mouseController.released_M(true);

		CONTROLLER.mouseController.pressed_M(new Point(640, 415));
		CONTROLLER.mouseController.dragged_M(new Point(640, 414));
		CONTROLLER.mouseController.dragged_M(new Point(640, 412));
		CONTROLLER.mouseController.dragged_M(new Point(640, 411));
		CONTROLLER.mouseController.dragged_M(new Point(640, 409));
		CONTROLLER.mouseController.dragged_M(new Point(640, 408));
		CONTROLLER.mouseController.dragged_M(new Point(640, 407));
		CONTROLLER.mouseController.dragged_M(new Point(640, 406));
		CONTROLLER.mouseController.dragged_M(new Point(640, 405));
		CONTROLLER.mouseController.released_M(true);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				;
			}
		});
		
	}
}
