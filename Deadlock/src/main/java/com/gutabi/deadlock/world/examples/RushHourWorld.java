package com.gutabi.deadlock.world.examples;

import java.util.ArrayList;

import com.gutabi.deadlock.core.Point;
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
	
	public static RushHourWorld createRushHourWorld(WorldScreen screen) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		final RushHourWorld w = new RushHourWorld(screen);
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(w);
		
		w.graph = g;
		
		final RushHourBoard b = w.createRushHourBoard(new Point(8, 8));
		
		Car c0 = Car.createCar(w, null, 4);
		c0.state = CarStateEnum.IDLE;
		c0.driver = new Driver(c0) {
			public void computeStartingProperties() { 
				
				overallPath = new GraphPositionPath(new ArrayList<GraphPosition>() {{
//					add(new RushHourBoardPosition(b, 0, 0));
					add(new RushHourBoardPosition(b, 0, 1));
					add(new RushHourBoardPosition(b, 0, 2));
					add(new RushHourBoardPosition(b, 0, 3));
					add(new RushHourBoardPosition(b, 0, 4));
					add(new RushHourBoardPosition(b, 0, 5));
					}});
				
				overallPos = overallPath.startingPos;
				
			}
			public Point gpppPointToCenter(Point gppp) {
				return gppp.plus(new Point(0, 0.5 * RushHourStud.SIZE));
			}
			public Point centerToGPPPPoint(Point center) {
				return center.minus(new Point(0, 0.5 * RushHourStud.SIZE));
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
		
		
		return w;
	}
	
}
