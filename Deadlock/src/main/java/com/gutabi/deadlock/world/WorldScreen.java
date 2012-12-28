package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.DLSFileChooser;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.ui.RenderingContextType;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.tools.RegularTool;
import com.gutabi.deadlock.world.tools.Tool;

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
	
	/**
	 * move physics forward by dt seconds
	 */
	public double DT = 0.01;
	
	public WorldCamera cam = new WorldCamera();
	
	public WorldCanvas oldCanvas;
	public WorldCanvas canvas;
	
	public ControlPanel controlPanel;
	
	public World world;
	
	public Tool tool;
	
	public Entity hilited;
	
	public WorldScreenMode mode;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	DLSFileChooser fc;
	
	
	
	public WorldScreen() {
		
		mode = WorldScreenMode.EDITING;
		
		tool = new RegularTool(this);
		
		stats = new Stats(this);
		
		canvas = new WorldCanvas(this);
		
		controlPanel = new ControlPanel(this);
		controlPanel.init();
		
	}
	
	public void setup(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		cp.setLayout(null);
		
		cp.add(canvas.java());
		
		Dimension size = canvas.java().getSize();
		canvas.java().setBounds(0, 0, size.width, size.height);
		
		cp.add(controlPanel.java());
		
		size = controlPanel.java().getSize();
		controlPanel.java().setBounds(0 + canvas.java().getWidth(), 0, size.width, size.height);
		
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
		
		world.canvasPostDisplay(canvasDim);
//		canvas.postDisplay();
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
		controlPanel.repaint();
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
		controlPanel.repaint();
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
		controlPanel.repaint();
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
				controlPanel.repaint();
				
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
				break;
			}
		} else {
			assert false;
		}
		
	}
	
	public void render() {
//		world.renderCanvas();
		canvas.render();
		controlPanel.preview.render();
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
				
				ctxt.g2 = g2;
				ctxt.cam = cam;
				ctxt.FPS_DRAW = FPS_DRAW;
				
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
			
			canvas.paint(ctxt);
			
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
			
			controlPanel.preview.paint(ctxt);
			
			AffineTransform origTrans = ctxt.getTransform();
			
			ctxt.scale(cam.pixelsPerMeter);
			ctxt.translate(-cam.worldViewport.x, -cam.worldViewport.y);
			
			world.paintWorldScene(ctxt);
			
			ctxt.setTransform(origTrans);
			
			break;
		}
		}
		
	}

}
