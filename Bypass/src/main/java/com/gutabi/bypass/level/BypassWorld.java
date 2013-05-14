package com.gutabi.bypass.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.QuadrantMap;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.WorldPanel;
import com.gutabi.capsloc.world.cars.Car;
import com.gutabi.capsloc.world.cars.CarStateEnum;
import com.gutabi.capsloc.world.graph.Axis;
import com.gutabi.capsloc.world.graph.BypassBoard;
import com.gutabi.capsloc.world.graph.BypassBoardPosition;
import com.gutabi.capsloc.world.graph.BypassStud;
import com.gutabi.capsloc.world.graph.Graph;
import com.gutabi.capsloc.world.graph.GraphPosition;
import com.gutabi.capsloc.world.graph.GraphPositionPath;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.graph.RoadPosition;
import com.gutabi.capsloc.world.graph.Side;
import com.gutabi.capsloc.world.graph.VertexPosition;
import com.gutabi.capsloc.world.sprites.CarSheet;
import com.gutabi.capsloc.world.sprites.CarSheet.CarType;

public class BypassWorld extends World implements Model {
	
	public Lock lock = new ReentrantLock(true);
	boolean rendered;
	
	public static BypassWorld BYPASSWORLD;
	
	public LevelDB levelDB;
	public Level curLevel;
	
	public WinnerMenu winnerMenu;
	
	BypassBoard bypassBoard;
	
	
	
	public static void create(LevelDB levelDB, int index) {
		if (levelDB == null) {
			throw new IllegalArgumentException();
		}
		
		BYPASSWORLD = BypassWorld.createBypassWorld(levelDB, index);
		
		BYPASSWORLD.preStart();
		
		APP.tool = new BypassCarTool();
	}
	
	public static void destroy() {
		
		BYPASSWORLD.postStop();
		
		BYPASSWORLD = null;
	}
	
	public static void start() {
		
	}
	
	public static void stop() {
		
	}
	
	public static void resume() {
		
		APP.model = BYPASSWORLD;
		
		AppScreen worldScreen = new AppScreen(new ContentPane(new WorldPanel()));
		APP.setAppScreen(worldScreen);
		
		APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
		
//		if (APP.DEBUGGER_SCREEN) {
//			
//			AppScreen debuggerScreen = new AppScreen(new ContentPane(new BypassControlPanel()));
//			APP.debuggerScreen = debuggerScreen;
//			
//		}
		
		BYPASSWORLD.simThreadTrigger.set(true);
		
		BYPASSWORLD.simThread = new Thread(new SimulationRunnable(BYPASSWORLD.simThreadTrigger));
		BYPASSWORLD.simThread.start();
		
	}
	
	public static void surfaceChanged(int width, int height) {
		
		BYPASSWORLD.lock.lock();
		
		APP.appScreen.postDisplay(width, height);
		
		BYPASSWORLD.render_worldPanel();
		
//		if (APP.DEBUGGER_SCREEN) {
//			
//			APP.debuggerScreen.postDisplay(width, height);
//			
//			BYPASSWORLD.render_preview();
//			
//			APP.platform.showDebuggerScreen();
//		}
		
		BYPASSWORLD.lock.unlock();
		
		/*
		 * repaint once just in case there is nothing else driving repainting (like shimmering)
		 */
		APP.appScreen.contentPane.repaint();
	}
	
