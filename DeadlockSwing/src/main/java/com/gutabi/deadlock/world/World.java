package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.CapsuleSequencePosition;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEventType;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.view.AnimatedExplosion;
import com.gutabi.deadlock.view.AnimatedGrass;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarEventListener;
import com.gutabi.deadlock.world.car.CarProximityEvent;
import com.gutabi.deadlock.world.car.CarStateEnum;
import com.gutabi.deadlock.world.car.DrivingEvent;
import com.gutabi.deadlock.world.car.VertexArrivalEvent;
import com.gutabi.deadlock.world.cursor.Cursor;
import com.gutabi.deadlock.world.cursor.RegularCursor;
import com.gutabi.deadlock.world.graph.Edge;
import com.gutabi.deadlock.world.graph.EdgePosition;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.Intersection;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.graph.VertexPosition;

@SuppressWarnings("static-access")
public class World implements Sweepable {
	
	public final double worldWidth;
	public final double worldHeight;
	
	public double PIXELS_PER_METER_DEBUG = 32.0;
	
	public AABB worldViewport;
	
	public WorldMode mode;
	
	public Cursor cursor;
	public Stroke stroke;
	
	public Stroke debugStroke;
	public Stroke debugStroke2;
	
	public Entity hilited;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
//	public boolean grid;
	
	public BufferedImage quadrantGrass;
	public BufferedImage canvasGrassImage;
	public BufferedImage canvasGraphImage;
	public BufferedImage previewImage;
	
	public static final int PREVIEW_WIDTH = 100;
	public static final int PREVIEW_HEIGHT = 100;
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
	public Map map;
	
	AnimatedGrass animatedGrass1;
	AnimatedGrass animatedGrass2;
	AnimatedGrass animatedGrass3;
	
	public Graph graph = new Graph();
	
	public List<Car> cars = new ArrayList<Car>();
	
	private List<AnimatedExplosion> explosions = new ArrayList<AnimatedExplosion>();
	
//	private List<Point> skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	public AABB aabb;
	
	private static Logger logger = Logger.getLogger(World.class);
	
