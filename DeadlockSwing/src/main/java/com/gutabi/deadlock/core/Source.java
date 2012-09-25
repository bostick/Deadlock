package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class Source extends Fixture {
	
	Sink matchingSink;
	Path pathToMatchingSink;
	
	public Source(Point p) {
		super(p);
	}
	
	public void preprocess() {
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		pathToMatchingSink = Path.createPathFromSkeleton(poss);
		
	}
	
	public Path getPathToMatchingSink() {
		return pathToMatchingSink;
	}
	
}
