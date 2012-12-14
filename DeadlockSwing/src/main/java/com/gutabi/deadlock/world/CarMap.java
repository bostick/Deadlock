package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarProximityEvent;
import com.gutabi.deadlock.world.car.CarStateEnum;
import com.gutabi.deadlock.world.car.Driver;
import com.gutabi.deadlock.world.car.DrivingEvent;
import com.gutabi.deadlock.world.car.VertexArrivalEvent;
import com.gutabi.deadlock.world.graph.Vertex;

@SuppressWarnings("static-access")
public class CarMap {
	
	public final World world;
	
	private List<Car> cars = new ArrayList<Car>();
	
	public CarMap(World world) {
		this.world = world;
	}
	
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
	
	public boolean intersect(AABB aabb) {
		
		org.jbox2d.collision.AABB b2dAABB = new org.jbox2d.collision.AABB(aabb.ul.vec2(), aabb.br.vec2());
		
		final boolean[] intersecting = new boolean[1];
		
		world.b2dWorld.queryAABB(new QueryCallback() {
			public boolean reportFixture(org.jbox2d.dynamics.Fixture fixture) {
				intersecting[0] = true;
				return false;
			}
		}, b2dAABB);
		
		return intersecting[0];
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (APP.DEBUG_DRAW) {
			ctxt.setPixelStroke(1);
			world.b2dWorld.setDebugDraw(ctxt);
			world.b2dWorld.drawDebugData();
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
				if (world.hilited == c) {
					world.hilited = null;
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
			Driver di = cars.get(i).driver;
			
			switch (di.c.state) {
			case DRIVING:
			case BRAKING:
			case SINKED:
				
				Driver to = findDeadlockCause(di);
				Driver h = findDeadlockCause(findDeadlockCause(di));
				
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
				
				to = di;
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
				
				if (di.stoppedTime != -1) {
					di.deadlocked = true;
				}
				
				break;
			}
		}
		
		/*
		 * 
		 */
		for (int i = 0; i < cars.size(); i++) {
			Driver di = cars.get(i).driver;
			
			if (!di.deadlocked) {
				DrivingEvent e = findDeadlockEvent(di);
				
				if (e == null) {
					continue;
				}
				
				if (e instanceof CarProximityEvent) {
					
					Driver cause = ((CarProximityEvent)e).otherDriver;
					if (cause != null &&
							cause.deadlocked) {
						
						if (cause.stoppedTime <= di.stoppedTime || (t - di.stoppedTime > Driver.COMPLETE_STOP_WAIT_TIME)) {
							
							assert di.stoppedTime != -1;
							assert di.c.state == CarStateEnum.BRAKING;
							di.deadlocked = true;
							
						}
						
					}
					
				} else if (e instanceof VertexArrivalEvent) {
					
					Driver cause = ((VertexArrivalEvent)e).v.driverQueue.get(0);
					
					if (cause != di) {
						
						if (cause != null &&
								cause.deadlocked) {
							
							assert di.stoppedTime != -1;
							assert di.c.state == CarStateEnum.BRAKING;
							di.deadlocked = true;
							
						}
						
					}
					
				} else {
					assert false;
				}
				
			}
			
		}
		
	}
	
	private DrivingEvent findDeadlockEvent(Driver d) {
		if (d == null) {
			return null;
		}
		
		if (d.curVertexArrivalEvent != null) {
			
			Vertex v = d.curVertexArrivalEvent.v;
			
			assert v.driverQueue.contains(d);
			
			Driver leavingDriver = v.driverQueue.get(0);
			
			if (leavingDriver != d) {
				if (d.stoppedTime != -1 &&
						leavingDriver.stoppedTime != -1
						) {
					
					return d.curVertexArrivalEvent;
					
				}
			}
			
		}
		
		if (d.curCarProximityEvent != null) {
			Driver next = d.curCarProximityEvent.otherDriver;
			
			if (d.stoppedTime != -1 &&
					next.stoppedTime != -1
					) {
				return d.curCarProximityEvent;
			}
		}
		
		return null;
	}
	
	private Driver findDeadlockCause(Driver d) {
		if (d == null) {
			return null;
		}
		
		if (d.curCarProximityEvent != null) {
			Driver next = d.curCarProximityEvent.otherDriver;
			
			if (d.stoppedTime != -1 &&
					next.stoppedTime != -1
					) {
				return next;
			}
		}
		
		if (d.curVertexArrivalEvent != null) {
			
			Vertex v = d.curVertexArrivalEvent.v;
			
			assert v.driverQueue.contains(d);
			
			Driver leavingDriver = v.driverQueue.get(0);
			
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
