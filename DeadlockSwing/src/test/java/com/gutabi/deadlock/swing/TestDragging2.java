package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.TestDragging.testDragged;
import static com.gutabi.deadlock.swing.TestDragging.testPressed;
import static com.gutabi.deadlock.swing.TestDragging.testReleased;
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
		
		Thread.sleep(2000);
		
		MODEL.clear();
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
		
		//Thread.sleep(Long.MAX_VALUE);
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
		
		//Thread.sleep(Long.MAX_VALUE);
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
		
		//Thread.sleep(Long.MAX_VALUE);
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
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug5() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

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
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug6() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(323., 334.));
		testDragged(new Point(323., 333.));
		testDragged(new Point(323., 332.));
		testDragged(new Point(324., 331.));
		testDragged(new Point(324., 328.));
		testDragged(new Point(326., 327.));
		testDragged(new Point(326., 326.));
		testDragged(new Point(327., 326.));
		testDragged(new Point(327., 325.));
		testDragged(new Point(327., 324.));
		testDragged(new Point(328., 324.));
		testDragged(new Point(328., 322.));
		testDragged(new Point(329., 322.));
		testDragged(new Point(329., 321.));
		testDragged(new Point(330., 321.));
		testDragged(new Point(331., 321.));
		testDragged(new Point(331., 320.));
		testDragged(new Point(333., 320.));
		testDragged(new Point(334., 320.));
		testDragged(new Point(335., 319.));
		testDragged(new Point(336., 319.));
		testDragged(new Point(337., 319.));
		testDragged(new Point(337., 318.));
		testDragged(new Point(338., 318.));
		testDragged(new Point(340., 317.));
		testDragged(new Point(341., 317.));
		testDragged(new Point(341., 315.));
		testDragged(new Point(342., 315.));
		testDragged(new Point(342., 314.));
		testDragged(new Point(343., 313.));
		testDragged(new Point(344., 312.));
		testDragged(new Point(344., 311.));
		testDragged(new Point(344., 310.));
		testDragged(new Point(345., 310.));
		testDragged(new Point(345., 308.));
		testDragged(new Point(345., 307.));
		testDragged(new Point(347., 307.));
		testDragged(new Point(347., 306.));
		testDragged(new Point(358., 298.));
		testDragged(new Point(395., 264.));
		testDragged(new Point(459., 199.));
		testDragged(new Point(544., 135.));
		testDragged(new Point(635., 57.));
		testDragged(new Point(713., 2.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug7() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(277., 531.));
		testDragged(new Point(846., 319.));
		testReleased();

		testPressed(new Point(549., 355.));
		testDragged(new Point(563., 381.));
		testDragged(new Point(598., 436.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public void testBug8() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(277., 531.));
		testDragged(new Point(377., 531.));
		testReleased();
		
		testPressed(new Point(477., 531.));
		testDragged(new Point(379., 531.));
		testDragged(new Point(378., 531.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public void testBug9() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(280., 553.));
		testDragged(new Point(282., 553.));
		testDragged(new Point(288., 550.));
		testDragged(new Point(309., 536.));
		testDragged(new Point(348., 508.));
		testDragged(new Point(403., 474.));
		testDragged(new Point(478., 440.));
		testDragged(new Point(548., 407.));
		testDragged(new Point(598., 370.));
		testDragged(new Point(632., 347.));
		testDragged(new Point(657., 336.));
		testDragged(new Point(681., 326.));
		testDragged(new Point(684., 324.));
		testDragged(new Point(685., 324.));
		testDragged(new Point(685., 323.));
		testReleased();

		testPressed(new Point(768., 314.));
		testDragged(new Point(767., 314.));
		testDragged(new Point(761., 316.));
		testDragged(new Point(758., 317.));
		testDragged(new Point(755., 318.));
		testDragged(new Point(754., 318.));
		testDragged(new Point(753., 318.));
		testDragged(new Point(752., 320.));
		testDragged(new Point(750., 320.));
		testDragged(new Point(749., 320.));
		testDragged(new Point(748., 320.));
		testDragged(new Point(746., 320.));
		testDragged(new Point(745., 320.));
		testDragged(new Point(743., 320.));
		testDragged(new Point(742., 321.));
		testDragged(new Point(741., 321.));
		testDragged(new Point(740., 321.));
		testDragged(new Point(739., 321.));
		testDragged(new Point(738., 321.));
		testDragged(new Point(736., 322.));
		testDragged(new Point(735., 322.));
		testDragged(new Point(733., 322.));
		testDragged(new Point(732., 322.));
		testDragged(new Point(729., 322.));
		testDragged(new Point(727., 322.));
		testDragged(new Point(723., 322.));
		testDragged(new Point(722., 322.));
		testDragged(new Point(719., 322.));
		testDragged(new Point(718., 322.));
		testDragged(new Point(715., 322.));
		testDragged(new Point(713., 322.));
		testDragged(new Point(712., 322.));
		testDragged(new Point(711., 322.));
		testDragged(new Point(710., 322.));
		testDragged(new Point(709., 322.));
		testDragged(new Point(708., 322.));
		testDragged(new Point(706., 322.));
		testDragged(new Point(705., 322.));
		testDragged(new Point(704., 322.));
		testDragged(new Point(703., 322.));
		testDragged(new Point(702., 322.));
		testDragged(new Point(701., 322.));
		testDragged(new Point(699., 322.));
		testDragged(new Point(698., 322.));
		testDragged(new Point(697., 322.));
		testDragged(new Point(696., 322.));
		testDragged(new Point(695., 322.));
		testDragged(new Point(694., 322.));
		testDragged(new Point(692., 322.));
		testDragged(new Point(691., 322.));
		testDragged(new Point(690., 322.));
		testDragged(new Point(689., 322.));
		testDragged(new Point(688., 322.));
		testDragged(new Point(687., 322.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug10() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(602., 596.));
		testDragged(new Point(640., 620.));
		testDragged(new Point(687., 638.));
		testReleased();
		
		testPressed(new Point(676., 574.));
		testDragged(new Point(640., 621.));
		testDragged(new Point(604., 652.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	
	@Test
	public void testBug11() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(642., 457.));
		testDragged(new Point(700., 432.));
		testReleased();

		testPressed(new Point(648., 431.));
		testDragged(new Point(657., 453.));
		testDragged(new Point(668., 471.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public void testBug12() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(828., 511.));
		testDragged(new Point(869., 546.));
		testDragged(new Point(906., 566.));
		testReleased();

		testPressed(new Point(853., 559.));
		testDragged(new Point(894., 528.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
	}
	
	
	
	@Test
	public void testBug13() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(643, 293));
		testDragged(new Point(643, 292));
		testDragged(new Point(642, 292));
		testDragged(new Point(643, 292));
		testReleased();
		
	}
	
	
	
	@Test
	public void testBug14() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(532, 627));
		testDragged(new Point(515, 620));
		testReleased();
		
		testPressed(new Point(565, 599));
		testDragged(new Point(542, 615));
		testDragged(new Point(521, 635));
		testReleased();
		
//		Thread.sleep(Long.MAX_VALUE);
		
	}
	
	
	
	@Test
	public void testBug15() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(43, 96));
		testDragged(new Point(46, 49));
		testDragged(new Point(36, 4));
		testDragged(new Point(52, 66));
		testReleased();
		
	}
	
	
	
	
	@Test
	public void testBug16() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(43, 96));
		testDragged(new Point(36, 4));
		testDragged(new Point(52, 66));		
		testDragged(new Point(97, 69));
		testDragged(new Point(5, 66));
		testReleased();
		
	}
	
	
	
	
	
	@Test
	public void testBug17() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(438., 429.));
		testDragged(new Point(428., 461.));
		testDragged(new Point(421., 494.));
		testReleased();
		
		testPressed(new Point(483., 463.));
		testDragged(new Point(436., 457.));
		testDragged(new Point(304., 446.));
		testReleased();
		
	}
	
	
	@Test
	public void testBug18() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(760., 624.));
		testDragged(new Point(758., 628.));
		testDragged(new Point(753., 635.));
		testDragged(new Point(746., 643.));
		testDragged(new Point(741., 650.));
		testDragged(new Point(737., 656.));
		testDragged(new Point(733., 668.));
		testDragged(new Point(729., 683.));
		testDragged(new Point(729., 694.));
		testDragged(new Point(729., 700.));
		testDragged(new Point(733., 705.));
		testDragged(new Point(742., 708.));
		testDragged(new Point(756., 710.));
		testDragged(new Point(782., 712.));
		testDragged(new Point(812., 709.));
		testDragged(new Point(841., 700.));
		testDragged(new Point(884., 672.));
		testDragged(new Point(896., 662.));
		testDragged(new Point(909., 650.));
		testDragged(new Point(912., 645.));
		testDragged(new Point(913., 638.));
		testDragged(new Point(913., 635.));
		testDragged(new Point(913., 630.));
		testDragged(new Point(908., 625.));
		testDragged(new Point(901., 622.));
		testDragged(new Point(894., 617.));
		testDragged(new Point(870., 607.));
		testDragged(new Point(861., 605.));
		testDragged(new Point(855., 603.));
		testDragged(new Point(844., 602.));
		testDragged(new Point(840., 602.));
		testDragged(new Point(833., 600.));
		testDragged(new Point(829., 600.));
		testDragged(new Point(822., 600.));
		testDragged(new Point(819., 600.));
		testDragged(new Point(814., 600.));
		testDragged(new Point(807., 601.));
		testDragged(new Point(801., 601.));
		testDragged(new Point(788., 602.));
		testDragged(new Point(782., 604.));
		testDragged(new Point(780., 605.));
		testDragged(new Point(779., 606.));
		testDragged(new Point(777., 606.));
		testDragged(new Point(777., 608.));
		testDragged(new Point(776., 609.));
		testDragged(new Point(775., 610.));
		
		/*
		 * after adding <<774, 612>, <763, 620>> there are now 3 edges
		 */
		testDragged(new Point(774., 612.));
		testDragged(new Point(763., 620.));
		testDragged(new Point(763., 622.));
		testReleased();
		
		// assert stand-alone loop
	}

	
	
	
	@Test
	public void testBug19() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(495., 556.));
		testDragged(new Point(489., 503.));
		testDragged(new Point(482., 335.));
		testDragged(new Point(488., 480.));
		testDragged(new Point(493., 516.));
		testReleased();
		
	}
	
	
	
	
	@Test
	public void testBug20() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(880., 316.));
		testDragged(new Point(891., 298.));
		testDragged(new Point(900., 284.));
		testDragged(new Point(906., 274.));
		testReleased();
		
		testPressed(new Point(918., 316.));
		testDragged(new Point(896., 292.));
		testDragged(new Point(863., 270.));
		testReleased();
		
	}
	
	@Test
	public void testBug21() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;

		testPressed(new Point(1187., 459.));
		testDragged(new Point(1210., 529.));
		testDragged(new Point(1218., 503.));
		testDragged(new Point(1225., 478.));
		testDragged(new Point(1241., 425.));
		testReleased();
	
		//Thread.sleep(Long.MAX_VALUE);
		
		// assert 1 edge
	}
	
	
	
	@Test
	public void testBug22() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(323., 617.));
		testDragged(new Point(327., 611.));
		testDragged(new Point(356., 582.));
		testDragged(new Point(399., 540.));
		testDragged(new Point(452., 490.));
		testDragged(new Point(537., 421.));
		testDragged(new Point(610., 368.));
		testDragged(new Point(665., 333.));
		testDragged(new Point(720., 299.));
		testDragged(new Point(805., 239.));
		testDragged(new Point(822., 226.));
		testDragged(new Point(839., 215.));
		testDragged(new Point(842., 214.));
		testDragged(new Point(843., 213.));
		testDragged(new Point(843., 211.));
		testReleased();

		testPressed(new Point(746., 178.));
		testDragged(new Point(746., 180.));
		testDragged(new Point(745., 184.));
		testDragged(new Point(742., 189.));
		testDragged(new Point(735., 194.));
		testDragged(new Point(728., 201.));
		testDragged(new Point(720., 211.));
		testDragged(new Point(708., 234.));
		testDragged(new Point(695., 257.));
		testDragged(new Point(679., 275.));
		testDragged(new Point(661., 295.));
		testDragged(new Point(626., 336.));
		testDragged(new Point(586., 383.));
		testDragged(new Point(530., 444.));
		testDragged(new Point(504., 492.));
		testDragged(new Point(478., 527.));
		testDragged(new Point(458., 548.));
		testDragged(new Point(409., 585.));
		testDragged(new Point(399., 597.));
		testDragged(new Point(387., 627.));
		testDragged(new Point(378., 645.));
		testDragged(new Point(377., 648.));
		testReleased();
		
		//Thread.sleep(Long.MAX_VALUE);
		
		// assert 6 vertices
	}
	
	
	@Test
	public void testBug23() throws Exception {
		
		CONTROLLER.strat = MassageStrategy.CURRENT;
		
		testPressed(new Point(330., 385.));
		testDragged(new Point(406., 373.));
		testDragged(new Point(425., 366.));
		testDragged(new Point(483., 354.));
		testReleased();
		
		// assert 1 edge
	}
	
	
}
