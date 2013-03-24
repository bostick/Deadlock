package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.physics.PhysicsBody;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.CarSheet.CarSheetSprite;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public abstract class Car extends PhysicsBody {
	
	public static final double METERS_PER_CARLENGTH = 1.0;
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public Driver driver;
	public Engine engine;
	
	public int id;
	
//	public static int carCounter;
	
//	public Point localFront;
	
	boolean atleastPartiallyOnRoad;
	boolean inMerger;
	
	public Point toolOrigP;
//	public double toolOrigAngle;
	public OBB toolOrigShape;
	
	public boolean destroyed;
	
	
	public static int carIDCounter;
	
	public Car(World world) {
		super(world);
		
		id = carIDCounter;
		carIDCounter++;
		
		state = CarStateEnum.DRIVING;
	}
	
	public void computeCtorProperties(int r) {
		
		sprite = CarSheet.sprite(r);
		length = sprite.carLength() * METERS_PER_CARLENGTH;
		width = sprite.carWidth() * METERS_PER_CARLENGTH;
//		localFront = new Point(0.75 * length, 0.0);
		
		localAABB = new AABB(-length / 2, -width / 2, length, width);
		
		localULX = -length / 2;
		localULY = -width / 2;
		
		BRAKE_SIZE = 0.25;
		BRAKE_LOCALX = -BRAKE_SIZE / 2;
		BRAKE_LOCALY = -BRAKE_SIZE / 2;
		
		CAR_BRAKE1X = localULX + BRAKE_LOCALX;
		CAR_BRAKE1Y = localULY + width/4 + BRAKE_LOCALY;
		
		CAR_BRAKE2X = localULX + BRAKE_LOCALX;
		CAR_BRAKE2Y = localULY + 3 * width/4 + BRAKE_LOCALY;
	}
	
	public void setTransform(Point center, double angle) {
		this.center = center;
		this.angle = angle;
		shape = Geom.localToWorld(localAABB, angle, center);
	}
	
	public void skid() {
		state = CarStateEnum.SKIDDED;
		((AutonomousDriver)driver).clear();
	}
	
	public void destroy() {
		switch (state) {
		case BRAKING:
		case CRASHED:
		case DRIVING:
		case SINKED:
		case SKIDDED:
			super.destroy();
			break;
		case IDLE:
		case DRAGGING:
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			break;
		}
		destroyed = true;
	}
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
//	public double newAngle(GraphPositionPathPosition curPos, GraphPositionPathPosition testPos) {
//		
//		GraphPosition testGpos = testPos.getGraphPosition();
//		
//		double a;
//		
//		if (testPos.equals(curPos)) {
//			assert false;
//			a = Double.NaN;
//		} else if (DMath.lessThan(curPos.combo, testPos.combo)) {
//			a = Math.atan2(testGpos.p.y - curPos.p.y, testGpos.p.x - curPos.p.x);
//		} else {
//			a = Math.atan2(curPos.p.y - testGpos.p.y, curPos.p.x - testGpos.p.x);
//		}
//		
//		if (DMath.lessThan(a, 0.0)) {
//			a = a + 2 * Math.PI;
//		}
//		
//		return a;
//	}
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(Color.BLUE);
		paintRect(ctxt);
	}
	
	public double BRAKE_SIZE;
	public double BRAKE_LOCALX;
	public double BRAKE_LOCALY;
	
	public double CAR_BRAKE1X;
	public double CAR_BRAKE1Y;
	
	public double CAR_BRAKE2X;
	public double CAR_BRAKE2Y;
	
	public CarSheetSprite sprite;
	
	public abstract void paint(RenderingContext ctxt);
	
	protected void paintImage(RenderingContext ctxt) {
			
		Transform origTransform = ctxt.getTransform();
		if (inMerger) {
			ctxt.setAlpha(0.5);
		}
		
		ctxt.translate(center.x, center.y);
		ctxt.rotate(angle);
		ctxt.translate(localULX, localULY);
		
		APP.carSheet.paint(ctxt, sprite, world.worldScreen.pixelsPerMeter, 0, 0, length, width);
		
		if (inMerger) {
			ctxt.setAlpha(1.0);
		}
		ctxt.setTransform(origTransform);
		
	}
	
	protected void paintRect(RenderingContext ctxt) {
		shape.paint(ctxt);
	}
	
	protected void paintBrakes(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(center.x, center.y);
		ctxt.rotate(angle);
		
		Transform brakeTransform = ctxt.getTransform();
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BRAKE, world.worldScreen.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(brakeTransform);
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BRAKE, world.worldScreen.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public void paintID(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(center.x, center.y);
		
		ctxt.setColor(Color.WHITE);
		ctxt.paintString(localULX, 0.0, 2.0/world.worldScreen.pixelsPerMeter, Integer.toString(id));
		
		ctxt.setTransform(origTransform);
	}

}
