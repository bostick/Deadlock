package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Side;

public class FourByFourGridWorld extends World {

	public FourByFourGridWorld() {
		super(new int[][] {
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1}
			});
	}
	
	public void init() {
		super.init();
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(this, new Point(i * worldWidth / 16, 0), Axis.TOPBOTTOM);
			Fixture sink = new Fixture(this, new Point(i * worldWidth / 16, worldHeight), Axis.TOPBOTTOM);
			source.match = sink;
			sink.match = source;
			
			if (i % 2 == 0) {
				
				source.setType(FixtureType.SINK);
				sink.setType(FixtureType.SOURCE);
				
				source.setSide(Side.TOP);
				sink.setSide(Side.TOP);
				
			} else {
				
				source.setType(FixtureType.SOURCE);
				sink.setType(FixtureType.SINK);
				
				source.setSide(Side.BOTTOM);
				sink.setSide(Side.BOTTOM);
				
			}
			
			graph.addVertexTop(source);
			graph.addVertexTop(sink);
			
			Stroke s = new Stroke(this);
			s.add(source.p);
			s.add(sink.p);
			s.finish();
			s.processNewStroke();
			
		}
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(this, new Point(0, i * worldHeight / 16), Axis.LEFTRIGHT);
			Fixture sink = new Fixture(this, new Point(worldWidth, i * worldHeight / 16), Axis.LEFTRIGHT);
			source.match = sink;
			sink.match = source;
			
			if (i % 2 == 0) {
				
				source.setType(FixtureType.SINK);
				sink.setType(FixtureType.SOURCE);
				
				source.setSide(Side.LEFT);
				sink.setSide(Side.LEFT);
				
			} else {
				
				source.setType(FixtureType.SOURCE);
				sink.setType(FixtureType.SINK);
				
				source.setSide(Side.RIGHT);
				sink.setSide(Side.RIGHT);
				
			}
			
			graph.addVertexTop(source);
			graph.addVertexTop(sink);
			
			Stroke s = new Stroke(this);
			s.add(source.p);
			s.add(sink.p);
			s.finish();
			s.processNewStroke();
			
		}
		
		pixelsPerMeter = 12.5;
		
	}
	
}
