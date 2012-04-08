package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.swing.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.io.IOException;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.PointUtils;

public class Test1 {
	
	static Logger logger = Logger.getLogger("test");
	
	static void createAndShowGUI(String[] args) throws Exception {
		
		MODEL.init();
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		
		CONTROLLER.mouseController.pressed(new Point(5, 5));
		CONTROLLER.mouseController.dragged(new Point(6, 6));
		CONTROLLER.mouseController.dragged(new Point(7, 7));
		CONTROLLER.mouseController.released();
		
		List<Edge> edges = MODEL.getEdges();
		assert edges.size() == 1;
		Edge e = edges.get(0);
		List<Point> pts = e.getPoints();
		assert pts.size() == 2;
		assert PointUtils.equals(pts.get(0), new Point(5, 5));
		assert PointUtils.equals(pts.get(1), new Point(7, 7));
		
		MODEL.clear();
		
		CONTROLLER.mouseController.pressed(new Point(5, 5));
		CONTROLLER.mouseController.dragged(new Point(6, 6));
		CONTROLLER.mouseController.dragged(new Point(4, 4));
		CONTROLLER.mouseController.released();
		
		edges = MODEL.getEdges();
		assert edges.size() == 1;
		e = edges.get(0);
		assert PointUtils.equals(e.getStart().getPoint(), new Point(6, 6));
		assert PointUtils.equals(e.getEnd().getPoint(), new Point(4, 4));
		
		MODEL.clear();
		
		CONTROLLER.mouseController.pressed(new Point(267., 581.));
		CONTROLLER.mouseController.dragged(new Point(267., 580.));
		CONTROLLER.mouseController.dragged(new Point(267., 582.));
		CONTROLLER.mouseController.released();
		
		MODEL.clear();
		
//		CONTROLLER.mouseController.pressed(new Point(631., 681.));
//		CONTROLLER.mouseController.dragged(new Point(629., 681.));
//		CONTROLLER.mouseController.dragged(new Point(620., 679.));
//		CONTROLLER.mouseController.dragged(new Point(606., 673.));
//		CONTROLLER.mouseController.dragged(new Point(588., 662.));
//		CONTROLLER.mouseController.dragged(new Point(554., 643.));
//		CONTROLLER.mouseController.dragged(new Point(495., 616.));
//		CONTROLLER.mouseController.dragged(new Point(449., 576.));
//		CONTROLLER.mouseController.dragged(new Point(408., 529.));
//		CONTROLLER.mouseController.dragged(new Point(387., 500.));
//		CONTROLLER.mouseController.dragged(new Point(373., 474.));
//		CONTROLLER.mouseController.dragged(new Point(361., 424.));
//		CONTROLLER.mouseController.dragged(new Point(358., 383.));
//		CONTROLLER.mouseController.dragged(new Point(358., 344.));
//		CONTROLLER.mouseController.dragged(new Point(367., 280.));
//		CONTROLLER.mouseController.dragged(new Point(379., 235.));
//		CONTROLLER.mouseController.dragged(new Point(390., 212.));
//		CONTROLLER.mouseController.dragged(new Point(406., 197.));
//		CONTROLLER.mouseController.dragged(new Point(435., 185.));
//		CONTROLLER.mouseController.dragged(new Point(474., 185.));
//		CONTROLLER.mouseController.dragged(new Point(508., 185.));
//		CONTROLLER.mouseController.dragged(new Point(541., 191.));
//		CONTROLLER.mouseController.dragged(new Point(581., 206.));
//		CONTROLLER.mouseController.dragged(new Point(669., 278.));
//		CONTROLLER.mouseController.dragged(new Point(697., 318.));
//		CONTROLLER.mouseController.dragged(new Point(718., 360.));
//		CONTROLLER.mouseController.dragged(new Point(728., 419.));
//		CONTROLLER.mouseController.dragged(new Point(728., 459.));
//		CONTROLLER.mouseController.dragged(new Point(714., 499.));
//		CONTROLLER.mouseController.dragged(new Point(668., 556.));
//		CONTROLLER.mouseController.dragged(new Point(601., 601.));
//		CONTROLLER.mouseController.pressed(new Point(543., 628.));
//		CONTROLLER.mouseController.dragged(new Point(461., 643.));
//		CONTROLLER.mouseController.dragged(new Point(393., 647.));
//		CONTROLLER.mouseController.dragged(new Point(354., 647.));
//		CONTROLLER.mouseController.dragged(new Point(327., 643.));
//		CONTROLLER.mouseController.pressed(new Point(307., 634.));
//		CONTROLLER.mouseController.dragged(new Point(291., 619.));
//		CONTROLLER.mouseController.dragged(new Point(278., 601.));
//		CONTROLLER.mouseController.dragged(new Point(270., 589.));
//		CONTROLLER.mouseController.pressed(new Point(268., 584.));
		CONTROLLER.mouseController.pressed(new Point(2., 2.));
		CONTROLLER.mouseController.dragged(new Point(1., 2.));
		CONTROLLER.mouseController.dragged(new Point(1., 1.));
		CONTROLLER.mouseController.dragged(new Point(1., 3.));
		CONTROLLER.mouseController.released();
		
		MODEL.clear();
		
		CONTROLLER.mouseController.pressed(new Point(532., 627.));
		CONTROLLER.mouseController.dragged(new Point(515., 620.));
		CONTROLLER.mouseController.dragged(new Point(500., 612.));
		CONTROLLER.mouseController.dragged(new Point(480., 598.));
		CONTROLLER.mouseController.dragged(new Point(443., 565.));
		CONTROLLER.mouseController.dragged(new Point(415., 534.));
		CONTROLLER.mouseController.dragged(new Point(397., 513.));
		CONTROLLER.mouseController.dragged(new Point(383., 493.));
		CONTROLLER.mouseController.dragged(new Point(377., 473.));
		CONTROLLER.mouseController.dragged(new Point(372., 446.));
		CONTROLLER.mouseController.dragged(new Point(372., 424.));
		CONTROLLER.mouseController.dragged(new Point(372., 410.));
		CONTROLLER.mouseController.dragged(new Point(372., 396.));
		CONTROLLER.mouseController.dragged(new Point(372., 380.));
		CONTROLLER.mouseController.dragged(new Point(373., 363.));
		CONTROLLER.mouseController.dragged(new Point(380., 343.));
		CONTROLLER.mouseController.dragged(new Point(390., 331.));
		CONTROLLER.mouseController.dragged(new Point(403., 320.));
		CONTROLLER.mouseController.dragged(new Point(420., 314.));
		CONTROLLER.mouseController.dragged(new Point(434., 310.));
		CONTROLLER.mouseController.dragged(new Point(449., 306.));
		CONTROLLER.mouseController.dragged(new Point(460., 306.));
		CONTROLLER.mouseController.dragged(new Point(471., 306.));
		CONTROLLER.mouseController.dragged(new Point(498., 306.));
		CONTROLLER.mouseController.dragged(new Point(518., 309.));
		CONTROLLER.mouseController.dragged(new Point(540., 316.));
		CONTROLLER.mouseController.dragged(new Point(556., 331.));
		CONTROLLER.mouseController.dragged(new Point(571., 344.));
		CONTROLLER.mouseController.dragged(new Point(582., 359.));
		CONTROLLER.mouseController.dragged(new Point(592., 367.));
		CONTROLLER.mouseController.dragged(new Point(597., 374.));
		CONTROLLER.mouseController.dragged(new Point(604., 382.));
		CONTROLLER.mouseController.dragged(new Point(607., 386.));
		CONTROLLER.mouseController.dragged(new Point(610., 389.));
		CONTROLLER.mouseController.dragged(new Point(614., 398.));
		CONTROLLER.mouseController.dragged(new Point(616., 412.));
		CONTROLLER.mouseController.dragged(new Point(618., 453.));
		CONTROLLER.mouseController.dragged(new Point(618., 472.));
		CONTROLLER.mouseController.dragged(new Point(617., 486.));
		CONTROLLER.mouseController.dragged(new Point(613., 506.));
		CONTROLLER.mouseController.dragged(new Point(606., 533.));
		CONTROLLER.mouseController.dragged(new Point(599., 556.));
		CONTROLLER.mouseController.dragged(new Point(590., 571.));
		CONTROLLER.mouseController.dragged(new Point(580., 583.));
		CONTROLLER.mouseController.dragged(new Point(565., 599.));
		CONTROLLER.mouseController.dragged(new Point(542., 615.));
		CONTROLLER.mouseController.dragged(new Point(521., 635.));
		CONTROLLER.mouseController.released();
		
		MODEL.clear();
		
		CONTROLLER.mouseController.pressed(new Point(5.0, 5.0));
		//CONTROLLER.mouseController.dragged(new Point(6.0, 6.0));
		CONTROLLER.mouseController.dragged(new Point(7.0, 7.0));
		CONTROLLER.mouseController.released();
		//CONTROLLER.mouseController.pressed(new Point(5.0, 5.0));
		CONTROLLER.mouseController.pressed(new Point(6.0, 6.0));
		CONTROLLER.mouseController.dragged(new Point(4.0, 4.0));
		CONTROLLER.mouseController.released();
//		CONTROLLER.mouseController.pressed(new Point(267.0, 581.0));
//		CONTROLLER.mouseController.dragged(new Point(267.0, 580.0));
//		CONTROLLER.mouseController.dragged(new Point(267.0, 582.0));
//		CONTROLLER.mouseController.released();
//		CONTROLLER.mouseController.pressed(new Point(2.0, 2.0));
//		CONTROLLER.mouseController.dragged(new Point(1.0, 2.0));
//		CONTROLLER.mouseController.dragged(new Point(1.0, 1.0));
//		CONTROLLER.mouseController.dragged(new Point(1.0, 3.0));
//		CONTROLLER.mouseController.released();
//		CONTROLLER.mouseController.pressed(new Point(532.0, 627.0));
//		CONTROLLER.mouseController.dragged(new Point(515.0, 620.0));
//		CONTROLLER.mouseController.dragged(new Point(500.0, 612.0));
//		CONTROLLER.mouseController.dragged(new Point(480.0, 598.0));
//		CONTROLLER.mouseController.dragged(new Point(443.0, 565.0));
//		CONTROLLER.mouseController.dragged(new Point(415.0, 534.0));
//		CONTROLLER.mouseController.dragged(new Point(397.0, 513.0));
//		CONTROLLER.mouseController.dragged(new Point(383.0, 493.0));
//		CONTROLLER.mouseController.dragged(new Point(377.0, 473.0));
//		CONTROLLER.mouseController.dragged(new Point(372.0, 446.0));
//		CONTROLLER.mouseController.dragged(new Point(372.0, 424.0));
//		CONTROLLER.mouseController.dragged(new Point(372.0, 410.0));
//		CONTROLLER.mouseController.dragged(new Point(372.0, 396.0));
//		CONTROLLER.mouseController.dragged(new Point(372.0, 380.0));
//		CONTROLLER.mouseController.dragged(new Point(373.0, 363.0));
//		CONTROLLER.mouseController.dragged(new Point(380.0, 343.0));
//		CONTROLLER.mouseController.dragged(new Point(390.0, 331.0));
//		CONTROLLER.mouseController.dragged(new Point(403.0, 320.0));
//		CONTROLLER.mouseController.dragged(new Point(420.0, 314.0));
//		CONTROLLER.mouseController.dragged(new Point(434.0, 310.0));
//		CONTROLLER.mouseController.dragged(new Point(449.0, 306.0));
//		CONTROLLER.mouseController.dragged(new Point(460.0, 306.0));
//		CONTROLLER.mouseController.dragged(new Point(471.0, 306.0));
//		CONTROLLER.mouseController.dragged(new Point(498.0, 306.0));
//		CONTROLLER.mouseController.dragged(new Point(518.0, 309.0));
//		CONTROLLER.mouseController.dragged(new Point(540.0, 316.0));
//		CONTROLLER.mouseController.dragged(new Point(556.0, 331.0));
//		CONTROLLER.mouseController.dragged(new Point(571.0, 344.0));
//		CONTROLLER.mouseController.dragged(new Point(582.0, 359.0));
//		CONTROLLER.mouseController.dragged(new Point(592.0, 367.0));
//		CONTROLLER.mouseController.dragged(new Point(597.0, 374.0));
//		CONTROLLER.mouseController.dragged(new Point(604.0, 382.0));
//		CONTROLLER.mouseController.dragged(new Point(607.0, 386.0));
//		CONTROLLER.mouseController.dragged(new Point(610.0, 389.0));
//		CONTROLLER.mouseController.dragged(new Point(614.0, 398.0));
//		CONTROLLER.mouseController.dragged(new Point(616.0, 412.0));
//		CONTROLLER.mouseController.dragged(new Point(618.0, 453.0));
//		CONTROLLER.mouseController.dragged(new Point(618.0, 472.0));
//		CONTROLLER.mouseController.dragged(new Point(617.0, 486.0));
//		CONTROLLER.mouseController.dragged(new Point(613.0, 506.0));
//		CONTROLLER.mouseController.dragged(new Point(606.0, 533.0));
//		CONTROLLER.mouseController.dragged(new Point(599.0, 556.0));
//		CONTROLLER.mouseController.dragged(new Point(590.0, 571.0));
//		CONTROLLER.mouseController.dragged(new Point(580.0, 583.0));
//		CONTROLLER.mouseController.dragged(new Point(565.0, 599.0));
//		CONTROLLER.mouseController.dragged(new Point(542.0, 615.0));
//		CONTROLLER.mouseController.dragged(new Point(521.0, 635.0));
//		CONTROLLER.mouseController.released();
//		CONTROLLER.mouseController.pressed(new Point(841.0, 101.0));
//		CONTROLLER.mouseController.dragged(new Point(838.0, 102.0));
//		CONTROLLER.mouseController.dragged(new Point(830.0, 104.0));
//		CONTROLLER.mouseController.dragged(new Point(823.0, 105.0));
//		CONTROLLER.mouseController.dragged(new Point(814.0, 109.0));
//		CONTROLLER.mouseController.dragged(new Point(804.0, 115.0));
//		CONTROLLER.mouseController.dragged(new Point(798.0, 118.0));
//		CONTROLLER.mouseController.dragged(new Point(792.0, 125.0));
//		CONTROLLER.mouseController.dragged(new Point(787.0, 135.0));
//		CONTROLLER.mouseController.dragged(new Point(779.0, 145.0));
//		CONTROLLER.mouseController.dragged(new Point(772.0, 150.0));
//		CONTROLLER.mouseController.dragged(new Point(762.0, 158.0));
//		CONTROLLER.mouseController.dragged(new Point(752.0, 168.0));
//		CONTROLLER.mouseController.dragged(new Point(742.0, 178.0));
//		CONTROLLER.mouseController.dragged(new Point(730.0, 201.0));
//		CONTROLLER.mouseController.dragged(new Point(715.0, 241.0));
//		CONTROLLER.mouseController.dragged(new Point(711.0, 255.0));
//		CONTROLLER.mouseController.dragged(new Point(705.0, 276.0));
//		CONTROLLER.mouseController.dragged(new Point(705.0, 287.0));
//		CONTROLLER.mouseController.dragged(new Point(703.0, 306.0));
//		CONTROLLER.mouseController.dragged(new Point(703.0, 320.0));
//		CONTROLLER.mouseController.dragged(new Point(703.0, 328.0));
//		CONTROLLER.mouseController.dragged(new Point(703.0, 335.0));
//		CONTROLLER.mouseController.dragged(new Point(707.0, 345.0));
//		CONTROLLER.mouseController.dragged(new Point(711.0, 348.0));
//		CONTROLLER.mouseController.dragged(new Point(718.0, 352.0));
//		CONTROLLER.mouseController.dragged(new Point(729.0, 356.0));
//		CONTROLLER.mouseController.dragged(new Point(739.0, 359.0));
//		CONTROLLER.mouseController.dragged(new Point(748.0, 363.0));
//		CONTROLLER.mouseController.dragged(new Point(755.0, 366.0));
//		CONTROLLER.mouseController.dragged(new Point(761.0, 368.0));
//		CONTROLLER.mouseController.dragged(new Point(767.0, 370.0));
//		CONTROLLER.mouseController.dragged(new Point(770.0, 371.0));
//		CONTROLLER.mouseController.dragged(new Point(773.0, 372.0));
//		CONTROLLER.mouseController.dragged(new Point(774.0, 372.0));
//		CONTROLLER.mouseController.dragged(new Point(775.0, 372.0));
//		CONTROLLER.mouseController.released();
//		CONTROLLER.mouseController.pressed(new Point(775.0, 335.0));
//		CONTROLLER.mouseController.dragged(new Point(774.0, 333.0));
//		CONTROLLER.mouseController.dragged(new Point(771.0, 333.0));
//		CONTROLLER.mouseController.dragged(new Point(767.0, 332.0));
//		CONTROLLER.mouseController.dragged(new Point(760.0, 330.0));
//		CONTROLLER.mouseController.dragged(new Point(754.0, 329.0));
//		CONTROLLER.mouseController.dragged(new Point(748.0, 327.0));
//		CONTROLLER.mouseController.dragged(new Point(743.0, 324.0));
//		CONTROLLER.mouseController.dragged(new Point(737.0, 323.0));
//		CONTROLLER.mouseController.dragged(new Point(732.0, 319.0));
//		CONTROLLER.mouseController.dragged(new Point(728.0, 318.0));
//		CONTROLLER.mouseController.dragged(new Point(723.0, 316.0));
//		CONTROLLER.mouseController.dragged(new Point(717.0, 315.0));
//		CONTROLLER.mouseController.dragged(new Point(708.0, 313.0));
//		CONTROLLER.mouseController.dragged(new Point(699.0, 311.0));
//		CONTROLLER.mouseController.dragged(new Point(690.0, 310.0));
//		CONTROLLER.mouseController.dragged(new Point(684.0, 308.0));
//		CONTROLLER.mouseController.dragged(new Point(673.0, 306.0));
//		CONTROLLER.mouseController.dragged(new Point(670.0, 303.0));
//		CONTROLLER.mouseController.dragged(new Point(666.0, 302.0));
//		CONTROLLER.mouseController.dragged(new Point(661.0, 302.0));
//		CONTROLLER.mouseController.dragged(new Point(659.0, 301.0));
//		CONTROLLER.mouseController.dragged(new Point(656.0, 299.0));
//		CONTROLLER.mouseController.dragged(new Point(653.0, 298.0));
//		CONTROLLER.mouseController.dragged(new Point(650.0, 296.0));
//		CONTROLLER.mouseController.dragged(new Point(649.0, 295.0));
//		CONTROLLER.mouseController.dragged(new Point(647.0, 294.0));
//		CONTROLLER.mouseController.dragged(new Point(645.0, 293.0));
//		CONTROLLER.mouseController.dragged(new Point(643.0, 293.0));
//		CONTROLLER.mouseController.dragged(new Point(643.0, 292.0));
//		CONTROLLER.mouseController.dragged(new Point(642.0, 292.0));
//		CONTROLLER.mouseController.dragged(new Point(643.0, 292.0));
		
		MODEL.clear();
		
		CONTROLLER.mouseController.pressed(new Point(5.0, 5.0));
		CONTROLLER.mouseController.dragged(new Point(6.0, 6.0));
		CONTROLLER.mouseController.dragged(new Point(7.0, 7.0));
		CONTROLLER.mouseController.released();
		CONTROLLER.mouseController.pressed(new Point(5.0, 5.0));
		CONTROLLER.mouseController.pressed(new Point(6.0, 6.0));
		CONTROLLER.mouseController.dragged(new Point(4.0, 4.0));
		CONTROLLER.mouseController.released();
		CONTROLLER.mouseController.pressed(new Point(267.0, 581.0));
		CONTROLLER.mouseController.dragged(new Point(267.0, 580.0));
		CONTROLLER.mouseController.dragged(new Point(267.0, 582.0));
		CONTROLLER.mouseController.released();
		CONTROLLER.mouseController.pressed(new Point(2.0, 2.0));
		CONTROLLER.mouseController.dragged(new Point(1.0, 2.0));
		CONTROLLER.mouseController.dragged(new Point(1.0, 1.0));
		CONTROLLER.mouseController.dragged(new Point(1.0, 3.0));
		CONTROLLER.mouseController.released();
		CONTROLLER.mouseController.pressed(new Point(532.0, 627.0));
		CONTROLLER.mouseController.dragged(new Point(515.0, 620.0));
		CONTROLLER.mouseController.dragged(new Point(500.0, 612.0));
		CONTROLLER.mouseController.dragged(new Point(480.0, 598.0));
		CONTROLLER.mouseController.dragged(new Point(443.0, 565.0));
		CONTROLLER.mouseController.dragged(new Point(415.0, 534.0));
		CONTROLLER.mouseController.dragged(new Point(397.0, 513.0));
		CONTROLLER.mouseController.dragged(new Point(383.0, 493.0));
		CONTROLLER.mouseController.dragged(new Point(377.0, 473.0));
		CONTROLLER.mouseController.dragged(new Point(372.0, 446.0));
		CONTROLLER.mouseController.dragged(new Point(372.0, 424.0));
		CONTROLLER.mouseController.dragged(new Point(372.0, 410.0));
		CONTROLLER.mouseController.dragged(new Point(372.0, 396.0));
		CONTROLLER.mouseController.dragged(new Point(372.0, 380.0));
		CONTROLLER.mouseController.dragged(new Point(373.0, 363.0));
		CONTROLLER.mouseController.dragged(new Point(380.0, 343.0));
		CONTROLLER.mouseController.dragged(new Point(390.0, 331.0));
		CONTROLLER.mouseController.dragged(new Point(403.0, 320.0));
		CONTROLLER.mouseController.dragged(new Point(420.0, 314.0));
		CONTROLLER.mouseController.dragged(new Point(434.0, 310.0));
		CONTROLLER.mouseController.dragged(new Point(449.0, 306.0));
		CONTROLLER.mouseController.dragged(new Point(460.0, 306.0));
		CONTROLLER.mouseController.dragged(new Point(471.0, 306.0));
		CONTROLLER.mouseController.dragged(new Point(498.0, 306.0));
		CONTROLLER.mouseController.dragged(new Point(518.0, 309.0));
		CONTROLLER.mouseController.dragged(new Point(540.0, 316.0));
		CONTROLLER.mouseController.dragged(new Point(556.0, 331.0));
		CONTROLLER.mouseController.dragged(new Point(571.0, 344.0));
		CONTROLLER.mouseController.dragged(new Point(582.0, 359.0));
		CONTROLLER.mouseController.dragged(new Point(592.0, 367.0));
		CONTROLLER.mouseController.dragged(new Point(597.0, 374.0));
		CONTROLLER.mouseController.dragged(new Point(604.0, 382.0));
		CONTROLLER.mouseController.dragged(new Point(607.0, 386.0));
		CONTROLLER.mouseController.dragged(new Point(610.0, 389.0));
		CONTROLLER.mouseController.dragged(new Point(614.0, 398.0));
		CONTROLLER.mouseController.dragged(new Point(616.0, 412.0));
		CONTROLLER.mouseController.dragged(new Point(618.0, 453.0));
		CONTROLLER.mouseController.dragged(new Point(618.0, 472.0));
		CONTROLLER.mouseController.dragged(new Point(617.0, 486.0));
		CONTROLLER.mouseController.dragged(new Point(613.0, 506.0));
		CONTROLLER.mouseController.dragged(new Point(606.0, 533.0));
		CONTROLLER.mouseController.dragged(new Point(599.0, 556.0));
		CONTROLLER.mouseController.dragged(new Point(590.0, 571.0));
		CONTROLLER.mouseController.dragged(new Point(580.0, 583.0));
		CONTROLLER.mouseController.dragged(new Point(565.0, 599.0));
		CONTROLLER.mouseController.dragged(new Point(542.0, 615.0));
		CONTROLLER.mouseController.dragged(new Point(521.0, 635.0));
		CONTROLLER.mouseController.released();
		CONTROLLER.mouseController.pressed(new Point(841.0, 101.0));
		CONTROLLER.mouseController.dragged(new Point(838.0, 102.0));
		CONTROLLER.mouseController.dragged(new Point(830.0, 104.0));
		CONTROLLER.mouseController.dragged(new Point(823.0, 105.0));
		CONTROLLER.mouseController.dragged(new Point(814.0, 109.0));
		CONTROLLER.mouseController.dragged(new Point(804.0, 115.0));
		CONTROLLER.mouseController.dragged(new Point(798.0, 118.0));
		CONTROLLER.mouseController.dragged(new Point(792.0, 125.0));
		CONTROLLER.mouseController.dragged(new Point(787.0, 135.0));
		CONTROLLER.mouseController.dragged(new Point(779.0, 145.0));
		CONTROLLER.mouseController.dragged(new Point(772.0, 150.0));
		CONTROLLER.mouseController.dragged(new Point(762.0, 158.0));
		CONTROLLER.mouseController.dragged(new Point(752.0, 168.0));
		CONTROLLER.mouseController.dragged(new Point(742.0, 178.0));
		CONTROLLER.mouseController.dragged(new Point(730.0, 201.0));
		CONTROLLER.mouseController.dragged(new Point(715.0, 241.0));
		CONTROLLER.mouseController.dragged(new Point(711.0, 255.0));
		CONTROLLER.mouseController.dragged(new Point(705.0, 276.0));
		CONTROLLER.mouseController.dragged(new Point(705.0, 287.0));
		CONTROLLER.mouseController.dragged(new Point(703.0, 306.0));
		CONTROLLER.mouseController.dragged(new Point(703.0, 320.0));
		CONTROLLER.mouseController.dragged(new Point(703.0, 328.0));
		CONTROLLER.mouseController.dragged(new Point(703.0, 335.0));
		CONTROLLER.mouseController.dragged(new Point(707.0, 345.0));
		CONTROLLER.mouseController.dragged(new Point(711.0, 348.0));
		CONTROLLER.mouseController.dragged(new Point(718.0, 352.0));
		CONTROLLER.mouseController.dragged(new Point(729.0, 356.0));
		CONTROLLER.mouseController.dragged(new Point(739.0, 359.0));
		CONTROLLER.mouseController.dragged(new Point(748.0, 363.0));
		CONTROLLER.mouseController.dragged(new Point(755.0, 366.0));
		CONTROLLER.mouseController.dragged(new Point(761.0, 368.0));
		CONTROLLER.mouseController.dragged(new Point(767.0, 370.0));
		CONTROLLER.mouseController.dragged(new Point(770.0, 371.0));
		CONTROLLER.mouseController.dragged(new Point(773.0, 372.0));
		CONTROLLER.mouseController.dragged(new Point(774.0, 372.0));
		CONTROLLER.mouseController.dragged(new Point(775.0, 372.0));
		CONTROLLER.mouseController.released();
		CONTROLLER.mouseController.pressed(new Point(775.0, 335.0));
		CONTROLLER.mouseController.dragged(new Point(774.0, 333.0));
		CONTROLLER.mouseController.dragged(new Point(771.0, 333.0));
		CONTROLLER.mouseController.dragged(new Point(767.0, 332.0));
		CONTROLLER.mouseController.dragged(new Point(760.0, 330.0));
		CONTROLLER.mouseController.dragged(new Point(754.0, 329.0));
		CONTROLLER.mouseController.dragged(new Point(748.0, 327.0));
		CONTROLLER.mouseController.dragged(new Point(743.0, 324.0));
		CONTROLLER.mouseController.dragged(new Point(737.0, 323.0));
		CONTROLLER.mouseController.dragged(new Point(732.0, 319.0));
		CONTROLLER.mouseController.dragged(new Point(728.0, 318.0));
		CONTROLLER.mouseController.dragged(new Point(723.0, 316.0));
		CONTROLLER.mouseController.dragged(new Point(717.0, 315.0));
		CONTROLLER.mouseController.dragged(new Point(708.0, 313.0));
		CONTROLLER.mouseController.dragged(new Point(699.0, 311.0));
		CONTROLLER.mouseController.dragged(new Point(690.0, 310.0));
		CONTROLLER.mouseController.dragged(new Point(684.0, 308.0));
		CONTROLLER.mouseController.dragged(new Point(673.0, 306.0));
		CONTROLLER.mouseController.dragged(new Point(670.0, 303.0));
		CONTROLLER.mouseController.dragged(new Point(666.0, 302.0));
		CONTROLLER.mouseController.dragged(new Point(661.0, 302.0));
		CONTROLLER.mouseController.dragged(new Point(659.0, 301.0));
		CONTROLLER.mouseController.dragged(new Point(656.0, 299.0));
		CONTROLLER.mouseController.dragged(new Point(653.0, 298.0));
		CONTROLLER.mouseController.dragged(new Point(650.0, 296.0));
		CONTROLLER.mouseController.dragged(new Point(649.0, 295.0));
		CONTROLLER.mouseController.dragged(new Point(647.0, 294.0));
		CONTROLLER.mouseController.dragged(new Point(645.0, 293.0));
		CONTROLLER.mouseController.dragged(new Point(643.0, 293.0));
		CONTROLLER.mouseController.dragged(new Point(643.0, 292.0));
		CONTROLLER.mouseController.dragged(new Point(642.0, 292.0));
		CONTROLLER.mouseController.dragged(new Point(643.0, 292.0));
		CONTROLLER.mouseController.released();
		
		MODEL.clear();
		
		CONTROLLER.mouseController.pressed(new Point(532., 627.));
		CONTROLLER.mouseController.dragged(new Point(515., 620.));
		CONTROLLER.mouseController.dragged(new Point(500., 612.));
		CONTROLLER.mouseController.dragged(new Point(480., 598.));
		CONTROLLER.mouseController.dragged(new Point(443., 565.));
		CONTROLLER.mouseController.dragged(new Point(415., 534.));
		CONTROLLER.mouseController.dragged(new Point(397., 513.));
		CONTROLLER.mouseController.dragged(new Point(383., 493.));
		CONTROLLER.mouseController.dragged(new Point(377., 473.));
		CONTROLLER.mouseController.dragged(new Point(372., 446.));
		CONTROLLER.mouseController.dragged(new Point(372., 424.));
		CONTROLLER.mouseController.dragged(new Point(372., 410.));
		CONTROLLER.mouseController.dragged(new Point(372., 396.));
		CONTROLLER.mouseController.dragged(new Point(372., 380.));
		CONTROLLER.mouseController.dragged(new Point(373., 363.));
		CONTROLLER.mouseController.dragged(new Point(380., 343.));
		CONTROLLER.mouseController.dragged(new Point(390., 331.));
		CONTROLLER.mouseController.dragged(new Point(403., 320.));
		CONTROLLER.mouseController.dragged(new Point(420., 314.));
		CONTROLLER.mouseController.dragged(new Point(434., 310.));
		CONTROLLER.mouseController.dragged(new Point(449., 306.));
		CONTROLLER.mouseController.dragged(new Point(460., 306.));
		CONTROLLER.mouseController.dragged(new Point(471., 306.));
		CONTROLLER.mouseController.dragged(new Point(498., 306.));
		CONTROLLER.mouseController.dragged(new Point(518., 309.));
		CONTROLLER.mouseController.dragged(new Point(540., 316.));
		CONTROLLER.mouseController.dragged(new Point(556., 331.));
		CONTROLLER.mouseController.dragged(new Point(571., 344.));
		CONTROLLER.mouseController.dragged(new Point(582., 359.));
		CONTROLLER.mouseController.dragged(new Point(592., 367.));
		CONTROLLER.mouseController.dragged(new Point(597., 374.));
		CONTROLLER.mouseController.dragged(new Point(604., 382.));
		CONTROLLER.mouseController.dragged(new Point(607., 386.));
		CONTROLLER.mouseController.dragged(new Point(610., 389.));
		CONTROLLER.mouseController.dragged(new Point(614., 398.));
		CONTROLLER.mouseController.dragged(new Point(616., 412.));
		CONTROLLER.mouseController.dragged(new Point(618., 453.));
		CONTROLLER.mouseController.dragged(new Point(618., 472.));
		CONTROLLER.mouseController.dragged(new Point(617., 486.));
		CONTROLLER.mouseController.dragged(new Point(613., 506.));
		CONTROLLER.mouseController.dragged(new Point(606., 533.));
		CONTROLLER.mouseController.dragged(new Point(599., 556.));
		CONTROLLER.mouseController.dragged(new Point(590., 571.));
		CONTROLLER.mouseController.dragged(new Point(580., 583.));
		CONTROLLER.mouseController.dragged(new Point(565., 599.));
		CONTROLLER.mouseController.dragged(new Point(542., 615.));
		CONTROLLER.mouseController.dragged(new Point(521., 635.));
		CONTROLLER.mouseController.released();
		
		// break the loop
		CONTROLLER.mouseController.pressed(new Point(619.0, 448.0));
		CONTROLLER.mouseController.dragged(new Point(580.0, 423.0));
		CONTROLLER.mouseController.released();
		
	}
	
	static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable t) {
			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					createAndShowGUI(args);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}
	
}
