package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.core.path.GraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPosition;
import com.gutabi.deadlock.core.path.STGraphPositionPathPositionPath;

public abstract class Car {
	
	protected CarState state;
	
	GraphPositionPathPosition pos;
	
	private Point prevPoint;
	
	public long startingStep;
	public long crashingStep;
	public Source source;
	
	public STGraphPositionPathPositionPath nextPath;
	public CarState nextState;
	
	GraphPositionPath overallPath;
	
	public final int id;
	
	public static int carCounter;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarState.RUNNING;
		nextState = null;
		
		source = s;
		
		overallPath = s.getPathToMatchingSink();
		
		pos = new GraphPositionPathPosition(overallPath, 0, 0.0);
		
	}
	
	public abstract double getSpeed();
	
	/**
	 * Returns true if car moved in this update
	 */
	public boolean updateNext() {
		
		assert nextPath == null;
		assert nextState == null;
		
		switch (state) {
		case RUNNING:
			
			nextPath = STGraphPositionPathPositionPath.advanceOneTimeStep(pos, getSpeed());
			
			STGraphPositionPathPosition last = nextPath.get(nextPath.size()-1);
			
			logger.debug("last nextPath: " + last);
			
			if (last.getSpace().getGraphPosition() instanceof Sink) {
				nextState = CarState.SINKED;
			} else {
				nextState = CarState.RUNNING;
			}
			break;
		case CRASHED:
			nextPath = STGraphPositionPathPositionPath.crashOneTimeStep(pos);
			nextState = CarState.CRASHED;
			break;
		default:
			assert false;
		}
		
		
		return nextState == CarState.RUNNING || nextState == CarState.SINKED;
	}
	
	public boolean updateCurrentFromNext() {

		prevPoint = pos.getPoint();
		
		STGraphPositionPathPosition first = nextPath.get(0);
		STGraphPositionPathPosition last = nextPath.get(nextPath.size()-1);
		
		assert DMath.equals(first.getTime(), 0.0);
		assert DMath.equals(last.getTime(), 1.0);
		
		pos = last.getSpace();
		
		if (pos.getGraphPosition() instanceof EdgePosition) {
			Edge e = ((EdgePosition)pos.getGraphPosition()).getEdge();
			assert !e.isRemoved();
		}
		
		CarState s = nextState;
		
		nextPath = null;
		nextState = null;
		
		state = s;
		return s == CarState.RUNNING || s == CarState.CRASHED;
	}
	
	public int getId() {
		return id;
	}
	
	public GraphPosition getPosition() {
		return pos.getGraphPosition();
	}
	
	public Point getPreviousPoint() {
		return prevPoint;
	}
	
	/**
	 * Remove the rest of the edge after pIndex (and all other edges after eIndex) and replace the last position
	 * with pos (if they are not already equal)
	 * 
	 */
	public void nextPathCrash(double time) {
		nextPath = nextPath.crash(time);
	}
	
//	public void nextPathSynchronizeX(double time) {
//		nextPath = nextPath.synchronize(time);
//	}
	
	public STGraphPositionPathPositionPath getNextPath() {
		return nextPath;
	}
	
	abstract BufferedImage image();
	
	public void paint(Graphics2D g2) {
		
		if (state == CarState.RUNNING) {
			
			AffineTransform origTransform = g2.getTransform();
			
			Point p = pos.getPoint();
			
			AffineTransform carTrans = (AffineTransform)origTransform.clone();
			carTrans.translate(p.getX(), p.getY());
			
			double rad = 0;
			if (prevPoint != null) {
				rad = Math.atan2(p.getY()-prevPoint.getY(), p.getX()-prevPoint.getX());
			}
			
			carTrans.rotate(rad);
			
			g2.setTransform(carTrans);
			
			BufferedImage im = image();
			
			int x = (int)(-im.getWidth()/2);
			int y = (int)(-im.getHeight()/2);
			g2.drawImage(im, x, y, null);
			
			g2.setTransform(origTransform);
			
		} else if (state == CarState.CRASHED) {
			
			Point p = pos.getPoint();
			
			int x = (int)(p.getX()-MODEL.world.CAR_WIDTH/2);
			int y = (int)(p.getY()-MODEL.world.CAR_WIDTH/2);
			g2.drawImage(VIEW.wreck, x, y, null);
			
		} else {
			assert false;
		}
		
	}
	
}
