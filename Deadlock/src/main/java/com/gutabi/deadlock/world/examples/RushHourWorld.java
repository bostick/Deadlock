package com.gutabi.deadlock.world.examples;

import java.util.ArrayList;


import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.Driver;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.RushHourStud;

public class RushHourWorld extends World {
	
	private RushHourWorld(WorldScreen screen) {
		super(screen);
	}
	
	@SuppressWarnings("serial")
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
//				{'0', '0', ' ', ' ', ' ', '1'},
//				{'2', ' ', ' ', '4', ' ', '1'},
//				{'2', '3', '3', '4', ' ', '1'},
//				{'2', ' ', ' ', '4', ' ', ' '},
//				{'5', ' ', ' ', ' ', '6', '6'},
//				{'5', ' ', '7', '7', '7', ' '},
//		};
		
		final RushHourBoard b = w.createRushHourBoard(new Point(8, 8));
		
		for (int i = 0; i < 1; i++) {
			if (i == 0) {
				
			}
		}
		
		Car c0 = Car.createCar(w, null, 4);
		c0.state = CarStateEnum.IDLE;
		c0.driver = new Driver(c0) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 0, 0));
					add(new RushHourBoardPosition(b, 0, 1));
					add(new RushHourBoardPosition(b, 0, 2));
					add(new RushHourBoardPosition(b, 0, 3));
					add(new RushHourBoardPosition(b, 0, 4));
//					add(new RushHourBoardPosition(b, 0, 5));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 0, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(1.0 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(1.0 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
		};
		c0.computeCtorProperties();
		c0.driver.computeStartingProperties();
		c0.p = c0.driver.gpppPointToCenter(c0.driver.overallPos.p);
		c0.angle = 1.0 * Math.PI;
		c0.setTransform(c0.p, c0.angle);
		
		w.carMap.addCar(c0);
		
		
		Car c1 = Car.createCar(w, null, 5);
		c1.state = CarStateEnum.IDLE;
		c1.driver = new Driver(c1) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 0, 5));
					add(new RushHourBoardPosition(b, 1, 5));
					add(new RushHourBoardPosition(b, 2, 5));
					add(new RushHourBoardPosition(b, 3, 5));
//					add(new RushHourBoardPosition(b, 4, 5));
//					add(new RushHourBoardPosition(b, 5, 5));
					}});
				
				overallPos = overallPath.startingPos;
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(0.5 * RushHourStud.SIZE, 1.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(0.5 * RushHourStud.SIZE, 1.5 * RushHourStud.SIZE));
			}
		};
		c1.computeCtorProperties();
		c1.computeCtorProperties();
		c1.driver.computeStartingProperties();
		c1.p = c1.driver.gpppPointToCenter(c1.driver.overallPos.p);
		c1.angle = 1.5 * Math.PI;
		c1.setTransform(c1.p, c1.angle);
		
		w.carMap.addCar(c1);
		
		
		
		Car c2 = Car.createCar(w, null, 6);
		c2.state = CarStateEnum.IDLE;
		c2.driver = new Driver(c2) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 0, 0));
					add(new RushHourBoardPosition(b, 1, 0));
					add(new RushHourBoardPosition(b, 2, 0));
					add(new RushHourBoardPosition(b, 3, 0));
//					add(new RushHourBoardPosition(b, 4, 0));
//					add(new RushHourBoardPosition(b, 5, 0));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 1, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(0.5 * RushHourStud.SIZE, 1.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(0.5 * RushHourStud.SIZE, 1.5 * RushHourStud.SIZE));
			}
		};
		c2.computeCtorProperties();
		c2.computeCtorProperties();
		c2.driver.computeStartingProperties();
		c2.p = c2.driver.gpppPointToCenter(c2.driver.overallPos.p);
		c2.angle = 1.5 * Math.PI;
		c2.setTransform(c2.p, c2.angle);
		
		w.carMap.addCar(c2);
		
		
		
		
		Car c3 = Car.createCar(w, null, 7);
		c3.state = CarStateEnum.IDLE;
		c3.driver = new Driver(c3) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
