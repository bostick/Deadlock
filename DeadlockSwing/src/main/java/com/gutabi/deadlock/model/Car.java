package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;
import com.gutabi.deadlock.core.path.STPointPath;

public abstract class Car extends Entity implements RayCastCallback {
	
	public CarStateEnum state;
	
	public long startingTime;
	public long crashingTime;
	public Source source;
	
	GraphPositionPath overallPath;
	GraphPosition closestGraphPos;
	
	public final int id;
	
	public static int carCounter;
	
	Point startPos;
	double startHeading;
	
	private int b2dVertexCount = 0;
	private int b2dEdgeCount = 0;
	private boolean intersectedWithVertex;
	private boolean intersectedWithEdge;
	
	boolean aboveVertex;
	boolean aboveEdge;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.NEW;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
		startPos = overallPath.getGraphPosition(0).getPoint();
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(startPos);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
//		Point nextDTGoalPoint = overallPath.getEnd().getPoint();
		
//		logger.debug("nextDTGoalPoint: " + nextDTGoalPoint);
		
		startHeading = Math.atan2(nextDTGoalPoint.getY()-startPos.getY(), nextDTGoalPoint.getX()-startPos.getX());
		
		b2dInit();
		
	}
	
	/**
	 * meters per millisecond
	 * @return
	 */
	public abstract double getSpeed();
	
	public CarStateEnum getState() {
		return state;
	}
	
	public void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)startPos.getX(), (float)startPos.getY());
		bodyDef.angle = (float)startHeading;
		bodyDef.bullet = true;
		b2dBody = MODEL.world.b2dWorld.createBody(bodyDef);
		b2dBody.setUserData(this);
		
		b2dShape = new PolygonShape();
		((PolygonShape)b2dShape).setAsBox((float)(MODEL.world.CAR_LENGTH / 2), (float)(MODEL.world.CAR_LENGTH / 4));
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = b2dShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1f;
		b2dFixture = b2dBody.createFixture(fixtureDef);
	}
	
	public void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	public boolean hitTest(Point p) {
		return b2dShape.testPoint(b2dBody.getTransform(), new Vec2((float)p.getX(), (float)p.getY()));
	}
	
	public void incrementB2DEdgeCount() {
		b2dEdgeCount++;
	}
	
	public void decrementB2DEdgeCount() {
		b2dEdgeCount--;
	}
	
	public int getB2DEdgeCount() {
		return b2dEdgeCount;
	}
	
	public void incrementB2DVertexCount() {
		b2dVertexCount++;
	}
	
	public void decrementB2DVertexCount() {
		b2dVertexCount--;
	}
	
	public int getB2DVertexCount() {
		return b2dVertexCount;
	}
	
	public void crash() {
		
		state = CarStateEnum.CRASHED;
		
		b2dBody.setLinearDamping(2.0f);
		b2dBody.setAngularDamping(2.0f);
		
	}
	
	public void preStep() {
		
		logger.debug("car preStep");
		
		switch (state) {
		case NEW:
		case RUNNING: {
//			updateFriction();
			updateDrive();
			updateTurn();
		}
		case CRASHED:
			break;
		case SINKED:
			assert false;
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
		
		logger.debug("updateDrive");
		
		Vec2 curVec = b2dBody.getPosition();
		
		Point curPoint = new Point(curVec.x, curVec.y);
		
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(curPoint);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, 3.5f * (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		Vec2 currentRightNormal = b2dBody.getWorldVector(new Vec2(1, 0));
		Vec2 currentUpNormal = b2dBody.getWorldVector(new Vec2(0, 1));
		
		Vec2 vel = b2dBody.getLinearVelocity();
		
		float forwardSpeed = Vec2.dot(currentRightNormal, b2dBody.getLinearVelocity());
		logger.debug("forward speed: " + forwardSpeed);
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(b2dBody.getMass());
		
		float cancelingForwardImpulse = Vec2.dot(currentRightNormal, cancelingImpulse);
		cancelingForwardImpulse = 0.999f * cancelingForwardImpulse;
		
		float cancelingLateralImpulse = Vec2.dot(currentUpNormal, cancelingImpulse);
		cancelingLateralImpulse = 0.3f * cancelingLateralImpulse;
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
	    
		
		
		Vec2 dVec = new Vec2((float)(nextDTGoalPoint.getX()), (float)(nextDTGoalPoint.getY())).sub(curVec);
		
		Vec2 vVec = dVec.mul(1 / (((float)MODEL.world.dt) / ((float)1000)));
		
//		logger.debug("dvVec: " + dvVec.length());
		vVec = vVec.mul(((float)(getSpeed() * 1000)) / vVec.length());
				
//		Vec2 impulse = vVec.mul(b2dBody.getMass());
		float impulse = ((float)(getSpeed() * 1000)) * b2dBody.getMass();
		float force = impulse * (1 / (((float)MODEL.world.dt) / ((float)1000)));
		
//		Vec2 idealForce = impulse.mul(1 / (((float)MODEL.world.dt) / ((float)1000)));
		
//		float idealForwardForce = Vec2.dot(currentRightNormal, idealForce);
//		idealForwardForce = 1.0f * idealForwardForce;
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(impulse), b2dBody.getWorldCenter());
//		b2dBody.applyForce(currentRightNormal.mul(force), b2dBody.getWorldCenter());
		
//		if (idealForwardForce > 30) {
//			idealForwardForce = 30;
//		} else if (idealForwardForce < 0) {
//			idealForwardForce = 0;
//		}
		
//		logger.debug("ff: " + idealForwardForce);
		
//		Vec2 idealForwardForce = currentRightNormal.mul();
		
//		float idealLateralForce = Vec2.dot(currentUpNormal, idealForce);
		
//		logger.debug("idealLateralForce: " + idealLateralForce);
		
//		idealLateralForce = 0.0f * idealLateralForce;
		
//		b2dBody.applyForce(currentUpNormal.mul(idealLateralForce), b2dBody.getWorldCenter());
		
		
//		float dragForce = 0.0f * -1 * 5.0f;
//		
//		b2dBody.applyForce(currentRightNormal.mul(dragForce), b2dBody.getWorldCenter());
		
		logger.debug("done updateDrive");
		
	}
	
	private void updateTurn() {
		
		logger.debug("updateTurn");
		
		Vec2 curVec = b2dBody.getPosition();
		
		float curAngle = b2dBody.getAngle();
		
//		logger.debug("curAngle: " + curAngle);
		
		Point curPoint = new Point(curVec.x, curVec.y);
		
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(curPoint);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, 3.5f * (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		double nextDTGoalAngle = Math.atan2(nextDTGoalPoint.getY()-curVec.y, nextDTGoalPoint.getX()-curVec.x);
		
		float curAngVel = b2dBody.getAngularVelocity();
		float dw = ((float)nextDTGoalAngle) - curAngle;
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
//		dw = 1.0f * dw;
		
		// use value of 15 deg / 70 milliseconds to figure time-independent version
		// 0.214286 deg / millisecond
		
		double maxRadsPerMillisecond = 0.00373999;
		float maxRads = (float)(maxRadsPerMillisecond * MODEL.world.dt);
		
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
//		logger.debug("dw: " + dw);
		
		float cancelingAngImpulse = 0.999f * b2dBody.getInertia() * -curAngVel;
//		float cancelingTorque = cancelingAngimpulse / (((float)MODEL.world.dt) / ((float)1000));
		b2dBody.applyAngularImpulse(cancelingAngImpulse);
		
		float angImpulse = b2dBody.getInertia() * (dw / (((float)MODEL.world.dt) / ((float)1000)));
		float idealTorque = angImpulse / (((float)MODEL.world.dt) / ((float)1000));
		
		float actualTorque = idealTorque;
		actualTorque = 0.9999f * actualTorque;
		
//		logger.debug("actualTorque: " + actualTorque);
		
		b2dBody.applyAngularImpulse(angImpulse);
		//b2dBody.applyTorque(actualTorque);
		
		logger.debug("done updateTurn");
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep() {
		
		closestGraphPos = MODEL.world.graph.findClosestGraphPosition(getPoint(), null, Double.POSITIVE_INFINITY);
		
		if (state == CarStateEnum.RUNNING) {
			Vec2 pos = b2dBody.getPosition();
			Point end = overallPath.getEnd().getPoint();
			boolean sinked = false;
			if (Point.distance(new Point(pos.x,  pos.y), end) < MODEL.world.SINK_EPSILON) {
				sinked = true;
			}
			
			if (sinked) {
				state = CarStateEnum.SINKED;
				return false;
			}
		}
		
		return true;	
	}
	
	@Override
	public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
		
		intersectedWithVertex = false;
		intersectedWithEdge = false;
		
		Object userData = fixture.getBody().getUserData();
		
		if (userData instanceof Car) {
			
			logger.debug("intersected with car");
			
		} else if (userData instanceof Vertex) {
			
			logger.debug("intersected with vertex");
			
			intersectedWithVertex = true;
			
		} else if (userData instanceof Edge) {
			
			logger.debug("intersected with edge");
			
			intersectedWithEdge = true;
			
		} else {
			throw new AssertionError();
		}
		
		return 1;
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
		
		paintRect(g2);
//		paintImage(g2, image());
		paintPoint(g2);
		
	}
	
	public void paintHilite(Graphics2D g2) {
		
		paintRect(g2);
		
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
		
		g2.drawImage(im,
				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2),
				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
		
		g2.setTransform(origTransform);
	}
	
	private void paintRect(Graphics2D g2) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
		Vec2 pos = b2dBody.getPosition();
		float angle = b2dBody.getAngle();
		b2dTrans.translate(pos.x, pos.y);
		b2dTrans.rotate(angle);
		
		b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
		
		g2.setTransform(b2dTrans);
		
//		g2.setColor(color);
		if (b2dVertexCount > 0 || b2dEdgeCount > 0) {
			g2.setColor(Color.CYAN);
		} else if (intersectedWithEdge || intersectedWithVertex) {
			g2.setColor(Color.RED);
		} else {
			g2.setColor(Color.BLUE);
		}
		
		g2.fillRect(
				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2 - 1),
				(int)(-MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 4 - 1),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER + 2),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2 + 2));
		
		g2.setTransform(origTransform);
	}
	
	private void paintPoint(Graphics2D g2) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
		Vec2 pos = closestGraphPos.getPoint().vec2();
		b2dTrans.translate(pos.x, pos.y);
		
		b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
		
		g2.setTransform(b2dTrans);
		
		g2.setColor(Color.BLACK);
		
		g2.fillOval(-2, -2, 4, 4);
		
		g2.setTransform(origTransform);
	}
	
}
