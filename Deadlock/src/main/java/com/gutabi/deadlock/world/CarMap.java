package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.cars.AutonomousDriver;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarProximityEvent;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.Driver;
import com.gutabi.deadlock.world.cars.DrivingEvent;
import com.gutabi.deadlock.world.cars.VertexArrivalEvent;
import com.gutabi.deadlock.world.graph.Vertex;

public class CarMap {
	
	public final World world;
	
	public List<Car> cars = new ArrayList<Car>();
	
	public CarMap(World world) {
		this.world = world;
	}
	
	public void addCar(Car c) {
		cars.add(c);
	}
	
	public void destroyCar(Car c) {
		
		c.destroy();
		
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
		
		if (APP.DEBUG_DRAW) {
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			
			world.drawPhysicsDebug(ctxt);
			
		}
		
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
			Driver di = cars.get(i).driver;
			
			switch (di.c.state) {
			case DRIVING:
			case BRAKING:
			case SINKED:
				
				AutonomousDriver to = findDeadlockCause((AutonomousDriver)di);
				AutonomousDriver h = findDeadlockCause(findDeadlockCause((AutonomousDriver)di));
				
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
				
				to = (AutonomousDriver)di;
				while (true) {
					if (to == h) {
						break;
					}
					to = findDeadlockCause(to);
					h = findDeadlockCause(h);
				}
				
				h = findDeadlockCause(to);
				
				assert h.stoppedTime != -1;
				assert h.c.state == CarStateEnum.BRAKING;
				h.deadlocked = true;
				
				while (true) {
					if (to == h) {
						break;
					}
					h = findDeadlockCause(h);
					
					assert h.stoppedTime != -1;
					assert h.c.state == CarStateEnum.BRAKING;
					h.deadlocked = true;
				}
				
				break;
			case CRASHED:
			case SKIDDED:
				
				if (((AutonomousDriver)di).stoppedTime != -1) {
					((AutonomousDriver)di).deadlocked = true;
				}
				
				break;
			case IDLE:
			case DRAGGING:
			case COASTING_FORWARD:
			case COASTING_BACKWARD:
				break;
			}
		}
		
		/*
		 * 
		 */
		for (int i = 0; i < cars.size(); i++) {
			Driver di = cars.get(i).driver;
			
			switch (di.c.state) {
			case DRIVING:
			case BRAKING:
			case SINKED:
			case CRASHED:
			case SKIDDED:
				
				if (!((AutonomousDriver)di).deadlocked) {
					DrivingEvent e = findDeadlockEvent((AutonomousDriver)di);
					
					if (e == null) {
						continue;
					}
					
					if (e instanceof CarProximityEvent) {
						
						AutonomousDriver cause = ((CarProximityEvent)e).otherDriver;
						if (cause != null &&
								cause.deadlocked) {
							
							if (cause.stoppedTime <= ((AutonomousDriver)di).stoppedTime || (t - ((AutonomousDriver)di).stoppedTime > AutonomousDriver.COMPLETE_STOP_WAIT_TIME)) {
								
								assert ((AutonomousDriver)di).stoppedTime != -1;
								assert di.c.state == CarStateEnum.BRAKING;
								((AutonomousDriver)di).deadlocked = true;
								
							}
							
						}
						
					} else if (e instanceof VertexArrivalEvent) {
						
						AutonomousDriver cause = ((VertexArrivalEvent)e).v.driverQueue.get(0);
						
						if (cause != di) {
							
							if (cause != null &&
									cause.deadlocked) {
								
								assert ((AutonomousDriver)di).stoppedTime != -1;
								assert di.c.state == CarStateEnum.BRAKING;
								((AutonomousDriver)di).deadlocked = true;
								
							}
							
						}
						
					} else {
						assert false;
					}
					
				}
				
				break;
			case IDLE:
			case DRAGGING:
			case COASTING_FORWARD:
			case COASTING_BACKWARD:
				break;
			}
			
		}
		
	}
	
	private DrivingEvent findDeadlockEvent(AutonomousDriver d) {
		if (d == null) {
			return null;
		}
		
		if (d.curVertexArrivalEvent != null) {
			
			Vertex v = d.curVertexArrivalEvent.v;
			
			assert v.driverQueue.contains(d);
			
			AutonomousDriver leavingDriver = v.driverQueue.get(0);
			
			if (leavingDriver != d) {
				if (d.stoppedTime != -1 &&
						leavingDriver.stoppedTime != -1
						) {
					
					return d.curVertexArrivalEvent;
					
				}
			}
			
		}
		
		if (d.curCarProximityEvent != null) {
			AutonomousDriver next = d.curCarProximityEvent.otherDriver;
			
			if (d.stoppedTime != -1 &&
					next.stoppedTime != -1
					) {
				return d.curCarProximityEvent;
			}
		}
		
		return null;
	}
	
	private AutonomousDriver findDeadlockCause(AutonomousDriver d) {
		if (d == null) {
			return null;
		}
		
		if (d.curCarProximityEvent != null) {
			AutonomousDriver next = d.curCarProximityEvent.otherDriver;
			
			if (d.stoppedTime != -1 &&
					next.stoppedTime != -1
					) {
				return next;
			}
		}
		
		if (d.curVertexArrivalEvent != null) {
			
			Vertex v = d.curVertexArrivalEvent.v;
			
			assert v.driverQueue.contains(d);
			
			AutonomousDriver leavingDriver = v.driverQueue.get(0);
			
			if (leavingDriver != d) {
				if (d.stoppedTime != -1 &&
						leavingDriver.stoppedTime != -1
						) {
					
					return leavingDriver;
					
				}
			}
			
		}
		
		return null;
	}
	
}
