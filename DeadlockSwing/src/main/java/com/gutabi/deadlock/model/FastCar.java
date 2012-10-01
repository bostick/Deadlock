package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Source;

public class FastCar extends Car {
	
	public final double speed = 0.2;
	
	public FastCar(Source s) {
		super(s);
	}

	@Override
	BufferedImage image() {
		return VIEW.fastCar;
	}
	
	public double getSpeed() {
		return speed;
	}
}
