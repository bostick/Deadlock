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
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;
import com.gutabi.deadlock.core.path.STPointPath;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	double maxRadsPerSecond = 45.0;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public Source source;
	
	protected GraphPositionPath overallPath;
	GraphPositionPathPosition overallPos;
//	private GraphPositionPath restOfPath;
	
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
	
	boolean atleastPartiallyOnRoad;
	
	public final double length;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		super(s.graph);
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.NEW;
		
		source = s;
		
		color = Color.BLUE;
		hiliteColor = Color.BLUE;
		
		length = CAR_LENGTH;
		
		p1 = new Point(CAR_LENGTH / 2, CAR_LENGTH / 4);
		p2 = new Point(CAR_LENGTH / 2, -CAR_LENGTH / 4);
		p3 = new Point(-CAR_LENGTH / 2, -CAR_LENGTH / 4);
		p4 = new Point(-CAR_LENGTH / 2, CAR_LENGTH / 4);
		
		computeArea();
		
	}
	
	protected void computeStarting() {
		
//		restOfPath = overallPath;
		
		overallPos = new GraphPositionPathPosition(overallPath, 0, 0.0);
		GraphPosition closestGraphPos = overallPos.getGraphPosition();
		startPoint = closestGraphPos.getPoint();
		
		STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, getMetersPerSecond() * MODEL.dt);
		
		STPointPath nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		startHeading = Math.atan2(nextDTGoalPoint.y-startPoint.y, nextDTGoalPoint.x-startPoint.x);
		
		b2dInit();
		
	}
	
	/**
	 * meters per millisecond
	 * @return
	 */
	public abstract double getMetersPerSecond();
	
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
		
//		logger.debug("mass: " + b2dBody.getMass());
//		logger.debug("moment of intertia: " + b2dBody.getInertia());
		
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
		
		source.outstandingCars--;
	}
	
	private Point carToWorld(Point p) {
		AffineTransform b2dTrans = new AffineTransform();
		Vec2 pos = b2dBody.getPosition();
		float angle = b2dBody.getAngle();
		b2dTrans.translate(pos.x, pos.y);
		b2dTrans.rotate(angle);
		return Point.point(b2dTrans.transform(p.point2D(), null));
	}
	
	public void preStep(double t) {
		
		Entity e1 = graph.hitTest(carToWorld(p1));
		Entity e2 = graph.hitTest(carToWorld(p2));
		Entity e3 = graph.hitTest(carToWorld(p3));
		Entity e4 = graph.hitTest(carToWorld(p4));
		
		if (e1 == null && e2 == null && e3 == null && e4 == null) {
			atleastPartiallyOnRoad = false;
		} else {
			atleastPartiallyOnRoad = true;
		}
		
		switch (state) {
		case NEW: {
			
			state = CarStateEnum.RUNNING;
			
			updateDrive();
			updateTurn();
			break;
		}
		case RUNNING: {
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
	
	private float cancelingForwardImpulseCoefficient() {
		return 1.0f;
	}
	
	private float cancelingLateralImpulseCoefficient() {
		return atleastPartiallyOnRoad ? 0.3f : 0.08f;
	}
	
	private float forwardImpulseCoefficient() {
		return atleastPartiallyOnRoad ? 1.0f : 0.4f;
	}
	
	private void updateDrive() {
		
		Vec2 currentRightNormal = b2dBody.getWorldVector(new Vec2(1, 0));
		Vec2 currentUpNormal = b2dBody.getWorldVector(new Vec2(0, 1));
		
		Vec2 vel = b2dBody.getLinearVelocity();
		
//		float forwardSpeed = Vec2.dot(currentRightNormal, b2dBody.getLinearVelocity());
//		logger.debug("forward speed: " + forwardSpeed);
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(b2dBody.getMass());
		
		float cancelingForwardImpulse = Vec2.dot(currentRightNormal, cancelingImpulse);
		cancelingForwardImpulse =  cancelingForwardImpulseCoefficient() * cancelingForwardImpulse;
		
		float cancelingLateralImpulse = Vec2.dot(currentUpNormal, cancelingImpulse);
		cancelingLateralImpulse = cancelingLateralImpulseCoefficient() * cancelingLateralImpulse;
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
		
		float impulse = ((float)(forwardImpulseCoefficient() * getMetersPerSecond())) * b2dBody.getMass();
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(impulse), b2dBody.getWorldCenter());
		
	}
	
	private void updateTurn() {
		
		Vec2 curVec = b2dBody.getPosition();
		
		float curAngle = b2dBody.getAngle();
		
		Point curPoint = new Point(curVec.x, curVec.y);
		
		GraphPositionPathPosition newOverallPos = overallPath.findClosestGraphPositionPathPosition(curPoint, overallPos);
		
//		double overallCombo = overallPos.index + overallPos.param;
//		double newOverallCombo = newOverallPos.index + newOverallPos.param;
////		
//		assert newOverallCombo >= overallCombo;
		
//		logger.debug("newOverallCombo: " + newOverallCombo);
		
		overallPos = newOverallPos;
		
		STGraphPositionPathPositionPath x = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, 0.2f * getMetersPerSecond());
		
		// fix pathing problem
		// uncomment this code to update restOfPath, so that we are always strictly testing what is ahead in the path, to prevent loops when random
//		d;
//		GraphPositionPathPosition nextOverallPos = x.get(x.size()-1).getSpace();
		
//		logger.debug("nextOverallPos: " + nextOverallPos);
		
//		GraphPositionPath restOfPathTest = nextOverallPos.getRestOfPath();
//		if (restOfPathTest != null) {
//			restOfPath = restOfPathTest;
//		} else {
//			
//		}
		
		STPointPath nextRealPath = x.toSTGraphPositionPath().toSTPointPath();
		
		Point nextDTGoalPoint = nextRealPath.end.getSpace();
		
		double nextDTGoalAngle = Math.atan2(nextDTGoalPoint.y-curVec.y, nextDTGoalPoint.x-curVec.x);
		
		float curAngVel = b2dBody.getAngularVelocity();
		
//		logger.debug("angular speed: " + curAngVel);
		
		double dw = ((float)nextDTGoalAngle) - curAngle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		float maxRads = (float)(maxRadsPerSecond * MODEL.dt);
		
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
//		logger.debug("dw: " + dw);
		
		float cancelingAngImpulse = 1.0f * b2dBody.getInertia() * -curAngVel;
		
//		logger.debug("canceling angular impulse: " + cancelingAngImpulse);
		
		b2dBody.applyAngularImpulse(cancelingAngImpulse);
		
		float angImpulse = b2dBody.getInertia() * (float)(dw / MODEL.dt);
		
//		logger.debug("angular impulse: " + angImpulse);
		
		b2dBody.applyAngularImpulse(angImpulse);
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep() {
		
		switch (state) {
		case NEW:
			assert false;
		case RUNNING: {
			Vec2 pos = b2dBody.getPosition();
			Sink s = (Sink)overallPath.getEnd().getEntity();
			boolean sinked = false;
			if (Point.distance(new Point(pos.x,  pos.y), s.getPoint()) < MODEL.world.SINK_EPSILON) {
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
			break;
		case SINKED:
			assert false;
		}
		
		return true;	
	}
	
	
	
	public int getId() {
		return id;
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
		paintImage(g2, image());
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
		
		b2dTrans.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setTransform(b2dTrans);
		
		g2.drawImage(im,
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER / 2),
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER / 2),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER), null);
		
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
		
		g2.setColor(hiliteColor);
		
		g2.fill(path);
		
		g2.setTransform(origTransform);
	}
	
}
