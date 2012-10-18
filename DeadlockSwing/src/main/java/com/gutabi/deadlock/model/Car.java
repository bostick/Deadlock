package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;
import com.gutabi.deadlock.core.path.STPointPath;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
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
	
	Point p1;
	Point p2;
	Point p3;
	Point p4;
	protected Body b2dBody;
	protected PolygonShape b2dShape;
	protected Fixture b2dFixture;
	protected boolean b2dInited;
	
	boolean atleastPartiallyOnRoad;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.NEW;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
//		startPos = overallPath.getGraphPosition(0).getPoint();
		startPos = overallPath.getGraphPosition(0).getPoint();
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(startPos);
		
		p1 = new Point(MODEL.world.CAR_LENGTH / 2, MODEL.world.CAR_LENGTH / 4);
		p2 = new Point(MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 4);
		p3 = new Point(-MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 4);
		p4 = new Point(-MODEL.world.CAR_LENGTH / 2, MODEL.world.CAR_LENGTH / 4);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, (getSpeed() * 1000) * (((float)MODEL.world.dt) / ((float)1000)));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
//		Point nextDTGoalPoint = overallPath.getEnd().getPoint();
		
//		logger.debug("nextDTGoalPoint: " + nextDTGoalPoint);
		
		startHeading = Math.atan2(nextDTGoalPoint.y-startPos.y, nextDTGoalPoint.x-startPos.x);
		
		color = Color.BLUE;
		
		computeArea();
		
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
	
	private void computeArea() {
		
		path = new GeneralPath();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p4.x, p4.y);
		path.closePath();
		
	}
	
	public void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)startPos.x, (float)startPos.y);
		bodyDef.angle = (float)startHeading;
		bodyDef.bullet = true;
		b2dBody = MODEL.world.b2dWorld.createBody(bodyDef);
		b2dBody.setUserData(this);
		
		b2dShape = new PolygonShape();
		/*
		 * do not know exactly what magic I have to call in order to get intertia setup properly when using set
		 * 
		 * so stick with setAsBox for now
		 */
