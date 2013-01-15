package com.gutabi.deadlock.world.examples;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.InteractiveCar;
import com.gutabi.deadlock.world.cars.InteractiveDriver;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPositionPathFactory;
import com.gutabi.deadlock.world.graph.Intersection;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.RushHourStud;
import com.gutabi.deadlock.world.graph.Side;

public class RushHourWorld extends World {
	
	private RushHourWorld(WorldScreen screen) {
		super(screen);
	}
	
	public static RushHourWorld createRushHourWorld(WorldScreen screen) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		final RushHourWorld w = new RushHourWorld(screen);
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(w);
		
		w.graph = g;
		
//		char[][] rushHourIni = new char[][] {
//				{'A', 'A', ' ', ' ', ' ', 'B'},
//				{'C', ' ', ' ', 'D', ' ', 'B'},
//				{'C', 'R', 'R', 'D', ' ', 'B'},
//				{'C', ' ', ' ', 'D', ' ', ' '},
//				{'E', ' ', ' ', ' ', 'F', 'F'},
//				{'E', ' ', 'G', 'G', 'G', ' '},
//		};
		
		final RushHourBoard b = w.createRushHourBoard(new Point(8, 8));
		
		final Intersection i0 = new Intersection(w, b.a.aabb.center.minus(new Point(RushHourStud.SIZE/2, 0)));
		w.addIntersection(i0);
		
		final Intersection i1 = new Intersection(w, new Point(2, 2));
		w.addIntersection(i1);
		
		List<Point> pts = new ArrayList<Point>();
		pts.add(i0.p);
		pts.add(i1.p);
		w.createRoad(i0, i1, pts);
		
		for (int i = 0; i < 1; i++) {
			if (i == 0) {
				
			}
		}
		
		InteractiveCar c0 = InteractiveCar.createCar(w, 4);
		c0.state = CarStateEnum.IDLE;
		c0.driver = new InteractiveDriver(c0);
		c0.driver.startGP = new RushHourBoardPosition(b, 0, 0);
		c0.driver.overallSide = Side.LEFT;
		c0.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c0.driver.startGP, c0.driver.overallSide);
		c0.computeCtorProperties();
		c0.driver.computeStartingProperties();
		c0.p = c0.driver.gpppPointToCenter(c0.driver.overallPos.p);
		c0.angle = c0.driver.overallSide.getAngle();
		c0.setTransform(c0.p, c0.angle);
		
		w.carMap.addCar(c0);
		
		
		InteractiveCar c1 = InteractiveCar.createCar(w, 5);
		c1.state = CarStateEnum.IDLE;
		c1.driver = new InteractiveDriver(c1);
		c1.driver.startGP = new RushHourBoardPosition(b, 0, 5);
		c1.driver.overallSide = Side.TOP;
		c1.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c1.driver.startGP, c1.driver.overallSide);
		c1.computeCtorProperties();
		c1.driver.computeStartingProperties();
		c1.p = c1.driver.gpppPointToCenter(c1.driver.overallPos.p);
		c1.angle = c1.driver.overallSide.getAngle();
		c1.setTransform(c1.p, c1.angle);
		
		w.carMap.addCar(c1);
		
		
		
		InteractiveCar c2 = InteractiveCar.createCar(w, 6);
		c2.state = CarStateEnum.IDLE;
		c2.driver = new InteractiveDriver(c2);
		c2.driver.startGP = new RushHourBoardPosition(b, 1, 0);
		c2.driver.overallSide = Side.TOP;
		c2.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c2.driver.startGP, c2.driver.overallSide);
		c2.computeCtorProperties();
		c2.driver.computeStartingProperties();
		c2.p = c2.driver.gpppPointToCenter(c2.driver.overallPos.p);
		c2.angle = c2.driver.overallSide.getAngle();
		c2.setTransform(c2.p, c2.angle);
		
		w.carMap.addCar(c2);
		
		
		
		
		InteractiveCar c3 = InteractiveCar.createCar(w, 7);
		c3.state = CarStateEnum.IDLE;
		c3.driver = new InteractiveDriver(c3);
		c3.driver.startGP = new RushHourBoardPosition(b, 2, 2);
		c3.driver.overallSide = Side.RIGHT;
		c3.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c3.driver.startGP, c3.driver.overallSide);
		c3.computeCtorProperties();
		c3.driver.computeStartingProperties();
		c3.p = c3.driver.gpppPointToCenter(c3.driver.overallPos.p);
		c3.angle = c3.driver.overallSide.getAngle();
		c3.setTransform(c3.p, c3.angle);
		
		w.carMap.addCar(c3);
		
		
		
		
		
		InteractiveCar c4 = InteractiveCar.createCar(w, 8);
		c4.state = CarStateEnum.IDLE;
		c4.driver = new InteractiveDriver(c4);
		c4.driver.startGP = new RushHourBoardPosition(b, 1, 3);
		c4.driver.overallSide = Side.TOP;
		c4.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c4.driver.startGP, c4.driver.overallSide);
		c4.computeCtorProperties();
		c4.driver.computeStartingProperties();
		c4.p = c4.driver.gpppPointToCenter(c4.driver.overallPos.p);
		c4.angle = c4.driver.overallSide.getAngle();
		c4.setTransform(c4.p, c4.angle);
		
		w.carMap.addCar(c4);
		
		
		
		
		
		
		InteractiveCar c5 = InteractiveCar.createCar(w, 9);
		c5.state = CarStateEnum.IDLE;
		c5.driver = new InteractiveDriver(c5);
		c5.driver.startGP = new RushHourBoardPosition(b, 4, 0);
		c5.driver.overallSide = Side.TOP;
		c5.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c5.driver.startGP, c5.driver.overallSide);
		c5.computeCtorProperties();
		c5.driver.computeStartingProperties();
		c5.p = c5.driver.gpppPointToCenter(c5.driver.overallPos.p);
		c5.angle = c5.driver.overallSide.getAngle();
		c5.setTransform(c5.p, c5.angle);
		
		w.carMap.addCar(c5);
		
		
		
		
		
		
		InteractiveCar c6 = InteractiveCar.createCar(w, 11);
		c6.state = CarStateEnum.IDLE;
		c6.driver = new InteractiveDriver(c6);
		c6.driver.startGP = new RushHourBoardPosition(b, 4, 4);
		c6.driver.overallSide = Side.LEFT;
		c6.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c6.driver.startGP, c6.driver.overallSide);
		c6.computeCtorProperties();
		c6.driver.computeStartingProperties();
		c6.p = c6.driver.gpppPointToCenter(c6.driver.overallPos.p);
		c6.angle = c6.driver.overallSide.getAngle();
		c6.setTransform(c6.p, c6.angle);
		
		w.carMap.addCar(c6);
		
		
		
		InteractiveCar c7 = InteractiveCar.createCar(w, 10);
		c7.state = CarStateEnum.IDLE;
		c7.driver = new InteractiveDriver(c7);
		c7.driver.startGP = new RushHourBoardPosition(b, 5, 2);
		c7.driver.overallSide = Side.LEFT;
		c7.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, (RushHourBoardPosition)c7.driver.startGP, c7.driver.overallSide);
		c7.computeCtorProperties();
		c7.driver.computeStartingProperties();
		c7.p = c7.driver.gpppPointToCenter(c7.driver.overallPos.p);
		c7.angle = c7.driver.overallSide.getAngle();
		c7.setTransform(c7.p, c7.angle);
		
		w.carMap.addCar(c7);
		
		
		return w;
	}
	
}
