package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.core.Event.CloseEvent;
import com.gutabi.deadlock.core.Event.IntersectionEvent;

public class GraphController {
	
	Graph graph;
	
	public GraphController(Graph graph) {
		this.graph = graph;
	}
	
	/**
	 * after all top methods, run this to do work
	 * that is too expensive to run during editing
	 */
	private void postTop() {
		for (Edge e : graph.edges) {
			if (e.area == null) {
				e.computeArea();
			}
		}
	}
	
	public void removeEdgeTop(Edge e) {
		
		/*
		 * have to properly cleanup start and end intersections before removing edges
		 */
		if (!e.isLoop()) {
			
			Vertex eStart = e.getStart();
			Vertex eEnd = e.getEnd();
			
			graph.destroyEdge(e);
			
			graph.postEdgeChange(eStart);
			graph.postEdgeChange(eEnd);
			
		} else if (!e.isStandAlone()) {
			
			Vertex v = e.getStart();
			
			graph.destroyEdge(e);
			
			graph.postEdgeChange(v);
			
		} else {
			graph.destroyEdge(e);
		}
		
		postTop();
	}
	
	public void removeVertexTop(Vertex v) {
		
		Set<Vertex> affectedVertices = new HashSet<Vertex>();
		
		/*
		 * copy, since removing edges modifies v.getEdges()
		 * and use a set since loops will be in the list twice
		 */
		Set<Edge> eds = new HashSet<Edge>(v.getEdges());
		for (Edge e : eds) {
			
			if (!e.isLoop()) {
				
				Vertex eStart = e.getStart();
				Vertex eEnd = e.getEnd();
				
				affectedVertices.add(eStart);
				affectedVertices.add(eEnd);
				
				graph.destroyEdge(e);
				
			} else {
				
				Vertex eV = e.getStart();
				
				affectedVertices.add(eV);
				
				graph.destroyEdge(e);
				
			}
		}
		
		graph.destroyVertex(v);
		affectedVertices.remove(v);
		
		for (Vertex a : affectedVertices) {
			graph.postEdgeChange(a);
		}
		
		postTop();
	}
	
	public void processNewStrokeTop(List<Point> stroke) {
		
		boolean tooClose = false;
		Point tooClosePoint = null;
		
		for (int i = 0; i < stroke.size()-1; i++) {
			Point preA = stroke.get(i);
			Point preB = stroke.get(i+1);
			
//			logger.debug("process segment: " + preA + " " + preB);
			
			Point a;
			Point b;
			
			if (!tooClose) {
				
				GraphPosition aP;
				aP = graph.findClosestVertexPosition(preA, null, MODEL.world.VERTEX_RADIUS + MODEL.world.MOUSE_RADIUS/MODEL.world.PIXELS_PER_METER);
				if (aP == null) {
					aP = graph.findClosestEdgePosition(preA, null, MODEL.world.ROAD_RADIUS + MODEL.world.MOUSE_RADIUS/MODEL.world.PIXELS_PER_METER);
				}
				
				if (aP != null) {
					
					if (!aP.getPoint().equals(preA)) {
						
						tooClose = true;
						tooClosePoint = aP.getPoint();
						a = tooClosePoint;
						
					} else {
						
						a = preA;
						
					}
					
				} else {
					a = preA;
				}
				
			} else {
				/*
				 * a has already been changed by previous iteration
				 */
				a = tooClosePoint;
			}
			
			if (!tooClose) {
				
				GraphPosition bP;
				bP = graph.findClosestVertexPosition(preB, a, MODEL.world.VERTEX_RADIUS + MODEL.world.MOUSE_RADIUS/MODEL.world.PIXELS_PER_METER);
				if (bP == null) {
					bP = graph.findClosestEdgePosition(preB, a, MODEL.world.ROAD_RADIUS + MODEL.world.MOUSE_RADIUS/MODEL.world.PIXELS_PER_METER);
				}
				
				if (bP != null) {
					tooClose = true;
					tooClosePoint = bP.getPoint();
					b = tooClosePoint;
				} else {
					b = preB;
				}
				
				processNewSegment(a, b);
				
			} else {
				
				GraphPosition bP;
				bP = graph.findClosestVertexPosition(preB, a, MODEL.world.VERTEX_RADIUS + MODEL.world.MOUSE_RADIUS/MODEL.world.PIXELS_PER_METER);
				if (bP == null) {
					bP = graph.findClosestEdgePosition(preB, a, MODEL.world.ROAD_RADIUS + MODEL.world.MOUSE_RADIUS/MODEL.world.PIXELS_PER_METER);
				}
				
				if (bP != null) {
					
					if (DMath.greaterThan(Point.distance(bP.getPoint(), tooClosePoint), 2 * MODEL.world.ROAD_RADIUS)) {
						
						//still too close, but now to a different point
						tooClosePoint = bP.getPoint();
						b = tooClosePoint;
						
//						processNewSegment(a, b);
						
					} else {
						
						b = tooClosePoint;
						
						processNewSegment(a, b);
					}
					
				} else {
					
					if (DMath.greaterThan(Point.distance(preB, tooClosePoint), 2 * MODEL.world.ROAD_RADIUS)) {
						
						tooClose = false;
						tooClosePoint = null;
						
						b = preB;
						
					} else {
						
						b = tooClosePoint;
						
					}
					
					processNewSegment(a, b);
					
				}
				
			}
			
		}
		
		cleanupEdges();
		
		postTop();
	}
	
