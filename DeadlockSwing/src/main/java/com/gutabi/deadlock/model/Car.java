package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.VertexPosition;

public class Car {
	
	private CarState state;
	
	private Position pos;
	
	//@SuppressWarnings("serial")
	private List<List<Position>> futurePath = new ArrayList<List<Position>>();
	
	public CarState futureState;
	
	public double distanceToMove;
	
	public Position getPosition() {
		return pos;
	}
	
	public void setPosition(Position pos) {
		this.pos = pos;
	}
	
	public void futurePathNewEdge() {
		futurePath.add(new ArrayList<Position>());
	}
	
	public void futurePathAdd(Position pos) {
		List<Position> last = futurePath.get(futurePath.size()-1);
		last.add(pos);
		assert futurePathConsistent();
	}
	
	/**
	 * Remove the rest of the edge after pIndex (and all other edges after eIndex) and replace the last position
	 * with pos (if they are not already equal)
	 * 
	 */
	public void futurePathCrash(Position pos, int eIndex, int pIndex) {
		
		List<List<Position>> newFuturePath = new ArrayList<List<Position>>();
		for (int i = 0; i < eIndex; i++) {
			newFuturePath.add(futurePath.get(i));
		}
		
		List<Position> oldE = futurePath.get(eIndex);
		
		List<Position> newE = new ArrayList<Position>();
		newFuturePath.add(newE);
		Position last = null;
		for (int i = 0; i < pIndex; i++) {
			last = oldE.get(i);
			newE.add(last);
		}
		
		if (last == null || !pos.equals(last)) {
			newE.add(pos);
		}
		
		futurePath = newFuturePath;
		
		assert futurePathConsistent();
	}
	
	public void futurePathClear() {
		futurePath.clear();
		//futurePath.add(new ArrayList<Position>());
	}
	
	public List<List<Position>> getFuturePath() {
		return futurePath;
	}
	
	private boolean futurePathConsistent() {
		VertexPosition last = null;
		for (List<Position> ePath : futurePath) {
			
			Edge e = null;
			for (int i = 0; i < ePath.size()-1; i++) {
				Position a = ePath.get(i);
				Position b = ePath.get(i+1);
				if (i == 0) {
					e = a.getEdge();
					if (a instanceof VertexPosition) {
						if (last != null) {
							assert last.getVertex() == ((VertexPosition)a).getVertex();
							
							/*
							 * could be one edge on vertex
							 */
							//assert last.getEdge() != ((VertexPosition)a).getEdge();
							last = null;
						}
					}
				} else {
					assert a instanceof EdgePosition;
				}
				assert b.getEdge() == e;
				if (i == ePath.size()-2) {
					if (b instanceof VertexPosition) {
						last = ((VertexPosition)b);
					}
				}
				assert !a.equals(b);
//				if (a instanceof EdgePosition) {
//					if (b instanceof EdgePosition) {
//						assert !a.equals(b);
//					} else {
//						assert !a.equals(b);
//					}
//				} else {
//					if (b instanceof EdgePosition) {
//						assert !a.equals(b);
//					} else {
//						assert !a.equals(b);
//						//assert ((VertexPosition)a).getEdge() != ((VertexPosition)b).getEdge(); 
//					}
//				}
			}
		}
		return true;
	}
	
	public Position getLastFuturePosition() {
		int l = futurePath.size()-1;
		List<Position> last = futurePath.get(l);
		return last.get(last.size()-1);
	}
	
	public CarState getState() {
		return state;
	}
	
	public void setState(CarState state) {
		this.state = state;
	}
	
	public Car copy() {
		Car c = new Car();
		c.pos = getPosition();
		return c;
	}
	
	public String toString() {
		return "car " + pos.getPoint();
	}
	
}
