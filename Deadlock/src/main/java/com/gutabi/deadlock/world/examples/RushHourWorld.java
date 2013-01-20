package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.InteractiveCar;
import com.gutabi.deadlock.world.cars.InteractiveDriver;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPositionPathFactory;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
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
		
		char[][] rushHourIni = new char[][] {
				{'A', 'A', ' ', ' ', ' ', 'B'},
				{'C', ' ', ' ', 'D', ' ', 'B'},
				{'C', 'R', 'R', 'D', ' ', 'B'},
				{'C', ' ', ' ', 'D', ' ', ' '},
				{'E', ' ', ' ', ' ', 'F', 'F'},
				{'E', ' ', 'G', 'G', 'G', ' '},
		};
		
		final RushHourBoard b = w.createRushHourBoard(new Point(8, 8));
		
		char[] carChars = new char[] { 'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
		int cur2Count = 0;
		int cur3Count = 0;
		carLoop:
		for (char c : carChars) {
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++) {
					if (rushHourIni[i][j] == c) {
						if (c == 'R') {
							addNewCar(w, b, i, j+1, Side.RIGHT, CarType.RED, 0);
							continue carLoop;
						} else if (i+1 < 6 && rushHourIni[i+1][j] == c) {
							if (i+2 < 6 && rushHourIni[i+2][j] == c) {
								addNewCar(w, b, i, j, Side.TOP, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i, j, Side.TOP, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						} else {
							assert rushHourIni[i][j+1] == c;
							if (j+2 < 6 && rushHourIni[i][j+2] == c) {
								addNewCar(w, b, i, j, Side.LEFT, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i, j, Side.LEFT, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						}
					}
				}
			}
		}
		
		return w;
	}
	
	public enum CarType {
		
		TWO, THREE, RED
		
	}
	
	private static void addNewCar(World w, RushHourBoard b, int frontRow, int frontCol, Side side, CarType type, int curTypeCount) {
		
		int sheetIndex = 0;
		switch (type) {
		case TWO:
			switch (curTypeCount) {
			case 0:
				sheetIndex = 4;
				break;
			case 1:
				sheetIndex = 9;
				break;
			case 2:
				sheetIndex = 11;
				break;
			}
			break;
		case THREE:
			switch (curTypeCount) {
			case 0:
				sheetIndex = 5;
				break;
			case 1:
				sheetIndex = 6;
				break;
			case 2:
				sheetIndex = 8;
				break;
			case 3:
				sheetIndex = 10;
				break;
			}
			break;
		case RED:
			sheetIndex = 7;
			break;
		}
		
		InteractiveCar c = InteractiveCar.createCar(w, sheetIndex);
		c.state = CarStateEnum.IDLE;
		c.driver = new InteractiveDriver(c);
		switch (side) {
		case TOP:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_LENGTH/2, frontCol + c.CAR_WIDTH/2);
			break;
		case LEFT:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_WIDTH/2, frontCol + c.CAR_LENGTH/2);
			break;
		case RIGHT:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_WIDTH/2, frontCol + c.CAR_LENGTH/2 - 1);
			break;
		case BOTTOM:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_LENGTH/2 - 1, frontCol + c.CAR_WIDTH/2);
			break;
		}
		c.driver.overallSide = side;
		c.driver.overallPath = GraphPositionPathFactory.createRushHourBoardPath(b, type, (RushHourBoardPosition)c.driver.startGP, c.driver.overallSide);
		c.computeCtorProperties();
		c.computeStartingProperties();
		
		w.carMap.addCar(c);
		
	}
	
}
