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

import com.gutabi.deadlock.DeadlockMain;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Hilitable;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.model.Car;

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
		
		kc.init();
		
		e = Executors.newSingleThreadExecutor();
		
		try {
			queueAndWait(new Runnable(){

				@Override
				public void run() {
					Thread.currentThread().setName("controller");
					Thread.currentThread().setUncaughtExceptionHandler(DeadlockMain.handler);
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
	
	
	Point lastPressPanelPoint;
	long lastPressTime;
	
	Point origWorldViewLoc;
	
	public void pressed(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		Point p = ev.getPoint();
		
		if (c == VIEW.panel) {
			
			lastPressPanelPoint = p;
			lastPressTime = System.currentTimeMillis();
			
			lastDragPanelPoint = null;
			lastDragTime = -1;
			
		}
		
	}
	
	
	Point lastDragPanelPoint;
	Point lastDragPreviewPoint;
	long lastDragTime;
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		Point p = ev.getPoint();
		
		if (c == VIEW.panel) {
			
			boolean lastDragPanelPointWasNull = (lastDragPanelPoint == null);
			
			lastDragPanelPoint = p;
			lastDragTime = System.currentTimeMillis();
			
			synchronized (MODEL) {
				switch (MODEL.getMode()) {
				case IDLE: {
					
					if (lastDragPanelPointWasNull) {
						// first drag
						draftStart(lastPressPanelPoint);
						draftMove(lastDragPanelPoint);
						VIEW.repaint();
					} else {
						assert false;
					}
					
					break;
				}
				case DRAFTING:
					draftMove(lastDragPanelPoint);
					//VIEW.renderBackground();
					VIEW.repaint();
					break;
				case RUNNING:
				case PAUSED:
					;
					break;
				}
			}
			
		}
		
	}
	
	
	long lastReleaseTime;
	
	public void released(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		if (c == VIEW.panel) {
			
			lastReleaseTime = System.currentTimeMillis();
			
			synchronized (MODEL) {
				switch (MODEL.getMode()) {
				case IDLE: {
					
					if (lastReleaseTime - lastPressTime < 500 && lastDragPanelPoint == null) {
						// click
						;
					}
					
					break;
				}
				case DRAFTING:
					draftEnd();
					VIEW.renderBackground();
					VIEW.repaint();
					break;
				case RUNNING:
				case PAUSED:
					;
					break;
				}
			}
			
		}
		
	}
	
	public void moved(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		if (c == VIEW.panel) {
			
			Point p = ev.getPoint();
			
			synchronized (MODEL) {
				switch (MODEL.getMode()) {
				case RUNNING:
				case PAUSED:
				case IDLE: {
					
					Hilitable closest = MODEL.world.hitTest(VIEW.panelToWorld(p));
					MODEL.hilited = closest;
					
					VIEW.repaint();
					break;
				}
				case DRAFTING:
					;
					break;
				}
			}
			
		}
		
	}
	
	public void deleteKey() {
		
		synchronized (MODEL) {
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Vertex) {
					Vertex v = (Vertex)MODEL.hilited;
					
					if (v instanceof Sink) {
						return;
					} else if (v instanceof Source) {
						return;
					}
					
					MODEL.world.removeVertex(v);
					
				} else if (MODEL.hilited instanceof Edge) {
					Edge e = (Edge)MODEL.hilited;
					
					MODEL.world.removeEdge(e);
					
				} else if (MODEL.hilited instanceof Car) {
					Car c = (Car)MODEL.hilited;
					
					c.b2dCleanUp();
					MODEL.world.cars.remove(c);
					
				} else {
					throw new AssertionError();
				}
				
				VIEW.renderBackground();
				VIEW.repaint();
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
		
		MODEL.world.preStart();
		
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
		
		MODEL.stroke.start(p);
	}
	
	private void draftMove(Point p) {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.stroke.move(p);
	}
	
	private void draftEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		List<Point> curStroke = MODEL.stroke.getPoints();
		curStroke = massageCurrent(curStroke);
		if (curStroke.size() >= 2) {
			MODEL.world.processNewWorldStroke(curStroke);
		}
		assert MODEL.world.checkConsistency();
		MODEL.stroke.clear();
		
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
		m = panelToWorld(m);
		m = removeDuplicates(m);
		return m;
		
	}
	
	private List<Point> panelToWorld(List<Point> raw) {
		List<Point> adj = new ArrayList<Point>();
		for (Point p : raw) {
			adj.add(VIEW.panelToWorld(p));
		}
		return adj;
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