//					add(new RushHourBoardPosition(b, 2, 0));
					add(new RushHourBoardPosition(b, 2, 1));
					add(new RushHourBoardPosition(b, 2, 2));
					add(new RushHourBoardPosition(b, 2, 3));
					add(new RushHourBoardPosition(b, 2, 4));
					add(new RushHourBoardPosition(b, 2, 5));
					add(new RushHourBoardPosition(b, 2, 6));
					add(new RushHourBoardPosition(b, 2, 7));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 1, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(0.0 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(0.0 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
		};
		c3.computeCtorProperties();
		c3.computeCtorProperties();
		c3.driver.computeStartingProperties();
		c3.p = c3.driver.gpppPointToCenter(c3.driver.overallPos.p);
		c3.angle = 0.0 * Math.PI;
		c3.setTransform(c3.p, c3.angle);
		
		w.carMap.addCar(c3);
		
		
		
		
		
		Car c4 = Car.createCar(w, null, 8);
		c4.state = CarStateEnum.IDLE;
		c4.driver = new Driver(c4) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 0, 3));
					add(new RushHourBoardPosition(b, 1, 3));
					add(new RushHourBoardPosition(b, 2, 3));
					add(new RushHourBoardPosition(b, 3, 3));
//					add(new RushHourBoardPosition(b, 4, 3));
//					add(new RushHourBoardPosition(b, 5, 3));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 1, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(0.5 * RushHourStud.SIZE, 1.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(0.5 * RushHourStud.SIZE, 1.5 * RushHourStud.SIZE));
			}
		};
		c4.computeCtorProperties();
		c4.computeCtorProperties();
		c4.driver.computeStartingProperties();
		c4.p = c4.driver.gpppPointToCenter(c4.driver.overallPos.p);
		c4.angle = 1.5 * Math.PI;
		c4.setTransform(c4.p, c4.angle);
		
		w.carMap.addCar(c4);
		
		
		
		
		
		
		Car c5 = Car.createCar(w, null, 9);
		c5.state = CarStateEnum.IDLE;
		c5.driver = new Driver(c5) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 0, 0));
					add(new RushHourBoardPosition(b, 1, 0));
					add(new RushHourBoardPosition(b, 2, 0));
					add(new RushHourBoardPosition(b, 3, 0));
					add(new RushHourBoardPosition(b, 4, 0));
//					add(new RushHourBoardPosition(b, 5, 0));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 4, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(0.5 * RushHourStud.SIZE, 1.0 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(0.5 * RushHourStud.SIZE, 1.0 * RushHourStud.SIZE));
			}
		};
		c5.computeCtorProperties();
		c5.computeCtorProperties();
		c5.driver.computeStartingProperties();
		c5.p = c5.driver.gpppPointToCenter(c5.driver.overallPos.p);
		c5.angle = 1.5 * Math.PI;
		c5.setTransform(c5.p, c5.angle);
		
		w.carMap.addCar(c5);
		
		
		
		
		
		
		Car c6 = Car.createCar(w, null, 11);
		c6.state = CarStateEnum.IDLE;
		c6.driver = new Driver(c6) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 4, 0));
					add(new RushHourBoardPosition(b, 4, 1));
					add(new RushHourBoardPosition(b, 4, 2));
					add(new RushHourBoardPosition(b, 4, 3));
					add(new RushHourBoardPosition(b, 4, 4));
//					add(new RushHourBoardPosition(b, 4, 5));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 4, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(1.0 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(1.0 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
		};
		c6.computeCtorProperties();
		c6.computeCtorProperties();
		c6.driver.computeStartingProperties();
		c6.p = c6.driver.gpppPointToCenter(c6.driver.overallPos.p);
		c6.angle = 1.0 * Math.PI;
		c6.setTransform(c6.p, c6.angle);
		
		w.carMap.addCar(c6);
		
		
		
		Car c7 = Car.createCar(w, null, 10);
		c7.state = CarStateEnum.IDLE;
		c7.driver = new Driver(c7) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
					add(new RushHourBoardPosition(b, 5, 0));
					add(new RushHourBoardPosition(b, 5, 1));
					add(new RushHourBoardPosition(b, 5, 2));
					add(new RushHourBoardPosition(b, 5, 3));
//					add(new RushHourBoardPosition(b, 5, 4));
//					add(new RushHourBoardPosition(b, 5, 5));
					}});
				
				overallPos = new GraphPositionPathPosition(overallPath, 2, 0.0);
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(1.5 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(1.5 * RushHourStud.SIZE, 0.5 * RushHourStud.SIZE));
			}
		};
		c7.computeCtorProperties();
		c7.computeCtorProperties();
		c7.driver.computeStartingProperties();
		c7.p = c7.driver.gpppPointToCenter(c7.driver.overallPos.p);
		c7.angle = 1.0 * Math.PI;
		c7.setTransform(c7.p, c7.angle);
		
		w.carMap.addCar(c7);
		
		
		return w;
	}
	
}
