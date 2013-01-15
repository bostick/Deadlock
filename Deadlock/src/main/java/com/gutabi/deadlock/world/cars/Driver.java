package com.gutabi.deadlock.world.cars;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RushHourStud;
import com.gutabi.deadlock.world.graph.Side;

public class Driver {
	
	public static final double COMPLETE_STOP_WAIT_TIME = 0.0;
	
	public double carProximityLookahead;
	public double vertexArrivalLookahead;
	
//	double steeringLookaheadDistance = Car.CAR_LENGTH * 0.5;
	
	public final Car c;
	
	public GraphPositionPath overallPath;
	
	public Side overallSide;
	public GraphPositionPathPosition overallPos;
	
	public VertexArrivalEvent curVertexArrivalEvent;
	public CarProximityEvent curCarProximityEvent;
	
	List<VertexEvent> vertexDepartureQueue = new ArrayList<VertexEvent>();
	
	double decelTime = -1;
	public double stoppedTime = -1;
	public boolean deadlocked;
	
	Point goalPoint;
	
	public Driver(Car c) {
		this.c = c;
	}
	
	public void computeStartingProperties() {
		
		computePath();
		
		overallPath.currentDrivers.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.add(this);
		}
		
		overallPos = overallPath.startingPos;
		
		vertexDepartureQueue.add(new VertexSpawnEvent(overallPos));
	}
	
	public void computePath() {
		overallPath = c.source.getShortestPathToMatch();
	}
	
	public void computeDynamicPropertiesMoving() {
		overallPos = overallPath.findClosestGraphPositionPathPosition(centerToGPPPPoint(c.p), overallPos, true);
	}
	
	public Point gpppPointToCenter(Point gppp) {
		
		if (overallSide == null) {
			return gppp;
		}
		
		switch (overallSide) {
		case TOP:
			return gppp.plus(new Point(c.CAR_WIDTH/2 * RushHourStud.SIZE, c.CAR_LENGTH/2 * RushHourStud.SIZE));
		case LEFT:
			return gppp.plus(new Point(c.CAR_LENGTH/2 * RushHourStud.SIZE, c.CAR_WIDTH/2 * RushHourStud.SIZE));
		case RIGHT:
			return gppp.plus(new Point((c.CAR_LENGTH/2 - 1) * RushHourStud.SIZE, c.CAR_WIDTH/2 * RushHourStud.SIZE));
		case BOTTOM:
			return gppp.plus(new Point(c.CAR_WIDTH/2 * RushHourStud.SIZE, (c.CAR_LENGTH/2 - 1) * RushHourStud.SIZE));
		}
		return null;
	}
	
	public Point centerToGPPPPoint(Point center) {
		
		if (overallSide == null) {
			return center;
		}
		
		switch (overallSide) {
		case TOP:
			return center.minus(new Point(c.CAR_WIDTH/2 * RushHourStud.SIZE, c.CAR_LENGTH/2 * RushHourStud.SIZE));
		case LEFT:
			return center.minus(new Point(c.CAR_LENGTH/2 * RushHourStud.SIZE, c.CAR_WIDTH/2 * RushHourStud.SIZE));
		case RIGHT:
			return center.minus(new Point((c.CAR_LENGTH/2 - 1) * RushHourStud.SIZE, c.CAR_WIDTH/2 * RushHourStud.SIZE));
		case BOTTOM:
			return center.minus(new Point(c.CAR_WIDTH/2 * RushHourStud.SIZE, (c.CAR_LENGTH/2 - 1) * RushHourStud.SIZE));
		}
		return null;
	}
	