	public static void pause() {
		
//		if (APP.DEBUGGER_SCREEN) {
//			APP.platform.unshowDebuggerScreen();
//		}
		
		BYPASSWORLD.simThreadTrigger.set(false);
		
		try {
			
			BYPASSWORLD.simThread.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void reset() {
		
		if (curLevel.isWon) {
			return;
		}
		
		BYPASSWORLD.simThreadTrigger.set(false);
		
		try {
			
			BYPASSWORLD.simThread.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		curLevel.userMoves = 0;
		
		BypassCarTool tool = (BypassCarTool)APP.tool;
		
		BypassCar movingCar = tool.car;
		if (movingCar != null) {
			movingCar.coastingVel = 0.0;
		}
		
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
			
			c.state = CarStateEnum.IDLE;
			
		}
		
		double cameraX = worldCamera.origWorldViewport.x;
		double cameraY = worldCamera.origWorldViewport.y;
		
		worldCamera.panAbsolute(cameraX, cameraY);
		worldCamera.zoomAbsolute(1.0);
		
		BYPASSWORLD.simThreadTrigger.set(true);
		
		BYPASSWORLD.simThread = new Thread(new SimulationRunnable(BYPASSWORLD.simThreadTrigger));
		BYPASSWORLD.simThread.start();
		
	}
	
	public static BypassWorld createBypassWorld(LevelDB levelDB, int index) {
		if (levelDB == null) {
			throw new IllegalArgumentException();
		}
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
			};
		
		BypassWorld w = new BypassWorld();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(w);
		
		w.graph = g;
		
		try {
			
			Level level = levelDB.getLevel(index);
			
			w.levelDB = levelDB;
			w.curLevel = level;
			
			level.isWon = false;
			level.userMoves = 0;
			level.userStartTime = System.currentTimeMillis();
			
			BypassBoard board = w.createBypassBoard(new Point(qm.worldAABB.width / 2, qm.worldAABB.height / 2), level.ini);
			
			w.bypassBoard = board;
			
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
	
	public Menu getMenu() {
		return winnerMenu;
	}
	
	public void handlePanning(Car car, Point center) {
		
		if (car.driver.toolLastExitingVertexPos != null && car.driver.toolLastExitingVertexPos.gp.entity == bypassBoard.exitVertex) {
			/*
			 * no panning when exiting
			 */
			return;
		}
		
		GraphPosition gpos = car.driver.overallPos.gp;
		
		if (gpos instanceof RoadPosition) {
			
			
			
		} else if (gpos instanceof VertexPosition) {
			
		} else {
			assert gpos instanceof BypassBoardPosition;
			
			if (bypassBoard.withinGrid(car, car.angle, car.center)) {
				
				double cameraX = worldCamera.origWorldViewport.x;
				double cameraY = worldCamera.origWorldViewport.y;
				
				worldCamera.panAbsolute(cameraX, cameraY);
				worldCamera.zoomAbsolute(1.0);
				return;
			}
		}
		
		Point aa = Point.worldToPanel(center, worldCamera);
		Point bb = aa.minus(worldCamera.panelAABB.centerX, worldCamera.panelAABB.centerY);
		Point cc = Point.panelToWorld(bb, worldCamera);
		
		double fraction = bypassBoard.carInGridFraction(car);
		
		double cameraX;
		double cameraY;
		
		cameraX = cc.x + fraction * (worldCamera.origWorldViewport.x - cc.x);
		cameraY = cc.y + fraction * (worldCamera.origWorldViewport.y - cc.y);
		
		worldCamera.panAbsolute(cameraX, cameraY);
		worldCamera.zoomAbsolute(1.0);
	}
	
	public void winner() {
		
		int diff = (curLevel.userMoves - curLevel.requiredMoves);
		if (diff <= 0) {
			/*
			 * diff can be negative in tutorials since we just set it to 100
			 */
			curLevel.grade = "A+";
		} else if (diff < 1 * 3) {
			curLevel.grade = "A";
		} else if (diff < 9) {
			curLevel.grade = "A-";
		} else if (diff < 27) {
			curLevel.grade = "B+";
		} else if (diff < 81) {
			curLevel.grade = "B";
		} else if (diff < 243) {
			curLevel.grade = "B-";
		} else if (diff < 729) {
			curLevel.grade = "C+";
		} else if (diff < 2187) {
			curLevel.grade = "C";
		} else if (diff < 6561) {
			curLevel.grade = "C-";
		} else if (diff < 19683) {
			curLevel.grade = "D+";
		} else if (diff < 59049) {
			curLevel.grade = "D";
		} else if (diff < 177147) {
			curLevel.grade = "D-";
		} else {
			curLevel.grade = "F";
		}
		
		curLevel.userTime = (System.currentTimeMillis() - curLevel.userStartTime);
		
		curLevel.isWon = true;
		
		BYPASSAPP.bypassPlatform.saveScore(levelDB, curLevel);
		
		for (int i = 0; i < levelDB.levelCount; i++) {
			if (levelDB.getLevel(i).isWon) {
				
			} else {
				levelDB.firstUnwon = i;
				break;
			}
		}
		
		levelDB.computePercentageComplete();
		
		WinnerMenu.action();
		
	}
	
	public void panelPostDisplay(int width, int height) {
		
		int min = Math.min(width, height);
		double ppm = min / 8.0;
		
		worldCamera.pixelsPerMeter = ppm;
		worldCamera.origPixelsPerMeter = ppm;
		
		super.panelPostDisplay(width, height);
		
		if (curLevel.isWon) {
			
			winnerMenu.postDisplay(width, height);
		}
	}
	
	public void render_worldPanel() {
		
		super.render_worldPanel();
		
		if (curLevel.isWon) {
			
			winnerMenu.render();
			
		}
		
		rendered = true;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		BYPASSWORLD.lock.lock();
		try {
			
			if (!BYPASSWORLD.rendered) {
				return;
			}
			
			super.paint_panel(ctxt);
			
			if (curLevel.isWon && winnerMenu != null && winnerMenu.ready) {
				winnerMenu.paint_panel(ctxt);
			}
			
		} finally {
			BYPASSWORLD.lock.unlock();
		}
	}

}
