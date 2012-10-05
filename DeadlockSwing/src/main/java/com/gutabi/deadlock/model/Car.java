package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;
import com.gutabi.deadlock.core.path.STPointPath;

public abstract class Car {
	
	public CarStateEnum state;
	
	public long startingTime;
	public long crashingTime;
	public Source source;
	
	GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	protected Body b2dBody;
	private Shape b2dShape;
	private Fixture b2dFixture;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.RUNNING;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
//		Point end = overallPath.getEnd().getPoint();
		Point startPos = overallPath.getGraphPosition(0).getPoint();
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(startPos);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
//		Point nextDTGoalPoint = overallPath.getEnd().getPoint();
		
//		logger.debug("nextDTGoalPoint: " + nextDTGoalPoint);
		
		double startHeading = Math.atan2(nextDTGoalPoint.getY()-startPos.getY(), nextDTGoalPoint.getX()-startPos.getX());
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC; // dynamic means it is subject to forces
		bodyDef.position.set((float)startPos.getX(), (float)startPos.getY());
		bodyDef.angle = (float)startHeading;
		bodyDef.bullet = true;
		b2dBody = MODEL.world.b2dWorld.createBody(bodyDef);
		b2dBody.setUserData(this);
		
		b2dShape = new PolygonShape();
		((PolygonShape)b2dShape).setAsBox((float)(MODEL.world.CAR_LENGTH / 2), (float)(MODEL.world.CAR_LENGTH / 4));
		
