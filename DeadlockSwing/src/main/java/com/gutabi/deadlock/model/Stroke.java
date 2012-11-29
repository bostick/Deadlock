package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEventType;
import com.gutabi.deadlock.core.geom.Sweeper;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class Stroke {
	
	private List<Circle> cs;
	
	public final double r;
	
	private 
	
	static Logger logger = Logger.getLogger(Stroke.class);
	
	public Stroke(double r) {
		cs = new ArrayList<Circle>();
		this.r = r;
	}
	
	
	private AABB aabb;
	
	public final AABB getAABB() {
		return aabb;
	}
	
	public double getRadius() {
		return r;
	}
	
	public void add(Point p) {
		
		cs.add(new Circle(null, p, r));
		
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
	
	List<SweepEvent> events;
	Sweeper sweeper;
	int vertexCount;
	int capsuleCount;
	int mergerCount;
	List<SweepEvent> vertexEvents;
	
	public List<SweepEvent> events() {
		
		vertexCount = 0;
		capsuleCount = 0;
		mergerCount = 0;
		vertexEvents = new ArrayList<SweepEvent>();
		
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < cs.size()-1; i++) {
			Circle a = cs.get(i);
			Circle b = cs.get(i+1);
			caps.add(new Capsule(null, a, b));
		}
		
		sweeper = new Sweeper(null, caps) {
			public void start(SweepEvent e) {
				events.add(e);
			}
			
			public void event(SweepEvent e) {
				events.add(e);
			}
		};
		
		events = new ArrayList<SweepEvent>();
		
		MODEL.world.sweepStart(sweeper);
		
		Collections.sort(events, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : events) {
			startSorted(e);
		}
		
		if ((vertexCount + capsuleCount + mergerCount) == 0) {
			logger.debug("start in nothing");
			vertexEvents.add(new SweepEvent(null, null, sweeper, 0, 0.0));
		} else {
			logger.debug("end counts: " + vertexCount + " " + capsuleCount + " " + mergerCount);
		}
		
		for (int i = 0; i < cs.size()-1; i++) {
			
			events = new ArrayList<SweepEvent>();
			
			MODEL.world.sweep(sweeper, i);
			
			Collections.sort(events, SweepEvent.COMPARATOR);
			
			for (SweepEvent e : events) {
				eventSorted(e);
			}
		}
		
		if ((vertexCount + capsuleCount + mergerCount) == 0) {
			logger.debug("end in nothing");
			vertexEvents.add(new SweepEvent(null, null, sweeper, cs.size()-1, 0.0));
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
			capsuleCount++;
			if (capsuleCount == 1) {
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
		default:
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
			capsuleCount++;
			if (capsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITROADCAPSULE:
			capsuleCount--;
			assert capsuleCount >= 0;
			if (capsuleCount == 0) {
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
		}
	}
	
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Circle c : cs) {
			aabb = AABB.union(aabb, c.aabb);
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		java.awt.Stroke origStroke = ctxt.getStroke();
		ctxt.setStroke(VIEW.worldStroke);
		paintStroke(ctxt);
		
		ctxt.setStroke(origStroke);
		
		if (MODEL.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
		
	}
	
	private void paintStroke(RenderingContext ctxt) {
		
		if (!cs.isEmpty()) {
			
			ctxt.setColor(Color.GRAY);
			
			List<Capsule> caps = new ArrayList<Capsule>();
			for (int i = 0; i < cs.size()-1; i++) {
				caps.add(new Capsule(null, cs.get(i), cs.get(i+1)));
			}
			
			CapsuleSequence seq = new CapsuleSequence(null, caps);
			
			seq.draw(ctxt);
			
		}
		
	}
	
}
