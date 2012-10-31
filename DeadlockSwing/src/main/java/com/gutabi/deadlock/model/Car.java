package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

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
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.Sink;
import com.gutabi.deadlock.core.graph.Source;
import com.gutabi.deadlock.core.graph.StopSign;
import com.gutabi.deadlock.core.graph.VertexPosition;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	public static final double COMPLETE_STOP_WAIT_TIME = 0.0;
	double steeringLookaheadDistance = CAR_LENGTH * 1.5;
//	double eventLookaheadDistance = CAR_LENGTH * 3;
	double eventLookaheadDistance = getMetersPerSecond() * 0.2;
	
	
	protected int sheetRow;
	protected int sheetCol;
	
	public CarStateEnum state;
	
	private Path2D path;
	
	public double startingTime;
	public double crashingTime;
	
	public Source source;
	
	protected GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	Point startPoint;
	double startHeading;
	float mass;
	float momentOfInertia;
	
	Point p1;
	Point p2;
	Point p3;
	Point p4;
	protected Body b2dBody;
	protected PolygonShape b2dShape;
	protected Fixture b2dFixture;
	protected boolean b2dInited;
	
	/*
	 * dynamic properties
	 */
	Point p;
	Vec2 pVec2;
	Vec2 currentRightNormal;
	Vec2 currentUpNormal;
	Vec2 vel;
	Vec2 forwardVel;
	float angle;
	float angularVel;
	AffineTransform carTrans;
	boolean atleastPartiallyOnRoad;
	boolean completelyOnRoad;
	GraphPositionPathPosition overallPos;
	Point worldPoint1;
	Point worldPoint2;
	Point worldPoint3;
	Point worldPoint4;
	
	/*
	 * 
	 */
	StopSign curEvent;
	GraphPositionPathPosition curEventPosition;
	GraphPositionPathPosition curEventMatchingPosition;
	List<GraphPositionPathPosition> oldEventPositions = new ArrayList<GraphPositionPathPosition>();
	double decelTime = -1;
	double accelTime = -1;
	double stoppedTime = -1;
	Point goalPoint;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.DRIVING;
		
		source = s;
		
		color = Color.BLUE;
		hiliteColor = Color.BLUE;
		
		p1 = new Point(CAR_LENGTH / 2, CAR_LENGTH / 4);
		p2 = new Point(CAR_LENGTH / 2, -CAR_LENGTH / 4);
		p3 = new Point(-CAR_LENGTH / 2, -CAR_LENGTH / 4);
		p4 = new Point(-CAR_LENGTH / 2, CAR_LENGTH / 4);
		
		carTrans = new AffineTransform();
		
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
		fixtureDef.friction = 0.3f;
		b2dFixture = b2dBody.createFixture(fixtureDef);
		
		mass = b2dBody.getMass();
		momentOfInertia = b2dBody.getInertia();
		
	}
	
	
	Vec2 right = new Vec2(1, 0);
	Vec2 up = new Vec2(0, 1);
	
	private void computeDynamicProperties() {
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		vel = b2dBody.getLinearVelocity();
		angle = b2dBody.getAngle();
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		if (DMath.equals(vel.length(), 0.0)) {
			String.class.getName();
		}
		
		worldPoint1 = carToWorld(p1);
		worldPoint2 = carToWorld(p2);
		worldPoint3 = carToWorld(p3);
		worldPoint4 = carToWorld(p4);
		
		Entity e1 = MODEL.world.graphHitTest(worldPoint1);
		Entity e2 = MODEL.world.graphHitTest(worldPoint2);
		Entity e3 = MODEL.world.graphHitTest(worldPoint3);
		Entity e4 = MODEL.world.graphHitTest(worldPoint4);
		
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
		
		computeAABB();
		
		GraphPositionPathPosition max = overallPos.travel(Math.min(2 * CAR_LENGTH, overallPos.lengthToEndOfPath));
		overallPos = overallPath.findClosestGraphPositionPathPosition(p, overallPos, max);
		
	}
	
	
	AABB b2dAABB = new AABB();
	
	protected void computeAABB() {
		b2dShape.computeAABB(b2dAABB, b2dBody.getTransform());
		aabb = new Rect(b2dAABB.lowerBound.x, b2dAABB.lowerBound.y, b2dAABB.upperBound.x-b2dAABB.lowerBound.x, b2dAABB.upperBound.y-b2dAABB.lowerBound.y);
	}
	
	private Point carToWorld(Point p) {
		return Point.point(carTrans.transform(p.point2D(), null));
	}
	
	
	
	
	public void destroy() {
		b2dCleanup();
	}
	
	private void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	
	
	
	public Point worldPoint1() {
		return worldPoint1;
	}
	public Point worldPoint2() {
		return worldPoint2;
	}
	public Point worldPoint3() {
		return worldPoint3;
	}
	public Point worldPoint4() {
		return worldPoint4;
	}
	
	public boolean hitTest(Point p, double radius) {
		if (b2dShape.testPoint(b2dBody.getTransform(), p.vec2())) {
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
		
		source.outstandingCars--;
	}
	
	public void preStep(double t) {
		
		if (state == CarStateEnum.CRASHED) {
			
		} else {
			
			if (curEvent == null) {
				List<GraphPositionPathPosition> eventPositions = overallPath.eventPositions(overallPos, Math.min(eventLookaheadDistance, overallPos.lengthToEndOfPath));
				/*
				 * since we deal with events before actually reaching them (front of car reaches them vs center of car where position is counted),
				 * simply keep a list of already processed events to use to filter
				 */
				eventPositions.removeAll(oldEventPositions);
				
				if (!eventPositions.isEmpty()) {
					
//					logger.debug("setting curEvent: " + events + "     " + oldEvents);
					
					curEventPosition = eventPositions.get(0);
					GraphPositionPathPosition n = curEventPosition.nextBound();
					assert n.gpos instanceof VertexPosition;
					curEventMatchingPosition = n.nextBound().travel(CAR_LENGTH / 2);
					curEvent = curEventPosition.gpos.getEvents().get(0);
					
				}
			}
			
			if (curEvent != null) {
				
				if (decelTime == -1) {
					// start braking
					
					logger.debug("decel: " + t);
					
					curEvent.v.queue.add(this);
					
					assert state == CarStateEnum.DRIVING;
					state = CarStateEnum.BRAKING;
					
				} else if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME && accelTime == -1 && curEvent.v.queue.get(0) == this) {
					// start driving
					
					oldEventPositions.add(curEventPosition);
					
					assert state == CarStateEnum.BRAKING;
					state = CarStateEnum.DRIVING;
					
				} else if (overallPos.combo >= curEventMatchingPosition.combo) {
					assert curEvent.v.queue.get(0) == this;
					
					stoppedTime = -1;
					decelTime = -1;
					accelTime = -1;
					
					curEvent.v.queue.remove(this);
					
					curEventPosition = null;
					curEventMatchingPosition = null;
					curEvent = null;
					
				}
				
				
			} else {
				assert state == CarStateEnum.DRIVING;
			}
			
		}
		
		updateFriction();
		
		switch (state) {
		case DRIVING:
			GraphPositionPathPosition next = overallPos.travel(Math.min(steeringLookaheadDistance, overallPos.lengthToEndOfPath));
			goalPoint = next.gpos.p;
			updateDrive(t);
			break;
		case BRAKING:
			goalPoint = curEvent.getPoint();
			updateBrake(t);
			break;
		case CRASHED:
			break;
		case SINKED:
			assert false;
		}
	}
	
	private float cancelingForwardImpulseCoefficient() {
		return 0.02f;
	}
	
	private float cancelingLateralImpulseCoefficient() {
//		return atleastPartiallyOnRoad ? 0.1f : 0.04f;
		return 0.1f;
	}
	
	private float cancelingAngularImpulseCoefficient() {
		return 0.1f;
	}
	
	private float forwardImpulseCoefficient() {
		return 1.0f;
	}
	
	private float brakeImpulseCoefficient() {
		return 0.2f;
//		return 0.1f;
	}
	
	/*
	 * turning radius
	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
	 */
	private double maxRadsPerMeter() {
		return 0.4;
	}
	
	private void updateFriction() {
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		Vec2 cancelingForce = vel.mul(-1).mul(mass).mul((float)(1/MODEL.dt));
		
		float cancelingForwardImpulse = cancelingForwardImpulseCoefficient() * Vec2.dot(currentRightNormal, cancelingImpulse);
		float cancelingLateralImpulse = cancelingLateralImpulseCoefficient() * Vec2.dot(currentUpNormal, cancelingImpulse);
		
		float cancelingForwardForce = cancelingForwardImpulseCoefficient() * Vec2.dot(currentRightNormal, cancelingForce);
		float cancelingLateralForce = cancelingLateralImpulseCoefficient() * Vec2.dot(currentUpNormal, cancelingForce);
		
//		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
//		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
		
		if (!DMath.equals(cancelingForwardForce, 0.0)) {
//			logger.debug("cancelingForwardForce: " + cancelingForwardForce);
			b2dBody.applyForce(currentRightNormal.mul(cancelingForwardForce), pVec2);
		}
		if (!DMath.equals(cancelingLateralForce, 0.0)) {
//			logger.debug("cancelingLateralForce: " + cancelingLateralForce);
			b2dBody.applyForce(currentUpNormal.mul(cancelingLateralForce), pVec2);
		}
		
		float cancelingAngImpulse = cancelingAngularImpulseCoefficient() * momentOfInertia * -angularVel;
		float cancelingAngForce = cancelingAngularImpulseCoefficient() * momentOfInertia * -angularVel * (float)(1/MODEL.dt);
		
		//b2dBody.applyAngularImpulse(cancelingAngImpulse);
		if (!DMath.equals(cancelingAngForce, 0.0)) {
			b2dBody.applyTorque(cancelingAngForce);
		}
	}
	
	private void updateDrive(double t) {
		
		if (stoppedTime != -1) {
			accelTime = t;
		}
		
		double goalForwardVel = (float)getMetersPerSecond();
		
		double forwardSpeed = forwardVel.length();
		
		float acc = (float)(goalForwardVel - forwardSpeed);
		if (acc < 0) {
			acc = 0;
		}
		
		float forwardImpulse = forwardImpulseCoefficient() * mass * acc;
		float forwardForce = forwardImpulseCoefficient() * mass * acc * (float)(1/MODEL.dt);
		
		//b2dBody.applyLinearImpulse(currentRightNormal.mul(forwardImpulse), b2dBody.getWorldCenter());
		if (!DMath.equals(forwardForce, 0.0)) {
//			logger.debug("forwardForce: " + forwardForce);
			b2dBody.applyForce(currentRightNormal.mul(forwardForce), pVec2);
		}
		
		updateTurn();
		
	}
	
	private void updateBrake(double t) {
		
		double forwardSpeed = forwardVel.length();
		
		Point dp;
		double goalForwardVel;
		if (stoppedTime == -1) {
			
			if (decelTime == -1) {
				decelTime = t;
			}
			
			dp = new Point(goalPoint.x-p.x, goalPoint.y-p.y);
			/*
			 * subtract CAR_LENGTH/2 to get the front of the car at the sign
			 */
			goalForwardVel = getMetersPerSecond() * ((dp.length - CAR_LENGTH/2) / (eventLookaheadDistance - CAR_LENGTH/2));
			if (goalForwardVel < 1.0) {
				goalForwardVel = 0;
			}
			goalForwardVel = 0;
		} else {
//			assert t <= stoppedTime + COMPLETE_STOP_WAIT_TIME;
			
			dp = new Point(goalPoint.x-p.x, goalPoint.y-p.y);
			goalForwardVel = 0;
			
		}
		
		float acc = (float)(goalForwardVel - forwardSpeed);
		if (acc > 0) {
			acc = 0;
		}
		
		float brakeImpulse = brakeImpulseCoefficient() * mass * acc;
		float brakeForce = brakeImpulseCoefficient() * mass * acc * (float)(1/MODEL.dt);
		
		if (!DMath.equals(brakeForce, 0.0)) {
			b2dBody.applyForce(currentRightNormal.mul(brakeForce), pVec2);
		}
		
		updateTurn();
	}
	
	private void updateTurn() {
		
		Point dp = new Point(goalPoint.x-p.x, goalPoint.y-p.y);
		double forwardSpeed = forwardVel.length();
		
		double nextDTGoalAngle = Math.atan2(dp.y, dp.x);
		double dw = ((float)nextDTGoalAngle) - angle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		/*
		 * turning radius
		 */
		double actualDistance = forwardSpeed * MODEL.dt;
		double maxRads = maxRadsPerMeter() * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
		float goalAngVel = (float)(dw / MODEL.dt);
		
		float angImpulse = momentOfInertia * goalAngVel;
		float angForce = momentOfInertia * goalAngVel * (float)(1/MODEL.dt);
		
//		b2dBody.applyAngularImpulse(angImpulse);
		if (!DMath.equals(angForce, 0.0)) {
			b2dBody.applyTorque(angForce);
		}
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep(double t) {
		
		computeDynamicProperties();
		
		switch (state) {
		case DRIVING: {
			
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
		case BRAKING:
			
			if (curEvent != null) {
				
				if (stoppedTime == -1) {
					
					if (DMath.equals(vel.length(), 0.0)) {
						
						logger.debug("stopped: " + t);
						stoppedTime = t;
						
					}
					
				} else {
					
				}
				
			}
			
			break;
		case CRASHED:
			break;
		case SINKED:
			assert false;
		}
		
		return true;	
	}
	
	
	
	public double distanceTo(Point p) {
		double d1 = Point.distance(p, worldPoint1);
		double d2 = Point.distance(p, worldPoint2);
		double d3 = Point.distance(p, worldPoint3);
		double d4 = Point.distance(p, worldPoint4);
		return Math.min(Math.min(d1, d2), Math.min(d3, d4));
	}
	
	
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.transform(carTrans);
		
		paintImage(g2);
		
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			g2.fillOval((int)(goalPoint.x * MODEL.PIXELS_PER_METER), (int)(goalPoint.y * MODEL.PIXELS_PER_METER), 2, 2);
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
		}
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.transform(carTrans);
		
		paintRect(g2);
		
		g2.setTransform(origTransform);
	}
	
	protected void paintImage(Graphics2D g2) {
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.drawImage(MODEL.world.sheet,
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
				sheetCol, sheetRow, sheetCol+64, sheetRow+32,
				null);
		
//		g2.drawImage(MODEL.world.sheet,
//				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
//				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
//				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
//				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
//				sheetCol+64, sheetRow, sheetCol+64+64, sheetRow+32,
//				null);
//		
		if (state == CarStateEnum.BRAKING) {
			g2.drawImage(MODEL.world.sheet,
					(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
					(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
					(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
					(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
					sheetCol+64+64, sheetRow, sheetCol+64+64+64, sheetRow+32,
					null);
		}
		
	}
	
	private void paintRect(Graphics2D g2) {
		g2.setColor(hiliteColor);
		g2.fill(path);
	}
}
