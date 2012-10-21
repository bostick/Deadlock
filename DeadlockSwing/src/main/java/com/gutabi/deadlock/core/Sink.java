package com.gutabi.deadlock.core;

import java.awt.Color;

public class Sink extends Vertex {
	
	public Source matchingSource;
	
	public Sink(Point p, Graph graph) {
		super(p, graph);
		color = Color.RED;
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
}
