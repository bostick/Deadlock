package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.cursor.Cursor;
import com.gutabi.deadlock.world.cursor.RegularCursor;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;

@SuppressWarnings("static-access")
public class WorldScreen extends ScreenBase {
	
	enum WorldScreenMode {
		EDITING,
		RUNNING,
		PAUSED,
	}
	
	public World world;
	
	public Preview preview;
	
	public Cursor cursor;
	
	public Entity hilited;
	
	public WorldScreenMode mode;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	
	
	public WorldScreen() {
		
		mode = WorldScreenMode.EDITING;
		
		cursor = new RegularCursor(this);
		
		preview = new Preview(this);
		
		stats = new Stats(world);
		
	}
	
	public void init() {
		
		world.init();
		
		preview.init();
	}
	
	public void canvasPostDisplay(Dim dim) {	
		
		world.canvasPostDisplay(dim);
		
	}
	
	public void startRunning() {
		
		mode = WorldScreenMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable(this));
		t.start();
		
	}
	
	public void stopRunning() {
		
		mode = WorldScreenMode.EDITING;
		
	}
	
	public void pauseRunning() {
		
		mode = WorldScreenMode.PAUSED;
	}
	
	public void unpauseRunning() {
		
		mode = WorldScreenMode.RUNNING;
		
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public void qKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.qKey(ev);
			break;
		}
	}
	
	public void wKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.wKey(ev);
			break;
		}
	}
	
	public void gKey(InputEvent ev) {
		
		world.quadrantMap.toggleGrid();
		
		cursor.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
		
		render();
		repaint();
	}
	
	public void deleteKey(InputEvent ev) {
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					world.removeCarTop(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					world.removeVertexTop(v);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					world.removeRoadTop(e);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					world.removeMergerTop(e);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					world.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		render();
		repaint();
	}
	
	public void insertKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.insertKey(ev);
			break;
		}
	}
	
	public void escKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.escKey(ev);
			break;
		}
	}
	
	public void d1Key(InputEvent ev) {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case EDITING:
			cursor.d1Key(ev);
			break;
		}
	}
	
	public void d2Key(InputEvent ev) {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case EDITING:
			cursor.d2Key(ev);
			break;
		}
	}
	
	public void d3Key(InputEvent ev) {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case EDITING:
			cursor.d3Key(ev);
			break;
		}
	}
	
	public void plusKey(InputEvent ev) {
		
		world.zoom(1.1);
		
		lastMovedOrDraggedWorldPoint = world.canvasToWorld(VIEW.canvas.lastMovedOrDraggedCanvasPoint);
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			Entity closest = world.hitTest(lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			cursor.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
			break;
		}
		
		render();
		repaint();
	}
	
	public void minusKey(InputEvent ev) {
		
		world.zoom(0.9);
		
		lastMovedOrDraggedWorldPoint = world.canvasToWorld(VIEW.canvas.lastMovedOrDraggedCanvasPoint);
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			Entity closest = world.hitTest(lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			cursor.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
			break;
		}
		
		render();
		repaint();
	}
	
	public void aKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.aKey(ev);
			break;
		}
	}
	
	public void sKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.sKey(ev);
			break;
		}
	}
	
	public void dKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.dKey(ev);
			break;
		}
	}
	
	public Point lastPressedWorldPoint;
	
	public void pressed(InputEvent ev) {
		
		if (ev.c == VIEW.canvas) {
			Point p = ev.p;
			
			lastPressedWorldPoint = world.canvasToWorld(p);
			lastDraggedWorldPoint = null;
		} else {
			assert false;
		}
		
	}
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	public void dragged(InputEvent ev) {
		
		if (ev.c == VIEW.canvas) {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = world.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			
			switch (mode) {
			case RUNNING:
			case PAUSED:
				break;
			case EDITING:
				cursor.dragged(ev);
				break;
			}
		} else if (ev.c == VIEW.previewPanel) {
			preview.dragged(ev);
		} else {
			assert false;
		}
		
	}
	
	public void released(InputEvent ev) {
		
		if (ev.c == VIEW.canvas) {
			switch (mode) {
			case RUNNING:
			case PAUSED:
				break;
			case EDITING:
				cursor.released(ev);
				break;
			}
		} else {
			assert false;
		}
		
	}
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		if (ev.c == VIEW.canvas) {
			
			Point p = ev.p;
			
			lastMovedWorldPoint = world.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			
			switch (mode) {
			case RUNNING:
			case PAUSED:
				break;
			case EDITING:
				cursor.moved(ev);
				break;	
			}
			
		} else {
			assert false;
		}
	}
	
	public void exited(InputEvent ev) {
		
		if (ev.c == VIEW.canvas) {
			switch (mode) {
			case RUNNING:
			case PAUSED:
				break;
			case EDITING:
				cursor.exited(ev);
				break;
			}
		} else if (ev.c == VIEW.oldCanvas) {
			switch (mode) {
			case RUNNING:
			case PAUSED:
				break;
			case EDITING:
				cursor.exited(ev);
				break;
			}
		} else {
			assert false;
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			VIEW.controlPanel.stopButton.setEnabled(true);
			
			startRunning();
			
		} else if (e.getActionCommand().equals("stop")) {
			
			VIEW.controlPanel.startButton.setText("Start");
			VIEW.controlPanel.startButton.setActionCommand("start");
			
			VIEW.controlPanel.stopButton.setEnabled(false);
			
			stopRunning();
			
		} else if (e.getActionCommand().equals("pause")) {
			
			VIEW.controlPanel.startButton.setText("Unpause");
			VIEW.controlPanel.startButton.setActionCommand("unpause");
			
			pauseRunning();
			
		} else if (e.getActionCommand().equals("unpause")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			unpauseRunning();
			
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = VIEW.controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				APP.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = VIEW.controlPanel.debugCheckBox.isSelected();
			
			APP.DEBUG_DRAW = state;
			
			render();
			repaint();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			APP.FPS_DRAW = state;
			
			render();
			repaint();
			
		} else if (e.getActionCommand().equals("stopSignDraw")) {
			
			boolean state = VIEW.controlPanel.stopSignCheckBox.isSelected();
			
			APP.STOPSIGN_DRAW = state;
			
			render();
			repaint();
			
		} else if (e.getActionCommand().equals("carTextureDraw")) {
			
			boolean state = VIEW.controlPanel.carTextureCheckBox.isSelected();
			
			APP.CARTEXTURE_DRAW = state;
			
			repaint();
			
		} else if (e.getActionCommand().equals("explosionsDraw")) {
			
			boolean state = VIEW.controlPanel.explosionsCheckBox.isSelected();
			
			APP.EXPLOSIONS_DRAW = state;
			
			repaint();
		}
	}
	
	/**
	 * screen method
	 */
	public void render() {
		world.renderCanvas();
		preview.render();
	}
	
	
	/**
	 * screen method
	 */
	public void repaint() {
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (mode == WorldScreenMode.RUNNING) {
				return;
			}
		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				g2.setColor(Color.LIGHT_GRAY);
				g2.fillRect(0, 0, VIEW.canvas.getWidth(), VIEW.canvas.getHeight());
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.scale(world.pixelsPerMeter);
				ctxt.translate(-world.worldViewport.x, -world.worldViewport.y);
				
				synchronized (VIEW) {
					paintWorldScreen(ctxt);
				}
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());
		
		VIEW.controlPanel.repaint();
		
	}
	
	public void paintWorldScreen(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			world.paintWorld(ctxt);
			
			Entity hilitedCopy;
			synchronized (APP) {
				hilitedCopy = hilited;
			}
			
			if (hilitedCopy != null) {
				hilitedCopy.paintHilite(ctxt);
			}
			
			cursor.draw(ctxt);
			
			if (APP.FPS_DRAW) {
				
				ctxt.translate(world.worldViewport.x, world.worldViewport.y);
				
				stats.paint(ctxt);
			}
			break;
		case PREVIEW:
			world.paintWorld(ctxt);
			break;
		}
		
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c == VIEW.canvas) {
			VIEW.canvas.bs.show();
		} else if (ev.c == VIEW.previewPanel) {
			preview.paint(ev.ctxt);
		} else {
			assert false;
		}
	}

}
