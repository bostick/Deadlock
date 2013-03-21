package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.World;

public class AutonomousEngine extends Engine {
	
	public AutonomousEngine(World world, Car car) {
		super(world, car);
		
		
//		double steeringLookaheadDistance = CAR_LENGTH * 1.5;
//		double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.4;
//		double vertexArrivalLookahead = CAR_LENGTH * 0.5 + CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.4;
//		/*
//		 * turning radius
//		 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
//		 */
//		private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
//		private double maxAcceleration = getMaxSpeed() / 3.0;
//		private double maxDeceleration = -getMaxSpeed() / 3.0;
//		private double frictionForwardImpulseCoefficient = 0.0;
//		private double frictionLateralImpulseCoefficient = 0.0;
//		private double frictionAngularImpulseCoefficient = 0.0;
//		private double driveForwardImpulseCoefficient = 1.0;
//		private double driveLateralImpulseCoefficient = 1.0;
//		private double brakeForwardImpulseCoefficient = 1.0;
//		private double brakeLateralImpulseCoefficient = 1.0;
//		private double turnAngularImpulseCoefficient = 1.0;
		
		
		
		
//		double steeringLookaheadDistance = CAR_LENGTH * 0.5;
//		double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.8 * CAR_LENGTH;
//		double vertexArrivalLookahead = CAR_LENGTH * 0.5;
//		/*
//		 * turning radius
//		 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
//		 */
//		private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
//		private double maxAcceleration = Double.POSITIVE_INFINITY;
//		private double maxDeceleration = Double.NEGATIVE_INFINITY;
//		private double frictionForwardImpulseCoefficient = 0.0;
//		private double frictionLateralImpulseCoefficient = 0.0;
//		private double frictionAngularImpulseCoefficient = 0.0;
//		private double driveForwardImpulseCoefficient = 1.0;
//		private double driveLateralImpulseCoefficient = 1.0;
//		private double brakeForwardImpulseCoefficient = 1.0;
//		private double brakeLateralImpulseCoefficient = 1.0;
//		private double turnAngularImpulseCoefficient = 1.0;
		
		/*
		 * turning radius
		 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
		 */
		maxRadsPerMeter = 1.0;
		maxAcceleration = Double.POSITIVE_INFINITY;
//		private double maxDeceleration = Double.NEGATIVE_INFINITY;
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
