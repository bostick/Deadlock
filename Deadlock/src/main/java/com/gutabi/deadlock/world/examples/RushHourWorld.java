package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.InteractiveCar;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.RushHourStud;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.CarSheet.CarType;

public class RushHourWorld extends World {
	
	private RushHourWorld(WorldScreen screen, DebuggerScreen debuggerScreen) {
		super(screen, debuggerScreen);
	}
	
	public static RushHourWorld createRushHourWorld(WorldScreen screen, DebuggerScreen debuggerScreen) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		final RushHourWorld w = new RushHourWorld(screen, debuggerScreen);
		
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
		
		/*
		 * good level
		 */
//		char[][] boardIni = new char[][] {
//				{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' ', ' '},
//				{'J', 'C', 'A', 'A', 'D', 'X', 'B', ' ', ' '},
//				{' ', 'C', 'X', 'X', 'D', 'X', 'B', ' ', ' '},
//				{' ', 'C', 'R', 'R', 'D', 'X', 'B', 'Y', ' '},
//				{' ', 'E', 'X', 'X', 'G', 'G', 'G', ' ', ' '},
//				{' ', 'E', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//				{' ', 'X', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//				{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//				{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//		};
		
		/*
		 * test joint loop
		 */
//		char[][] boardIni = new char[][] {
//				{' ', ' ', ' ', ' ', 'J', ' ', ' ', ' ', ' '},
//				{'J', 'X', 'X', 'X', 'X', 'X', 'B', ' ', ' '},
//				{' ', 'X', 'X', 'X', 'X', 'X', 'B', ' ', ' '},
//				{' ', 'X', 'R', 'R', 'X', 'X', 'B', 'Y', ' '},
//				{' ', 'E', 'X', 'X', 'X', 'X', 'X', ' ', ' '},
//				{' ', 'E', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//				{'K', 'X', 'X', 'X', 'F', 'X', 'X', ' ', ' '},
//				{' ', ' ', ' ', ' ', 'K', ' ', ' ', ' ', ' '},
//				{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//		};
		
		/*
		 * blank
		 */
//		char[][] boardIni = new char[][] {
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//			{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//		};

		
//		char[][] boardIni = Config.randomConfig().ini;
		
		char[][] boardIni = new char[][] {
			{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'A', 'A', 'A', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
			{' ', 'X', 'X', 'C', 'B', 'B', 'B', ' '},
			{' ', 'X', 'X', 'C', 'R', 'X', 'X', ' '},
			{'K', 'D', 'D', 'D', 'R', 'X', 'X', 'J'},
			{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '}
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
							if (i+1-b.originRow < b.rowCount && boardIni[i+1][j] == c) {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Axis.TOPBOTTOM, CarType.RED, 0);
								continue carLoop;
							} else {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Axis.LEFTRIGHT, CarType.RED, 0);
								continue carLoop;
							}
						} else if (i+1-b.originRow < b.rowCount && boardIni[i+1][j] == c) {
							if (i+2-b.originRow < b.rowCount && boardIni[i+2][j] == c) {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Axis.TOPBOTTOM, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Axis.TOPBOTTOM, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						} else {
							assert boardIni[i][j+1] == c;
							if (j+2-b.originCol < b.colCount && boardIni[i][j+2] == c) {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Axis.LEFTRIGHT, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(w, b, i-b.originRow, j-b.originCol, Axis.LEFTRIGHT, CarType.TWO, cur2Count);
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
	
	private static void addNewCar(World w, RushHourBoard b, int firstULRow, int firstULCol, Axis a, CarType type, int curTypeCount) {
		
		int sheetIndex = CarSheet.sheetIndex(type, curTypeCount);
		
		InteractiveCar c = InteractiveCar.createCar(w, sheetIndex);
		c.state = CarStateEnum.IDLE;
		
		/*
		 * direction of path determines direction of car
		 */
		GraphPositionPath path = null;
		Side side = null;
		switch (a) {
		case LEFTRIGHT: {
			
			path = b.getPath(a, firstULRow);
			
			GraphPosition test = new RushHourBoardPosition(b, firstULRow + c.width/2, firstULCol);
			GraphPositionPathPosition posTest = path.findClosestGraphPositionPathPosition(test);
			
			GraphPositionPathPosition next = posTest.nextBound();
			
			Point dir = next.p.minus(test.p);
			
			if (dir.x == RushHourStud.SIZE) {
				assert dir.y == 0.0;
				side = Side.RIGHT;
			} else if (dir.x == -RushHourStud.SIZE) {
				assert dir.y == 0.0;
				side = Side.LEFT;
			} else {
				assert false;
			}
			
			break;
		}
		case TOPBOTTOM: {
			
			path = b.getPath(a, firstULCol);
			
			GraphPosition test = new RushHourBoardPosition(b, firstULRow, firstULCol + c.width/2);
			GraphPositionPathPosition posTest = path.findClosestGraphPositionPathPosition(test);
			
			GraphPositionPathPosition next = posTest.nextBound();
			
			Point dir = next.p.minus(test.p);
			
			if (dir.y == RushHourStud.SIZE) {
				assert dir.x == 0.0;
				side = Side.BOTTOM;
			} else if (dir.y == -RushHourStud.SIZE) {
				assert dir.x == 0.0;
				side = Side.TOP;
			} else {
				assert false;
			}
			
			break;
		}
		}
		
		switch (side) {
		case RIGHT:
			c.driver.startGP = new RushHourBoardPosition(b, firstULRow + c.width/2, firstULCol + c.length/2);
			c.setTransform(c.driver.startGP.p, 0.0 * Math.PI);
			c.driver.overallPath = path;
			c.driver.overallPos = path.findClosestGraphPositionPathPosition(c.driver.startGP);
			assert c.driver.overallPos != null;
			break;
		case BOTTOM:
			c.driver.startGP = new RushHourBoardPosition(b, firstULRow + c.length/2, firstULCol + c.width/2);
			c.setTransform(c.driver.startGP.p, 0.5 * Math.PI);
			c.driver.overallPath = path;
			c.driver.overallPos = path.findClosestGraphPositionPathPosition(c.driver.startGP);
			assert c.driver.overallPos != null;
			break;
		case LEFT:
			c.driver.startGP = new RushHourBoardPosition(b, firstULRow + c.width/2, firstULCol + c.length/2);
			c.setTransform(c.driver.startGP.p, 1.0 * Math.PI);
			c.driver.overallPath = path;
			c.driver.overallPos = path.findClosestGraphPositionPathPosition(c.driver.startGP);
			assert c.driver.overallPos != null;
			break;
		case TOP:
			c.driver.startGP = new RushHourBoardPosition(b, firstULRow + c.length/2, firstULCol + c.width/2);
			c.setTransform(c.driver.startGP.p, 1.5 * Math.PI);
			c.driver.overallPath = path;
			c.driver.overallPos = path.findClosestGraphPositionPathPosition(c.driver.startGP);
			assert c.driver.overallPos != null;
			break;
		}
		
		c.physicsInit();
		c.setB2dCollisions(false);
		c.computeDynamicPropertiesAlways();
		c.computeDynamicPropertiesMoving();
		
		w.carMap.addCar(c);
		
	}
	
}
