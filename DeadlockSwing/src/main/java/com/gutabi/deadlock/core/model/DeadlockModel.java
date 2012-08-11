package com.gutabi.deadlock.core.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.IntersectionInfo;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.QuadTree.SegmentIndex;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexHandler;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.core.controller.ControlMode;

public class DeadlockModel implements VertexHandler {
	
	public final int WORLD_WIDTH = 1400;
	public final int WORLD_HEIGHT = 822;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ControlMode mode = ControlMode.IDLE;
	
	public DPoint lastPointRaw;
	public List<DPoint> curStrokeRaw = new ArrayList<DPoint>();
	public List<List<DPoint>> allStrokes = new ArrayList<List<DPoint>>();
	
	private final Graph graph;
	
	public List<Car> cars = new ArrayList<Car>();
	
	public DeadlockModel() {
		graph = new Graph(this);
	}
	
	public void clear() {
		lastPointRaw = null;
		curStrokeRaw.clear();
		allStrokes.clear();
		graph.clear();
		cars.clear();
	}
	
	public void processStroke(DPoint a, DPoint b) {
		graph.processStroke(a, b);
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
	public Vertex findClosestVertex(DPoint a) {
		
		Vertex closest = null;
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
			DPoint vp = new DPoint(v.getPoint());
			
			if (closest == null) {
				closest = v;
			} else if (Point.dist(a, vp) < Point.dist(a, closest.getPoint())) {
				closest = v;
			}
			
		}
		
		return closest;
	}
	
	public IntersectionInfo findClosestSegment(DPoint a) {
		return graph.getSegmentTree().findClosestSegment(a);
	}
	
	public List<SegmentIndex> findAllSegments(Point a) {
		return graph.getSegmentTree().findAllSegments(a);
	}
	
	public List<Vertex> getVertices() {
		return graph.getVertices();
	}
	
	public List<Edge> getEdges() {
		return graph.getEdges();
	}
	
//	public List<Vertex> getSources() {
//		return sources;
//	}
//	
//	public List<Vertex> getSinks() {
//		return sinks;
//	}
	
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
		//Map<Object, Object> m = v.getMetaData();
		
		if (p.y <= 10 || p.x <= 10) {
			//m.put("type", VertexType.SOURCE);
			//sources.add(v);
			v.setType(VertexType.SOURCE);
		} else if (p.x >= WORLD_WIDTH-10 || p.y >= WORLD_HEIGHT-10) {
			//m.put("type", VertexType.SINK);
			v.setType(VertexType.SINK);
		} else {
			//m.put("type", VertexType.COMMON);
			v.setType(VertexType.COMMON);
		}
		
	}
	
	@Override
	public void vertexRemoved(Vertex v) {
		;
	}
	
}
