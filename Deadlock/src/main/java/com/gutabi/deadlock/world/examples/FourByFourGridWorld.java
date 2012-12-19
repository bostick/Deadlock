package com.gutabi.deadlock.world.examples;

import java.util.HashSet;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.Vertex;

public class FourByFourGridWorld extends World {
	
	private FourByFourGridWorld(WorldCamera cam) {
		super(cam);
	}
	
	public static FourByFourGridWorld createFourByFourGridWorld(WorldCamera cam) {
		
		int[][] ini = new int[][] {
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1}
			};
		
		FourByFourGridWorld w = new FourByFourGridWorld(cam);
		
		cam.pixelsPerMeter = 12.5;
		
		QuadrantMap qm = new QuadrantMap(cam, ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(cam, w);
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(cam, w, new Point(i * qm.worldWidth / 16, 0), Axis.TOPBOTTOM);
			Fixture sink = new Fixture(cam, w, new Point(i * qm.worldWidth / 16, qm.worldHeight), Axis.TOPBOTTOM);
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
			
			Set<Vertex> res = g.addVertexTop(source);
			affected.addAll(res);
			res = g.addVertexTop(sink);
			affected.addAll(res);
			
			Stroke s = new Stroke(cam, w, g);
			s.add(source.p);
			s.add(sink.p);
			s.finish();
			
			res = s.processNewStroke();
			affected.addAll(res);
			g.computeVertexRadii(affected);
			
		}
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(cam, w, new Point(0, i * qm.worldHeight / 16), Axis.LEFTRIGHT);
			Fixture sink = new Fixture(cam, w, new Point(qm.worldWidth, i * qm.worldHeight / 16), Axis.LEFTRIGHT);
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
			
			Set<Vertex> res = g.addVertexTop(source);
			affected.addAll(res);
			res = g.addVertexTop(sink);
			affected.addAll(res);
			
			Stroke s = new Stroke(cam, w, g);
			s.add(source.p);
			s.add(sink.p);
			s.finish();
			
			res = s.processNewStroke();
			affected.addAll(res);
			g.computeVertexRadii(affected);
			
		}
		
		w.graph = g;
		
		return w;
	}
	
}