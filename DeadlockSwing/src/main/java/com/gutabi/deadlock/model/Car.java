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

import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;
import com.gutabi.deadlock.core.path.STPointPath;

public abstract class Car {
	
	public CarStateEnum state;
	
//	Point realPos;
//	double heading;
	
	public long startingTime;
	public long crashingTime;
	public Source source;
	
	public STPointPath nextRealPath;
//	public boolean nextCrashed;
	
	GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	Body b2dBody;
	Shape b2dShape;
	Fixture b2dFixture;
//	public boolean b2dCrash;
	
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
	
	public void preStep() {
		
//		assert nextRealPath == null;
		
		switch (state) {
		case RUNNING: {
			
			Vec2 pos = b2dBody.getPosition();
			
//			logger.debug("pos: " + pos);
			
			float angle = b2dBody.getAngle();
			
			Point posP = new Point(pos.x, pos.y);
			EdgePosition edgePos = MODEL.world.graph.findClosestEdgePosition(posP, null, MODEL.world.CAR_LENGTH);
			Vertex vertexPos = MODEL.world.graph.findClosestVertexPosition(posP, null, MODEL.world.CAR_LENGTH, false);
			GraphPosition graphPos = null;
			if (edgePos != null) {
				graphPos = edgePos;
			}
			if (vertexPos != null && (edgePos == null || Point.distance(posP, vertexPos.getPoint()) < Point.distance(posP, edgePos.getPoint()))) {
				graphPos = vertexPos;
			}
			
//			logger.debug("graphPos: " + graphPos);
			
			GraphPositionPathPosition overallPos = overallPath.hitTest(graphPos);
			
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
			
//			return true;
		}
		case CRASHED: {
//			return false;
		}
		default:
//			assert false;
//			return false;
			break;
		}
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep() {
		
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
		case RUNNING: {
			
			AffineTransform origTransform = g2.getTransform();
//			g2.setColor(Color.BLUE);
			
			AffineTransform b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
			Vec2 pos = b2dBody.getPosition();
			float angle = b2dBody.getAngle();
			b2dTrans.translate(pos.x, pos.y);
			b2dTrans.rotate(angle);
			
			b2dTrans.translate(-MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 4);
			b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
			
			g2.setTransform(b2dTrans);
			
//			g2.fillRect(
//					0,
//					0,
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2));
			BufferedImage im = image();
			g2.drawImage(im,
					0,
					0,
					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
			
			g2.setTransform(origTransform);
			
			
			
			
//			BufferedImage im = image();
//			
//			AffineTransform carTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
//			carTrans.translate(realPos.getX(), realPos.getY());
//			carTrans.rotate(heading);
//			
////			Point2D src = new Point2D.Double(0, 0);
////			Point2D dst = carTrans.transform(src, null);
////			logger.debug("dst: " + dst);
//			
//			carTrans.translate(-MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 2);
//			carTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
//			
//			g2.setTransform(carTrans);
//			
//			g2.drawImage(im,
//					0,
//					0,
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
//			
//			g2.setTransform(origTransform);
			
			
			
			break;
		}
		case CRASHED: {
			
			AffineTransform origTransform = g2.getTransform();
//			g2.setColor(Color.BLUE);
			
			AffineTransform b2dTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
			Vec2 pos = b2dBody.getPosition();
			float angle = b2dBody.getAngle();
			b2dTrans.translate(pos.x, pos.y);
			b2dTrans.rotate(angle);
			
			b2dTrans.translate(-MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 4);
			b2dTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
			
			g2.setTransform(b2dTrans);
			
//			g2.fillRect(
//					0,
//					0,
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER / 2));
			BufferedImage im = VIEW.crash;
			g2.drawImage(im,
					0,
					0,
					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
			
			g2.setTransform(origTransform);
			
			
			
			
			
			
			
//			AffineTransform origTransform = g2.getTransform();
//			
//			BufferedImage im = VIEW.crash;
//			
//			AffineTransform carTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
//			carTrans.translate(realPos.getX(), realPos.getY());
//			carTrans.rotate(heading);
//			carTrans.translate(-MODEL.world.CAR_LENGTH / 2, -MODEL.world.CAR_LENGTH / 2);
//			carTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
//			
//			g2.setTransform(carTrans);
//			
//			g2.drawImage(im,
//					0,
//					0,
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER),
//					(int)(MODEL.world.CAR_LENGTH * MODEL.world.PIXELS_PER_METER), null);
//			
//			g2.setTransform(origTransform);
//			
//			break;
		}
		}
		
	}
	
}
