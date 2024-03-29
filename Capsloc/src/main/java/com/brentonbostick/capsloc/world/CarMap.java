package com.brentonbostick.capsloc.world;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.cars.AutonomousDriver;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.cars.CarProximityEvent;
import com.brentonbostick.capsloc.world.cars.CarStateEnum;
import com.brentonbostick.capsloc.world.cars.Driver;
import com.brentonbostick.capsloc.world.cars.DrivingEvent;
import com.brentonbostick.capsloc.world.cars.VertexArrivalEvent;
import com.brentonbostick.capsloc.world.graph.Vertex;
import com.brentonbostick.capsloc.world.sprites.CarSheet.CarType;

public class CarMap {
	
	public final World world;
	
	public List<Car> cars = new ArrayList<Car>();
	
	public Car redCar;
	
	public CarMap(World world) {
		this.world = world;
	}
	
	public void addCar(Car c) {
		
		if (c.type == CarType.RED) {
			redCar = c;
		}
		
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
		
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			c.paint(ctxt);
		}
		
	}
	
	public void postStop() {
		for (Car c : cars) {
			c.destroy();
		}
		cars.clear();
	}
	
	public boolean preStep(double t) {
		
		Car.carIDCounter = 0;
		
		boolean res = false;
		
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			res = res | c.preStep(t);
		}
		
		findDeadlockCycles(t);
		
		return res;
	}
	
	
	List<Car> toBeRemoved = new ArrayList<Car>();
	
	public void postStep(double t) {
		
		toBeRemoved.clear();
		
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			boolean shouldPersist = c.postStep(t);
			if (!shouldPersist) {
				c.destroy();
				toBeRemoved.add(c);
			}
		}
		
		for (int i = 0; i < toBeRemoved.size(); i++) {
			Car c = toBeRemoved.get(i);
			cars.remove(c);
		}
		
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
	
	private static DrivingEvent findDeadlockEvent(AutonomousDriver d) {
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
	
	private static AutonomousDriver findDeadlockCause(AutonomousDriver d) {
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
