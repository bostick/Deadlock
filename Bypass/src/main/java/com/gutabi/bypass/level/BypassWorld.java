package com.gutabi.bypass.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.BypassControlPanel;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.UIAnimationRunnable;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.SimulationRunnable;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldMode;
import com.gutabi.deadlock.world.WorldPanel;
import com.gutabi.deadlock.world.cars.Car;
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
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.graph.VertexPosition;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.CarSheet.CarType;

public class BypassWorld extends World implements Model {
	
	public static BypassWorld BYPASSWORLD;
	
	public Level curLevel;
	
	public Label winnerLabel;
	public Label gradeLabel;
	public WinnerMenu winnerMenu;
	
	
	public static void create(int index) {
		BYPASSWORLD = BypassWorld.createBypassWorld(index);
		
		BYPASSWORLD.preStart();
		
	}
	
	public static void destroy() {
		
		BYPASSWORLD.postStop();
		
		BYPASSWORLD = null;
	}
	
	public static void start() {
		
	}
	
	public static void stop() {
		
	}
	
	AtomicBoolean trigger = new AtomicBoolean(true);
	Thread uiThread;
	
	public static void resume() {
		
		APP.model = BYPASSWORLD;
		
		AppScreen worldScreen = new AppScreen(new ContentPane(new WorldPanel()));
		APP.setAppScreen(worldScreen);
		
		APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
		
		worldScreen.postDisplay();
		
		if (APP.DEBUGGER_SCREEN) {
			
			AppScreen debuggerScreen = new AppScreen(new ContentPane(new BypassControlPanel()));
			APP.debuggerScreen = debuggerScreen;
			
			APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.pcp);
			APP.debuggerScreen.postDisplay();
			
			BYPASSWORLD.render_preview();
			
			APP.platform.showDebuggerScreen();
		}
		
		APP.tool = new BypassCarTool();
		
		BYPASSWORLD.mode = WorldMode.RUNNING;
		
		BYPASSWORLD.simThread = new Thread(new SimulationRunnable());
		BYPASSWORLD.simThread.start();
		
		BYPASSWORLD.trigger.set(true);
		
