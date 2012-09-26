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
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.PathPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.STPath;
import com.gutabi.deadlock.core.STPosition;
import com.gutabi.deadlock.core.Source;

public abstract class Car {
	
	protected CarState state;
	
	PathPosition pos;
	
	private Point prevPoint;
	
	public long startingStep;
	public long crashingStep;
	public Source source;
	
	public STPath nextPath;
	public CarState nextState;
	
	Path overallPath;
	
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
		
		pos = new PathPosition(overallPath, 0, 0.0);
		
	}
	
	public abstract boolean updateNext();
	
	public boolean updateCurrentFromNext() {

		prevPoint = pos.getGraphPosition().getPoint();
		
		STPosition first = nextPath.get(0);
		STPosition last = nextPath.get(nextPath.size()-1);
		
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
	
	public STPath getNextPath() {
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
			
			int x = (int)(p.getX()-MODEL.CAR_WIDTH/2);
			int y = (int)(p.getY()-MODEL.CAR_WIDTH/2);
			g2.drawImage(VIEW.wreck, x, y, null);
			
		} else {
			assert false;
		}
		
	}
	
}
