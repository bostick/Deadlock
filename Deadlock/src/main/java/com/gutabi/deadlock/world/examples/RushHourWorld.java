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
		
		/*
		 * 'A' through 'G', and 'R' - cars
		 * 'X' - empty stud
		 * 'J' - joint stud
		 * 'Y' - exit stud
		 */
		
//		char[][] boardIni = new char[][] {
//				{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' ', ' '},
//				{'J', 'C', 'A', 'A', 'D', 'X', 'B', ' ', ' '},
//				{' ', 'C', 'X', 'X', 'D', 'X', 'B', ' ', ' '},
//				{' ', 'C', 'R', 'R', 'D', 'X', 'B', 'Y', 'Y'},
//				{' ', 'E', 'X', 'X', 'G', 'G', 'G', ' ', ' '},
//				{' ', 'E', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//				{' ', 'X', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//				{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//				{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//		};
		char[][] boardIni = new char[][] {
				{' ', 'K', ' ', ' ', ' ', 'J', ' ', ' ', ' '},
				{' ', 'C', 'A', 'A', 'D', 'X', ' ', ' ', ' '},
				{' ', 'C', 'X', 'X', 'D', 'X', ' ', ' ', ' '},
				{' ', 'C', 'R', 'R', 'D', 'X', ' ', ' ', ' '},
				{' ', 'E', 'X', 'X', 'G', 'G', 'K', ' ', ' '},
				{' ', 'E', 'X', 'X', 'F', 'X', ' ', ' ', ' '},
				{'J', 'X', 'X', 'X', 'F', 'X', ' ', ' ', ' '},
				{' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' ', ' '},
				{' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' ', ' '}
		};
		
		final RushHourBoard b = w.createRushHourBoard(new Point(QuadrantMap.QUADRANT_WIDTH/2, QuadrantMap.QUADRANT_WIDTH/2), boardIni);
		
		char[] carChars = new char[] { 'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
//		char[] carChars = new char[] { 'A' };
		int cur2Count = 0;
		int cur3Count = 0;
		carLoop:
		for (char c : carChars) {
			for (int i = b.originRow; i < b.originRow+b.rowCount; i++) {
				for (int j = b.originCol; j < b.originCol+b.colCount; j++) {
					if (boardIni[i][j] == c) {
						if (c == 'R') {
							addNewCar(w, b, i-b.originRow, j+1-b.originCol, Side.RIGHT, CarType.RED, 0);
							continue carLoop;
						} else if (i+1-b.originRow < b.rowCount && boardIni[i+1][j] == c) {
							if (i+2-b.originRow < b.rowCount && boardIni[i+2][j] == c) {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Side.TOP, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Side.TOP, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						} else {
							assert boardIni[i][j+1] == c;
							if (j+2-b.originCol < b.colCount && boardIni[i][j+2] == c) {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Side.LEFT, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Side.LEFT, CarType.TWO, cur2Count);
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
				sheetIndex = 0;
				break;
			case 1:
				sheetIndex = 1;
				break;
			case 2:
				sheetIndex = 2;
				break;
			case 3:
				sheetIndex = 4;
				break;
			case 4:
				sheetIndex = 9;
				break;
			case 5:
				sheetIndex = 11;
				break;
			default:
				assert false;
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
			default:
				assert false;
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
