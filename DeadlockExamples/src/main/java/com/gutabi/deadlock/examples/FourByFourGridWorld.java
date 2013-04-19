package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.HashSet;
import java.util.Set;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldPanel;
import com.gutabi.deadlock.world.graph.Axis;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.FixtureType;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.tools.RegularTool;

public class FourByFourGridWorld extends World implements Model {
	
	private FourByFourGridWorld() {
		
	}
	
	public static void action() {
		
		FourByFourGridWorld world = FourByFourGridWorld.createFourByFourGridWorld();
		APP.model = world;
		
		AppScreen worldScreen = new AppScreen(new ContentPane(new WorldPanel()));
		APP.setAppScreen(worldScreen);
		
		AppScreen debuggerScreen = new AppScreen(new ContentPane(new ControlPanel()));
		APP.debuggerScreen = debuggerScreen;
		
		APP.tool = new RegularTool();
		
		APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
		
		APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.pcp);
		
		worldScreen.postDisplay();
		
		APP.debuggerScreen.postDisplay();
		
		world.startRunning();
		
		world.render_worldPanel();
		world.render_preview();
		
		APP.platform.showAppScreen();
		APP.platform.showDebuggerScreen();
		
	}
	
	public static FourByFourGridWorld createFourByFourGridWorld() {
		
		int[][] ini = new int[][] {
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1},
				{1, 1, 1, 1}
			};
		
		FourByFourGridWorld w = new FourByFourGridWorld();
		
		w.worldCamera.pixelsPerMeter = 12.5;
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		Graph g = new Graph(w);
		
		w.graph = g;
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(w, new Point(i * qm.worldWidth / 16, 0), Axis.TOPBOTTOM);
			Fixture sink = new Fixture(w, new Point(i * qm.worldWidth / 16, qm.worldHeight), Axis.TOPBOTTOM);
			source.match = sink;
			sink.match = source;
			
			if (i % 2 == 0) {
				
				source.setType(FixtureType.SINK);
				sink.setType(FixtureType.SOURCE);
				
				source.setFacingSide(Side.TOP);
				sink.setFacingSide(Side.TOP);
				
			} else {
				
				source.setType(FixtureType.SOURCE);
				sink.setType(FixtureType.SINK);
				
				source.setFacingSide(Side.BOTTOM);
				sink.setFacingSide(Side.BOTTOM);
				
			}
			
			Set<Vertex> res = w.addFixture(source);
			affected.addAll(res);
			res = w.addFixture(sink);
			affected.addAll(res);
			
			Stroke s = new Stroke(w);
			s.add(source.p);
			s.add(sink.p);
			s.finish();
			
			res = s.processNewStroke(false);
			affected.addAll(res);
			g.computeVertexRadii(affected);
			
		}
		
		for (int i = 1; i < 16; i++) {
			Fixture source = new Fixture(w, new Point(0, i * qm.worldHeight / 16), Axis.LEFTRIGHT);
			Fixture sink = new Fixture(w, new Point(qm.worldWidth, i * qm.worldHeight / 16), Axis.LEFTRIGHT);
			source.match = sink;
			sink.match = source;
			
			if (i % 2 == 0) {
				
				source.setType(FixtureType.SINK);
				sink.setType(FixtureType.SOURCE);
				
				source.setFacingSide(Side.LEFT);
				sink.setFacingSide(Side.LEFT);
				
			} else {
				
				source.setType(FixtureType.SOURCE);
				sink.setType(FixtureType.SINK);
				
				source.setFacingSide(Side.RIGHT);
				sink.setFacingSide(Side.RIGHT);
				
			}
			
			Set<Vertex> res = w.addFixture(source);
			affected.addAll(res);
			res = w.addFixture(sink);
			affected.addAll(res);
			
			Stroke s = new Stroke(w);
			s.add(source.p);
			s.add(sink.p);
			s.finish();
			
			res = s.processNewStroke(false);
			affected.addAll(res);
			g.computeVertexRadii(affected);
			
		}
		
		return w;
	}
	
	public Menu getMenu() {
		return null;
	}
	
}
