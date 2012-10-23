package com.gutabi.deadlock.core;

import java.util.List;

import com.gutabi.deadlock.model.Stroke;

public class GraphController {
	
	Graph graph;
	
	public GraphController(Graph graph) {
		this.graph = graph;
	}
	
	public void removeEdgeTop(Edge e) {
		graph.removeEdgeTop(e);
		postTop();
	}
	
	public void removeVertexTop(Vertex v) {
		graph.removeVertexTop(v);
		postTop();
	}
	
	public void processNewStrokeTop(Stroke stroke) {
		
		Point startPoint = stroke.getWorldPoint(0);
		GraphPosition startPos = graph.findClosestGraphPosition(startPoint, Vertex.INIT_VERTEX_RADIUS);
		
		int i;
		for (i = 1; i < stroke.size(); i++) {
			Point b = stroke.getWorldPoint(i);
			GraphPosition bPos = graph.findClosestGraphPosition(b, Vertex.INIT_VERTEX_RADIUS);
			if (bPos != null) {
				if (startPos != null && bPos.getEntity() == startPos.getEntity()) {
					continue;
				} else {
					break;
				}
			}
		}
		if (i == stroke.size()) {
			/*
			 * we know that the loop reached the end
			 */
			i = stroke.size()-1;
		}
		
		Point endPoint = stroke.getWorldPoint(i);
		GraphPosition endPos = graph.findClosestGraphPosition(endPoint, Vertex.INIT_VERTEX_RADIUS);
		
		if (startPos == null && endPos == null) {
			if (DMath.lessThanEquals(Point.distance(startPoint, endPoint), Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS)) {
				/*
				 * the two new vertices would be overlapping
				 */
				return;
			}
		}
		
		if (startPos == null) {
			Intersection start = new Intersection(startPoint, graph);
			graph.addIntersection(start);
		}
		
		if (endPos == null) {
			Intersection end = new Intersection(endPoint, graph);
			graph.addIntersection(end);
		}
		
		startPos = graph.findClosestGraphPosition(startPoint, Vertex.INIT_VERTEX_RADIUS);
		assert startPos != null;
		Vertex start;
		if (startPos instanceof EdgePosition) {
			start = graph.split((EdgePosition)startPos);
		} else {
			start = ((VertexPosition)startPos).getVertex();
		}
		
		endPos = graph.findClosestGraphPosition(endPoint, Vertex.INIT_VERTEX_RADIUS);
		assert endPos != null;
		Vertex end;
		if (endPos instanceof EdgePosition) {
			end = graph.split((EdgePosition)endPos);
		} else {
			end = ((VertexPosition)endPos).getVertex();
		}
		
		graph.createEdgeTop(start, end, stroke.getWorldPoints().subList(0, i+1));
		
		postTop();
		
	}
	
	/**
	 * after all top methods, run this to do work
	 * that is too expensive to run during editing
	 */
	private void postTop() {
		List<Vertex> all = graph.getAllVertices();
		for (int i = 0; i < all.size(); i++) {
			Vertex vi = all.get(i);
			double maximumRadius = Double.POSITIVE_INFINITY;
			for (int j = 0; j < all.size(); j++) {
				Vertex vj = all.get(j);
				if (vi == vj) {
					continue;
				}
				double max = Point.distance(vi.getPoint(), vj.getPoint()) - vj.getRadius();
				if (max < maximumRadius) {
					maximumRadius = max;
				}
			}
			vi.computeRadius(maximumRadius);
		}
	}
}
