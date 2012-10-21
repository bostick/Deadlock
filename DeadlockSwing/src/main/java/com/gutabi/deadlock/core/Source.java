package com.gutabi.deadlock.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.path.GraphPositionPath;

public class Source extends Vertex {
	
	public Sink matchingSink;
	GraphPositionPath pathToMatchingSink;
	
	public Source(Point p, Graph graph) {
		super(p, graph);
		color = Color.GREEN;
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public void preStart() {
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		pathToMatchingSink = GraphPositionPath.createPathFromSkeleton(poss, graph);
		
	}
	
	public GraphPositionPath getPathToMatchingSink() {
		return pathToMatchingSink;
	}
	
}
