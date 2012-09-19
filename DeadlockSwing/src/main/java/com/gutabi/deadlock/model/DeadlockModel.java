package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	
	public final int WORLD_WIDTH = 2048;
	public final int WORLD_HEIGHT = 2048;
	
	public double DISTANCE_PER_TIMESTEP = 9.0;
	
	/*
	 * spawn cars every SPAWN_FREQUENCY time steps
	 * -1 means no spawning
	 */
	public int SPAWN_FREQUENCY = 1;
	
	public long WAIT = 16;
	
	public Random RANDOM = new Random();
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Driveable hilited;
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public final Graph graph;
	
	public List<Car> movingCars = new ArrayList<Car>();
	public List<Car> crashedCars = new ArrayList<Car>();
	
	public DeadlockModel() {
		graph = new Graph();
	}
	
	public void init() {
		
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
	
//	public void addSource(Point p) {
//		graph.addSource(p);
//	}
//	
//	public void addSink(Point p) {
//		graph.addSink(p);
//	}
	
	public void addVertexTop(Point p) {
		graph.addVertexTop(p);
	}
	
//	public void addHub(Point p) {
//		graph.addHub(p);
//	}
	
	
	
	
	
	
	
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
	public Position closestPosition(Point a, double radius) {
		return graph.closestPosition(a, radius);
	}
	
	public List<Segment> findAllSegments(Point a) {
		return graph.getSegmentTree().findAllSegments(a);
	}
	
	public Position closestPosition(Point p) {
		return graph.closestPosition(p);
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
	
//	public List<Hub> getHubs() {
//		return graph.getHubs();
//	}
	
	public ControlMode getMode() {
		assert Thread.holdsLock(MODEL);
		
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
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
