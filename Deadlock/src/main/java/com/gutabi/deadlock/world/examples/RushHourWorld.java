package com.gutabi.deadlock.world.examples;

import java.util.ArrayList;
import java.util.List;

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
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;

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
		
//		Point boardUL = new Point(8, 8).minus(new Point(3 * RushHourStud.SIZE, 3 * RushHourStud.SIZE));
		final RushHourBoard b = w.createRushHourBoard(new Point(8, 8));
		
		Car c = Car.createCar(w, null, 4);
		c.state = CarStateEnum.IDLE;
		c.driver = new Driver(c) {
			public void computeStartingProperties() {
				
				List<GraphPosition> poss = new ArrayList<GraphPosition>();
				poss.add(new RushHourBoardPosition(b, 0, 0));
				poss.add(new RushHourBoardPosition(b, 0, 1));
				poss.add(new RushHourBoardPosition(b, 0, 2));
				poss.add(new RushHourBoardPosition(b, 0, 3));
				poss.add(new RushHourBoardPosition(b, 0, 4));
				poss.add(new RushHourBoardPosition(b, 0, 5));
				
				overallPath = new GraphPositionPath(poss);
//				overallPath.startingPos = null;
				
				overallPos = overallPath.startingPos;
				
			}
		};
		c.computeCtorProperties();
		c.computeStartingProperties();
		
		w.carMap.addCar(c);
		
		return w;
	}
	
}
