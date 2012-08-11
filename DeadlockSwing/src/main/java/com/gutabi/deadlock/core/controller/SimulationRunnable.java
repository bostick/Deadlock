package com.gutabi.deadlock.core.controller;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.IntersectionInfo;
import com.gutabi.deadlock.core.TravelException;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.core.model.Car;

public class SimulationRunnable implements Runnable {
	
	@Override
	public void run() {
		
		int n = 20;
		
		for (int i = 0; i < n; i++) {
			MODEL.cars.add(new Car());
		}
		
		Random r = new Random();
		
//		Vertex curVertex = null;
//		boolean travelingForward;
		
		List<Vertex> sources = new ArrayList<Vertex>();
		for (Vertex v : MODEL.getVertices()) {
			if (v.getType() == VertexType.SOURCE) {
				sources.add(v);
			}
		}
		
		for (Car c : MODEL.cars) {
			int i = r.nextInt(sources.size());
			c.curVertex = sources.get(i);
			List<Edge> eds = c.curVertex.getEdges();
			i = r.nextInt(eds.size());
			c.curEdge = eds.get(i);
			c.travelingForward = (c.curVertex == c.curEdge.getStart());
			if (c.travelingForward) {
				c.curIndex = 0;
				c.curParam = 0.0;
			} else {
				c.curIndex = c.curEdge.size()-2;
				c.curParam = 1.0;
			}
		}
		
		PLATFORMVIEW.repaint();
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
					
//					Edge e = MODEL.car.curEdge;
//					int index = MODEL.car.curIndex;
//					double param = MODEL.car.curParam;
					
					try {
						if (c.travelingForward) {
							
							double distanceToEndOfEdge = c.curEdge.dist(c.curIndex, c.curParam, 1);
							
							if (distanceToMove < distanceToEndOfEdge) {
								
								IntersectionInfo info = c.curEdge.travel(c.curIndex, c.curParam, distanceToMove, 1);
								
								c.curIndex = info.index;
								c.curParam = info.param;
								
								break inner;
								
							} else {
								
								c.curVertex = c.curEdge.getEnd();
								distanceToMove -= distanceToEndOfEdge;
								
							}
							
						} else {
							
							double distanceToStartOfEdge = c.curEdge.dist(c.curIndex, c.curParam, -1);
							
							if (distanceToMove < distanceToStartOfEdge) {
								
								IntersectionInfo info = c.curEdge.travel(c.curIndex, c.curParam, distanceToMove, -1);
								
								c.curIndex = info.index;
								c.curParam = info.param;
								
								break inner;
								
							} else {
								
								c.curVertex = c.curEdge.getStart();
								distanceToMove -= distanceToStartOfEdge;
								
							}
							
						}
						
						List<Edge> eds = c.curVertex.getEdges();
						int i = r.nextInt(eds.size());
						c.curEdge = eds.get(i);
						
						c.travelingForward = (c.curVertex == c.curEdge.getStart());
						
						if (c.travelingForward) {
							c.curIndex = 0;
							c.curParam = 0.0;
						} else {
							c.curIndex = c.curEdge.size()-2;
							c.curParam = 1.0;
						}
						
					} catch (TravelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				} // end inner loop
				
			}
			
			PLATFORMVIEW.repaint();
			
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
