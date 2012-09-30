package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.Main;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;

public class DeadlockController implements ActionListener {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
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
		
//		VIEW.previewPanel.addMouseListener(mc);
//		VIEW.previewPanel.addMouseMotionListener(mc);
		
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
	
	
	Point lastPressWorldPoint;
	Point lastPressPreviewPoint;
	long lastPressTime;
	
	Point origWorldViewLoc;
	
	public void pressed(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		//final Point p = new Point(ev.getX(), ev.getY());
		
		Point p = ev.getPoint();
		
		if (c == VIEW.panel) {
			
			lastPressWorldPoint = VIEW.panelToWorld(p);
			lastPressTime = System.currentTimeMillis();
			
			lastDragWorldPoint = null;
			lastDragTime = -1;
			
		}
//		else if (c == VIEW.previewPanel) {
//			
//			lastPressPreviewPoint = p;
//			lastPressTime = System.currentTimeMillis();
//			
//			lastDragPreviewPoint = null;
//			lastDragTime = -1;
//			
//			origWorldViewLoc = VIEW.worldViewLoc;
//		}
		
	}
	
	
	Point lastDragWorldPoint;
	Point lastDragPreviewPoint;
	long lastDragTime;
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		//final Point p = new Point(ev.getX(), ev.getY());
		Point p = ev.getPoint();
		
