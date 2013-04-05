package com.gutabi.deadlock.world.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.gen.BoardsIndex;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.WinnerMenu;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.tools.InteractiveCarTool;

public class RushHourWorld extends World {
	
	public int index;
	
	public boolean isWon;
	public WinnerMenu winnerMenu;
	
	public static void action(int index) {
		
		try {
			
			World world = RushHourWorld.createRushHourWorld(index);
			APP.model = world;
			
			WorldScreen worldScreen = new WorldScreen();
			APP.setAppScreen(worldScreen);
			
			DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
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
	
	public static RushHourWorld createRushHourWorld(int boardIndex) {
		
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
		
		String id = BoardsIndex.table[boardIndex];
		
		try {
			
			char[][] board = APP.platform.readBoard(APP.platform.boardResource(id));
			
			w.createRushHourBoard(new Point(1.5 * QuadrantMap.QUADRANT_WIDTH, 2.0 * QuadrantMap.QUADRANT_HEIGHT), board);
			w.index = boardIndex;
			
		} catch (Exception e) {
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
	
}
