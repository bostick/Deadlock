package com.gutabi.deadlock.examples;

import com.gutabi.deadlock.model.Map;
import com.gutabi.deadlock.model.World;

public class OneByOneWorld extends World {

	public OneByOneWorld() {
		super(1, 1);
	}
	
	public void init() throws Exception {
		
		int[][] ini = new int[][] {
				{1}
			};
			
		map = new Map(ini);
		
		super.init();
		
	}

}
