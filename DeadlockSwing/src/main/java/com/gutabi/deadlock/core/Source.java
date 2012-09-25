package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

public class Source extends Fixture {
	
	Path pathToClosestSink;
	
	public Source(Point p) {
		super(p);
	}
	
	public void preprocess() {
		
		Sink bestChoice = null;
		
		double bestDistance = Double.POSITIVE_INFINITY;
		for (Sink s : MODEL.getSinks()) {
			double dist = this.distanceTo(s);
			if (dist < bestDistance) {
				bestDistance = dist;
				bestChoice = s;
			}
		}
		
		if (bestChoice != null) {
			
			List<GraphPosition> poss = new ArrayList<GraphPosition>();
			poss.add(this);
			poss.add(bestChoice);
			pathToClosestSink = Path.createPathFromSkeleton(poss);
			
		}
		
	}
	
	public Path getPathToClosestSink() {
		return pathToClosestSink;
	}
	
}
