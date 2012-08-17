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
import com.gutabi.deadlock.model.CrashInfo;

public class SimulationRunnable implements Runnable {
	
	List<Car> specials = new ArrayList<Car>();
	
	long step = 0;
	
	@Override
	public void run() {
		
		//System.out.println(MODEL.RANDOM);
		
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
			
			if (MODEL.SPAWN_FREQUENCY > 0 && (step == 0 || (step - lastSpawnStep) >= MODEL.SPAWN_FREQUENCY)) {
				
				List<Car> newCars = new ArrayList<Car>();
				
				List<Vertex> sources = new ArrayList<Vertex>();
				
				for (Vertex v : verticesCopy) {
					if (v.getType() == VertexType.SOURCE && !v.hasCrash) {
						sources.add(v);
					}
				}
				List<Vertex> toRemove = new ArrayList<Vertex>();
				for (Car c : carsCopy) {
					Position carPos = c.getPosition();
					for (Vertex v : sources) {
						/*
						 * sources have one edge
						 */
						Edge e = v.getEdges().get(0);
						if (carPos.getEdge() == e) {
							VertexPosition pos = new VertexPosition(v, e);
							if (carPos.distanceTo(pos) <= 10) {
								toRemove.add(v);
							}
						}
					}
				}
				for (Vertex v : toRemove) {
					sources.remove(v);
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
					
					
					c.startingVertex = v;
					c.startingStep = step;
					
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
				
				c.distanceToMove = MODEL.DISTANCE_PER_TIMESTEP;
				
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
							
							c.futurePathAdd(pos.travel(1, c.distanceToMove));
							
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
							
							c.futurePathAdd(pos.travel(-1, c.distanceToMove));
							
							c.distanceToMove = 0.0;
							
						}
						
						break;
					case CRASHED:
						c.distanceToMove = 0.0;
						break;
					case SINKED:
						c.distanceToMove = 0.0;
						break;
					case NEW:
						assert false;
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
				Thread.sleep(MODEL.WAIT);
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
		//iloop:
		for (int i = 0; i < cs.length; i++) {
			final Car ci = cs[i];
			//jloop:
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
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
//								if (e != cja.getEdge()) {
//									continue lloop;
//								}
								if (cib.equals(cjb) && isSink(cib)) {
									continue lloop;
								}
								double dist = cib.distanceTo(cjb);
								if (dist <= 10) {
									saveCrashInfo(new CrashInfo(ci, cj, cia, cja, cib, cjb, iDir, jDir, dist, cie, k+1, cje, l+1));
								}
							} } // l loop
						
						} else {
							// cj is crashed
							Position cjp = cj.getPosition();
							
							if (cjp instanceof EdgePosition) {
								
//								if (e != cjp.getEdge()) {
//									continue kloop;
//								}
								
								double dist = cib.distanceTo(cjp);
								if (dist <= 10.0) {
									saveCrashInfo(new CrashInfo(ci, cj, cia, null, cib, cjp, iDir, 0, dist, cie, k+1, -1, -1));
								}
								
							} else {
								
								Vertex v = ((VertexPosition)cjp).getVertex();
								
								List<Edge> eds = v.getEdges();
								
//								if (!eds.contains(e)) {
//									continue kloop;
//								}
								
								/*
								 * with correct edge
								 */
//								VertexPosition newCjp;
//								if (e.isLoop()) {
//									if (iDir == 1) {
//										newCjp = new VertexPosition(v, e, VertexPositionType.END);
//									} else {
//										newCjp = new VertexPosition(v, e, VertexPositionType.START);
//									}
//								} else if (v == e.getStart()) {
//									newCjp = new VertexPosition(v, e, VertexPositionType.START);
//								} else {
//									newCjp = new VertexPosition(v, e, VertexPositionType.END);
//								}
								
								double dist = cib.distanceTo(cjp);
								if (dist <= 10) {
									saveCrashInfo(new CrashInfo(ci, cj, cia, null, cib, cjp, iDir, 0, dist, cie, k+1, -1, -1));
								}
								
							}
							
						}
					} } // k loop
				
				} else {
					// ci is crashed
					
					if (cj.getState() != CarState.CRASHED) {
						
						Position cip = ci.getPosition();
						
						if (cip instanceof EdgePosition) {
							
							Edge e = cip.getEdge();
								
							for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
								List<Position> cjEPath = cj.getFuturePath().get(cje);
							lloop: for (int l = 0; l < cjEPath.size()-1; l++) {
								Position cja = cjEPath.get(l);
								Position cjb = cjEPath.get(l+1);
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
								
//								if (e != cja.getEdge()) {
//									continue lloop;
//								}
								
								double dist = cip.distanceTo(cjb);
								if (dist <= 10) {
									saveCrashInfo(new CrashInfo(ci, cj, null, cja, cip, cjb, 0, jDir, dist, -1, -1, cje, l+1));
								}
							} } // l loop
							
						} else {
							
							Vertex v = ((VertexPosition)cip).getVertex();
							
							List<Edge> eds = v.getEdges();
							
							for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
								List<Position> cjEPath = cj.getFuturePath().get(cje);
							lloop: for (int l = 0; l < cjEPath.size()-1; l++) {
								Position cja = cjEPath.get(l);
								Position cjb = cjEPath.get(l+1);
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
								
//								if (!eds.contains(cja.getEdge())) {
//									continue lloop;
//								}
								
								Edge e = cja.getEdge();
								
								/*
								 * with correct edge
								 */
//								VertexPosition newCip;
//								if (e.isLoop()) {
//									if (jDir == 1) {
//										newCip = new VertexPosition(v, e, VertexPositionType.END);
//									} else {
//										newCip = new VertexPosition(v, e, VertexPositionType.START);
//									}
//								} else if (v == e.getStart()) {
//									newCip = new VertexPosition(v, e, VertexPositionType.START);
//								} else {
//									newCip = new VertexPosition(v, e, VertexPositionType.END);
//								}
								
								double dist = cip.distanceTo(cjb);
								if (dist <= 10) {
									saveCrashInfo(new CrashInfo(ci, cj, null, cja, cip, cjb, 0, jDir, dist, -1, -1, cje, l+1));
								}
							} } // l loop
							
						}
						
					} else {
						// both ci and cj are crashed
						;
					}
					
				}
			} // j loop
		} // i loop
		
		processCrashInfo();
		
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			Position cPos = c.getLastFuturePosition();
			for (int j = i + 1; j < cars.size(); j++) {
				Car d = cars.get(j);
				Position dPos = d.getLastFuturePosition();
				if (cPos.getEdge() == dPos.getEdge()) {
					double dist = cPos.distanceTo(dPos);
					assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
				}
//				if (cPos.getEdge() == dPos.getEdge()) {
//					if (!(cPos instanceof VertexPosition || dPos instanceof VertexPosition)) {
//						/*
//						 * TODO
//						 * ignore vertices for now
//						 */
//						double dist = cPos.distanceTo(dPos);
//						assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
////						if (!(DMath.doubleEquals(dist, 10.0) || dist > 10.0)) {
////							if (c.futureState != CarState.CRASHED || d.futureState != CarState.CRASHED) {
////								String.class.getName();
////							}
////							c.special = true;
////							d.special = true;
////							specials.add(c);
////							specials.add(d);
////							System.out.println("step: " + step);
////							System.out.println("Car c: " + c + " <" + c.getState() + " " + c.getPosition().distanceToStartOfEdge() + "> <" + c.futureState + " " + cPos.distanceToStartOfEdge() + ">");
////							System.out.println("Car d: " + d + " <" + d.getState() + " " + d.getPosition().distanceToStartOfEdge() + "> <" + d.futureState + " " + dPos.distanceToStartOfEdge() + ">");
////							System.out.println("dist: " + dist);
////							System.out.println();
////						}
//					}
//				}
			}
		}
		
	}
	
	
	List<CrashInfo> crashes = new ArrayList<CrashInfo>();
	
	private void saveCrashInfo(CrashInfo ci) {
		crashes.add(ci);
	}
	
	private void processCrashInfo() {
		
		for (CrashInfo info : crashes) {
			Car i = info.i;
			Car j = info.j;
			Position ip = info.ip;
			Position jp = info.jp;
			int iDir = info.iDir;
			int jDir = info.jDir;
			double dist = info.dist;
			int ie = info.ie;
			int ik = info.ik;
			int je = info.je;
			int jl = info.jl;
			
			if (iDir != 0) {
				if (jDir != 0) {
					
					Position crashPos = Position.middle(ip, jp);
					
					if (iDir == 1) {
						if (jDir == 1) {
							switch (Position.COMPARATOR.compare(ip, jp)) {
							case -1:
								i.futurePathCrash(crashPos.travelClamped(-1, 5), ie, ik);
								i.futureState = CarState.CRASHED;
								j.futurePathCrash(crashPos.travelClamped(1, 5), je, jl);
								j.futureState = CarState.CRASHED;
							case 1:
								i.futurePathCrash(crashPos.travelClamped(1, 5), ie, ik);
								i.futureState = CarState.CRASHED;
								j.futurePathCrash(crashPos.travelClamped(-1, 5), je, jl);
								j.futureState = CarState.CRASHED;
							default:
								assert false;
							}
						} else {
							assert Position.COMPARATOR.compare(jp.travelClamped(-1, 5), ip.travelClamped(1, 5)) != 1;
							i.futurePathCrash(crashPos.travelClamped(-1, 5), ie, ik);
							i.futureState = CarState.CRASHED;
							j.futurePathCrash(crashPos.travelClamped(1, 5), je, jl);
							j.futureState = CarState.CRASHED;
						}
					} else {
						if (jDir == 1) {
							assert Position.COMPARATOR.compare(ip.travelClamped(-1, 5), jp.travelClamped(1, 5)) != 1;
							i.futurePathCrash(crashPos.travelClamped(1, 5), ie, ik);
							i.futureState = CarState.CRASHED;
							j.futurePathCrash(crashPos.travelClamped(-1, 5), je, jl);
							j.futureState = CarState.CRASHED;
						} else {
							switch (Position.COMPARATOR.compare(ip, jp)) {
							case -1:
								i.futurePathCrash(crashPos.travelClamped(-1, 5), ie, ik);
								i.futureState = CarState.CRASHED;
								j.futurePathCrash(crashPos.travelClamped(1, 5), je, jl);
								j.futureState = CarState.CRASHED;
							case 1:
								i.futurePathCrash(crashPos.travelClamped(1, 5), ie, ik);
								i.futureState = CarState.CRASHED;
								j.futurePathCrash(crashPos.travelClamped(-1, 5), je, jl);
								j.futureState = CarState.CRASHED;
							default:
								assert false;
							}
						}
					}
				} else {
					// j has crashed
					if (jp instanceof EdgePosition) {
						if (iDir == 1) {
							assert Position.COMPARATOR.compare(jp, ip.travelClamped(1, 10)) != 1;
							i.futurePathCrash(jp.travelClamped(-1, 10), ie, ik);
							i.futureState = CarState.CRASHED;
						} else {
							assert Position.COMPARATOR.compare(ip.travel(-1, 10), jp) != 1;
							i.futurePathCrash(jp.travelClamped(1, 10), ie, ik);
							i.futureState = CarState.CRASHED;
						}
					} else {
						if (iDir == 1) {
							assert Position.COMPARATOR.compare(jp, ip.travelClamped(1, 10)) == 0;
							i.futurePathCrash(jp.travelClamped(-1, 10), ie, ik);
							i.futureState = CarState.CRASHED;
						} else {
							assert Position.COMPARATOR.compare(ip.travelClamped(-1, 10), jp) == 0;
							i.futurePathCrash(jp.travelClamped(1, 10), ie, ik);
							i.futureState = CarState.CRASHED;
						}
					}
				}
			} else {
				// i has crashed
				if (jDir != 0) {
					if (ip instanceof EdgePosition) {
						if (jDir == 1) {
							assert Position.COMPARATOR.compare(ip, jp.travel(1, 10)) != 1;
							j.futurePathCrash(ip.travelClamped(-1, 10), je, jl);
							j.futureState = CarState.CRASHED;
						} else {
							assert Position.COMPARATOR.compare(jp.travelClamped(-1, 10), ip) != 1;
							j.futurePathCrash(ip.travelClamped(1, 10), je, jl);
							j.futureState = CarState.CRASHED;
						}
					} else {
						if (jDir == 1) {
							assert Position.COMPARATOR.compare(ip, jp.travelClamped(1, 10)) == 0;
							j.futurePathCrash(ip.travelClamped(-1, 10), je, jl);
							j.futureState = CarState.CRASHED;	
						} else {
							assert Position.COMPARATOR.compare(jp.travelClamped(-1, 10), ip) == 0;
							j.futurePathCrash(ip.travelClamped(1, 10), je, jl);
							j.futureState = CarState.CRASHED;
						}
					}
					
				} else {
					
				}
			}
			
		}
		
		crashes.clear();
		
	}
	
	private static boolean isSink(Position p) {
		return (p instanceof VertexPosition) && ((VertexPosition)p).getVertex().getType() == VertexType.SINK;
	}
	
	private void collapseFutures(List<Car> cars) {
		
		cloop:
		for (Car c : cars) {
			switch (c.getState()) {
				case CRASHED:
				case SINKED:
				continue cloop;
				default:
					break;
			}
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
			
			if (p instanceof VertexPosition) {
				Vertex v = ((VertexPosition)p).getVertex();
				if (s == CarState.CRASHED) {
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
