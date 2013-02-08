package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.InteractiveCar;
import com.gutabi.deadlock.world.graph.Graph;
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
		
		char[][] boardIni = new char[][] {
				{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' ', ' '},
				{'J', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
				{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
				{' ', 'X', 'X', 'X', 'X', 'X', 'X', 'E', 'E'},
				{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
				{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
				{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' '}
		};
		
		char[][] carIni = new char[][] {
				{'A', 'A', ' ', ' ', ' ', 'B'},
				{'C', ' ', ' ', 'D', ' ', 'B'},
				{'C', 'R', 'R', 'D', ' ', 'B'},
				{'C', ' ', ' ', 'D', ' ', ' '},
				{'E', ' ', ' ', ' ', 'F', 'F'},
				{'E', ' ', 'G', 'G', 'G', ' '}
		};
		
		final RushHourBoard b = w.createRushHourBoard(new Point(8, 8), boardIni);
		
//		char[] carChars = new char[] { 'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
		char[] carChars = new char[] { 'A', 'D' };
		int cur2Count = 0;
		int cur3Count = 0;
		carLoop:
		for (char c : carChars) {
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++) {
					if (carIni[i][j] == c) {
						if (c == 'R') {
							addNewCar(w, b, i, j+1, Side.RIGHT, CarType.RED, 0);
							continue carLoop;
						} else if (i+1 < 6 && carIni[i+1][j] == c) {
							if (i+2 < 6 && carIni[i+2][j] == c) {
								addNewCar(w, b, i, j, Side.TOP, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i, j, Side.TOP, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						} else {
							assert carIni[i][j+1] == c;
							if (j+2 < 6 && carIni[i][j+2] == c) {
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
//		c.driver = new InteractiveDriver(c);
		switch (side) {
		case RIGHT:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_WIDTH/2, frontCol + c.CAR_LENGTH/2 - 1);
			c.setTransform(c.driver.startGP.p, 0.0 * Math.PI);
			c.driver.overallPath = b.getPath(Side.RIGHT, frontRow);
			c.driver.overallPos = c.driver.overallPath.findClosestGraphPositionPathPosition(c.driver.startGP);
//			c.driver.directionInTrack = 1;
			break;
		case BOTTOM:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_LENGTH/2 - 1, frontCol + c.CAR_WIDTH/2);
			c.setTransform(c.driver.startGP.p, 0.5 * Math.PI);
			c.driver.overallPath = b.getPath(Side.BOTTOM, frontCol);
			c.driver.overallPos = c.driver.overallPath.findClosestGraphPositionPathPosition(c.driver.startGP);
//			c.driver.directionInTrack = 1;
			break;
		case LEFT:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_WIDTH/2, frontCol + c.CAR_LENGTH/2);
			c.setTransform(c.driver.startGP.p, 1.0 * Math.PI);
			c.driver.overallPath = b.getPath(Side.LEFT, frontRow);
			c.driver.overallPos = c.driver.overallPath.findClosestGraphPositionPathPosition(c.driver.startGP);
//			c.driver.directionInTrack = -1;
			break;
		case TOP:
			c.driver.startGP = new RushHourBoardPosition(b, frontRow + c.CAR_LENGTH/2, frontCol + c.CAR_WIDTH/2);
			c.setTransform(c.driver.startGP.p, 1.5 * Math.PI);
			c.driver.overallPath = b.getPath(Side.TOP, frontCol);
			c.driver.overallPos = c.driver.overallPath.findClosestGraphPositionPathPosition(c.driver.startGP);
//			c.driver.directionInTrack = -1;
			break;
		}
		
		w.carMap.addCar(c);
		
	}
	
}
