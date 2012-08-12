package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.TravelException;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.model.Car;

public class SimulationRunnable implements Runnable {
	
	@Override
	public void run() {
		
		List<Vertex> sources = new ArrayList<Vertex>();
		for (Vertex v : MODEL.getVertices()) {
			if (v.getType() == VertexType.SOURCE) {
				sources.add(v);
			}
		}
		
		int n = sources.size();
		
		for (int i = 0; i < n; i++) {
			MODEL.cars.add(new Car());
		}
		
		Random r = new Random();
		
		for (Car c : MODEL.cars) {
			if (sources.size() == 0) {
				continue;
			}
			int i = r.nextInt(sources.size());
			c.curVertex = sources.get(i);
			
			sources.remove(c.curVertex);
			
			List<Edge> eds = c.curVertex.getEdges();
			i = r.nextInt(eds.size());
			Edge e = eds.get(i);
			int index;
			double param;
			c.travelingForward = (c.curVertex == e.getStart());
			if (c.travelingForward) {
				index = 0;
				param = 0.0;
			} else {
				index = e.size()-2;
				param = 1.0;
			}
			
			c.setPosition(new Position(e, index, param));
		}
		
		VIEW.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		outer:
		while (true) {
			
			synchronized (MODEL) {
				if (MODEL.getMode() == ControlMode.IDLE) {
					break outer;
				}
			}
			
			for (Car c : MODEL.cars) {
				
				double distanceToMove = 10.0;
				
				inner:
				while (true) {
					
					try {
						if (c.travelingForward) {
							
							double distanceToEndOfEdge = c.getPosition().distToEndOfEdge();
							
							if (distanceToMove < distanceToEndOfEdge) {
								
								Position newPos = Position.travelForward(c.getPosition(), distanceToMove);
								
//								c.curIndex = info.index;
//								c.curParam = info.param;
								
								c.setPosition(newPos);
								
								break inner;
								
							} else {
								
								c.curVertex = c.getPosition().edge.getEnd();
								distanceToMove -= distanceToEndOfEdge;
								
							}
							
						} else {
							
							double distanceToStartOfEdge = c.getPosition().distToStartOfEdge();
							
							if (distanceToMove < distanceToStartOfEdge) {
								
								Position newPos = Position.travelBackward(c.getPosition(), distanceToMove);
								
//								c.curIndex = info.index;
//								c.curParam = info.param;
								
								c.setPosition(newPos);
								
								break inner;
								
							} else {
								
								c.curVertex = c.getPosition().edge.getStart();
								distanceToMove -= distanceToStartOfEdge;
								
							}
							
						}
						
						/*
						 * pick new edge
						 */
						
						List<Edge> eds = c.curVertex.getEdges();
						int i = r.nextInt(eds.size());
						Edge e = eds.get(i);
						
						c.travelingForward = (c.curVertex == e.getStart());
						
						int index;
						double param;
						if (c.travelingForward) {
							index = 0;
							param = 0.0;
						} else {
							index = e.size()-2;
							param = 1.0;
						}
						
						c.setPosition(new Position(e, index, param));
						
					} catch (TravelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				} // end inner loop
				
			}
			
			VIEW.repaint();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
		}
			
		
		MODEL.cars.clear();
		
	}
	
}
