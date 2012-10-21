package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.List;

@SuppressWarnings("static-access")
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
	
	public void processNewStrokeTop(List<Point> stroke) {
		
		Point startPoint = stroke.get(0);
		Point endPoint = stroke.get(stroke.size()-1);
		
		GraphPosition startPos = graph.findClosestGraphPosition(startPoint, null, MODEL.MOUSE_RADIUS/MODEL.PIXELS_PER_METER);
		GraphPosition endPos = graph.findClosestGraphPosition(endPoint, null, MODEL.MOUSE_RADIUS/MODEL.PIXELS_PER_METER);
		
		Vertex start;
		Vertex end;
		
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
		
		graph.createEdgeTop(start, end, stroke);
		
		postTop();
	}
	
	/**
	 * after all top methods, run this to do work
	 * that is too expensive to run during editing
	 */
	private void postTop() {
		for (Vertex v : graph.getAllVertices()) {
			v.computeRadius();
		}
	}
}
