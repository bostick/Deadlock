package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

@SuppressWarnings("static-access")
public class NormalCar extends Car {
	
	private final double speed = 2.5;
	
	static Logger logger = Logger.getLogger(NormalCar.class);
	
	public NormalCar(Source s) {
		super(s);
	}
	
	@Override
	BufferedImage image() {
		return MODEL.world.normalCar;
	}
	
	public double getMetersPerSecond() {
		return speed;
	}

}
