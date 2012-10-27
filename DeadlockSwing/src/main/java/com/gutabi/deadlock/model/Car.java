package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public Source source;
	
	protected GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	Point startPoint;
	double startHeading;
	
	Point p1;
	Point p2;
	Point p3;
	Point p4;
	protected Body b2dBody;
	protected PolygonShape b2dShape;
	protected Fixture b2dFixture;
	protected boolean b2dInited;
	
	/*
	 * trans is updated from b2dBody transformation after every update
	 * we use this since we can actually use it to do transforms
	 */
	Point p;
	Vec2 currentRightNormal;
	Vec2 currentUpNormal;
	Vec2 vel;
	Vec2 forwardVel;
	float angle;
	float angularVel;
	AffineTransform carTrans;
	Point aabbLoc;
	Dim aabbDim;
	boolean atleastPartiallyOnRoad;
	boolean completelyOnRoad;
	GraphPositionPathPosition overallPos;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		super();
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.NEW;
		
		source = s;
		
		color = Color.BLUE;
		hiliteColor = Color.BLUE;
		
		p1 = new Point(CAR_LENGTH / 2, CAR_LENGTH / 4);
		p2 = new Point(CAR_LENGTH / 2, -CAR_LENGTH / 4);
		p3 = new Point(-CAR_LENGTH / 2, -CAR_LENGTH / 4);
		p4 = new Point(-CAR_LENGTH / 2, CAR_LENGTH / 4);
		
		carTrans = new AffineTransform();
		aabb = new AABB();
		
		computePath();
		
	}
	
	protected void computeStartingProperties() {
		
		overallPos = new GraphPositionPathPosition(overallPath, 0, 0.0);
		GraphPosition closestGraphPos = overallPos.gpos;
		startPoint = closestGraphPos.p;
		
		GraphPositionPathPosition next = overallPos.travel(getMetersPerSecond() * MODEL.dt);
		
		Point nextDTGoalPoint = next.gpos.p;
		
		startHeading = Math.atan2(nextDTGoalPoint.y-startPoint.y, nextDTGoalPoint.x-startPoint.x);
		
		b2dInit();
		
		computeDynamicProperties();
	}
	
	/**
	 * meters per millisecond
	 * @return
	 */
	public abstract double getMetersPerSecond();
	
	public void computePath() {
		
		path = new GeneralPath();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p4.x, p4.y);
		path.closePath();
		
	}
	
	private void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)startPoint.x, (float)startPoint.y);
		bodyDef.angle = (float)startHeading;
		bodyDef.allowSleep = true;
		bodyDef.awake = true;
		
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
		((PolygonShape)b2dShape).setAsBox((float)(CAR_LENGTH / 2), (float)(CAR_LENGTH / 4));
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = b2dShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1f;
		b2dFixture = b2dBody.createFixture(fixtureDef);
		
	}
	
	
	AABB aabb = new AABB();
	
	Vec2 right = new Vec2(1, 0);
	Vec2 up = new Vec2(0, 1);
	
	private void computeDynamicProperties() {
		
		Vec2 curVec = b2dBody.getPosition();
		p = new Point(curVec.x, curVec.y);
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		vel = b2dBody.getLinearVelocity();
		angle = b2dBody.getAngle();
		angularVel = b2dBody.getAngularVelocity();
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		
		Entity e1 = MODEL.world.graphHitTest(carToWorld(p1));
		Entity e2 = MODEL.world.graphHitTest(carToWorld(p2));
		Entity e3 = MODEL.world.graphHitTest(carToWorld(p3));
		Entity e4 = MODEL.world.graphHitTest(carToWorld(p4));
		
		if (e1 == null && e2 == null && e3 == null && e4 == null) {
			atleastPartiallyOnRoad = false;
			completelyOnRoad = false;
		} else {
			atleastPartiallyOnRoad = true;
			if (e1 != null && e2 != null && e3 != null && e4 != null) {
				completelyOnRoad = true;
			} else {
				completelyOnRoad = false;
			}
		}
		
		Mat22 r = b2dBody.getTransform().R;
		carTrans.setTransform(r.col1.x, r.col1.y, r.col2.x, r.col2.y, p.x, p.y);
		
		b2dShape.computeAABB(aabb, b2dBody.getTransform());
		aabbLoc = new Point(aabb.lowerBound.x, aabb.lowerBound.y);
		aabbDim = new Dim(aabb.upperBound.x-aabb.lowerBound.x, aabb.upperBound.y-aabb.lowerBound.y);
		
		double actualDistance = forwardVel.length() * MODEL.dt;
		
		GraphPositionPathPosition max = overallPos.travel(Math.min(2 * actualDistance, overallPos.lengthToEndOfPath));
		overallPos = overallPath.findClosestGraphPositionPathPosition(p, overallPos, max);
		
	}
	
	public void destroy() {
		b2dCleanup();
	}
	
	private void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	
	
	public boolean hitTest(Point p) {
//		if () {
//			aabb.
//		}
		return hitTest(p, 0.0);
	}
	
	public boolean hitTest(Point p, double radius) {
		if (b2dShape.testPoint(b2dBody.getTransform(), new Vec2((float)p.x, (float)p.y))) {
			return true;
		}
		if (DMath.lessThanEquals(Point.distance(p, p1, p2), radius)) {
			return true;
		}
		if (DMath.lessThanEquals(Point.distance(p, p2, p3), radius)) {
			return true;
		}
		if (DMath.lessThanEquals(Point.distance(p, p3, p4), radius)) {
			return true;
		}
		if (DMath.lessThanEquals(Point.distance(p, p4, p1), radius)) {
			return true;
		}
		return false;
	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	public void crash() {
		
		state = CarStateEnum.CRASHED;
		
//		b2dBody.setLinearDamping(2.0f);
//		b2dBody.setAngularDamping(2.0f);
		
		source.outstandingCars--;
	}
	
	private Point carToWorld(Point p) {
		return Point.point(carTrans.transform(p.point2D(), null));
	}
	
	public void preStep(double t) {
		
		switch (state) {
		case NEW: {
			
			state = CarStateEnum.RUNNING;
			
			updateFriction();
			
			updateDrive();
			updateTurn();
			
			break;
		}
		case RUNNING: {
			
			updateFriction();
			
			updateDrive();
			updateTurn();
			
			break;
		}
		case CRASHED:
			
			if (b2dBody.isAwake()) {
				updateFriction();
			} else {
//				updateFriction();
			}
			
			break;
		case SINKED:
			assert false;
		}
	}
	
	private float cancelingForwardImpulseCoefficient() {
		return 0.05f;
	}
	
	private float cancelingLateralImpulseCoefficient() {
//		return atleastPartiallyOnRoad ? 0.1f : 0.04f;
		return 0.05f;
	}
	
	private float cancelingAngularImpulseCoefficient() {
		return 0.05f;
	}
	
	private float forwardImpulseCoefficient() {
		return atleastPartiallyOnRoad ? 1.0f : 0.4f;
	}
	
	/*
	 * turning radius
	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
	 */
	private double maxRadsPerMeter() {
		return 0.04;
	}
	
	private void updateFriction() {
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(b2dBody.getMass());
		Vec2 cancelingForce = vel.mul(-1).mul(b2dBody.getMass()).mul((float)(1/MODEL.dt));
		
		float cancelingForwardImpulse = cancelingForwardImpulseCoefficient() * Vec2.dot(currentRightNormal, cancelingImpulse);
		float cancelingLateralImpulse = cancelingLateralImpulseCoefficient() * Vec2.dot(currentUpNormal, cancelingImpulse);
		
		float cancelingForwardForce = cancelingForwardImpulseCoefficient() * Vec2.dot(currentRightNormal, cancelingForce);
		float cancelingLateralForce = cancelingLateralImpulseCoefficient() * Vec2.dot(currentUpNormal, cancelingForce);
		
//		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
//		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
		
		b2dBody.applyForce(currentRightNormal.mul(cancelingForwardForce), b2dBody.getWorldCenter());
		b2dBody.applyForce(currentUpNormal.mul(cancelingLateralForce), b2dBody.getWorldCenter());
		
		float cancelingAngImpulse = cancelingAngularImpulseCoefficient() * b2dBody.getInertia() * -angularVel;
		float cancelingAngForce = cancelingAngularImpulseCoefficient() * b2dBody.getInertia() * -angularVel * (float)(1/MODEL.dt);
		
		//b2dBody.applyAngularImpulse(cancelingAngImpulse);
		b2dBody.applyTorque(cancelingAngForce);
	}
	
	private void updateDrive() {
		
		float forwardSpeed = forwardVel.length();
		
		float goalForwardVel = (float)getMetersPerSecond();
		float acc = goalForwardVel - forwardSpeed;
		
		float forwardImpulse = forwardImpulseCoefficient() * b2dBody.getMass() * acc;
		float forwardForce = forwardImpulseCoefficient() * b2dBody.getMass() * acc * (float)(1/MODEL.dt);
		
		//b2dBody.applyLinearImpulse(currentRightNormal.mul(forwardImpulse), b2dBody.getWorldCenter());
		b2dBody.applyForce(currentRightNormal.mul(forwardForce), b2dBody.getWorldCenter());
		
	}
	
	private void updateTurn() {
		
		double lookaheadDistance = 1.5;
		double actualDistance = forwardVel.length() * MODEL.dt;
		
		GraphPositionPathPosition next = overallPos.travel(Math.min(lookaheadDistance, overallPos.lengthToEndOfPath));
		Point nextDTGoalPoint = next.gpos.p;
		
		double nextDTGoalAngle = Math.atan2(nextDTGoalPoint.y-p.y, nextDTGoalPoint.x-p.x);
		
		double dw = ((float)nextDTGoalAngle) - angle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		double maxRads = maxRadsPerMeter() * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		float goalAngVel = (float)(dw / MODEL.dt);
		
		float angImpulse = b2dBody.getInertia() * goalAngVel;
		float angForce = b2dBody.getInertia() * goalAngVel * (float)(1/MODEL.dt);
		
//		b2dBody.applyAngularImpulse(angImpulse);
		b2dBody.applyTorque(angForce);
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep() {
		
		switch (state) {
		case NEW:
			assert false;
		case RUNNING: {
			
			computeDynamicProperties();
			
			Sink s = (Sink)overallPath.end.getEntity();
			boolean sinked = false;
			if (Point.distance(p, s.p) < MODEL.world.SINK_EPSILON) {
				sinked = true;
			}
			if (sinked) {
				s.matchingSource.outstandingCars--;
				state = CarStateEnum.SINKED;
				return false;
			}
			break;
		}
		case CRASHED:
			
			if (b2dBody.isAwake()) {
				computeDynamicProperties();
			}
			
			break;
		case SINKED:
			assert false;
		}
		
		return true;	
	}
	
	
	
	public double distanceTo(Point p) {
		double d1 = Point.distance(p, carToWorld(p1));
		double d2 = Point.distance(p, carToWorld(p2));
		double d3 = Point.distance(p, carToWorld(p3));
		double d4 = Point.distance(p, carToWorld(p4));
		return Math.min(Math.min(d1, d2), Math.min(d3, d4));
	}
	
	abstract BufferedImage image();
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.transform(carTrans);
		
//		paintRect(g2);
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		paintImage(g2, image());
		
		if (MODEL.DEBUG_DRAW) {
			
			g2.setTransform(origTransform);
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			paintAABB(g2);
		}
		
		g2.setTransform(origTransform);
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
//		AffineTransform trans = (AffineTransform)origTransform.clone();
		
//		trans.concatenate(carTrans);
		
//		g2.setTransform(trans);
		
		g2.transform(carTrans);
		
		paintRect(g2);
		
		g2.setTransform(origTransform);
	}
	
	private void paintImage(Graphics2D g2, BufferedImage im) {
		
		g2.drawImage(im,
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER / 2),
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER / 2),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER), null);
	}
	
	private void paintRect(Graphics2D g2) {
		
//		if (completelyOnRoad) {
//			g2.setColor(Color.GREEN);
//		} else if (atleastPartiallyOnRoad) {
//			g2.setColor(Color.YELLOW);
//		} else {
//			g2.setColor(Color.RED);
//		}
		
		g2.setColor(hiliteColor);
		
		g2.fill(path);
		
	}
	
	private void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabbLoc.x * MODEL.PIXELS_PER_METER),
				(int)(aabbLoc.y * MODEL.PIXELS_PER_METER),
				(int)(aabbDim.width * MODEL.PIXELS_PER_METER),
				(int)(aabbDim.height * MODEL.PIXELS_PER_METER));
		
	}
	
}
