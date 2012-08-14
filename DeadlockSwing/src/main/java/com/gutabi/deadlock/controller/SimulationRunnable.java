package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;

public class SimulationRunnable implements Runnable {
	
	static double DISTANCE_PER_TIMESTEP = 2.0;
	
	@Override
	public void run() {
		
		long step = 0;
		long lastSpawnStep = 0;
		
		Random r = new Random();
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		ControlMode modeCopy;
		//List<Point> curStrokeCopy;
		//Point lastPointCopy;
		List<Car> carsCopy;
		
		//List<Car> crashes;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>();
			for (Edge e : MODEL.getEdges()) {
				edgesCopy.add(e);
			}
			verticesCopy = new ArrayList<Vertex>();
			for (Vertex v : MODEL.getVertices()) {
				v.hasCrash = false;
				verticesCopy.add(v);
			}
			modeCopy = MODEL.getMode();
			//curStrokeCopy = new ArrayList<Point>(MODEL.curStrokeRaw);
			//lastPointCopy = MODEL.lastPointRaw;
			carsCopy = new ArrayList<Car>();
			for (Car c : MODEL.cars) {
				carsCopy.add(c);
			}
			//crashes = new ArrayList<Car>();
		}
		
		outer:
		while (true) {
			
			synchronized (MODEL) {
				modeCopy = MODEL.getMode();
				if (modeCopy == ControlMode.IDLE) {
					break outer;
				}
			}
			
			if (step == 0 || (step - lastSpawnStep) * DISTANCE_PER_TIMESTEP >= 20.0) {
				
				List<Car> newCars = new ArrayList<Car>();
				
				List<Vertex> sources = new ArrayList<Vertex>();
				//synchronized (MODEL) {
				for (Vertex v : verticesCopy) {
					if (v.getType() == VertexType.SOURCE && !v.hasCrash) {
						sources.add(v);
					}
				}
				//}
				
				int n = sources.size();
				
				for (int i = 0; i < n; i++) {
					newCars.add(new Car());
				}
				
				for (Car c : newCars) {
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
				
				collapseFutures(newCars);
				
				//synchronized (MODEL) {
				carsCopy.addAll(newCars);
				//}
				
				lastSpawnStep = step;
			}
			
			for (Car c : carsCopy) {
				
				double distanceToMove = DISTANCE_PER_TIMESTEP;
				
				inner:
				while (true) {
					
					Vertex v = null;
					Position pos;
					Edge e = null;
						
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
							c.futurePath.add(new VertexPosition(v, e));
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
							c.futurePath.add(new VertexPosition(v, e));
							distanceToMove -= distanceToStartOfEdge;
							
						}
						break;
					case CRASHED:
					case SINKED:
						break inner;
					}
					
					/*
					 * pick new edge
					 */
					assert v != null;
					
					List<Edge> eds = new ArrayList<Edge>(v.getEdges());
					
					if (eds.size() > 1) {
						/*
						 * don't go back the same way
						 */
						eds.remove(e);
					}
					
					int i = r.nextInt(eds.size());
					Edge newE = eds.get(i);
					Position newPos = new VertexPosition(v, newE);
					
					c.futureState = (v.getType() == VertexType.SINK ? CarState.SINKED : ((v == newE.getStart()) ? CarState.FORWARD : CarState.BACKWARD));
					c.futurePath.add(newPos);
					
				} // end inner loop
				
			}  // for 
			
			doCrashes(carsCopy);
			
			collapseFutures(carsCopy);
			
			removeSinked(carsCopy);
			//moveCrashed(carsCopy, crashes);
			
			synchronized (MODEL) {
				MODEL.cars = carsCopy;
			}
			
			VIEW.repaint();
			
			try {
				Thread.sleep(70);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			step++;
			
		} // outer
		
