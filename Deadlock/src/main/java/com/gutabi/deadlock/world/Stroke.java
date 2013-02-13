package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Capsule;
import com.gutabi.deadlock.geom.CapsuleSequence;
import com.gutabi.deadlock.geom.CapsuleSequencePosition;
import com.gutabi.deadlock.geom.CapsuleSequenceSweepEvent;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.SweepEvent;
import com.gutabi.deadlock.geom.SweepEventType;
import com.gutabi.deadlock.geom.SweepUtils;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.graph.EdgePosition;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.Intersection;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.graph.VertexPosition;

public class Stroke {
	
	public static final double STROKE_RADIUS = Vertex.INIT_VERTEX_RADIUS;
	
	World world;
	
	private List<Circle> cs;
	
	private boolean finished;
	public List<Capsule> caps;
	public CapsuleSequence seq;
	
	public Stroke(World world) {
		this.world = world;
		cs = new ArrayList<Circle>();
	}
	
	private AABB aabb;
	
	public final AABB getAABB() {
		return aabb;
	}
	
	public void add(Point p) {
		assert !finished;
		
		cs.add(APP.platform.createShapeEngine().createCircle(p, STROKE_RADIUS));
		
		computeAABB();
	}
	
	public Point get(int index) {
		return cs.get(index).center;
	}
	
	public Circle getCircle(int index) {
		return cs.get(index);
	}
	
	public int size() {
		return cs.size();
	}
	
	public void finish() {
		assert !finished;
		
		caps = new ArrayList<Capsule>();
		for (int i = 0; i < cs.size()-1; i++) {
			Circle a = cs.get(i);
			Circle b = cs.get(i+1);
			caps.add(APP.platform.createShapeEngine().createCapsule(a, b));
		}
		seq = new CapsuleSequence(caps);
		
		finished = true;
	}
	
	public Set<Vertex> processNewStroke() {
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
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
		
		List<SweepEvent> events = events(true);
		
		/*
		 * go through and find any merger events and fixture events
		 */
		for (int i = 0; i < events.size(); i++) {
			SweepEvent e = events.get(i);
			if (e.type == SweepEventType.ENTERMERGER) {
				return affected;
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
					Vertex ev = (Vertex)e.stillParent;
					Vertex fv = (Vertex)f.stillParent;
					if (ev.m != null && ev.m == fv.m) {
						/*
						 * FIXME: this currently disallows connecting vertices in a merger, even outside the merger
						 * fix this so that it's only a road within the merger that is disallowed
						 */
						return affected;
					}
				}
			}
		}
		
