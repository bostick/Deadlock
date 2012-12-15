package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Triangle;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.World;

public class QuadrantEditor extends ScreenBase {
	
	public static final int EDITOR_WIDTH = 800;
	public static final int EDITOR_HEIGHT = 600;
	
	int redWidth = 3 * EDITOR_WIDTH / 4;
	int redHeight = 3 * EDITOR_HEIGHT / 4;
	
	AABB worldCanvasAABB = new AABB(redWidth / 2 - 350/2, redHeight / 2 - 350/2, 350, 350);
	
	int quadrantRows;
	int quadrantCols;
	
	BufferedImage quadrantGrass;
	
	World world;
//	Button[][] quads;
	
	Button removeCol;
	Button addCol;
	Button removeRow;
	Button addRow;
	Button create;
	
	AABB hilited;
	
	public QuadrantEditor() {
		
		world = new WorldA();
		
		APP.PIXELS_PER_METER = (1.0 * worldCanvasAABB.width) / world.worldWidth;
		
		world.canvasPostDisplay(new Dim(worldCanvasAABB.width, worldCanvasAABB.height));
		
		final int bottomX = 0;
		final int bottomY = 3 * EDITOR_HEIGHT / 4;
		final int bottomWidth = 3 * EDITOR_WIDTH / 4;
		final int bottomHeight = EDITOR_HEIGHT / 4;
		
		removeRow = new Button(bottomX + bottomWidth/2 - 50/2, bottomY + bottomHeight/2 - 50/2 - 50/2, 50, 50) {
			Triangle tri = new Triangle(new Point(10, 35), new Point(40, 35), new Point(25, 15));
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLUE);
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(bottomX + bottomWidth/2 - 50/2, bottomY + bottomHeight/2 - 50/2 - 50/2);
				tri.paint(ctxt);
				ctxt.setTransform(origTransform);
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addRow = new Button(bottomX + bottomWidth/2 - 50/2, bottomY + bottomHeight/2 - 50/2 - 50/2 + 50, 50, 50) {
			Triangle tri = new Triangle(new Point(10, 15), new Point(40, 15), new Point(25, 35));
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLUE);
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(bottomX + bottomWidth/2 - 50/2, bottomY + bottomHeight/2 - 50/2 - 50/2 + 50);
				tri.paint(ctxt);
				ctxt.setTransform(origTransform);
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
		final int rightX = 3 * EDITOR_WIDTH / 4;
		final int rightY = 0;
		final int rightWidth = EDITOR_WIDTH / 4;
		final int rightHeight = 3 * EDITOR_HEIGHT / 4;
		
		removeCol = new Button(rightX + rightWidth/2 - 50/2 - 50/2, rightY + rightHeight/2 - 50/2, 50, 50) {
			Triangle tri = new Triangle(new Point(15, 25), new Point(35, 10), new Point(35, 40));
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLUE);
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(rightX + rightWidth/2 - 50/2 - 50/2, rightY + rightHeight/2 - 50/2);
				tri.paint(ctxt);
				ctxt.setTransform(origTransform);
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addCol = new Button(rightX + rightWidth/2 - 50/2 - 50/2 + 50, rightY + rightHeight/2 - 50/2, 50, 50) {
			Triangle tri = new Triangle(new Point(35, 25), new Point(15, 10), new Point(15, 40));
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLUE);
				AffineTransform origTransform = ctxt.getTransform();
				ctxt.translate(rightX + rightWidth/2 - 50/2 - 50/2 + 50, rightY + rightHeight/2 - 50/2);
				tri.paint(ctxt);
				ctxt.setTransform(origTransform);
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
		int cornerX = 3 * EDITOR_WIDTH / 4;
		int cornerY = 3 * EDITOR_HEIGHT / 4;
		int cornerWidth = EDITOR_WIDTH / 4;
		int cornerHeight = EDITOR_HEIGHT / 4;
		
		create = new Button(cornerX + cornerWidth/2 - 50/2, cornerY + cornerHeight/2 - 50/2, 50, 50) {
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		
	}
	
	public void init() throws Exception {
		world.init();
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
		if (create.hitTest(p)) {
			return create;
		}
		return null;
	}
	
	public void escKey(InputEvent ev) {
		
		try {
			
			APP.PIXELS_PER_METER = 1.0;
			APP.screen = new MainMenu();
			APP.screen.init();
			
			APP.screen.render();
			APP.screen.repaint();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			Point pWorldCanvas = lastMovedOrDraggedEditorPoint.minus(worldCanvasAABB.ul);
			
			Point pWorld = world.canvasToWorld(pWorldCanvas);
			
			Quadrant q = world.quadrantMap.findQuadrant(pWorld);
			
			hilited = world.worldToCanvas(q.aabb).plus(worldCanvasAABB.ul);
			
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
		
		AffineTransform origTrans = ctxt.getTransform();
		ctxt.translate(worldCanvasAABB.x, worldCanvasAABB.y);
		ctxt.scale(APP.PIXELS_PER_METER);
		ctxt.translate(-world.worldViewport.x, -world.worldViewport.y);
		world.paintWorld(ctxt);
		ctxt.setTransform(origTrans);
		
		removeCol.paint(ctxt);
		addCol.paint(ctxt);
		removeRow.paint(ctxt);
		addRow.paint(ctxt);
		create.paint(ctxt);
		
		if (hilited != null) {
			ctxt.setColor(Color.RED);
			hilited.draw(ctxt);
		}
		
//		if (lastMovedOrDraggedEditorPoint != null) {
//			new Circle(null, lastMovedOrDraggedEditorPoint, 5).paint(ctxt);
//		}
		
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c == VIEW.canvas) {
			VIEW.canvas.bs.show();
		} else {
			assert false;
		}
	}
	
}
