package com.brentonbostick.capsloc.world.cars;

import com.brentonbostick.capsloc.world.World;

public abstract class Engine {
	
	public double maxSpeed;
	
	protected double maxRadsPerMeter;
	protected double maxAcceleration;
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
