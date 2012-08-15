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
	
	/**
	 * test with integer length edge, helps expose edge cases
	 */
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
	
	@Test
	public void test2() throws Exception {
		
		long seed = 209929963529206L;
		
		MODEL.RANDOM.setSeed(seed);
		
		testPressed(new Point(5., 414.));
		testDragged(new Point(11., 414.));
		testDragged(new Point(21., 414.));
		testDragged(new Point(35., 414.));
		testDragged(new Point(54., 414.));
		testDragged(new Point(91., 414.));
		testDragged(new Point(138., 419.));
		testDragged(new Point(191., 425.));
		testDragged(new Point(261., 435.));
		testDragged(new Point(314., 442.));
		testDragged(new Point(350., 447.));
		testDragged(new Point(380., 447.));
		testDragged(new Point(407., 449.));
		testDragged(new Point(437., 449.));
		testDragged(new Point(465., 454.));
		testDragged(new Point(489., 456.));
		testDragged(new Point(509., 458.));
		testDragged(new Point(525., 458.));
		testDragged(new Point(534., 460.));
		testReleased();

		testPressed(new Point(534., 460.));
		testDragged(new Point(534., 457.));
		testDragged(new Point(533., 451.));
		testDragged(new Point(533., 443.));
		testDragged(new Point(533., 431.));
		testDragged(new Point(533., 423.));
		testDragged(new Point(533., 414.));
		testDragged(new Point(533., 410.));
		testDragged(new Point(534., 406.));
		testDragged(new Point(537., 403.));
		testDragged(new Point(538., 398.));
		testDragged(new Point(543., 393.));
		testDragged(new Point(550., 390.));
		testDragged(new Point(559., 388.));
		testDragged(new Point(572., 388.));
		testDragged(new Point(589., 388.));
		testDragged(new Point(603., 388.));
		testDragged(new Point(611., 388.));
		testDragged(new Point(620., 391.));
		testDragged(new Point(627., 394.));
		testDragged(new Point(639., 400.));
		testDragged(new Point(649., 408.));
		testDragged(new Point(664., 415.));
		testDragged(new Point(679., 423.));
		testDragged(new Point(693., 432.));
		testDragged(new Point(703., 439.));
		testDragged(new Point(712., 454.));
		testDragged(new Point(718., 469.));
		testDragged(new Point(722., 486.));
		testDragged(new Point(722., 497.));
		testDragged(new Point(722., 505.));
		testDragged(new Point(722., 514.));
		testDragged(new Point(719., 523.));
		testDragged(new Point(714., 533.));
		testDragged(new Point(705., 548.));
		testDragged(new Point(693., 560.));
		testDragged(new Point(680., 573.));
		testDragged(new Point(670., 581.));
		testDragged(new Point(660., 587.));
		testDragged(new Point(657., 590.));
		testDragged(new Point(655., 591.));
		testDragged(new Point(652., 591.));
		testDragged(new Point(646., 591.));
		testDragged(new Point(643., 592.));
		testDragged(new Point(642., 592.));
		testDragged(new Point(639., 592.));
		testDragged(new Point(635., 592.));
		testDragged(new Point(631., 592.));
		testDragged(new Point(626., 590.));
		testDragged(new Point(623., 589.));
		testDragged(new Point(622., 588.));
		testDragged(new Point(616., 582.));
		testDragged(new Point(612., 579.));
		testDragged(new Point(605., 572.));
		testDragged(new Point(600., 567.));
		testDragged(new Point(595., 562.));
		testDragged(new Point(590., 555.));
		testDragged(new Point(588., 552.));
		testDragged(new Point(584., 549.));
		testDragged(new Point(582., 547.));
		testDragged(new Point(581., 544.));
		testDragged(new Point(581., 543.));
		testDragged(new Point(578., 537.));
		testDragged(new Point(574., 528.));
		testDragged(new Point(573., 521.));
		testDragged(new Point(573., 517.));
		testDragged(new Point(571., 514.));
		testDragged(new Point(570., 511.));
		testDragged(new Point(570., 510.));
		testDragged(new Point(569., 509.));
		testDragged(new Point(568., 506.));
		testDragged(new Point(566., 505.));
		testDragged(new Point(565., 503.));
		testDragged(new Point(565., 502.));
		testDragged(new Point(564., 500.));
		testDragged(new Point(563., 500.));
		testDragged(new Point(563., 499.));
		testDragged(new Point(562., 497.));
		testDragged(new Point(560., 494.));
		testDragged(new Point(559., 491.));
		testDragged(new Point(558., 491.));
		testDragged(new Point(558., 490.));
		testDragged(new Point(556., 489.));
		testDragged(new Point(555., 489.));
		testDragged(new Point(551., 485.));
		testDragged(new Point(548., 482.));
		testDragged(new Point(547., 481.));
		testDragged(new Point(544., 479.));
		testDragged(new Point(543., 478.));
		testDragged(new Point(542., 478.));
		testDragged(new Point(541., 477.));
		testDragged(new Point(540., 476.));
		testDragged(new Point(539., 475.));
		testDragged(new Point(538., 473.));
		testDragged(new Point(538., 472.));
		testDragged(new Point(536., 471.));
		testDragged(new Point(535., 469.));
		testDragged(new Point(535., 467.));
		testDragged(new Point(535., 466.));
		testDragged(new Point(535., 465.));
		testReleased();

		testPressed(new Point(8., 621.));
		testDragged(new Point(10., 621.));
		testDragged(new Point(14., 621.));
		testDragged(new Point(22., 621.));
		testDragged(new Point(28., 621.));
		testDragged(new Point(35., 621.));
		testDragged(new Point(43., 621.));
		testDragged(new Point(59., 621.));
		testDragged(new Point(78., 621.));
		testDragged(new Point(100., 621.));
		testDragged(new Point(114., 621.));
		testDragged(new Point(130., 621.));
		testDragged(new Point(147., 621.));
		testDragged(new Point(166., 621.));
		testDragged(new Point(185., 621.));
		testDragged(new Point(201., 621.));
		testDragged(new Point(212., 621.));
		testDragged(new Point(223., 621.));
		testDragged(new Point(230., 621.));
		testDragged(new Point(236., 621.));
		testDragged(new Point(242., 621.));
		testDragged(new Point(248., 623.));
		testDragged(new Point(254., 623.));
		testDragged(new Point(266., 623.));
		testDragged(new Point(275., 623.));
		testDragged(new Point(279., 623.));
		testDragged(new Point(282., 623.));
		testDragged(new Point(283., 623.));
		testDragged(new Point(284., 623.));
		testDragged(new Point(285., 623.));
		testDragged(new Point(286., 623.));
		testDragged(new Point(287., 623.));
		testDragged(new Point(289., 623.));
		testDragged(new Point(291., 623.));
		testDragged(new Point(294., 623.));
		testDragged(new Point(296., 623.));
		testDragged(new Point(298., 623.));
		testDragged(new Point(300., 623.));
		testDragged(new Point(301., 623.));
		testDragged(new Point(304., 623.));
		testDragged(new Point(306., 623.));
		testDragged(new Point(309., 623.));
		testDragged(new Point(310., 623.));
		testDragged(new Point(311., 623.));
		testDragged(new Point(312., 623.));
		testReleased();

		testPressed(new Point(312., 623.));
		testDragged(new Point(312., 621.));
		testDragged(new Point(312., 619.));
		testDragged(new Point(312., 618.));
		testDragged(new Point(312., 617.));
		testDragged(new Point(312., 614.));
		testDragged(new Point(312., 611.));
		testDragged(new Point(312., 607.));
		testDragged(new Point(312., 604.));
		testDragged(new Point(312., 602.));
		testDragged(new Point(312., 601.));
		testDragged(new Point(312., 599.));
		testDragged(new Point(312., 598.));
		testDragged(new Point(312., 597.));
		testDragged(new Point(312., 596.));
		testDragged(new Point(312., 595.));
		testDragged(new Point(312., 594.));
		testDragged(new Point(314., 594.));
		testDragged(new Point(315., 594.));
		testDragged(new Point(316., 594.));
		testDragged(new Point(317., 594.));
		testDragged(new Point(318., 594.));
		testDragged(new Point(319., 594.));
		testDragged(new Point(321., 594.));
		testDragged(new Point(323., 594.));
		testDragged(new Point(324., 594.));
		testDragged(new Point(325., 594.));
		testDragged(new Point(328., 594.));
		testDragged(new Point(329., 594.));
		testDragged(new Point(331., 594.));
		testDragged(new Point(333., 594.));
		testDragged(new Point(334., 595.));
		testDragged(new Point(335., 595.));
		testDragged(new Point(336., 596.));
		testDragged(new Point(336., 597.));
		testDragged(new Point(337., 597.));
		testDragged(new Point(337., 598.));
		testDragged(new Point(337., 600.));
		testDragged(new Point(338., 601.));
		testDragged(new Point(338., 602.));
		testDragged(new Point(338., 603.));
		testDragged(new Point(338., 606.));
		testDragged(new Point(340., 608.));
		testDragged(new Point(340., 610.));
		testDragged(new Point(340., 612.));
		testDragged(new Point(340., 613.));
		testDragged(new Point(340., 614.));
		testDragged(new Point(340., 616.));
		testDragged(new Point(341., 617.));
		testDragged(new Point(341., 618.));
		testDragged(new Point(341., 619.));
		testDragged(new Point(341., 620.));
		testDragged(new Point(341., 621.));
		testDragged(new Point(341., 623.));
		testDragged(new Point(341., 624.));
		testDragged(new Point(341., 625.));
		testDragged(new Point(341., 626.));
		testDragged(new Point(341., 627.));
		testDragged(new Point(341., 628.));
		testDragged(new Point(341., 630.));
		testDragged(new Point(341., 631.));
		testDragged(new Point(341., 632.));
		testDragged(new Point(341., 633.));
		testDragged(new Point(341., 634.));
		testDragged(new Point(340., 635.));
		testDragged(new Point(339., 637.));
		testDragged(new Point(339., 638.));
		testDragged(new Point(337., 640.));
		testDragged(new Point(336., 640.));
		testDragged(new Point(335., 641.));
		testDragged(new Point(334., 641.));
		testDragged(new Point(333., 643.));
		testDragged(new Point(330., 643.));
		testDragged(new Point(330., 644.));
		testDragged(new Point(328., 644.));
		testDragged(new Point(326., 645.));
		testDragged(new Point(325., 645.));
		testDragged(new Point(324., 645.));
		testDragged(new Point(323., 645.));
		testDragged(new Point(322., 645.));
		testDragged(new Point(321., 645.));
		testDragged(new Point(319., 645.));
		testDragged(new Point(318., 645.));
		testDragged(new Point(317., 645.));
		testDragged(new Point(316., 643.));
		testDragged(new Point(316., 642.));
		testDragged(new Point(315., 641.));
		testDragged(new Point(315., 640.));
		testDragged(new Point(315., 637.));
		testDragged(new Point(314., 636.));
		testDragged(new Point(314., 634.));
		testDragged(new Point(314., 633.));
		testDragged(new Point(314., 631.));
		testDragged(new Point(314., 630.));
		testDragged(new Point(314., 629.));
		testDragged(new Point(314., 628.));
		testDragged(new Point(314., 627.));
		testReleased();

		testPressed(new Point(60., 622.));
		testDragged(new Point(60., 625.));
		testDragged(new Point(60., 631.));
		testDragged(new Point(60., 640.));
		testDragged(new Point(60., 651.));
		testDragged(new Point(60., 659.));
		testDragged(new Point(60., 664.));
		testDragged(new Point(60., 670.));
		testDragged(new Point(58., 676.));
		testDragged(new Point(56., 685.));
		testDragged(new Point(56., 699.));
		testDragged(new Point(56., 707.));
		testDragged(new Point(56., 711.));
		testDragged(new Point(55., 716.));
		testDragged(new Point(55., 719.));
		testDragged(new Point(55., 720.));
		testDragged(new Point(53., 721.));
		testDragged(new Point(53., 723.));
		testDragged(new Point(52., 731.));
		testDragged(new Point(52., 735.));
		testDragged(new Point(52., 739.));
		testDragged(new Point(52., 744.));
		testDragged(new Point(52., 752.));
		testDragged(new Point(52., 757.));
		testDragged(new Point(52., 759.));
		testDragged(new Point(52., 762.));
		testDragged(new Point(52., 765.));
		testDragged(new Point(52., 767.));
		testDragged(new Point(52., 770.));
		testDragged(new Point(52., 771.));
		testDragged(new Point(52., 774.));
		testDragged(new Point(52., 775.));
		testDragged(new Point(52., 777.));
		testDragged(new Point(52., 780.));
		testDragged(new Point(51., 781.));
		testDragged(new Point(51., 783.));
		testDragged(new Point(51., 786.));
		testDragged(new Point(51., 787.));
		testDragged(new Point(51., 790.));
		testDragged(new Point(50., 793.));
		testDragged(new Point(50., 794.));
		testDragged(new Point(48., 796.));
		testDragged(new Point(48., 797.));
		testDragged(new Point(48., 799.));
		testDragged(new Point(48., 800.));
		testDragged(new Point(48., 801.));
		testDragged(new Point(48., 802.));
		testDragged(new Point(47., 802.));
		testDragged(new Point(47., 803.));
		testDragged(new Point(47., 804.));
		testDragged(new Point(47., 806.));
		testDragged(new Point(46., 807.));
		testDragged(new Point(46., 808.));
		testDragged(new Point(46., 809.));
		testDragged(new Point(46., 810.));
		testDragged(new Point(46., 811.));
		testDragged(new Point(46., 813.));
		testDragged(new Point(45., 814.));
		testDragged(new Point(45., 815.));
		testDragged(new Point(45., 816.));
		testDragged(new Point(45., 817.));
		testReleased();

		testPressed(new Point(108., 626.));
		testDragged(new Point(108., 631.));
		testDragged(new Point(108., 635.));
		testDragged(new Point(108., 640.));
		testDragged(new Point(108., 642.));
		testDragged(new Point(108., 645.));
		testDragged(new Point(108., 648.));
		testDragged(new Point(108., 652.));
		testDragged(new Point(108., 656.));
		testDragged(new Point(108., 661.));
		testDragged(new Point(108., 665.));
		testDragged(new Point(108., 671.));
		testDragged(new Point(108., 677.));
		testDragged(new Point(108., 682.));
		testDragged(new Point(108., 686.));
		testDragged(new Point(108., 690.));
		testDragged(new Point(108., 699.));
		testDragged(new Point(108., 718.));
		testDragged(new Point(108., 727.));
		testDragged(new Point(108., 737.));
		testDragged(new Point(108., 740.));
		testDragged(new Point(108., 741.));
		testDragged(new Point(108., 747.));
		testDragged(new Point(108., 750.));
		testDragged(new Point(108., 753.));
		testDragged(new Point(108., 756.));
		testDragged(new Point(108., 761.));
		testDragged(new Point(108., 765.));
		testDragged(new Point(108., 769.));
		testDragged(new Point(108., 772.));
		testDragged(new Point(108., 775.));
		testDragged(new Point(108., 776.));
		testDragged(new Point(108., 777.));
		testDragged(new Point(108., 778.));
		testDragged(new Point(108., 779.));
		testDragged(new Point(108., 781.));
		testDragged(new Point(108., 782.));
		testDragged(new Point(108., 783.));
		testDragged(new Point(108., 784.));
		testDragged(new Point(108., 785.));
		testDragged(new Point(108., 786.));
		testDragged(new Point(108., 788.));
		testDragged(new Point(107., 789.));
		testDragged(new Point(107., 790.));
		testDragged(new Point(107., 791.));
		testDragged(new Point(107., 792.));
		testDragged(new Point(106., 793.));
		testDragged(new Point(106., 795.));
		testDragged(new Point(106., 796.));
		testDragged(new Point(106., 797.));
		testDragged(new Point(106., 798.));
		testDragged(new Point(106., 799.));
		testDragged(new Point(104., 799.));
		testDragged(new Point(104., 800.));
		testDragged(new Point(104., 802.));
		testDragged(new Point(104., 803.));
		testDragged(new Point(104., 804.));
		testDragged(new Point(103., 804.));
		testDragged(new Point(103., 805.));
		testDragged(new Point(103., 806.));
		testDragged(new Point(103., 807.));
		testDragged(new Point(102., 807.));
		testDragged(new Point(102., 809.));
		testDragged(new Point(102., 810.));
		testDragged(new Point(101., 810.));
		testDragged(new Point(101., 811.));
		testDragged(new Point(101., 812.));
		testDragged(new Point(101., 813.));
		testDragged(new Point(101., 814.));
		testDragged(new Point(101., 815.));
		testDragged(new Point(100., 815.));
		testDragged(new Point(100., 817.));
		testReleased();

		testPressed(new Point(157., 624.));
		testDragged(new Point(157., 627.));
		testDragged(new Point(157., 631.));
		testDragged(new Point(157., 634.));
		testDragged(new Point(157., 638.));
		testDragged(new Point(157., 641.));
		testDragged(new Point(157., 642.));
		testDragged(new Point(157., 646.));
		testDragged(new Point(157., 650.));
		testDragged(new Point(157., 656.));
		testDragged(new Point(157., 667.));
		testDragged(new Point(155., 684.));
		testDragged(new Point(155., 686.));
		testDragged(new Point(153., 691.));
		testDragged(new Point(153., 693.));
		testDragged(new Point(148., 717.));
		testDragged(new Point(146., 722.));
		testDragged(new Point(146., 726.));
		testDragged(new Point(146., 727.));
		testDragged(new Point(145., 731.));
		testDragged(new Point(144., 737.));
		testDragged(new Point(140., 744.));
		testDragged(new Point(139., 748.));
		testDragged(new Point(137., 757.));
		testDragged(new Point(136., 761.));
		testDragged(new Point(134., 766.));
		testDragged(new Point(134., 774.));
		testDragged(new Point(133., 775.));
		testDragged(new Point(133., 777.));
		testDragged(new Point(132., 780.));
		testDragged(new Point(132., 781.));
		testDragged(new Point(132., 782.));
		testDragged(new Point(132., 784.));
		testDragged(new Point(132., 785.));
		testDragged(new Point(131., 785.));
		testDragged(new Point(131., 786.));
		testDragged(new Point(131., 787.));
		testDragged(new Point(131., 790.));
		testDragged(new Point(131., 791.));
		testDragged(new Point(131., 792.));
		testDragged(new Point(131., 794.));
		testDragged(new Point(131., 797.));
		testDragged(new Point(131., 798.));
		testDragged(new Point(131., 799.));
		testDragged(new Point(131., 800.));
		testDragged(new Point(131., 802.));
		testDragged(new Point(132., 802.));
		testDragged(new Point(132., 803.));
		testDragged(new Point(133., 804.));
		testDragged(new Point(133., 805.));
		testDragged(new Point(134., 806.));
		testDragged(new Point(134., 807.));
		testDragged(new Point(135., 807.));
		testDragged(new Point(135., 809.));
		testDragged(new Point(137., 809.));
		testDragged(new Point(137., 810.));
		testDragged(new Point(138., 810.));
		testDragged(new Point(138., 811.));
		testDragged(new Point(139., 812.));
		testDragged(new Point(140., 813.));
		testDragged(new Point(140., 814.));
		testDragged(new Point(140., 816.));
		testDragged(new Point(140., 817.));
		testDragged(new Point(140., 818.));
		testDragged(new Point(141., 818.));
		testReleased();

		testPressed(new Point(197., 620.));
		testDragged(new Point(197., 623.));
		testDragged(new Point(197., 629.));
		testDragged(new Point(197., 637.));
		testDragged(new Point(197., 645.));
		testDragged(new Point(197., 651.));
		testDragged(new Point(197., 658.));
		testDragged(new Point(197., 662.));
		testDragged(new Point(197., 666.));
		testDragged(new Point(197., 675.));
		testDragged(new Point(197., 691.));
		testDragged(new Point(197., 698.));
		testDragged(new Point(197., 702.));
		testDragged(new Point(197., 706.));
		testDragged(new Point(197., 709.));
		testDragged(new Point(197., 712.));
		testDragged(new Point(197., 716.));
		testDragged(new Point(197., 724.));
		testDragged(new Point(197., 730.));
		testDragged(new Point(197., 735.));
		testDragged(new Point(197., 743.));
		testDragged(new Point(197., 746.));
		testDragged(new Point(198., 749.));
		testDragged(new Point(198., 751.));
		testDragged(new Point(198., 753.));
		testDragged(new Point(198., 755.));
		testDragged(new Point(198., 756.));
		testDragged(new Point(198., 760.));
		testDragged(new Point(198., 763.));
		testDragged(new Point(198., 764.));
		testDragged(new Point(198., 766.));
		testDragged(new Point(198., 768.));
		testDragged(new Point(198., 769.));
		testDragged(new Point(198., 773.));
		testDragged(new Point(198., 775.));
		testDragged(new Point(200., 780.));
		testDragged(new Point(200., 781.));
		testDragged(new Point(200., 782.));
		testDragged(new Point(200., 785.));
		testDragged(new Point(200., 787.));
		testDragged(new Point(200., 788.));
		testDragged(new Point(200., 790.));
		testDragged(new Point(200., 791.));
		testDragged(new Point(200., 792.));
		testDragged(new Point(200., 793.));
		testDragged(new Point(200., 794.));
		testDragged(new Point(200., 797.));
		testDragged(new Point(200., 798.));
		testDragged(new Point(200., 799.));
		testDragged(new Point(200., 800.));
		testDragged(new Point(200., 803.));
		testDragged(new Point(200., 805.));
		testDragged(new Point(200., 806.));
		testDragged(new Point(200., 808.));
		testDragged(new Point(200., 809.));
		testDragged(new Point(200., 810.));
		testDragged(new Point(200., 811.));
		testDragged(new Point(200., 812.));
		testDragged(new Point(200., 813.));
		testDragged(new Point(200., 815.));
		testReleased();

		testPressed(new Point(57., 705.));
		testDragged(new Point(59., 705.));
		testDragged(new Point(60., 705.));
		testDragged(new Point(61., 705.));
		testDragged(new Point(63., 705.));
		testDragged(new Point(65., 705.));
		testDragged(new Point(66., 705.));
		testDragged(new Point(67., 705.));
		testDragged(new Point(68., 705.));
		testDragged(new Point(69., 705.));
		testDragged(new Point(72., 705.));
		testDragged(new Point(74., 705.));
		testDragged(new Point(85., 703.));
		testDragged(new Point(94., 703.));
		testDragged(new Point(100., 703.));
		testDragged(new Point(104., 703.));
		testDragged(new Point(108., 703.));
		testDragged(new Point(117., 703.));
		testDragged(new Point(122., 703.));
		testDragged(new Point(128., 703.));
		testDragged(new Point(134., 703.));
		testDragged(new Point(140., 702.));
		testDragged(new Point(146., 702.));
		testDragged(new Point(152., 702.));
		testDragged(new Point(159., 702.));
		testDragged(new Point(162., 702.));
		testDragged(new Point(165., 702.));
		testDragged(new Point(166., 702.));
		testDragged(new Point(168., 702.));
		testDragged(new Point(169., 702.));
		testDragged(new Point(171., 702.));
		testDragged(new Point(172., 702.));
		testDragged(new Point(173., 702.));
		testDragged(new Point(174., 702.));
		testDragged(new Point(175., 702.));
		testDragged(new Point(176., 702.));
		testDragged(new Point(178., 702.));
		testDragged(new Point(179., 702.));
		testDragged(new Point(180., 702.));
		testDragged(new Point(181., 702.));
		testDragged(new Point(182., 702.));
		testDragged(new Point(183., 702.));
		testDragged(new Point(185., 702.));
		testDragged(new Point(186., 702.));
		testDragged(new Point(187., 702.));
		testDragged(new Point(188., 702.));
		testDragged(new Point(189., 702.));
		testDragged(new Point(190., 702.));
		testDragged(new Point(191., 702.));
		testDragged(new Point(193., 702.));
		testReleased();
		
		VIEW.controlPanel.simulationButton.doClick();
		
		String.class.getName();
		
	}
	
}
