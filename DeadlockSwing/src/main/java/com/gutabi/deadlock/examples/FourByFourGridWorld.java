package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Side;
import com.gutabi.deadlock.model.Fixture;
import com.gutabi.deadlock.model.FixtureType;
import com.gutabi.deadlock.model.Map;
import com.gutabi.deadlock.model.Stroke;
import com.gutabi.deadlock.model.World;

public class FourByFourGridWorld extends World {

	public FourByFourGridWorld() {
		super(4, 4);
	}
	
	public void init() throws Exception {
		
		int[][] ini = new int[][] {
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1}
			};
			
		map = new Map(ini);
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(new Point(i * worldWidth / 16, 0), Axis.TOPBOTTOM);
			Fixture sink = new Fixture(new Point(i * worldWidth / 16, worldHeight), Axis.TOPBOTTOM);
			source.match = sink;
			sink.match = source;
			
			if (i % 2 == 0) {
				
				source.type = FixtureType.SINK;
				sink.type = FixtureType.SOURCE;
				
				source.s = Side.TOP;
				sink.s = Side.TOP;
				
			} else {
				
				source.type = FixtureType.SOURCE;
				sink.type = FixtureType.SINK;
				
				source.s = Side.BOTTOM;
				sink.s = Side.BOTTOM;
				
			}
			
			graph.addVertexTop(source);
			graph.addVertexTop(sink);
			
			Stroke s = new Stroke();
			s.add(source.p);
			s.add(sink.p);
			CONTROLLER.processNewStroke(s);
			
		}
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(new Point(0, i * worldHeight / 16), Axis.LEFTRIGHT);
			Fixture sink = new Fixture(new Point(worldWidth, i * worldHeight / 16), Axis.LEFTRIGHT);
			source.match = sink;
			sink.match = source;
			
			if (i % 2 == 0) {
				
				source.type = FixtureType.SINK;
				sink.type = FixtureType.SOURCE;
				
				source.s = Side.LEFT;
				sink.s = Side.LEFT;
				
			} else {
				
				source.type = FixtureType.SOURCE;
				sink.type = FixtureType.SINK;
				
				source.s = Side.RIGHT;
				sink.s = Side.RIGHT;
				
			}
			
			graph.addVertexTop(source);
			graph.addVertexTop(sink);
			
			Stroke s = new Stroke();
			s.add(source.p);
			s.add(sink.p);
			CONTROLLER.processNewStroke(s);
			
		}
		
		super.init();
		
	}

}
