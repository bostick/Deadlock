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
public class QuadrantEditorCanvas extends PanelBase {
	
	public final int EDITOR_WIDTH = 800;
	public final int EDITOR_HEIGHT = 600;
	
	QuadrantEditor screen;
	public WorldScreen worldScreen;
	
	AABB worldCanvasAABB = new AABB(50, 50, 350, 350);
	
	
	int[][] ini;
	
	Button removeCol;
	Button addCol;
	Button removeRow;
	Button addRow;
	Button removeBoth;
	Button addBoth;
	Button go;
	
	static Logger logger = Logger.getLogger(QuadrantEditorCanvas.class);
	
	public QuadrantEditorCanvas(final QuadrantEditor screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, 1584, 822);
		
		ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		worldScreen = new WorldScreen();
		worldScreen.world = World.createWorld(worldScreen, ini);
		
		double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
		double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
		worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
		
		worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
		worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
		
		
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
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
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
		removeRow.setBounds(worldCanvasAABB.center.x - 50/2, worldCanvasAABB.brY + 40 - 50/2, 50, 50);
		
		
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
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
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
		addRow.setBounds(worldCanvasAABB.center.x - 50/2, worldCanvasAABB.brY + 40 - 50/2 + 50, 50, 50);
		
		
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
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
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
		removeCol.setBounds(worldCanvasAABB.brX + 40 - 50/2, worldCanvasAABB.center.y - 50/2, 50, 50);
		
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
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
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
		addCol.setBounds(worldCanvasAABB.brX + 40 - 50/2 + 50, worldCanvasAABB.center.y - 50/2, 50, 50);
		
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
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
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
		removeBoth.setBounds(worldCanvasAABB.brX + 40 - 50/2, worldCanvasAABB.brY + 40 - 50/2, 50, 50);
		
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
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
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
		addBoth.setBounds(worldCanvasAABB.brX + 40 - 50/2 + 50, worldCanvasAABB.brY + 40 - 50/2 + 50, 50, 50);
		
		go = new Button() {
			
			public void action() {
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				worldScreen.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				worldScreen.postDisplay();
				
				worldScreen.world.render_canvas();
				worldScreen.world.render_preview();
				worldScreen.contentPane.repaint();
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
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	Point lastClickedCanvasPoint;
	Point lastMovedEditorPoint;
	Point lastMovedOrDraggedEditorPoint;
	
	public Point canvasToEditor(Point p) {
		return new Point(p.x - (aabb.width/2 - EDITOR_WIDTH/2), p.y - (aabb.height/2 - EDITOR_HEIGHT/2));
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		Point p = ev.p;
		
		lastMovedEditorPoint = canvasToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldCanvasAABB.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldCanvas = lastMovedOrDraggedEditorPoint.minus(new Point(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2));
			
			Point pWorld = worldScreen.canvasToWorld(pWorldCanvas);
			
			Quadrant q = worldScreen.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				screen.hilited = worldScreen.worldToCanvas(q.aabb).plus(new Point(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2));
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
		
		lastMovedEditorPoint = canvasToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldCanvasAABB.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldCanvas = lastMovedOrDraggedEditorPoint.minus(new Point(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2));
			
			Point pWorld = worldScreen.canvasToWorld(pWorldCanvas);
			
			Quadrant q = worldScreen.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				
				ini[q.r][q.c] = (q.active?0:1);
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / worldScreen.world.quadrantMap.worldWidth;
				double pixelsPerMeterHeight = worldCanvasAABB.height / worldScreen.world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
				
				worldScreen.world.canvasPostDisplay(new Dim(worldScreen.world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, worldScreen.world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, worldScreen.world.quadrantMap.worldWidth, worldScreen.world.quadrantMap.worldHeight);
				
				worldScreen.world.render_canvas();
				
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
		worldCanvasAABB.paint(ctxt);
		
		AffineTransform origTrans2 = ctxt.getTransform();
		ctxt.translate(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2);
		
		worldScreen.world.paint_canvas(ctxt);
		
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
