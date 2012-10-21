package com.gutabi.deadlock.core;

import java.awt.Color;


public class Intersection extends Vertex {

	public Intersection(Point p, Graph graph) {
		super(p, graph);
		color = new Color(0x44, 0x44, 0x44, 0xff);
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}

}
