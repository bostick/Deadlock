package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	public static final int STOPSIGN_SIZE = 32;
	
	public Intersection(Point p, Graph graph) {
		super(p, graph);
		color = new Color(0x44, 0x44, 0x44, 0xff);
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep() {
		return true;
	}
	
	public void paint(Graphics2D g2) {
		super.paint(g2);
		
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform trans = (AffineTransform)origTransform.clone();
		trans.translate(p.x, p.y);
		
		trans.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setTransform(trans);
		
		BufferedImage im = MODEL.world.stopSign;
		
		g2.drawImage(im,
				(int)(-STOPSIGN_SIZE/2),
				(int)(-STOPSIGN_SIZE/2),
				(int)(STOPSIGN_SIZE),
				(int)(STOPSIGN_SIZE), null);
		
		g2.setTransform(origTransform);
		
	}
	
	public void paintHilite(Graphics2D g2) {
		paint(g2);
		
		g2.setColor(Color.WHITE);
		
		Stroke s = new BasicStroke((float)MODEL.METERS_PER_PIXEL);
		
		g2.setStroke(s);
		
		g2.draw(path);
		
	}
	
}