	private void processNewSegment(Point a, Point b) {
		
		if (a.equals(b)) {
			return;
		}
		
		/*
		 * first, split the segment up into ranges that do not overlap any existing segments
		 */
		
		List<Double> ranges = nonOverlappingRanges(a, b);
		
		for (int i = 0; i < ranges.size(); i+=2) {
			Point aa = Point.point(a, b, ranges.get(i));
			Point bb = Point.point(a, b, ranges.get(i+1));
			processNewRange(aa, bb);
		}
	}
	
	private List<Double> nonOverlappingRanges(Point a, Point b) {
		
		List<Double> ranges = new ArrayList<Double>();
		ranges.add(0.0);
		ranges.add(1.0);
		
		for (Segment in : graph.segTree.getAllSegments()) {
			int index = in.index;
			Point c = in.edge.getPoint(index);
			Point d = in.edge.getPoint(index+1);
			
			try {
				Point.intersection(a, b, c, d);
			} catch (OverlappingException ex) {
				
				double cParam = Point.param(c, a, b);
				double dParam = Point.param(d, a, b);
				
				if (dParam < cParam) {
					double tmp = dParam;
					dParam = cParam;
					cParam = tmp;
				}
				
				double overlapStart = Math.max(0.0, cParam);
				double overlapEnd = Math.min(dParam, 1.0);
				
				// find range that overlap breaks
				int r = -1;
				for (int i = 0; i < ranges.size(); i+=2) {
					double aa = ranges.get(i);
					double bb = ranges.get(i+1);
					if ((DMath.lessThanEquals(aa, overlapStart)) && (DMath.greaterThanEquals(bb, overlapEnd))) {
						r = i;
					}
				}
				assert r != -1;
				
				double aa = ranges.get(r);
				double bb = ranges.get(r+1);
				
				ranges.remove(r);
				ranges.remove(r);
				
				if (DMath.lessThan(overlapEnd, bb)) {
					ranges.add(r, bb);
					ranges.add(r, overlapEnd);
				}
				if (DMath.lessThan(aa, overlapStart)) {
					ranges.add(r, overlapStart);
					ranges.add(r, aa);
				}
				
			}
			
			
		}
		
		return ranges;
	}
	
