package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

public class Sink extends Vertex {
	
	public Source matchingSource;
	
	public Sink(Point p) {
		super(p);
	}
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(Color.RED);
		
		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g2.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
	}
	
	public void paintHilite(Graphics2D g2) {
		
		g2.setColor(Color.RED);
		
		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-MODEL.world.VERTEX_WIDTH/2, -MODEL.world.VERTEX_WIDTH/2)));
		
		g2.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_WIDTH * MODEL.world.PIXELS_PER_METER));
		
	}
	
}