		/*
		 * go through and create or split where needed to make sure vertices are present
		 */
		for (int i = 0; i < events.size(); i++) {
			
			CapsuleSequenceSweepEvent e = (CapsuleSequenceSweepEvent)events.get(i);
			
			if (e.type == null) {
				
				Entity hit = world.graph.pureGraphIntersectCircle(e.circle);
				
				if (hit == null) {
					Intersection i0 = new Intersection(world, e.p);
					Set<Vertex> res = world.addIntersection(i0);
					affected.addAll(res);
				}
				
			} else if (e.type == SweepEventType.ENTERROAD || e.type == SweepEventType.EXITROAD) {
				
				Entity hit = world.graph.pureGraphIntersectCircle(e.circle);
				
				if (hit instanceof Vertex) {
					continue;
				}
				
				GraphPosition pos = null;
				
				double nextEventCombo;
				if (e.type == SweepEventType.ENTERROAD) {
					if (i < events.size()-1) {
						SweepEvent nextEvent = events.get(i+1);
						nextEventCombo = nextEvent.combo;
					} else {
						nextEventCombo = Double.POSITIVE_INFINITY;
					}
				} else {
					if (i >= 1) {
						SweepEvent nextEvent = events.get(i-1);
						nextEventCombo = nextEvent.combo;
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
				if (e.type == SweepEventType.ENTERROAD) {
					a = e.circle;
					if (DMath.lessThan(nextEventCombo, j+1)) {
						/*
						 * next event is before next stroke point, so use the event circle
						 */
						b = ((CapsuleSequenceSweepEvent)events.get(i+1)).circle;
					} else {
						b = getCircle(j+1);
					}
				} else {
					if (DMath.greaterThan(nextEventCombo, j)) {
						a = ((CapsuleSequenceSweepEvent)events.get(i-1)).circle;
					} else {
						a = getCircle(j);
					}
					b = e.circle;
				}
				
				while (true) {
					
					hit = world.graph.pureGraphIntersectCapsule(APP.platform.createShapeEngine().createCapsule(a, b));
					
					if (hit == null) {
						
					} else if (hit instanceof Vertex) {
						pos = new VertexPosition((Vertex)hit);
						break;
					} else {
						
						RoadPosition skeletonIntersection = (RoadPosition)((Road)hit).findSkeletonIntersection(a.center, b.center);
						
						if (skeletonIntersection != null) {
							
							pos = skeletonIntersection;
							
							break;
							
						}
						
					}
					
					if (e.type == SweepEventType.ENTERROAD) {
						j++;
						if (!(j < Math.min(nextEventCombo, size()-1))) {
							break;
						}
					} else {
						j--;
						if (!(j >= Math.max(nextEventCombo, 0))) {
							break;
						}
					}
					
					a = getCircle(j);
					b = getCircle(j+1);
					
				}
				
				if (pos == null) {
					pos = world.graph.findClosestRoadPosition(e.p, e.circle.radius);
				}
				
				assert pos != null;
				
				Entity hit2;
				if (pos instanceof EdgePosition) {
					hit2 = world.graph.pureGraphIntersectCircle(APP.platform.createShapeEngine().createCircle(pos.p, e.circle.radius));
				} else {
					hit2 = ((VertexPosition)pos).v;
				}
				
				if (hit2 instanceof Road) {
					world.splitRoad((RoadPosition)pos);
					
//					assert ShapeUtils.intersectCC(e.circle, v.getShape());
					
				} else {
					
				}
				
			} else if (e.type == SweepEventType.ENTERVERTEX || e.type == SweepEventType.EXITVERTEX) {
				
			} else if (e.type == SweepEventType.ENTERSTROKE || e.type == SweepEventType.EXITSTROKE) {
				
				CapsuleSequence sub = seq.subsequence(e.index);
				
				CapsuleSequencePosition pos = null;
				
				double nextEventCombo;
				if (e.type == SweepEventType.ENTERSTROKE) {
					if (i < events.size()-1) {
						SweepEvent nextEvent = events.get(i+1);
						nextEventCombo = nextEvent.combo;
					} else {
						nextEventCombo = Double.POSITIVE_INFINITY;
					}
				} else {
					if (i >= 1) {
						SweepEvent nextEvent = events.get(i-1);
						nextEventCombo = nextEvent.combo;
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
						b = ((CapsuleSequenceSweepEvent)events.get(i+1)).circle;
					} else {
						b = getCircle(j+1);
					}
				} else {
					if (DMath.greaterThan(nextEventCombo, j)) {
						a = ((CapsuleSequenceSweepEvent)events.get(i-1)).circle;
					} else {
						a = getCircle(j);
					}
					b = e.circle;
				}
				
				while (true) {
					
					CapsuleSequencePosition skeletonIntersection = sub.findSkeletonIntersection(a.center, b.center);
					
					if (skeletonIntersection != null) {
						
						pos = skeletonIntersection;
						
						break;
					}
					
					
					if (e.type == SweepEventType.ENTERSTROKE) {
						j++;
						if (!(j < Math.min(nextEventCombo, size()-1))) {
							break;
						}
					} else {
						j--;
						if (!(j >= Math.max(nextEventCombo, 0))) {
							break;
						}
					}
					
					a = getCircle(j);
					b = getCircle(j+1);
					
				}
				
				if (pos == null) {
					
					pos = new CapsuleSequencePosition(seq, e.index, e.param);
					
				}
				
				assert pos != null;
				
				Entity hit = world.graph.pureGraphIntersectCircle(APP.platform.createShapeEngine().createCircle(pos.p, e.circle.radius));
				
				if (hit == null) {
					Intersection i0 = new Intersection(world, pos.p);
					Set<Vertex> res = world.addIntersection(i0);
					affected.addAll(res);
					
				} else {
					
				}
				
			} else {
				assert false;
			}
			
		}
		
		/*
		 * run events again to pick up any new vertices from now having vertices around
		 * 
		 * even after 
		 * 
		 */
		List<SweepEvent> toKeep = new ArrayList<SweepEvent>();
		events = events(false);
		for (int i = 0; i < events.size(); i++) {
			
			SweepEvent e = events.get(i);
			
			if (e.type == SweepEventType.ENTERVERTEX || e.type == SweepEventType.EXITVERTEX) {
				assert e.stillParent != null;
				e.setVertex((Vertex)e.stillParent);
				toKeep.add(e);
			} else {
				/*
				 * still possible that there are ROAD events, even after roads have been split and vertices added
				 * 
				 */
//				assert false;
			}
			
		}
		events = toKeep;
		
		/*
		 * now go through and create roads
		 */
		for (int i = 0; i < events.size()-1; i++) {
			
			SweepEvent e0 = events.get(i);
			SweepEvent e1 = events.get(i+1);
			
			if (e0.type == SweepEventType.ENTERVERTEX && e1.type == SweepEventType.EXITVERTEX) {
				
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			}
//			else if (e0.type == SweepEventType.ENTERROAD && e1.type == SweepEventType.EXITROAD) {
//				
//				i = i+1;
//				if (i == events.size()-1) {
//					break;
//				}
//				e0 = events.get(i);
//				e1 = events.get(i+1);
//				
//			}
			
			Vertex v0 = e0.getVertex();
			Vertex v1 = e1.getVertex();
			
//			if (v0 == v1) {
//				logger.debug("same vertex");
//				continue;
//			}
			
			List<Point> roadPts = new ArrayList<Point>();
			roadPts.add(v0.p);
			for (int j = e0.index+1; j < e1.index; j++) {
				roadPts.add(get(j));
			}
			if (e1.index >= e0.index+1) {
				if (!DMath.equals(e1.param, 0.0)) {
					roadPts.add(get(e1.index));
					roadPts.add(v1.p);
				} else {
					roadPts.add(v1.p);
				}
			} else {
				assert e1.index == e0.index;
				roadPts.add(v1.p);
			}
			
			Set<Vertex> res = world.createRoad(v0, v1, roadPts);
			affected.addAll(res);
		}
		
		return affected;
	}
	
	private List<SweepEvent> events(boolean sweepSelf) {
		assert finished;
		
		int vertexCount = 0;
		int roadCapsuleCount = 0;
		int mergerCount = 0;
		int strokeCapsuleCount = 0;
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		List<SweepEvent> startEvents = new ArrayList<SweepEvent>();
		for (Vertex v : world.graph.vertices) {
			startEvents.addAll(SweepUtils.sweepStartCSoverC(v, v.getShape(), seq, 0));
		}
		for (Road r : world.graph.roads) {
			startEvents.addAll(SweepUtils.sweepStartCSoverCS(r, r.getShape(), seq, 0));
		}
		for (Merger m : world.graph.mergers) {
			startEvents.addAll(SweepUtils.sweepStartCSoverA(m, m.getShape(), seq, 0));
		}
		
		Collections.sort(startEvents, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : startEvents) {
			switch (e.type) {
			case ENTERROADCAPSULE:
				roadCapsuleCount++;
				if (roadCapsuleCount == 1) {
					e.type = SweepEventType.ENTERROAD;
					events.add(e);
				}
				break;
			case ENTERVERTEX:
				vertexCount++;
				events.add(e);
				break;
			case ENTERMERGER:
				mergerCount++;
				events.add(e);
				break;
			default:
				assert false;
				break;
			}
		}
		if ((vertexCount + roadCapsuleCount + mergerCount + strokeCapsuleCount) == 0) {
			events.add(new CapsuleSequenceSweepEvent(null, null, null, seq, 0, 0.0, 0));
		}
		
		
		List<Capsule> selfEnteredCaps = new ArrayList<Capsule>();
		
		for (int i = 0; i < seq.capsuleCount(); i++) {
			
			CapsuleSequence capSeq = seq.capseq(i);
			
			List<SweepEvent> capEvents = new ArrayList<SweepEvent>();
			for (Vertex v : world.graph.vertices) {
				capEvents.addAll(SweepUtils.sweepCSoverC(v, v.getShape(), capSeq, 0, i));
			}
			for (Road r : world.graph.roads) {
				capEvents.addAll(SweepUtils.sweepCSoverCS(r, r.getShape(), capSeq, 0, i));
			}
			for (Merger m : world.graph.mergers) {
				capEvents.addAll(SweepUtils.sweepCSoverA(m, m.getShape(), capSeq, 0, i));
			}
			
			if (sweepSelf) {
				capEvents.addAll(selfEvents(i, selfEnteredCaps));
			}
			
			Collections.sort(capEvents, SweepEvent.COMPARATOR);
			
			for (SweepEvent e : capEvents) {
				switch (e.type) {
				case ENTERVERTEX:
					vertexCount++;
					events.add(e);
					break;
				case EXITVERTEX:
					vertexCount--;
					assert vertexCount >= 0;
					events.add(e);
					break;
				case ENTERROADCAPSULE:
					roadCapsuleCount++;
					if (roadCapsuleCount == 1) {
						e.type = SweepEventType.ENTERROAD;
						events.add(e);
					}
					break;
				case EXITROADCAPSULE:
					roadCapsuleCount--;
					assert roadCapsuleCount >= 0;
					if (roadCapsuleCount == 0) {
						e.type = SweepEventType.EXITROAD;
						events.add(e);
					}
					break;
				case ENTERMERGER:
					mergerCount++;
					events.add(e);
					break;
				case EXITMERGER:
					mergerCount--;
					assert mergerCount >= 0;
					events.add(e);
					break;
				case ENTERSTROKE:
					strokeCapsuleCount++;
					events.add(e);
					break;
				case EXITSTROKE:
					strokeCapsuleCount--;
					assert strokeCapsuleCount >= 0;
					events.add(e);
					break;
				default:
					break;
				}
			}
		}
		
		if ((vertexCount + roadCapsuleCount + mergerCount + strokeCapsuleCount) == 0) {
			events.add(new CapsuleSequenceSweepEvent(null, null, null, seq, seq.capsuleCount, 0.0, 0));
		}
		
		List<SweepEvent> adj = new ArrayList<SweepEvent>();
		
		for (int i = 0; i < events.size(); i++) {
			SweepEvent e = events.get(i);
			if (e.type == SweepEventType.EXITMERGER) {
				/*
				 * if there is an EXITMERGER event, then ignore this stroke
				 */
				return adj;
			}
		}
		
		/*
		 * go through and give vertex events higher precedence over capsule events
		 * remove any capsule events between matching vertex events
		 * and also remove the matching capsule event
		 */
		
		boolean insideVertex = false;
		boolean lookingForExitRoadCapsule = false;
		boolean lookingForExitMerger = false;
		for (int i = 0; i < events.size(); i++) {
			SweepEvent e = events.get(i);
			if (!insideVertex) {
				if (e.type == SweepEventType.ENTERVERTEX) {
					insideVertex = true;
					adj.add(e);
				} else if (e.type == SweepEventType.EXITVERTEX) {
					assert false;
				} else {
					if (!lookingForExitRoadCapsule && !lookingForExitMerger) {
						adj.add(e);
					} else {
						if (lookingForExitRoadCapsule) {
							if (e.type == SweepEventType.EXITROAD) {
								lookingForExitRoadCapsule = false;
							}
						}
						if (lookingForExitMerger) {
							if (e.type == SweepEventType.EXITMERGER) {
								lookingForExitMerger = false;
							}
						}
					}
				}
			} else {
				if (e.type == SweepEventType.ENTERVERTEX) {
					assert false;
				} else if (e.type == SweepEventType.EXITVERTEX) {
					insideVertex = false;
					adj.add(e);
				} else if (e.type == SweepEventType.ENTERROAD) {
					lookingForExitRoadCapsule = true;
				} else if (e.type == SweepEventType.EXITROAD) {
					assert lookingForExitRoadCapsule;
					lookingForExitRoadCapsule = false;
				} else if (e.type == SweepEventType.ENTERMERGER) {
					lookingForExitMerger = true;
				} else if (e.type == SweepEventType.EXITMERGER) {
					assert lookingForExitMerger;
					lookingForExitMerger = false;
				} else {
					/*
					 * finish implementing
					 */
					assert false;
				}
			}
		}
		
		/*
		 * TODO
		 * go through and verify that events are well formed:
		 * every ENTERROADCAPSULE and EXITROADCAPSULE match up
		 * every ENTER AND EXIT VERTEX match up
		 * every ENTER AND EXIT MERGER match up
		 * 
		 * figure out FSM that is actually accurate
		 */
		
		return adj;
	} 
	
	private List<SweepEvent> selfEvents(int i, List<Capsule> entered) {
			
		CapsuleSequence moving = seq.capseq(i);
		CapsuleSequence still = seq.subsequence(i);
		
		List<SweepEvent> subStartEvents = SweepUtils.sweepStartCSoverCS(this, still, moving, i);
		List<Capsule> overlappingAtStart = new ArrayList<Capsule>();
		for (SweepEvent e : subStartEvents) {
			overlappingAtStart.add((Capsule)e.still);
		}
		
		List<SweepEvent> subEvents = SweepUtils.sweepCSoverCS(this, still, moving, 0, i);
		
		Collections.sort(subEvents, SweepEvent.COMPARATOR);
		
		List<SweepEvent> toKeep = new ArrayList<SweepEvent>();
		Capsule eventCapsule;
		for (SweepEvent e : subEvents) {
			switch (e.type) {
			case ENTERSTROKE:
				eventCapsule = ((Capsule)e.still);
				entered.add(eventCapsule);
				toKeep.add(e);
				break;
			case EXITSTROKE:
				eventCapsule = ((Capsule)e.still);
				/*
				 * the moving CS (really it is only 1 capsule) may be overlapping several previous capsules when it starts
				 * do not count exiting these as events
				 * unless the eventcapsule really was entered earlier   
				 */
				if (entered.contains(eventCapsule) || !overlappingAtStart.contains(eventCapsule)) {
					toKeep.add(e);
				}
				break;
			default:
				assert false;
				break;
			}
		}
		
		return toKeep;
	}
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Circle c : cs) {
			aabb = AABB.union(aabb, c.aabb);
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		paintStroke(ctxt);
		
	}
	
	private void paintStroke(RenderingContext ctxt) {
		
		if (!cs.isEmpty()) {
			
			ctxt.setColor(Color.GRAY);
			
			List<Capsule> caps = new ArrayList<Capsule>();
			for (int i = 0; i < cs.size()-1; i++) {
				caps.add(APP.platform.createShapeEngine().createCapsule(cs.get(i), cs.get(i+1)));
			}
			
			CapsuleSequence seq = new CapsuleSequence(caps);
			
			seq.draw(ctxt);
			
		}
		
	}
	
}
