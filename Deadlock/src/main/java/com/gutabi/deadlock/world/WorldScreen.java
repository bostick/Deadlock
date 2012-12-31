package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.DLSFileChooser;
import com.gutabi.deadlock.ui.InputEvent;
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
	public int previewWidth;
	public int previewHeight;
	
	public WorldScreenContentPane contentPane;
	public WorldCanvas canvas;
//	public WorldCanvas oldCanvas;
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
		
	}
	
	public void setup(RootPaneContainer container) {
		
//		Container cp = container.getContentPane();
		contentPane = new WorldScreenContentPane(this);
		
		canvas = new WorldCanvas(this);
		
		controlPanel = new ControlPanel(this);
		
		contentPane.setLayout(null);
		
//		contentPane.add(canvas.java());
		
//		Dimension size = canvas.java().getSize();
		canvas.setLocation(0, 0);
		
//		contentPane.add(controlPanel.java());
		
//		size = controlPanel.java().getSize();
//		controlPanel.java().setBounds(0 + canvas.java().getWidth(), 0, size.width, size.height);
		controlPanel.setLocation(0 + canvas.aabb.width, 0);
		
		container.setContentPane(contentPane);
		contentPane.setFocusable(true);
	}
	
	public void teardown(RootPaneContainer container) {
		
//		Container cp = container.getContentPane();
//		
//		cp.remove(canvas.java());
//		cp.remove(controlPanel.java());
//		
////		oldCanvas = canvas;
//		canvas = null;
//		controlPanel = null;
		
	}
	
	public void postDisplay() {
		
		Dim canvasDim = canvas.postDisplay();
		
		world.canvasPostDisplay(canvasDim);
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
	
	public void qKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.qKey();
			break;
		}
	}
	
	public void wKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.wKey();
			break;
		}
	}
	
	public void gKey() {
		
		world.quadrantMap.toggleGrid();
		
		tool.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
		
		world.render_canvas();
		world.render_preview();
		contentPane.repaint();
	}
	
	public void deleteKey() {
		
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
		
		world.render_canvas();
		world.render_preview();
		contentPane.repaint();
//		controlPanel.repaint();
	}
	
	public void insertKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.insertKey();
			break;
		}
	}
	
	public void escKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			tool.escKey();
			break;
		case DIALOG:
			break;
		}
	}
	
	public void d1Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			tool.d1Key();
			break;
		}
	}
	
	public void d2Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			tool.d2Key();
			break;
		}
	}
	
	public void d3Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			tool.d3Key();
			break;
		}
	}
	
	public void plusKey() {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			world.zoom(1.1);
			
			lastMovedOrDraggedWorldPoint = canvasToWorld(lastMovedOrDraggedCanvasPoint);
			break;
		case DIALOG:
			break;
		case EDITING:
			world.zoom(1.1);
			
			lastMovedOrDraggedWorldPoint = canvasToWorld(lastMovedOrDraggedCanvasPoint);
			Entity closest = world.hitTest(lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			tool.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
			break;
		}
		
		world.render_canvas();
		world.render_preview();
		contentPane.repaint();
//		controlPanel.repaint();
	}
	
	public void minusKey() {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			world.zoom(0.9);
			
			lastMovedOrDraggedWorldPoint = canvasToWorld(lastMovedOrDraggedCanvasPoint);
			break;
		case DIALOG:
			break;
		case EDITING:
			world.zoom(0.9);
			
			lastMovedOrDraggedWorldPoint = canvasToWorld(lastMovedOrDraggedCanvasPoint);
			Entity closest = world.hitTest(lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			tool.setPoint(world.quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
			break;
		}
		
		world.render_canvas();
		world.render_preview();
		contentPane.repaint();
//		this.canvas.repaint();
//		controlPanel.repaint();
	}
	
	public void aKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.aKey();
			break;
		}
	}
	
	public void sKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.sKey();
			break;
		}
	}
	
	public void ctrlSKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			
			mode = WorldScreenMode.DIALOG;
			
			contentPane.disableKeyListener();
			
			fc = new DLSFileChooser();
			int res = fc.showDialog(contentPane, "Save");
			
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
				
				contentPane.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
			} else {
				
				contentPane.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
			}
			
			break;
		}
	}
	
	public void ctrlOKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			
			mode = WorldScreenMode.DIALOG;
			
			contentPane.disableKeyListener();
			
			fc = new DLSFileChooser();
			int res = fc.showDialog(contentPane, "Open");
			
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
				
				contentPane.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
				postDisplay();
				
				world.render_canvas();
				world.render_preview();
				contentPane.repaint();
