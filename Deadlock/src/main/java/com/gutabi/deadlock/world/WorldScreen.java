package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.DLSFileChooser;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.tool.RegularTool;
import com.gutabi.deadlock.world.tool.Tool;

//@SuppressWarnings("static-access")
public class WorldScreen extends ScreenBase {
	
	enum WorldScreenMode {
		EDITING,
		RUNNING,
		PAUSED,
		DIALOG
	}
	
	public boolean FPS_DRAW = false;
	public boolean STOPSIGN_DRAW = true;
	public boolean CARTEXTURE_DRAW = true;
	public boolean EXPLOSIONS_DRAW = true;
	public boolean DEBUG_DRAW = false;
	
	
	
	public WorldCamera cam = new WorldCamera();
	
	public WorldCanvas oldCanvas;
	public WorldCanvas canvas;
	
	public ControlPanel controlPanel;
	
	public World world;
	
	public Preview preview;
	
	public Tool tool;
	
	public Entity hilited;
	
	public WorldScreenMode mode;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	DLSFileChooser fc;
	
	
	
	public WorldScreen() {
		
		mode = WorldScreenMode.EDITING;
		
		tool = new RegularTool(this);
		
		preview = new Preview(this);
		
		stats = new Stats(this);
		
	}
	
	public void setup(RootPaneContainer container) {
		
		canvas = new WorldCanvas(this);
		
		controlPanel = new ControlPanel(this);
		controlPanel.init();
		
		Container cp = container.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas.java());
		cp.add(controlPanel.java());
	}
	
	public void teardown(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		
		cp.remove(canvas.java());
		cp.remove(controlPanel.java());
		
		oldCanvas = canvas;
		canvas = null;
		controlPanel = null;
		
	}
	
	public void postDisplay() {
		
		Dim canvasDim = canvas.postDisplay();
		Dim previewDim = controlPanel.previewPanel.postDisplay();
		
		world.canvasPostDisplay(canvasDim);
		preview.previewPostDisplay(previewDim);
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
		case DIALOG:
			break;
		case EDITING:
			tool.qKey(ev);
			break;
		}
	}
	
	public void wKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.wKey(ev);
			break;
		}
	}
	
	public void gKey(InputEvent ev) {
		
		world.quadrantMap.toggleGrid();
		
		tool.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
		
		render();
		repaintCanvas();
	}
	
	public void deleteKey(InputEvent ev) {
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					world.carMap.destroyCar(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					Set<Vertex> affected = world.graph.removeVertexTop(v);
					world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					Set<Vertex> affected = world.graph.removeRoadTop(e);
					world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					Set<Vertex> affected = world.graph.removeMergerTop(e);
					world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					s.r.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		render();
		repaintCanvas();
		repaintControlPanel();
	}
	
	public void insertKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.insertKey(ev);
			break;
		}
	}
	
	public void escKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			tool.escKey(ev);
			break;
		case DIALOG:
			
