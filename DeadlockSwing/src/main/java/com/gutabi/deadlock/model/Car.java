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
	
	public STPointPath nextRealPath;
	
	GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	private Body b2dBody;
	private Shape b2dShape;
	private Fixture b2dFixture;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.RUNNING;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
		Point end = overallPath.getEnd().getPoint();
		
		Point startPos = overallPath.getGraphPosition(0).getPoint();
		double startHeading = Math.atan2(end.getY()-startPos.getY(), end.getX()-startPos.getX());
		
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
			
			Vec2 pos = b2dBody.getPosition();
			
//			logger.debug("pos: " + pos);
			
			float angle = b2dBody.getAngle();
			
			Point posP = new Point(pos.x, pos.y);
			
			GraphPositionPathPosition overallPos = overallPath.findClosestGraphPositionPathPosition(posP);
			
			STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, getSpeed() * MODEL.world.dt);
			
			nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
			
			Point nextPos = nextRealPath.end.getSpace();
			double nextHeading = Math.atan2(nextPos.getY()-pos.y, nextPos.getX()-pos.x);
			
			Vec2 v1 = new Vec2((float)(nextPos.getX()-pos.x), (float)(nextPos.getY()-pos.y));
			v1.normalize();
			v1.mulLocal((float)(getSpeed() * 1000));
			
			b2dBody.setLinearVelocity(v1);
			
			float angV = (float)(nextHeading - angle);
			angV = angV * 1000 / MODEL.world.dt;
			
//			logger.debug("angV: " + angV);
			
			b2dBody.setAngularVelocity(angV);
			
		}
		case CRASHED:
			break;
		}
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
		
		b2dTrans.translate(-MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 4);
		b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
		
		g2.setTransform(b2dTrans);
		
		g2.drawImage(im,
				0,
				0,
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
				(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
		
		g2.setTransform(origTransform);
	}
	
}
