package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.DLSFileChooser;
import com.gutabi.deadlock.world.WorldScreen.WorldScreenMode;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;

@SuppressWarnings("serial")
public class WorldScreenContentPane extends ContentPane {
	
	public WorldScreen screen;
	
	public WorldPanel worldPanel;
	public ControlPanel controlPanel;
	
	static Logger logger = Logger.getLogger(WorldScreenContentPane.class);
	
	public WorldScreenContentPane(WorldScreen screen) {
		this.screen = screen;
		
		worldPanel = new WorldPanel(screen) {{
			setLocation(0, 0);
		}};
		
		controlPanel = new ControlPanel(screen) {{
			setLocation(0 + worldPanel.aabb.width, 0);
		}};
		
		children.add(worldPanel);
		children.add(controlPanel);
	}
	
	public void qKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.qKey();
			break;
		}
	}
	
	public void wKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.wKey();
			break;
		}
	}
	
	public void gKey() {
		
		screen.world.quadrantMap.toggleGrid();
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		
		screen.world.render_worldPanel();
		screen.world.render_preview();
		screen.contentPane.repaint();
	}
	
	public void deleteKey() {
		
		if (screen.hilited != null) {
			
			if (screen.hilited.isUserDeleteable()) {
				
				if (screen.hilited instanceof Car) {
					Car c = (Car)screen.hilited;
					
					screen.world.carMap.destroyCar(c);
					
				} else if (screen.hilited instanceof Vertex) {
					Vertex v = (Vertex)screen.hilited;
					
					Set<Vertex> affected = screen.world.graph.removeVertexTop(v);
					screen.world.graph.computeVertexRadii(affected);
					
				} else if (screen.hilited instanceof Road) {
					Road e = (Road)screen.hilited;
					
					Set<Vertex> affected = screen.world.graph.removeRoadTop(e);
					screen.world.graph.computeVertexRadii(affected);
					
				} else if (screen.hilited instanceof Merger) {
					Merger e = (Merger)screen.hilited;
					
					Set<Vertex> affected = screen.world.graph.removeMergerTop(e);
					screen.world.graph.computeVertexRadii(affected);
					
				} else if (screen.hilited instanceof StopSign) {
					StopSign s = (StopSign)screen.hilited;
					
					s.r.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				screen.hilited = null;
				
			}
			
		}
		
		screen.world.render_worldPanel();
		screen.world.render_preview();
		screen.contentPane.repaint();
	}
	
	public void insertKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.insertKey();
			break;
		}
	}
	
	public void escKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			screen.tool.escKey();
			break;
		case DIALOG:
			break;
		}
	}
	
	public void d1Key() {
		switch (screen.mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.d1Key();
			break;
		}
	}
	
	public void d2Key() {
		switch (screen.mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.d2Key();
			break;
		}
	}
	
	public void d3Key() {
		switch (screen.mode) {
		case PAUSED:
		case RUNNING:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.d3Key();
			break;
		}
	}
	
	public void plusKey() {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
			screen.world.zoom(1.1);
			
			if (screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint != null) {
				screen.world.lastMovedOrDraggedWorldPoint = screen.contentPane.worldPanel.panelToWorld(screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint);
			}
			break;
		case DIALOG:
			break;
		case EDITING:
			screen.world.zoom(1.1);
			
			if (screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint != null) {
				screen.world.lastMovedOrDraggedWorldPoint = screen.contentPane.worldPanel.panelToWorld(screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint);
			}
			Entity closest = screen.world.hitTest(screen.world.lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				screen.hilited = closest;
			}
			screen.world.quadrantMap.computeGridSpacing();
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
			break;
		}
		
		screen.world.render_worldPanel();
		screen.world.render_preview();
		screen.contentPane.repaint();
	}
	
	public void minusKey() {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
			screen.world.zoom(0.9);
			
			if (screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint != null) {
				screen.world.lastMovedOrDraggedWorldPoint = screen.contentPane.worldPanel.panelToWorld(screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint);
			}
			break;
		case DIALOG:
			break;
		case EDITING:
			screen.world.zoom(0.9);
			
			if (screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint != null) {
				screen.world.lastMovedOrDraggedWorldPoint = screen.contentPane.worldPanel.panelToWorld(screen.contentPane.worldPanel.lastMovedOrDraggedPanelPoint);
			}
			Entity closest = screen.world.hitTest(screen.world.lastMovedOrDraggedWorldPoint);
			synchronized (APP) {
				screen.hilited = closest;
			}
			screen.world.quadrantMap.computeGridSpacing();
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
			break;
		}
		
		screen.world.render_worldPanel();
		screen.world.render_preview();
		screen.contentPane.repaint();
	}
	
	public void aKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.aKey();
			break;
		}
	}
	
	public void sKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.sKey();
			break;
		}
	}
	
	public void ctrlSKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			
			screen.mode = WorldScreenMode.DIALOG;
			
			screen.contentPane.disableKeyListener();
			
			screen.fc = new DLSFileChooser();
			int res = screen.fc.showDialog(screen.contentPane, "Save");
			
			if (res == JFileChooser.APPROVE_OPTION) {
				
				try {
					
					File f = screen.fc.getSelectedFile();
					
					if (!f.exists()) {
						f.createNewFile();
					}
					
					FileWriter w = new FileWriter(f);
					
					w.write(screen.world.toFileString());
					
					w.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				screen.contentPane.enableKeyListener();
				
				screen.mode = WorldScreenMode.EDITING;
				
			} else {
				
				screen.contentPane.enableKeyListener();
				
				screen.mode = WorldScreenMode.EDITING;
				
			}
			
			break;
		}
	}
	
	public void ctrlOKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			
			screen.mode = WorldScreenMode.DIALOG;
			
			screen.contentPane.disableKeyListener();
			
			screen.fc = new DLSFileChooser();
			int res = screen.fc.showDialog(screen.contentPane, "Open");
			
			if (res == JFileChooser.APPROVE_OPTION) {
				
				String fileString = null;
				
				try {
					
					File f = screen.fc.getSelectedFile();
					
					Scanner s = new Scanner(f);
					fileString = s.useDelimiter("\\A").next();
					
					s.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				screen.world = World.fromFileString(screen, fileString);
				
				screen.contentPane.enableKeyListener();
				
				screen.mode = WorldScreenMode.EDITING;
				
				postDisplay();
				
				screen.world.render_worldPanel();
				screen.world.render_preview();
				screen.contentPane.repaint();
				
			} else {
				
				screen.contentPane.enableKeyListener();
				
				screen.mode = WorldScreenMode.EDITING;
				
			}
			
			break;
		}
	}
	
	public void dKey() {
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.dKey();
			break;
		}
	}
	
	public void enterKey() {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
			break;
		case DIALOG:
			break;
		case EDITING:
			screen.tool.dKey();
			break;
		}
		
	}
	
}
