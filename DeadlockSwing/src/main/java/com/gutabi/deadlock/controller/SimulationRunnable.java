package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;
import com.gutabi.deadlock.core.VertexPositionType;
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;

public class SimulationRunnable implements Runnable {
	
	static double DISTANCE_PER_TIMESTEP = 5.0;
	
	@Override
	public void run() {
		
		long step = 0;
		long lastSpawnStep = 0;
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		ControlMode modeCopy;
		List<Car> carsCopy;
		
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
			carsCopy = new ArrayList<Car>();
			for (Car c : MODEL.cars) {
				carsCopy.add(c);
			}
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
				
				for (Vertex v : verticesCopy) {
					if (v.getType() == VertexType.SOURCE && !v.hasCrash) {
						sources.add(v);
					}
				}
				
				int n = sources.size();
				
				for (int i = 0; i < n; i++) {
					newCars.add(new Car());
				}
				
				for (Car c : newCars) {
					if (sources.size() == 0) {
						continue;
					}
					int i = MODEL.RANDOM.nextInt(sources.size());
					
					Vertex v = sources.get(i);
					List<Edge> eds = v.getEdges();
					i = MODEL.RANDOM.nextInt(eds.size());
					Edge newE = eds.get(i);
					
					c.futurePathNewEdge();
					VertexPosition newPos;
					if (newE.isLoop()) {
						newPos = new VertexPosition(v, newE, (MODEL.RANDOM.nextInt(2) == 0 ? VertexPositionType.START : VertexPositionType.END));
					} else if (v == newE.getStart()) {
						newPos = new VertexPosition(v, newE, VertexPositionType.START);
					} else {
						newPos = new VertexPosition(v, newE, VertexPositionType.END);
					}
					c.futurePathAdd(newPos);
					
					CarState s = ((newPos.getType() == VertexPositionType.START) ? CarState.FORWARD : CarState.BACKWARD);
					c.futureState = s;
					
					sources.remove(v);
				}
				
				collapseFutures(newCars);
				
				carsCopy.addAll(newCars);
				
				lastSpawnStep = step;
			}
			
			for (Car c : carsCopy) {
				
				c.distanceToMove = DISTANCE_PER_TIMESTEP;
				
				inner:
				while (true) {
					
					Vertex v = null;
					Position pos = c.getLastFuturePosition();
					Edge e = pos.getEdge();
						
					switch (c.futureState) {
					case FORWARD:
						double distanceToEndOfEdge = pos.distanceToEndOfEdge();
						
						if (DMath.doubleEquals(c.distanceToMove, distanceToEndOfEdge)) {
							
							v = e.getEnd();
							c.futurePathAdd(new VertexPosition(v, e, VertexPositionType.END));
							
							c.distanceToMove = 0.0;
							
							pickNewEdge(c, v, e);
							
						} else if (c.distanceToMove > distanceToEndOfEdge) {
							
							v = e.getEnd();
							c.futurePathAdd(new VertexPosition(v, e, VertexPositionType.END));
							
							c.distanceToMove -= distanceToEndOfEdge;
							
							pickNewEdge(c, v, e);
							
						} else {
							
							c.futurePathAdd(pos.travelForward(c.distanceToMove));
							
							c.distanceToMove = 0.0;
						}
						
						break;
					case BACKWARD:
						double distanceToStartOfEdge = pos.distanceToStartOfEdge();
						
						if (DMath.doubleEquals(c.distanceToMove, distanceToStartOfEdge)) {
							
							v = e.getStart();
							c.futurePathAdd(new VertexPosition(v, e, VertexPositionType.START));
							
							c.distanceToMove = 0.0;
							
							pickNewEdge(c, v, e);
							
						} else if (c.distanceToMove > distanceToStartOfEdge) {
							
							v = e.getStart();
							c.futurePathAdd(new VertexPosition(v, e, VertexPositionType.START));
							
							c.distanceToMove -= distanceToStartOfEdge;
							
							pickNewEdge(c, v, e);
							
						} else {
							
							c.futurePathAdd(pos.travelBackward(c.distanceToMove));
							
							c.distanceToMove = 0.0;
							
						}
						
						break;
					case CRASHED:
						c.distanceToMove = 0.0;
						break;
					case SINKED:
						c.distanceToMove = 0.0;
						break;
					}
					
					if (c.distanceToMove == 0.0) {
						break inner;
					}
					
				} // end inner loop
				
			}  // for 
			