	public World(int[][] ini) {
		
		int quadrantCols = ini[0].length;
		int quadrantRows = ini.length;
		
		map = new Map(ini);
//		map.computeGridSpacing();
		
		worldWidth = quadrantCols * APP.QUADRANT_WIDTH;
		
		worldHeight = quadrantRows * APP.QUADRANT_HEIGHT;
		
		worldViewport = new AABB(
				-(VIEW.canvas.getWidth() / PIXELS_PER_METER_DEBUG) / 2 + worldWidth/2 ,
				-(VIEW.canvas.getHeight() / PIXELS_PER_METER_DEBUG) / 2 + worldHeight/2,
				VIEW.canvas.getWidth() / PIXELS_PER_METER_DEBUG,
				VIEW.canvas.getHeight() / PIXELS_PER_METER_DEBUG);
		
		
		quadrantGrass = new BufferedImage(
				512,
				512,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D quadrantGrassG2 = quadrantGrass.createGraphics();
		for (int i = 0; i < (int)Math.round(PIXELS_PER_METER_DEBUG * APP.QUADRANT_WIDTH)/32; i++) {
			for (int j = 0; j < (int)Math.round(PIXELS_PER_METER_DEBUG * APP.QUADRANT_HEIGHT)/32; j++) {
				quadrantGrassG2.drawImage(VIEW.sheet,
						32 * i, 32 * j, 32 * i + 32, 32 * j + 32,
						0, 224, 0+32, 224+32, null);
			}
		}
		
		
		mode = WorldMode.REGULAR;
		
		cursor = new RegularCursor();
		
		stats = new Stats();
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		computeAABB();
		
//		animatedGrass1 = new AnimatedGrass(new Point(worldWidth/4, worldHeight/4));
		
//		animatedGrass2 = new AnimatedGrass(new Point(3*WORLD_WIDTH/4, 2*WORLD_HEIGHT/4));
//		animatedGrass3 = new AnimatedGrass(new Point(WORLD_WIDTH/4, 3*WORLD_HEIGHT/4));
//		
//		WorldSource a = new WorldSource(new Point(WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
//		WorldSource b = new WorldSource(new Point(2*WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
//		WorldSource t1 = new WorldSource(new Point(3*WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
//		
//		WorldSource c = new WorldSource(new Point(0, WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSource d = new WorldSource(new Point(0, 2*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSource t2 = new WorldSource(new Point(0, 3*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		
//		WorldSink e = new WorldSink(new Point(WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
//		WorldSink f = new WorldSink(new Point(2*WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
//		WorldSink t3 = new WorldSink(new Point(3*WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
//		
//		WorldSink g = new WorldSink(new Point(WORLD_WIDTH, WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSink h = new WorldSink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSink t4 = new WorldSink(new Point(WORLD_WIDTH, 3*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		
//		a.matchingSink = e;
//		e.matchingSource = a;
//		
//		b.matchingSink = f;
//		f.matchingSource = b;
//		
//		t1.matchingSink = t3;
//		t3.matchingSource = t1;
//		
//		c.matchingSink = g;
//		g.matchingSource = c;
//		
//		d.matchingSink = h;
//		h.matchingSource = d;
//		
//		t2.matchingSink = t4;
//		t4.matchingSource = t2;
//		
//		graph.addVertexTop(a);
//		graph.addVertexTop(b);
//		graph.addVertexTop(t1);
//		
//		graph.addVertexTop(c);
//		graph.addVertexTop(d);
//		graph.addVertexTop(t2);
//		
//		graph.addVertexTop(e);
//		graph.addVertexTop(f);
//		graph.addVertexTop(t3);
//		
//		graph.addVertexTop(g);
//		graph.addVertexTop(h);
//		graph.addVertexTop(t4);
		
	}
	
	public void init() throws Exception {
		
	}
	
	public void postDisplay() {
		
		canvasGrassImage = new BufferedImage(VIEW.canvas.getWidth(), VIEW.canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		canvasGraphImage = new BufferedImage(VIEW.canvas.getWidth(), VIEW.canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		previewImage = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
	}
	
	
	
	public Quadrant findQuadrant(Point p) {
		return map.findQuadrant(p);
	}
	
	public Quadrant upFixPoint(Quadrant q) {
		return map.upFixPoint(q);
	}
	
	public Quadrant leftFixPoint(Quadrant q) {
		return map.leftFixPoint(q);
	}
	
	public Quadrant rightFixPoint(Quadrant q) {
		return map.rightFixPoint(q);
	}
	
	public Quadrant downFixPoint(Quadrant q) {
		return map.downFixPoint(q);
	}
	
	
	
	public void addVertexTop(Vertex v) {
		Set<Vertex> affected = graph.addVertexTop(v);
		
		postIdleTop(affected);
	}
	
	public void createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		Set<Vertex> affected = graph.createRoadTop(start, end, pts);
		
		postDraftingTop(affected);
		
//		assert checkConsistency();
	}
	
	public Vertex splitRoadTop(RoadPosition pos) {
		return graph.split(pos);
	}
	
	public void removeVertexTop(Vertex v) {
		Set<Vertex> affected = graph.removeVertexTop(v);
		
		postIdleTop(affected);
	}
	
	public void removeRoadTop(Road e) {
		Set<Vertex> affected = graph.removeRoadTop(e);
		
		postIdleTop(affected);
	}
	
	public void removeMergerTop(Merger m) {
		Set<Vertex> affected = graph.removeMergerTop(m);
		
		postIdleTop(affected);
	}
	
	public void removeStopSignTop(StopSign s) {
		s.e.removeStopSignTop(s);
	}
	
	public void removeCarTop(Car c) {
		
		c.destroy();
		
		synchronized (APP) {
			cars.remove(c);
		}
		
		postRunningTop();
	}
	
	public void insertMergerTop(Point p) {
		Set<Vertex> affected = graph.insertMergerTop(p);
		
		postIdleTop(affected);
		
	}
	
	
	
	
	
	
	public void startRunning() {
		
		mode = WorldMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		
		mode = WorldMode.REGULAR;
		
	}
	
	public void pauseRunning() {
		
		mode = WorldMode.PAUSED;
	}
	
	public void unpauseRunning() {
		
		mode = WorldMode.RUNNING;
		
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public void processNewStroke(Stroke s) {
		
		/*
		 * TODO:
		 * fix this and turn on
		 * a stroke that starts/ends on a fixture may technically be outside of the quadrant, but it should still count
		 */
//		for (int i = 0; i < s.size()-1; i++) {
//			Capsule cap = new Capsule(null, s.getCircle(i), s.getCircle(i+1));
//			if (!completelyContains(cap)) {
//				return;
//			}
//		}
		
		List<SweepEvent> events = s.events(true);
		
		/*
		 * go through and find any merger events and fixture events
		 */
		for (int i = 0; i < events.size(); i++) {
			SweepEvent e = events.get(i);
			if (e.type == SweepEventType.ENTERMERGER) {
				return;
			}
//			if (e.type == SweepEventType.ENTERVERTEX) {
//				Vertex v = (Vertex)e.shape.parent;
//				if (v instanceof Fixture && (v.roads.size() + (v.m!=null?1:0)) > 0) {
//					assert ((v.roads.size() + (v.m!=null?1:0))) == 1;
//					return;
//				}
//			}
			if (i < events.size()-1) {
				SweepEvent f = events.get(i+1);
				if (e.type == SweepEventType.EXITVERTEX && f.type == SweepEventType.ENTERVERTEX) {
					Vertex ev = (Vertex)e.still.parent;
					Vertex fv = (Vertex)f.still.parent;
					if (ev.m != null && ev.m == fv.m) {
						/*
						 * FIXME: this currently disallows connecting vertices in a merger, even outside the merger
						 * fix this so that it's only a road within the merger that is disallowed
						 */
						return;
					}
				}
			}
		}
		
		/*
		 * go through and create or split where needed to make sure vertices are present
		 */
		for (int i = 0; i < events.size(); i++) {
			
			SweepEvent e = events.get(i);
			
			if (e.type == null) {
				
				Entity hit = pureGraphBestHitTestCircle(e.circle);
//				assert hit == null;
				
				if (hit == null) {
					logger.debug("create");
					Intersection v = new Intersection(e.p);
					addVertexTop(v);
				}
				
//				e.setVertex(v);
				
			} else if (e.type == SweepEventType.ENTERROADCAPSULE || e.type == SweepEventType.EXITROADCAPSULE) {
				
				Entity hit = pureGraphBestHitTestCircle(e.circle);
				
				if (hit instanceof Vertex) {
//					e.setVertex((Vertex)hit);
					continue;
				}
				
				GraphPosition pos = null;
				
				double nextEventCombo;
				if (e.type == SweepEventType.ENTERROADCAPSULE) {
					if (i < events.size()-1) {
						nextEventCombo = events.get(i+1).combo;
					} else {
						nextEventCombo = Double.POSITIVE_INFINITY;
					}
				} else {
					if (i >= 1) {
						nextEventCombo = events.get(i-1).combo;
					} else {
						nextEventCombo = Double.NEGATIVE_INFINITY;
					}
				}
				
				/*
				 * find better place to split by checking for intersection with road
				 */
				Circle a;
				Circle b;
				/*
				 * e could be like 6.9999999999, and we don't want b to be the next index, 7, which is the same point
				 * so do some massaging here
				 */
				double eCombo = e.combo;
				int j = (int)Math.floor(eCombo);
				if (e.type == SweepEventType.ENTERROADCAPSULE) {
					a = e.circle;
					if (DMath.lessThan(nextEventCombo, j+1)) {
						/*
						 * next event is before next stroke point, so use the event circle
						 */
						b = events.get(i+1).circle;
					} else {
						b = s.getCircle(j+1);
					}
				} else {
					if (DMath.greaterThan(nextEventCombo, j)) {
						a = events.get(i-1).circle;
					} else {
						a = s.getCircle(j);
					}
					b = e.circle;
				}
				
				while (true) {
					
					hit = pureGraphBestHitTestCapsule(new Capsule(null, a, b, -1));
					
					if (hit == null) {
						
					} else if (hit instanceof Vertex) {
						pos = new VertexPosition((Vertex)hit);
						break;
					} else {
						
						RoadPosition skeletonIntersection = (RoadPosition)((Road)hit).findSkeletonIntersection(a.center, b.center);
						
						if (skeletonIntersection != null) {
							
							/*
							 * FIXME: see stroke section
							 */
//							if (DMath.lessThanEquals(Point.distance(skeletonIntersection.p, e.p), Stroke.STROKE_RADIUS + Stroke.STROKE_RADIUS + 0.2)) {
//								
//								pos = skeletonIntersection;
//								
//								logger.debug("found intersection");
//								
//								break;
//							}
							
							pos = skeletonIntersection;
							
							logger.debug("found intersection");
							
							break;
							
						}
						
					}
					
					j += (e.type == SweepEventType.ENTERROADCAPSULE) ? 1 : -1;
					
					if (!((e.type == SweepEventType.ENTERROADCAPSULE) ? j < Math.min(nextEventCombo, s.size()-1) : j >= Math.max(nextEventCombo, 0))) {
						break;
					}
					
					a = s.getCircle(j);
					b = s.getCircle(j+1);
					
				}
				
				if (pos == null) {
					logger.debug("pos was null");
					pos = findClosestRoadPosition(e.p, e.circle.radius);
				}
				
				assert pos != null;
				
				Entity hit2;
				if (pos instanceof EdgePosition) {
					hit2 = pureGraphBestHitTestCircle(new Circle(null, pos.p, e.circle.radius));
				} else {
//					assert false;
					hit2 = ((VertexPosition)pos).v;
				}
				
				if (hit2 instanceof Road) {
					Vertex v = splitRoadTop((RoadPosition)pos);
					
					assert ShapeUtils.intersectCC(e.circle, v.getShape());
					
//					e.setVertex(v);
				} else {
//					e.setVertex((Vertex)hit2);
				}
				
			} else if (e.type == SweepEventType.ENTERVERTEX || e.type == SweepEventType.EXITVERTEX) {
//				e.setVertex((Vertex)e.shape.parent);
			} else if (e.type == SweepEventType.ENTERSTROKE || e.type == SweepEventType.EXITSTROKE) {
				
				CapsuleSequence sub = s.seq.subsequence(e.index);
				
				CapsuleSequencePosition pos = null;
				
				double nextEventCombo;
				if (e.type == SweepEventType.ENTERSTROKE) {
					if (i < events.size()-1) {
						nextEventCombo = events.get(i+1).combo;
					} else {
						nextEventCombo = Double.POSITIVE_INFINITY;
					}
				} else {
					if (i >= 1) {
						nextEventCombo = events.get(i-1).combo;
					} else {
						nextEventCombo = Double.NEGATIVE_INFINITY;
					}
				}
				
				/*
				 * find better place to split by checking for intersection with stroke
				 */
				Circle a;
				Circle b;
				
				/*
				 * e could be like 6.9999999999, and we don't want b to be the next index, 7, which is the same point
				 * so do some massaging here
				 */
				double eCombo = e.combo;
				int j = (int)Math.floor(eCombo);
				
				if (e.type == SweepEventType.ENTERSTROKE) {
					a = e.circle;
					if (DMath.lessThan(nextEventCombo, j+1)) {
						/*
						 * next event is before next stroke point, so use the event circle
						 */
						b = events.get(i+1).circle;
					} else {
						b = s.getCircle(j+1);
					}
				} else {
					if (DMath.greaterThan(nextEventCombo, j)) {
						a = events.get(i-1).circle;
					} else {
						a = s.getCircle(j);
					}
					b = e.circle;
				}
				
				while (true) {
					
//					hit = strokeBestHitTestCapsule(new Capsule(null, a, b, -1));
					
					CapsuleSequencePosition skeletonIntersection = sub.findSkeletonIntersection(a.center, b.center);
					
					if (skeletonIntersection != null) {
						
						/*
						 * FIXME:
						 * 
						 * the 0.3 is a hack, because an intersection won't always be touching the event
						 * 
						 * here:
						 * 
						 * Graphics[{capsule[#, r] & /@ 
   Partition[{{5.5, 5.5}, {10, 10}, {10, 2}, {6.5, 6.5}, {2, 10}}, 2, 
    1], Blue, Point[{6.5, 6.5}], Point[{7.375, 5.375}], 
  Circle[{7.375, 5.375}, r]}]
  
  							figure out exactly what to do here
						 */
//						double dist = Point.distance(skeletonIntersection.p, e.p);
//						if (DMath.lessThanEquals(dist, Stroke.STROKE_RADIUS + Stroke.STROKE_RADIUS + 0.2)) {
//							
//							pos = skeletonIntersection;
//							
//							logger.debug("found intersection");
//							
//							break;
//						}
						pos = skeletonIntersection;
						
						logger.debug("found intersection");
						
						break;
					}
					
					j += (e.type == SweepEventType.ENTERSTROKE) ? 1 : -1;
					
					if (!((e.type == SweepEventType.ENTERSTROKE) ? j < Math.min(nextEventCombo, s.size()-1) : j >= Math.max(nextEventCombo, 0))) {
						break;
					}
					
					a = s.getCircle(j);
					b = s.getCircle(j+1);
					
				}
				
				if (pos == null) {
					logger.debug("pos was null");
//					pos = sub.findClosestGraphPosition(e.p, e.circle.radius);
//					pos = sub.findClosestStrokePosition(e.p, e.circle.radius);
					
//					Capsule stillCapsule = (Capsule)e.still;
//					Point stillA = stillCapsule.a;
//					Point stillB = stillCapsule.b;
//					
//					Point.p
					
					pos = new CapsuleSequencePosition(s.seq, e.index, e.param);
					
				}
				
				assert pos != null;
				
				Entity hit = pureGraphBestHitTestCircle(new Circle(null, pos.p, e.circle.radius));
				
				if (hit == null) {
					
					logger.debug("create");
					Intersection v = new Intersection(pos.p);
					addVertexTop(v);
					
//					assert ShapeUtils.intersectCC(e.circle, v.getShape());
					
//					e.setVertex(v);
					
				} else {
					
//					e.setVertex((Vertex)hit);
					
				}
				
			} else {
				assert false;
			}
			
//			assert e.getVertex() != null;
		}
		
		/*
		 * run events again to pick up any new vertices from now having vertices around
		 * 
		 * should only be connecting vertices here
		 * 
		 */
		events = s.events(false);
		for (int i = 0; i < events.size(); i++) {
			
			SweepEvent e = events.get(i);
			
			if (e.type == SweepEventType.ENTERVERTEX || e.type == SweepEventType.EXITVERTEX) {
				e.setVertex((Vertex)e.still.parent);
			} else {
				assert false;
			}
			
		}
		
		/*
		 * now go through and create roads
		 */
		for (int i = 0; i < events.size()-1; i++) {
			
//			if (i == 0) {
//				return;
//			}
			
			SweepEvent e0 = events.get(i);
			SweepEvent e1 = events.get(i+1);
			
			if (e0.type == SweepEventType.ENTERVERTEX && e1.type == SweepEventType.EXITVERTEX) {
				
				logger.debug("skipping");
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			} else if (e0.type == SweepEventType.ENTERROADCAPSULE && e1.type == SweepEventType.EXITROADCAPSULE) {
				
				logger.debug("skipping");
				
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			}
			
			Vertex v0 = e0.getVertex();
			Vertex v1 = e1.getVertex();
			
//			if (v0 == v1) {
//				logger.debug("same vertex");
//				continue;
//			}
			
			List<Point> roadPts = new ArrayList<Point>();
			roadPts.add(v0.p);
			for (int j = e0.index+1; j < e1.index; j++) {
				roadPts.add(s.get(j));
			}
			if (e1.index >= e0.index+1) {
				if (!DMath.equals(e1.param, 0.0)) {
					roadPts.add(s.get(e1.index));
					roadPts.add(v1.p);
				} else {
					roadPts.add(v1.p);
				}
			} else {
				assert e1.index == e0.index;
				roadPts.add(v1.p);
			}
			
			createRoadTop(v0, v1, roadPts);
			
//			if (i == 0) {
//				return;
//			}
		}
		
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		worldViewport = new AABB(
				worldViewport.x + worldDP.x,
				worldViewport.y + worldDP.y,
				worldViewport.width,
				worldViewport.height);
	}
	
	public void zoom(double factor) {
		
		Point center = new Point(worldViewport.x + worldViewport.width / 2, worldViewport.y + worldViewport.height / 2);
		
		PIXELS_PER_METER_DEBUG = factor * PIXELS_PER_METER_DEBUG; 
		
		double newWidth = VIEW.canvas.getWidth() / PIXELS_PER_METER_DEBUG;
		double newHeight = VIEW.canvas.getHeight() / PIXELS_PER_METER_DEBUG;
		
		worldViewport = new AABB(center.x - newWidth/2, center.y - newHeight/2, newWidth, newHeight);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	public void qKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case STRAIGHTEDGECURSOR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.qKey();
			break;
		}
	}
	
	public void wKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case FIXTURECURSOR:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.wKey();
			break;
		}
	}
	
