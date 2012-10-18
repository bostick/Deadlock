package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

public class FastCar extends Car {
	
	private final double speed = 10.0;
	
	static Logger logger = Logger.getLogger(FastCar.class);
	
	public FastCar(Source s) {
		super(s);
		
//		logger.debug("fast car: b2dBody linear v: " + b2dBody.getLinearVelocity());
		
	}

	@SuppressWarnings("static-access")
	@Override
	BufferedImage image() {
		return MODEL.world.fastCar;
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
}
