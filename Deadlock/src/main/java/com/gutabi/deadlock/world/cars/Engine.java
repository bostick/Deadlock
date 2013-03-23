package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.World;

public abstract class Engine {
	
	public double maxSpeed;
	
	protected double maxRadsPerMeter;
	protected double maxAcceleration;
//	private double maxDeceleration = Double.NEGATIVE_INFINITY;
	protected double frictionForwardImpulseCoefficient;
	protected double frictionLateralImpulseCoefficient;
	protected double frictionAngularImpulseCoefficient;
	protected double driveForwardImpulseCoefficient;
	protected double driveLateralImpulseCoefficient;
	protected double brakeForwardImpulseCoefficient;
	protected double brakeLateralImpulseCoefficient;
	protected double turnAngularImpulseCoefficient;
	
	World world;
	Car c;
	
	protected Engine(World world, Car c) {
		this.world = world;
		this.c = c;
	}
	
	public abstract void preStep(double t);
	
}
