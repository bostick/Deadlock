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
import com.gutabi.deadlock.core.path.STGraphPositionPathPosition;
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
	public CarStateEnum nextState;
	
	GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarStateEnum.RUNNING;
		nextState = null;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
		
		realPos = overallPath.getGraphPosition(0).getPoint();
		
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
		assert nextState == null;
		
		switch (state) {
		case RUNNING: {
			
			GraphPosition graphPos = MODEL.world.graph.hitTest(realPos);
			
			GraphPositionPathPosition overallPos = overallPath.hitTest(graphPos);
			
			STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.advanceOneTimeStep(overallPos, getSpeed() * MODEL.world.dt);
			
			STGraphPositionPathPosition last = nextPlannedPath.get(nextPlannedPath.size()-1);
			
//			logger.debug("last nextPath: " + last);
			
			nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
			
			if (last.getSpace().getGraphPosition() instanceof Sink) {
				nextState = CarStateEnum.SINKED;
			} else {
				nextState = CarStateEnum.RUNNING;
			}
			break;
		}
		case CRASHED: {
			
			GraphPosition graphPos = MODEL.world.graph.hitTest(realPos);
			
			GraphPositionPathPosition overallPos = overallPath.hitTest(graphPos);
			
			STGraphPositionPathPositionPath nextPlannedPath = STGraphPositionPathPositionPath.crashOneTimeStep(overallPos);
			
			nextRealPath = nextPlannedPath.toSTGraphPositionPath().toSTPointPath();
			
			nextState = CarStateEnum.CRASHED;
			break;
		}
		default:
			assert false;
		}
		
		
		return nextState == CarStateEnum.RUNNING || nextState == CarStateEnum.SINKED;
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
			
			state = nextState;
			
			nextRealPath = null;
			nextState = null;
			
			return state == CarStateEnum.RUNNING || state == CarStateEnum.CRASHED;
		}
		case CRASHED: {
			realPos = nextRealPath.end.getSpace();
			
			state = nextState;
			
			nextRealPath = null;
			nextState = null;
			
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
	
	public void nextRealPathCrash(double time) {
		nextRealPath = nextRealPath.crash(time);
	}
	
	abstract BufferedImage image();
	
	public void paint(Graphics2D g2) {
		
		switch (state) {
		case RUNNING: {
			AffineTransform origTransform = g2.getTransform();
			
			AffineTransform carTrans = (AffineTransform)origTransform.clone();
			carTrans.translate(realPos.getX(), realPos.getY());
			
			carTrans.rotate(heading);
			
			g2.setTransform(carTrans);
			
			BufferedImage im = image();
			
			int x = (int)(-im.getWidth()/2);
			int y = (int)(-im.getHeight()/2);
			g2.drawImage(im, x, y, null);
			
			g2.setTransform(origTransform);
			break;
		}
		case CRASHED: {
			AffineTransform origTransform = g2.getTransform();
			
			AffineTransform carTrans = (AffineTransform)origTransform.clone();
			carTrans.translate(realPos.getX(), realPos.getY());
			
			carTrans.rotate(heading);
			
			g2.setTransform(carTrans);
			
			BufferedImage im = VIEW.crash;
			
			int x = (int)(-im.getWidth()/2);
			int y = (int)(-im.getHeight()/2);
			g2.drawImage(im, x, y, null);
			
			g2.setTransform(origTransform);
			break;
		}
		case SINKED:
			break;
		}
		
	}
	
}
