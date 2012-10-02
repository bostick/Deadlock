package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;
import com.gutabi.deadlock.core.path.STPointPath;

public abstract class Car {
	
	protected CarStateEnum state;
	
	Point realPos;
	double heading;
	
	public long startingTime;
	public long crashingTime;
	public Source source;
	
	public STPointPath nextRealPath;
//	public CarStateEnum nextState;
	public boolean nextCrashed;
	
	GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.RUNNING;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
		Point end = overallPath.getEnd().getPoint();
		
		realPos = overallPath.getGraphPosition(0).getPoint();
		heading = Math.atan2(end.getY()-realPos.getY(), end.getX()-realPos.getX());
		
	}
	
	/**
	 * pixels per millisecond
	 * @return
	 */
	public abstract double getSpeed();
	
	/**
	 * Returns true if car moved in this update
	 */
	public boolean updateNext() {
		
		assert nextRealPath == null;
		
		switch (state) {
		case RUNNING: {
			
			if (false) {
				
				GraphPosition graphPos = MODEL.world.graph.hitTest(realPos);
				
				GraphPositionPathPosition overallPos = overallPath.hitTest(graphPos);
				
				STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, getSpeed() * MODEL.world.dt);
				
				nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
				
			} else {
				
				nextRealPath = STPointPath.advanceOneTimeStep(realPos, heading, getSpeed() * MODEL.world.dt);
				
			}
			
			return true;
		}
		case CRASHED: {
			
			if (false) {
				
				GraphPosition graphPos = MODEL.world.graph.hitTest(realPos);
				
				GraphPositionPathPosition overallPos = overallPath.hitTest(graphPos);
				
				STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.crashOneTimeStep(overallPos);
				
				nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
				
			} else {
				
				nextRealPath = STPointPath.advanceOneTimeStep(realPos, heading, 0 * MODEL.world.dt);
				
			}
			
			return false;
		}
		default:
			assert false;
			return false;
		}
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean updateCurrentFromNext() {
		
		switch (state) {
		case RUNNING: {
			Point prevPos = realPos;
			realPos = nextRealPath.end.getSpace();
			heading = Math.atan2(realPos.getY()-prevPos.getY(), realPos.getX()-prevPos.getX());
			
			if (MODEL.world.graph.hitTest(realPos) instanceof Sink) {
				state = CarStateEnum.SINKED;
			} else if (nextCrashed) {
				state = CarStateEnum.CRASHED;
			} else {
				state = CarStateEnum.RUNNING;
			}
			
			nextRealPath = null;
			
			return state == CarStateEnum.RUNNING || state == CarStateEnum.CRASHED;
		}
		case CRASHED: {
			
			nextRealPath = null;
			
			return true;
		}
		default:
			assert false;
			return false;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Point getPosition() {
		return realPos;
	}
	
	abstract BufferedImage image();
	
	public void paint(Graphics2D g2) {
		
		switch (state) {
		case RUNNING: {
			
			AffineTransform origTransform = g2.getTransform();
			
			BufferedImage im = image();
			
			AffineTransform carTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
			carTrans.translate(realPos.getX(), realPos.getY());
			carTrans.rotate(heading);
			carTrans.translate(-MODEL.world.CAR_WIDTH / 2, -MODEL.world.CAR_WIDTH / 2);
			carTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
			
			g2.setTransform(carTrans);
			
			g2.drawImage(im,
					0,
					0,
					(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER), null);
			
			g2.setTransform(origTransform);
			
			break;
		}
		case CRASHED: {
			
			AffineTransform origTransform = g2.getTransform();
			
			BufferedImage im = VIEW.crash;
			
			AffineTransform carTrans = (AffineTransform)VIEW.worldToPanelTransform.clone();
			carTrans.translate(realPos.getX(), realPos.getY());
			carTrans.rotate(heading);
			carTrans.translate(-MODEL.world.CAR_WIDTH / 2, -MODEL.world.CAR_WIDTH / 2);
			carTrans.scale(1/((double)MODEL.world.PIXELS_PER_METER), 1/((double)MODEL.world.PIXELS_PER_METER));
			
			g2.setTransform(carTrans);
			
			g2.drawImage(im,
					0,
					0,
					(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER),
					(int)(MODEL.world.CAR_WIDTH * MODEL.world.PIXELS_PER_METER), null);
			
			g2.setTransform(origTransform);
			
			break;
		}
		case SINKED:
			break;
		}
		
	}
	
}
