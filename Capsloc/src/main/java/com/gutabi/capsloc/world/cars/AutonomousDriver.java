package com.gutabi.capsloc.world.cars;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.world.graph.GraphPositionPath;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;

public final class AutonomousDriver extends Driver {
	
	public static final double COMPLETE_STOP_WAIT_TIME = 0.0;
	
	public double carProximityLookahead;
	public double vertexArrivalLookahead;
	
//	double steeringLookaheadDistance = Car.length * 0.5;
	
	public VertexArrivalEvent curVertexArrivalEvent;
	public CarProximityEvent curCarProximityEvent;
	
	List<VertexEvent> vertexDepartureQueue = new ArrayList<VertexEvent>();
	
	double decelTime = -1;
	public double stoppedTime = -1;
	public boolean deadlocked;
	
	public AutonomousDriver(AutonomousCar c) {
		super(c);
	}
	
	public void computeStartingProperties() {
		
		overallPath = ((AutonomousCar)c).source.getShortestPathToMatch();
		
		overallPath.currentDrivers.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.add(this);
		}
		
		overallPos = overallPath.startPos;
		startGP = overallPos.gp;
		
		vertexDepartureQueue.add(new VertexSpawnEvent(overallPos));
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
					
				} else if (overallPos.combo >= e.carPastExitPosition.travelForward(c.length/2).combo) {
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
			double steeringLookaheadDistance = c.length * 0.5;
			GraphPositionPathPosition next = overallPos.travelForward(Math.min(steeringLookaheadDistance, overallPos.lengthToEndOfPath));
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
		default:
			assert false;
			break;
		}
	}
	
	public void postStep(double t) {
		
		setOverallPos(overallPos.forwardSearch(c.center, Double.POSITIVE_INFINITY));
		
	}
	
	private CarProximityEvent findNewCarProximityEvent() {
		AutonomousDriver driverProx = overallPath.driverProximityTest(overallPos, Math.min(carProximityLookahead, overallPos.lengthToEndOfPath));
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
				
				AutonomousDriver otherDriver = ((CarProximityEvent) e).otherDriver;
				
				if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME) {
					
					GraphPositionPathPosition otherPosition = null;
					if (otherDriver.overallPos != null) {
						otherPosition = overallPath.hitTest(otherDriver, overallPos);
					}
					
					if (otherPosition == null || DMath.greaterThan(overallPos.lengthTo(otherPosition), carProximityLookahead)) {
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
	
}
