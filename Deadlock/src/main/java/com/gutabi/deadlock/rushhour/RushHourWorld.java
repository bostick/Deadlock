package com.gutabi.deadlock.rushhour;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.VertexPosition;
import com.gutabi.deadlock.world.tools.InteractiveCarTool;

public class RushHourWorld extends World {
	
	public LevelDB levelDB;
	public Level curLevel;
	
	public boolean isWon;
	public WinnerMenu winnerMenu;
	
	public static void action(LevelDB levelDB, int index) {
		
		try {
			
			World world = RushHourWorld.createRushHourWorld(levelDB, index);
			APP.model = world;
			
			WorldScreen worldScreen = new WorldScreen();
			APP.setAppScreen(worldScreen);
			
			DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
			RushHourControlPanel controlPanel = new RushHourControlPanel() {{
				setLocation(0, 0);
			}};
			debuggerScreen.contentPane.pcp.getChildren().add(controlPanel);
			APP.debuggerScreen = debuggerScreen;
			
			APP.tool = new InteractiveCarTool();
			
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
	
	public static RushHourWorld createRushHourWorld(LevelDB levelDB, int index) {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
				
			};
		
		final RushHourWorld w = new RushHourWorld();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(w);
		
		w.graph = g;
		
		w.levelDB = levelDB;
		
		try {
			
			Level level = levelDB.readLevel(index);
			
			w.curLevel = level;
			
			w.createRushHourBoard(new Point(1.5 * QuadrantMap.QUADRANT_WIDTH, 2.0 * QuadrantMap.QUADRANT_HEIGHT), level.board);
			
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
		
		return w;
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
	
	public void handleZooming(Car car) {
		World world = (World)APP.model;
		
		GraphPosition gpos = car.driver.overallPos.gp;
		
		if (gpos instanceof RoadPosition) {
			
			RoadPosition rpos = (RoadPosition)gpos;
			
			double alpha = rpos.lengthToStartOfRoad / rpos.r.getTotalLength(rpos.r.start, rpos.r.end);
			double para;
			if (DMath.equals(alpha, 0.0)) {
				para = 1.0;
			} else if (DMath.equals(alpha, 1.0)) {
				para = 1.0;
			} else {
				double[] vals = new double[] {1.0, 0.75, 0.5, 0.5, 0.5, 0.5, 0.75, 1.0};
				double a = vals[(int)Math.floor(alpha * (vals.length-1))];
				double b = vals[(int)Math.floor(alpha * (vals.length-1))+1];
				para = DMath.lerp(a, b, (alpha * (vals.length-1) - Math.floor(alpha * (vals.length-1))));
			}
			
			world.worldCamera.zoomAbsolute(para);
			
//			worldScreen.world.render_worldPanel();
			
		} else if (gpos instanceof VertexPosition) {
			
			GraphPosition prevGPos = car.driver.prevOverallPos.gp;
			
			if (prevGPos instanceof RoadPosition) {
				
				world.worldCamera.zoomAbsolute(1.0);
				
//				worldScreen.world.render_worldPanel();
			}
			
		} else {
			assert gpos instanceof RushHourBoardPosition;
			
			GraphPosition prevGPos = car.driver.prevOverallPos.gp;
			
			if (prevGPos instanceof RoadPosition) {
				
				world.worldCamera.zoomAbsolute(1.0);
				
//				worldScreen.world.render_worldPanel();
			}
		}
		
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
