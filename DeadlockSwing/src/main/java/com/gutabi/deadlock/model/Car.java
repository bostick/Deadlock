package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.PathPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.STPath;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;

public class Car {
	
	public double DISTANCE_PER_TIMESTEP = 10;
	
	private CarState state;
	
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
	
	/**
	 * Returns true if car moved in this update
	 */
	public boolean updateNext() {
		
		assert nextPath == null;
		assert nextState == null;
		
		switch (state) {
		case RUNNING:
			
			
			
			nextPath = STPath.advanceOneTimeStep(pos, DISTANCE_PER_TIMESTEP);
			
			logger.debug("last nextPath: " + nextPath.getLastPosition());
			
			if (nextPath.getLastPosition().getSpace().getGraphPosition() instanceof Sink) {
				nextState = CarState.SINKED;
			} else {
				nextState = CarState.RUNNING;
			}
			break;
		case CRASHED:
			nextPath = STPath.crashOneTimeStep(pos);
			nextState = CarState.CRASHED;
			break;
		default:
			assert false;
		}
		
		
		return nextState == CarState.RUNNING || nextState == CarState.SINKED;
	}
	
	public boolean updateCurrentFromNext() {

		prevPoint = pos.getGraphPosition().getPoint();
		
		pos = nextPath.get(nextPath.size()-1).getSpace();
		
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
	
	public void nextPathSynchronize(double time) {
		nextPath = nextPath.synchronize(time);
	}
	
	public STPath getNextPath() {
		return nextPath;
	}
	
}