//	public Point gpppToCenter(GraphPositionPathPosition gppp) {
//		
//		GraphPosition gpos = gppp.getGraphPosition();
//		
//		Point p = null;
//		if (gpos instanceof VertexPosition) {
//			
//			p = gppp.p;
//			
//		} else if (gpos instanceof EdgePosition) {
//			
//			p = gppp.p;
//			
//		} else if (gpos instanceof RushHourBoardPosition) {
//			
//			
//			
//		}
//		
//		return p;
//	}
//	
//	public GraphPositionPathPosition centerToGPPP(Point p) {
//		
//		return p;
//	}
	
	public void clear() {
		
		overallPos = null;
		
		overallPath.currentDrivers.remove(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.remove(this);
		}
		
		deadlocked = false;
		curVertexArrivalEvent = null;
		curCarProximityEvent = null;
		
		stoppedTime = -1;
		decelTime = -1;
		
		cleanupVertexDepartureQueue();
	}
	
	public void preStep(double t) {
		
		if (curCarProximityEvent == null) {
			curCarProximityEvent = findNewCarProximityEvent();
		}
		
		if (stoppedTime == -1 && curVertexArrivalEvent == null) {
			curVertexArrivalEvent = findNewVertexArrivalEvent();
			/*
			 * since stopped, we know cannot have a new vertex arrival event
			 */
		}
		
		if (curCarProximityEvent != null) {
			boolean persist = handleDrivingEvent(curCarProximityEvent, t);
			if (!persist) {
				curCarProximityEvent = null;
			}
		}
		
		if (curVertexArrivalEvent != null) {
			boolean persist = handleDrivingEvent(curVertexArrivalEvent, t);
			if (!persist) {
				curVertexArrivalEvent = null;
			}
		}
		
		if (curCarProximityEvent == null && curVertexArrivalEvent == null) {
			
			c.state = CarStateEnum.DRIVING;
			
			stoppedTime = -1;
			deadlocked = false;
			decelTime = -1;
		}
		
		cleanupVertexDepartureQueue();
		
		switch (c.state) {
		case DRIVING:
			double steeringLookaheadDistance = c.CAR_LENGTH * 0.5;
			GraphPositionPathPosition next = overallPos.travel(Math.min(steeringLookaheadDistance, overallPos.lengthToEndOfPath));
			goalPoint = next.p;
			break;
		case BRAKING:
			goalPoint = null;
			break;
		case CRASHED:
			break;
		case SKIDDED:
			break;
		case SINKED:
			break;
		case IDLE:
			break;
		}
	}
	
	private CarProximityEvent findNewCarProximityEvent() {
		Driver driverProx = overallPath.driverProximityTest(overallPos, Math.min(carProximityLookahead, overallPos.lengthToEndOfPath));
		if (driverProx != null) {
			return new CarProximityEvent(this, driverProx);
		} else {
			return null;
		}
	}
	
	private VertexArrivalEvent findNewVertexArrivalEvent() {
		
		VertexArrivalEvent vertexArr = overallPath.vertexArrivalTest(this, Math.min(vertexArrivalLookahead, overallPos.lengthToEndOfPath));
		if (vertexArr != null) {
			
			if (!vertexDepartureQueue.contains(vertexArr)) {
				vertexDepartureQueue.add(vertexArr);
				
				assert !vertexArr.v.driverQueue.contains(this);
				vertexArr.v.driverQueue.add(this);
				
				return vertexArr;
			}
			
		}
		
		return null;
		
	}
	
	private boolean handleDrivingEvent(DrivingEvent e, double t) {
		
		if (e instanceof CarProximityEvent) {
			
			if (decelTime == -1) {
				// start braking
				
				c.state = CarStateEnum.BRAKING;
				
			} else {
				
				Driver otherDriver = ((CarProximityEvent) e).otherDriver;
				
				if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME) {
					
					GraphPositionPathPosition otherPosition = null;
					if (otherDriver.overallPos != null) {
						otherPosition = overallPath.hitTest(otherDriver, overallPos);
					}
					
					if (otherPosition == null || DMath.greaterThan(overallPos.distanceTo(otherPosition), carProximityLookahead)) {
						return false;
					}
					
				}
				
			}
			
		} else if (e instanceof VertexArrivalEvent) {
			
			if (((VertexArrivalEvent)e).sign.isEnabled()) {
				if (decelTime == -1) {
					c.state = CarStateEnum.BRAKING;					
				} else if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME && ((VertexArrivalEvent)e).v.driverQueue.get(0) == this) {
					return false;
					
				}
			} else {
				
				/*
				 * could have simply been passing through, so never braking
				 * or could have ben already had CarProximityEvent, it went away and was masking a VertexArrivalevent, so immediately go to that
				 * so already braking without a stop sign
				 * 
				 * all that matters now is to be driving
				 */
				return false;
				
			}
			
		} else {
			assert false;
		}
		
		return true;
	}
	
	private void cleanupVertexDepartureQueue() {
		
		if (!vertexDepartureQueue.isEmpty()) {
			
			List<VertexEvent> toRemove = new ArrayList<VertexEvent>();
			for (VertexEvent e : vertexDepartureQueue) {
				if (overallPos == null) {
					/*
					 * crashed or skidded
					 */
					
					e.v.driverQueue.remove(this);
					
					toRemove.add(e);
					
				} else if (e.carPastExitPosition == null) {
					/*
					 * at sink
					 */
					
					e.v.driverQueue.remove(this);
					
					toRemove.add(e);
					
				} else if (overallPos.combo >= e.carPastExitPosition.travel(c.CAR_LENGTH/2).combo) {
					/*
					 * driving past exit of intersection, so cleanup
					 */
					
					/*
					 * sign may not be enabled, so this car may be further up in queue
					 */
					//assert e.v.carQueue.indexOf(this) == 0;
					
					e.v.driverQueue.remove(this);
					
					toRemove.add(e);
					
				}
			}
			for (VertexEvent e : toRemove) {
				vertexDepartureQueue.remove(e);
			}
		}
		
	}
	
}
