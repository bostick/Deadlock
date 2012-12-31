package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

//@SuppressWarnings("serial")
public class QuadrantEditorPanel extends PanelBase {
	
	public final int EDITOR_WIDTH = 800;
	public final int EDITOR_HEIGHT = 600;
	
	QuadrantEditor screen;
	public WorldScreen worldScreen;
	
	int[][] ini;
	
	Button removeCol;
	Button addCol;
	Button removeRow;
	Button addRow;
	Button removeBoth;
	Button addBoth;
	Button go;
	
	static Logger logger = Logger.getLogger(QuadrantEditorPanel.class);
	
	public QuadrantEditorPanel(final QuadrantEditor screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, 1584, 822);
		
		ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		worldScreen = new WorldScreen();
		worldScreen.world = World.createWorld(worldScreen, ini);
		worldScreen.contentPane.worldPanel.aabb = new AABB(50, 50, 350, 350);
		
		double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
		double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
		worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
		
		worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
		worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
		worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
		worldScreen.world.panelPostDisplay();
		
		
		removeRow = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.x, aabb.y);
				ctxt.rotate(6 * Math.PI / 4, aabb.width/2, aabb.height/2);
				
				ctxt.paintImage(APP.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
			
		};
		removeRow.setBounds(worldScreen.contentPane.worldPanel.aabb.center.x - 50/2, worldScreen.contentPane.worldPanel.aabb.brY + 40 - 50/2, 50, 50);
		
		
		addRow = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length+1][old[0].length];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				screen.contentPane.repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(2 * Math.PI / 4, aabb.width/2, aabb.height/2);
				
				ctxt.paintImage(APP.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addRow.setBounds(worldScreen.contentPane.worldPanel.aabb.center.x - 50/2, worldScreen.contentPane.worldPanel.aabb.brY + 40 - 50/2 + 50, 50, 50);
		
		
		removeCol = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length][old[0].length-1];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(4 * Math.PI / 4, aabb.width/2, aabb.height/2);
				
				ctxt.paintImage(APP.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		removeCol.setBounds(worldScreen.contentPane.worldPanel.aabb.brX + 40 - 50/2, worldScreen.contentPane.worldPanel.aabb.center.y - 50/2, 50, 50);
		
		addCol = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length][old[0].length+1];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(0 * Math.PI / 4, aabb.width/2, aabb.height/2);
				
				ctxt.paintImage(APP.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
				
			}
		};
		addCol.setBounds(worldScreen.contentPane.worldPanel.aabb.brX + 40 - 50/2 + 50, worldScreen.contentPane.worldPanel.aabb.center.y - 50/2, 50, 50);
		
		removeBoth = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length-1];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(5 * Math.PI / 4, aabb.width/2, aabb.height/2);
				
				ctxt.paintImage(APP.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		removeBoth.setBounds(worldScreen.contentPane.worldPanel.aabb.brX + 40 - 50/2, worldScreen.contentPane.worldPanel.aabb.brY + 40 - 50/2, 50, 50);
		
		addBoth = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length+1][old[0].length+1];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(1 * Math.PI / 4, aabb.width/2, aabb.height/2);
				
				ctxt.paintImage(APP.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addBoth.setBounds(worldScreen.contentPane.worldPanel.aabb.brX + 40 - 50/2 + 50, worldScreen.contentPane.worldPanel.aabb.brY + 40 - 50/2 + 50, 50, 50);
		
		go = new Button() {
			
			public void action() {
				
				WorldScreen s = new WorldScreen();
				s.world = World.createWorld(s, ini);
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				
				s.world.render_worldPanel();
				s.world.render_preview();
				s.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		go.setBounds(700, 500, 50, 50);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
	}
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	Point lastClickedPanelPoint;
	Point lastMovedEditorPoint;
	Point lastMovedOrDraggedEditorPoint;
	
	public Point panelToEditor(Point p) {
		return new Point(p.x - (aabb.width/2 - EDITOR_WIDTH/2), p.y - (aabb.height/2 - EDITOR_HEIGHT/2));
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
		
		Point p = ev.p;
		
		lastMovedEditorPoint = panelToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldScreen.contentPane.worldPanel.aabb.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldPanel = lastMovedOrDraggedEditorPoint.minus(new Point(worldScreen.contentPane.worldPanel.aabb.center.x - worldScreen.cam.panelWidth/2, worldScreen.contentPane.worldPanel.aabb.center.y - worldScreen.cam.panelHeight/2));
			
			Point pWorld = worldScreen.contentPane.worldPanel.panelToWorld(pWorldPanel);
			
			Quadrant q = worldScreen.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				screen.hilited = worldScreen.contentPane.worldPanel.worldToPanel(q.aabb).plus(new Point(worldScreen.contentPane.worldPanel.aabb.center.x - worldScreen.cam.panelWidth/2, worldScreen.contentPane.worldPanel.aabb.center.y - worldScreen.cam.panelHeight/2));
			} else {
				screen.hilited = null;
			}
			
		} else {
			
			if (removeCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = removeCol.aabb;
			} else if (addCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = addCol.aabb;
			} else if (removeRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = removeRow.aabb;
			} else if (addRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = addRow.aabb;
			} else if (removeBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = removeBoth.aabb;
			} else if (addBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = addBoth.aabb;
			} else if (go.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = go.aabb;
			} else {
				screen.hilited = null;
			}
			
		}
		
		screen.contentPane.repaint();
	}
	
	public void clicked(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedEditorPoint = panelToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldScreen.contentPane.worldPanel.aabb.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldPanel = lastMovedOrDraggedEditorPoint.minus(new Point(worldScreen.contentPane.worldPanel.aabb.center.x - worldScreen.cam.panelWidth/2, worldScreen.contentPane.worldPanel.aabb.center.y - worldScreen.cam.panelHeight/2));
			
			Point pWorld = worldScreen.contentPane.worldPanel.panelToWorld(pWorldPanel);
			
			Quadrant q = worldScreen.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				
				ini[q.r][q.c] = (q.active?0:1);
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldScreen.contentPane.worldPanel.aabb.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelsPerMeterHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
				
//				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				worldScreen.cam.panelWidth = (int)(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter);
				worldScreen.cam.panelHeight = (int)(worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter);
				worldScreen.world.panelPostDisplay();
				
				worldScreen.world.render_worldPanel();
				
				screen.contentPane.repaint();
				
			} else {
				
			}
			
		} else {
			
			if (removeCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				removeCol.action();
			} else if (addCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				addCol.action();
			} else if (removeRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				removeRow.action();
			} else if (addRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				addRow.action();
			} else if (removeBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				removeBoth.action();
			} else if (addBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				addBoth.action();
			} else if (go.hitTest(lastMovedOrDraggedEditorPoint)) {
				go.action();
			}
			
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
	
		ctxt.cam = worldScreen.cam;
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.translate(aabb.width/2 - EDITOR_WIDTH/2, aabb.height/2 - EDITOR_HEIGHT/2);
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT);
		
		ctxt.setColor(Color.LIGHT_GRAY);
		worldScreen.contentPane.worldPanel.aabb.paint(ctxt);
		
		AffineTransform origTrans2 = ctxt.getTransform();
		ctxt.translate(worldScreen.contentPane.worldPanel.aabb.center.x - worldScreen.cam.panelWidth/2, worldScreen.contentPane.worldPanel.aabb.center.y - worldScreen.cam.panelHeight/2);
		
		worldScreen.world.paint_panel(ctxt);
		
		ctxt.setTransform(origTrans2);
		
		removeRow.paint(ctxt);
		addRow.paint(ctxt);
		removeCol.paint(ctxt);
		addCol.paint(ctxt);
		removeBoth.paint(ctxt);
		addBoth.paint(ctxt);
		go.paint(ctxt);
		
		if (screen.hilited != null) {
			ctxt.setColor(Color.RED);
			screen.hilited.draw(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}

}
