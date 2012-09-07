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


public class TestDragging2 {
	
	static Point OFFSET = new Point(0, 0);
	
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
		CONTROLLER.mc.pressed(p.add(OFFSET));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testDragged(Point p) throws Exception {
		CONTROLLER.mc.dragged(p.add(OFFSET));
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
	public void testBug1() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(49., 3.));
		testDragged(new Point(793., 406));
		testReleased();

		testPressed(new Point(795., 353));
		testDragged(new Point(760., 385));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug2() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(400., 400.));
		testDragged(new Point(500., 400.));
		testReleased();

		testPressed(new Point(450., 350.));
		testDragged(new Point(450, 450.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public void testBug3() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(400., 400.));
		testDragged(new Point(500., 400.));
		testReleased();
		
		testPressed(new Point(450., 300.));
		testDragged(new Point(450, 395.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug4() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(382., 668.));
		testDragged(new Point(384., 668.));
		testDragged(new Point(385., 668.));
		testDragged(new Point(387., 668.));
		testDragged(new Point(388., 668.));
		testDragged(new Point(390., 667.));
		testDragged(new Point(391., 667.));
		testDragged(new Point(392., 666.));
		testDragged(new Point(393., 666.));
		testDragged(new Point(394., 665.));
		testDragged(new Point(395., 664.));
		testDragged(new Point(397., 663.));
		testDragged(new Point(400., 659.));
		testDragged(new Point(403., 655.));
		testDragged(new Point(403., 654.));
		testDragged(new Point(404., 653.));
		testDragged(new Point(407., 649.));
		testDragged(new Point(409., 648.));
		testDragged(new Point(411., 647.));
		testDragged(new Point(412., 646.));
		testDragged(new Point(414., 645.));
		testDragged(new Point(417., 643.));
		testDragged(new Point(421., 642.));
		testDragged(new Point(425., 641.));
		testDragged(new Point(428., 639.));
		testDragged(new Point(431., 638.));
		testDragged(new Point(436., 635.));
		testDragged(new Point(438., 632.));
		testDragged(new Point(439., 631.));
		testDragged(new Point(440., 630.));
		testDragged(new Point(441., 629.));
		testDragged(new Point(444., 626.));
		testDragged(new Point(445., 625.));
		testDragged(new Point(446., 623.));
		testDragged(new Point(446., 622.));
		testDragged(new Point(447., 621.));
		testDragged(new Point(448., 619.));
		testDragged(new Point(448., 618.));
		testDragged(new Point(449., 617.));
		testDragged(new Point(451., 616.));
		testDragged(new Point(452., 615.));
		testDragged(new Point(453., 614.));
		testDragged(new Point(454., 614.));
		testDragged(new Point(455., 612.));
		testDragged(new Point(456., 611.));
		testDragged(new Point(459., 611.));
		testDragged(new Point(460., 610.));
		testDragged(new Point(461., 610.));
		testDragged(new Point(462., 609.));
		testDragged(new Point(466., 608.));
		testDragged(new Point(467., 607.));
		testDragged(new Point(468., 605.));
		testDragged(new Point(469., 605.));
		testDragged(new Point(472., 604.));
		testDragged(new Point(472., 603.));
		testDragged(new Point(474., 602.));
		testDragged(new Point(475., 601.));
		testDragged(new Point(476., 600.));
		testDragged(new Point(478., 600.));
		testDragged(new Point(478., 598.));
		testDragged(new Point(479., 598.));
		testDragged(new Point(479., 597.));
		testDragged(new Point(479., 596.));
		testDragged(new Point(480., 596.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug5() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
//		"testPressed(new Point(473., 702.));
//		testDragged(new Point(475., 702.));
//		testDragged(new Point(475., 701.));
//		testDragged(new Point(476., 701.));
//		testDragged(new Point(477., 699.));
//		testDragged(new Point(478., 697.));
//		testDragged(new Point(479., 695.));
//		testDragged(new Point(481., 690.));
//		testDragged(new Point(482., 688.));
//		testDragged(new Point(484., 685.));
//		testDragged(new Point(486., 681.));
//		testDragged(new Point(487., 680.));
//		testDragged(new Point(491., 677.));
//		testDragged(new Point(492., 676.));
//		testDragged(new Point(496., 675.));
//		testDragged(new Point(498., 674.));
//		testDragged(new Point(504., 671.));
//		testDragged(new Point(508., 667.));
//		testDragged(new Point(511., 664.));
//		testDragged(new Point(514., 659.));
//		testDragged(new Point(516., 655.));
//		testDragged(new Point(517., 652.));
//		testDragged(new Point(521., 644.));
//		testDragged(new Point(523., 640.));
//		testDragged(new Point(526., 637.));
//		testDragged(new Point(527., 636.));
//		testDragged(new Point(533., 630.));
//		testDragged(new Point(535., 629.));
//		testDragged(new Point(538., 626.));
//		testDragged(new Point(540., 623.));
//		testDragged(new Point(542., 622.));
//		testDragged(new Point(545., 619.));
//		testDragged(new Point(550., 614.));
//		testDragged(new Point(555., 611.));
//		testDragged(new Point(567., 600.));
//		testDragged(new Point(568., 598.));
//		testDragged(new Point(571., 596.));
//		testDragged(new Point(572., 593.));
//		testDragged(new Point(574., 592.));
//		testDragged(new Point(576., 591.));
//		testDragged(new Point(577., 590.));
//		testDragged(new Point(579., 587.));
//		testDragged(new Point(581., 586.));
//		testDragged(new Point(583., 585.));
//		testDragged(new Point(584., 583.));
//		testDragged(new Point(586., 580.));
//		testDragged(new Point(589., 576.));
//		testDragged(new Point(590., 573.));
//		testDragged(new Point(593., 570.));
//		testDragged(new Point(594., 566.));
//		testDragged(new Point(597., 558.));
//		testDragged(new Point(600., 556.));
//		testDragged(new Point(601., 554.));
//		testDragged(new Point(604., 552.));
//		testDragged(new Point(607., 549.));
//		testDragged(new Point(608., 548.));
//		testDragged(new Point(611., 546.));
//		testDragged(new Point(614., 543.));
//		testDragged(new Point(615., 542.));
//		testDragged(new Point(616., 542.));
//		testDragged(new Point(617., 540.));
//		testDragged(new Point(618., 540.));
//		testDragged(new Point(620., 539.));
//		testDragged(new Point(621., 537.));
//		testDragged(new Point(622., 535.));
//		testDragged(new Point(623., 535.));
//		testReleased();
//
//		testPressed(new Point(627., 657.));
//		testDragged(new Point(629., 656.));
//		testDragged(new Point(632., 653.));
//		testDragged(new Point(635., 650.));
//		testDragged(new Point(638., 648.));
//		testDragged(new Point(643., 645.));
//		testDragged(new Point(645., 643.));
//		testDragged(new Point(648., 641.));
//		testDragged(new Point(651., 638.));
//		testDragged(new Point(654., 634.));
//		testDragged(new Point(658., 625.));
//		testDragged(new Point(661., 616.));
//		testDragged(new Point(665., 609.));
//		testDragged(new Point(668., 602.));
//		testDragged(new Point(670., 599.));
//		testDragged(new Point(674., 593.));
//		testDragged(new Point(677., 591.));
//		testDragged(new Point(678., 589.));
//		testDragged(new Point(682., 586.));
//		testDragged(new Point(684., 583.));
//		testDragged(new Point(689., 578.));
//		testDragged(new Point(695., 569.));
//		testDragged(new Point(701., 562.));
//		testDragged(new Point(706., 552.));
//		testDragged(new Point(709., 548.));
//		testDragged(new Point(717., 538.));
//		testDragged(new Point(720., 534.));
//		testDragged(new Point(723., 531.));
//		testDragged(new Point(728., 523.));
//		testDragged(new Point(732., 516.));
//		testDragged(new Point(733., 515.));
//		testDragged(new Point(733., 512.));
//		testDragged(new Point(734., 511.));
//		testDragged(new Point(734., 510.));
//		testDragged(new Point(734., 508.));
//		testDragged(new Point(736., 506.));
//		testDragged(new Point(737., 502.));
//		testDragged(new Point(738., 501.));
//		testDragged(new Point(739., 499.));
//		testDragged(new Point(740., 497.));
//		testDragged(new Point(742., 495.));
//		testDragged(new Point(743., 494.));
//		testDragged(new Point(747., 490.));
//		testDragged(new Point(748., 489.));
//		testDragged(new Point(750., 487.));
//		testDragged(new Point(752., 486.));
//		testDragged(new Point(753., 484.));
//		testDragged(new Point(756., 481.));
//		testDragged(new Point(759., 476.));
//		testDragged(new Point(762., 472.));
//		testDragged(new Point(763., 465.));
//		testDragged(new Point(766., 462.));
//		testDragged(new Point(772., 455.));
//		testDragged(new Point(774., 452.));
//		testDragged(new Point(775., 449.));
//		testDragged(new Point(777., 447.));
//		testDragged(new Point(778., 444.));
//		testDragged(new Point(779., 441.));
//		testDragged(new Point(781., 438.));
//		testDragged(new Point(782., 435.));
//		testDragged(new Point(782., 433.));
//		testDragged(new Point(784., 428.));
//		testDragged(new Point(785., 424.));
//		testDragged(new Point(785., 421.));
//		testDragged(new Point(787., 418.));
//		testDragged(new Point(788., 416.));
//		testReleased();

		testPressed(new Point(748., 690.));
		testDragged(new Point(748., 689.));
		testDragged(new Point(748., 686.));
		testDragged(new Point(748., 685.));
		testDragged(new Point(749., 682.));
		testDragged(new Point(749., 681.));
		testDragged(new Point(750., 680.));
		testDragged(new Point(751., 679.));
		testDragged(new Point(753., 678.));
		testDragged(new Point(753., 676.));
		testDragged(new Point(754., 675.));
		testDragged(new Point(755., 674.));
		testDragged(new Point(759., 670.));
		testDragged(new Point(760., 669.));
		testDragged(new Point(761., 668.));
		testDragged(new Point(762., 667.));
		testDragged(new Point(764., 665.));
		testDragged(new Point(765., 665.));
		testDragged(new Point(766., 664.));
		testDragged(new Point(769., 662.));
		testDragged(new Point(772., 657.));
		testDragged(new Point(776., 654.));
		testDragged(new Point(784., 645.));
		testDragged(new Point(787., 642.));
		testDragged(new Point(788., 639.));
		testDragged(new Point(791., 636.));
		testDragged(new Point(794., 632.));
		testDragged(new Point(796., 629.));
		testDragged(new Point(798., 628.));
		testDragged(new Point(800., 625.));
		testDragged(new Point(803., 623.));
		testDragged(new Point(806., 616.));
		testDragged(new Point(807., 611.));
		testDragged(new Point(810., 607.));
		testDragged(new Point(812., 602.));
		testDragged(new Point(818., 595.));
		testDragged(new Point(822., 592.));
		testDragged(new Point(825., 589.));
		testDragged(new Point(832., 587.));
		testDragged(new Point(834., 584.));
		testDragged(new Point(842., 581.));
		testDragged(new Point(845., 578.));
		testDragged(new Point(847., 577.));
		testDragged(new Point(850., 574.));
		testDragged(new Point(854., 569.));
		testDragged(new Point(857., 564.));
		testDragged(new Point(859., 560.));
		testDragged(new Point(862., 555.));
		testDragged(new Point(865., 552.));
		testDragged(new Point(868., 549.));
		testDragged(new Point(869., 547.));
		testDragged(new Point(872., 544.));
		testDragged(new Point(873., 543.));
		testDragged(new Point(876., 541.));
		testDragged(new Point(879., 540.));
		testDragged(new Point(880., 537.));
		testDragged(new Point(884., 536.));
		testDragged(new Point(887., 534.));
		testDragged(new Point(890., 533.));
		testDragged(new Point(893., 530.));
		testDragged(new Point(896., 528.));
		testDragged(new Point(899., 524.));
		testDragged(new Point(902., 519.));
		testDragged(new Point(905., 515.));
		testDragged(new Point(908., 510.));
		testDragged(new Point(909., 507.));
		testDragged(new Point(912., 503.));
		testDragged(new Point(914., 500.));
		testDragged(new Point(915., 499.));
		testDragged(new Point(916., 496.));
		testDragged(new Point(918., 493.));
		testDragged(new Point(921., 490.));
		testDragged(new Point(922., 488.));
		testDragged(new Point(924., 483.));
		testDragged(new Point(926., 480.));
		testDragged(new Point(930., 474.));
		testDragged(new Point(931., 469.));
		testDragged(new Point(934., 463.));
		testDragged(new Point(938., 458.));
		testDragged(new Point(940., 455.));
		testDragged(new Point(945., 449.));
		testDragged(new Point(946., 448.));
		testDragged(new Point(950., 446.));
		testDragged(new Point(951., 445.));
		testDragged(new Point(952., 443.));
		testReleased();
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
}
