package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.PathPosition;
import com.gutabi.deadlock.core.STPath;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;

public class Car {
	
	private CarState state;
	
	//private Position pos;
	PathPosition pos;
	
	private GraphPosition prevPos;
	
	public long startingStep;
	public long crashingStep;
	public Source source;
	
	public STPath nextPath;
	//public PathPosition nextPos;
	public CarState nextState;
//	public Edge nextEdge;
//	public Vertex nextDest;
//	public Edge previousEdge;
	
	Path overallPath;
	
//	public double nextDistanceToMove;
	
	public final int id;
	
	public static int carCounter;
	
	static Logger logger = Logger.getLogger(Car.class);
	
	public Car(Source s) {
		
		id = carCounter;
		carCounter++;
		
		state = CarState.RUNNING;
		nextState = null;
		
//		state = CarState.VERTEX;
//		nextState = CarState.VERTEX;
		
		source = s;
//		pos = s;
//		pathIndex = 0;
//		pathParam = 0.0;
		
		overallPath = s.getPathToClosestSink();
		
		pos = new PathPosition(overallPath, 0, 0.0);
		
	}
	
	/**
	 * Returns true if car moved in this update
	 */
	public boolean updateNext() {
		
		assert nextPath == null;
//		assert nextPos == null;
		assert nextState == null;
		
//		nextDistanceToMove = MODEL.DISTANCE_PER_TIMESTEP;
		
		// find pos on overallPath
		// travel MODEL.DISTANCE_PER_TIMESTEP forward
		// set nextPath to that new chunk, with time running from 0 to 1
		// handle being SINKED
		
		switch (state) {
		case RUNNING:
			nextPath = STPath.advanceOneTimeStep(pos, MODEL.DISTANCE_PER_TIMESTEP);
			
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
	
//	private boolean nextChoice(Position pos) {
//		//nextChoiceRandom(pos);
//		return nextChoiceToSink(pos);
//	}
	
//	private boolean nextChoiceRandom(Position pos) {
//		if (pos instanceof VertexPosition) {
//			
//			VertexPosition vp = (VertexPosition)pos;
//			
//			Vertex v = vp.getVertex();
//			List<Edge> eds = new ArrayList<Edge>(v.getEdges());
//			
//			if (eds.size() > 1 && previousEdge != null) {
//				/*
//				 * don't go back the same way
//				 */
//				eds.remove(previousEdge);
//			}
//			
//			int i = MODEL.RANDOM.nextInt(eds.size());
//			nextEdge = eds.get(i);
//			if (nextEdge.isLoop()) {
//				/*
//				 * even though a loop is still registered as 2 edges in eds, tehre is no direction information encoded
//				 * The edge e is simply in eds twice.
//				 * Si if e is a loop, take this extra step to pick a direction
//				 */
//				nextDir = 2*MODEL.RANDOM.nextInt(2)-1;
//			} else {
//				nextDir = (nextEdge.getStart() == v) ? 1 : -1;
//			}
//			
//			nextState = CarState.EDGE;
//			
//		} else {
//			assert false;
//		}
//		
//		return true;
//	}
	
	/*
	 * choose best edge to get to the correct sink
	 */
//	private boolean nextChoiceToSink(Position pos) {
//		if (pos instanceof Vertex) {
//			
//			Direction goal = source.getDirection().opposite();
//			List<Sink> sinks = new ArrayList<Sink>();
//			for (Sink s : MODEL.getSinks()) {
//				if (s.getDirection() == goal) {
//					sinks.add(s);
//				}
//			}
//			
//			Vertex v = (Vertex)pos;
//			
//			Vertex bestChoice = null;
//			
//			double bestDistance = Double.POSITIVE_INFINITY;
//			for (Sink s : MODEL.getSinks()) {
//				Vertex choice = MODEL.shortestPathChoice(v, s);
//				if (choice != null) {
//					double dist = v.distanceTo(choice) + choice.distanceTo(s);
//					if (dist < bestDistance) {
//						bestDistance = dist;
//						bestChoice = choice;
//					}
//				}
//			}
//			
//			if (bestChoice != null) {
//				
//				Edge bestEdge = null;
//				List<Connector> cons = Vertex.commonConnectors(v, bestChoice);
//				for (Connector c : cons) {
//					Edge e = (Edge)c;
//					if (bestEdge == null || e.getTotalLength() < bestEdge.getTotalLength()) {
//						bestEdge = e;
//					}
//				}
//				
//				nextEdge = bestEdge;
//				
//				nextDest = (v == nextEdge.getStart()) ? nextEdge.getEnd() : nextEdge.getStart();
//				
//				nextState = CarState.EDGE;
//				
//			} else {
//				// no way to get to any sink, crash self now
//				nextState = CarState.CRASHED;
//			}
//			
//		} else {
//			assert false;
//		}
//		
//		return nextState != CarState.CRASHED;
//	}
	
	
	public boolean updateCurrentFromNext() {
		
//		Position p = overallPath.getPosition(pathIndex, pathParam);

		prevPos = pos.getGraphPosition();
//		pos = p;
		
//		pathIndex =;
//		pathParam =;
		pos = nextPath.get(nextPath.size()-1).getSpace();
		
		CarState s = nextState;
		
		nextPath = null;
		nextState = null;
		
		state = s;
		return s == CarState.RUNNING || s == CarState.CRASHED;
	}
	
//	public Car copy() {
//		Car c = new Car();
//		c.pos = getPosition();
//		return c;
//	}
	
	public int getId() {
		return id;
	}
	
	public GraphPosition getPosition() {
		return pos.getGraphPosition();
	}
	
	public GraphPosition getPreviousPosition() {
		return prevPos;
	}
	
//	public void setPosition(Position pos) {
//		this.prevPos = this.pos;
//		this.pos = pos;
//	}
	
//	public void nextPathAdd(STPosition pos) {
//		if (nextPath == null) {
//			List<STPosition> poss = new ArrayList<STPosition>();
//			poss.add(new STPosition(this.pos, 0.0));
//			poss.add(pos);
//			nextPath = new STPath(poss);
//		} else {
//			nextPath = nextPath.append(pos);
//		}
//	}
	
	/**
	 * Remove the rest of the edge after pIndex (and all other edges after eIndex) and replace the last position
	 * with pos (if they are not already equal)
	 * 
	 */
	public void nextPathCrash(double time) {
//		if (state == CarState.CRASHED || state == CarState.SINKED) {
//			throw new IllegalArgumentException();
//		}
		
		nextPath = nextPath.crash(time);
	}
	
	public void nextPathSynchronize(double time) {
		nextPath = nextPath.synchronize(time);
	}
	
	public STPath getNextPath() {
		return nextPath;
	}
	
//	private Position getLastNextPosition() {
//		return nextPath.getLastPosition().getSpace();
//	}
	
//	public CarState getState() {
//		return state;
//	}
//	
//	public void setState(CarState s) {
//		this.state = s;
//	}
	
}
