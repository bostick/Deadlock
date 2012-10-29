package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.graph.Source;

@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 6.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(Source s) {
		super(s);
		overallPath = s.getRandomPathToMatchingSink();
		
		sheetCol = 0;
		sheetRow = 64;
		
		computeStartingProperties();
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
	
	
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.transform(carTrans);
		
		paintImage(g2);
		
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			// paint random path
			d;
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
		}
		
	}
}
