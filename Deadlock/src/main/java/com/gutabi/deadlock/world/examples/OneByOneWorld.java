package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public class OneByOneWorld extends World {

	private OneByOneWorld(WorldScreen screen, DebuggerScreen debuggerScreen) {
		super(screen, debuggerScreen);
	}
	
	public static OneByOneWorld createOneByOneWorld(WorldScreen screen, DebuggerScreen debuggerScreen) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		OneByOneWorld w = new OneByOneWorld(screen, debuggerScreen);
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
