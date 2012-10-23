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
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.model.Car;

@SuppressWarnings("static-access")
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
			
			VIEW.panel.requestFocusInWindow();
			
			lastPressPanelPoint = p;
			lastPressTime = System.currentTimeMillis();
			
			MODEL.stroke.press(p);
			
			lastDragPanelPoint = null;
			lastDragTime = -1;
			
			VIEW.repaint();
		}
		
	}
	
	
	Point lastDragPanelPoint;
	Point lastDragPreviewPoint;
	long lastDragTime;
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		Point p = ev.getPoint();
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			boolean lastDragPanelPointWasNull = (lastDragPanelPoint == null);
			
			lastDragPanelPoint = p;
			lastDragTime = System.currentTimeMillis();
			
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
			case MENU:
				;
				break;
			}
			
		}
		
	}
	
	
	long lastReleaseTime;
	
	public void released(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			lastReleaseTime = System.currentTimeMillis();
			
			switch (MODEL.getMode()) {
			case IDLE: {
				
				if (lastReleaseTime - lastPressTime < 500 && lastDragPanelPoint == null) {
					// click
					
					Point w = VIEW.panelToWorld(lastPressPanelPoint);
					
					Entity hit = MODEL.world.hitTest(w);
					
					if (hit != null) {
						
						if (hit instanceof Vertex) {
							
							((Vertex) hit).getEdges();
							
						} else if (hit instanceof Edge) {
							
						}
						
					}
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
			case MENU:
				
				if (lastReleaseTime - lastPressTime < 500 && lastDragPanelPoint == null) {
					// click
					
				}
				
				break;
			}
			
		}
		
	}
	
	public void moved(InputEvent ev) {
		
		Component c = ev.getComponent();
		
		if (c == VIEW.panel) {
			
			VIEW.panel.requestFocusInWindow();
			
			Point p = ev.getPoint();
			
			switch (MODEL.getMode()) {
			case RUNNING:
			case PAUSED:
			case IDLE: {
				
				Point worldPoint = VIEW.panelToWorld(p);
				
				Entity closest = MODEL.world.hitTest(worldPoint);
				MODEL.hilited = closest;
				
				MODEL.stroke.move(p);
				
				VIEW.repaint();
				
				break;
			}
			case DRAFTING:
			case MENU:
				;
				break;
			}
			
		}
		
	}
	
	public void deleteKey() {
		
		if (MODEL.hilited != null) {
			
			if (MODEL.hilited instanceof Vertex) {
				Vertex v = (Vertex)MODEL.hilited;
				
				if (v instanceof Sink) {
					return;
				} else if (v instanceof Source) {
					return;
				}
				
				MODEL.world.removeVertexTop(v);
				
			} else if (MODEL.hilited instanceof Edge) {
				Edge e = (Edge)MODEL.hilited;
				
				MODEL.world.removeEdgeTop(e);
				
			} else if (MODEL.hilited instanceof Car) {
				Car c = (Car)MODEL.hilited;
				
				MODEL.world.removeCarTop(c);
				
			} else {
				throw new AssertionError();
			}
			
			MODEL.hilited = null;
		}
		
		MODEL.world.renderBackground();
		VIEW.repaint();
		
	}
	
//	public void tabKey() {
//		
//	}
	
//	public void undoKey() {
//		
//		switch (MODEL.getMode()) {
//		case IDLE:
//			
//			break;
//		case DRAFTING:
//			;
//			break;
//		case RUNNING:
//		case PAUSED:
//			;
//			break;
//		}
//		
//	}
	
//	public void redoKey() {
//		
//		switch (MODEL.getMode()) {
//		case IDLE:
//			
//			break;
//		case DRAFTING:
//			;
//			break;
//		case RUNNING:
//		case PAUSED:
//			;
//			break;
//		}
//		
//	}
	
//	public void insertKey() {
//		
//		switch (MODEL.getMode()) {
//		case IDLE:
//			
//			MODEL.world.renderBackground();
//			VIEW.repaint();
//			
//			break;
//		case DRAFTING:
//			;
//			break;
//		case RUNNING:
//		case PAUSED:
//			;
//			break;
//		}
//		
//	}

	
	
	
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
	
	public void pauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.PAUSED);
	}
	
	public void unpauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.setMode(ControlMode.RUNNING);
		
		synchronized (MODEL.pauseLock) {
			MODEL.pauseLock.notifyAll();
		}
	}
	
	
	
	private void draftStart(Point p) {
		assert Thread.currentThread().getName().equals("controller");
			
		MODEL.setMode(ControlMode.DRAFTING);
		
		MODEL.hilited = null;
		
//		MODEL.stroke = new Stroke();
		
		MODEL.stroke.start(p);
			
	}
	
	private void draftMove(Point p) {
		assert Thread.currentThread().getName().equals("controller");

		MODEL.stroke.drag(p);
	}
	
	private void draftEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		if (MODEL.stroke.size() >= 2) {
			MODEL.world.processNewStrokeTop(MODEL.stroke);
		}
		
		MODEL.stroke.clear();
		
		MODEL.world.renderBackground();
		VIEW.repaint();
		
		assert MODEL.world.checkConsistency();
		
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