		MODEL.cars.clear();
		
	}
	
	private void doCrashes(List<Car> cars) {
		
		Car[] cs = cars.toArray(new Car[0]);
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
								if (cib.distanceTo(cjb) <= 10) {
									int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
									if (iDir == 1) {
										if (jDir == 1) {
											continue lloop;
										} else {
											if (Position.COMPARATOR.compare(cjb.travelBackwardClamped(5), cib.travelForwardClamped(5)) != 1) {
												Position crashPos = Position.middle(cjb, cib);
												ci.futurePath.add(crashPos.travelBackwardClamped(5));
												cj.futurePath.add(crashPos.travelForwardClamped(5));
												ci.futureState = CarState.CRASHED;
												cj.futureState = CarState.CRASHED;
												continue iloop;
											}
										}
									} else {
										if (jDir == 1) {
											if (Position.COMPARATOR.compare(cib.travelBackwardClamped(5), cjb.travelForwardClamped(5)) != 1) {
												Position crashPos = Position.middle(cib, cjb);
												ci.futurePath.add(crashPos.travelForwardClamped(5));
												cj.futurePath.add(crashPos.travelBackwardClamped(5));
												ci.futureState = CarState.CRASHED;
												cj.futureState = CarState.CRASHED;
												continue iloop;
											}
										} else {
											continue lloop;
										}
									}
								}
							} // l loop
						} else {
							// cj is crashed
							Position cjp = cj.getLastFuturePosition();
							
							if (cjp instanceof EdgePosition) {
								
								if (e != cjp.getEdge()) {
									continue kloop;
								}
								
								if (cib.distanceTo(cjp) <= 10) {
									if (iDir == 1) {
										if (Position.COMPARATOR.compare(cjp.travelBackwardClamped(5), cib.travelForward(5)) != 1) {
											ci.futurePath.add(cjp.travelBackwardClamped(10));
											ci.futureState = CarState.CRASHED;
											continue iloop;
										}
									} else {
										if (Position.COMPARATOR.compare(cib.travelBackward(5), cjp.travelForwardClamped(5)) != 1) {
											ci.futurePath.add(cjp.travelForwardClamped(10));
											ci.futureState = CarState.CRASHED;
											continue iloop;
										}
									}
								}
								
							} else {
								
								Vertex v = ((VertexPosition)cjp).getVertex();
								
								List<Edge> eds = v.getEdges();
								
								if (!eds.contains(e)) {
									continue kloop;
								}
								
								VertexPosition newCjp = new VertexPosition(v, e);
								
								if (cib.distanceTo(newCjp) <= 10) {
									if (iDir == 1) {
										if (Position.COMPARATOR.compare(newCjp, cib.travelForwardClamped(5)) == 0) {
											ci.futurePath.add(newCjp.travelBackwardClamped(10));
											ci.futureState = CarState.CRASHED;
											continue iloop;
										}
									} else {
										if (Position.COMPARATOR.compare(cib.travelBackwardClamped(5), newCjp) == 0) {
											ci.futurePath.add(newCjp.travelForwardClamped(10));
											ci.futureState = CarState.CRASHED;
											continue iloop;
										}
									}
								}
								
							}
							
						}
					} // k loop
					
				} else {
					// ci is crashed
					
					Position cip = ci.getLastFuturePosition();
					
					if (cip instanceof EdgePosition) {
						
						Edge e = cip.getEdge();
						
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
								if (cip.distanceTo(cjb) <= 10) {
									int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
									if (jDir == 1) {
										if (Position.COMPARATOR.compare(cip.travelBackwardClamped(5), cjb.travelForward(5)) != 1) {
											cj.futurePath.add(cip.travelBackwardClamped(10));
											cj.futureState = CarState.CRASHED;
											continue jloop;
										}
									} else {
										if (Position.COMPARATOR.compare(cjb.travelBackwardClamped(5), cip.travelForwardClamped(5)) != 1) {
											cj.futurePath.add(cip.travelForwardClamped(10));
											cj.futureState = CarState.CRASHED;
											continue jloop;
										}
									}
								}
							} // l loop
						} else {
							// both ci and cj are crashed
							;
						}
						
					} else {
						
						Vertex v = ((VertexPosition)cip).getVertex();
						
						List<Edge> eds = v.getEdges();
						
						if (cj.futureState != CarState.CRASHED) {
							lloop: for (int l = 0; l < cj.futurePath.size()-1; l++) {
								Position cja = cj.futurePath.get(l);
								Position cjb = cj.futurePath.get(l+1);
								if (!eds.contains(cja.getEdge())) {
									continue lloop;
								}
								if (cja.getEdge() != cjb.getEdge()) {
									continue lloop;
								}
								VertexPosition newCip = new VertexPosition(v, cja.getEdge());
								if (newCip.distanceTo(cjb) <= 10) {
									int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
									if (jDir == 1) {
										if (Position.COMPARATOR.compare(newCip, cjb.travelForwardClamped(5)) == 0) {
											cj.futurePath.add(newCip.travelBackwardClamped(10));
											cj.futureState = CarState.CRASHED;
											continue jloop;
										}
									} else {
										if (Position.COMPARATOR.compare(cjb.travelBackwardClamped(5), newCip) == 0) {
											cj.futurePath.add(newCip.travelForwardClamped(10));
											cj.futureState = CarState.CRASHED;
											continue jloop;
										}
									}
								}
							} // l loop
						} else {
							// both ci and cj are crashed
							;
						}
						
					}
					
				}
			} // j loop
		} // i loop
		
	}
	
	private void collapseFutures(List<Car> cars) {
		
		for (Car c : cars) {
			Position p = c.getLastFuturePosition();
			c.setPosition(p);
			c.futurePath.clear();
			c.futurePath.add(p);
			CarState s = c.futureState;
			c.setState(s);
			
			if (s == CarState.CRASHED) {
				if (p instanceof VertexPosition) {
					Vertex v = ((VertexPosition)p).getVertex();
					v.hasCrash = true;
				}
			}
		}
		
	}
	
	private void removeSinked(List<Car> cars) {
		List<Car> toRemove = new ArrayList<Car>();
		for (Car c : cars) {
			if (c.getState() == CarState.SINKED) {
				toRemove.add(c);
			}
		}
		for (Car c : toRemove) {
			cars.remove(c);
		}
	}
	
//	private void moveCrashed(List<Car> cars, List<Car> crashes) {
//		List<Car> toMove = new ArrayList<Car>();
//		for (Car c : cars) {
//			if (c.getState() == CarState.CRASHED) {
//				toMove.add(c);
//			}
//		}
//		for (Car c : toMove) {
//			cars.remove(c);
//			crashes.add(c);
//		}
//	}
}
