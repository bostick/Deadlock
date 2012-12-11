package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEventType;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

@SuppressWarnings("static-access")
public class Stroke {
	
	public static final double STROKE_RADIUS = Vertex.INIT_VERTEX_RADIUS;
	
	private List<Circle> cs;
	
	private boolean finished;
	public List<Capsule> caps;
	public CapsuleSequence seq;
	
	static Logger logger = Logger.getLogger(Stroke.class);
	
	public Stroke() {
		cs = new ArrayList<Circle>();
	}
	
	private AABB aabb;
	
	public final AABB getAABB() {
		return aabb;
	}
	
	public void add(Point p) {
		assert !finished;
		
		cs.add(new Circle(null, p, STROKE_RADIUS));
		
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
			caps.add(new Capsule(this, a, b, i));
		}
		seq = new CapsuleSequence(this, caps);
		
		finished = true;
	}
	
//	private int vertexCount;
//	private int roadCapsuleCount;
//	private int mergerCount;
//	private int strokeCapsuleCount;
	
	public List<SweepEvent> events(boolean sweepSelf) {
		assert finished;
		
		int vertexCount = 0;
		int roadCapsuleCount = 0;
		int mergerCount = 0;
		int strokeCapsuleCount = 0;
		List<SweepEvent> vertexEvents = new ArrayList<SweepEvent>();
		
		Circle start = seq.getStart();
		
		List<SweepEvent> startEvents = APP.world.sweepStart(start);
		
		Collections.sort(startEvents, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : startEvents) {
			boolean keep = false;
			switch (e.type) {
			case ENTERROADCAPSULE:
				roadCapsuleCount++;
				if (roadCapsuleCount == 1) {
					keep = true;
				}
				break;
			case ENTERVERTEX:
				vertexCount++;
				if (vertexCount == 1) {
					keep = true;
				}
				break;
			case ENTERMERGER:
				mergerCount++;
				if (mergerCount == 1) {
					keep = true;
				}
				break;
			case ENTERSTROKE:
			case EXITMERGER:
			case EXITROADCAPSULE:
			case EXITSTROKE:
			case EXITVERTEX:
				assert false;
				break;
			}
			if (keep) {
				vertexEvents.add(e);
			}
		}
		
		if ((vertexCount + roadCapsuleCount + mergerCount + strokeCapsuleCount) == 0) {
			logger.debug("start in nothing");
			vertexEvents.add(new SweepEvent(null, null, start, 0, 0.0));
//			assert false;
		} else {
//			logger.debug("start counts: " + vertexCount + " " + roadCapsuleCount + " " + mergerCount);
		}
		
		List<Capsule> strokeEnteredCapsules = new ArrayList<Capsule>();
//		Capsule prevCapsule = null;
		
		for (int i = 0; i < seq.capsuleCount(); i++) {
			
			Capsule cap = seq.getCapsule(i);
			
			List<SweepEvent> events = APP.world.sweep(cap);
			
			if (sweepSelf) {
				
				if (i == 0) {
					
				} else {
					
					CapsuleSequence sub = seq.subsequence(i);
					
					List<SweepEvent> subStartEvents = sub.sweepStart(cap.ac);
					List<SweepEvent> subEvents = sub.sweep(cap);
					
					Collections.sort(subEvents, SweepEvent.COMPARATOR);
					
					List<SweepEvent> toKeep = new ArrayList<SweepEvent>();
					Capsule eventCapsule;
					for (SweepEvent e : subEvents) {
						switch (e.type) {
						case ENTERSTROKE:
							eventCapsule = ((Capsule)e.still);
							
							strokeEnteredCapsules.add(eventCapsule);
							toKeep.add(e);
							
							break;
						case EXITSTROKE:
							eventCapsule = ((Capsule)e.still);
							
							boolean atStart = false;
							for (SweepEvent se : subStartEvents) {
								if (se.still == eventCapsule) {
									atStart = true;
									break;
								}
							}
							if (strokeEnteredCapsules.contains(eventCapsule) || !atStart) {
								toKeep.add(e);
							}
							
							break;
						case ENTERMERGER:
						case ENTERROADCAPSULE:
						case ENTERVERTEX:
						case EXITMERGER:
						case EXITROADCAPSULE:
						case EXITVERTEX:
							assert false;
							break;
						}
					}
					
					events.addAll(toKeep);
					
				}
				
			}
			
			Collections.sort(events, SweepEvent.COMPARATOR);
			
			for (SweepEvent e : events) {
				boolean keep = false;
				switch (e.type) {
				case ENTERVERTEX:
					vertexCount++;
					if (vertexCount == 1) {
						keep = true;
					}
					break;
				case EXITVERTEX:
					vertexCount--;
					assert vertexCount >= 0;
					if (vertexCount == 0) {
						keep = true;
					}
					break;
				case ENTERROADCAPSULE:
					roadCapsuleCount++;
					if (roadCapsuleCount == 1) {
						keep = true;
					}
					break;
				case EXITROADCAPSULE:
					roadCapsuleCount--;
					assert roadCapsuleCount >= 0;
					if (roadCapsuleCount == 0) {
						keep = true;
					}
					break;
				case ENTERMERGER:
					mergerCount++;
					if (mergerCount == 1) {
						keep = true;
					}
					break;
				case EXITMERGER:
					mergerCount--;
					assert mergerCount >= 0;
					if (mergerCount == 0) {
						keep = true;
					}
					break;
				case ENTERSTROKE:
					strokeCapsuleCount++;
					if (strokeCapsuleCount == 1) {
						keep = true;
					}
					break;
				case EXITSTROKE:
					strokeCapsuleCount--;
					assert strokeCapsuleCount >= 0;
					if (strokeCapsuleCount == 0) {
						keep = true;
					}
					break;
				}
				if (keep) {
					vertexEvents.add(e);
				}
			}
		}
		
		if ((vertexCount + roadCapsuleCount + mergerCount + strokeCapsuleCount) == 0) {
			logger.debug("end in nothing");
			vertexEvents.add(new SweepEvent(null, null, cs.get(cs.size()-1), cs.size()-1, 0.0));
//			assert false;
		}
		
		
		List<SweepEvent> adj = new ArrayList<SweepEvent>();
		
		for (int i = 0; i < vertexEvents.size(); i++) {
			SweepEvent e = vertexEvents.get(i);
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
		for (int i = 0; i < vertexEvents.size(); i++) {
			SweepEvent e = vertexEvents.get(i);
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
							if (e.type == SweepEventType.EXITROADCAPSULE) {
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
				} else if (e.type == SweepEventType.ENTERROADCAPSULE) {
					lookingForExitRoadCapsule = true;
				} else if (e.type == SweepEventType.EXITROADCAPSULE) {
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
		
		if (logger.isDebugEnabled()) {
			logger.debug("vertexEvents:");
			logger.debug(adj);
		}
		
		return adj;
	}
	
//	public List<SweepEvent> startEndEventsX() {
//		assert finished;
//		
//		vertexCount = 0;
//		roadCapsuleCount = 0;
//		mergerCount = 0;
////		strokeCapsuleCount = 0;
//		List<SweepEvent> vertexEvents = new ArrayList<SweepEvent>();
//		
//		Circle start = seq.getStart();
//		
//		APP.world.sweepStart(start);
//		
//		if ((vertexCount + roadCapsuleCount + mergerCount) == 0) {
//			logger.debug("start in nothing");
//			vertexEvents.add(new SweepEvent(null, null, start, 0, 0.0));
//		} else {
//			logger.debug("start counts: " + vertexCount + " " + roadCapsuleCount + " " + mergerCount);
//		}
//		
////		List<Capsule> curStrokeCapsules = new ArrayList<Capsule>();
////		Capsule prevCapsule = null;
//		
//		for (int i = 0; i < seq.capsuleCount(); i++) {
//			
//			Capsule cap = seq.getCapsule(i);
//			
//			APP.world.sweep(cap);
//			
////			Collections.sort(events, SweepEvent.COMPARATOR);
////			
////			for (SweepEvent e : events) {
////				boolean keep = eventSorted(e);
////				if (keep) {
////					vertexEvents.add(e);
////				}
////			}
//		}
//		
//		if ((vertexCount + roadCapsuleCount + mergerCount) == 0) {
//			logger.debug("end in nothing");
//			vertexEvents.add(new SweepEvent(null, null, cs.get(cs.size()-1), cs.size()-1, 0.0));
//		}
//		
//		return vertexEvents;
//	}
	
//	private boolean startSorted(SweepEvent e) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("startSorted: " + e + " " + e.shape + " " + e.index + " " + e.param);
//		}
//		switch (e.type) {
//		case ENTERROADCAPSULE:
//			roadCapsuleCount++;
//			if (roadCapsuleCount == 1) {
////				vertexEvents.add(e);
//				return true;
//			}
//			break;
//		case ENTERVERTEX:
//			vertexCount++;
//			if (vertexCount == 1) {
////				vertexEvents.add(e);
//				return true;
//			}
//			break;
//		case ENTERMERGER:
//			mergerCount++;
//			if (mergerCount == 1) {
////				vertexEvents.add(e);
//				return true;
//			}
//			break;
//		case ENTERSTROKE:
//		case EXITMERGER:
//		case EXITROADCAPSULE:
//		case EXITSTROKE:
//		case EXITVERTEX:
//			assert false;
//			break;
//		}
//		return false;
//	}
	
//	private boolean eventSorted(SweepEvent e) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("eventSorted: " + e + " " + e.shape + " " + e.index + " " + e.param);
//		}
//		switch (e.type) {
//		case ENTERVERTEX:
//			vertexCount++;
//			if (vertexCount == 1) {
//				return true;
//			}
//			break;
//		case EXITVERTEX:
//			vertexCount--;
//			assert vertexCount >= 0;
//			if (vertexCount == 0) {
//				return true;
//			}
//			break;
//		case ENTERROADCAPSULE:
//			roadCapsuleCount++;
//			if (roadCapsuleCount == 1) {
//				return true;
//			}
//			break;
//		case EXITROADCAPSULE:
//			roadCapsuleCount--;
//			assert roadCapsuleCount >= 0;
//			if (roadCapsuleCount == 0) {
//				return true;
//			}
//			break;
//		case ENTERMERGER:
//			mergerCount++;
//			if (mergerCount == 1) {
//				return true;
//			}
//			break;
//		case EXITMERGER:
//			mergerCount--;
//			assert mergerCount >= 0;
//			if (mergerCount == 0) {
//				return true;
//			}
//			break;
//			
//		case ENTERSTROKE:
//			strokeCapsuleCount++;
//			if (strokeCapsuleCount == 1) {
//				return true;
//			}
//			break;
//		case EXITSTROKE:
//			strokeCapsuleCount--;
//			assert strokeCapsuleCount >= 0;
//			if (strokeCapsuleCount == 0) {
//				return true;
//			}
//			break;
//		}
//		return false;
//	}
	
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Circle c : cs) {
			aabb = AABB.union(aabb, c.aabb);
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setWorldPixelStroke(1);
		paintStroke(ctxt);
		
		if (APP.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
		
	}
	
	private void paintStroke(RenderingContext ctxt) {
		
		if (!cs.isEmpty()) {
			
			ctxt.setColor(Color.GRAY);
			
			List<Capsule> caps = new ArrayList<Capsule>();
			for (int i = 0; i < cs.size()-1; i++) {
				caps.add(new Capsule(null, cs.get(i), cs.get(i+1), i));
			}
			
			CapsuleSequence seq = new CapsuleSequence(null, caps);
			
			seq.draw(ctxt);
			
		}
		
	}
	
}
