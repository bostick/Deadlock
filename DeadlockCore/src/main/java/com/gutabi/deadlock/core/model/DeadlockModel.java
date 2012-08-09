package com.gutabi.deadlock.core.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.core.DPoint;
import com.gutabi.core.Edge;
import com.gutabi.core.Graph;
import com.gutabi.core.IntersectionInfo;
import com.gutabi.core.Point;
import com.gutabi.core.QuadTree.SegmentIndex;
import com.gutabi.core.Vertex;
import com.gutabi.core.VertexHandler;

public class DeadlockModel implements VertexHandler {
	
	public static final int WORLD_WIDTH = 1400;
	public static final int WORLD_HEIGHT = 822;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private final Graph graph;
	
	private List<Vertex> sources = new ArrayList<Vertex>();
	private List<Vertex> sinks = new ArrayList<Vertex>();
	
	public DeadlockModel() {
		graph = new Graph(this);
	}
	
	public void clear() {
		graph.clear();
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
	
	public List<Vertex> getSources() {
		return sources;
	}
	
	public List<Vertex> getSinks() {
		return sinks;
	}
	
	@Override
	public void vertexCreated(Vertex v) {
		
		Point p = v.getPoint();
		//Map<Object, Object> m = v.getMetaData();
		
		if (p.y <= 10 || p.x <= 10) {
			//m.put("type", VertexType.SOURCE);
			sources.add(v);
		} else if (p.x >= WORLD_WIDTH-10 || p.y >= WORLD_HEIGHT-10) {
			//m.put("type", VertexType.SINK);
			sinks.add(v);
		} else {
			//m.put("type", VertexType.COMMON);
		}
		
	}
	
	@Override
	public void vertexRemoved(Vertex v) {
		if (sources.contains(v)) {
			sources.remove(v);
		} else if (sinks.contains(v)) {
			sinks.remove(v);
		}
	}
	
}
