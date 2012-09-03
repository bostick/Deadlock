package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Driveable;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Hub;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Segment;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.view.WindowInfo;

public class DeadlockModel {
	
	public final int WORLD_WIDTH = 1400;
	public final int WORLD_HEIGHT = 822;
	
	public double DISTANCE_PER_TIMESTEP = 5.0;
	
	/*
	 * spawn cars every SPAWN_FREQUENCY time steps
	 * -1 means no spawning
	 */
	public int SPAWN_FREQUENCY = 5;
	
	public long WAIT = 40;
	
	public Point viewLoc;
	public Dim viewDim;
	
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
		viewLoc = new Point(0, 0);
		viewDim = WindowInfo.windowDim();
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
	
	public void addSource(Point p) {
		graph.addSource(p);
	}
	
	public void addSink(Point p) {
		graph.addSink(p);
	}
	
	public void addHub(Point p) {
		graph.addHub(p);
	}
	
	
	
	
	public double getZoom() {
		return ((double)WindowInfo.windowWidth()) / ((double)viewDim.width);
	}
	
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
	
	public List<Hub> getHubs() {
		return graph.getHubs();
	}
	
	public Vertex tryFindVertex(Point a) {
		return graph.tryFindVertex(a);
	}
	
	public EdgePosition tryFindEdgePosition(Point a) {
		return graph.tryFindEdgePosition(a);
	}
	
	public ControlMode getMode() {
		assert Thread.holdsLock(MODEL);
		
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
	public Path shortestPath(Vertex start, Vertex end) {
		return graph.shortestPath(start, end);
	}
	
	public double distanceTo(Vertex start, Vertex end) {
		return graph.distanceTo(start, end);
	}
}
