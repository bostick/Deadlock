package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

@SuppressWarnings("static-access")
public class FastCar extends Car {
	
	private final double speed = 5.0;
	
	static Logger logger = Logger.getLogger(FastCar.class);
	
	public FastCar(Source s) {
		super(s);
		overallPath = s.getShortestPathToMatchingSink();
		computeStarting();
	}
	
	@Override
	BufferedImage image() {
		return MODEL.world.fastCar;
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
	
}