//				controlPanel.repaint();
				
			} else {
				
				contentPane.enableKeyListener();
				
				mode = WorldScreenMode.EDITING;
				
			}
			
			break;
		}
	}
	
	public void dKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.dKey();
			break;
		}
	}
	
	public void enterKey() {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case DIALOG:
			break;
		case EDITING:
			tool.dKey();
			break;
		}
		
	}
	
	
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / cam.pixelsPerMeter + cam.worldViewport.x,
				p.y / cam.pixelsPerMeter + cam.worldViewport.y);
	}
	
	public AABB canvasToWorld(AABB aabb) {
		Point ul = canvasToWorld(aabb.ul);
		Point br = canvasToWorld(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point worldToCanvas(Point p) {
		return new Point(
				(p.x - cam.worldViewport.x) * cam.pixelsPerMeter,
				(p.y - cam.worldViewport.y) * cam.pixelsPerMeter);
	}
	
	public AABB worldToCanvas(AABB aabb) {
		Point ul = worldToCanvas(aabb.ul);
		Point br = worldToCanvas(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point previewToWorld(Point p) {
		
		double pixelsPerMeterWidth = previewWidth / world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewHeight / world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((1/s) * p.x, (1/s) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		double pixelsPerMeterWidth = previewWidth / world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = previewHeight / world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((s) * p.x, (s) * p.y);
	}
	
	public Point controlPanelToPreview(Point p) {
		return new Point(p.x - world.previewAABB.x, p.y - world.previewAABB.y);
	}
	
	public Point contentPaneToCanvas(double x, double y) {
		return new Point(x - 0, y - 0);
	}
	
	public Point contentPaneToControlPanel(double x, double y) {
		return new Point(x - controlPanel.aabb.x, y - controlPanel.aabb.y);
	}
	
	public boolean canvasHitTest(double x, double y) {
//		AABB canvasAABB = new AABB(0, 0, canvas.aabb.width, canvas.);
		if (canvas.aabb.hitTest(new Point(x, y))) {
			return true;
		}
		return false;
	}
	
	public boolean controlPanelHitTest(double x, double y) {
//		AABB controlPanelAABB = new AABB(canvas.java().getWidth(), 0, controlPanel.java().getWidth(), controlPanel.java().getHeight());
		if (controlPanel.aabb.hitTest(new Point(x, y))) {
			return true;
		}
		return false;
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	Point lastClickedCanvasPoint;
	
	
	public Point lastPressedControlPanelPoint;
	
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	long lastPressTime;
	long lastDragTime;
	public Point lastDraggedControlPanelPoint;
	
	public Point lastPressedWorldPoint;
	
	public void pressed_canvas(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressedWorldPoint = canvasToWorld(p);
		lastDraggedWorldPoint = null;
		
	}
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	public void dragged_canvas(InputEvent ev) {
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		switch (mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			tool.dragged(ev);
			break;
		}
		}
		
	}
	
	public void released_canvas(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.released(ev);
			break;
		}
		
	}
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void moved_canvas(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		switch (mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastMovedWorldPoint = canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastMovedWorldPoint = canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			tool.moved(ev);
			break;
		}
		}
	}
	
	public void exited_canvas(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			tool.exited(ev);
			break;
		}
		
	}
	
	
	public void clicked_controlpanel(InputEvent ev) {
		
		if (controlPanel.normalCarButton.hitTest(ev.p)) {
			controlPanel.normalCarButton.action();
		} else if (controlPanel.fastCarButton.hitTest(ev.p)) {
			controlPanel.fastCarButton.action();
		} else if (controlPanel.reallyCarButton.hitTest(ev.p)) {
			controlPanel.reallyCarButton.action();
		} else if (controlPanel.fpsCheckBox.hitTest(ev.p)) {
			controlPanel.fpsCheckBox.action();
		} else if (controlPanel.stopSignCheckBox.hitTest(ev.p)) {
			controlPanel.stopSignCheckBox.action();
		} else if (controlPanel.carTextureCheckBox.hitTest(ev.p)) {
			controlPanel.carTextureCheckBox.action();
		} else if (controlPanel.explosionsCheckBox.hitTest(ev.p)) {
			controlPanel.explosionsCheckBox.action();
		} else if (controlPanel.debugCheckBox.hitTest(ev.p)) {
			controlPanel.debugCheckBox.action();
		} else if (controlPanel.startButton.hitTest(ev.p)) {
			controlPanel.startButton.action();
		} else if (controlPanel.stopButton.hitTest(ev.p)) {
			controlPanel.stopButton.action();
		}
		
	}
	
	public void pressed_controlpanel(InputEvent ev) {
		
		Point p = ev.p;
		
		if (world.previewHitTest(p)) {
			
			lastPressedControlPanelPoint = p;
			lastDraggedControlPanelPoint = null;
			
			lastPressPreviewPoint = controlPanelToPreview(p);
			lastPressTime = System.currentTimeMillis();
			
			lastDragPreviewPoint = null;
			lastDragTime = -1;
			
		} else {
			
		}
		
	}
	
	public void dragged_controlpanel(InputEvent ev) {
		
		Point p = ev.p;
		
		if (world.previewHitTest(p) && lastPressedControlPanelPoint != null && world.previewHitTest(lastPressedControlPanelPoint)) {
			
			penDragPreviewPoint = lastDragPreviewPoint;
			lastDragPreviewPoint = controlPanelToPreview(p);
			lastDragTime = System.currentTimeMillis();
			
			if (penDragPreviewPoint != null) {
				
				double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
				double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
				
				world.previewPan(new Point(dx, dy));
				
				world.render_canvas();
				world.render_preview();
				contentPane.repaint();
//				controlPanel.repaint();
			}
			
		}
		
	}
	
}