		BYPASSWORLD.uiThread = new Thread(new UIAnimationRunnable(BYPASSWORLD.trigger));
		BYPASSWORLD.uiThread.start();
		
	}
	
	public static void pause() {
		
		if (APP.DEBUGGER_SCREEN) {
			APP.platform.unshowDebuggerScreen();
		}
		
		BYPASSWORLD.trigger.set(false);
		
		BYPASSWORLD.mode = WorldMode.EDITING;
		
		try {
			BYPASSWORLD.uiThread.join();
			
			BYPASSWORLD.simThread.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void reset() {
		
		curLevel.userMoves = 0;
		
		for (Car c : carMap.cars) {
			
			switch (c.driver.startSide) {
			case RIGHT:
				c.setTransform(c.driver.startGP.p, 0.0 * Math.PI);
				c.driver.setOverallPos(c.driver.overallPath.findGraphPositionPathPosition(c.driver.startGP, 0.0 * Math.PI));
				break;
			case BOTTOM:
				c.setTransform(c.driver.startGP.p, 0.5 * Math.PI);
				c.driver.setOverallPos(c.driver.overallPath.findGraphPositionPathPosition(c.driver.startGP, 0.5 * Math.PI));
				break;
			case LEFT:
				c.setTransform(c.driver.startGP.p, 1.0 * Math.PI);
				c.driver.setOverallPos(c.driver.overallPath.findGraphPositionPathPosition(c.driver.startGP, 0.0 * Math.PI));
				break;
			case TOP:
				c.setTransform(c.driver.startGP.p, 1.5 * Math.PI);
				c.driver.setOverallPos(c.driver.overallPath.findGraphPositionPathPosition(c.driver.startGP, 0.5 * Math.PI));
				break;
			}
			
			c.setPhysicsTransform();
			
		}
		
	}
	
	public static BypassWorld createBypassWorld(int index) {
		
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
		
		try {
			
			Level level = BYPASSAPP.levelDB.readLevel(index);
			
			w.curLevel = level;
			
			level.isWon = false;
			level.userMoves = 0;
			level.userStartTime = System.currentTimeMillis();
			
			BypassBoard board = w.createBypassBoard(new Point(1.5 * QuadrantMap.QUADRANT_WIDTH, 2.0 * QuadrantMap.QUADRANT_HEIGHT), level.ini);
			
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
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.TOPBOTTOM, CarType.RED, 0, c);
								continue carLoop;
							} else {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.LEFTRIGHT, CarType.RED, 0, c);
								continue carLoop;
							}
						} else if (i+1-board.originRow < board.rowCount && board.ini[i+1][j] == c) {
							if (i+2-board.originRow < board.rowCount && board.ini[i+2][j] == c) {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.TOPBOTTOM, CarType.THREE, cur3Count, c);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.TOPBOTTOM, CarType.TWO, cur2Count, c);
								cur2Count++;
								continue carLoop;
							}
						} else {
							assert board.ini[i][j+1] == c;
							if (j+2-board.originCol < board.colCount && board.ini[i][j+2] == c) {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.LEFTRIGHT, CarType.THREE, cur3Count, c);
								cur3Count++;
								continue carLoop;
							} else {
								addNewCar(board, i-board.originRow, j-board.originCol, Axis.LEFTRIGHT, CarType.TWO, cur2Count, c);
								cur2Count++;
								continue carLoop;
							}
						}
					}
				}
			}
		}
		
	}
	
	private void addNewCar(BypassBoard board, int firstULRow, int firstULCol, Axis a, CarType type, int curTypeCount, char boardLetter) {
		
		int sheetIndex = CarSheet.sheetIndex(type, curTypeCount);
		
		BypassCar c = BypassCar.createCar(this, type, sheetIndex, boardLetter);
		
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
			c.driver.startSide = Side.RIGHT;
			c.setTransform(c.driver.startGP.p, 0.0 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.0 * Math.PI));
			break;
		case BOTTOM:
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.length/2, firstULCol + c.width/2);
			c.driver.startSide = Side.BOTTOM;
			c.setTransform(c.driver.startGP.p, 0.5 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.5 * Math.PI));
			break;
		case LEFT:
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.width/2, firstULCol + c.length/2);
			c.driver.startSide = Side.LEFT;
			c.setTransform(c.driver.startGP.p, 1.0 * Math.PI);
			c.driver.overallPath = path;
			c.driver.setOverallPos(path.findGraphPositionPathPosition(c.driver.startGP, 0.0 * Math.PI));
			break;
		case TOP:
			c.driver.startGP = new BypassBoardPosition(board, firstULRow + c.length/2, firstULCol + c.width/2);
			c.driver.startSide = Side.TOP;
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
		
		GraphPosition gpos = car.driver.overallPos.gp;
		
		BypassBoard b;
		if (gpos instanceof RoadPosition) {
			
			Vertex origExitingVertex = (Vertex)car.driver.toolOrigExitingVertexPos.gp.entity;
			
			b = origExitingVertex.s.board;
			
			if (origExitingVertex == b.exitVertex) {
				/*
				 * don't pan while exiting
				 */
				return;
			}
			
		} else if (gpos instanceof VertexPosition) {
			
			b = ((Vertex)gpos.entity).s.board;
			
			if (gpos.entity == b.exitVertex) {
				/*
				 * don't pan while exiting
				 */
				return;
			}
			
		} else {
			assert gpos instanceof BypassBoardPosition;
			b = (BypassBoard)gpos.entity;
		}
		
		Point aa = Point.worldToPanel(car.center, worldCamera);
		Point bb = aa.plus(car.toolOrigPixelOffset);
		Point cc = Point.panelToWorld(bb, worldCamera);
		
		double fraction = b.carInGridFraction(car);
		
		double cameraX = cc.x + (1-(1-fraction)) * (worldCamera.origWorldViewport.x - cc.x);
		double cameraY = cc.y + (1-(1-fraction)) * (worldCamera.origWorldViewport.y - cc.y);
		
		worldCamera.panAbsolute(cameraX, cameraY);
		
	}
	
	public void panelPostDisplay() {
		
		super.panelPostDisplay();
		
		if (curLevel.isWon) {
			
			double totalHeight = winnerLabel.aabb.height + 5 + gradeLabel.aabb.height + 5 + winnerMenu.aabb.height;
			
			gradeLabel.setLocation(worldCamera.worldPanel.aabb.width/2 - gradeLabel.aabb.width/2, worldCamera.worldPanel.aabb.height/2 - totalHeight/2);
			
			winnerLabel.setLocation(worldCamera.worldPanel.aabb.width/2 - winnerLabel.aabb.width/2, gradeLabel.aabb.y+gradeLabel.aabb.height + 5);
			
			winnerMenu.setLocation(worldCamera.worldPanel.aabb.width/2 - winnerMenu.aabb.width/2, winnerLabel.aabb.y+winnerLabel.aabb.height + 5);
			
			winnerMenu.ready = true;
		}
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		super.paint_panel(ctxt);
		
		if (curLevel.isWon && winnerMenu != null && winnerMenu.ready) {
			
			double maxWidth = winnerLabel.aabb.width;
			double x = winnerLabel.aabb.x;
			if (gradeLabel.aabb.width > maxWidth) {
				maxWidth = gradeLabel.aabb.width;
				x = gradeLabel.aabb.x;
			}
			if (winnerMenu.aabb.width > maxWidth) {
				maxWidth = winnerMenu.aabb.width;
				x = winnerMenu.aabb.x;
			}
			double totalHeight = winnerLabel.aabb.height + 5 + gradeLabel.aabb.height + 5 + winnerMenu.aabb.height;
			
			ctxt.setColor(Color.menuBackground);
			ctxt.fillRect(
					(int)(-5 + x),
					(int)(-5 + gradeLabel.aabb.y),
					(int)(maxWidth + 5 + 5),
					(int)(totalHeight + 5 + 5));
			
			gradeLabel.paint(ctxt);
			winnerLabel.paint(ctxt);
			winnerMenu.paint_panel(ctxt);
		}
		
	}

}
