package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.MutableOBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.physics.PhysicsBody;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.CarSheet.CarSheetSprite;
import com.gutabi.deadlock.world.sprites.CarSheet.CarType;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public abstract class Car extends PhysicsBody {
	
	public static final double METERS_PER_CARLENGTH = 1.0;
	
	public final CarType type;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public Driver driver;
	public Engine engine;
	
	public int id;
	
	boolean atleastPartiallyOnRoad;
	boolean inMerger;
	
	public Point toolOrigCenter;
	public MutableOBB toolOrigShape = APP.platform.createMutableOBB();
	public Point toolOrigPixelOffset;
	
	public boolean destroyed;
	
	
	public CarSheetSprite sprite;
	public int sheetIndex;
	
	
	public static int carIDCounter;
	
	public Car(World world, CarType type) {
		super(world);
		
		this.type = type;
		
		id = carIDCounter;
		carIDCounter++;
		
		state = CarStateEnum.DRIVING;
	}
	
	public void computeCtorProperties(int sheetIndex) {
		
		this.sheetIndex = sheetIndex;
		
		sprite = CarSheet.sprite(sheetIndex);
		length = sprite.carLength() * METERS_PER_CARLENGTH;
		width = sprite.carWidth() * METERS_PER_CARLENGTH;
		
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
		Geom.localToWorld(localAABB, angle, center, shape);
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
	
	public abstract void paint(RenderingContext ctxt);
	
	
	
	Transform origTransformImage = APP.platform.createTransform();
	
	protected void paintImage(RenderingContext ctxt) {
			
		ctxt.getTransform(origTransformImage);
		if (inMerger) {
			ctxt.setAlpha(0.5);
		}
		
		ctxt.translate(center.x, center.y);
		ctxt.rotate(angle);
		ctxt.translate(localULX, localULY);
		
		APP.carSheet.paint(ctxt, sprite, ctxt.cam.pixelsPerMeter, 0, 0, length, width);
		
		if (inMerger) {
			ctxt.setAlpha(1.0);
		}
		ctxt.setTransform(origTransformImage);
	}
	
	protected void paintRect(RenderingContext ctxt) {
		shape.paint(ctxt);
	}
	
	
	
	Transform origTransformBrakes = APP.platform.createTransform();
	Transform brakeTransformBrakes = APP.platform.createTransform();
	
	protected void paintBrakes(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransformBrakes);
		
		ctxt.translate(center.x, center.y);
		ctxt.rotate(angle);
		
		ctxt.getTransform(brakeTransformBrakes);
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BRAKE, ctxt.cam.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(brakeTransformBrakes);
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BRAKE, ctxt.cam.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(origTransformBrakes);
		
	}
	
	
	Transform origTransformID = APP.platform.createTransform();
	
	public void paintID(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransformID);
		
		ctxt.translate(center.x, center.y);
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.paintString(0.0, 0.0, 2.0/ctxt.cam.pixelsPerMeter, Integer.toString(id));
		ctxt.clearXORMode();
		
		ctxt.setTransform(origTransformID);
	}

}
