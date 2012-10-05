package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

public class FastCar extends Car {
	
	private final double speed = 0.006;
	
	static Logger logger = Logger.getLogger(FastCar.class);
	
	public FastCar(Source s) {
		super(s);
		
//		logger.debug("fast car: b2dBody linear v: " + b2dBody.getLinearVelocity());
		
	}

	@Override
	BufferedImage image() {
		return VIEW.fastCar;
	}
	
	public double getSpeed() {
		return speed;
	}
}