	public void gKey() {
		
		map.toggleGrid();
		
		render();
		VIEW.repaintCanvas();
	}
	
	public void deleteKey() {
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					removeCarTop(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					removeVertexTop(v);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					removeRoadTop(e);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					removeMergerTop(e);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		render();
		VIEW.repaintCanvas();
		VIEW.repaintControlPanel();
		
	}
	
	public void insertKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.insertKey();
			break;
		}
	}
	
	public void escKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.escKey();
			break;
		}
	}
	
	public void d1Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case REGULAR:
		case CIRCLECURSOR:
		case FIXTURECURSOR:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case QUADCURSOR:
			cursor.d1Key();
			break;
		}
	}
	
	public void d2Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case REGULAR:
		case CIRCLECURSOR:
		case FIXTURECURSOR:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case QUADCURSOR:
			cursor.d2Key();
			break;
		}
	}
	
	public void d3Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case REGULAR:
		case CIRCLECURSOR:
		case FIXTURECURSOR:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case QUADCURSOR:
			cursor.d3Key();
			break;
		}
	}
	
	public void plusKey() {
		
		zoom(1.1);
		
		lastMovedOrDraggedWorldPoint = canvasToWorld(VIEW.canvas.lastMovedOrDraggedCanvasPoint);
		
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.plusKey();
			break;
		}
		
	}
	
	public void minusKey() {
		
		zoom(0.9);
		
		lastMovedOrDraggedWorldPoint = canvasToWorld(VIEW.canvas.lastMovedOrDraggedCanvasPoint);
		
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.minusKey();
			break;
		}
		
	}
	
	public void aKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case CIRCLECURSOR:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case FIXTURECURSOR:
		case QUADCURSOR:
			cursor.aKey();
			break;
		}
	}
	
	public void sKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case QUADCURSOR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case CIRCLECURSOR:
		case STRAIGHTEDGECURSOR:
			cursor.sKey();
			break;
		}
	}
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / PIXELS_PER_METER_DEBUG + worldViewport.x,
				p.y / PIXELS_PER_METER_DEBUG + worldViewport.y);
	}
	
