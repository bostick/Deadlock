package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.PositionException;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.view.WindowInfo;

public class DeadlockController implements ActionListener {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public MassageStrategy strat = MassageStrategy.CURRENT;
	
	public MouseController mc;
	public KeyboardController kc;
	
	ExecutorService e;
	
	Logger logger = Logger.getLogger(DeadlockController.class);
	
	public DeadlockController() {
		
	}
	
	public void init() {
		
		mc = new MouseController();
		kc = new KeyboardController();
		
		VIEW.panel.addMouseListener(mc);
		VIEW.panel.addMouseMotionListener(mc);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "rightKeyAction");
		VIEW.panel.getActionMap().put("rightKeyAction", kc.rightKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "leftKeyAction");
		VIEW.panel.getActionMap().put("leftKeyAction", kc.leftKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "upKeyAction");
		VIEW.panel.getActionMap().put("upKeyAction", kc.upKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "downKeyAction");
		VIEW.panel.getActionMap().put("downKeyAction", kc.downKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("EQUALS"), "equalsKeyAction");
		VIEW.panel.getActionMap().put("equalsKeyAction", kc.equalsKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("MINUS"), "minusKeyAction");
		VIEW.panel.getActionMap().put("minusKeyAction", kc.minusKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("0"), "zeroKeyAction");
		VIEW.panel.getActionMap().put("zeroKeyAction", kc.zeroKeyAction);
		
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
	
