package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Driveable;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Segment;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;

public class DeadlockModel {
	
	public final double ROAD_WIDTH = 32.0;
	public final double VERTEX_WIDTH = Math.sqrt(2 * ROAD_WIDTH * ROAD_WIDTH);
	
	//public final double MOUSE_RADIUS = 16.0;
	public final double MOUSE_RADIUS = VERTEX_WIDTH;
	
	public final double CAR_WIDTH = ROAD_WIDTH;
	
	
	public final int WORLD_WIDTH = 512;
	public final int WORLD_HEIGHT = WORLD_WIDTH;
	
	public double DISTANCE_PER_TIMESTEP = 3;
	
	/*
	 * spawn cars every SPAWN_FREQUENCY time steps
	 * -1 means no spawning
	 */
	public int SPAWN_FREQUENCY = 50;
	
	public long WAIT = 16;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Driveable hilited;
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public Graph graph;
	
	public List<Car> movingCars = new ArrayList<Car>();
	public List<Car> crashedCars = new ArrayList<Car>();
	
	public DeadlockModel() {
		
	}
	
	public void init() {
		graph = new Graph();
	}
	
	public void clear() {
		lastPointRaw = null;
		curStrokeRaw.clear();
		allStrokes.clear();
		graph.clear();
		movingCars.clear();
		crashedCars.clear();
	}
	
	public void processNewStroke(List<Point> stroke) {
		graph.processNewStroke(stroke);	
	}
	
	public void removeEdge(Edge e) {
		graph.removeEdgeTop(e);
	}
	
	public void removeVertex(Vertex i) {
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
	
	public ControlMode getMode() {
		assert Thread.holdsLock(MODEL);
		
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
	/**
	 * the next choice to make
	 */
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
