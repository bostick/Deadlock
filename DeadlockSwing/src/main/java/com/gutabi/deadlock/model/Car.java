package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Matrix;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Geom;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.VertexPosition;
import com.gutabi.deadlock.model.fixture.WorldSink;
import com.gutabi.deadlock.model.fixture.WorldSource;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	public static final double COMPLETE_STOP_WAIT_TIME = 0.0;
	double steeringLookaheadDistance = CAR_LENGTH * 1.5;
	double eventLookaheadDistance = getMetersPerSecond() * 0.3;
	
	protected int sheetRow;
	protected int sheetCol;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public  WorldSource source;
	
	protected GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	Point startPoint;
	double startHeading;
	float mass;
	float momentOfInertia;
	
	Quad localQuad;
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
	Matrix carTrans;
	float carAngle;
	boolean atleastPartiallyOnRoad;
//	boolean completelyOnRoad;
	boolean inMerger;
	GraphPositionPathPosition overallPos;
//	Point worldPoint1;
//	Point worldPoint2;
//	Point worldPoint3;
//	Point worldPoint4;
	Quad worldQuad;
	Point prevWorldPoint0;
	Point prevWorldPoint3;
	
	/*
	 * 
	 */
	StopSign curSign;
	GraphPositionPathPosition curBorderPosition;
	GraphPositionPathPosition curVertexPosition;
	GraphPositionPathPosition curBorderMatchingPosition;
	List<GraphPositionPathPosition> oldBorderPositions = new ArrayList<GraphPositionPathPosition>();
	double decelTime = -1;
	double accelTime = -1;
	double stoppedTime = -1;
	Point goalPoint;
	
	protected Color color;
	protected Color hiliteColor;
	
	static Logger logger = Logger.getLogger(Car.class);
	static Logger pathingLogger = Logger.getLogger("com.gutabi.deadlock.model.Car.pathing");
			
	public Car(WorldSource s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.DRIVING;
		
		source = s;
		
		color = Color.BLUE;
		hiliteColor = Color.BLUE;
		
		Point p0 = new Point(-CAR_LENGTH / 2, CAR_LENGTH / 4);
		Point p1 = new Point(CAR_LENGTH / 2, CAR_LENGTH / 4);
		Point p2 = new Point(CAR_LENGTH / 2, -CAR_LENGTH / 4);
		Point p3 = new Point(-CAR_LENGTH / 2, -CAR_LENGTH / 4);
		localQuad = new Quad(this, p0, p1, p2, p3);
		
	}
	
	protected void computeStartingProperties() {
		
		logger.debug("spawn");
		
		overallPos = new GraphPositionPathPosition(overallPath, 0, 0.0);
		GraphPosition closestGraphPos = overallPos.gpos;
		startPoint = closestGraphPos.p;
		
		if (pathingLogger.isDebugEnabled()) {
			pathingLogger.debug("overall path:");
			for (GraphPosition gp : overallPath.poss) {
				pathingLogger.debug(gp);
			}
			pathingLogger.debug("");
			
			pathingLogger.debug("overall path bounds:");
			GraphPositionPathPosition cur = overallPos;
			while (true) {
				pathingLogger.debug(cur);
				if (cur.isEndOfPath()) {
					break;
				}
				cur = cur.nextBound();
			}
			pathingLogger.debug("");
		}
		
		GraphPositionPathPosition next = overallPos.travel(getMetersPerSecond() * MODEL.dt);
		
		Point nextDTGoalPoint = next.gpos.p;
		
		Point dp = new Point(nextDTGoalPoint.x-startPoint.x, nextDTGoalPoint.y-startPoint.y);
		
		startHeading = Math.atan2(dp.y, dp.x);
		
		b2dInit();
		
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		Mat22 r = b2dBody.getTransform().R;
		carTrans = new Matrix(r.col1.x, r.col2.x, r.col1.y, r.col2.y);
		
		worldQuad = Geom.localToWorld(localQuad, carTrans, p);
		shape = worldQuad;
		
		
		Vec2 v = dp.vec2();
		v.normalize();
		v.mulLocal((float)getMetersPerSecond());
		
		b2dBody.setLinearVelocity(v);
		
		computeDynamicProperties();
	}
	
	/**
	 * meters per millisecond
	 * @return
	 */
	public abstract double getMetersPerSecond();
	
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
		fixtureDef.friction = 0.8f;
		b2dFixture = b2dBody.createFixture(fixtureDef);
		
		mass = b2dBody.getMass();
		momentOfInertia = b2dBody.getInertia();
		
	}
	
	
	Vec2 right = new Vec2(1, 0);
	Vec2 up = new Vec2(0, 1);
	Filter normalCarFilter = new Filter();
	Filter mergingCarFilter = new Filter();
	{
		normalCarFilter.categoryBits = 1;
		normalCarFilter.maskBits = 1;
		mergingCarFilter.categoryBits = 2;
		mergingCarFilter.maskBits = 0;
	}
	
	private void computeDynamicProperties() {
		
//		Point prevP = p;
//		Point prevWorldPoint1 = worldPoint1;
//		Point prevWorldPoint2 = worldPoint1;
		prevWorldPoint0 = worldQuad.p0;
		prevWorldPoint3 = worldQuad.p3;
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		vel = b2dBody.getLinearVelocity();
		angle = b2dBody.getAngle();
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		
		if (logger.isDebugEnabled()) {
			logger.debug("vel: " + forwardVel.length());
		}
		
		Mat22 r = b2dBody.getTransform().R;
		carTrans = new Matrix(r.col1.x, r.col2.x, r.col1.y, r.col2.y);
		carAngle = b2dBody.getAngle();
		
//		worldPoint1 = carToWorld(p1);
//		worldPoint2 = carToWorld(p2);
//		worldPoint3 = carToWorld(p3);
//		worldPoint4 = carToWorld(p4);
		worldQuad = Geom.localToWorld(localQuad, carTrans, p);
		shape = worldQuad;
		
		Entity e = MODEL.world.graphBestHitTest(shape);
		
		boolean wasInMerger = inMerger;
		if (e == null) {
			atleastPartiallyOnRoad = false;
//			completelyOnRoad = false;
			inMerger = false;
		} else {
			atleastPartiallyOnRoad = true;
			if (e instanceof Merger && ((Quad)shape).containedIn((Quad)e.shape)) {
				inMerger = true;
			} else {
				inMerger = false;
			}
		}
		
		if (!atleastPartiallyOnRoad) {
			skid();
		}
		
		if (inMerger == !wasInMerger) {
			if (inMerger) {
				b2dFixture.setFilterData(mergingCarFilter);
			} else {
				b2dFixture.setFilterData(normalCarFilter);
			}
		}
		
		
//		computeAABB();
		
	}
	