		FixtureDef fixtureDef = new FixtureDef(); // fixture def that we load up with the following info:
		fixtureDef.shape = b2dShape; // ... its shape is the dynamic box (2x2 rectangle)
		fixtureDef.density = 1.0f; // ... its density is 1 (default is zero)
		fixtureDef.friction = 1f; // ... its surface has some friction coefficient
		b2dFixture = b2dBody.createFixture(fixtureDef); // bind the dense, friction-laden fixture to the body
		
	}
	
	/**
	 * meters per millisecond
	 * @return
	 */
	public abstract double getSpeed();
	
	public void crash() {
		
		state = CarStateEnum.CRASHED;
		
		b2dBody.setLinearDamping(2.0f);
		b2dBody.setAngularDamping(2.0f);
		
	}
	
	public void preStep() {
		
		switch (state) {
		case RUNNING: {
//			updateFriction();
			updateDrive();
			updateTurn();
		}
		case CRASHED:
			break;
		}
	}
	
	private Vec2 getLateralVelocity() {
		Vec2 currentRightNormal = b2dBody.getWorldVector(new Vec2(0, 1));
		return currentRightNormal.mul(Vec2.dot(currentRightNormal, b2dBody.getLinearVelocity()));
	}
	
	private Vec2 getForwardVelocity() {
		Vec2 currentRightNormal = b2dBody.getWorldVector(new Vec2(1, 0));
		return currentRightNormal.mul(Vec2.dot(currentRightNormal, b2dBody.getLinearVelocity()));
	}
	
	private void updateFriction() {
		
		Vec2 lateralImpulse = getLateralVelocity().mul(-b2dBody.getMass());
		Vec2 forwardImpulse = getForwardVelocity().mul(-b2dBody.getMass());
		
//		float maxLateralImpulse = 3.0f;
//		if (impulse.length() > maxLateralImpulse) {
//			impulse = impulse.mul(maxLateralImpulse / impulse.length());
//		}
		
		b2dBody.applyLinearImpulse(lateralImpulse, b2dBody.getWorldCenter());
		b2dBody.applyLinearImpulse(forwardImpulse, b2dBody.getWorldCenter());
		
		b2dBody.applyAngularImpulse(1.0f * b2dBody.getInertia() * -b2dBody.getAngularVelocity());
		
//		Vec2 currentForwardNormal = getForwardVelocity();
//		float currentForwardSpeed = currentForwardNormal.normalize();
//		float dragForceMagnitude = -2 * currentForwardSpeed;
//		b2dBody.applyForce(currentForwardNormal.mul(dragForceMagnitude), b2dBody.getWorldCenter());
	}
	
	
	Vec2 maxIdealLinForce = null;
	float maxIdealTorque = 0;
	
	private void updateDrive() {
		
		Vec2 curVec = b2dBody.getPosition();
		
		Point curPoint = new Point(curVec.x, curVec.y);
		
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(curPoint);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		Vec2 vel = b2dBody.getLinearVelocity();
		Vec2 forwardVelocity = getForwardVelocity();
		Vec2 lateralVelocity = getLateralVelocity();
	    
		float dx = ((float)nextDTGoalPoint.getX()) - curVec.x;
		float dy = ((float)nextDTGoalPoint.getY()) - curVec.y;
		
		float dvx = (-vel.x + dx / (((float)MODEL.world.dt) / ((float)1000)));
		float dvy = (-vel.y + dy / (((float)MODEL.world.dt) / ((float)1000)));
		
		float dfvx = (-forwardVelocity.x + dx / (((float)MODEL.world.dt) / ((float)1000)));
		float dfvy = (-forwardVelocity.y + dy / (((float)MODEL.world.dt) / ((float)1000)));
		
		float dlvx = (-lateralVelocity.x + dx / (((float)MODEL.world.dt) / ((float)1000)));
		float dlvy = (-lateralVelocity.y + dy / (((float)MODEL.world.dt) / ((float)1000)));
		
		
		float impulseX = b2dBody.getMass() * dvx;
		float impulseY = b2dBody.getMass() * dvy;
	    
		float forwardImpulseX = b2dBody.getMass() * dfvx;
		float forwardImpulseY = b2dBody.getMass() * dfvy;		
		
		float lateralImpulseX = b2dBody.getMass() * dlvx;
		float lateralImpulseY = b2dBody.getMass() * dlvy;
		
		Vec2 idealForce = new Vec2(impulseX / (((float)MODEL.world.dt) / ((float)1000)), impulseY / (((float)MODEL.world.dt) / ((float)1000)));
		
		Vec2 idealForwardForce = new Vec2(forwardImpulseX / (((float)MODEL.world.dt) / ((float)1000)), forwardImpulseY / (((float)MODEL.world.dt) / ((float)1000)));
		Vec2 idealLateralForce = new Vec2(lateralImpulseX / (((float)MODEL.world.dt) / ((float)1000)), lateralImpulseY / (((float)MODEL.world.dt) / ((float)1000)));
		
//		b2dBody.applyForce(idealForwardForce, b2dBody.getWorldCenter());
//		b2dBody.applyForce(idealLateralForce, b2dBody.getWorldCenter());
		b2dBody.applyForce(idealForce, b2dBody.getWorldCenter());
		
	}
	
	private void updateTurn() {
		Vec2 curVec = b2dBody.getPosition();
		
		float curAngle = b2dBody.getAngle();
		
		logger.debug("curAngle: " + curAngle);
		
		Point curPoint = new Point(curVec.x, curVec.y);
		
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(curPoint);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		double nextDTGoalAngle = Math.atan2(nextDTGoalPoint.getY()-curVec.y, nextDTGoalPoint.getX()-curVec.x);
		
		/*
		 * apply single impulses for velocities
		 */
		
		float curAngVel = b2dBody.getAngularVelocity();
		float dw = ((float)nextDTGoalAngle) - curAngle;
		
		float angImpulse = b2dBody.getInertia() * (-curAngVel + dw / (((float)MODEL.world.dt) / ((float)1000)));
		
		float idealTorque = angImpulse / (((float)MODEL.world.dt) / ((float)1000));
		
		float actualTorque = idealTorque;
		
		logger.debug("actualTorque: " + actualTorque);
		
		b2dBody.applyTorque(actualTorque);
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep() {
		
		if (state == CarStateEnum.RUNNING) {
			Vec2 pos = b2dBody.getPosition();
			Point end = overallPath.getEnd().getPoint();
			boolean sinked = false;
			if (Point.distance(new Point(pos.x,  pos.y), end) < MODEL.world.CAR_LENGTH) {
				sinked = true;
			}
			
			if (sinked) {
				
				b2dCleanUp();
				
				return false;
			}
		}
		
		return true;
		
	}
	
	public void b2dCleanUp() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	public int getId() {
		return id;
	}
	
	public Point getPoint() {
		Vec2 pos = b2dBody.getPosition();
		return new Point(pos.x, pos.y);
	}
	
	abstract BufferedImage image();
	
	public void paint(Graphics2D g2) {
		
		switch (state) {
		case RUNNING:
			paintImage(g2, image());
			break;
		case CRASHED:
			paintImage(g2, image());
			break;
		}
		
	}
	
	private void paintImage(Graphics2D g2, BufferedImage im) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
		Vec2 pos = b2dBody.getPosition();
		float angle = b2dBody.getAngle();
		b2dTrans.translate(pos.x, pos.y);
		b2dTrans.rotate(angle);
		
		b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
		
		g2.setTransform(b2dTrans);
		
//		g2.setColor(Color.BLUE);
//		
//		g2.fillRect(
//				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2),
//				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 4),
//				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
//				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2));
		
		g2.drawImage(im,
				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2),
				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
		
		g2.setTransform(origTransform);
	}
	
}
