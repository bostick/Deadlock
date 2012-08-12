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
			
			Position newPos = new Position(e, index, param);
			//c.setPosition(new Position(e, index, param));
			c.futurePath.add(newPos);
		}
		
		for (Car c : MODEL.cars) {
			Position p = c.getLastFuturePosition();
			c.setPosition(p);
			c.futurePath.clear();
			c.futurePath.add(p);
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
							
							double distanceToEndOfEdge = c.getLastFuturePosition().distToEndOfEdge();
							
							if (distanceToMove < distanceToEndOfEdge) {
								
								Position newPos = Position.travelForward(c.getLastFuturePosition(), distanceToMove);
								
//								c.curIndex = info.index;
//								c.curParam = info.param;
								
								//c.setPosition(newPos);
								c.futurePath.add(newPos);
								
								break inner;
								
							} else {
								
								c.futurePath.add(new Position(c.getLastFuturePosition().edge, c.getLastFuturePosition().edge.size()-2, 1.0));
								
								c.curVertex = c.getLastFuturePosition().edge.getEnd();
								distanceToMove -= distanceToEndOfEdge;
								
							}
							
						} else {
							
							double distanceToStartOfEdge = c.getLastFuturePosition().distToStartOfEdge();
							
							if (distanceToMove < distanceToStartOfEdge) {
								
								Position newPos = Position.travelBackward(c.getLastFuturePosition(), distanceToMove);
								
//								c.curIndex = info.index;
//								c.curParam = info.param;
								
								//c.setPosition(newPos);
								c.futurePath.add(newPos);
								
								break inner;
								
							} else {
								
								c.futurePath.add(new Position(c.getLastFuturePosition().edge, 0, 0.0));
								
								c.curVertex = c.getLastFuturePosition().edge.getStart();
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
						
						//c.setPosition(new Position(e, index, param));
						c.futurePath.add(new Position(e, index, param));
						
					} catch (TravelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				} // end inner loop
				
			}
			
			Car[] cs = MODEL.cars.toArray(new Car[0]);
			for (int i = 0; i < cs.length; i++) {
				Car ci = cs[i];
				for (int j = i+1; j < cs.length; j++) {
					Car cj = cs[j];
					kloop: for (int k = 0; k < ci.futurePath.size()-1; k++) {
						Position cia = ci.futurePath.get(k);
						Position cib = ci.futurePath.get(k+1);
						int iDir = (Position.posComparator.compare(cia, cib) == -1) ? 1 : -1;
						if (cia.edge != cib.edge) {
							continue kloop;
						}
						lloop: for (int l = 0; l < cj.futurePath.size()-1; l++) {
							Position cja = cj.futurePath.get(l);
							Position cjb = cj.futurePath.get(l+1);
							int jDir = (Position.posComparator.compare(cja, cjb) == -1) ? 1 : -1;
							if (cja.edge != cjb.edge) {
								continue lloop;
							}
							if (cia.edge != cja.edge) {
								continue lloop;
							}
							if (iDir == 1) {
								if (jDir == 1) {
									continue lloop;
								} else {
									if (Position.posComparator.compare(cjb, cib) == -1) {
										String.class.getName();
									}
								}
							} else {
								if (jDir == 1) {
									if (Position.posComparator.compare(cib, cjb) == -1) {
										String.class.getName();
									}
								} else {
									continue lloop;
								}
							}
						}
					}
				}
			}
			
			for (Car c : MODEL.cars) {
				Position p = c.getLastFuturePosition();
				c.setPosition(p);
				c.futurePath.clear();
				c.futurePath.add(p);
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