		if (c == VIEW.panel) {
			
			boolean lastDragWorldPointWasNull = (lastDragWorldPoint == null);
			
			lastDragWorldPoint = VIEW.panelToWorld(p);
			lastDragTime = System.currentTimeMillis();
			
			synchronized (MODEL) {
				switch (MODEL.getMode()) {
				case IDLE: {
					
					if (lastDragWorldPointWasNull) {
						// first drag
						draftStart(lastPressWorldPoint);
						draftMove(lastDragWorldPoint);
						VIEW.repaint();
					} else {
						assert false;
					}
					
					break;
				}
				case DRAFTING:
					draftMove(lastDragWorldPoint);
					//VIEW.renderBackground();
					VIEW.repaint();
					break;
				case ZOOMING:
					assert false;
					break;
				case RUNNING:
				case PAUSED:
					;
					break;
				}
			}
			
		}
//		else if (c == VIEW.previewPanel) {
//			
//			lastDragPreviewPoint = p;
//			lastDragTime = System.currentTimeMillis();
//			
//			double x = p.getX() - lastPressPreviewPoint.getX();
//			double y = p.getY() - lastPressPreviewPoint.getY();
//			
//			x = x * MODEL.WORLD_WIDTH / 100;
//			y = y * MODEL.WORLD_HEIGHT / 100;
//			
//			VIEW.worldViewLoc = new Point(origWorldViewLoc.getX() + x, origWorldViewLoc.getY() + y);
//			
//			VIEW.renderBackground();
//			VIEW.repaint();
//			
//		}
		
	}
	
	
	long lastReleaseTime;
	
	public void released(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		if (c == VIEW.panel) {
			
			lastReleaseTime = System.currentTimeMillis();
			
			synchronized (MODEL) {
				switch (MODEL.getMode()) {
				case IDLE: {
					
					if (lastReleaseTime - lastPressTime < 500 && lastDragWorldPoint == null) {
						// click
						
						MODEL.world.addVertexTop(lastPressWorldPoint);
						
						VIEW.renderBackground();
						VIEW.repaint();
						
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
				case PAUSED:
					;
					break;
				}
			}
			
		}
//		else if (c == VIEW.previewPanel) {
//			
//		}
		
	}
	
	public void moved(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		if (c == VIEW.panel) {
			
			//final Point p = new Point(ev.getX(), ev.getY());
			Point p = ev.getPoint();
			
			synchronized (MODEL) {
				switch (MODEL.getMode()) {
				case IDLE:
					
					Position closest = MODEL.world.findClosestPosition(VIEW.panelToWorld(p), MODEL.world.MOUSE_RADIUS);
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
				case PAUSED:
					;
					break;
				}
			}
			
		}
//		else if (c == VIEW.previewPanel) {
//			
//		}
		
	}
	
	public void deleteKey() {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				
				if (MODEL.hilited != null) {
					
					Point p;
					
					if (MODEL.hilited instanceof Vertex) {
						Vertex v = (Vertex)MODEL.hilited;
						
						if (v instanceof Sink) {
							return;
						} else if (v instanceof Source) {
							return;
						}
						
						p = v.getPoint();
						
						MODEL.world.removeVertex(v);
						
					} else {
						Edge e = (Edge)MODEL.hilited;
						
						if (!e.isStandAlone()) {
							Position middle = e.getStart().travel(e, e.getEnd(), e.getTotalLength()/2);
							p = middle.getPoint();
						} else {
							p = e.getPoint(0);
						}
						
						MODEL.world.removeEdge(e);
						
					}
					
					Position closest = MODEL.world.findClosestDeleteablePosition(p);
					if (closest != null) {
						
						if (closest instanceof Vertex) {
							MODEL.hilited = ((Vertex)closest);
						} else {
							MODEL.hilited = ((EdgePosition)closest).getEdge();
						}
						
					} else {
						MODEL.hilited = null;
					}
					
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
			case PAUSED:
				;
				break;
			}
		}
		
	}
	
	public void tabKey() {
		
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
			case PAUSED:
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
			case PAUSED:
				;
				break;
			}
		}
		
	}
	
	public void insertKey() {
		
		synchronized (MODEL) {
			switch (MODEL.getMode()) {
			case IDLE:
				
				VIEW.renderBackground();
				VIEW.repaint();
				
				break;
			case DRAFTING:
				;
				break;
			case ZOOMING:
				assert false;
				break;
			case RUNNING:
			case PAUSED:
				;
				break;
			}
		}
		
	}

	
	
	
	public void startRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.RUNNING);
		
		VIEW.renderBackground();
		
		MODEL.world.preprocess();
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.IDLE);
	}
	
	public void pauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.PAUSED);
	}
	
	public void unpauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.RUNNING);
		synchronized (MODEL) {
			MODEL.notifyAll();
		}
	}
	
	
	
	private void draftStart(Point p) {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.DRAFTING);
		
		MODEL.hilited = null;
		
		MODEL.lastPointRaw = p;
		MODEL.curStrokeRaw.add(p);
		MODEL.allStrokes.add(new ArrayList<Point>());
		MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
	}
	
	private void draftMove(Point p) {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.curStrokeRaw.add(p);
		MODEL.lastPointRaw = p;
		MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
	}
	
	private void draftEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		List<Point> curStroke = null;
		curStroke = massageCurrent(MODEL.curStrokeRaw);
		if (curStroke.size() >= 2) {
			MODEL.world.processNewStroke(curStroke);
		}
		assert MODEL.world.checkConsistency();
		MODEL.lastPointRaw = null;
		MODEL.curStrokeRaw.clear();
		
		MODEL.setMode(ControlMode.IDLE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			VIEW.controlPanel.stopButton.setEnabled(true);
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.startRunning();
				}}
			);
			
		} else if (e.getActionCommand().equals("stop")) {
			
			VIEW.controlPanel.startButton.setText("Start");
			VIEW.controlPanel.startButton.setActionCommand("start");
			
			VIEW.controlPanel.stopButton.setEnabled(false);
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.stopRunning();
				}}
			);
		} else if (e.getActionCommand().equals("pause")) {
			
			VIEW.controlPanel.startButton.setText("Unpause");
			VIEW.controlPanel.startButton.setActionCommand("unpause");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.pauseRunning();
				}}
			);
		} else if (e.getActionCommand().equals("unpause")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.unpauseRunning();
				}}
			);
		}
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
			if (last == null || !p.equals(last)) {
				adj.add(p);
			}
			last = p;
		}
		return adj;
	}
	
}
