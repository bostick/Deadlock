package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;
import com.gutabi.deadlock.core.VertexPositionType;
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
			while (!crashSites.isEmpty()) {
				processCrashInfo(carsCopy);
				collapseFutures(crashedCars);
				crashedCars.clear();
				doCrashes(carsCopy);
			}
			
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
		for (int i = 0; i < cs.length; i++) {
			final Car ci = cs[i];
			for (int j = i+1; j < cs.length; j++) {
				Car cj = cs[j];
				
				if (ci.getState() != CarState.CRASHED) {
					
					double ciTraveled = 0;
					for (int cie = 0; cie < ci.getFuturePath().size(); cie++) {
						List<Position> ciEPath = ci.getFuturePath().get(cie);
					for (int k = 0; k < ciEPath.size()-1; k++) {
						Position cia = ciEPath.get(k);
						Position cib = ciEPath.get(k+1);
						ciTraveled += cia.distanceTo(cib);
						
						int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
						
						if (cj.getState() != CarState.CRASHED) {
							
							double cjTraveled = 0;
							for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
								List<Position> cjEPath = cj.getFuturePath().get(cje);
							for (int l = 0; l < cjEPath.size()-1; l++) {
								Position cja = cjEPath.get(l);
								Position cjb = cjEPath.get(l+1);
								cjTraveled += cja.distanceTo(cjb);
								
								int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
								
								double dist = cib.distanceTo(cjb);
								if (DMath.doubleEquals(dist, 10.0)) {
									assert DMath.doubleEquals(ciTraveled, cjTraveled);
									saveCrashInfo(new CrashInfo(new CrashSite(Position.middle(cib, cjb), ciTraveled), ci, cj, cia, cja, cib, cjb, iDir, jDir, dist, cie, k+1, cje, l+1));
								} else if (dist < 10) {
									double diff = 10 - dist;
									double inc = diff / 2;
									ciTraveled -= inc;
									cjTraveled -= inc;
									assert DMath.doubleEquals(ciTraveled, cjTraveled);
									Position adjustedCib = cib.travelTo(cia, inc);
									Position adjustedCjb = cjb.travelTo(cja, inc);
									saveCrashInfo(new CrashInfo(new CrashSite(Position.middle(adjustedCib, adjustedCjb), ciTraveled), ci, cj, cia, cja, adjustedCib, adjustedCjb, iDir, jDir, dist, cie, k+1, cje, l+1));
								}
							} } // l loop
						
						} else {
							// cj is crashed
							Position cjp = cj.getPosition();
							
							double dist = cib.distanceTo(cjp);
							if (DMath.doubleEquals(dist, 10.0)) {
								saveCrashInfo(new CrashInfo(new CrashSite(Position.middle(cib, cjp), ciTraveled), ci, cj, cia, null, cib, cjp, iDir, 0, dist, cie, k+1, -1, -1));
							} else if (dist < 10) {
								double diff = 10 - dist;
								double inc = diff;
								ciTraveled -= inc;
								Position adjustedCib = cib.travelTo(cia, inc);
								saveCrashInfo(new CrashInfo(new CrashSite(Position.middle(adjustedCib, cjp), ciTraveled), ci, cj, cia, null, adjustedCib, cjp, iDir, 0, dist, cie, k+1, -1, -1));
							}
						}
					} } // k loop
				
				} else {
					// ci is crashed
					
					if (cj.getState() != CarState.CRASHED) {
						
						Position cip = ci.getPosition();
						
						double cjTraveled = 0;
						for (int cje = 0; cje < cj.getFuturePath().size(); cje++) {
							List<Position> cjEPath = cj.getFuturePath().get(cje);
						for (int l = 0; l < cjEPath.size()-1; l++) {
							Position cja = cjEPath.get(l);
							Position cjb = cjEPath.get(l+1);
							cjTraveled += cja.distanceTo(cjb);
							
							int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
							
							double dist = cip.distanceTo(cjb);
							if (DMath.doubleEquals(dist, 10.0)) {
								saveCrashInfo(new CrashInfo(new CrashSite(Position.middle(cip, cjb), cjTraveled), ci, cj, null, cja, cip, cjb, 0, jDir, dist, -1, -1, cje, l+1));
							} else if (dist < 10) {
								double diff = 10 - dist;
								double inc = diff;
								cjTraveled -= inc;
								Position adjustedCjb = cjb.travelTo(cja, inc);
								saveCrashInfo(new CrashInfo(new CrashSite(Position.middle(cip, adjustedCjb), cjTraveled), ci, cj, null, cja, cip, adjustedCjb, 0, jDir, dist, -1, -1, cje, l+1));
							}
						} } // l loop
						
					} else {
						// both ci and cj are crashed
						;
					}
					
				}
			} // j loop
		} // i loop
		
	}
	
	
	List<CrashSite> crashSites = new ArrayList<CrashSite>();
	Map<Position, List<CrashInfo>> crashMap = new HashMap<Position, List<CrashInfo>>();
	List<Car> crashedCars = new ArrayList<Car>();
	
	private void saveCrashInfo(CrashInfo ci) {
		CrashSite site = ci.crashSite;
		
		CrashSite samePosition = null;
		for (CrashSite a : crashSites) {
			if (a.p.equals(site.p)) {
				samePosition = a;
			}
		}
		
		if (samePosition == null) {
			// add new
			crashSites.add(site);
			List<CrashInfo> val = crashMap.get(site.p);
			assert val == null;
			val = new ArrayList<CrashInfo>();
			val.add(ci);
			crashMap.put(site.p, val);
		} else if (DMath.doubleEquals(samePosition.time, site.time)) {
			// add existing
			List<CrashInfo> val = crashMap.get(site.p);
			val.add(ci);
		} else if (site.time < samePosition.time) {
			// replace
			crashSites.remove(samePosition);
			List<CrashInfo> val = crashMap.get(samePosition.p);
			val.clear();
			
			crashSites.add(site);
			val.add(ci);
			
		} else {
			// ignore
			;
		}
		
	}
	
	private void processCrashInfo(List<Car> cars) {
		
		for (CrashSite site : crashSites) {
			for (CrashInfo info : crashMap.get(site.p)) {
				//Position crashSite = info.crashSite;
				Car i = info.i;
				Car j = info.j;
				Position ip = info.ip;
				Position jp = info.jp;
				int iDir = info.iDir;
				int jDir = info.jDir;
				//double dist = info.dist;
				int ie = info.ie;
				int ik = info.ik;
				int je = info.je;
				int jl = info.jl;
				
				if (iDir != 0) {
					i.futurePathCrash(ip, ie, ik);
					i.futureState = CarState.CRASHED;
					crashedCars.add(i);
				}
				
				if (jDir != 0) {
					j.futurePathCrash(jp, je, jl);
					j.futureState = CarState.CRASHED;
					crashedCars.add(j);
				}
				
			}
		}
		
		crashSites.clear();
		crashMap.clear();
		
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