	private void processNewRange(Point a, Point b) {
		
		Timeline timeline = new Timeline(a, b);
		
		for (Segment in : graph.segTree.getAllSegments()) {
			int index = in.index;
			Edge ed = in.edge;
			Point c = ed.getPoint(index);
			Point d = ed.getPoint(index+1);
			
			try {
				
				Point inter = Point.intersection(a, b, c, d);
				if (inter != null) {
					
					double interParam = Point.param(inter, a, b);
					double interStartParam = Point.travelBackward(a, b, interParam, 2 * MODEL.world.ROAD_RADIUS);
					double interEndParam = Point.travelForward(a, b, interParam, 2 * MODEL.world.ROAD_RADIUS);
					
					timeline.addEvent(new IntersectionEvent(inter, interParam, interStartParam, interEndParam, in));
				} else {
					
					Point aProjected = Point.point(c, d, DMath.clip(Point.u(c, a, d)));
					Point bProjected = Point.point(c, d, DMath.clip(Point.u(c, b, d)));
					
					CloseEvent cEvent = detectClose(timeline, c, a, b);
					CloseEvent dEvent = detectClose(timeline, d, a, b);
					CloseEvent aEvent = detectClose(timeline, aProjected, a, b);
					CloseEvent bEvent = detectClose(timeline, bProjected, a, b);
					
					CloseEvent e = null;
					
					if (cEvent != null) {
						e = new CloseEvent(cEvent.getBorderStartParam(), cEvent.getBorderEndParam());
					}
					
					if (dEvent != null) {
						if (e == null) {
							e = new CloseEvent(dEvent.getBorderStartParam(), dEvent.getBorderEndParam());
						} else {
							e = new CloseEvent(Math.min(e.getBorderStartParam(), dEvent.getBorderStartParam()), Math.max(e.getBorderEndParam(), dEvent.getBorderEndParam()));
						}
					}
					
					if (aEvent != null) {
						if (e == null) {
							e = new CloseEvent(aEvent.getBorderStartParam(), aEvent.getBorderEndParam());
						} else {
							e = new CloseEvent(Math.min(e.getBorderStartParam(), aEvent.getBorderStartParam()), Math.max(e.getBorderEndParam(), aEvent.getBorderEndParam()));
						}
					}
					
					if (bEvent != null) {
						if (e == null) {
							e = new CloseEvent(bEvent.getBorderStartParam(), bEvent.getBorderEndParam());
						} else {
							e = new CloseEvent(Math.min(e.getBorderStartParam(), bEvent.getBorderStartParam()), Math.max(e.getBorderEndParam(), bEvent.getBorderEndParam()));
						}
					}
					
					if (e != null) {
						timeline.addEvent(e);
					}
				}
				
			} catch (OverlappingException ex) {
				
				assert false;
				
			}
			
		}
		
		/*
		 * so a either intersects or not
		 * and b either intersects or not
		 * and no intersections in between
		 * 
		 * if a does not intersect and b does not intersect, (will only be at start of segmentf) only add if not close to anything
		 * if a does not intersect and b does intersect,  only add if not close to anything
		 * if a does intersect and b does not intersect,  only add if not close to anything
		 * if a does intersect and b does intersect,   only add if not close to anything
		 */
		
//		for (each intersection or end point) {
//			Point aa = Point.point(a, b, blah.get(i));
//			Point bb = Point.point(a, b, blah.get(i+1));
//			processNewBetweenIntersection(aa, bb, );
//		}
		
		
		
		
		
		
		
		List<Cluster> clusters = timeline.clusters;
		
		if (clusters.size() == 0) {
			
			assert timeline.aClusterIndex == -1;
			assert timeline.bClusterIndex == -1;
			
			Point aa = a;
			
			Point bb = b;
			graph.addSegment(aa, bb);
			
		} else if (timeline.aClusterIndex == timeline.bClusterIndex && timeline.aClusterIndex != -1) {
			
			assert clusters.size() == 1;
			
			Cluster c = clusters.get(0);
			
			if (c.intersectionEvents.isEmpty()) {
				
				;
				
			} else {
				
				Point aa = a;
				Point bb;
				
				IntersectionEvent e = c.intersectionEvents.get(0);
				
				bb = e.getSourceStart();
				if (!aa.equals(bb)) {
					graph.addSegment(aa, bb);
				}
				
				aa = bb;
				
				bb = b;
				if (!aa.equals(bb)) {
					graph.addSegment(aa, bb);
				}
			
			
			}
			
		} else {
			
			Point aa;
			Point bb;
			
			/*
			 * handle A cluster
			 */
			if (timeline.aClusterIndex != -1) {
				assert timeline.aClusterIndex == 0;
				// there is an A cluster
				
				Cluster c = clusters.get(timeline.aClusterIndex);
				
				if (c.intersectionEvents.isEmpty()) {
					
					aa = a;
					
					bb = Point.point(a, b, c.borderEndParam);
					graph.addSegment(aa, bb);
					
					aa = bb;
					
				} else {
					
					aa = a;
					
					for (IntersectionEvent e : c.intersectionEvents) {
						bb = e.getSourceStart();
						
						if (!aa.equals(bb)) {
							graph.addSegment(aa, bb);
						}
						
						aa = bb;
					}
					
					bb = Point.point(a, b, c.borderEndParam);
					graph.addSegment(aa, bb);
					
					aa = bb;
					
				}
				
			} else {
				// there is no A cluster
				aa = a;
			}
			
			/*
			 * handle all in between clusters
			 */
			int middleFirst = (timeline.aClusterIndex != -1) ? 1 : 0;
			int middleLast = (timeline.bClusterIndex != -1) ? clusters.size()-2 : clusters.size()-1;
			for (int middleIndex = middleFirst; middleIndex <= middleLast; middleIndex++) {
				Cluster c = clusters.get(middleIndex);
				
				if (c.intersectionEvents.isEmpty()) {
					
					bb = Point.point(a, b, c.borderStartParam); 
					graph.addSegment(aa, bb);
					
					aa = Point.point(a, b, c.borderEndParam);
					
				} else {
					
					bb = Point.point(a, b, c.borderStartParam); 
					graph.addSegment(aa, bb);
					
					aa = bb;
					
					for (IntersectionEvent e : c.intersectionEvents) {
						
						bb = e.getSourceStart();
						if (!aa.equals(bb)) {
							graph.addSegment(aa, bb);
						}
						
						aa = bb;
						
					}
					
					bb = Point.point(a, b, c.borderEndParam);
					graph.addSegment(aa, bb);
					
					aa = bb;
					
				}
				
			}
			
			/*
			 * handle b cluster
			 */
			if (timeline.bClusterIndex != -1) {
				assert timeline.bClusterIndex == clusters.size()-1;
				// there is a B cluster
				
				Cluster c = clusters.get(timeline.bClusterIndex);
				
				if (c.intersectionEvents.isEmpty()) {
					
					bb = Point.point(a, b, c.borderStartParam);
					graph.addSegment(aa, bb);
					
					aa = bb;
					
					bb = b;
					if (!aa.equals(bb)) {
						graph.addSegment(aa, bb);
					}
					
				} else {
					
					bb = Point.point(a, b, c.borderStartParam);
					graph.addSegment(aa, bb);
					
					aa = bb;
					
					for (IntersectionEvent e : c.intersectionEvents) {
						
						bb = e.getSourceStart();
						graph.addSegment(aa, bb);
						
						aa = bb;
					}
					
					bb = b;
					if (!aa.equals(bb)) {
						graph.addSegment(aa, bb);
					}
					
				}
				
			} else {
				// there is no B cluster
				bb = b;
				graph.addSegment(aa, bb);
			}
			
		}
		
	}
	
