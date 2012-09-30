package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Source;

public class FastCar extends Car {
	
	public FastCar(Source s) {
		super(s);
	}

	@Override
	BufferedImage image() {
		return VIEW.fastCar;
	}
	
	public double getSpeed() {
		return 2;
	}
}
