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

import org.apache.log4j.Logger;

import com.gutabi.deadlock.Main;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
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
		
		kc.init();
		
		e = Executors.newSingleThreadExecutor();
		
		try {
			queueAndWait(new Runnable(){

				@Override
				public void run() {
					Thread.currentThread().setName("controller");
					Thread.currentThread().setUncaughtExceptionHandler(Main.handler);
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
	
	
	Point lastPressPoint;
	long lastPressTime;
	
	public void pressed(Point p) {
		
		lastPressPoint = p;
		lastPressTime = System.currentTimeMillis();
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE: {
				
				if (p.getY() <= 10 || p.getX() <= 10) {
					// source
				} else if (p.getX() >= MODEL.WORLD_WIDTH-10 || p.getY() >= MODEL.WORLD_HEIGHT-10) {
					// sink
				} else {
					draftStart(p);
					VIEW.repaint();
				}
				break;
			}
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
		
		lastDragPoint = null;
		lastDragTime = -1;
	}
	
	
	Point lastDragPoint;
	long lastDragTime;
	
	public void dragged(final Point p) {
		
		lastDragPoint = p;
		lastDragTime = System.currentTimeMillis();
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				break;
			case DRAFTING:
				draftMove(p);
				//VIEW.renderBackground();
				VIEW.repaint();
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
	
	
	long lastReleaseTime;
	
	public void released() {
		
		lastReleaseTime = System.currentTimeMillis();
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE: {
				
				if (lastReleaseTime - lastPressTime < 500 && lastDragPoint == null) {
					// click
					if (lastPressPoint.getY() <= 10 || lastPressPoint.getX() <= 10) {
						// source
						
						MODEL.addSource(lastPressPoint);
						
						VIEW.renderBackground();
						VIEW.repaint();
						
					} else if (lastPressPoint.getX() >= MODEL.WORLD_WIDTH-10 || lastPressPoint.getY() >= MODEL.WORLD_HEIGHT-10) {
						// sink
						
						MODEL.addSink(lastPressPoint);
						
						VIEW.renderBackground();
						VIEW.repaint();
						
					} else {
						
					}
				}
				
				break;
			}
			case DRAFTING:
				draftEnd();
				VIEW.renderBackground();
				VIEW.repaint();
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
	
	public void moved(final Point p) {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				
				Position closest = MODEL.closestPosition(p, 10);
				if (closest != null) {
					MODEL.hilited = closest.getDriveable();
				} else {
					MODEL.hilited = null;
				}
				
				VIEW.repaint();
				break;
			case DRAFTING:
				;
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
	
	public void deleteKey() {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				
				if (MODEL.hilited != null) {
					
					if (MODEL.hilited instanceof Vertex) {
						Vertex v = (Vertex)MODEL.hilited;
						
						MODEL.removeVertex(v);
						
					} else {
						Edge e = (Edge)MODEL.hilited;
						
						MODEL.removeEdge(e);
						
					}
					
					MODEL.hilited = null;
					
					VIEW.renderBackground();
					VIEW.repaint();
				}
				
				break;
			case DRAFTING:
				;
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
	
	public void undoKey() {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				
				break;
			case DRAFTING:
				;
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
	
	public void redoKey() {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				
				break;
			case DRAFTING:
				;
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
		
		VIEW.renderBackground();
		
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
		
		MODEL.hilited = null;
		
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
		p = new Point(Math.rint(p.getX() * 1/MODEL.getZoom()), Math.rint(p.getY() * 1/MODEL.getZoom()));
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
				//curStroke = massageStrategy1(MODEL.curStrokeRaw);
				curStroke = MODEL.curStrokeRaw;
				break;
			case CURRENT:
				curStroke = massageCurrent(MODEL.curStrokeRaw);
				break;
			}
		} else {
			curStroke = MODEL.curStrokeRaw;
		}
		if (curStroke.size() >= 2) {
			MODEL.processNewStroke(curStroke);
		}
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
	 * deal with general shape here
	 * 
	 * do not worry about intersections with other edges here
	 */
	private List<Point> massageCurrent(List<Point> raw) {
		
		List<Point> m = raw;
		m = removeDuplicates(m);
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
	
}
