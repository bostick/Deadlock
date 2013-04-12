package com.gutabi.bypass.level;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.bypass.BypassControlPanel;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.DebuggerScreenContentPane;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreenContentPane;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.BypassBoard;
import com.gutabi.deadlock.world.graph.BypassBoardPosition;
import com.gutabi.deadlock.world.graph.BypassStud;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.VertexPosition;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.CarSheet.CarType;

public class BypassWorld extends World implements Model {
	
	public LevelDB levelDB;
	public Level curLevel;
	
	public boolean isWon;
	public WinnerMenu winnerMenu;
	
	public static void action(LevelDB levelDB, int index) {
		
		try {
			
			BypassWorld world = BypassWorld.createBypassWorld(levelDB, index);
			APP.model = world;
			
			AppScreen worldScreen = new AppScreen(new WorldScreenContentPane());
			APP.setAppScreen(worldScreen);
			
			AppScreen debuggerScreen = new AppScreen(new DebuggerScreenContentPane());
			BypassControlPanel controlPanel = new BypassControlPanel() {{
				setLocation(0, 0);
			}};
			debuggerScreen.contentPane.pcp.getChildren().add(controlPanel);
			APP.debuggerScreen = debuggerScreen;
			
			APP.tool = new BypassCarTool();
			
			APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
			
			APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.pcp);
			
			worldScreen.postDisplay();
			
			APP.debuggerScreen.postDisplay();
			
			world.startRunning();
			
			world.render_worldPanel();
			world.render_preview();
			worldScreen.contentPane.repaint();
			
			APP.platform.showAppScreen();
			APP.platform.showDebuggerScreen();
			
		} catch (Exception e) {
			assert false;
		}
		
	}
	
	public static BypassWorld createBypassWorld(LevelDB levelDB, int index) {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
				
			};
		
		final BypassWorld w = new BypassWorld();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(w);
		
		w.graph = g;
		
		w.levelDB = levelDB;
		
		try {
			
			Level level = levelDB.readLevel(index);
			
			w.curLevel = level;
			
			BypassBoard board = w.createBypassBoard(new Point(1.5 * QuadrantMap.QUADRANT_WIDTH, 2.0 * QuadrantMap.QUADRANT_HEIGHT), level.board);
			
			w.addBypassCars(board);
			
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
		
		return w;
	}
	
	void addBypassCars(BypassBoard board) {
		
		char[] carChars = new char[] { 'R', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
		int cur2Count = 0;
		int cur3Count = 0;
		carLoop:
		for (char c : carChars) {
			for (int i = board.originRow; i < board.originRow+board.rowCount; i++) {
				for (int j = board.originCol; j < board.originCol+board.colCount; j++) {
					if (board.ini[i][j] == c) {
						if (c == 'R') {
							if (i+1-board.originRow < board.rowCount && board.ini[i+1][j] == c) {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.TOPBOTTOM, CarType.RED, 0);
								continue carLoop;
							} else {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.LEFTRIGHT, CarType.RED, 0);
								continue carLoop;
							}
						} else if (i+1-board.originRow < board.rowCount && board.ini[i+1][j] == c) {
							if (i+2-board.originRow < board.rowCount && board.ini[i+2][j] == c) {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.TOPBOTTOM, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.TOPBOTTOM, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						} else {
							assert board.ini[i][j+1] == c;
							if (j+2-board.originCol < board.colCount && board.ini[i][j+2] == c) {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.LEFTRIGHT, CarType.THREE, cur3Count);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.LEFTRIGHT, CarType.TWO, cur2Count);
								cur2Count++;
								continue carLoop;
							}
						}
					}
				}
			}
		}
		
	}
	
	private void addNewCar(BypassBoard board, int firstULRow, int firstULCol, Axis a, CarType type, int curTypeCount) {
		
		int sheetIndex = CarSheet.sheetIndex(type, curTypeCount);
		
		BypassCar c = BypassCar.createCar(this, type, sheetIndex);
		c.state = CarStateEnum.IDLE;
		
		/*
		 * direction of path determines direction of car
		 */
		GraphPositionPath path = null;
		Side side = null;
		switch (a) {
		case LEFTRIGHT: {
			
			path = board.getPath(a, firstULRow);
			
			GraphPosition test = new BypassBoardPosition(board, firstULRow + c.width/2, firstULCol);
			GraphPositionPathPosition posTest = path.findGraphPositionPathPosition(test, 0.0 * Math.PI);
			
			Point dir;
			if (!posTest.isEndOfPath()) {
				GraphPositionPathPosition next = posTest.nextBound();
				dir = next.p.minus(test.p);
			} else {
				GraphPositionPathPosition prev = posTest.prevBound();
				dir = test.p.minus(prev.p);
			}
			
			if (dir.x == BypassStud.SIZE) {
				assert dir.y == 0.0;
				side = Side.RIGHT;
			} else if (dir.x == -BypassStud.SIZE) {
				assert dir.y == 0.0;
				side = Side.LEFT;
			} else {
				assert false;
			}
			
			break;
		}
		case TOPBOTTOM: {
			
			path = board.getPath(a, firstULCol);
			
			GraphPosition test = new BypassBoardPosition(board, firstULRow, firstULCol + c.width/2);
			GraphPositionPathPosition posTest = path.findGraphPositionPathPosition(test, 0.5 * Math.PI);
			
			Point dir;
			if (!posTest.isEndOfPath()) {
				GraphPositionPathPosition next = posTest.nextBound();
				dir = next.p.minus(test.p);
			} else {
				GraphPositionPathPosition prev = posTest.prevBound();
				dir = test.p.minus(prev.p);
			}
			
			if (dir.y == BypassStud.SIZE) {
				assert dir.x == 0.0;
				side = Side.BOTTOM;
			} else if (dir.y == -BypassStud.SIZE) {
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
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.width/2, firstULCol + c.length/2);
			c.setTransform(c.driver.startGP.p, 0.0 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.0 * Math.PI));
			break;
		case BOTTOM:
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.length/2, firstULCol + c.width/2);
			c.setTransform(c.driver.startGP.p, 0.5 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.5 * Math.PI));
			break;
		case LEFT:
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.width/2, firstULCol + c.length/2);
			c.setTransform(c.driver.startGP.p, 1.0 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.0 * Math.PI));
			break;
		case TOP:
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.length/2, firstULCol + c.width/2);
			c.setTransform(c.driver.startGP.p, 1.5 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.5 * Math.PI));
			break;
		}
		
		c.physicsInit();
		c.setB2dCollisions(false);
		c.computeDynamicPropertiesAlways();
		c.computeDynamicPropertiesMoving();
		
		carMap.addCar(c);
	}
	
	public static char[][] cw90(char[][] ini) {
		
		char[][] newIni = new char[ini[0].length][ini.length];
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				newIni[j][ini.length-1-i] = ini[i][j];
			}
		}
		
		return newIni;
	}
	
	public static char[][] ccw90(char[][] ini) {
		
		char[][] newIni = new char[ini[0].length][ini.length];
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				newIni[ini[i].length-1-j][i] = ini[i][j];
			}
		}
		
		return newIni;
	}

	public static char[][] transpose(char[][] ini) {
		
		char[][] newIni = new char[ini[0].length][ini.length];
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				newIni[j][i] = ini[i][j];
			}
		}
		
		return newIni;
	}
	
	public Menu getMenu() {
		return winnerMenu;
	}
	
	public void handleZooming(Car car) {
		World world = (World)APP.model;
		
		GraphPosition gpos = car.driver.overallPos.gp;
		
		double para;
		if (gpos instanceof RoadPosition) {
			
//			RoadPosition rpos = (RoadPosition)gpos;
//			
//			double alpha = rpos.lengthToStartOfRoad / rpos.r.getTotalLength(rpos.r.start, rpos.r.end);
//			if (DMath.equals(alpha, 0.0)) {
//				para = 1.0;
//			} else if (DMath.equals(alpha, 1.0)) {
//				para = 1.0;
//			} else {
//				double[] vals = new double[] {1.0, 0.3, 0.3, 0.3, 1.0};
//				double a = vals[(int)Math.floor(alpha * (vals.length-1))];
//				double b = vals[(int)Math.floor(alpha * (vals.length-1))+1];
//				para = DMath.lerp(a, b, (alpha * (vals.length-1) - Math.floor(alpha * (vals.length-1))));
//			}
			para = 0.3;
			
		} else if (gpos instanceof VertexPosition) {
			
			para = 0.3;
			
		} else {
			assert gpos instanceof BypassBoardPosition;
			
			BypassBoardPosition bpos = (BypassBoardPosition)gpos;
			BypassBoard b = (BypassBoard)bpos.entity;
			
			if (!b.floorAndCeilWithinGrid(car)) {
				
				para = 0.3;
				
			} else {
				
				para = 1.0;
				
			}
			
		}
		
		world.worldCamera.zoomAbsolute(para);
	}
	
	public void panelPostDisplay() {
		
		super.panelPostDisplay();
		
		if (winnerMenu != null) {
			winnerMenu.aabb = new AABB(worldCamera.worldPanel.aabb.width/2 - winnerMenu.aabb.width/2, worldCamera.worldPanel.aabb.height/2 - winnerMenu.aabb.height/2, winnerMenu.aabb.width, winnerMenu.aabb.height);
		}
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		super.paint_panel(ctxt);
		
		if (isWon) {
			winnerMenu.paint_panel(ctxt);
		}
		
	}

}
