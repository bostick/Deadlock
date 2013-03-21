package com.gutabi.deadlock.world.cars;

import org.jbox2d.common.Vec2;

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
		
		double dv;
		if (maxSpeed > c.forwardSpeed) {
			dv = maxSpeed - c.forwardSpeed;
		} else {
			dv = 0.0f;
		}
		if (dv < 0) {
			assert false;
		}
		if (dv > maxAcceleration * world.screen.DT) {
			dv = maxAcceleration * world.screen.DT;
		}
		
		float forwardImpulse = (float)(driveForwardImpulseCoefficient * c.mass * dv);
		
		c.b2dBody.applyLinearImpulse(c.currentRightNormal.mul(forwardImpulse), c.b2dBody.getWorldCenter());
		
		Vec2 cancelingImpulse = c.vel.mul(-1).mul(c.mass);
		float driveLateralImpulse = (float)(driveLateralImpulseCoefficient * Vec2.dot(c.currentUpNormal, cancelingImpulse));
		
		c.b2dBody.applyLinearImpulse(c.currentUpNormal.mul(driveLateralImpulse), c.b2dBody.getWorldCenter());
		
		
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
		
		double actualDistance = Math.abs(c.forwardSpeed * world.screen.DT);
		double maxRads = maxRadsPerMeter * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		float goalAngVel = (float)(dw / world.screen.DT);
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * c.momentOfInertia * (goalAngVel - c.angularVel));
		
		c.b2dBody.applyAngularImpulse(angImpulse);
		
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
		if (dv > maxAcceleration * world.screen.DT) {
			dv = maxAcceleration * world.screen.DT;
		}
		
		float backwardImpulse = (float)(driveForwardImpulseCoefficient * c.mass * dv);
		
		c.b2dBody.applyLinearImpulse(c.currentRightNormal.mul(-backwardImpulse), c.b2dBody.getWorldCenter());
		
		Vec2 cancelingImpulse = c.vel.mul(-1).mul(c.mass);
		float driveLateralImpulse = (float)(driveLateralImpulseCoefficient * Vec2.dot(c.currentUpNormal, cancelingImpulse));
		
		c.b2dBody.applyLinearImpulse(c.currentUpNormal.mul(driveLateralImpulse), c.b2dBody.getWorldCenter());
		
		
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
		
		double actualDistance = Math.abs(backwardSpeed * world.screen.DT);
		double maxRads = maxRadsPerMeter * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		float goalAngVel = (float)(dw / world.screen.DT);
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * c.momentOfInertia * (goalAngVel - c.angularVel));
		
		c.b2dBody.applyAngularImpulse(angImpulse);
		
	}
	
	private void updateBrake(double t) {
		
		if (((AutonomousDriver)c.driver).decelTime == -1) {
			
			((AutonomousDriver)c.driver).decelTime = t;
		}
		
		if (((AutonomousDriver)c.driver).stoppedTime != -1) {
			return;
		}
		
		Vec2 cancelingVel = c.vel.mul(-1);
		double cancelingRightVel = Vec2.dot(cancelingVel, c.currentRightNormal);
		double cancelingUpVel = Vec2.dot(cancelingVel, c.currentUpNormal);
		
		float rightBrakeImpulse = (float)(brakeForwardImpulseCoefficient * cancelingRightVel * c.mass);
		float upBrakeImpulse = (float)(brakeLateralImpulseCoefficient * cancelingUpVel * c.mass);
		
		c.b2dBody.applyLinearImpulse(c.currentRightNormal.mul(rightBrakeImpulse), c.pVec2);
		c.b2dBody.applyLinearImpulse(c.currentUpNormal.mul(upBrakeImpulse), c.pVec2);
		
		
		double goalAngVel = 0.0;
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * c.momentOfInertia * (goalAngVel - c.angularVel));
		
		c.b2dBody.applyAngularImpulse(angImpulse);
	}
	
}
