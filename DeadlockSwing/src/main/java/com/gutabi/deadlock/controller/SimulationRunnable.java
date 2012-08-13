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
import com.gutabi.deadlock.core.VertexPosition;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;

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
			
			Vertex v = sources.get(i);
			List<Edge> eds = v.getEdges();
			i = r.nextInt(eds.size());
			Edge newE = eds.get(i);
			
			Position pos = new VertexPosition(v, newE);
			
			CarState s = ((v == newE.getStart()) ? CarState.FORWARD : CarState.BACKWARD);
			c.futurePath.add(pos);
			c.futureState = s;
			
			sources.remove(v);
		}
		
		collapseFutures();
		
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
					
					Vertex v = null;
					
					try {
						
						Position pos;
						Edge e;
						switch (c.futureState) {
						case FORWARD:
							pos = c.getLastFuturePosition();
							e = pos.getEdge();
							double distanceToEndOfEdge = pos.distanceToEndOfEdge();
							
							if (distanceToMove < distanceToEndOfEdge) {
								
								c.futurePath.add(pos.travelForward(distanceToMove));
								
								break inner;
								
							} else {
								
								v = e.getEnd();
								
								distanceToMove -= distanceToEndOfEdge;
								
							}
							break;
						case BACKWARD:
							pos = c.getLastFuturePosition();
							e = pos.getEdge();
							double distanceToStartOfEdge = pos.distanceToStartOfEdge();
							
							if (distanceToMove < distanceToStartOfEdge) {
								
								c.futurePath.add(pos.travelBackward(distanceToMove));
								
								break inner;
								
							} else {
								
								v = e.getStart();
								
								distanceToMove -= distanceToStartOfEdge;
								
							}
							break;
						case CRASHED:
							break inner;
						}
						
					} catch (TravelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					/*
					 * pick new edge
					 */
					assert v != null;
					
					List<Edge> eds = v.getEdges();
					int i = r.nextInt(eds.size());
					Edge newE = eds.get(i);
					Position pos = new VertexPosition(v, newE);
					
					c.futureState = ((v == newE.getStart()) ? CarState.FORWARD : CarState.BACKWARD);
					c.futurePath.add(pos);
					
				} // end inner loop
				
			}  // for 
			
			doCrashes();
			
			collapseFutures();
			
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
	
	private void doCrashes() {
		
		Car[] cs = MODEL.cars.toArray(new Car[0]);
		iloop: for (int i = 0; i < cs.length; i++) {
			final Car ci = cs[i];
			jloop: for (int j = i+1; j < cs.length; j++) {
				Car cj = cs[j];
				
				if (ci.futureState != CarState.CRASHED) {
					
					kloop: for (int k = 0; k < ci.futurePath.size()-1; k++) {
						Position cia = ci.futurePath.get(k);
						Position cib = ci.futurePath.get(k+1);
						
						Edge e = cia.getEdge();
						if (e != cib.getEdge()) {
							continue kloop;
						}
						
						int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
						if (cj.futureState != CarState.CRASHED) {
							lloop: for (int l = 0; l < cj.futurePath.size()-1; l++) {
								Position cja = cj.futurePath.get(l);
								Position cjb = cj.futurePath.get(l+1);
								if (e != cja.getEdge()) {
									continue lloop;
								}
								if (cja.getEdge() != cjb.getEdge()) {
									continue lloop;
								}
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
								if (iDir == 1) {
									if (jDir == 1) {
										continue lloop;
									} else {
										if (Position.COMPARATOR.compare(cjb, cib) == -1) {
											Position crashPos = Position.middle(cjb, cib);
											ci.futurePath.add(crashPos);
											cj.futurePath.add(crashPos);
											ci.futureState = CarState.CRASHED;
											cj.futureState = CarState.CRASHED;
											continue iloop;
										}
									}
								} else {
									if (jDir == 1) {
										if (Position.COMPARATOR.compare(cib, cjb) == -1) {
											Position crashPos = Position.middle(cib, cjb);
											ci.futurePath.add(crashPos);
											cj.futurePath.add(crashPos);
											ci.futureState = CarState.CRASHED;
											cj.futureState = CarState.CRASHED;
											continue iloop;
										}
									} else {
										continue lloop;
									}
								}
							} // l loop
						} else {
							// cj is crashed
							Position cjp = cj.getLastFuturePosition();
							if (e != cjp.getEdge()) {
								continue kloop;
							}
							if (iDir == 1) {
								if (Position.COMPARATOR.compare(cjp, cib) == -1) {
									ci.futurePath.add(cjp);
									ci.futureState = CarState.CRASHED;
									continue iloop;
								}
							} else {
								if (Position.COMPARATOR.compare(cib, cjp) == -1) {
									ci.futurePath.add(cjp);
									ci.futureState = CarState.CRASHED;
									continue iloop;
								}
							}
						}
					} // k loop
					
				} else {
					// ci is crashed
					
					Position cip = ci.getLastFuturePosition();
					Edge e = cip.getEdge();
					
					if (cj.getState() != CarState.CRASHED) {
						lloop: for (int l = 0; l < cj.futurePath.size()-1; l++) {
							Position cja = cj.futurePath.get(l);
							Position cjb = cj.futurePath.get(l+1);
							if (e != cja.getEdge()) {
								continue lloop;
							}
							if (cja.getEdge() != cjb.getEdge()) {
								continue lloop;
							}
							int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
							if (jDir == 1) {
								if (Position.COMPARATOR.compare(cip, cjb) == -1) {
									cj.futurePath.add(cip);
									cj.futureState = CarState.CRASHED;
									continue jloop;
								}
							} else {
								if (Position.COMPARATOR.compare(cjb, cip) == -1) {
									cj.futurePath.add(cip);
									cj.futureState = CarState.CRASHED;
									continue jloop;
								}
							}
						} // l loop
					} else {
						// both ci and cj are crashed
						;
					}
					
				}
			} // j loop
		} // i loop
		
	}
	
	private void collapseFutures() {
		
		for (Car c : MODEL.cars) {
			switch (c.futureState) {
			case FORWARD:
			case BACKWARD:
			case CRASHED:
				Position p = c.getLastFuturePosition();
				c.setPosition(p);
				c.futurePath.clear();
				c.futurePath.add(p);
				CarState s = c.futureState;
				c.setState(s);
				c.futureState = s;
				break;
			}
		}
		
	}
}
