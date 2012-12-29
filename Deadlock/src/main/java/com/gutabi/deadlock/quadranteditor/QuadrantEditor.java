package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public class QuadrantEditor extends ScreenBase {
	
	public final int EDITOR_WIDTH = 800;
	public final int EDITOR_HEIGHT = 600;
	
	public QuadrantEditorCanvas canvas;
	
	AABB worldCanvasAABB = new AABB(50, 50, 350, 350);
	
	public WorldScreen worldScreen;
	
	int quadrantRows;
	int quadrantCols;
	
	BufferedImage quadrantGrass;
	
	World world;
	
	Button removeCol;
	Button addCol;
	Button removeRow;
	Button addRow;
	Button removeBoth;
	Button addBoth;
	Button go;
	
	AABB hilited;
	
	int[][] ini;
	
	public QuadrantEditor() {
		
		ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		worldScreen = new WorldScreen();
		
		world = World.createWorld(worldScreen, ini);
		
		double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
		double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
		worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
		
		world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
		worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
		
		removeRow = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				QuadrantEditor.this.worldScreen.canvas.render();
				QuadrantEditor.this.canvas.repaint();
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
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				QuadrantEditor.this.worldScreen.canvas.render();
				QuadrantEditor.this.canvas.repaint();
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
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				QuadrantEditor.this.worldScreen.canvas.render();
				QuadrantEditor.this.canvas.repaint();
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
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				QuadrantEditor.this.worldScreen.canvas.render();
				QuadrantEditor.this.canvas.repaint();
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
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				QuadrantEditor.this.worldScreen.canvas.render();
				QuadrantEditor.this.canvas.repaint();
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
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				QuadrantEditor.this.worldScreen.canvas.render();
				QuadrantEditor.this.canvas.repaint();
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
				
				teardown(APP.container);
				
				worldScreen.world = World.createWorld(worldScreen, ini);
				
				worldScreen.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				worldScreen.postDisplay();
				worldScreen.render();
				worldScreen.canvas.repaint();
				worldScreen.controlPanel.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		go.setBounds(700, 500, 50, 50);
		
		canvas = new QuadrantEditorCanvas(this);
		
	}
	
	public void setup(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		
		cp.setLayout(null);
		
		cp.add(canvas.java());
		
		Dimension size = canvas.java().getSize();
		canvas.java().setBounds(0, 0, size.width, size.height);
		
	}
	
	public void teardown(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		cp.remove(canvas.java());
		
		canvas = null;
	}
	
	public void postDisplay() {
		
		canvas.postDisplay();
		
	}
	
	public Point canvasToEditor(Point p) {
		return new Point(p.x - (canvas.getWidth()/2 - EDITOR_WIDTH/2), p.y - (canvas.getHeight()/2 - EDITOR_HEIGHT/2));
	}
	
	public Button hitTest(Point p) {
		if (removeCol.hitTest(p)) {
			return removeCol;
		}
		if (addCol.hitTest(p)) {
			return addCol;
		}
		if (removeRow.hitTest(p)) {
			return removeRow;
		}
		if (addRow.hitTest(p)) {
			return addRow;
		}
		if (removeBoth.hitTest(p)) {
			return removeBoth;
		}
		if (addBoth.hitTest(p)) {
			return addBoth;
		}
		if (go.hitTest(p)) {
			return go;
		}
		return null;
	}
	
	public void escKey(InputEvent ev) {
		
		teardown(APP.container);
		
		MainMenu s = new MainMenu();
		
		s.setup(APP.container);
		((JFrame)APP.container).setVisible(true);
		s.postDisplay();
		
		s.canvas.render();
		s.canvas.repaint();
	}
	
	
	Point lastMovedEditorPoint;
	Point lastMovedOrDraggedEditorPoint;
	
	public void moved(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedEditorPoint = canvasToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldCanvasAABB.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldCanvas = lastMovedOrDraggedEditorPoint.minus(new Point(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2));
			
			Point pWorld = world.canvasToWorld(pWorldCanvas);
			
			Quadrant q = world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				hilited = world.worldToCanvas(q.aabb).plus(new Point(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2));
			} else {
				hilited = null;
			}
			
		} else {
			
			Button closest = hitTest(lastMovedOrDraggedEditorPoint);
			
			if (closest != null) {
				hilited = closest.aabb;
			} else {
				hilited = null;
			}
			
		}
		
		canvas.repaint();
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
			
			Point pWorld = world.canvasToWorld(pWorldCanvas);
			
			Quadrant q = world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				
				ini[q.r][q.c] = (q.active?0:1);
				
				world = World.createWorld(worldScreen, ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.quadrantMap.worldWidth;
				double pixelsPerMeterHeight = worldCanvasAABB.height / world.quadrantMap.worldHeight;
				worldScreen.cam.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.quadrantMap.worldWidth * worldScreen.cam.pixelsPerMeter, world.quadrantMap.worldHeight * worldScreen.cam.pixelsPerMeter));
				worldScreen.cam.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				
				worldScreen.canvas.render();
				canvas.repaint();
				
			} else {
				
			}
			
		} else {
			
			Button b = hitTest(lastMovedOrDraggedEditorPoint);
			
			if (b != null) {
				b.action();
			}
			
		}
		
	}
	
//	public void render() {
//		worldScreen.canvas.render();
//	}
	
	public void paintEditor(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT);
		
		ctxt.setColor(Color.LIGHT_GRAY);
		worldCanvasAABB.paint(ctxt);
		
		AffineTransform origTrans = ctxt.getTransform();
		ctxt.translate(worldCanvasAABB.center.x - worldScreen.cam.canvasWidth/2, worldCanvasAABB.center.y - worldScreen.cam.canvasHeight/2);
		
		worldScreen.canvas.paint(ctxt);
		
		ctxt.scale(worldScreen.cam.pixelsPerMeter);
		ctxt.translate(-worldScreen.cam.worldViewport.x, -worldScreen.cam.worldViewport.y);
		world.paintWorldScene(ctxt);
		ctxt.setTransform(origTrans);
		
		removeRow.paint(ctxt);
		addRow.paint(ctxt);
		removeCol.paint(ctxt);
		addCol.paint(ctxt);
		removeBoth.paint(ctxt);
		addBoth.paint(ctxt);
		go.paint(ctxt);
		
		if (hilited != null) {
			ctxt.setColor(Color.RED);
			hilited.draw(ctxt);
		}
		
	}
	
}
