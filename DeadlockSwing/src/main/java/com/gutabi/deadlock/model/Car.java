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

@SuppressWarnings("serial")
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
		s = "car " + id;
	}
	
	public boolean updateNext() {
		
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
				boolean moving = nextChoice(pos);
				if (!moving) {
					nextDistanceToMove = 0.0;
				}
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
		
		return !(nextState == CarState.CRASHED || nextState == CarState.SINKED);
	}
	
	private boolean nextChoice(Position pos) {
		//nextChoiceRandom(pos);
		return nextChoiceToSink(pos);
	}
	
	private boolean nextChoiceRandom(Position pos) {
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
		
		return true;
	}
	
	/*
	 * choose best edge to get to the correct sink
	 */
	private boolean nextChoiceToSink(Position pos) {
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
			
			Path bestPath = null;
			double bestDistance = Double.POSITIVE_INFINITY;
			for (Sink s : MODEL.getSinks()) {
				//VertexPosition sp = new VertexPosition(s, null, null, 0);
				Path path = MODEL.shortestPath(v, s);
				if (path != null) {
					if (path.totalLength() < bestDistance) {
						bestDistance = path.totalLength();
						bestPath = path; 
					}
				}
			}
			
			if (bestPath != null) {
				
				assert bestPath.get(0).equals(vp);
				VertexPosition nextVP = (VertexPosition)bestPath.get(1);
				
				nextEdge = nextVP.prevDirEdge;
				nextDir = nextVP.prevDir;
				
				nextState = CarState.EDGE;
				
			} else {
				// no way to get to any sink, crash self now
				nextState = CarState.CRASHED;
			}
			
		} else {
			assert false;
		}
		
		return nextState != CarState.CRASHED;
	}
	
	
	public void updateCurrentFromNext() {
		final Position p = getLastNextPosition();
		setPosition(p);
		nextPath = new Path(new ArrayList<Position>(){{add(p);}});
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
		nextPath = nextPath.append(pos);
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
		
		nextPath = nextPath.crash(pos, pIndex);
		nextState = CarState.CRASHED;
	}
	
	public Path getNextPath() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		return nextPath;
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