			doCrashes(carsCopy);
			
			collapseFutures(carsCopy);
			
			removeSinked(carsCopy);
			
			synchronized (MODEL) {
				MODEL.cars = carsCopy;
			}
			
			VIEW.repaint();
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			step++;
			
		} // outer
		
		MODEL.cars.clear();
		
	}
	
	private void pickNewEdge(Car c, Vertex v, Edge oldEdge) {
		
		if (v.getType() == VertexType.SINK) {
			c.futureState = CarState.SINKED;
			c.distanceToMove = 0.0;
			return;
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
			eds.remove(oldEdge);
		}
		
		int i = MODEL.RANDOM.nextInt(eds.size());
		Edge newE = eds.get(i);
		
		VertexPosition newPos;
		if (newE.isLoop()) {
			newPos = new VertexPosition(v, newE, (MODEL.RANDOM.nextInt(2) == 0 ? VertexPositionType.START : VertexPositionType.END));
		} else if (v == newE.getStart()) {
			newPos = new VertexPosition(v, newE, VertexPositionType.START);
		} else {
			newPos = new VertexPosition(v, newE, VertexPositionType.END);
		}
		
		c.futureState = ((newPos.getType() == VertexPositionType.START) ? CarState.FORWARD : CarState.BACKWARD);
		c.futurePathNewEdge();
		c.futurePathAdd(newPos);
		
	}
	
	private void doCrashes(List<Car> cars) {
		
		Car[] cs = cars.toArray(new Car[0]);
		iloop:
		for (int i = 0; i < cs.length; i++) {
			final Car ci = cs[i];
			jloop:
			for (int j = i+1; j < cs.length; j++) {
				Car cj = cs[j];
				
				if (ci.getState() != CarState.CRASHED) {
					
					for (int cie = 0; cie < ci.getFuturePath().size(); cie++) {
						List<Position> ciEPath = ci.getFuturePath().get(cie);
					kloop: for (int k = 0; k < ciEPath.size()-1; k++) {
						Position cia = ciEPath.get(k);
						Position cib = ciEPath.get(k+1);
						
						int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
						
						Edge e = cib.getEdge();
						
						if (cj.getState() != CarState.CRASHED) {
							
							for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
								List<Position> cjEPath = cj.getFuturePath().get(cje);
							lloop: for (int l = 0; l < cjEPath.size()-1; l++) {
								Position cja = cjEPath.get(l);
								Position cjb = cjEPath.get(l+1);
								
								if (e != cja.getEdge()) {
									continue lloop;
								}
								
								if (cib.equals(cjb) && isSink(cib)) {
									continue lloop;
								}
								
								if (cib.distanceTo(cjb) <= 10) {
									int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
									if (iDir == 1) {
										if (jDir == 1) {
											Position crashPos;
											switch (Position.COMPARATOR.compare(cib, cjb)) {
											case -1:
												crashPos = Position.middle(cib, cjb);
												ci.futurePathCrash(crashPos.travelBackwardClamped(5), cie, k+1);
												cj.futurePathCrash(crashPos.travelForwardClamped(5), cje, l+1);
												ci.futureState = CarState.CRASHED;
												cj.futureState = CarState.CRASHED;
												continue iloop;
											case 1:
												crashPos = Position.middle(cib, cjb);
												ci.futurePathCrash(crashPos.travelForwardClamped(5), cie, k+1);
												cj.futurePathCrash(crashPos.travelBackwardClamped(5), cje, l+1);
												ci.futureState = CarState.CRASHED;
												cj.futureState = CarState.CRASHED;
												continue iloop;
											default:
												assert false;
											}
										} else {
											assert Position.COMPARATOR.compare(cjb.travelBackwardClamped(5), cib.travelForwardClamped(5)) != 1;
											Position crashPos = Position.middle(cjb, cib);
											ci.futurePathCrash(crashPos.travelBackwardClamped(5), cie, k+1);
											cj.futurePathCrash(crashPos.travelForwardClamped(5), cje, l+1);
											ci.futureState = CarState.CRASHED;
											cj.futureState = CarState.CRASHED;
											continue iloop;
										}
									} else {
										if (jDir == 1) {
											assert Position.COMPARATOR.compare(cib.travelBackwardClamped(5), cjb.travelForwardClamped(5)) != 1;
											Position crashPos = Position.middle(cib, cjb);
											ci.futurePathCrash(crashPos.travelForwardClamped(5), cie, k+1);
											cj.futurePathCrash(crashPos.travelBackwardClamped(5), cje, l+1);
											ci.futureState = CarState.CRASHED;
											cj.futureState = CarState.CRASHED;
											continue iloop;
										} else {
											Position crashPos;
											switch (Position.COMPARATOR.compare(cib, cjb)) {
											case -1:
												crashPos = Position.middle(cib, cjb);
												ci.futurePathCrash(crashPos.travelBackwardClamped(5), cie, k+1);
												cj.futurePathCrash(crashPos.travelForwardClamped(5), cje, l+1);
												ci.futureState = CarState.CRASHED;
												cj.futureState = CarState.CRASHED;
												continue iloop;
											case 1:
												crashPos = Position.middle(cib, cjb);
												ci.futurePathCrash(crashPos.travelForwardClamped(5), cie, k+1);
												cj.futurePathCrash(crashPos.travelBackwardClamped(5), cje, l+1);
												ci.futureState = CarState.CRASHED;
												cj.futureState = CarState.CRASHED;
												continue iloop;
											default:
												assert false;
											}
										}
									}
								}
							} } // l loop
						
						} else {
							// cj is crashed
							Position cjp = cj.getPosition();
							
							if (cjp instanceof EdgePosition) {
								
								if (e != cjp.getEdge()) {
									continue kloop;
								}
								
								if (cib.distanceTo(cjp) <= 10) {
									if (iDir == 1) {
										assert Position.COMPARATOR.compare(cjp, cib.travelForwardClamped(10)) != 1;
										ci.futurePathCrash(cjp.travelBackwardClamped(10), cie, k+1);
										ci.futureState = CarState.CRASHED;
										continue iloop;
									} else {
										assert Position.COMPARATOR.compare(cib.travelBackward(10), cjp) != 1;
										ci.futurePathCrash(cjp.travelForwardClamped(10), cie, k+1);
										ci.futureState = CarState.CRASHED;
										continue iloop;
									}
								}
								
							} else {
								
								Vertex v = ((VertexPosition)cjp).getVertex();
								
								List<Edge> eds = v.getEdges();
								
								if (!eds.contains(e)) {
									continue kloop;
								}
								
								/*
								 * with correct edge
								 */
								VertexPosition newCjp;
								if (iDir == 1) {
									newCjp = new VertexPosition(v, e, VertexPositionType.END);
								} else {
									newCjp = new VertexPosition(v, e, VertexPositionType.START);
								}
								
								if (cib.distanceTo(newCjp) <= 10) {
									if (iDir == 1) {
										assert Position.COMPARATOR.compare(newCjp, cib.travelForwardClamped(10)) == 0;
										ci.futurePathCrash(newCjp.travelBackwardClamped(10), cie, k+1);
										ci.futureState = CarState.CRASHED;
										continue iloop;
									} else {
										assert Position.COMPARATOR.compare(cib.travelBackwardClamped(10), newCjp) == 0;
										ci.futurePathCrash(newCjp.travelForwardClamped(10), cie, k+1);
										ci.futureState = CarState.CRASHED;
										continue iloop;
									}
								}
								
							}
							
						}
					} } // k loop
				
				} else {
					// ci is crashed
					
					Position cip = ci.getPosition();
					
					if (cip instanceof EdgePosition) {
						
						Edge e = cip.getEdge();
						
						if (cj.getState() != CarState.CRASHED) {
							
							for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
								List<Position> cjEPath = cj.getFuturePath().get(cje);
							lloop: for (int l = 0; l < cjEPath.size()-1; l++) {
								Position cja = cjEPath.get(l);
								Position cjb = cjEPath.get(l+1);
								
								if (e != cja.getEdge()) {
									continue lloop;
								}
								
								if (cip.distanceTo(cjb) <= 10) {
									int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
									if (jDir == 1) {
										assert Position.COMPARATOR.compare(cip, cjb.travelForward(10)) != 1;
										cj.futurePathCrash(cip.travelBackwardClamped(10), cje, l+1);
										cj.futureState = CarState.CRASHED;
										continue jloop;
									} else {
										assert Position.COMPARATOR.compare(cjb.travelBackwardClamped(10), cip) != 1;
										cj.futurePathCrash(cip.travelForwardClamped(10), cje, l+1);
										cj.futureState = CarState.CRASHED;
										continue jloop;
									}
								}
							} } // l loop
						
						} else {
							// both ci and cj are crashed
							;
						}
						
					} else {
						
						Vertex v = ((VertexPosition)cip).getVertex();
						
						List<Edge> eds = v.getEdges();
						
						if (cj.getState() != CarState.CRASHED) {
							
							for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
								List<Position> cjEPath = cj.getFuturePath().get(cje);
							lloop: for (int l = 0; l < cjEPath.size()-1; l++) {
								Position cja = cjEPath.get(l);
								Position cjb = cjEPath.get(l+1);
								
								if (!eds.contains(cja.getEdge())) {
									continue lloop;
								}
								
								Edge e = cja.getEdge();
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
								
								/*
								 * with correct edge
								 */
								VertexPosition newCip;
								if (jDir == 1) {
									newCip = new VertexPosition(v, e, VertexPositionType.END);
								} else {
									newCip = new VertexPosition(v, e, VertexPositionType.START);
								}
								
								if (newCip.distanceTo(cjb) <= 10) {
									if (jDir == 1) {
										assert Position.COMPARATOR.compare(newCip, cjb.travelForwardClamped(10)) == 0;
										cj.futurePathCrash(newCip.travelBackwardClamped(10), cje, l+1);
										cj.futureState = CarState.CRASHED;
										continue jloop;
									} else {
										assert Position.COMPARATOR.compare(cjb.travelBackwardClamped(10), newCip) == 0;
										cj.futurePathCrash(newCip.travelForwardClamped(10), cje, l+1);
										cj.futureState = CarState.CRASHED;
										continue jloop;
									}
								}
							} } // l loop
						
						} else {
							// both ci and cj are crashed
							;
						}
						
					}
					
				}
			} // j loop
		} // i loop
		
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			Position cPos = c.getLastFuturePosition();
			for (int j = i + 1; j < cars.size(); j++) {
				Car d = cars.get(j);
				Position dPos = d.getLastFuturePosition();
				if (cPos.getEdge() == dPos.getEdge()) {
					if (!(cPos instanceof VertexPosition || dPos instanceof VertexPosition)) {
						/*
						 * TODO
						 * ignore vertices for now
						 */
						double dist = cPos.distanceTo(dPos);
						assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
					}
				}
			}
		}
		
	}
	
	private static boolean isSink(Position p) {
		return (p instanceof VertexPosition) && ((VertexPosition)p).getVertex().getType() == VertexType.SINK;
	}
	
	private void collapseFutures(List<Car> cars) {
		
		for (Car c : cars) {
			Position p = c.getLastFuturePosition();
			c.setPosition(p);
			c.futurePathClear();
			c.futurePathNewEdge();
			c.futurePathAdd(p);
			
			CarState s;
			if (isSink(p)) {
				s = CarState.SINKED;
			} else {
				s = c.futureState;
				c.setState(s);
			}
			
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
			if (c.getPosition() instanceof VertexPosition) {
				Vertex v = ((VertexPosition)c.getPosition()).getVertex();
				if (v.getType() == VertexType.SINK) {
					toRemove.add(c);
				}
			}
		}
		for (Car c : toRemove) {
			cars.remove(c);
		}
	}
	
}
