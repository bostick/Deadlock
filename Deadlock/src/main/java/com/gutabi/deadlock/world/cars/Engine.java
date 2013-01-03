package com.gutabi.deadlock.world.cars;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.World;

public class Engine {
	
	World world;
	Car c;
	
	public Engine(World world, Car c) {
		this.world = world;
		this.c = c;
	}
	
	public void preStep(double t) {
		
		updateFriction();
		
		switch (c.state) {
		case DRIVING:
			updateDrive(t);
			break;
		case BRAKING:
			updateBrake(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
			break;
		case EDITING:
			break;
		}
		
	}
	
	
	
//	double steeringLookaheadDistance = CAR_LENGTH * 1.5;
//	double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.4;
//	double vertexArrivalLookahead = CAR_LENGTH * 0.5 + CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.4;
//	/*
//	 * turning radius
//	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
//	 */
//	private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
//	private double maxAcceleration = getMaxSpeed() / 3.0;
//	private double maxDeceleration = -getMaxSpeed() / 3.0;
//	private double frictionForwardImpulseCoefficient = 0.0;
//	private double frictionLateralImpulseCoefficient = 0.0;
//	private double frictionAngularImpulseCoefficient = 0.0;
//	private double driveForwardImpulseCoefficient = 1.0;
//	private double driveLateralImpulseCoefficient = 1.0;
//	private double brakeForwardImpulseCoefficient = 1.0;
//	private double brakeLateralImpulseCoefficient = 1.0;
//	private double turnAngularImpulseCoefficient = 1.0;
	
	
	
	
//	double steeringLookaheadDistance = CAR_LENGTH * 0.5;
//	double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.8 * CAR_LENGTH;
//	double vertexArrivalLookahead = CAR_LENGTH * 0.5;
//	/*
//	 * turning radius
//	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
//	 */
//	private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
//	private double maxAcceleration = Double.POSITIVE_INFINITY;
//	private double maxDeceleration = Double.NEGATIVE_INFINITY;
//	private double frictionForwardImpulseCoefficient = 0.0;
//	private double frictionLateralImpulseCoefficient = 0.0;
//	private double frictionAngularImpulseCoefficient = 0.0;
//	private double driveForwardImpulseCoefficient = 1.0;
//	private double driveLateralImpulseCoefficient = 1.0;
//	private double brakeForwardImpulseCoefficient = 1.0;
//	private double brakeLateralImpulseCoefficient = 1.0;
//	private double turnAngularImpulseCoefficient = 1.0;
	
	/*
	 * turning radius
	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
	 */
	private double maxRadsPerMeter = 1.0;
	private double maxAcceleration = Double.POSITIVE_INFINITY;
//	private double maxDeceleration = Double.NEGATIVE_INFINITY;
	private double frictionForwardImpulseCoefficient = 0.01;
	private double frictionLateralImpulseCoefficient = 0.04;
	private double frictionAngularImpulseCoefficient = 0.02;
	private double driveForwardImpulseCoefficient = 0.02;
	private double driveLateralImpulseCoefficient = 1.0;
	private double brakeForwardImpulseCoefficient = 0.05;
	private double brakeLateralImpulseCoefficient = 1.0;
	private double turnAngularImpulseCoefficient = 1.0;
	
	private void updateFriction() {
		
		Vec2 cancelingImpulse = c.vel.mul(-1).mul(c.mass);
		
		if (cancelingImpulse.lengthSquared() == 0.0f) {
			return;
		}
		
		float cancelingForwardImpulse = (float)(frictionForwardImpulseCoefficient * Vec2.dot(c.currentRightNormal, cancelingImpulse));
		float cancelingLateralImpulse = (float)(frictionLateralImpulseCoefficient * Vec2.dot(c.currentUpNormal, cancelingImpulse));
		
		c.b2dBody.applyLinearImpulse(c.currentRightNormal.mul(cancelingForwardImpulse), c.b2dBody.getWorldCenter());
		c.b2dBody.applyLinearImpulse(c.currentUpNormal.mul(cancelingLateralImpulse), c.b2dBody.getWorldCenter());
		
		float cancelingAngImpulse = (float)(frictionAngularImpulseCoefficient * c.momentOfInertia * -c.angularVel);
		
		c.b2dBody.applyAngularImpulse(cancelingAngImpulse);
	}
	
	private void updateDrive(double t) {
		
//		double goalForwardVel = (float)c.getMaxSp;
		
		double dv;
		if (c.maxSpeed > c.forwardSpeed) {
			dv = c.maxSpeed - c.forwardSpeed;
		} else {
			dv = 0.0f;
		}
		if (dv < 0) {
			assert false;
		}
		if (dv > maxAcceleration * world.screen.DT) {
			dv = maxAcceleration * world.screen.DT;
		}
		
//		logger.debug("acc for driving: " + acc);
		
		float forwardImpulse = (float)(driveForwardImpulseCoefficient * c.mass * dv);
		
		c.b2dBody.applyLinearImpulse(c.currentRightNormal.mul(forwardImpulse), c.b2dBody.getWorldCenter());
		
		Vec2 cancelingImpulse = c.vel.mul(-1).mul(c.mass);
		float driveLateralImpulse = (float)(driveLateralImpulseCoefficient * Vec2.dot(c.currentUpNormal, cancelingImpulse));
		
		c.b2dBody.applyLinearImpulse(c.currentUpNormal.mul(driveLateralImpulse), c.b2dBody.getWorldCenter());
		
		
		
		Point dp = new Point(c.driver.goalPoint.x-c.p.x, c.driver.goalPoint.y-c.p.y);
		
		double goalAngle = Math.atan2(dp.y, dp.x);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("updateTurn: goalAngle: " + goalAngle);
//		}
		
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
		
		double actualDistance = Math.abs(c.forwardSpeed * world.screen.DT);
		double maxRads = maxRadsPerMeter * actualDistance;
//		double negMaxRads = -maxRads;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
//		if (dw > 0.52) {
//			String.class.getName();
//		} else if (dw < -0.52) {
//			String.class.getName();
//		}
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("updateTurn: dw: " + dw);
//		}
		
		float goalAngVel = (float)(dw / world.screen.DT);
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * c.momentOfInertia * (goalAngVel - c.angularVel));
		
		c.b2dBody.applyAngularImpulse(angImpulse);
		
	}
	
	private void updateBrake(double t) {
		
		if (c.driver.decelTime == -1) {
			
//			logger.debug("decel");
			
			c.driver.decelTime = t;
		}
		
		if (c.driver.stoppedTime != -1) {
			return;
		}
		
		//logger.debug("acc for braking: " + -forwardVel.length());
		
		Vec2 cancelingVel = c.vel.mul(-1);
		double cancelingRightVel = Vec2.dot(cancelingVel, c.currentRightNormal);
		double cancelingUpVel = Vec2.dot(cancelingVel, c.currentUpNormal);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("braking dv: " + dv);
//		}
		
		float rightBrakeImpulse = (float)(brakeForwardImpulseCoefficient * cancelingRightVel * c.mass);
		float upBrakeImpulse = (float)(brakeLateralImpulseCoefficient * cancelingUpVel * c.mass);
		
		c.b2dBody.applyLinearImpulse(c.currentRightNormal.mul(rightBrakeImpulse), c.pVec2);
		c.b2dBody.applyLinearImpulse(c.currentUpNormal.mul(upBrakeImpulse), c.pVec2);
		
		
		double goalAngVel = 0.0;
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * c.momentOfInertia * (goalAngVel - c.angularVel));
		
		c.b2dBody.applyAngularImpulse(angImpulse);
	}
	
}
