package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.DLSFileChooser;
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
	
	/**
	 * move physics forward by dt seconds
	 */
	public double DT = 0.01;
	
	public WorldCamera cam = new WorldCamera();
	public int previewWidth;
	public int previewHeight;
	
	public WorldScreenContentPane contentPane;
	
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
		
		contentPane = new WorldScreenContentPane(this);
		
		contentPane.setLayout(null);
		
		container.setContentPane(contentPane);
		contentPane.setFocusable(true);
		contentPane.requestFocusInWindow();
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
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
		
		tool.setPoint(world.quadrantMap.getPoint(contentPane.canvas.lastMovedOrDraggedWorldPoint));
		
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
			
			contentPane.canvas.lastMovedOrDraggedWorldPoint = canvasToWorld(contentPane.canvas.lastMovedOrDraggedCanvasPoint);
			break;
		case DIALOG:
			break;
		case EDITING:
			world.zoom(1.1);
			
			contentPane.canvas.lastMovedOrDraggedWorldPoint = canvasToWorld(contentPane.canvas.lastMovedOrDraggedCanvasPoint);
			Entity closest = world.hitTest(contentPane.canvas.lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			tool.setPoint(world.quadrantMap.getPoint(contentPane.canvas.lastMovedOrDraggedWorldPoint));
			break;
		}
		
		world.render_canvas();
		world.render_preview();
		contentPane.repaint();
	}
	
	public void minusKey() {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			world.zoom(0.9);
			
			contentPane.canvas.lastMovedOrDraggedWorldPoint = canvasToWorld(contentPane.canvas.lastMovedOrDraggedCanvasPoint);
			break;
		case DIALOG:
			break;
		case EDITING:
			world.zoom(0.9);
			
			contentPane.canvas.lastMovedOrDraggedWorldPoint = canvasToWorld(contentPane.canvas.lastMovedOrDraggedCanvasPoint);
			Entity closest = world.hitTest(contentPane.canvas.lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				hilited = closest;
			}
			world.quadrantMap.computeGridSpacing();
			tool.setPoint(world.quadrantMap.getPoint(contentPane.canvas.lastMovedOrDraggedWorldPoint));
			break;
		}
		
		world.render_canvas();
		world.render_preview();
		contentPane.repaint();
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
		return new Point(p.x - contentPane.controlPanel.previewAABB.x, p.y - contentPane.controlPanel.previewAABB.y);
	}
	
}
