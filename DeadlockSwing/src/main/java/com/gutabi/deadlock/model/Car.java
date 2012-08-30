package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Direction;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;

public class Car {
	
	private CarState state;
	
	private Position pos;
	
	public long startingStep;
	public Source source;
	
	public Path nextPath;
	public CarState nextState;
	public Edge nextEdge;
	public int nextDir;
	public Edge previousEdge;
	
	public double nextDistanceToMove;
	
	public final int id;
	
	String s;
	
	public static int carCounter;
	
	public Car() {
		state = CarState.NEW;
		id = carCounter;
		carCounter++;
		
		nextPath = new Path();
		
		s = "car " + id;
	}
	
	public void updateNext() {
		
		nextDistanceToMove = MODEL.DISTANCE_PER_TIMESTEP;
		
		inner:
		while (true) {
			
			switch (nextState) {
			case EDGE: {
				Position pos = getLastNextPosition();
				Edge e = nextEdge;
				int dir = nextDir;
				double distanceLeftOnEdge;
				if (pos instanceof EdgePosition) {
					distanceLeftOnEdge = (dir == 1) ? ((EdgePosition)pos).distanceToEndOfEdge() : ((EdgePosition)pos).distanceToStartOfEdge();
				} else {
					distanceLeftOnEdge = e.getTotalLength();
				}
				
				Position nextPos;
				if (pos instanceof EdgePosition) {
					nextPos = ((EdgePosition)pos).travel(dir, Math.min(nextDistanceToMove, distanceLeftOnEdge));
				} else {
					nextPos = ((VertexPosition)pos).travel(e, dir, Math.min(nextDistanceToMove, distanceLeftOnEdge));
				}
				nextPathAdd(nextPos);
				
				if (DMath.doubleEquals(nextDistanceToMove, distanceLeftOnEdge)) {
					
					previousEdge = e;
					Vertex v = (dir == 1) ? e.getEnd() : e.getStart();
					nextState = CarState.VERTEX;
					
					assert getLastNextPosition() instanceof VertexPosition;
					
					nextDistanceToMove = 0.0;
					
					if (v instanceof Sink) {
						nextState = CarState.SINKED;
					}
					
				} else if (nextDistanceToMove > distanceLeftOnEdge) {
					
					previousEdge = e;
					Vertex v = (dir == 1) ? e.getEnd() : e.getStart();
					nextState = CarState.VERTEX;
					
					assert getLastNextPosition() instanceof VertexPosition;
					
					nextDistanceToMove -= distanceLeftOnEdge;
					
					if (v instanceof Sink) {
						nextState = CarState.SINKED;
						nextDistanceToMove = 0.0;
					}
					
				} else {
					nextDistanceToMove = 0.0;
				}
				
				break;
			}
			case VERTEX: {
				VertexPosition pos = (VertexPosition)getLastNextPosition();
				nextChoice(pos);
				break;
			}
			case CRASHED:
				nextDistanceToMove = 0.0;
				break;
			case SINKED:
				nextDistanceToMove = 0.0;
				break;
			case NEW:
				assert false;
			}
			
			if (nextDistanceToMove == 0.0) {
				break inner;
			}
			
		} // end inner loop
		
		assert nextState == CarState.SINKED || DMath.doubleEquals(nextPath.totalLength(), MODEL.DISTANCE_PER_TIMESTEP);
		
	}
	
	private void nextChoice(Position pos) {
		nextChoiceRandom(pos);
	}
	
	private void nextChoiceRandom(Position pos) {
		if (pos instanceof VertexPosition) {
			
			VertexPosition vp = (VertexPosition)pos;
			
			Vertex v = vp.getVertex();
			List<Edge> eds = new ArrayList<Edge>(v.getEdges());
			
			if (eds.size() > 1 && previousEdge != null) {
				/*
				 * don't go back the same way
				 */
				eds.remove(previousEdge);
			}
			
			int i = MODEL.RANDOM.nextInt(eds.size());
			nextEdge = eds.get(i);
			if (nextEdge.isLoop()) {
				/*
				 * even though a loop is still registered as 2 edges in eds, tehre is no direction information encoded
				 * The edge e is simply in eds twice.
				 * Si if e is a loop, take this extra step to pick a direction
				 */
				nextDir = 2*MODEL.RANDOM.nextInt(2)-1;
			} else {
				nextDir = (nextEdge.getStart() == v) ? 1 : -1;
			}
			nextState = CarState.EDGE;
			
		} else {
			assert false;
		}
	}
	
	private void nextChoiceToSink(Position pos) {
		if (pos instanceof VertexPosition) {
			
			Direction goal = source.getDirection().opposite();
			List<Sink> sinks = new ArrayList<Sink>();
			for (Sink s : MODEL.getSinks()) {
				if (s.getDirection() == goal) {
					sinks.add(s);
				}
			}
			
			VertexPosition vp = (VertexPosition)pos;
			
			Vertex v = vp.getVertex();
			
			Edge best = null;
			double bestDistance = Double.POSITIVE_INFINITY;
			for (Edge e : v.getEdges()) {
				for (Sink s : MODEL.getSinks()) {
					//distance
				}
			}
			
			List<Edge> eds = new ArrayList<Edge>(v.getEdges());
			
			int i = MODEL.RANDOM.nextInt(eds.size());
			nextEdge = eds.get(i);
			if (nextEdge.isLoop()) {
				nextDir = 2*MODEL.RANDOM.nextInt(2)-1;
			} else {
				nextDir = (nextEdge.getStart() == v) ? 1 : -1;
			}
			nextState = CarState.EDGE;
			
		} else {
			assert false;
		}
	}
	
	
	public void updateCurrentFromNext() {
		Position p = getLastNextPosition();
		setPosition(p);
		nextPathClear();
		nextPathAdd(p);
		CarState s = nextState;
		setState(s);
	}
	
	public Car copy() {
		Car c = new Car();
		c.pos = getPosition();
		return c;
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
	
	public void nextPathAdd(Position pos) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		nextPath.add(pos);
	}
	
	/**
	 * Remove the rest of the edge after pIndex (and all other edges after eIndex) and replace the last position
	 * with pos (if they are not already equal)
	 * 
	 */
	public void nextPathCrash(Position pos, int pIndex) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		nextPath.crash(pos, pIndex);
	}
	
	public void nextPathClear() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		nextPath.clear();
	}
	
	public List<Position> getNextPath() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		return nextPath.path;
	}
	
	public Position getLastNextPosition() {
		return nextPath.getLastPosition();
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
	
}
