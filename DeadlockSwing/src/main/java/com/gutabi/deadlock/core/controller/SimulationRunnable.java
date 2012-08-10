package com.gutabi.deadlock.core.controller;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

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
		
		Random r = new Random();
		
		Vertex start = null;
		boolean travelingForward;
		
		synchronized (MODEL) {
			for (Vertex v : MODEL.getVertices()) {
				if (v.getType() == VertexType.SOURCE) {
					start = v;
					break;
				}
			}
			
			List<Edge> eds = start.getEdges();
			int i = r.nextInt(eds.size());
			Edge e = eds.get(i);
			
			travelingForward = (start == e.getStart());
			
			MODEL.car = new Car();
			MODEL.car.curEdge = e;
			if (travelingForward) {
				MODEL.car.curIndex = 0;
				MODEL.car.curParam = 0.0;
			} else {
				MODEL.car.curIndex = e.size()-2;
				MODEL.car.curParam = 1.0;
			}
		}
		
		PLATFORMVIEW.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		loop:
		while (true) {
			
			synchronized (MODEL) {
				if (MODEL.getMode() == ControlMode.IDLE) {
					break loop;
				}
			}
			
			double distanceToMove = 10.0;
			
			if (travelingForward) {
				
				try {
					IntersectionInfo info = MODEL.car.curEdge.travel(MODEL.car.curIndex, MODEL.car.curParam, distanceToMove);
					
					MODEL.car.curIndex = info.index;
					MODEL.car.curParam = info.param;
					
				} catch (TravelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				
				
				
			}
			
			
			PLATFORMVIEW.repaint();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
		}
		
	}
	
}
