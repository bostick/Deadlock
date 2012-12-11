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
	
	private int vertexCount;
	private int roadCapsuleCount;
	private int mergerCount;
	private int strokeCapsuleCount;
	List<SweepEvent> vertexEvents;
	
	public List<SweepEvent> selfEvents() {
		assert finished;
		
//		vertexCount = 0;
//		roadCapsuleCount = 0;
//		mergerCount = 0;
		strokeCapsuleCount = 0;
		vertexEvents = new ArrayList<SweepEvent>();
		
//		Circle start = seq.getStart();
//		
//		List<SweepEvent> events = APP.world.sweepStart(start);
//		
//		Collections.sort(events, SweepEvent.COMPARATOR);
//		
//		for (SweepEvent e : events) {
//			startSorted(e);
//		}
		
//		if ((vertexCount + roadCapsuleCount + mergerCount + strokeCapsuleCount) == 0) {
//			logger.debug("start in nothing");
//			vertexEvents.add(new SweepEvent(null, null, start, 0, 0.0));
//		} else {
//			logger.debug("start counts: " + vertexCount + " " + roadCapsuleCount + " " + mergerCount + " " + strokeCapsuleCount);
//		}
		
//		List<Capsule> curStrokeCapsules = new ArrayList<Capsule>();
//		Capsule prevCapsule = null;
		
		for (int i = 0; i < seq.capsuleCount(); i++) {
			
			Capsule cap = seq.getCapsule(i);
			
			List<SweepEvent> events = new ArrayList<SweepEvent>();
			
			if (i == 0) {
				
//				prevCapsule = cap;
				
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
						eventCapsule = ((Capsule)e.shape);
						
						toKeep.add(e);
						
						break;
					case EXITSTROKE:
						eventCapsule = ((Capsule)e.shape);
						
						boolean atStart = false;
						for (SweepEvent se : subStartEvents) {
							if (se.shape == eventCapsule) {
								atStart = true;
								break;
							}
						}
						if (!atStart) {
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
				
//				prevCapsule = cap;
				
				events.addAll(toKeep);
				
//				String.class.getName();
				
			}
			
			Collections.sort(events, SweepEvent.COMPARATOR);
			
			for (SweepEvent e : events) {
				eventSorted(e);
			}
		}
		
//		if ((vertexCount + roadCapsuleCount + mergerCount + strokeCapsuleCount) == 0) {
//			logger.debug("end in nothing");
//			vertexEvents.add(new SweepEvent(null, null, cs.get(cs.size()-1), cs.size()-1, 0.0));
//		}
		
		return vertexEvents;
	}
	
	public List<SweepEvent> events() {
		assert finished;
		
		vertexCount = 0;
		roadCapsuleCount = 0;
		mergerCount = 0;
//		strokeCapsuleCount = 0;
		vertexEvents = new ArrayList<SweepEvent>();
		
		Circle start = seq.getStart();
		
		List<SweepEvent> events = APP.world.sweepStart(start);
		
		Collections.sort(events, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : events) {
			startSorted(e);
		}
		
		if ((vertexCount + roadCapsuleCount + mergerCount) == 0) {
			logger.debug("start in nothing");
			vertexEvents.add(new SweepEvent(null, null, start, 0, 0.0));
		} else {
			logger.debug("start counts: " + vertexCount + " " + roadCapsuleCount + " " + mergerCount);
		}
		
//		List<Capsule> curStrokeCapsules = new ArrayList<Capsule>();
//		Capsule prevCapsule = null;
		
		for (int i = 0; i < seq.capsuleCount(); i++) {
			
			Capsule cap = seq.getCapsule(i);
			
			events = APP.world.sweep(cap);
			
			Collections.sort(events, SweepEvent.COMPARATOR);
			
			for (SweepEvent e : events) {
				eventSorted(e);
			}
		}
		
		if ((vertexCount + roadCapsuleCount + mergerCount) == 0) {
			logger.debug("end in nothing");
			vertexEvents.add(new SweepEvent(null, null, cs.get(cs.size()-1), cs.size()-1, 0.0));
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
		
		boolean insideCircle = false;
		boolean lookingForExitCapsule = false;
		boolean lookingForExitQuad = false;
		for (int i = 0; i < vertexEvents.size(); i++) {
			SweepEvent e = vertexEvents.get(i);
			if (!insideCircle) {
				if (e.type == SweepEventType.ENTERVERTEX) {
					insideCircle = true;
					adj.add(e);
				} else if (e.type == SweepEventType.EXITVERTEX) {
					assert false;
				} else {
					if (!lookingForExitCapsule && !lookingForExitQuad) {
						adj.add(e);
					} else {
						if (lookingForExitCapsule) {
							if (e.type == SweepEventType.EXITROADCAPSULE) {
								lookingForExitCapsule = false;
							}
						}
						if (lookingForExitQuad) {
							if (e.type == SweepEventType.EXITMERGER) {
								lookingForExitQuad = false;
							}
						}
					}
				}
			} else {
				if (e.type == SweepEventType.ENTERVERTEX) {
					assert false;
				} else if (e.type == SweepEventType.EXITVERTEX) {
					insideCircle = false;
					adj.add(e);
				} else if (e.type == SweepEventType.ENTERROADCAPSULE) {
					lookingForExitCapsule = true;
				} else if (e.type == SweepEventType.EXITROADCAPSULE) {
					assert lookingForExitCapsule;
					lookingForExitCapsule = false;
				} else if (e.type == SweepEventType.ENTERMERGER) {
					lookingForExitQuad = true;
				} else if (e.type == SweepEventType.EXITMERGER) {
					assert lookingForExitQuad;
					lookingForExitQuad = false;
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
	
	private void startSorted(SweepEvent e) {
		if (logger.isDebugEnabled()) {
			logger.debug("startSorted: " + e + " " + e.shape + " " + e.index + " " + e.param);
		}
		switch (e.type) {
		case ENTERROADCAPSULE:
			roadCapsuleCount++;
			if (roadCapsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERVERTEX:
			vertexCount++;
			if (vertexCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERMERGER:
			mergerCount++;
			if (mergerCount == 1) {
				vertexEvents.add(e);
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
	}
	
	private void eventSorted(SweepEvent e) {
		if (logger.isDebugEnabled()) {
			logger.debug("eventSorted: " + e + " " + e.shape + " " + e.index + " " + e.param);
		}
		switch (e.type) {
		case ENTERVERTEX:
			vertexCount++;
			if (vertexCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITVERTEX:
			vertexCount--;
			assert vertexCount >= 0;
			if (vertexCount == 0) {
				vertexEvents.add(e);
			}
			break;
		case ENTERROADCAPSULE:
			roadCapsuleCount++;
			if (roadCapsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITROADCAPSULE:
			roadCapsuleCount--;
			assert roadCapsuleCount >= 0;
			if (roadCapsuleCount == 0) {
				vertexEvents.add(e);
			}
			break;
		case ENTERMERGER:
			mergerCount++;
			if (mergerCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITMERGER:
			mergerCount--;
			assert mergerCount >= 0;
			if (mergerCount == 0) {
				vertexEvents.add(e);
			}
			break;
			
		case ENTERSTROKE:
			strokeCapsuleCount++;
			if (strokeCapsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITSTROKE:
			strokeCapsuleCount--;
			assert strokeCapsuleCount >= 0;
			if (strokeCapsuleCount == 0) {
				vertexEvents.add(e);
			}
			break;
		}
	}
	
	
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
