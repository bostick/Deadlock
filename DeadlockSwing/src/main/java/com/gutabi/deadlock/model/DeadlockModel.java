package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Segment;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexHandler;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.view.WindowInfo;

public class DeadlockModel implements VertexHandler {
	
	public final int WORLD_WIDTH = 1400;
	public final int WORLD_HEIGHT = 822;
	
	public Point viewLoc;
	public Dim viewDim;
	
	public Random RANDOM = new Random();
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	private final Graph graph;
	
	public List<Car> cars = new ArrayList<Car>();
	
	public DeadlockModel() {
		graph = new Graph(this);
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
		cars.clear();
	}
	
	public void processStroke(Point a, Point b) {
		graph.processStroke(a, b);
	}
	
	public double getZoom() {
		return ((double)WindowInfo.windowWidth()) / ((double)viewDim.width);
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
	public Vertex findClosestVertex(Point a) {
		
		Vertex closest = null;
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
			Point vp = v.getPoint();
			
			if (closest == null) {
				closest = v;
			} else if (Point.dist(a, vp) < Point.dist(a, closest.getPoint())) {
				closest = v;
			}
			
		}
		
		return closest;
	}
	
	public EdgePosition findClosestEdgePosition(Point a) {
		return graph.getSegmentTree().findClosestEdgePosition(a);
	}
	
	public List<Segment> findAllSegments(Point a) {
		return graph.getSegmentTree().findAllSegments(a);
	}
	
	public List<Vertex> getVertices() {
		return graph.getVertices();
	}
	
	public List<Edge> getEdges() {
		return graph.getEdges();
	}
	
	public ControlMode getMode() {
		assert Thread.holdsLock(MODEL);
		
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
	@Override
	public void vertexCreated(Vertex v) {
		
		Point p = v.getPoint();
		
		if (p.getY() <= 10 || p.getX() <= 10) {
			v.setType(VertexType.SOURCE);
		} else if (p.getX() >= WORLD_WIDTH-10 || p.getY() >= WORLD_HEIGHT-10) {
			v.setType(VertexType.SINK);
		} else {
			v.setType(VertexType.COMMON);
		}
		
	}
	
	@Override
	public void vertexRemoved(Vertex v) {
		;
	}
	
}
