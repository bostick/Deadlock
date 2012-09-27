package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class Source extends Fixture {
	
	public Sink matchingSink;
	public Path pathToMatchingSink;
	
	public Source(Point p) {
		super(p);
	}
	
	public void preStart() {
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		pathToMatchingSink = Path.createPathFromSkeleton(poss);
		
	}
	
	public Path getPathToMatchingSink() {
		return pathToMatchingSink;
	}
	
}
