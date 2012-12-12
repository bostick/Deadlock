package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarProximityEvent;
import com.gutabi.deadlock.world.car.CarStateEnum;
import com.gutabi.deadlock.world.car.DrivingEvent;
import com.gutabi.deadlock.world.car.VertexArrivalEvent;
import com.gutabi.deadlock.world.graph.Vertex;

public class CarMap {
	
	private List<Car> cars = new ArrayList<Car>();
	
	public void addCar(Car c) {
		cars.add(c);
	}
	
	public void remove(Car c) {
		cars.remove(c);
	}
	
	public int size() {
		return cars.size();
	}
	
	public Car carHitTest(Point p) {
		for (Car c : cars) {
			if (c.hitTest(p) != null) {
				return c;
			}
		}
		return null;
	}
	
	public void paint(RenderingContext ctxt) {
		for (Car c : cars) {
			c.paint(ctxt);
		}
	}
	
	public void postStop() {
		for (Car c : cars) {
			c.destroy();
		}
		cars.clear();
	}
	
	public void preStep(double t) {
		
		for (Car c : cars) {
			c.preStep(t);
		}
		
		findDeadlockCycles(t);
		
	}
	
	public void postStep(double t) {
		
		List<Car> toBeRemoved = new ArrayList<Car>();
		
		for (Car c : cars) {
			boolean shouldPersist = c.postStep(t);
			if (!shouldPersist) {
				if (APP.world.hilited == c) {
					APP.world.hilited = null;
				}
				c.destroy();
				toBeRemoved.add(c);
			}
		}
	
		cars.removeAll(toBeRemoved);
		
	}
	
	private void findDeadlockCycles(double t) {
		
		/*
		 * find deadlock cycles
		 */
		carLoop:
		for (int i = 0; i < cars.size(); i++) {
			Car ci = cars.get(i);
			
			switch (ci.state) {
			case DRIVING:
			case BRAKING:
			case SINKED:
				
				Car to = findDeadlockCause(ci);
				Car h = findDeadlockCause(findDeadlockCause(ci));
				
				while (true) {
					if (to == null || h == null) {
						continue carLoop;
					}
					if (to.deadlocked || h.deadlocked) {
						continue carLoop;
					}
					if (to == h) {
						break;
					}
					to = findDeadlockCause(to);
					h = findDeadlockCause(findDeadlockCause(h));
				}
				
				to = ci;
				while (true) {
					if (to == h) {
						break;
					}
					to = findDeadlockCause(to);
					h = findDeadlockCause(h);
				}
				
				h = findDeadlockCause(to);
				
				assert h.stoppedTime != -1;
				assert h.state == CarStateEnum.BRAKING;
				h.deadlocked = true;
				
				while (true) {
					if (to == h) {
						break;
					}
					h = findDeadlockCause(h);
					
					assert h.stoppedTime != -1;
					assert h.state == CarStateEnum.BRAKING;
					h.deadlocked = true;
				}
				
				break;
			case CRASHED:
			case SKIDDED:
				
				if (ci.stoppedTime != -1) {
					ci.deadlocked = true;
				}
				
				break;
			}
		}
		
		/*
		 * 
		 */
		for (int i = 0; i < cars.size(); i++) {
			Car ci = cars.get(i);
			
			if (!ci.deadlocked) {
				DrivingEvent e = findDeadlockEvent(ci);
				
				if (e == null) {
					continue;
				}
				
				if (e instanceof CarProximityEvent) {
					
					Car cause = ((CarProximityEvent)e).otherCar;
					if (cause != null &&
							cause.deadlocked) {
						
						if (cause.stoppedTime <= ci.stoppedTime || (t - ci.stoppedTime > Car.COMPLETE_STOP_WAIT_TIME)) {
							
							assert ci.stoppedTime != -1;
							assert ci.state == CarStateEnum.BRAKING;
							ci.deadlocked = true;
							
						}
						
					}
					
				} else if (e instanceof VertexArrivalEvent) {
					
					Car cause = ((VertexArrivalEvent)e).v.carQueue.get(0);
					
					if (cause != ci) {
						
						if (cause != null &&
								cause.deadlocked) {
							
							assert ci.stoppedTime != -1;
							assert ci.state == CarStateEnum.BRAKING;
							ci.deadlocked = true;
							
						}
						
					}
					
				} else {
					assert false;
				}
				
			}
			
		}
		
	}
	
	private DrivingEvent findDeadlockEvent(Car c) {
		if (c == null) {
			return null;
		}
		
		if (c.curVertexArrivalEvent != null) {
			
			Vertex v = c.curVertexArrivalEvent.v;
			
			assert v.carQueue.contains(c);
			
			Car leavingCar = v.carQueue.get(0);
			
			if (leavingCar != c) {
				if (c.stoppedTime != -1 &&
						leavingCar.stoppedTime != -1
						) {
					
					return c.curVertexArrivalEvent;
					
				}
			}
			
		}
		
		if (c.curCarProximityEvent != null) {
			Car next = c.curCarProximityEvent.otherCar;
			
			if (c.stoppedTime != -1 &&
					next.stoppedTime != -1
					) {
				return c.curCarProximityEvent;
			}
		}
		
		return null;
	}
	
	private Car findDeadlockCause(Car c) {
		if (c == null) {
			return null;
		}
		
		if (c.curCarProximityEvent != null) {
			Car next = c.curCarProximityEvent.otherCar;
			
			if (c.stoppedTime != -1 &&
					next.stoppedTime != -1
					) {
				return next;
			}
		}
		
		if (c.curVertexArrivalEvent != null) {
			
			Vertex v = c.curVertexArrivalEvent.v;
			
			assert v.carQueue.contains(c);
			
			Car leavingCar = v.carQueue.get(0);
			
			if (leavingCar != c) {
				if (c.stoppedTime != -1 &&
						leavingCar.stoppedTime != -1
						) {
					
					return leavingCar;
					
				}
			}
			
		}
		
		return null;
	}
	
}
