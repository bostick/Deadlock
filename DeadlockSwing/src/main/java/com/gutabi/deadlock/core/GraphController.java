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
		GraphPosition startPos = graph.findClosestGraphPosition(startPoint, stroke.getRadius());
		Vertex start;
		
		if (startPos != null) {
			if (startPos instanceof VertexPosition) {
				start = ((VertexPosition)startPos).getVertex();
			} else {
				start = graph.split((EdgePosition)startPos);
			}
		} else {
			start = new Intersection(startPoint, graph);
			graph.addIntersection((Intersection)start);
		}
		
		
		Point endPoint = stroke.getWorldPoint(stroke.size()-1);
		GraphPosition endPos = graph.findClosestGraphPosition(endPoint, stroke.getRadius());
		Vertex end;
		
		if (endPos != null) {
			if (endPos instanceof VertexPosition) {
				end = ((VertexPosition)endPos).getVertex();
			} else {
				end = graph.split((EdgePosition)endPos);
			}
		} else {
			end = new Intersection(endPoint, graph);
			graph.addIntersection((Intersection)end);
		}
		
		graph.createEdgeTop(start, end, stroke.getWorldPoints());
		
		postTop();
		
		
		
//		for (int i = 0; i < stroke.size()-1; i++) {
//			Point a = stroke.get(i);
//			Point b = stroke.get(i+1);
//			find next index and param where exiting start (and completely free)
//			
//			find next index and param where entering next event
//			find next index and param where exiting that event (and completely free)
//			
//			find next index and param where entering next event
//			find next index and param where exiting that event (and completely free)
//			
//			find next index and param where entering end
//		}
		
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
