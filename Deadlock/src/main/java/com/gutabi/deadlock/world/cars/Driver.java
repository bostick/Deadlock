package com.gutabi.deadlock.world.cars;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;

//@SuppressWarnings("static-access")
public class Driver {
	
	public static final double COMPLETE_STOP_WAIT_TIME = 0.0;
	
	double carProximityLookahead;
	double vertexArrivalLookahead;
	
	double steeringLookaheadDistance = Car.CAR_LENGTH * 0.5;
	
	public final Car c;
	
	public GraphPositionPath overallPath;
	
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
		
		if (c.getMaxSpeed() == 2.5) {
			carProximityLookahead = 2.0;
		} else if (c.getMaxSpeed() == 5.0) {
			carProximityLookahead = 2.0;
		} else if (c.getMaxSpeed() == 10.0) {
			carProximityLookahead = 2.25;
		} else {
			assert false;
		}
		
		if (c.getMaxSpeed() == 2.5) {
			vertexArrivalLookahead = 0.95;
		} else if (c.getMaxSpeed() == 5.0) {
			vertexArrivalLookahead = 1.30;
		} else if (c.getMaxSpeed() == 10.0) {
			vertexArrivalLookahead = 2.00;
		} else {
			assert false;
		}
		
	}
	
	public void computeStartingProperties() {
		
		computePath();
		
		overallPos = overallPath.startingPos;
		
		vertexDepartureQueue.add(new VertexSpawnEvent(overallPos));
		c.source.driverQueue.add(this);
	}
	
	private void computePath() {
		
		overallPath = c.source.getShortestPathToMatch();
		
		overallPath.currentDrivers.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.add(this);
		}
		
	}
	
	public void computeDynamicPropertiesMoving() {
		overallPos = overallPath.findClosestGraphPositionPathPosition(c.p, overallPos);
	}
	
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
					
				} else if (overallPos.combo >= e.carPastExitPosition.combo) {
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