//	protected void computeAABB() {
//		
//		aabb = null;
//		
//		aabb = new Rect(b2dAABB.lowerBound.x, b2dAABB.lowerBound.y, b2dAABB.upperBound.x-b2dAABB.lowerBound.x, b2dAABB.upperBound.y-b2dAABB.lowerBound.y);
//	}
	
	
//	private Point carToWorld(Point c) {
//		return carTrans.times(c).plus(p);
//	}
//	
//	private Quad carToWorld(Quad q) {
//		return carTrans.times(c).plus(p);
//	}
	
	
	
	public void destroy() {
		b2dCleanup();
	}
	
	private void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	
	
	
//	public Point worldPoint1() {
//		return worldPoint1;
//	}
//	public Point worldPoint2() {
//		return worldPoint2;
//	}
//	public Point worldPoint3() {
//		return worldPoint3;
//	}
//	public Point worldPoint4() {
//		return worldPoint4;
//	}
	
//	public Car hitTest(Point p) {
//		if (aabb.hitTest(p)) {
//			
//			if (worldQuad.hitTest(p)) {
//				return this;
//			} else {
//				return null;
//			}
//			
//		} else {
//			return null;
//		}
//	}
//	
//	public Car bestHitTest(Point p, double r) {
//		if (worldQuad.bestHitTest(p, r)) {
//			return this;
//		} else {
//			return null;
//		}
//	}
//	
//	public Car bestHitTest(Quad q) {
//		
//	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	public void crash() {
		
		state = CarStateEnum.CRASHED;
		
		overallPos = null;
		
