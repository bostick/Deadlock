package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.IntersectionPosition;
import com.gutabi.deadlock.core.IntersectionType;

public class Car {
	
	private CarState state;
	
	private Position pos;
	
	public long startingStep;
	public Intersection startingIntersection;
	
	public Path futurePath;
	
	public CarState futureState;
	
	public Edge futureEdge;
	public int futureDir;
	public Edge previousEdge;
	
	public double distanceToMove;
	
	public final int id;
	
	String s;
	
	public static int carCounter;
	
	public Car() {
		state = CarState.NEW;
		id = carCounter;
		carCounter++;
		
		futurePath = new Path();
		
		s = "car " + id;
	}
	
	public String toString() {
		return s;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public void setPosition(Position pos) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		this.pos = pos;
	}
	
	public void futurePathAdd(Position pos) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		futurePath.add(pos);
	}
	
	/**
	 * Remove the rest of the edge after pIndex (and all other edges after eIndex) and replace the last position
	 * with pos (if they are not already equal)
	 * 
	 */
	public void futurePathCrash(Position pos, int pIndex) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		futurePath.crash(pos, pIndex);
	}
	
	public void futurePathClear() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		futurePath.clear();
	}
	
	public List<Position> getFuturePath() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		return futurePath.path;
	}
	
	public Position getLastFuturePosition() {
		return futurePath.getLastPosition();
	}
	
	public CarState getState() {
		return state;
	}
	
	public void setState(CarState s) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		this.state = s;
	}
	
	public void calculateFuturePath() {
		
		distanceToMove = MODEL.DISTANCE_PER_TIMESTEP;
		
		inner:
		while (true) {
			
			switch (futureState) {
			case EDGE: {
				Position pos = getLastFuturePosition();
				Edge e = futureEdge;
				int dir = futureDir;
				double distanceLeftOnEdge;
				if (pos instanceof EdgePosition) {
					distanceLeftOnEdge = (dir == 1) ? ((EdgePosition)pos).distanceToEndOfEdge() : ((EdgePosition)pos).distanceToStartOfEdge();
				} else {
					distanceLeftOnEdge = e.getTotalLength();
				}
				
				Position nextPos;
				if (pos instanceof EdgePosition) {
					nextPos = ((EdgePosition)pos).travel(dir, Math.min(distanceToMove, distanceLeftOnEdge));
				} else {
					nextPos = ((IntersectionPosition)pos).travel(e, dir, Math.min(distanceToMove, distanceLeftOnEdge));
				}
				futurePathAdd(nextPos);
				
				if (DMath.doubleEquals(distanceToMove, distanceLeftOnEdge)) {
					
					previousEdge = e;
					Intersection v = e.getEnd();
					futureState = CarState.INTERSECTION;
					
					assert getLastFuturePosition() instanceof IntersectionPosition;
					
					distanceToMove = 0.0;
					
					if (v.getType() == IntersectionType.SINK) {
						futureState = CarState.SINKED;
					}
					
				} else if (distanceToMove > distanceLeftOnEdge) {
					
					previousEdge = e;
					Intersection v = e.getEnd();
					futureState = CarState.INTERSECTION;
					
					assert getLastFuturePosition() instanceof IntersectionPosition;
					
					distanceToMove -= distanceLeftOnEdge;
					
					if (v.getType() == IntersectionType.SINK) {
						futureState = CarState.SINKED;
						distanceToMove = 0.0;
					}
					
				} else {
					distanceToMove = 0.0;
				}
				
				break;
			}
			case INTERSECTION: {
				IntersectionPosition pos = (IntersectionPosition)getLastFuturePosition();
				Intersection v = pos.getIntersection();
				List<Edge> eds = new ArrayList<Edge>(v.getEdges());
				
//				Edge previousEdge = null;
//				List<Position> path = getFuturePath();
//				if (path.size() > 1) {
//					EdgePosition ep = (EdgePosition)path.get(path.size()-2);
//					previousEdge = ep.getEdge();
//				}
				
				if (eds.size() > 1 && previousEdge != null) {
					/*
					 * don't go back the same way
					 */
					eds.remove(previousEdge);
				}
				
				int i = MODEL.RANDOM.nextInt(eds.size());
				futureEdge = eds.get(i);
				if (futureEdge.isLoop()) {
					futureDir = 2*MODEL.RANDOM.nextInt(2)-1;
				} else {
					futureDir = (futureEdge.getStart() == v) ? 1 : -1;
				}
				futureState = CarState.EDGE;
				break;
			}
			case CRASHED:
				distanceToMove = 0.0;
				break;
			case SINKED:
				distanceToMove = 0.0;
				break;
			case NEW:
				assert false;
			}
			
			if (distanceToMove == 0.0) {
				break inner;
			}
			
		} // end inner loop
		
		assert futureState == CarState.SINKED || DMath.doubleEquals(futurePath.totalLength(), MODEL.DISTANCE_PER_TIMESTEP);
		
	}
	
	public Car copy() {
		Car c = new Car();
		c.pos = getPosition();
		return c;
	}
	
}
