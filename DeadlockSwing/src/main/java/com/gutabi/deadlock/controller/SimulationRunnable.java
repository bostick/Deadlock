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
import com.gutabi.deadlock.core.VertexType;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;
import com.gutabi.deadlock.model.CrashInfo;
import com.gutabi.deadlock.model.CrashSite;

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
			carsCopy = new ArrayList<Car>(MODEL.cars);
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
						if (carPos.distanceTo(new VertexPosition(v, null, null, 0)) <= 10) {
							toRemove.add(v);
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
//					List<Edge> eds = v.getEdges();
//					i = MODEL.RANDOM.nextInt(eds.size());
//					Edge newE = eds.get(i);
//					
					
					c.startingVertex = v;
					c.startingStep = step;
					
					//c.futurePathNewEdge();
//					VertexPosition newPos;
//					if (newE.isLoop()) {
//						newPos = new VertexPosition(v, newE, (MODEL.RANDOM.nextInt(2) == 0 ? VertexPositionType.START : VertexPositionType.END));
//					} else if (v == newE.getStart()) {
//						newPos = new VertexPosition(v, newE, VertexPositionType.START);
//					} else {
//						newPos = new VertexPosition(v, newE, VertexPositionType.END);
//					}
					c.futurePathAdd(new VertexPosition(v, null, null, 0));
					
					//CarState s = ((newE.getStart() == v) ? CarState.FORWARD : CarState.BACKWARD);
					c.futureState = CarState.VERTEX;
					//c.futureEdge = newE;
					
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
					
					switch (c.futureState) {
					case FORWARD: {
						Position pos = c.getLastFuturePosition();
						Edge e;
						int dir = 0;
						double distanceToEndOfEdge;
						if (pos instanceof EdgePosition) {
							e = ((EdgePosition)pos).getEdge();
							dir = 1;
							distanceToEndOfEdge = ((EdgePosition)pos).distanceToEndOfEdge();	
						} else {
							e = c.futureEdge;
							dir = c.futureDir;
							c.futureEdge = null;
							distanceToEndOfEdge = e.getTotalLength();
						}
						
						if (pos instanceof EdgePosition) {
							c.futurePathAdd(((EdgePosition)pos).travel(dir, Math.min(c.distanceToMove, distanceToEndOfEdge)));
						} else {
							c.futurePathAdd(((VertexPosition)pos).travel(e, dir, Math.min(c.distanceToMove, distanceToEndOfEdge)));
						}
						
						if (DMath.doubleEquals(c.distanceToMove, distanceToEndOfEdge)) {
							
							Vertex v = e.getEnd();
							c.futureState = CarState.VERTEX;
							
							c.distanceToMove = 0.0;
							
							if (v.getType() == VertexType.SINK) {
								c.futureState = CarState.SINKED;
							}
							
						} else if (c.distanceToMove > distanceToEndOfEdge) {
							
							Vertex v = e.getEnd();
							c.futureState = CarState.VERTEX;
							
							c.distanceToMove -= distanceToEndOfEdge;
							
							if (v.getType() == VertexType.SINK) {
								c.futureState = CarState.SINKED;
								c.distanceToMove = 0.0;
							}
							
						} else {
							c.distanceToMove = 0.0;
						}
						
						break;
					}
					case BACKWARD: {
						Position pos = c.getLastFuturePosition();
						Edge e;
						int dir = 0;
						double distanceToStartOfEdge;
						if (pos instanceof EdgePosition) {
							e = ((EdgePosition)pos).getEdge();
							dir = -1;
							distanceToStartOfEdge = ((EdgePosition)pos).distanceToStartOfEdge();	
						} else {
							e = c.futureEdge;
							dir = c.futureDir;
							//c.futureEdge = null;
							distanceToStartOfEdge = e.getTotalLength();
						}
						
						if (pos instanceof EdgePosition) {
							c.futurePathAdd(((EdgePosition)pos).travel(dir, Math.min(c.distanceToMove, distanceToStartOfEdge)));
						} else {
							c.futurePathAdd(((VertexPosition)pos).travel(e, dir, Math.min(c.distanceToMove, distanceToStartOfEdge)));
						}
						
						if (DMath.doubleEquals(c.distanceToMove, distanceToStartOfEdge)) {
							
							Vertex v = e.getStart();
							c.futureState = CarState.VERTEX;
							
							c.distanceToMove = 0.0;
							
							if (v.getType() == VertexType.SINK) {
								c.futureState = CarState.SINKED;
							}
							
						} else if (c.distanceToMove > distanceToStartOfEdge) {
							
							Vertex v = e.getStart();
							c.futureState = CarState.VERTEX;
							
							c.distanceToMove -= distanceToStartOfEdge;
							
							if (v.getType() == VertexType.SINK) {
								c.futureState = CarState.SINKED;
								c.distanceToMove = 0.0;
							}
							
						} else {
							c.distanceToMove = 0.0;
						}
						
						break;
					}
					case VERTEX: {
						VertexPosition pos = (VertexPosition)c.getLastFuturePosition();
						Vertex v = pos.getVertex();
						List<Edge> eds = new ArrayList<Edge>(v.getEdges());
						
						Edge previousEdge = null;
						List<Position> path = c.getFuturePath();
						if (path.size() > 1) {
							EdgePosition ep = (EdgePosition)path.get(path.size()-2);
							previousEdge = ep.getEdge();
						}
						
						if (eds.size() > 1 && previousEdge != null) {
							/*
							 * don't go back the same way
							 */
							eds.remove(previousEdge);
						}
						
						int i = MODEL.RANDOM.nextInt(eds.size());
						c.futureEdge = eds.get(i);
						if (c.futureEdge.isLoop()) {
							c.futureDir = 2*MODEL.RANDOM.nextInt(2)-1;
						} else {
							c.futureDir = (c.futureEdge.getStart() == v) ? 1 : -1;
						}
						c.futureState = (c.futureDir == 1) ? CarState.FORWARD : CarState.BACKWARD;
						break;
					}
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
				
				if (c.futureState != CarState.CRASHED && c.futureState != CarState.SINKED) {
					List<Position> path = c.futurePath.path;
					double total = 0;
					for (int i = 0; i < path.size()-1; i++) {
						Position a = path.get(i);
						Position b = path.get(i+1);
						total += a.distanceTo(b);
					}
					assert DMath.doubleEquals(total, MODEL.DISTANCE_PER_TIMESTEP);
				}
				
			}  // for 
			
			doCrashes(carsCopy);
			while (firstCrashSite != null) {
				processCrashInfo(carsCopy);
				collapseFutures(crashedCars);
				crashedCars.clear();
				doCrashes(carsCopy);
			}
			
			checkDistances(carsCopy);
			
			collapseFutures(carsCopy);
			
			removeSinked(carsCopy);
			
			synchronized (MODEL) {
				MODEL.cars = new ArrayList<Car>(carsCopy);
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
		
		synchronized (MODEL) {
			MODEL.cars.clear();
		}
		firstCrashSite = null;
		crashes.clear();
		crashedCars.clear();
		
	}
	
//	private void pickNewEdgeXX(Car c, Vertex v, Edge oldEdge) {
//		
//		if (v.getType() == VertexType.SINK) {
//			c.futureState = CarState.SINKED;
//			c.distanceToMove = 0.0;
//			return;
//		}
//		
//		/*
//		 * pick new edge
//		 */
//		assert v != null;
//		
//		List<Edge> eds = new ArrayList<Edge>(v.getEdges());
//		
//		if (eds.size() > 1) {
//			/*
//			 * don't go back the same way
//			 */
//			eds.remove(oldEdge);
//		}
//		
//		int i = MODEL.RANDOM.nextInt(eds.size());
//		Edge newE = eds.get(i);
//		
////		VertexPosition newPos;
////		if (newE.isLoop()) {
////			newPos = new VertexPosition(v, newE, (MODEL.RANDOM.nextInt(2) == 0 ? VertexPositionType.START : VertexPositionType.END));
////		} else if (v == newE.getStart()) {
////			newPos = new VertexPosition(v, newE, VertexPositionType.START);
////		} else {
////			newPos = new VertexPosition(v, newE, VertexPositionType.END);
////		}
//		
//		c.futureState = ((newE.getStart() == v) ? CarState.FORWARD : CarState.BACKWARD);
//		//c.futurePathNewEdge();
//		c.futurePathAdd(new VertexPosition(v));
//		
//	}
	
	private void doCrashes(List<Car> cars) {
		
		Car[] cs = cars.toArray(new Car[0]);
		for (int i = 0; i < cs.length; i++) {
			final Car ci = cs[i];
			jloop: for (int j = i+1; j < cs.length; j++) {
				Car cj = cs[j];
				
				if (ci.getState() != CarState.CRASHED) {
					
					double ciTraveled = 0;
					List<Position> ciFuturePath = ci.getFuturePath();
					for (int k = 0; k < ciFuturePath.size()-1; k++) {
						Position cia = ciFuturePath.get(k);
						Position cib = ciFuturePath.get(k+1);
						ciTraveled += cia.distanceTo(cib); 
						
						int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
						
						if (cj.getState() != CarState.CRASHED) {
							
							double cjTraveled = 0;
							List<Position> cjFuturePath = cj.getFuturePath();
							for (int l = 0; l < cjFuturePath.size()-1; l++) {
								Position cja = cjFuturePath.get(l);
								Position cjb = cjFuturePath.get(l+1);
								cjTraveled += cja.distanceTo(cjb);
								
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
								
								double dist = cib.distanceTo(cjb);
								if (DMath.doubleEquals(dist, 10.0)) {
									assert DMath.doubleEquals(ciTraveled, cjTraveled);
									saveCrashInfo(new CrashInfo(new CrashSite(ciTraveled), ci, cj, cib, cjb, iDir, jDir, dist, k+1, l+1));
									
									break jloop;
								} else if (dist < 10) {
									Position adjustedCib = cib;
									Position adjustedCjb = cjb;
									double adjustedCiTraveled = ciTraveled;
									double adjustedCjTraveled = cjTraveled;
									/*
									 * figure out who has traveled more and back up
									 */
									if (DMath.doubleEquals(adjustedCiTraveled, adjustedCjTraveled)) {
										
									} else if (adjustedCiTraveled > adjustedCjTraveled) {
										/*
										 * ci backs up
										 */
										double travelDiff = adjustedCiTraveled - adjustedCjTraveled;
										//adjustedCib = cib.travel(cia, travelDiff);
										if (cia instanceof EdgePosition) {
											adjustedCib = ((EdgePosition)cia).travel(cib.prevDir, cia.distanceTo(cib)-travelDiff);
										} else {
											adjustedCib = ((VertexPosition)cia).travel(cib.prevDirEdge, cib.prevDir, cia.distanceTo(cib)-travelDiff);
										}
										adjustedCiTraveled -= travelDiff;
										
									} else {
										/*
										 * cj backs up
										 */
										double travelDiff = adjustedCjTraveled - adjustedCiTraveled;
										//adjustedCjb = cjb.travel(cja, travelDiff);
										if (cja instanceof EdgePosition) {
											adjustedCjb = ((EdgePosition)cja).travel(cjb.prevDir, cja.distanceTo(cjb)-travelDiff);
										} else {
											adjustedCjb = ((VertexPosition)cja).travel(cjb.prevDirEdge, cjb.prevDir, cja.distanceTo(cjb)-travelDiff);
										}
										adjustedCjTraveled -= travelDiff;
									}
									
									/*
									 * ci and cj have now traveled the same amount, the crash site may still be wrong
									 */
									
									double newDist = adjustedCib.distanceTo(adjustedCjb);
									if (DMath.doubleEquals(newDist, 10.0)) {
										assert DMath.doubleEquals(adjustedCiTraveled, adjustedCjTraveled);
										saveCrashInfo(new CrashInfo(new CrashSite(adjustedCiTraveled), ci, cj, adjustedCib, adjustedCjb, iDir, jDir, newDist, k+1, l+1));
										
										break jloop;
									} else if (newDist < 10) {
										double diff = 10 - newDist;
										double inc = diff / 2;
										assert DMath.doubleEquals(adjustedCiTraveled, adjustedCjTraveled);
										//double newDist = adjustedCib.distanceTo(adjustedCjb);
										//Position newAdjustedCib = adjustedCib.travel(cia, inc);
										Position newAdjustedCib;
										if (cia instanceof EdgePosition) {
											newAdjustedCib = ((EdgePosition)cia).travel(adjustedCib.prevDir, cia.distanceTo(adjustedCib)-inc);
										} else {
											newAdjustedCib = ((VertexPosition)cia).travel(adjustedCib.prevDirEdge, adjustedCib.prevDir, cia.distanceTo(adjustedCib)-inc);
										}
										double newAdjustedCiTraveled = adjustedCiTraveled-inc;
										//Position newAdjustedCjb = adjustedCjb.travel(cja, inc);
										Position newAdjustedCjb;
										if (cja instanceof EdgePosition) {
											newAdjustedCjb = ((EdgePosition)cja).travel(adjustedCjb.prevDir, cja.distanceTo(adjustedCjb)-inc);
										} else {
											newAdjustedCjb = ((VertexPosition)cja).travel(adjustedCjb.prevDirEdge, adjustedCjb.prevDir, cja.distanceTo(adjustedCjb)-inc);
										}
										double newAdjustedCjTraveled = adjustedCjTraveled-inc;
										double newNewDist = newAdjustedCib.distanceTo(newAdjustedCjb);
										assert DMath.doubleEquals(newNewDist, 10);
										saveCrashInfo(new CrashInfo(new CrashSite(newAdjustedCiTraveled), ci, cj, newAdjustedCib, newAdjustedCjb, iDir, jDir, newDist, k+1, l+1));
										
										break jloop;
									}
									
								}
							} // l loop
						
						} else {
							// cj is crashed
							Position cjp = cj.getPosition();
							
							double dist = cib.distanceTo(cjp);
							if (DMath.doubleEquals(dist, 10.0)) {
								saveCrashInfo(new CrashInfo(new CrashSite(ciTraveled), ci, cj, cib, cjp, iDir, 0, dist, k+1, -1));
							} else if (dist < 10) {
								double diff = 10 - dist;
								double inc = diff;
								//Position adjustedCib = cib.travel(cia, inc);
								Position adjustedCib;
								if (cia instanceof EdgePosition) {
									adjustedCib = ((EdgePosition)cia).travel(cib.prevDir, cia.distanceTo(cib)-inc);
								} else {
									adjustedCib = ((VertexPosition)cia).travel(cib.prevDirEdge, cib.prevDir, cia.distanceTo(cib)-inc);
								}
								double adjustedCiTraveled = ciTraveled;
								adjustedCiTraveled -= inc;
								double newDist = adjustedCib.distanceTo(cjp);
								assert DMath.doubleEquals(newDist, 10);
								saveCrashInfo(new CrashInfo(new CrashSite(adjustedCiTraveled), ci, cj, adjustedCib, cjp, iDir, 0, dist, k+1, -1));
							}
						}
					} // k loop
				
				} else {
					// ci is crashed
					
					if (cj.getState() != CarState.CRASHED) {
						
						Position cip = ci.getPosition();
						
						double cjTraveled = 0;
						List<Position> cjFuturePath = cj.getFuturePath();
						for (int l = 0; l < cjFuturePath.size()-1; l++) {
							Position cja = cjFuturePath.get(l);
							Position cjb = cjFuturePath.get(l+1);
							cjTraveled += cja.distanceTo(cjb);
							
							int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
							
							double dist = cip.distanceTo(cjb);
							if (DMath.doubleEquals(dist, 10.0)) {
								saveCrashInfo(new CrashInfo(new CrashSite(cjTraveled), ci, cj, cip, cjb, 0, jDir, dist, -1, l+1));
								
								break jloop;
							} else if (dist < 10) {
								double diff = 10 - dist;
								double inc = diff;
								//Position adjustedCjb = cjb.travel(cja, inc);
								Position adjustedCjb;
								if (cja instanceof EdgePosition) {
									adjustedCjb = ((EdgePosition)cja).travel(cjb.prevDir, cja.distanceTo(cjb)-inc);
								} else {
									adjustedCjb = ((VertexPosition)cja).travel(cjb.prevDirEdge, cjb.prevDir, cja.distanceTo(cjb)-inc);
								}
								double adjustedCjTraveled = cjTraveled;
								adjustedCjTraveled -= inc;
								double newDist = cip.distanceTo(adjustedCjb);
								assert DMath.doubleEquals(newDist, 10);
								saveCrashInfo(new CrashInfo(new CrashSite(adjustedCjTraveled), ci, cj, cip, adjustedCjb, 0, jDir, dist, -1, l+1));
								
								break jloop;
							}
						} // l loop
						
					} else {
						// both ci and cj are crashed
					}
					
				}
			} // j loop
		} // i loop
		
	}
	
	
	CrashSite firstCrashSite;
	List<CrashInfo> crashes = new ArrayList<CrashInfo>();
	List<Car> crashedCars = new ArrayList<Car>();
	
	private void saveCrashInfo(CrashInfo ci) {
		
		CrashSite site = ci.crashSite;
		
		if (firstCrashSite == null) {
			firstCrashSite = site;
			assert crashes.isEmpty();
			crashes.add(ci);
		} else if (firstCrashSite.equals(site)) {
			crashes.add(ci);
		} else if (site.time < firstCrashSite.time) {
			firstCrashSite = site;
			crashes.clear();
			crashes.add(ci);
		} else {
			;
		}
		
	}
	
	private void processCrashInfo(List<Car> cars) {
		
		//for (CrashSite site : crashSites) {
		for (CrashInfo info : crashes) {
			//Position crashSite = info.crashSite;
			Car i = info.i;
			Car j = info.j;
			Position ip = info.ip;
			Position jp = info.jp;
			int iDir = info.iDir;
			int jDir = info.jDir;
			//double dist = info.dist;
			//int ie = info.ie;
			int ik = info.ik;
			//int je = info.je;
			int jl = info.jl;
			
			double dist = ip.distanceTo(jp);
			assert DMath.doubleEquals(dist, 10.0);
			
			if (iDir != 0) {
				i.futurePathCrash(ip, ik);
				i.futureState = CarState.CRASHED;
				crashedCars.add(i);
			}
			
			if (jDir != 0) {
				j.futurePathCrash(jp, jl);
				j.futureState = CarState.CRASHED;
				crashedCars.add(j);
			}
			
		}
		//}
		
		firstCrashSite = null;
		crashes.clear();
		
//		crashSites.clear();
//		crashMap.clear();
	}
	
	private void checkDistances(List<Car> cars) {
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			Position cPos = c.getLastFuturePosition();
			for (int j = i + 1; j < cars.size(); j++) {
				Car d = cars.get(j);
				Position dPos = d.getLastFuturePosition();
				double dist = cPos.distanceTo(dPos);
				assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
			}
		}
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
			//c.futurePathNewEdge();
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