//		source.outstandingCars--;
		
		if (curBorderPosition != null) {
			
			if (curSign != null) {
				stoppedTime = -1;
				decelTime = -1;
				accelTime = -1;
			}
			
			((VertexPosition)curVertexPosition.gpos).v.carQueue.remove(this);
			
			curBorderPosition = null;
			curVertexPosition = null;
			curBorderMatchingPosition = null;
			curSign = null;
			
		}
		
	}
	
	private void skid() {
		
		state = CarStateEnum.SKIDDED;
		
		overallPos = null;
		
//		source.outstandingCars--;
		
		if (curBorderPosition != null) {
			
			if (curSign != null) {
				stoppedTime = -1;
				decelTime = -1;
				accelTime = -1;
			}
			
			((VertexPosition)curVertexPosition.gpos).v.carQueue.remove(this);
			
			curBorderPosition = null;
			curVertexPosition = null;
			curBorderMatchingPosition = null;
			curSign = null;
			
		}
		
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	public void preStep(double t) {
		
		if (state == CarStateEnum.CRASHED || state == CarStateEnum.SKIDDED || state == CarStateEnum.SINKED) {
			
		} else {
			
			/*
			 * calculate current overall position on graph
			 */
			GraphPositionPathPosition max = overallPos.travel(Math.min(2 * CAR_LENGTH, overallPos.lengthToEndOfPath));
			overallPos = overallPath.findClosestGraphPositionPathPosition(p, overallPos, max);
			
			if (pathingLogger.isDebugEnabled()) {
				pathingLogger.debug("overallPos: " + overallPos.gpos);
			}
			
//			pathingLogger.debug("next bound after overallPos: " + overallPos.nextBound().gpos);
			
			if (curBorderPosition == null) {
				List<GraphPositionPathPosition> borderPositions = overallPath.borderPositions(overallPos, Math.min(eventLookaheadDistance, overallPos.lengthToEndOfPath));
				/*
				 * since we deal with events before actually reaching them (front of car reaches them vs center of car where position is counted),
				 * simply keep a list of already processed events to use to filter
				 */
				borderPositions.removeAll(oldBorderPositions);
				
				if (!borderPositions.isEmpty()) {
					
//					logger.debug("setting curEvent: " + events + "     " + oldEvents);
					
					curBorderPosition = borderPositions.get(0);
					curVertexPosition = curBorderPosition.nextBound();
					assert curVertexPosition.gpos instanceof VertexPosition;
					if (!curVertexPosition.isEndOfPath()) {
						curBorderMatchingPosition = curVertexPosition.nextBound().travel(CAR_LENGTH / 2);
					} else {
						curBorderMatchingPosition = null;
					}
					
					curSign = ((RoadPosition)curBorderPosition.gpos).sign;
					
					((VertexPosition)curVertexPosition.gpos).v.carQueue.add(this);
					
				}
			}
			
			if (curBorderPosition != null) {
				
				if (curSign != null) {
					if (decelTime == -1) {
						// start braking
						
//						logger.debug("decel: " + t);
						
						assert state == CarStateEnum.DRIVING;
						state = CarStateEnum.BRAKING;
						
					} else if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME && accelTime == -1 && ((VertexPosition)curVertexPosition.gpos).v.carQueue.get(0) == this) {
						// start driving
						
						oldBorderPositions.add(curBorderPosition);
						
						assert state == CarStateEnum.BRAKING;
						state = CarStateEnum.DRIVING;
						
					}
				}
				
				if (curBorderMatchingPosition != null && overallPos.combo >= curBorderMatchingPosition.combo) {
					
					if (curSign != null) {
						stoppedTime = -1;
						decelTime = -1;
						accelTime = -1;
					}
					
					((VertexPosition)curVertexPosition.gpos).v.carQueue.remove(this);
					
					curBorderPosition = null;
					curVertexPosition = null;
					curBorderMatchingPosition = null;
					curSign = null;
					
				}
				
				
			} else {
				assert state == CarStateEnum.DRIVING;
			}
			
		}
		
		updateFriction();
		
		switch (state) {
		case DRIVING:
			
			double lookaheadDistance = Math.min(steeringLookaheadDistance, overallPos.lengthToEndOfPath);
			
			GraphPositionPathPosition next = overallPos.travel(lookaheadDistance);
			
			if (pathingLogger.isDebugEnabled()) {
				pathingLogger.debug("goalPoint: " + next.gpos);
			}
			
			double nextDist = overallPos.distanceTo(next);
			assert DMath.equals(nextDist, lookaheadDistance);
			
			goalPoint = next.gpos.p;
			
			updateDrive(t);
			break;
		case BRAKING:
			goalPoint = curBorderPosition.gpos.p;
			updateBrake(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
			break;
		}
	}
	
	private float cancelingForwardImpulseCoefficient() {
		return 0.01f;
	}
	
	private float cancelingLateralImpulseCoefficient() {
//		return atleastPartiallyOnRoad ? 0.1f : 0.04f;
		return 0.05f;
	}
	
	private float cancelingAngularImpulseCoefficient() {
		return 0.1f;
	}
	
	private float forwardImpulseCoefficient() {
		return 1.0f;
	}
	
	private float brakeImpulseCoefficient() {
		return 0.1f;
	}
	
	/*
	 * turning radius
	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
	 */
	private double maxRadsPerMeter() {
		return 0.1;
	}
	
	private void updateFriction() {
		
//		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		Vec2 cancelingForce = vel.mul(-1).mul(mass).mul((float)(1/MODEL.dt));
		
//		float cancelingForwardImpulse = cancelingForwardImpulseCoefficient() * Vec2.dot(currentRightNormal, cancelingImpulse);
//		float cancelingLateralImpulse = cancelingLateralImpulseCoefficient() * Vec2.dot(currentUpNormal, cancelingImpulse);
		
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
		
//		float cancelingAngImpulse = cancelingAngularImpulseCoefficient() * momentOfInertia * -angularVel;
		float cancelingAngForce = cancelingAngularImpulseCoefficient() * momentOfInertia * -angularVel * (float)(1/MODEL.dt);
		
		//b2dBody.applyAngularImpulse(cancelingAngImpulse);
		if (!DMath.equals(cancelingAngForce, 0.0)) {
			b2dBody.applyTorque(cancelingAngForce);
		}
	}
	
	private void updateDrive(double t) {
		
		if (stoppedTime != -1) {
			
			logger.debug("accel");
			
			accelTime = t;
		}
		
		double goalForwardVel = (float)getMetersPerSecond();
		
		double forwardSpeed = forwardVel.length();
		
		float acc = (float)(goalForwardVel - forwardSpeed);
		if (acc < 0) {
			acc = 0;
		}
		if (acc > 0.1) {
			acc = 0.1f;
		}
		
//		float forwardImpulse = forwardImpulseCoefficient() * mass * acc;
		float forwardForce = forwardImpulseCoefficient() * mass * acc * (float)(1/MODEL.dt);
		
//		if (forwardForce > 20) {
//			String.class.getName();
//		}
		
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
				
				logger.debug("decel");
				
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
		} else {			
			dp = new Point(goalPoint.x-p.x, goalPoint.y-p.y);
			goalForwardVel = 0;
			
		}
		
		float acc = (float)(goalForwardVel - forwardSpeed);
		if (acc > 0) {
			acc = 0;
		}
		
//		float brakeImpulse = brakeImpulseCoefficient() * mass * acc;
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
		
//		float angImpulse = momentOfInertia * goalAngVel;
		float angForce = momentOfInertia * goalAngVel * (float)(1/MODEL.dt);
		
//		b2dBody.applyAngularImpulse(angImpulse);
		if (!DMath.equals(angForce, 0.0)) {
			b2dBody.applyTorque(angForce);
		}
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	@Override
	public boolean postStep(double t) {
		
		computeDynamicProperties();
		
		switch (state) {
		case DRIVING: {
			
			WorldSink s = (WorldSink)overallPath.end.e;
			boolean sinked = false;
			if (Point.distance(p, s.p) < MODEL.world.SINK_EPSILON) {
				sinked = true;
			}
			
			if (sinked) {
				
				logger.debug("sink");
				
				s.matchingSource.outstandingCars--;
				s.carQueue.remove(this);
				state = CarStateEnum.SINKED;
				return false;
			}
			break;
		}
		case BRAKING:
			
			if (stoppedTime == -1) {
				
				if (DMath.equals(vel.length(), 0.0)) {
					
//					logger.debug("stopped: " + t);
					stoppedTime = t;
					
				}
				
			}
			
			MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
			MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
			
			break;
		case CRASHED:
			break;
		case SKIDDED:
			
			MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
			MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
			
			break;
		case SINKED:
			break;
		}
		
		return true;	
	}
	
	
	
//	public double distanceTo(Point p) {
//		double d1 = Point.distance(p, worldPoint1);
//		double d2 = Point.distance(p, worldPoint2);
//		double d3 = Point.distance(p, worldPoint3);
//		double d4 = Point.distance(p, worldPoint4);
//		return Math.min(Math.min(d1, d2), Math.min(d3, d4));
//	}
	
	
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.translate(p.x, p.y);
//		g2.transform(carTrans.affineTransform());
		g2.rotate(carAngle);
		
		paintImage(g2);
		
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			if (goalPoint != null) {
				g2.setColor(Color.WHITE);
				g2.fillOval((int)(goalPoint.x * MODEL.PIXELS_PER_METER) - 2, (int)(goalPoint.y * MODEL.PIXELS_PER_METER) - 2, 4, 4);
			}
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
		}
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		paintRect(g2);
		
		g2.setTransform(origTransform);
	}
	
	
	static Composite aComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	static Composite normalComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	
	protected void paintImage(Graphics2D g2) {
		
		Composite origComposite = g2.getComposite();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		if (inMerger) {
			g2.setComposite(aComp);
		} else {
			g2.setComposite(normalComp);
		}
		
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
		
		g2.setComposite(origComposite);
		
	}
	
	private void paintRect(Graphics2D g2) {
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(hiliteColor);
		
		worldQuad.paint(g2);
	}
}