//			mode = WorldScreenMode.EDITING;
			
			break;
		}
	}
	
	public void d1Key(InputEvent ev) {
		switch (mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			tool.d1Key(ev);
			break;
		}
	}
	
	public void d2Key(InputEvent ev) {
		switch (mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			tool.d2Key(ev);
			break;
		}
	}
	
	public void d3Key(InputEvent ev) {
		switch (mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			tool.d3Key(ev);
			break;
		}
	}
	
	public void plusKey(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			world.zoom(1.1);
			
			lastMovedOrDraggedWorldPoint = world.canvasToWorld(canvas.lastMovedOrDraggedCanvasPoint);
			break;
		case DIALOG:
			break;
		case EDITING:
			world.zoom(1.1);
			
			lastMovedOrDraggedWorldPoint = world.canvasToWorld(canvas.lastMovedOrDraggedCanvasPoint);
			Entity closest = world.hitTest(lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			tool.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
			break;
		}
		
		render();
		repaintCanvas();
		repaintControlPanel();
	}
	
	public void minusKey(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			world.zoom(0.9);
			
			lastMovedOrDraggedWorldPoint = world.canvasToWorld(canvas.lastMovedOrDraggedCanvasPoint);
			break;
		case DIALOG:
			break;
		case EDITING:
			world.zoom(0.9);
			
			lastMovedOrDraggedWorldPoint = world.canvasToWorld(canvas.lastMovedOrDraggedCanvasPoint);
			Entity closest = world.hitTest(lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			tool.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
			break;
		}
		
		render();
		repaintCanvas();
		repaintControlPanel();
	}
	
	public void aKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.aKey(ev);
			break;
		}
	}
	
	public void sKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.sKey(ev);
			break;
		}
	}
	
	public void ctrlSKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			
			mode = WorldScreenMode.DIALOG;
			
			canvas.disableKeyListener();
			
			fc = new DLSFileChooser();
			int res = fc.showDialog(canvas.java(), "Save");
			
			if (res == JFileChooser.APPROVE_OPTION) {
				
				try {
					
					File f = fc.getSelectedFile();
					
					if (!f.exists()) {
						f.createNewFile();
					}
					
					FileWriter w = new FileWriter(f);
					
					w.write(world.toFileString());
					
					w.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				canvas.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
			} else {
				
				canvas.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
			}
			
			break;
		}
	}
	
	public void ctrlOKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			
			mode = WorldScreenMode.DIALOG;
			
			canvas.disableKeyListener();
			
			fc = new DLSFileChooser();
			int res = fc.showDialog(canvas.java(), "Open");
			
			if (res == JFileChooser.APPROVE_OPTION) {
				
				String fileString = null;
				
				try {
					
					File f = fc.getSelectedFile();
					
					Scanner s = new Scanner(f);
					fileString = s.useDelimiter("\\A").next();
					
					s.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				world = World.fromFileString(this, fileString);
				
				canvas.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
				postDisplay();
				render();
				repaintCanvas();
				repaintControlPanel();
				
			} else {
				
				canvas.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
			}
			
			break;
		}
	}
	
	public void dKey(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.dKey(ev);
			break;
		}
	}
	
	public void enterKey(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case DIALOG:
			break;
		case EDITING:
			tool.dKey(ev);
			break;
		}
		
	}
	
	public Point lastPressedWorldPoint;
	
	public void pressed(InputEvent ev) {
		
		if (ev.c == canvas) {
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
		
		if (ev.c == canvas) {
			
			switch (mode) {
			case RUNNING:
			case PAUSED: {
				Point p = ev.p;
				
				lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
				lastDraggedWorldPoint = world.canvasToWorld(p);
				lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
				break;
			}
			case DIALOG:
				break;
			case EDITING: {
				Point p = ev.p;
				
				lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
				lastDraggedWorldPoint = world.canvasToWorld(p);
				lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
				tool.dragged(ev);
				break;
			}
			}
		} else if (ev.c == controlPanel.previewPanel) {
			preview.dragged(ev);
		} else {
			assert false;
		}
		
	}
	
	public void released(InputEvent ev) {
		
		if (ev.c == canvas) {
			switch (mode) {
			case RUNNING:
			case PAUSED:
			case DIALOG:
				break;
			case EDITING:
				tool.released(ev);
				break;
			}
		} else {
			assert false;
		}
		
	}
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		if (ev.c == canvas) {
			
			switch (mode) {
			case RUNNING:
			case PAUSED: {
				Point p = ev.p;
				
				lastMovedWorldPoint = world.canvasToWorld(p);
				lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
				break;
			}
			case DIALOG:
				break;
			case EDITING: {
				Point p = ev.p;
				
				lastMovedWorldPoint = world.canvasToWorld(p);
				lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
				tool.moved(ev);
				break;
			}
			}
			
		} else {
			assert false;
		}
	}
	
	public void exited(InputEvent ev) {
		
		if (ev.c == canvas) {
			switch (mode) {
			case RUNNING:
			case PAUSED:
			case DIALOG:
				break;
			case EDITING:
				tool.exited(ev);
				break;
			}
		} else if (ev.c == oldCanvas) {
			switch (mode) {
			case RUNNING:
			case PAUSED:
			case DIALOG:
				break;
			case EDITING:
//				tool.exited(ev);
				break;
			}
		} else {
			assert false;
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			controlPanel.startButton.setText("Pause");
			controlPanel.startButton.setActionCommand("pause");
			
			controlPanel.stopButton.setEnabled(true);
			
			startRunning();
			
		} else if (e.getActionCommand().equals("stop")) {
			
			controlPanel.startButton.setText("Start");
			controlPanel.startButton.setActionCommand("start");
			
			controlPanel.stopButton.setEnabled(false);
			
			stopRunning();
			
		} else if (e.getActionCommand().equals("pause")) {
			
			controlPanel.startButton.setText("Unpause");
			controlPanel.startButton.setActionCommand("unpause");
			
			pauseRunning();
			
		} else if (e.getActionCommand().equals("unpause")) {
			
			controlPanel.startButton.setText("Pause");
			controlPanel.startButton.setActionCommand("pause");
			
			unpauseRunning();
			
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				world.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = controlPanel.debugCheckBox.isSelected();
			
			DEBUG_DRAW = state;
			
			render();
			repaintCanvas();
			repaintControlPanel();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = controlPanel.fpsCheckBox.isSelected();
			
			FPS_DRAW = state;
			
			render();
			repaintCanvas();
			repaintControlPanel();
			
		} else if (e.getActionCommand().equals("stopSignDraw")) {
			
			boolean state = controlPanel.stopSignCheckBox.isSelected();
			
			STOPSIGN_DRAW = state;
			
			render();
			repaintCanvas();
			repaintControlPanel();
			
		} else if (e.getActionCommand().equals("carTextureDraw")) {
			
			boolean state = controlPanel.carTextureCheckBox.isSelected();
			
			CARTEXTURE_DRAW = state;
			
			repaintCanvas();
			repaintControlPanel();
			
		} else if (e.getActionCommand().equals("explosionsDraw")) {
			
			boolean state = controlPanel.explosionsCheckBox.isSelected();
			
			EXPLOSIONS_DRAW = state;
			
			repaintCanvas();
			repaintControlPanel();
		}
	}
	
	/**
	 * screen method
	 */
	public void render() {
		world.renderCanvas();
		preview.render();
	}
	
	
	RenderingContext ctxt = new RenderingContext(RenderingContextType.CANVAS);
	
	/**
	 * screen method
	 */
	public void repaintCanvas() {
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (mode == WorldScreenMode.RUNNING) {
				return;
			}
		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
				
//				g2.setColor(Color.DARK_GRAY);
//				g2.fillRect(0, 0, cam.canvasWidth, cam.canvasHeight);
				
				ctxt.g2 = g2;
				ctxt.cam = cam;
				
				//synchronized (VIEW) {
				paintWorldScreen(ctxt);
				//}
				
				g2.dispose();
				
			} while (canvas.bs.contentsRestored());
			
			canvas.bs.show();
			
		} while (canvas.bs.contentsLost());
		
	}
	
	public void paintWorldScreen(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS: {
			
			world.paintWorldBackground(ctxt);
			
			AffineTransform origTrans = ctxt.getTransform();
			
			ctxt.scale(cam.pixelsPerMeter);
			ctxt.translate(-cam.worldViewport.x, -cam.worldViewport.y);
			
			world.paintWorldScene(ctxt);
			
			Entity hilitedCopy;
			synchronized (APP) {
				hilitedCopy = hilited;
			}
			
			if (hilitedCopy != null) {
				hilitedCopy.paintHilite(ctxt);
			}
			
			tool.draw(ctxt);
			
			if (ctxt.FPS_DRAW) {
				
				ctxt.translate(cam.worldViewport.x, cam.worldViewport.y);
				
				stats.paint(ctxt);
			}
			
			ctxt.setTransform(origTrans);
			
			break;
		}
		case PREVIEW: {
			
			world.paintWorldBackground(ctxt);
			
			
			
			AffineTransform origTrans = ctxt.getTransform();
			
			ctxt.scale(cam.pixelsPerMeter);
			ctxt.translate(-cam.worldViewport.x, -cam.worldViewport.y);
			
			world.paintWorldScene(ctxt);
			
			ctxt.setTransform(origTrans);
			
			break;
		}
		}
		
	}
	
	public void repaintControlPanel() {
		controlPanel.repaint();
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c == canvas) {
//			VIEW.canvas.bs.show();
			assert false;
		} else if (ev.c == controlPanel.previewPanel) {
			preview.paint(ev.ctxt);
		} else {
			assert false;
		}
	}

}
