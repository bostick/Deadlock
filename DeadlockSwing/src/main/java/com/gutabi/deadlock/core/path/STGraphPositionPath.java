package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.GraphPosition;

public class STGraphPositionPath {
	
	private List<STGraphPosition> poss;
	private Graph graph;
	
	public STGraphPositionPath(List<STGraphPosition> poss, Graph graph) {
		this.poss = poss;
		this.graph = graph;
	}
	
	public STPointPath toSTPointPath() {
		List<STPoint> newPath = new ArrayList<STPoint>();
		for (STGraphPosition p : poss) {
			newPath.add(new STPoint(p.getSpace().getPoint(), p.getTime()));
		}
		return new STPointPath(newPath);
	}
	
	public GraphPositionPath toGraphPositionPath() {
		List<GraphPosition> newPath = new ArrayList<GraphPosition>();
		for (STGraphPosition p : poss) {
			newPath.add(p.getSpace());
		}
		return new GraphPositionPath(newPath, graph);
	}
	
}
