package com.gutabi.deadlock.core.controller;

import static com.gutabi.deadlock.core.LoggerFactory.LOGGERFACTORY;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.gutabi.core.DPoint;
import com.gutabi.core.IntersectionInfo;
import com.gutabi.core.Point;
import com.gutabi.core.Vertex;
import com.gutabi.deadlock.core.Logger;

public class DeadlockController {
	
	public static DeadlockController CONTROLLER;
	
	public DPoint lastPointRaw;
	public List<DPoint> curStrokeRaw = new ArrayList<DPoint>();
	public List<List<DPoint>> allStrokes = new ArrayList<List<DPoint>>();
	
	public MassageStrategy strat = MassageStrategy.CURRENT;
	
	ExecutorService e;
	
	public ControlMode mode = ControlMode.IDLE;
	
	Logger logger = LOGGERFACTORY.getLogger(DeadlockController.class);
	
	public DeadlockController() {
		
	}
	
	public void init() {
//		Thread t = new Thread(new ControllerRunnable(DeadlockController.this), "controller");
//		t.setDaemon(true);
//		t.start();
		e = Executors.newSingleThreadExecutor();
		
		try {
			queueAndWait(new Runnable(){

				@Override
				public void run() {
					Thread.currentThread().setName("controller");
				}});
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void queue(Runnable task) {
		//Future f = e.submit(task);
		e.execute(task);
	}
	
	public void queueAndWait(Runnable task) throws InterruptedException, ExecutionException {
		Future<?> f = e.submit(task);
		f.get();
	}
	
	public void inputStart(InputEvent e) {
		assert Thread.currentThread().getName().equals("controller");
		
		DPoint p;
		p = e.getDPoint();
		p = new DPoint((int)(p.x * 1/VIEW.getZoom()), (int)(p.y * 1/VIEW.getZoom()));
		p = Point.add(p, VIEW.viewLoc);
		
		lastPointRaw = p;
		curStrokeRaw.add(p);
		allStrokes.add(new ArrayList<DPoint>());
		allStrokes.get(allStrokes.size()-1).add(p);
	}
	
	public void inputMove(InputEvent e) {
		assert Thread.currentThread().getName().equals("controller");
		
		DPoint p;
		p = e.getDPoint();
		p = new DPoint((int)(p.x * 1/VIEW.getZoom()), (int)(p.y * 1/VIEW.getZoom()));
		p = Point.add(p, VIEW.viewLoc);
		
		if (!DPoint.equals(p, lastPointRaw)) {
			curStrokeRaw.add(p);
			lastPointRaw = p;
			allStrokes.get(allStrokes.size()-1).add(p);
		}
	}
	
	public void inputEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		List<DPoint> curStroke = null;
		if (strat != null) {
			switch (strat) {
			case NONE:
				curStroke = curStrokeRaw;
				break;
			case STRATEGY1:
				curStroke = massageStrategy1(curStrokeRaw);
				break;
			case CURRENT:
				curStroke = massageCurrent(curStrokeRaw);
				break;
			}
		} else {
			curStroke = curStrokeRaw;
		}
		for (int i = 0; i < curStroke.size()-1; i++) {
			MODEL.processStroke(curStroke.get(i), curStroke.get(i+1));
		}
		assert MODEL.checkConsistency();
		lastPointRaw = null;
		curStrokeRaw.clear();
	}
	
	/*
	 * only adjust to closest vertices
	 */
	private List<DPoint> massageStrategy1(List<DPoint> raw) {
		
		List<DPoint> adj = new ArrayList<DPoint>(raw);
		
		if (adj.size() == 1) {
			return adj;
		}
		
		int s = raw.size();
		DPoint first = raw.get(0);
		DPoint last = raw.get(s-1);
		
		/*
		 * first and last have to be very close
		 */
		if ((!DPoint.equals(last, first)) && Point.dist(last, first) * VIEW.getZoom() <= 20.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!DPoint.equals(raw.get(s-2), first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		}
		
		DPoint firstBest = null;
		
		Vertex firstClosest = MODEL.findClosestVertex(first);
		if (firstClosest != null && Point.dist(first, firstClosest.getPoint()) * VIEW.getZoom() <= 40.0) {
			firstBest = firstClosest.getPoint().toDPoint();
		}
		
		if (firstBest != null) {
			
			if (adj.size() == 1) {
				adj.set(0, firstBest);
			} else {
				if (!DPoint.equals(adj.get(1), firstBest)) {
					adj.set(0, firstBest);
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		DPoint lastBest = null;
		
		Vertex lastClosest = MODEL.findClosestVertex(last);
		if (lastClosest != null && Point.dist(last, lastClosest.getPoint()) * VIEW.getZoom() <= 40.0) {
			lastBest = lastClosest.getPoint().toDPoint();
		}
		
		/*
		 * if firstBest and lastBest are both non-null and equal, then don't also connect the last to the same vertex, it looks weird
		 */
		if (lastBest != null && lastBest != firstBest) {
			
			if (adj.size() == 1) {
				adj.set(s-1, lastBest);
			} else {
				if (!DPoint.equals(adj.get(s-2), lastBest)) {
					adj.set(s-1, lastBest);
					last = adj.get(s-1);
				} else {
					adj.remove(s-1);
					s = s-1;
					last = adj.get(s-1);
				}
			}
			
		}
		
		return adj;
	}
	
	private List<DPoint> massageCurrent(List<DPoint> raw) {
		
		List<DPoint> m = raw;
		m = connectEndsToVerticesOrEdges(m);
		//m = mergeIntersectingOrCloseSegments(m);
//		if (approximate shape like line, circle, etc) {
//			m = massage into shape
//		} else {
//			m = smooth points
//		}
		return m;
		
	}
	
	private List<DPoint> connectEndsToVerticesOrEdges(List<DPoint> raw) {
		
		List<DPoint> adj = new ArrayList<DPoint>(raw);
		
		if (adj.size() == 1) {
			return adj;
		}
		
		int s = raw.size();
		DPoint first = raw.get(0);
		DPoint last = raw.get(s-1);
		
		/*
		 * first and last have to be very close
		 */
		if ((!DPoint.equals(last, first)) && Point.dist(last, first) <= 10.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!DPoint.equals(raw.get(s-2), first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		} else {
			logger.debug(Point.dist(last, first) + " " + VIEW.getZoom());
		}
		
		DPoint firstBest = null;
		
		Vertex firstClosest = MODEL.findClosestVertex(first);
		if (firstClosest != null && Point.dist(first, firstClosest.getPoint()) <= 10.0) {
			firstBest = firstClosest.getPoint().toDPoint();
		}
		
		if (firstBest == null) {
			/*
			 * the point doesn't necessarily have to exist yet, it could be between 2 other points
			 */
			IntersectionInfo closest = MODEL.findClosestSegment(first);
			if (closest != null && Point.dist(first, closest.point) <= 10.0) {
				firstBest = closest.point;
			}
		}
		
		if (firstBest != null) {
			
			if (adj.size() == 1) {
				adj.set(0, firstBest);
			} else {
				if (!DPoint.equals(adj.get(1), firstBest)) {
					adj.set(0, firstBest);
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		DPoint lastBest = null;
		
		Vertex lastClosest = MODEL.findClosestVertex(last);
		if (lastClosest != null && Point.dist(last, lastClosest.getPoint()) <= 10.0) {
			lastBest = lastClosest.getPoint().toDPoint();
		}
		
		if (lastBest == null) {
			/*
			 * the point doesn't necessarily have to exist yet, it could be between 2 other points
			 */
			IntersectionInfo closest = MODEL.findClosestSegment(last);
			if (closest != null && Point.dist(last, closest.point) <= 10.0) {
				lastBest = closest.point;
			}
		}
		
		/*
		 * if firstBest and lastBest are both non-null and equal, then don't also connect the last to the same vertex, it looks weird
		 */
		if (lastBest != null && (firstBest == null || !DPoint.equals(lastBest, firstBest))) {
			
			if (adj.size() == 1) {
				adj.set(s-1, lastBest);
			} else {
				if (!DPoint.equals(adj.get(s-2), lastBest)) {
					adj.set(s-1, lastBest);
					last = adj.get(s-1);
				} else {
					adj.remove(s-1);
					s = s-1;
					last = adj.get(s-1);
				}
			}
			
		}
		
		return adj;
		
	}
	
}