	public void pressed(Point p) {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				CONTROLLER.draftStart(p);
				break;
			case DRAFTING:
				assert false;
				break;
			case ZOOMING:
				assert false;
				break;
			case RUNNING:
				;
				break;
			}
		}
		
	}
	
	
	public void dragged(final Point p) {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				break;
			case DRAFTING:
				CONTROLLER.draftMove(p);
				break;
			case ZOOMING:
				assert false;
				break;
			case RUNNING:
				;
				break;
			}
		}
		
	}
	
	public void released() {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				break;
			case DRAFTING:
				CONTROLLER.draftEnd();
				break;
			case ZOOMING:
				assert false;
				break;
			case RUNNING:
				;
				break;
			}
		}
		
	}
	
	
	
	
	public void startRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.RUNNING);
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.IDLE);
	}
	
	private void draftStart(Point pp) {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.DRAFTING);
		
		Point p;
		p = pp;
		p = new Point(p.getX() * 1/MODEL.getZoom(), p.getY() * 1/MODEL.getZoom());
		p = Point.add(p, MODEL.viewLoc);
		
		MODEL.lastPointRaw = p;
		MODEL.curStrokeRaw.add(p);
		MODEL.allStrokes.add(new ArrayList<Point>());
		MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
	}
	
	private void draftMove(Point pp) {
		assert Thread.currentThread().getName().equals("controller");
		
		Point p;
		p = pp;
		p = new Point(p.getX() * 1/MODEL.getZoom(), p.getY() * 1/MODEL.getZoom());
		p = Point.add(p, MODEL.viewLoc);
		
		MODEL.curStrokeRaw.add(p);
		MODEL.lastPointRaw = p;
		MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
	}
	
	private void draftEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		List<Point> curStroke = null;
		if (strat != null) {
			switch (strat) {
			case NONE:
				curStroke = MODEL.curStrokeRaw;
				break;
			case STRATEGY1:
				curStroke = massageStrategy1(MODEL.curStrokeRaw);
				break;
			case CURRENT:
				curStroke = massageCurrent(MODEL.curStrokeRaw);
				break;
			}
		} else {
			curStroke = MODEL.curStrokeRaw;
		}
		MODEL.processStroke(curStroke);
		assert MODEL.checkConsistency();
		MODEL.lastPointRaw = null;
		MODEL.curStrokeRaw.clear();
		
		MODEL.setMode(ControlMode.IDLE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			JButton b = (JButton)e.getSource();
			b.setText("Stop");
			b.setActionCommand("stop");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.startRunning();
				}}
			);
			
		} else if (e.getActionCommand().equals("stop")) {
			JButton b = (JButton)e.getSource();
			b.setText("Start");
			b.setActionCommand("start");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.stopRunning();
				}}
			);
		}
	}
	
	public void moveCameraRight() {
		MODEL.viewLoc = MODEL.viewLoc.add(new Point(5, 0));
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("right: center=" + center);
	}
	
	public void moveCameraLeft() {
		MODEL.viewLoc = MODEL.viewLoc.add(new Point(-5, 0));
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("left: center=" + center);
	}
	
	public void moveCameraUp() {
		MODEL.viewLoc = MODEL.viewLoc.add(new Point(0, -5));
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("up: center=" + center);
	}
	
	public void moveCameraDown() {
		MODEL.viewLoc = MODEL.viewLoc.add(new Point(0, 5));
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("down: center=" + center);
	}
	
	public void zoomIn() {
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		MODEL.viewDim = MODEL.viewDim.times(0.9);
		MODEL.viewLoc = center.minus(MODEL.viewDim.divide(2));
		center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("zoom in : center=" + center);
	}
	
	public void zoomOut() {
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		MODEL.viewDim = MODEL.viewDim.times(1.1);
		MODEL.viewLoc = center.minus(MODEL.viewDim.divide(2));
		center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("zoom out : center=" + center);
	}
	
	public void zoomReset() {
		Point center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		MODEL.viewDim = WindowInfo.windowDim();
		MODEL.viewLoc = new Point(0, 0);
		center = MODEL.viewDim.divide(2).add(MODEL.viewLoc);
		logger.debug("zoom reset: center=" + center);
	}
	
	/*
	 * only adjust to closest vertices
	 */
	private List<Point> massageStrategy1(List<Point> raw) {
		
		List<Point> adj = new ArrayList<Point>(raw);
		
		if (adj.size() == 1) {
			return adj;
		}
		
		int s = raw.size();
		Point first = raw.get(0);
		Point last = raw.get(s-1);
		
		/*
		 * first and last have to be very close
		 */
		if ((!Point.equals(last, first)) && Point.dist(last, first) * MODEL.getZoom() <= 20.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!Point.equals(raw.get(s-2), first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		}
		
		Point firstBest = null;
		
		Vertex firstClosest = MODEL.findClosestVertex(first);
		if (firstClosest != null && Point.dist(first, firstClosest.getPoint()) * MODEL.getZoom() <= 40.0) {
			firstBest = firstClosest.getPoint();
		}
		
		if (firstBest != null) {
			
			if (adj.size() == 1) {
				adj.set(0, firstBest);
			} else {
				if (!Point.equals(adj.get(1), firstBest)) {
					adj.set(0, firstBest);
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		Point lastBest = null;
		
		Vertex lastClosest = MODEL.findClosestVertex(last);
		if (lastClosest != null && Point.dist(last, lastClosest.getPoint()) * MODEL.getZoom() <= 40.0) {
			lastBest = lastClosest.getPoint();
		}
		
		/*
		 * if firstBest and lastBest are both non-null and equal, then don't also connect the last to the same vertex, it looks weird
		 */
		if (lastBest != null && lastBest != firstBest) {
			
			if (adj.size() == 1) {
				adj.set(s-1, lastBest);
			} else {
				if (!Point.equals(adj.get(s-2), lastBest)) {
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
	
	private List<Point> massageCurrent(List<Point> raw) {
		
		List<Point> m = raw;
		m = removeDuplicates(m);
		m = connectEndsToVerticesOrEdges(m);
		//m = mergeIntersectingOrCloseSegments(m);
//		if (approximate shape like line, circle, etc) {
//			m = massage into shape
//		} else {
//			m = smooth points
//		}
		return m;
		
	}
	
	private List<Point> removeDuplicates(List<Point> raw) {
		List<Point> adj = new ArrayList<Point>();
		Point last = null;
		for (Point p : raw) {
			if (last == null || !Point.equals(p, last)) {
				adj.add(p);
			}
			last = p;
		}
		return adj;
	}
	
	private List<Point> connectEndsToVerticesOrEdges(List<Point> raw) {
		
		List<Point> adj = new ArrayList<Point>(raw);
		
		if (adj.size() == 1) {
			return adj;
		}
		
		int s = raw.size();
		Point first = raw.get(0);
		Point last = raw.get(s-1);
		
		/*
		 * first and last have to be very close
		 */
		if ((!Point.equals(last, first)) && Point.dist(last, first) <= 10.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!Point.equals(raw.get(s-2), first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		} else {
			logger.debug(Point.dist(last, first) + " " + MODEL.getZoom());
		}
		
		Point firstBest = null;
		
		Vertex firstClosest = MODEL.findClosestVertex(first);
		if (firstClosest != null && Point.dist(first, firstClosest.getPoint()) <= 10.0) {
			firstBest = firstClosest.getPoint();
		}
		
		if (firstBest == null) {
			/*
			 * the point doesn't necessarily have to exist yet, it could be between 2 other points
			 */
			try {
				EdgePosition closest = MODEL.findClosestEdgePosition(first);
				if (closest != null && Point.dist(first, closest.getPoint()) <= 10.0) {
					firstBest = closest.getPoint();
				}
			} catch (PositionException e) {
				/*
				 * this means there is a vertex closer to first than any segment
				 */
			}
		}
		
		if (firstBest != null) {
			
			if (adj.size() == 1) {
				adj.set(0, firstBest);
			} else {
				if (!Point.equals(adj.get(1), firstBest)) {
					adj.set(0, firstBest);
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		Point lastBest = null;
		
		Vertex lastClosest = MODEL.findClosestVertex(last);
		if (lastClosest != null && Point.dist(last, lastClosest.getPoint()) <= 10.0) {
			lastBest = lastClosest.getPoint();
		}
		
		if (lastBest == null) {
			/*
			 * the point doesn't necessarily have to exist yet, it could be between 2 other points
			 */
			try {
				EdgePosition closest = MODEL.findClosestEdgePosition(last);
				if (closest != null && Point.dist(last, closest.getPoint()) <= 10.0) {
					lastBest = closest.getPoint();
				}
			} catch (PositionException e) {
				/*
				 * this means there is a vertex closer to first than any segment
				 */
			}
		}
		
		/*
		 * if firstBest and lastBest are both non-null and equal, then don't also connect the last to the same vertex, it looks weird
		 */
		if (lastBest != null && (firstBest == null || !Point.equals(lastBest, firstBest))) {
			
			if (adj.size() == 1) {
				adj.set(s-1, lastBest);
			} else {
				if (!Point.equals(adj.get(s-2), lastBest)) {
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
