package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Segment;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;

public class World {

	public final double ROAD_WIDTH = 32.0;
	public final double VERTEX_WIDTH = Math.sqrt(2 * ROAD_WIDTH * ROAD_WIDTH);
	
	//public final double MOUSE_RADIUS = 16.0;
	public final double MOUSE_RADIUS = VERTEX_WIDTH;
	
	public final double CAR_WIDTH = ROAD_WIDTH;
	
	
	public final int WORLD_WIDTH = 512;
	public final int WORLD_HEIGHT = WORLD_WIDTH;
	
	/*
	 * spawn cars every SPAWN_FREQUENCY time steps
	 * -1 means no spawning
	 */
	public int SPAWN_FREQUENCY = 500;
	
	public Random RANDOM = new Random(1);
	
	public long WAIT = 17;
	
	public Graph graph;
	
	public List<Car> movingCars = new ArrayList<Car>();
	public List<Car> crashedCars = new ArrayList<Car>();
	
	public World() {
		
		graph = new Graph();
		
		Source a = new Source(new Point(WORLD_WIDTH/3, 0));
		Source b = new Source(new Point(2*WORLD_WIDTH/3, 0));
		Source c = new Source(new Point(0, WORLD_HEIGHT/3));
		Source d = new Source(new Point(0, 2*WORLD_HEIGHT/3));
		
		Sink e = new Sink(new Point(WORLD_WIDTH/3, WORLD_HEIGHT));
		Sink f = new Sink(new Point(2*WORLD_WIDTH/3, WORLD_HEIGHT));
		Sink g = new Sink(new Point(WORLD_WIDTH, WORLD_HEIGHT/3));
		Sink h = new Sink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/3));
		
		a.matchingSink = e;
		e.matchingSource = a;
		
		b.matchingSink = f;
		f.matchingSource = b;
		
		c.matchingSink = g;
		g.matchingSource = c;
		
		d.matchingSink = h;
		h.matchingSource = d;
		
		graph.sources.add(a);
		graph.sources.add(b);
		graph.sources.add(c);
		graph.sources.add(d);
		
		graph.sinks.add(e);
		graph.sinks.add(f);
		graph.sinks.add(g);
		graph.sinks.add(h);
		
	}
	
	/**
	 * run before the start of a simulation
	 */
	public void preStart() {
		
		graph.preStart();
		
		for (Source s : graph.getSources()) {
			s.preStart();
		}
		
	}
	
	public void processNewStroke(List<Point> stroke) {
		graph.processNewStroke(stroke);	
	}
	
	public void removeEdgeTop(Edge e) {
		graph.removeEdgeTop(e);
	}
	
	public void removeVertexTop(Vertex i) {
		graph.removeVertexTop(i);
	}
	
	public void addVertexTop(Point p) {
		graph.addVertexTop(p);
	}
	
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
	public Position findClosestPosition(Point a, double radius) {
		return graph.findClosestPosition(a, radius);
	}
	
	public List<Segment> findAllSegments(Point a) {
		return graph.getSegmentTree().findAllSegments(a);
	}
	
	public Position findClosestPosition(Point p) {
		return graph.findClosestPosition(p);
	}
	
	public Position findClosestDeleteablePosition(Point p) {
		return graph.findClosestDeleteablePosition(p);
	}
	
	public List<Intersection> getIntersections() {
		return graph.getIntersections();
	}
	
	public List<Edge> getEdges() {
		return graph.getEdges();
	}
	
	public List<Source> getSources() {
		return graph.getSources();
	}
	
	public List<Sink> getSinks() {
		return graph.getSinks();
	}
	
	public Vertex shortestPathChoice(Vertex start, Vertex end) {
		return graph.shortestPathChoice(start, end);
	}
	
	public double distanceBetweenVertices(Vertex start, Vertex end) {
		return graph.distanceBetweenVertices(start, end);
	}
	
	public boolean areNeighbors(Edge a, Edge b) {
		return graph.areNeighbors(a, b);
	}
	
	public Position hitTest(Point p) {
		return graph.hitTest(p);
	}
	
}