//	public int metersToPixels(double m) {
//		return (int)(Math.round(m * VIEW.PIXELS_PER_METER_DEBUG));
//	}
	
	public Point previewToWorld(Point p) {
		return new Point((worldWidth / PREVIEW_WIDTH) * p.x, (worldHeight / PREVIEW_HEIGHT) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		return new Point((PREVIEW_WIDTH / worldWidth) * p.x, (PREVIEW_HEIGHT / worldHeight) * p.y);
	}
	
	public Point lastPressedWorldPoint;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressedWorldPoint = canvasToWorld(p);
		lastDraggedWorldPoint = null;
		
//		switch (mode) {
//		case DRAFTING:
//		case FIXTURECURSOR:
//		case IDLE:
//		case MERGERCURSOR:
//		case PAUSED:
//		case RUNNING:
//		case STRAIGHTEDGECURSOR:
//			break;
//		case QUADCURSOR:
//			QuadCursor qc = (QuadCursor)cursor;
//			qc.pressed(ev);
//			break;
//		case CIRCLECURSOR:
//			CircleCursor cc = (CircleCursor)cursor;
//			cc.pressed(ev);
//			break;
//		}
		
	}
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	public void dragged(InputEvent ev) {
		
		Point p = ev.p;
		
		lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
		lastDraggedWorldPoint = canvasToWorld(p);
		lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case QUADCURSOR:
		case CIRCLECURSOR:
			cursor.dragged(ev);
			break;
		}
		
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case QUADCURSOR:
		case CIRCLECURSOR:
			cursor.released(ev);
			break;
		}
	}
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		Point p = ev.p;
		
		lastMovedWorldPoint = canvasToWorld(p);
		lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:			
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case QUADCURSOR:
		case CIRCLECURSOR:
			cursor.moved(ev);
			break;	
		}
	}
	