	private CloseEvent detectClose(Timeline t, Point p, Point a, Point b) {
		//how close is p to <a, b>?
		Point[] ints = new Point[2];
		int num = Point.circleLineIntersections(p, 2 * MODEL.world.ROAD_RADIUS, a, b, ints);
		switch (num) {
		case 2: {
			Point p1 = ints[0];
			Point p2 = ints[1];
			double param1 = Point.param(p1, a, b);
			double param2 = Point.param(p2, a, b);
			return (param1 < param2) ?
					new CloseEvent(param1, param2) :
						new CloseEvent(param2, param1);
		}
		default:
			return null;
		}
	}
	
	public class Timeline {
		
		Point a;
		Point b;
		
		final List<Cluster> clusters = new ArrayList<Cluster>();
		int aClusterIndex = -1;
		int bClusterIndex = -1;
		
		Timeline(Point a, Point b) {
			this.a = a;
			this.b = b;
		}
		
		void addEvent(Event e) {
			
			if (DMath.greaterThan(e.getBorderStartParam(), 1.0)) {
				return;
			}
			if (DMath.lessThan(e.getBorderEndParam(), 0.0)) {
				return;
			}
			
//			logger.debug(e + " " + e.getSourceStart() + " " + e.getSourceEnd());
			
			final List<Cluster> overlapping = new ArrayList<Cluster>();
			for (Cluster c : clusters) {
				if (DMath.rangesOverlap(e.getBorderStartParam(), e.getBorderEndParam(), c.borderStartParam, c.borderEndParam)) {
					overlapping.add(c);
				}
			}
			
			Cluster acc = new Cluster(a, b, e);
			
			for (Cluster c : overlapping) {
				acc = Cluster.merge(acc, c);
				clusters.remove(c);
			}
			
			// figure out where to insert acc
			int insertionPoint = -1;
			for (int i = 0; i < clusters.size(); i++) {
				Cluster c = clusters.get(i);
				if (acc.borderEndParam < c.borderStartParam) {
					insertionPoint = i;
					break;
				}
			}
			if (insertionPoint == -1) {
				insertionPoint = clusters.size();
			}
			
			clusters.add(insertionPoint, acc);
			
			aClusterIndex = -1;
			bClusterIndex = -1;
			for (int i = 0; i < clusters.size(); i++) {
				Cluster c = clusters.get(i);
				if (DMath.lessThanEquals(c.borderStartParam, 0.0)) {
					assert aClusterIndex == -1;
					aClusterIndex = i;
				}
				if (DMath.greaterThanEquals(c.borderEndParam, 1.0)) {
					assert bClusterIndex == -1;
					bClusterIndex = i;
				}
			}
		}
		
	}
	
