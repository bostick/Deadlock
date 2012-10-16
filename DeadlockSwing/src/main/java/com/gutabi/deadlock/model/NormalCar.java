package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

public class NormalCar extends Car {
	
	private final double speed = 0.001;
	
	static Logger logger = Logger.getLogger(NormalCar.class);
	
	public NormalCar(Source s) {
		super(s);
		
//		logger.debug("normal car: b2dBody linear v: " + b2dBody.getLinearVelocity());
		
	}

	@Override
	BufferedImage image() {
		return VIEW.normalCar;
	}
	
	public double getSpeed() {
		return speed;
	}

}