//	public void entered(InputEvent ev) {
//		
//		Point p = ev.p;
//		
//		lastMovedWorldPoint = canvasToWorld(p);
//		
//		switch (mode) {
//		case PAUSED:
//		case DRAFTING:
//		case RUNNING:
//			break;
//		case IDLE:
//		case MERGERCURSOR:
//		case FIXTURECURSOR:
//		case STRAIGHTEDGECURSOR:
//			
//			if (grid) {
//				
//				Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
//				cursor.setPoint(closestGridPoint);
//				
//			} else {
//				cursor.setPoint(lastMovedWorldPoint);
//			}
//			
//			VIEW.repaintCanvas();
//			break;
//			
//		case CIRCLECURSOR:
//			CircleCursor cc = (CircleCursor)cursor;
//			cc.entered(ev);
//			break;
//		case QUADCURSOR:
//			QuadCursor qc = (QuadCursor)cursor;
//			qc.entered(ev);
//			break;
//		}
//		
//	}
	
	public void exited(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case REGULAR:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case CIRCLECURSOR:
		case QUADCURSOR:
			cursor.exited(ev);
			break;
		}
	}
	








	
	
	
	
	
	
	
	
	
	
	public void addExplosion(AnimatedExplosion x) {
		
		if (explosions.size() == 100) {
			
			explosions.remove(0);
			
		} else {
			assert explosions.size() < 100;
		}
		
		explosions.add(x);
		
		assert explosions.size() <= 100;
	}
	
	
	
	
	
	
	
	public List<SweepEvent> sweepStart(Circle c) {
		return graph.sweepStart(c);
	}
	
	public List<SweepEvent> sweep(Capsule s) {
		return graph.sweep(s);
	}
	
	
	
	
	
	
	
	
	private void postIdleTop(Set<Vertex> affected) {
		
		graph.computeVertexRadii(affected);
		
		computeAABB();
	}
	
	public void postDraftingTop(Set<Vertex> affected) {
		
		graph.computeVertexRadii(affected);
		
		computeAABB();
	}
	
	private void postRunningTop() {
		;
	}
	
	private void computeAABB() {
		aabb = map.aabb;
		aabb = AABB.union(aabb, graph.getAABB());
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	
	
	/*
	 * run before game loop start
	 */
	public void preStart() {
		
//		renderSkidMarksFresh();
//		renderSkidMarksIncremental();
//		skidMarks = new ArrayList<Point>();
		
		if (animatedGrass1 != null) {
			animatedGrass1.preStart();
		}
		if (animatedGrass2 != null) {
			animatedGrass2.preStart();
		}
		if (animatedGrass3 != null) {
			animatedGrass3.preStart();
		}
		
		graph.preStart();
		
		t = 0;
	}
	
	/*
	 * run after game loop stops
	 */
	public void postStop() {
		
		graph.postStop();
		
		synchronized (APP) {
			for (Car c : cars) {
				c.destroy();
			}
			cars.clear();
			
			explosions.clear();
			
		}
		
//		skidMarks.clear();
	}
	
	
	
	int velocityIterations = 6;
	int positionIterations = 2;
	
	public void integrate(double t) {
		
		this.t = t;
		
		preStep();
		
		b2dWorld.step((float)APP.dt, velocityIterations, positionIterations);
		
		postStep();
		
	}
	
	private void preStep() {
		
		if (animatedGrass1 != null) {
			animatedGrass1.preStep(t);
		}
		if (animatedGrass2 != null) {
			animatedGrass2.preStep(t);
		}
		if (animatedGrass3 != null) {
			animatedGrass3.preStep(t);
		}
		
		graph.preStep(t);
		
		for (Car c : cars) {
			c.preStep(t);
		}
		
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
		
		for (AnimatedExplosion x : explosions) {
			x.preStep(t);
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
	
	
	
	
	
	public void addCar(Car c) {
		cars.add(c);
	}
	
	private void postStep() {
		
		List<Car> toBeRemoved = new ArrayList<Car>();
		
		synchronized (APP) {
			
			for (Car c : cars) {
				boolean shouldPersist = c.postStep(t);
				if (!shouldPersist) {
					if (hilited == c) {
						hilited = null;
					}
					c.destroy();
					toBeRemoved.add(c);
				}
			}
		
			cars.removeAll(toBeRemoved);
			
			List<AnimatedExplosion> exToBeRemoved = new ArrayList<AnimatedExplosion>();
			
			for (AnimatedExplosion e : explosions) {
				boolean shouldPersist = e.postStep(t);
				if (!shouldPersist) {
					exToBeRemoved.add(e);
				}
			}
			
			explosions.removeAll(exToBeRemoved);
			
		}
		
//		if (!skidMarks.isEmpty()) {
////			renderSkidMarksIncremental();
//			skidMarks.clear();
//		}
		
		graph.postStep(t);
		
	}
	
//	public void addSkidMarks(Point a, Point b) {
//		skidMarks.add(a);
//		skidMarks.add(b);
//	}
	
	/**
	 * the next choice to make
	 */
	public Vertex shortestPathChoice(Vertex start, Vertex end) {
		return graph.shortestPathChoice(start, end);
	}
	
	public Vertex randomPathChoice(Edge prev, Vertex start, Vertex end) {
		return graph.randomPathChoice(prev, start, end);
	}
	
	public double distanceBetweenVertices(Vertex start, Vertex end) {
		return graph.distanceBetweenVertices(start, end);
	}
	
	public Entity hitTest(Point p) {
		Car c = carHitTest(p);
		if (c != null) {
			return c;
		}
		Entity h = graphHitTest(p);
		if (h != null) {
			return h;
		}
		return null;
	}
	
	public Car carHitTest(Point p) {
		synchronized (APP) {
			for (Car c : cars) {
				if (c.hitTest(p) != null) {
					return c;
				}
			}
		}
		return null;
	}
	
	public Entity graphHitTest(Point p) {
		return graph.graphHitTest(p);
	}
	
	public Entity pureGraphBestHitTest(Shape s) {
		return graph.pureGraphBestHitTest(s);
	}
	
	public Entity pureGraphBestHitTestQuad(Quad q) {
		return graph.pureGraphBestHitTestQuad(q);
	}
	
	public Entity pureGraphBestHitTestCircle(Circle c) {
		return graph.pureGraphBestHitTestCircle(c);
	}
	
	public Entity pureGraphBestHitTestCapsule(Capsule c) {
		return graph.pureGraphBestHitTestCapsule(c);
	}
	
	public RoadPosition findClosestRoadPosition(Point p, double radius) {
		return graph.findClosestRoadPosition(p, radius);
	}
	
	public boolean completelyContains(Shape s) {
		return map.completelyContains(s);
	}
	
	
	
	
	
	
	
	public void render() {
		assert !Thread.holdsLock(APP);
		
		synchronized (VIEW) {
			Graphics2D canvasGrassImageG2 = canvasGrassImage.createGraphics();
			
			canvasGrassImageG2.setColor(Color.WHITE);
			canvasGrassImageG2.fillRect(0, 0, VIEW.canvas.getWidth(), VIEW.canvas.getHeight());
			
			canvasGrassImageG2.translate((int)(-worldViewport.x * PIXELS_PER_METER_DEBUG), (int)(-worldViewport.y * PIXELS_PER_METER_DEBUG));
			
			canvasGrassImageG2.scale(PIXELS_PER_METER_DEBUG, PIXELS_PER_METER_DEBUG);
			
			RenderingContext canvasGrassContext = new RenderingContext(canvasGrassImageG2, RenderingContextType.CANVAS);
			
			map.renderBackground(canvasGrassContext);
			
			canvasGrassImageG2.dispose();
		}
		
		synchronized (VIEW) {
			Graphics2D canvasGraphImageG2 = canvasGraphImage.createGraphics();
			
			Composite orig = canvasGraphImageG2.getComposite();
			AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC, 0.0f);
			canvasGraphImageG2.setComposite(c);
			canvasGraphImageG2.setColor(new Color(0, 0, 0, 0));
			canvasGraphImageG2.fillRect(0, 0, VIEW.canvas.getWidth(), VIEW.canvas.getHeight());
			canvasGraphImageG2.setComposite(orig);
			
			canvasGraphImageG2.translate((int)((-worldViewport.x) * PIXELS_PER_METER_DEBUG), (int)((-worldViewport.y) * PIXELS_PER_METER_DEBUG));
			
			canvasGraphImageG2.scale(PIXELS_PER_METER_DEBUG, PIXELS_PER_METER_DEBUG);
			
			RenderingContext canvasGraphContext = new RenderingContext(canvasGraphImageG2, RenderingContextType.CANVAS);
			
			graph.renderBackground(canvasGraphContext);
			
			canvasGraphImageG2.dispose();
		}
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		previewImageG2.setColor(Color.WHITE);
		previewImageG2.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		previewImageG2.scale(PREVIEW_WIDTH / worldWidth, PREVIEW_HEIGHT / worldHeight);
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		map.renderBackground(previewContext);
		
		graph.renderBackground(previewContext);
		
		previewImageG2.dispose();
	}
	
	
	
	public void repaint() {
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (mode == WorldMode.RUNNING) {
				return;
			}
		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.scale(PIXELS_PER_METER_DEBUG);
				ctxt.translate(-worldViewport.x, -worldViewport.y);
				
				paintBackground(ctxt);
				
				paintScene(ctxt);
					
				if (APP.DEBUG_DRAW) {
					ctxt.setColor(Color.BLACK);
					ctxt.setWorldPixelStroke(1);
					aabb.draw(ctxt);
					
				}
				
				if (stroke != null) {
					stroke.paint(ctxt);
				}
				
				if (cursor != null) {
					cursor.draw(ctxt);
				}
				
				if (APP.FPS_DRAW) {
					
					ctxt.translate(worldViewport.x, worldViewport.y);
					
					stats.paint(ctxt);
				}
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());
		
	}
	
	private void paintBackground(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		ctxt.translate(worldViewport.x, worldViewport.y);
		
		synchronized (VIEW) {
			ctxt.paintWorldImage(
					0, 0, canvasGrassImage, 0, 0, canvasGrassImage.getWidth(), canvasGrassImage.getHeight(),
					0, 0, canvasGrassImage.getWidth(), canvasGrassImage.getHeight());
		}
		
		ctxt.setTransform(origTransform);
		
		if (animatedGrass1 != null) {
			animatedGrass1.paint(ctxt);
		}
		if (animatedGrass2 != null) {
			animatedGrass2.paint(ctxt);
		}
		if (animatedGrass3 != null) {
			animatedGrass3.paint(ctxt);
		}
		
		origTransform = ctxt.getTransform();
		ctxt.translate(worldViewport.x, worldViewport.y);
		
		synchronized (VIEW) {
			ctxt.paintWorldImage(
					0, 0, canvasGraphImage, 0, 0, canvasGraphImage.getWidth(), canvasGraphImage.getHeight(),
					0, 0, canvasGraphImage.getWidth(), canvasGraphImage.getHeight());
		}
		
		ctxt.setTransform(origTransform);
		
//		drawSkidMarks(g2);
		
	}
	
	private void paintScene(RenderingContext ctxt) {
		
		graph.paintScene(ctxt);
		
		if (APP.DEBUG_DRAW) {
			b2dWorld.setDebugDraw(ctxt);
			b2dWorld.drawDebugData();
		}
		
		List<Car> carsCopy;
		List<AnimatedExplosion> explosionsCopy;
		Entity hilitedCopy;
		synchronized (APP) {
			carsCopy = new ArrayList<Car>(cars);
			explosionsCopy = new ArrayList<AnimatedExplosion>(explosions);
			hilitedCopy = hilited;
		}
		
		for (Car c : carsCopy) {
			c.paint(ctxt);
		}
		
		for (AnimatedExplosion x : explosionsCopy) {
			x.paint(ctxt);
		}
		
		if (hilitedCopy != null) {
			hilitedCopy.paintHilite(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			
			graph.paintIDs(ctxt);
			
		}
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.paintWorldString(0, 0, 1.0, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintWorldString(0, 0, 1.0, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintWorldString(0, 0, 1.0, "car count: " + cars.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintWorldString(0, 0, 1.0, "splosions count: " + explosions.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	
//	static java.awt.Stroke skidMarkStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
//	private void drawSkidMarks(Graphics2D g2) {
//		g2.setColor(Color.BLACK);
//		for (int i = 0; i < skidMarks.size(); i+=2) {
//			Point s0 = skidMarks.get(i);
//			Point s1 = skidMarks.get(i+1);
//			g2.drawLine(
//					(int)(s0.x * MODEL.PIXELS_PER_METER),
//					(int)(s0.y * MODEL.PIXELS_PER_METER),
//					(int)(s1.x * MODEL.PIXELS_PER_METER),
//					(int)(s1.y * MODEL.PIXELS_PER_METER));
//		}
//	}
//	
	
//	private BufferedImage skidMarksImage;
	
//	public void renderSkidMarksFresh() {
//		skidMarksImage = new BufferedImage(
//				(int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER),
//				(int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
//				BufferedImage.TYPE_INT_ARGB);
//		skidMarksImageG2 = skidMarksImage.createGraphics();
//		skidMarksImageG2.setColor(Color.BLACK);
//		skidMarksImageG2.setStroke(skidMarkStroke);
//	}
	
	
	
//	static java.awt.Stroke skidMarkStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	
//	Graphics2D skidMarksImageG2;
	
//	public void renderSkidMarksIncremental() {
//		assert !Thread.holdsLock(MODEL);
//		
//		for (int i = 0; i < skidMarks.size(); i+=2) {
//			Point s0 = skidMarks.get(i);
//			Point s1 = skidMarks.get(i+1);
//			skidMarksImageG2.drawLine(
//					(int)(s0.x * MODEL.PIXELS_PER_METER),
//					(int)(s0.y * MODEL.PIXELS_PER_METER),
//					(int)(s1.x * MODEL.PIXELS_PER_METER),
//					(int)(s1.y * MODEL.PIXELS_PER_METER));
//		}
//		
////		skidMarksImageG2.dispose();
//		
//	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
