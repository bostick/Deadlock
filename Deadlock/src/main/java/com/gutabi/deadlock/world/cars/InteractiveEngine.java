package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.World;

public class InteractiveEngine extends Engine {

	protected InteractiveEngine(World world, Car c) {
		super(world, c);
		
		/*
		 * turning radius
		 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
		 */
		maxRadsPerMeter = 1.0;
		maxAcceleration = Double.POSITIVE_INFINITY;
		
		frictionForwardImpulseCoefficient = 0.01;
		frictionLateralImpulseCoefficient = 0.04;
		frictionAngularImpulseCoefficient = 0.02;
		
		driveForwardImpulseCoefficient = 0.02;
		driveLateralImpulseCoefficient = 1.0;
		brakeForwardImpulseCoefficient = 0.05;
		brakeLateralImpulseCoefficient = 1.0;
		turnAngularImpulseCoefficient = 1.0;
		
	}

}
