package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.math.Point;
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
	
	public void preStep(double t) {
		
		switch (c.state) {
		case DRIVING:
			updateFriction();
			updateDrive(t);
			break;
		case BRAKING:
			updateFriction();
			updateBrake(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
		case COASTING_FORWARD:
			updateFriction();
			updateDrive(t);
			break;
		case COASTING_BACKWARD:
			updateFriction();
			updateReverseDrive(t);
			break;
		case IDLE:
		case DRAGGING:
			break;
		}
		
	}
	
	private void updateFriction() {
		c.applyCancelingForwardImpulse(frictionForwardImpulseCoefficient);
		c.applyCancelingLateralImpulse(frictionLateralImpulseCoefficient);
		c.applyCancelingAngularImpulse(frictionAngularImpulseCoefficient);
	}
	
	private void updateDrive(double t) {
		
		double dv;
		if (maxSpeed > c.forwardSpeed) {
			dv = maxSpeed - c.forwardSpeed;
		} else {
			dv = 0.0f;
		}
		if (dv < 0) {
			assert false;
		}
		if (dv > maxAcceleration * world.worldScreen.DT) {
			dv = maxAcceleration * world.worldScreen.DT;
		}
		
		c.applyForwardImpulse(driveForwardImpulseCoefficient, dv);
		
		c.applyCancelingLateralImpulse(driveLateralImpulseCoefficient);
		
		
		Point dp = new Point(c.driver.goalPoint.x-c.center.x, c.driver.goalPoint.y-c.center.y);
		
		double goalAngle = Math.atan2(dp.y, dp.x);
		
		double dw = ((float)goalAngle) - c.angle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		/*
		 * turning radius
		 */
		
		double actualDistance = Math.abs(c.forwardSpeed * world.worldScreen.DT);
		double maxRads = maxRadsPerMeter * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		float goalAngVel = (float)(dw / world.worldScreen.DT);
		
		c.applyAngularImpulse(turnAngularImpulseCoefficient, (goalAngVel - c.angularVel));
		
	}
	
	private void updateReverseDrive(double t) {
		
		double dv;
		
		double backwardSpeed = -c.forwardSpeed;
		
		if (maxSpeed > backwardSpeed) {
			dv = maxSpeed - backwardSpeed;
		} else {
			dv = 0.0f;
		}
		if (dv < 0) {
			assert false;
		}
		if (dv > maxAcceleration * world.worldScreen.DT) {
			dv = maxAcceleration * world.worldScreen.DT;
		}
		
		c.applyForwardImpulse(driveForwardImpulseCoefficient, -dv);
		
		c.applyCancelingLateralImpulse(driveLateralImpulseCoefficient);
		
		Point dp = new Point(c.driver.goalPoint.x-c.center.x, c.driver.goalPoint.y-c.center.y);
		
		double goalAngle = Math.atan2(dp.y, dp.x);
		
		double dw = ((float)goalAngle) - c.angle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		/*
		 * turning radius
		 */
		
		double actualDistance = Math.abs(c.forwardSpeed * world.worldScreen.DT);
		double maxRads = maxRadsPerMeter * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		float goalAngVel = (float)(dw / world.worldScreen.DT);
		
		c.applyAngularImpulse(turnAngularImpulseCoefficient, (goalAngVel - c.angularVel));
		
	}
	
	private void updateBrake(double t) {
		
		if (((AutonomousDriver)c.driver).decelTime == -1) {
			
			((AutonomousDriver)c.driver).decelTime = t;
		}
		
		if (((AutonomousDriver)c.driver).stoppedTime != -1) {
			return;
		}
		
		c.applyCancelingForwardImpulse(brakeForwardImpulseCoefficient);
		c.applyCancelingLateralImpulse(brakeLateralImpulseCoefficient);
		c.applyCancelingAngularImpulse(turnAngularImpulseCoefficient);
		
	}
	
}