//		b2dShape.set(new Vec2[] {
//				new Vec2((float)p1.getX(), (float)p1.getY()),
//				new Vec2((float)p2.getX(), (float)p2.getY()),
//				new Vec2((float)p3.getX(), (float)p3.getY()),
//				new Vec2((float)p4.getX(), (float)p4.getY())}, 4);
		((PolygonShape)b2dShape).setAsBox((float)(MODEL.world.CAR_LENGTH / 2), (float)(MODEL.world.CAR_LENGTH / 4));
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = b2dShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1f;
		b2dFixture = b2dBody.createFixture(fixtureDef);
		
		logger.debug("mass: " + b2dBody.getMass());
		logger.debug("moment of intertia: " + b2dBody.getInertia());
		
	}
	
	public void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	public boolean hitTest(Point p) {
		return b2dShape.testPoint(b2dBody.getTransform(), new Vec2((float)p.x, (float)p.y));
	}
	
	public void crash() {
		
		state = CarStateEnum.CRASHED;
		
		b2dBody.setLinearDamping(2.0f);
		b2dBody.setAngularDamping(2.0f);
		
	}
	
	public void preStep() {
		
//		logger.debug("car preStep");
		
		AffineTransform b2dTrans = new AffineTransform();
		Vec2 pos = b2dBody.getPosition();
		float angle = b2dBody.getAngle();
		b2dTrans.translate(pos.x, pos.y);
		b2dTrans.rotate(angle);
		
		Entity e1 = MODEL.world.graph.hitTest(Point.point(b2dTrans.transform(p1.point2D(), null)));
		Entity e2 = MODEL.world.graph.hitTest(Point.point(b2dTrans.transform(p2.point2D(), null)));
		Entity e3 = MODEL.world.graph.hitTest(Point.point(b2dTrans.transform(p3.point2D(), null)));
		Entity e4 = MODEL.world.graph.hitTest(Point.point(b2dTrans.transform(p4.point2D(), null)));
		
		if (e1 == null && e2 == null && e3 == null && e4 == null) {
			atleastPartiallyOnRoad = false;
		} else {
			atleastPartiallyOnRoad = true;
		}
		
		switch (state) {
		case NEW: {
			
			state = CarStateEnum.RUNNING;
			
//			updateFriction();
			updateDrive();
			updateTurn();
			break;
		}
		case RUNNING: {
//			updateFriction();
			updateDrive();
			updateTurn();
			break;
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
		
//		logger.debug("updateDrive");
		
		Vec2 currentRightNormal = b2dBody.getWorldVector(new Vec2(1, 0));
		Vec2 currentUpNormal = b2dBody.getWorldVector(new Vec2(0, 1));
		
		Vec2 vel = b2dBody.getLinearVelocity();
		
		float forwardSpeed = Vec2.dot(currentRightNormal, b2dBody.getLinearVelocity());
		logger.debug("forward speed: " + forwardSpeed);
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(b2dBody.getMass());
		
		float cancelingForwardImpulse = Vec2.dot(currentRightNormal, cancelingImpulse);
		cancelingForwardImpulse = 1.0f * cancelingForwardImpulse;
		
		float cancelingLateralImpulse = Vec2.dot(currentUpNormal, cancelingImpulse);
		cancelingLateralImpulse = (atleastPartiallyOnRoad ? 0.3f : 0.08f) * cancelingLateralImpulse;
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
		
		float impulse = ((float)((atleastPartiallyOnRoad ? 1.0 : 0.4) * getSpeed() * 1000)) * b2dBody.getMass();
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(impulse), b2dBody.getWorldCenter());
		
//		logger.debug("done updateDrive");
		
	}
	
	private void updateTurn() {
		
//		logger.debug("updateTurn");
		
		Vec2 curVec = b2dBody.getPosition();
		
		float curAngle = b2dBody.getAngle();
		
//		logger.debug("curAngle: " + curAngle);
		
		Point curPoint = new Point(curVec.x, curVec.y);
		
		GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(curPoint);
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, 0.1f * (getSpeed() * 1000));
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		double nextDTGoalAngle = Math.atan2(nextDTGoalPoint.y-curVec.y, nextDTGoalPoint.x-curVec.x);
		
		float curAngVel = b2dBody.getAngularVelocity();
		
		logger.debug("angular speed: " + curAngVel);
		
		float dw = ((float)nextDTGoalAngle) - curAngle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		double maxRadsPerMillisecond = 0.005;
		float maxRads = (float)(maxRadsPerMillisecond * MODEL.world.dt);
		
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		logger.debug("dw: " + dw);
		
		float cancelingAngImpulse = 1.0f * b2dBody.getInertia() * -curAngVel;
		
		logger.debug("canceling angular impulse: " + cancelingAngImpulse);
		
		b2dBody.applyAngularImpulse(cancelingAngImpulse);
		
		float angImpulse = b2dBody.getInertia() * (dw / (((float)MODEL.world.dt) / ((float)1000)));
		
		logger.debug("angular impulse: " + angImpulse);
		
		b2dBody.applyAngularImpulse(angImpulse);
		
//		logger.debug("done updateTurn");
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep() {
		
		closestGraphPos = MODEL.world.graph.findClosestGraphPosition(getPoint(), null, Double.POSITIVE_INFINITY);
		
		switch (state) {
		case NEW:
			assert false;
		case RUNNING: {
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
			break;
		}
		case CRASHED:
			break;
		case SINKED:
			assert false;
		}
		
		return true;	
	}
	
	
	
	public int getId() {
		return id;
	}
	
	public Point getPoint() {
		Vec2 pos = b2dBody.getPosition();
		return new Point(pos.x, pos.y);
	}
	
	abstract BufferedImage image();
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		paintRect(g2);
		paintImage(g2, image());
		
		if (MODEL.DEBUG_DRAW) {
			paintPoint(g2);
		}
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		paintRect(g2);
	}
	
	private void paintImage(Graphics2D g2, BufferedImage im) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform b2dTrans = (AffineTransform)origTransform.clone();
		Vec2 pos = b2dBody.getPosition();
		float angle = b2dBody.getAngle();
		b2dTrans.translate(pos.x, pos.y);
		b2dTrans.rotate(angle);
		
		b2dTrans.scale(1/((double)MODEL.PIXELS_PER_METER), 1/((double)MODEL.PIXELS_PER_METER));
		
		g2.setTransform(b2dTrans);
		
		g2.drawImage(im,
				(int)(-MODEL.world.CAR_LENGTH * MODEL.PIXELS_PER_METER / 2),
				(int)(-MODEL.world.CAR_LENGTH * MODEL.PIXELS_PER_METER / 2),
				(int)(MODEL.world.CAR_LENGTH * MODEL.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.PIXELS_PER_METER), null);
		
		g2.setTransform(origTransform);
	}
	
	private void paintRect(Graphics2D g2) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform b2dTrans = (AffineTransform)origTransform.clone();
		Vec2 pos = b2dBody.getPosition();
		float angle = b2dBody.getAngle();
		b2dTrans.translate(pos.x, pos.y);
		b2dTrans.rotate(angle);
		
		g2.setTransform(b2dTrans);
		
		g2.setColor(color);
		
		g2.fill(path);
		
		g2.setTransform(origTransform);
	}
	
	private void paintPoint(Graphics2D g2) {
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform b2dTrans = (AffineTransform)origTransform.clone();
		Vec2 pos = closestGraphPos.getPoint().vec2();
		b2dTrans.translate(pos.x, pos.y);
		
		b2dTrans.scale(1/((double)MODEL.PIXELS_PER_METER), 1/((double)MODEL.PIXELS_PER_METER));
		
		g2.setTransform(b2dTrans);
		
		g2.setColor(Color.BLACK);
		
		g2.fillOval(-2, -2, 4, 4);
		
		g2.setTransform(origTransform);
	}
	
}
