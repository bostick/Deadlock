package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.DeadlockMain;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.StopSign;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public class DeadlockController implements ActionListener {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public ControlMode mode;
	
	public MouseController mc;
	public KeyboardController kc;
	
	ExecutorService e;
	
	Logger logger = Logger.getLogger(DeadlockController.class);
	
	public DeadlockController() {
		mode = ControlMode.IDLE;
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
		
		Component c = ev.c;
		
		Point p = ev.p;
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			lastPressPanelPoint = p;
			lastPressTime = System.currentTimeMillis();
			
//			MODEL.stroke.press(p);
			
			lastDragPanelPoint = null;
			lastDragTime = -1;
			
			VIEW.repaint();
		}
		
	}
	
	
	Point lastDragPanelPoint;
	Point lastDragWorldPoint;
	Point lastDragPreviewPoint;
	long lastDragTime;
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.c;
		
		Point p = ev.p;
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			boolean lastDragPanelPointWasNull = (lastDragPanelPoint == null);
			
			lastDragPanelPoint = p;
			lastDragWorldPoint = VIEW.panelToWorld(p);
			lastDragTime = System.currentTimeMillis();
			
			MODEL.cursor.setPoint(lastDragWorldPoint);
			
			switch (mode) {
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
	
	
	long lastReleaseTime;
	
	public void released(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			lastReleaseTime = System.currentTimeMillis();
			
			switch (mode) {
			case IDLE: {
				
				if (lastReleaseTime - lastPressTime < 500 && lastDragPanelPoint == null) {
					// click
					
//					Point w = VIEW.panelToWorld(lastPressPanelPoint);
//					
//					Entity hit = MODEL.world.hitTest(w);
//					
//					if (hit != null) {
//						
//						if (hit instanceof Vertex) {
//							
////							((Vertex) hit).getEdges();
//							
//						} else if (hit instanceof Road) {
//							
//						}
//						
//					}
				}
				
				break;
			}
			case DRAFTING:
				draftEnd();
				MODEL.world.renderBackground();
				VIEW.repaint();
				break;
			case RUNNING:
			case PAUSED:
				;
				break;
			}
			
		}
		
	}
	
	
	Point lastMovedWorldPoint;
//	Point penMovedWorldPoint;
//	Stroke s = new Stroke(Vertex.INIT_VERTEX_RADIUS);
	
	public void moved(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			Point p = ev.p;
			
//			penMovedWorldPoint = lastMovedWorldPoint;
			lastMovedWorldPoint = VIEW.panelToWorld(p);
			
			MODEL.cursor.setPoint(lastMovedWorldPoint);
			
			switch (mode) {
			case RUNNING:
			case PAUSED:
			case IDLE: {
				
				Entity closest = MODEL.world.bestHitTest(MODEL.cursor.getPoint(), MODEL.cursor.r);
				MODEL.hilited = closest;
				
				VIEW.repaint();
				
				
//				s.add(lastMovedWorldPoint);
				
//				List<SweepEvent> ve = s.vertexEvents();
//				for (SweepEvent e : ve) {
//					logger.debug("event: " + e);
//				}
				
				
				break;
			}
			case DRAFTING:
				
				break;
			}
			
		}
		
	}
	
	public void qKey() {
//		s = new Stroke(Vertex.INIT_VERTEX_RADIUS);
	}
	
	public void deleteKey() {
		
		if (MODEL.hilited != null) {
			
			if (MODEL.hilited.isDeleteable()) {
				
				if (MODEL.hilited instanceof Car) {
					Car c = (Car)MODEL.hilited;
					
					MODEL.world.removeCarTop(c);
					
				} else if (MODEL.hilited instanceof Vertex) {
					Vertex v = (Vertex)MODEL.hilited;
					
					MODEL.world.removeVertexTop(v);
					
				} else if (MODEL.hilited instanceof Road) {
					Road e = (Road)MODEL.hilited;
					
					MODEL.world.removeRoadTop(e);
					
				} else if (MODEL.hilited instanceof Merger) {
					Merger e = (Merger)MODEL.hilited;
					
					MODEL.world.removeMergerTop(e);
					
				} else if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					MODEL.world.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				MODEL.hilited = null;
				
			}
			
		}
		
		MODEL.world.renderBackground();
		VIEW.repaint();
		
	}
	
	public void insertKey() {
		
		switch (mode) {
		case IDLE:
			
			mode = ControlMode.MERGEROUTLINE;
			
			break;
		case MERGEROUTLINE:
			MODEL.world.insertMergerTop(lastMovedWorldPoint);
			MODEL.world.renderBackground();
			VIEW.repaint();
			break;
		default:
			break;
		}
		
	}

	public void startRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		mode = ControlMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		mode = ControlMode.IDLE;
	}
	
	public void pauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		mode = ControlMode.PAUSED;
	}
	
	public void unpauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		mode = ControlMode.RUNNING;
		
		synchronized (MODEL.pauseLock) {
			MODEL.pauseLock.notifyAll();
		}
	}
	
	
	
	private void draftStart(Point p) {
		assert Thread.currentThread().getName().equals("controller");
			
		mode = ControlMode.DRAFTING;
		
		MODEL.hilited = null;
		
		MODEL.stroke = new Stroke(Vertex.INIT_VERTEX_RADIUS);
		MODEL.stroke.add(VIEW.panelToWorld(p));
			
	}
	
	private void draftMove(Point p) {
		assert Thread.currentThread().getName().equals("controller");

		MODEL.stroke.add(VIEW.panelToWorld(p));
	}
	
	private void draftEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.world.processNewStrokeTop(MODEL.stroke);
		
		MODEL.stroke = null;
		
//		MODEL.world.renderBackground();
//		VIEW.repaint();
		
		assert MODEL.world.checkConsistency();
		
		mode = ControlMode.IDLE;
		
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
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = VIEW.controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				MODEL.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					
				}}
			);
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = VIEW.controlPanel.debugCheckBox.isSelected();
			
			MODEL.DEBUG_DRAW = state;
			
			MODEL.world.renderBackground();
			VIEW.repaint();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			MODEL.FPS_DRAW = state;
			
			MODEL.world.renderBackground();
			VIEW.repaint();
			
		}
	}
	
}
