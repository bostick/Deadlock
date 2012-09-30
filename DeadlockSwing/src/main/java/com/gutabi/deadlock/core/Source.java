package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.path.GraphPositionPath;

public class Source extends Vertex {
	
	public Sink matchingSink;
	GraphPositionPath pathToMatchingSink;
	
	public Source(Point p) {
		super(p);
	}
	
	public void preprocess() {
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		pathToMatchingSink = GraphPositionPath.createPathFromSkeleton(poss);
		
	}
	
	public GraphPositionPath getPathToMatchingSink() {
		return pathToMatchingSink;
	}
	
}