	private static class Cluster {
		
		Point a;
		Point b;
		
		double borderStartParam;
		double borderEndParam;
		
		double closeBorderStartParam = Double.POSITIVE_INFINITY;
		double closeBorderEndParam = Double.NEGATIVE_INFINITY;
		
		List<IntersectionEvent> intersectionEvents = new ArrayList<IntersectionEvent>();
		List<CloseEvent> closeEvents = new ArrayList<CloseEvent>();
		
		private Cluster(Point a, Point b) {
			this.a = a;
			this.b = b;
		}
		
		Cluster(Point a, Point b, Event e) {
			
			this.a = a;
			this.b = b;
			
			if (e instanceof IntersectionEvent) {
				intersectionEvents.add((IntersectionEvent)e);
			} else if (e instanceof CloseEvent) {
				closeEvents.add((CloseEvent)e);
				closeBorderStartParam = ((CloseEvent)e).getBorderStartParam();
				closeBorderEndParam = ((CloseEvent)e).getBorderEndParam();
			} else {
				assert false;
			}
			borderStartParam = e.getBorderStartParam();
			borderEndParam = e.getBorderEndParam();
		}
		
		static Cluster merge(final Cluster c1, final Cluster c2) {
			
			Cluster acc = new Cluster(c1.a, c1.b);
			
			// sort based on param of source
			List<IntersectionEvent> allIntersectionEvents = new ArrayList<IntersectionEvent>();
			allIntersectionEvents.addAll(c1.intersectionEvents);
			allIntersectionEvents.addAll(c2.intersectionEvents);
			for (IntersectionEvent e : allIntersectionEvents) {
				int index = Collections.binarySearch(acc.intersectionEvents, e, IntersectionEvent.COMPARATOR);
				if (index < 0) {
					// not found
					int insertionPoint = -(index + 1);
					
					for (IntersectionEvent f : acc.intersectionEvents) {
						double dist = Point.distance(f.getSourceStart(), e.getSourceStart());
						if (dist < 1.0) {
							String.class.getName();
						} else {
							
						}
					}
					
					acc.intersectionEvents.add(insertionPoint, e);
				} else {
					// found
					//assert false;
				}
			}
			
			acc.closeEvents.addAll(c1.closeEvents);
			acc.closeEvents.addAll(c2.closeEvents);
			
			acc.borderStartParam = c1.borderStartParam;
			acc.borderEndParam = c1.borderEndParam;
			
			acc.closeBorderStartParam = c1.closeBorderStartParam;
			acc.closeBorderEndParam = c1.closeBorderEndParam;
			
			if (c2.borderStartParam < c1.borderStartParam) {
				acc.borderStartParam = c2.borderStartParam;
			}
			if (c2.borderEndParam > c1.borderEndParam) {
				acc.borderEndParam = c2.borderEndParam;
			}
			
			if (c2.closeBorderStartParam < c1.closeBorderStartParam) {
				acc.closeBorderStartParam = c2.closeBorderStartParam;
			}
			if (c2.closeBorderEndParam > c1.closeBorderEndParam) {
				acc.closeBorderEndParam = c2.closeBorderEndParam;
			}
			
			return acc;
		}
		
	}
	
	private void cleanupEdges() {
		
		List<Edge> toRemove = new ArrayList<Edge>();
		boolean changed;
		
		while (true) {
			toRemove.clear();
			changed = false;
			
			for (Edge e : graph.edges) {
				if (e.getTotalLength() <= MODEL.world.CAR_LENGTH) {
					toRemove.add(e);
					changed = true;
				}
			}
			
			for (Edge e : toRemove) {
				
				if (e.isRemoved()) {
					/*
					 * may be removed from having been merged in a previous iteration
					 */
					continue;
				}
				
				removeEdgeTop(e);
				
			}
			
			if (!changed) {
				break;
			}
		}
		
	}
}
