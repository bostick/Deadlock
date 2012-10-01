package com.gutabi.deadlock.model;


import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Source;



public class NormalCar extends Car {
	
	public double speed = 0.1;
	
	public NormalCar(Source s) {
		super(s);
	}

	@Override
	BufferedImage image() {
		return VIEW.normalCar;
	}
	
	public double getSpeed() {
		return speed;
	}

}
