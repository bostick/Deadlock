package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

//@SuppressWarnings("static-access")
public class QuadrantEditor extends ScreenBase {
	
	public static final int EDITOR_WIDTH = 800;
	public static final int EDITOR_HEIGHT = 600;
	
	AABB worldCanvasAABB = new AABB(50, 50, 350, 350);
	
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
		
		world = new World(ini);
		
		double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
		double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
		world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
		
		world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
		world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
		
		world.init();
		
		removeRow = new Button(worldCanvasAABB.center.x - 50/2, worldCanvasAABB.brY + 40 - 50/2, 50, 50) {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.translate(aabb.width/2, aabb.height/2);
				ctxt.rotate(3 * Math.PI / 2);
				ctxt.translate(-aabb.width/2, -aabb.height/2);
				ctxt.paintImage(0, 0, VIEW.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addRow = new Button(worldCanvasAABB.center.x - 50/2, worldCanvasAABB.brY + 40 - 50/2 + 50, 50, 50) {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length+1][old[0].length];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.translate(aabb.width/2, aabb.height/2);
				ctxt.rotate(1 * Math.PI / 2);
				ctxt.translate(-aabb.width/2, -aabb.height/2);
				ctxt.paintImage(0, 0, VIEW.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
		removeCol = new Button(worldCanvasAABB.brX + 40 - 50/2, worldCanvasAABB.center.y - 50/2, 50, 50) {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length][old[0].length-1];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.translate(aabb.width/2, aabb.height/2);
				ctxt.rotate(Math.PI);
				ctxt.translate(-aabb.width/2, -aabb.height/2);
				ctxt.paintImage(0, 0, VIEW.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addCol = new Button(worldCanvasAABB.brX + 40 - 50/2 + 50, worldCanvasAABB.center.y - 50/2, 50, 50) {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length][old[0].length+1];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.translate(aabb.width/2, aabb.height/2);
				ctxt.rotate(0 * Math.PI);
				ctxt.translate(-aabb.width/2, -aabb.height/2);
				ctxt.paintImage(0, 0, VIEW.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
				
			}
		};
		
		removeBoth = new Button(worldCanvasAABB.brX + 40 - 50/2, worldCanvasAABB.brY + 40 - 50/2, 50, 50) {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length-1];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.translate(aabb.width/2, aabb.height/2);
				ctxt.rotate(5 * Math.PI / 4);
				ctxt.translate(-aabb.width/2, -aabb.height/2);
				ctxt.paintImage(0, 0, VIEW.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
		addBoth = new Button(worldCanvasAABB.brX + 40 - 50/2 + 50, worldCanvasAABB.brY + 40 - 50/2 + 50, 50, 50) {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length+1][old[0].length+1];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelxPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.translate(aabb.width/2, aabb.height/2);
				ctxt.rotate(Math.PI / 4);
				ctxt.translate(-aabb.width/2, -aabb.height/2);
				ctxt.paintImage(0, 0, VIEW.sheet, 0, 0, 50, 50, 128, 224, 160, 256);
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
		go = new Button(700, 500, 50, 50) {
			
			public void action() {
				
				VIEW.teardownCanvas(VIEW.container);
				
				WorldScreen s = new WorldScreen();
				s.world = new World(ini);
				s.init();
				
				APP.screen = s;
				
				VIEW.setupCanvasAndControlPanel(VIEW.container);
				((JFrame)VIEW.container).setVisible(true);
				
				VIEW.canvas.canvasPostDisplay();
				
				APP.screen.render();
				APP.screen.repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
	}
	
	public void init() {
		
	}
	
	public void canvasPostDisplay(Dim d) {
		
	}
	
	public Point canvasToEditor(Point p) {
		return new Point(p.x - (VIEW.canvas.getWidth()/2 - EDITOR_WIDTH/2), p.y - (VIEW.canvas.getHeight()/2 - EDITOR_HEIGHT/2));
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
		
		APP.screen = new MainMenu();
		APP.screen.init();
		
		APP.screen.render();
		APP.screen.repaint();
		
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
			Point pWorldCanvas = lastMovedOrDraggedEditorPoint.minus(new Point(worldCanvasAABB.center.x - world.canvasWidth/2, worldCanvasAABB.center.y - world.canvasHeight/2));
			
			Point pWorld = world.canvasToWorld(pWorldCanvas);
			
			Quadrant q = world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				hilited = world.worldToCanvas(q.aabb).plus(new Point(worldCanvasAABB.center.x - world.canvasWidth/2, worldCanvasAABB.center.y - world.canvasHeight/2));
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
		
		repaint();
	}
	
	public void clicked(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedEditorPoint = canvasToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldCanvasAABB.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldCanvas = lastMovedOrDraggedEditorPoint.minus(new Point(worldCanvasAABB.center.x - world.canvasWidth/2, worldCanvasAABB.center.y - world.canvasHeight/2));
			
			Point pWorld = world.canvasToWorld(pWorldCanvas);
			
			Quadrant q = world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				
				ini[q.r][q.c] = (q.active?0:1);
				
				world = new World(ini);
				
				double pixelsPerMeterWidth = worldCanvasAABB.width / world.worldWidth;
				double pixelsPerMeterHeight = worldCanvasAABB.height / world.worldHeight;
				world.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
				
				world.canvasPostDisplay(new Dim(world.worldWidth * world.pixelsPerMeter, world.worldHeight * world.pixelsPerMeter));
				world.worldViewport = new AABB(0, 0, world.worldWidth, world.worldHeight);
				
				world.init();
				
				render();
				repaint();
				
			} else {
				
			}
			
		} else {
			
			Button b = hitTest(lastMovedOrDraggedEditorPoint);
			
			if (b != null) {
				b.action();
			}
			
		}
		
		repaint();
	}
	
	public void render() {
		world.renderCanvas();
	}
	
	public void repaint() {
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.translate(VIEW.canvas.getWidth()/2 - EDITOR_WIDTH/2, VIEW.canvas.getHeight()/2 - EDITOR_HEIGHT/2);
				
				paintEditor(ctxt);
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());

	}
	
	private void paintEditor(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT);
		
		ctxt.setColor(Color.LIGHT_GRAY);
		worldCanvasAABB.paint(ctxt);
		
		AffineTransform origTrans = ctxt.getTransform();
		ctxt.translate(worldCanvasAABB.center.x - world.canvasWidth/2, worldCanvasAABB.center.y - world.canvasHeight/2);
		ctxt.scale(world.pixelsPerMeter);
		ctxt.translate(-world.worldViewport.x, -world.worldViewport.y);
		world.paintWorld(ctxt);
		ctxt.setTransform(origTrans);
		
		removeCol.paint(ctxt);
		addCol.paint(ctxt);
		removeRow.paint(ctxt);
		addRow.paint(ctxt);
		removeBoth.paint(ctxt);
		addBoth.paint(ctxt);
		go.paint(ctxt);
		
		if (hilited != null) {
			ctxt.setColor(Color.RED);
			hilited.draw(ctxt);
		}
		
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c == VIEW.canvas) {
			VIEW.canvas.bs.show();
		} else {
			assert false;
		}
	}
	
}
