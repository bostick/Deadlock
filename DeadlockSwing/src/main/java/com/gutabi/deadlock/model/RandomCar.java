package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 2.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(Source s) {
		super(s);
		overallPath = s.getRandomPathToMatchingSink();
		computeStartingProperties();
	}
	
	@Override
	BufferedImage image() {
		return MODEL.world.randomCar;
	}
	
	public double getMetersPerSecond() {
		return speed;
	}

}
