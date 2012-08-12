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
			c.curVertex = sources.get(i);
			
			sources.remove(c.curVertex);
			
			List<Edge> eds = c.curVertex.getEdges();
			i = r.nextInt(eds.size());
			Edge e = eds.get(i);
			int index;
			double param;
			c.setState((c.curVertex == e.getStart()) ? CarState.FORWARD : CarState.BACKWARD);
			Position newPos;
			switch (c.getState()) {
			case FORWARD:
				index = 0;
				param = 0.0;
				newPos = new Position(e, index, param);
				//c.setPosition(new Position(e, index, param));
				c.futurePath.add(newPos);
				break;
			case BACKWARD:
				index = e.size()-2;
				param = 1.0;
				newPos = new Position(e, index, param);
				//c.setPosition(new Position(e, index, param));
				c.futurePath.add(newPos);
				break;
			case CRASHED:
				assert false;
				break;
			}
			
		}
		
		for (Car c : MODEL.cars) {
			switch (c.getState()) {
			case FORWARD:
			case BACKWARD:
				Position p = c.getLastFuturePosition();
				c.setPosition(p);
				c.futurePath.clear();
				c.futurePath.add(p);
				break;
			case CRASHED:
				break;
			}
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
						
						switch (c.getState()) {
						case FORWARD:
							double distanceToEndOfEdge = c.getLastFuturePosition().distToEndOfEdge();
							
							if (distanceToMove < distanceToEndOfEdge) {
								
								Position newPos = c.getLastFuturePosition().travelForward(distanceToMove);
								
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
							break;
						case BACKWARD:
							double distanceToStartOfEdge = c.getLastFuturePosition().distToStartOfEdge();
							
							if (distanceToMove < distanceToStartOfEdge) {
								
								Position newPos = c.getLastFuturePosition().travelBackward(distanceToMove);
								
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
							break;
						case CRASHED:
							break inner;
						}
						
						/*
						 * pick new edge
						 */
						
						List<Edge> eds = c.curVertex.getEdges();
						int i = r.nextInt(eds.size());
						Edge e = eds.get(i);
						
						c.setState((c.curVertex == e.getStart()) ? CarState.FORWARD : CarState.BACKWARD);
						
						int index;
						double param;
						Position newPos;
						
						switch (c.getState()) {
						case FORWARD:
							index = 0;
							param = 0.0;
							newPos = new Position(e, index, param);
							//c.setPosition(new Position(e, index, param));
							c.futurePath.add(newPos);
							break;
						case BACKWARD:
							index = e.size()-2;
							param = 1.0;
							newPos = new Position(e, index, param);
							//c.setPosition(new Position(e, index, param));
							c.futurePath.add(newPos);
							break;
						case CRASHED:
							assert false;
							break;
						}
						
					} catch (TravelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				} // end inner loop
				
			}
			
			Car[] cs = MODEL.cars.toArray(new Car[0]);
			iloop: for (int i = 0; i < cs.length; i++) {
				final Car ci = cs[i];
				jloop: for (int j = i+1; j < cs.length; j++) {
					Car cj = cs[j];
					
					if (ci.getState() != CarState.CRASHED) {
						
						kloop: for (int k = 0; k < ci.futurePath.size()-1; k++) {
							Position cia = ci.futurePath.get(k);
							Position cib = ci.futurePath.get(k+1);
							Edge e = cia.edge;
							if (e != cib.edge) {
								continue kloop;
							}
							int iDir = (e.posComparator.compare(cia, cib) == -1) ? 1 : -1;
							if (cj.getState() != CarState.CRASHED) {
								lloop: for (int l = 0; l < cj.futurePath.size()-1; l++) {
									Position cja = cj.futurePath.get(l);
									Position cjb = cj.futurePath.get(l+1);
									if (e != cja.edge) {
										continue lloop;
									}
									if (cja.edge != cjb.edge) {
										continue lloop;
									}
									int jDir = (e.posComparator.compare(cja, cjb) == -1) ? 1 : -1;
									if (iDir == 1) {
										if (jDir == 1) {
											continue lloop;
										} else {
											if (e.posComparator.compare(cjb, cib) == -1) {
												Position crashPos = Position.middle(cjb, cib);
												ci.setPosition(crashPos);
												cj.setPosition(crashPos);
												ci.setState(CarState.CRASHED);
												cj.setState(CarState.CRASHED);
												continue iloop;
											}
										}
									} else {
										if (jDir == 1) {
											if (e.posComparator.compare(cib, cjb) == -1) {
												Position crashPos = Position.middle(cib, cjb);
												ci.setPosition(crashPos);
												cj.setPosition(crashPos);
												ci.setState(CarState.CRASHED);
												cj.setState(CarState.CRASHED);
												continue iloop;
											}
										} else {
											continue lloop;
										}
									}
								} // l loop
							} else {
								// cj is crashed
								Position cjp = cj.getPosition();
								if (e != cjp.edge) {
									continue kloop;
								}
								if (iDir == 1) {
									if (e.posComparator.compare(cjp, cib) == -1) {
										//cj.state = CarState.CRASHED;
										//Position crashPos = Position.middle(cjb, cib);
										ci.setPosition(cjp);
										ci.setState(CarState.CRASHED);
										continue iloop;
										//cj.setPosition(crashPos);
									}
								} else {
									if (e.posComparator.compare(cib, cjp) == -1) {
										//cj.state = CarState.CRASHED;
										//Position crashPos = Position.middle(cjb, cib);
										ci.setPosition(cjp);
										ci.setState(CarState.CRASHED);
										continue iloop;
										//cj.setPosition(crashPos);
									}
								}
							}
						} // k loop
						
					} else {
						// ci is crashed
						
						Position cip = ci.getPosition();
						Edge e = cip.edge;
						
						if (cj.getState() != CarState.CRASHED) {
							lloop: for (int l = 0; l < cj.futurePath.size()-1; l++) {
								Position cja = cj.futurePath.get(l);
								Position cjb = cj.futurePath.get(l+1);
								if (e != cja.edge) {
									continue lloop;
								}
								if (cja.edge != cjb.edge) {
									continue lloop;
								}
								int jDir = (e.posComparator.compare(cja, cjb) == -1) ? 1 : -1;
								if (jDir == 1) {
									if (e.posComparator.compare(cip, cjb) == -1) {
										//Position crashPos = Position.middle(cib, cjb);
										//ci.setPosition(crashPos);
										cj.setPosition(cip);
										//ci.state = CarState.CRASHED;
										cj.setState(CarState.CRASHED);
										continue jloop;
									}
								} else {
									if (e.posComparator.compare(cjb, cip) == -1) {
										//Position crashPos = Position.middle(cjb, cib);
										//ci.setPosition(crashPos);
										cj.setPosition(cip);
										//ci.state = CarState.CRASHED;
										cj.setState(CarState.CRASHED);
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
			
			for (Car c : MODEL.cars) {
				switch (c.getState()) {
				case FORWARD:
				case BACKWARD:
					Position p = c.getLastFuturePosition();
					c.setPosition(p);
					c.futurePath.clear();
					c.futurePath.add(p);
					break;
				case CRASHED:
					break;
				}
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
